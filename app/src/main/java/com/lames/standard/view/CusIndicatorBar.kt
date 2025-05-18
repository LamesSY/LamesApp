package com.lames.standard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.lames.standard.R
import com.lames.standard.entity.StepBar
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.tools.dp
import com.lames.standard.tools.dp2px
import com.lames.standard.tools.forAttrColor
import com.lames.standard.tools.forColor

class CusIndicatorBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private val textBounds = Rect()
    private var indicatorText: String = "正常"
    private val drawTextSize = 16f.dp
    private val barHeight = dp2px(8).toFloat()
    private val indicatorHeight = dp2px(6).toFloat()
    private val intervalHeight = dp2px(2).toFloat()

    private val barRadius = barHeight / 2f

    private var indicatorIndex: Int = -1

    private val stepBarList = mutableListOf<StepBar>()

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = forAttrColor(context, R.attr.windowBg_1)
    }

    private val barPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = forAttrColor(context, R.attr.themeColor)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = drawTextSize
        textAlign = Paint.Align.LEFT
        color = forAttrColor(getContext(), R.attr.text_1)
    }

    private val indicatorPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = forAttrColor(context, R.attr.text_1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBar()
    }

    private fun Canvas.drawBar() {
        saveLayer(0f, 0f, viewWidth, viewHeight, null)
        drawRoundRect(
            0f,
            viewHeight - barHeight,
            viewWidth,
            viewHeight,
            barRadius,
            barRadius,
            bgPaint
        )

        var lastLeft = 0f
        val allRatio = stepBarList.sumOf { it.ratio }
        val rectFList = mutableListOf<RectF>()
        stepBarList.forEachIndexed { i, bar ->
            val length = viewWidth * bar.ratio / allRatio
            barPaint.color = forColor(bar.colorRes)
            //setAlpha需要在color之后执行，否则效果会被覆盖
            //barPaint.alpha = if (indicatorIndex == i) 255 else 50
            val rectF = RectF(lastLeft, viewHeight - barHeight, lastLeft + length, viewHeight)
            rectFList.add(rectF)
            if (bar.isRoundRect) drawRoundRect(rectF, barRadius, barRadius, barPaint)
            else drawRect(rectF, barPaint)
            lastLeft += length
            if (indicatorIndex == i) {
                val originX = (rectF.right + rectF.left) / 2
                val path = Path().apply {
                    moveTo(originX, viewHeight - barHeight - intervalHeight)
                    lineTo(
                        originX - indicatorHeight,
                        viewHeight - barHeight - indicatorHeight - intervalHeight
                    )
                    lineTo(
                        originX + indicatorHeight,
                        viewHeight - barHeight - indicatorHeight - intervalHeight
                    )
                    close()
                }
                drawPath(path, indicatorPaint)

                textPaint.getTextBounds(indicatorText, 0, indicatorText.length, textBounds)
                drawText(
                    indicatorText,
                    originX - (textBounds.width() / 2),
                    textBounds.height().toFloat(),
                    textPaint
                )
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        viewHeight = barHeight + indicatorHeight + intervalHeight + drawTextSize + intervalHeight
        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    /**
     * @param type 0随机 1空腹 2餐前 3餐后
     */
    fun setBsBar(bs: Float, type: Int) {
        if (type != 3) {
            this.indicatorIndex = if (bs in 4.4..10.0) 1 else if (bs > 10) 2 else 0
            this.indicatorText = if (bs in 4.4..10.0) "正常" else if (bs > 10) "偏高" else "偏低"
        } else {
            this.indicatorIndex = if (bs <= 7.8) 1 else 2
            this.indicatorText = if (bs <= 7.8) "正常" else "偏高"
        }

        this.stepBarList.clear()
        this.stepBarList.addAll(basicTheme3)
        invalidate()
    }

    /**
     * 血酮
     */
    fun setBkBar(value: Float) {
        this.indicatorIndex = if (value < 0.3) 0 else 1
        this.indicatorText = if (value < 0.3) "正常" else "偏高"
        this.stepBarList.clear()
        this.stepBarList.addAll(basicTheme2)
        invalidate()
    }

    /**
     * 尿酸
     */
    fun setUaBar(value: Float) {
        if (UserMMKV.user?.sex == 0) {
            this.indicatorIndex = if (value < 360) 0 else 1
            this.indicatorText = if (value < 360) "正常" else "偏高"
        } else {
            this.indicatorIndex = if (value < 420) 0 else 1
            this.indicatorText = if (value < 420) "正常" else "偏高"
        }
        this.stepBarList.clear()
        this.stepBarList.addAll(basicTheme2)
        invalidate()
    }

    fun setBpBar(sbp: Int = -1, dbp: Int = -1) {
        val titles = listOf("血压低", "正常", "高血压1级", "高血压2级", "高血压3级")
        val list = listOf(90, 140, 160, 180, Int.MAX_VALUE)
        for (i in 0..4) {
            if (sbp > list[i]) continue
            this.indicatorIndex = i
            this.indicatorText = titles[i]
            break
        }
        this.stepBarList.clear()
        this.stepBarList.addAll(basicTheme5)
        invalidate()
    }

    fun setBarTheme(stepBarList: List<StepBar>, indicatorIndex: Int = -1) {
        this.stepBarList.clear()
        this.stepBarList.addAll(stepBarList)
        this.indicatorIndex = if (indicatorIndex >= 0) indicatorIndex else -1
        invalidate()
    }

    companion object {
        val spo2hTheme = listOf(
            StepBar(2, R.color.label_normal, true),
            StepBar(1, R.color.label_warning, true),
            StepBar(1, R.color.label_warning2, true),
        )

        val basicTheme2 = listOf(
            StepBar(1, R.color.label_normal),
            StepBar(1, R.color.label_high),
        )

        val basicTheme3 = listOf(
            StepBar(1, R.color.label_normal),
            StepBar(1, R.color.label_warning2),
            StepBar(1, R.color.label_high),
        )

        val basicTheme4 = listOf(
            StepBar(1, R.color.label_low),
            StepBar(1, R.color.label_normal),
            StepBar(1, R.color.label_warning2),
            StepBar(1, R.color.label_high),
        )

        val basicTheme5 = listOf(
            StepBar(1, R.color.label_low),
            StepBar(1, R.color.label_normal),
            StepBar(1, R.color.label_warning),
            StepBar(1, R.color.label_warning2),
            StepBar(1, R.color.label_high),
        )
    }
}