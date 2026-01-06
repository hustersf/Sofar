package com.sofar.profiler.startup.model

data class StartupInfo(
  var applicationCost: Long,
  var firstScreenCost: Long,
  var allCost: Long
) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append("applicationCost=$applicationCost\n")
    sb.append("firstScreenCost=$firstScreenCost\n")
    sb.append("totalCost=$allCost")
    return sb.toString()
  }
}