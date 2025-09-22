package com.sofar.widget.recycler.overscroll

import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

class HorizontalEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {

  private var enable = true

  companion object {
    private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f
    private const val FLING_TRANSLATION_MAGNITUDE = 0.5f
  }

  override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
    return object : EdgeEffect(recyclerView.context) {

      val springAnimation: SpringAnimation =
        SpringAnimation(recyclerView, SpringAnimation.TRANSLATION_X)
          .setSpring(
            SpringForce()
              .setFinalPosition(0f)
              .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
              .setStiffness(SpringForce.STIFFNESS_LOW)
          )

      override fun onPull(deltaDistance: Float) {
        super.onPull(deltaDistance)
        handlePull(deltaDistance)
      }

      override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance, displacement)
        handlePull(deltaDistance)
      }

      private fun handlePull(deltaDistance: Float) {
        if (!enable) {
          return
        }
        val sign = if (direction == DIRECTION_RIGHT) -1 else 1
        val translationXDelta =
          sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
        springAnimation.cancel()
        recyclerView.translationX += translationXDelta
      }

      override fun onRelease() {
        super.onRelease()
        if (!enable) {
          return
        }
        springAnimation.start()
      }

      override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
        if (!enable) {
          return
        }
        val sign = if (direction == DIRECTION_RIGHT) -1 else 1
        val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
        springAnimation.setStartVelocity(translationVelocity).start()
      }
    }
  }

  fun enable(flag: Boolean) {
    this.enable = flag
  }
}