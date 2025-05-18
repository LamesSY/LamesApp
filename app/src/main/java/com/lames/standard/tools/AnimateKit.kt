@file:JvmName("AnimateKit")

package com.lames.standard.tools

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation


/**
 * Created by Jason
 */
/**
 * 旋转
 */
inline fun View.rotate(
    degree: Float,
    duration: Long = 200L,
    crossinline onEnd: (animator: Animator) -> Unit = {}
) {
    animate().setDuration(duration).rotation(degree)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) = onEnd(animation)
        })
}

/**
 * 淡入
 */
inline fun View.fadeIn(
    duration: Long = 200L,
    crossinline onEnd: (animator: Animator) -> Unit = {}
) {
    animate().setDuration(duration).alpha(1f).setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) = onEnd(animation)
    })
}

/**
 * 淡出
 */
inline fun View.fadeOut(
    duration: Long = 500L,
    crossinline onEnd: (animator: Animator) -> Unit = {}
) {
    animate().setDuration(duration).alpha(0f).setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) = onEnd(animation)
    })
}

/**
 * 缩放
 */
inline fun View.scale(
    fraction: Float,
    duration: Long = 300L,
    crossinline onEnd: (animator: Animator) -> Unit = {}
) {
    animate().setDuration(duration).scaleX(fraction).scaleY(fraction)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) = onEnd(animation)
        })
}

/**
 * Float 数值变换
 */
inline fun floatAnimate(
    startDelay: Long,
    duration: Long,
    vararg values: Float,
    crossinline block: (Float) -> Unit
) {
    ValueAnimator.ofFloat(*values).apply {
        setStartDelay(startDelay)
        setDuration(duration)
        interpolator = DecelerateInterpolator()
        addUpdateListener { block(it.animatedValue as Float) }
    }.start()
}

/**
 * 在 LinearLayout 中实现指定 Visibility 为 GONE 的 View 的展开动画，在其他布局使用可能出现闪烁
 */
inline fun View.expand(crossinline onEnd: (animator: Animation) -> Unit = {}) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight
    layoutParams.height = 0
    visibility = View.VISIBLE
    val anim = object : Animation() {
        override fun willChangeBounds(): Boolean = true
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            layoutParams.height =
                if (interpolatedTime == 1.0f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }
    }
    anim.duration = (targetHeight / context.resources.displayMetrics.density).toLong()
    startAnimation(anim)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) = onEnd(animation)
    })
}

/**
 * 为 View 实现收缩动画
 */
inline fun View.collapse(crossinline onEnd: (animator: Animation) -> Unit = {}) {
    val initialHeight = measuredHeight
    val anim = object : Animation() {
        override fun willChangeBounds(): Boolean = true
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                visibility = View.GONE
                return
            }
            layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
            requestLayout()
        }
    }
    anim.duration = (initialHeight / context.resources.displayMetrics.density).toLong()
    startAnimation(anim)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) = onEnd(animation)
    })
}

/**
 * 强调动画
 */
fun View.tada(shakeFactor: Float) {
    val scaleX = PropertyValuesHolder.ofKeyframe(
        View.SCALE_X,
        Keyframe.ofFloat(0f, 1f),
        Keyframe.ofFloat(.1f, .9f),
        Keyframe.ofFloat(.2f, .9f),
        Keyframe.ofFloat(.3f, 1.1f),
        Keyframe.ofFloat(.4f, 1.1f),
        Keyframe.ofFloat(.5f, 1.1f),
        Keyframe.ofFloat(.6f, 1.1f),
        Keyframe.ofFloat(.7f, 1.1f),
        Keyframe.ofFloat(.8f, 1.1f),
        Keyframe.ofFloat(.9f, 1.1f),
        Keyframe.ofFloat(1f, 1f)
    )
    val scaleY = PropertyValuesHolder.ofKeyframe(
        View.SCALE_Y,
        Keyframe.ofFloat(0f, 1f),
        Keyframe.ofFloat(.1f, .9f),
        Keyframe.ofFloat(.2f, .9f),
        Keyframe.ofFloat(.3f, 1.1f),
        Keyframe.ofFloat(.4f, 1.1f),
        Keyframe.ofFloat(.5f, 1.1f),
        Keyframe.ofFloat(.6f, 1.1f),
        Keyframe.ofFloat(.7f, 1.1f),
        Keyframe.ofFloat(.8f, 1.1f),
        Keyframe.ofFloat(.9f, 1.1f),
        Keyframe.ofFloat(1f, 1f)
    )
    val rotate = PropertyValuesHolder.ofKeyframe(
        View.ROTATION,
        Keyframe.ofFloat(0f, 0f),
        Keyframe.ofFloat(.1f, -3f * shakeFactor),
        Keyframe.ofFloat(.2f, -3f * shakeFactor),
        Keyframe.ofFloat(.3f, 3f * shakeFactor),
        Keyframe.ofFloat(.4f, -3f * shakeFactor),
        Keyframe.ofFloat(.5f, 3f * shakeFactor),
        Keyframe.ofFloat(.6f, -3f * shakeFactor),
        Keyframe.ofFloat(.7f, 3f * shakeFactor),
        Keyframe.ofFloat(.8f, -3f * shakeFactor),
        Keyframe.ofFloat(.9f, 3f * shakeFactor),
        Keyframe.ofFloat(1f, 0f)
    )
    ObjectAnimator.ofPropertyValuesHolder(this, scaleX, scaleY, rotate).setDuration(1000).start()
}

/**
 * 震荡动画
 */
fun View.shake(shakeOffset: Float) {
    val translateX = PropertyValuesHolder.ofKeyframe(
        View.TRANSLATION_X,
        Keyframe.ofFloat(0f, 0f),
        Keyframe.ofFloat(.10f, -shakeOffset),
        Keyframe.ofFloat(.26f, shakeOffset),
        Keyframe.ofFloat(.42f, -shakeOffset),
        Keyframe.ofFloat(.58f, shakeOffset),
        Keyframe.ofFloat(.74f, -shakeOffset),
        Keyframe.ofFloat(.90f, shakeOffset),
        Keyframe.ofFloat(1f, 0f)
    )
    ObjectAnimator.ofPropertyValuesHolder(this, translateX).setDuration(1000).start()
}

object ItemAnimation {
    fun fadeIn(view: View, position: Int) {
        view.alpha = 0.0f
        val animatorSet = AnimatorSet()
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.5f, 1.0f)
        val delay = if (position == -1) 200L else (position + 1) * 100L
        objectAnimator.startDelay = delay
        objectAnimator.duration = 200L
        animatorSet.play(objectAnimator as Animator)
        animatorSet.start()
    }

    fun rightToLeftFadeIn(view: View, position: Int) {
        view.alpha = 0.0f
        view.translationX = view.x + 30
        val delay = if (position == -1) 150L else (position + 1) * 150L

        val animatorSet = AnimatorSet()
        val transXAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(view, "translationX", view.x + 30, 0f)
        transXAnimator.startDelay = delay
        transXAnimator.duration = 200L
        val alphaAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.5f, 1.0f)
        alphaAnimator.startDelay = delay
        alphaAnimator.duration = 200L

        animatorSet.playTogether(transXAnimator as Animator, alphaAnimator as Animator)
        animatorSet.start()
    }
}
