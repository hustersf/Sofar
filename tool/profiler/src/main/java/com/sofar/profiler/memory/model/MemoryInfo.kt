package com.sofar.profiler.memory.model

import com.sofar.profiler.formatNumber

data class MemoryInfo(
  var pssTotalK: Int, var pssJavaK: Int, var pssNativeK: Int,
  var pssGraphicK: Int, var pssStackK: Int, var pssCodeK: Int
) {

  override fun toString(): String {
    var memInfo = StringBuffer()
    memInfo.append("pss(MB)\n")
    memInfo.append("Total=")
    memInfo.append(formatNumber(1.0f * pssTotalK / 1024))
    memInfo.append("\n")
    memInfo.append("Java=")
    memInfo.append(formatNumber(1.0f * pssJavaK / 1024))
    memInfo.append("\n")
    memInfo.append("Native=")
    memInfo.append(formatNumber(1.0f * pssNativeK / 1024))
    memInfo.append("\n")
    if (pssGraphicK != -1) {
      memInfo.append("Graphics=")
      memInfo.append(formatNumber(1.0f * pssGraphicK / 1024))
      memInfo.append("\n")
    }
    if (pssStackK != -1) {
      memInfo.append("Stack=")
      memInfo.append(formatNumber(1.0f * pssStackK / 1024))
      memInfo.append("\n")
    }
    if (pssCodeK != -1) {
      memInfo.append("Code=")
      memInfo.append(formatNumber(1.0f * pssCodeK / 1024))
    }
    return memInfo.toString()
  }
}