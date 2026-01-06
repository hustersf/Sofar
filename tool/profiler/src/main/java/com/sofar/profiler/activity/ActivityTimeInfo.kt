package com.sofar.profiler.activity

class ActivityTimeInfo {
  var title: String = ""
  var back = false
  var totalCost: Long = 0
  var pauseCost: Long = 0
  var launchCost: Long = 0
  var renderCost: Long = 0
  var otherCost: Long = 0

  override fun toString(): String {
    val sb = StringBuilder()
    sb.append("page:$title,")
    sb.append("back=$back\n")
    sb.append("totalCost=$totalCost\n")
    sb.append("pauseCost=$pauseCost\n")
    sb.append("launchCost=$launchCost\n")
    sb.append("renderCost=$renderCost\n")
    sb.append("otherCost=$otherCost")
    return sb.toString()
  }
}