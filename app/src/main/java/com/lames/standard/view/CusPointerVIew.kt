package com.lames.standard.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import com.lames.standard.tools.dp2px

class CusPointerVIew @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    val topCenter = PointF(0f, 0f)  // 顶部中心点
    val leftBottom = PointF(0f, 0f)
    val rightBottom = PointF(0f, 0f)

    // 计算顶部圆角的起始点和结束点
    val topLeft = PointF(0f, 0f)
    val topRight = PointF(0f, 0f)
    val path = Path()

    private var pointerAngle = 0f          // 0表示垂直向上

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFAEAEAE")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = dp2px(15).toFloat()
        val r = dp2px(25).toFloat()
        val cx = width / 2f
        val cy = height - r

        canvas.save()
        canvas.rotate(pointerAngle, cx, cy)

        canvas.drawCircle(cx, cy, r, paint)
        val topCornerRadius = dp2px(2)

        /*val topCenter = PointF(cx, 0f)  // 顶部中心点
        val leftBottom = PointF(cx - w, cy)
        val rightBottom = PointF(cx + w, cy)

        // 计算顶部圆角的起始点和结束点
        val topLeft = PointF(topCenter.x - topCornerRadius, topCenter.y + topCornerRadius)
        val topRight = PointF(topCenter.x + topCornerRadius, topCenter.y + topCornerRadius)
        val path = Path()*/

        topCenter.x = cx
        topCenter.y = 0f

        leftBottom.x = cx - w
        leftBottom.y = cy

        rightBottom.x = cx + w
        rightBottom.y = cy

        topLeft.x = topCenter.x - topCornerRadius
        topLeft.y = topCenter.y + topCornerRadius

        topRight.x = topCenter.x + topCornerRadius
        topRight.y = topCenter.y + topCornerRadius



        path.reset()
        // 从左侧底部开始
        path.moveTo(leftBottom.x, leftBottom.y)

        // 画直线到顶部左侧圆角起点
        path.lineTo(topLeft.x, topLeft.y)

        // 绘制顶部圆角（使用二次贝塞尔曲线）
        path.quadTo(
            topCenter.x, topCenter.y,  // 控制点（顶部顶点）
            topRight.x, topRight.y     // 结束点（顶部右侧）
        )

        // 画直线到右侧底部
        path.lineTo(rightBottom.x, rightBottom.y)

        // 闭合路径
        path.close()

        canvas.drawPath(path, paint)

    }

    //-90~90
    fun setTemperature(temperature: Float) {
        val max = 42
        val min = 35
        val a = (max - temperature) / (max - min) * 180 + (-90)
        animateToAngle(a)
    }

    fun animateToAngle(targetAngle: Float, duration: Long = 500) {
        ValueAnimator.ofFloat(pointerAngle, targetAngle).apply {
            this.duration = duration
            interpolator = OvershootInterpolator()
            addUpdateListener {
                pointerAngle = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }


}