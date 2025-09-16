package com.connect4.service

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame

class FrameLogCallback(private val onLog: (String) -> Unit): ResultCallback.Adapter<Frame>() {
  override fun onNext(frame: Frame) {
    onLog(String(frame.payload).trim())
  }
}
