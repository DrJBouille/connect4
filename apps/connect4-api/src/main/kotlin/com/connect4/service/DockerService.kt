package com.connect4.service

import com.connect4.model.entity.Jobs
import com.connect4.model.Stats
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import jakarta.enterprise.context.ApplicationScoped
import java.net.URI
import java.time.Duration

@ApplicationScoped
class DockerService {
  private val mapper = jacksonObjectMapper()

  fun getStats(jobs: Jobs): Stats {
    val parameters = jobs.parameters
    val dockerClient = createDockerClient()

    val container = dockerClient.createContainerCmd(parameters.image.imageName).withCmd(parameters.redDeepness.toString(), parameters.yellowDeepness.toString()).exec()

    dockerClient.startContainerCmd(container.id).exec()

    dockerClient.waitContainerCmd(container.id).start().awaitStatusCode()

    val logs = StringBuilder()
    dockerClient.logContainerCmd(container.id)
      .withStdOut(true)
      .withStdErr(true)
      .exec(FrameLogCallback {
        logs.append(it)
      }).awaitCompletion()

    dockerClient.removeContainerCmd(container.id).withForce(true).exec()
    val stats: Stats = mapper.readValue(logs.toString(), Stats::class.java)

    return stats
  }

  fun createDockerClient(): DockerClient {
    val config = DefaultDockerClientConfig.createDefaultConfigBuilder().build()

    val dockerHost: URI = config.dockerHost ?: URI.create("unix:///var/run/docker.sock")

    val builder = ApacheDockerHttpClient.Builder()
      .dockerHost(dockerHost)
      .maxConnections(100)
      .connectionTimeout(Duration.ofSeconds(30))
      .responseTimeout(Duration.ofSeconds(45))

    config.sslConfig?.let { builder.sslConfig(it) }

    val httpClient: DockerHttpClient = builder.build()

    return DockerClientImpl.getInstance(config, httpClient)
  }
}
