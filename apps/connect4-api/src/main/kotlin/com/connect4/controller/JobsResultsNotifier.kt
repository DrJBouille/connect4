package com.connect4.controller

import com.connect4.model.DTO.NotificationDTO
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
class JobsResultsNotifier {
  private val sessions: MutableSet<Session> = mutableSetOf()

  @OnOpen
  fun onOpen(session: Session) {
    sessions.add(session)
    println("New connection established : ${session.id}")
  }

  @OnClose
  fun onClose(session: Session) {
    sessions.remove(session)
    println("Connection closed : ${session.id}")
  }

  @OnError
  fun onError(session: Session?, throwable: Throwable) {
    println("Error on ${session?.id} : ${throwable.message}")
  }

  @OnMessage
  fun onMessage(session: Session, message: String) {
    println("Message received : $message")
    session.asyncRemote.sendText(message)
  }

  fun broadcast(batchId: String, jobsId: String) {
    val jsonNotification = jacksonObjectMapper().writeValueAsString(NotificationDTO(batchId, jobsId))

    sessions.filter { it.isOpen }.forEach { it.asyncRemote.sendText(jsonNotification) }
  }
}
