package com.connect4.controller

import com.connect4.model.IdDTO
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
class TasksNotifier {
  private val sessionsToJobId: MutableMap<Session, String> = mutableMapOf()

  @OnOpen
  fun onOpen(session: Session) {
    println("New connection established : ${session.id}")
  }

  @OnClose
  fun onClose(session: Session) {
    sessionsToJobId.remove(session)
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
    sessionsToJobId[session] = id
  }

  fun broadcast(jobId: String, taskId: String) {
    val json = jacksonObjectMapper().writeValueAsString(IdDTO(taskId))
    sessionsToJobId.filter { (session, subscribedJobId) -> session.isOpen && subscribedJobId == jobId }.forEach { (session, _) -> session.asyncRemote.sendText(json) }
  }
}
