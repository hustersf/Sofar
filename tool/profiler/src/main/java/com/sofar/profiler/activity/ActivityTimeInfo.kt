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
    var sb = StringBuilder()
    sb.append("ActivityTimeInfo{")
    sb.append("title=$title,")
    sb.append("back=$back,")
    sb.append("totalCost=$totalCost,")
    sb.append("pauseCost=$pauseCost,")
    sb.append("launchCost=$launchCost,")
    sb.append("renderCost=$renderCost,")
    sb.append("otherCost=$otherCost,")
    sb.append("}")
    return sb.toString()
  }
}