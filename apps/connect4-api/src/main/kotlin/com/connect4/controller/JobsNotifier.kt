package com.connect4.controller

import com.connect4.model.dto.JobsIdDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.websocket.OnClose
import jakarta.websocket.OnError
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.ServerEndpoint

@ApplicationScoped
@ServerEndpoint("/ws/jobs")
class JobsNotifier {
  private val sessionsToBatchId: MutableMap<Session, String> = mutableMapOf()

  @OnOpen
  fun onOpen(session: Session) {
    println("New connection established : ${session.id}")
  }

  @OnClose
  fun onClose(session: Session) {
    sessionsToBatchId.remove(session)
    println("Connection closed : ${session.id}")
  }

  @OnError
  fun onError(session: Session?, throwable: Throwable) {
    println("Error on ${session?.id} : ${throwable.message}")
  }

  @OnMessage
  fun onMessage(session: Session, message: String) {
    val json = jacksonObjectMapper().readTree(message)
    println("Message received : $message")
    val id = json.get("id").asText()
    sessionsToBatchId[session] = id
  }

  fun broadcast(batchId: String, jobsId: String) {
    val json = jacksonObjectMapper().writeValueAsString(JobsIdDTO(jobsId))
    sessionsToBatchId.forEach { session, jobId ->}
    sessionsToBatchId.filter { (session, subscribedBatchId) -> session.isOpen && subscribedBatchId == batchId }.forEach { (session, _) -> session.asyncRemote.sendText(json) }
  }
}
