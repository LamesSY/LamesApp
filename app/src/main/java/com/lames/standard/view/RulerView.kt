package com.lames.standard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import com.lames.standard.R
import com.lames.standard.tools.LogKit
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * GradationView
 * 刻度卷尺控件
 *
 *
 * 思路：
 * 1. 把float类型数据，乘10，转为int类型。之所以把它放在第一位，因为踩过这个坑：不转的话，由于精度问题，会导致计算异常复杂
 * 2. 绘制刻度：
 * - 根据中间指针位置的数值，来计算最小值位置与中间指针位置的距离
 * - 为了绘制性能，只绘制控件宽度范围内的刻度。但不出现数值突变（两侧刻度出现突然显示或不显示），两侧各增加2个单位
 * 3. 滑动时，通过移动最小位置与中间指针位置的距离，逆向推算当前刻度值
 * 4. 滑动停止后，自动调整到最近的刻度：使用滑动器Scroller，需要计算出最终要抵达的位置
 * 5. 惯性滑动：使用速度跟踪器VelocityTracker
 *
 *
 * Author: Ralap
 * Description:
 * Date 2018/7/29
 */
class RulerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * 滑动阈值
     */
    private val touchSlop: Int

    /**
     * 惯性滑动最小、最大速度
     */
    private val minFlingVelocity: Int
    private val maxFlingVelocity: Int

    /**
     * 背景色
     */
    private var bgColor = 0

    /**
     * 刻度颜色
     */
    private var gradationColor = 0

    /**
     * 短刻度线宽度
     */
    private var shortLineWidth = 0f

    /**
     * 长刻度线宽度
     * 默认 = 2 * shortLineWidth
     */
    private var longLineWidth = 0f

    /**
     * 短刻度长度
     */
    private var shortGradationLen = 0f

    /**
     * 长刻度长度
     * 默认为短刻度的2倍
     */
    private var longGradationLen = 0f

    /**
     * 刻度字体颜色
     */
    private var textColor = 0

    /**
     * 刻度字体大小
     */
    private var textSize = 0f

    /**
     * 中间指针线颜色
     */
    private var indicatorLineColor = 0

    /**
     * 中间指针线宽度
     */
    private var indicatorLineWidth = 0f

    /**
     * 中间指针线长度
     */
    private var indicatorLineLen = 0f

    /**
     * 最小值
     */
    var minValue = 0f
        private set

    /**
     * 最大值
     */
    var maxValue = 0f
        private set

    /**
     * 当前值
     */
    var currentValue = 0f

    /**
     * 刻度最小单位
     */
    private var gradationUnit = 0f

    /**
     * 需要绘制的数值
     */
    private var numberPerCount = 0

    /**
     * 刻度间距离
     */
    private var gradationGap = 0f

    /**
     * 刻度与文字的间距
     */
    private var gradationNumberGap = 0f

    /**
     * 最小数值，放大10倍：minValue * 10
     */
    private var minNumber = 0

    /**
     * 最大数值，放大10倍：maxValue * 10
     */
    private var maxNumber = 0

    /**
     * 当前数值
     */
    private var currentNumber = 0

    /**
     * 最大数值与最小数值间的距离：(maxNumber - minNumber) / numberUnit * gradationGap
     */
    private var numberRangeDistance = 0f

    /**
     * 刻度数值最小单位：gradationUnit * 10
     */
    private var numberUnit = 0

    /**
     * 当前数值与最小值的距离：(mCurrentNumber - minValue) / mNumberUnit * gradationGap
     */
    private var currentDistance = 0f

    /**
     * 控件宽度所占有的数值范围：mWidth / gradationGap * mNumberUnit
     */
    private var widthRangeNumber = 0

    /**
     * 普通画笔
     */
    private lateinit var paint: Paint

    /**
     * 文字画笔
     */
    private lateinit var textPaint: TextPaint

    /**
     * 滑动器
     */
    private lateinit var scroller: Scroller

    /**
     * 速度跟踪器
     */
    private var velocityTracker: VelocityTracker? = null

    /**
     * 尺寸
     */
    private var viewWidth = 0
    private var halfWidth = 0
    private var viewHeight = 0
    private var enableScroll = true
    private var downX = 0
    private var lastX = 0
    private var lastY = 0
    private var isMoved = false
    private var valueChangedListener: ((Float) -> Unit)? = null
    private var onDragListener: ((Boolean) -> Unit)? = null

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView)
        bgColor = ta.getColor(R.styleable.RulerView_bgColor, Color.parseColor("#00000000"))
        gradationColor = ta.getColor(R.styleable.RulerView_gradationColor, Color.LTGRAY)
        shortLineWidth = ta.getDimension(R.styleable.RulerView_shortLineWidth, dp2px(1f).toFloat())
        shortGradationLen =
            ta.getDimension(R.styleable.RulerView_shortGradationLen, dp2px(16f).toFloat())
        longGradationLen =
            ta.getDimension(R.styleable.RulerView_longGradationLen, shortGradationLen * 2)
        longLineWidth = ta.getDimension(R.styleable.RulerView_longLineWidth, shortLineWidth * 2)
        textColor = ta.getColor(R.styleable.RulerView_textColor, Color.BLACK)
        textSize = ta.getDimension(R.styleable.RulerView_textSize, sp2px(14f).toFloat())
        indicatorLineColor =
            ta.getColor(R.styleable.RulerView_indicatorLineColor, Color.parseColor("#FFE29543"))
        indicatorLineWidth =
            ta.getDimension(R.styleable.RulerView_indicatorLineWidth, dp2px(3f).toFloat())
        indicatorLineLen =
            ta.getDimension(R.styleable.RulerView_indicatorLineLen, dp2px(35f).toFloat())
        minValue = ta.getFloat(R.styleable.RulerView_minValue, 0f)
        maxValue = ta.getFloat(R.styleable.RulerView_maxValue, 100f)
        currentValue = ta.getFloat(R.styleable.RulerView_currentValue, 50f)
        gradationUnit = ta.getFloat(R.styleable.RulerView_gradationUnit, .1f)
        numberPerCount = ta.getInt(R.styleable.RulerView_numberPerCount, 10)
        gradationGap = ta.getDimension(R.styleable.RulerView_gradationGap, dp2px(10f).toFloat())
        gradationNumberGap =
            ta.getDimension(R.styleable.RulerView_gradationNumberGap, dp2px(8f).toFloat())

        if ((currentValue in minValue..maxValue).not()) {
            currentValue = if (currentValue > maxValue) maxValue else minValue
        }

        ta.recycle()
    }

    /**
     * 初始化
     */
    private fun init(context: Context) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = shortLineWidth
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = textSize
        textPaint.color = textColor
        scroller = Scroller(context)
    }

    /**
     * 把真实数值转换成绘制数值
     * 为了防止float的精度丢失，把minValue、maxValue、currentValue、gradationUnit都放大10倍
     */
    private fun convertValue2Number() {
        minNumber = (minValue * 1000).toInt()
        maxNumber = (maxValue * 1000).toInt()
        currentNumber = (currentValue * 1000).toInt()
        numberUnit = (gradationUnit * 1000).toInt()
        currentDistance = (currentNumber - minNumber) / numberUnit * gradationGap
        numberRangeDistance = (maxNumber - minNumber) / numberUnit * gradationGap
        if (viewWidth != 0) widthRangeNumber = (viewWidth / gradationGap * numberUnit).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewWidth = calculateSize(true, widthMeasureSpec)
        viewHeight = calculateSize(false, heightMeasureSpec)
        halfWidth = viewWidth shr 1
        if (widthRangeNumber == 0) widthRangeNumber =
            (viewWidth / gradationGap * numberUnit).toInt()
        setMeasuredDimension(viewWidth, viewHeight)
    }

    /**
     * 计算宽度或高度的真实大小
     *
     *
     * 宽或高为wrap_content时，父控件的测量模式无论是EXACTLY还是AT_MOST，默认给的测量模式都是AT_MOST，测量大小为父控件的size
     * 所以，我们宽度不管，只处理高度，默认80dp
     *
     * @param isWidth 是不是宽度
     * @param spec    测量规则
     * @return 真实的大小
     */
    private fun calculateSize(isWidth: Boolean, spec: Int): Int {
        val mode = MeasureSpec.getMode(spec)
        val size = MeasureSpec.getSize(spec)
        var realSize = size
        if (MeasureSpec.AT_MOST == mode) {
            if (isWidth.not()) {
                val defaultContentSize = dp2px(80f)
                realSize = realSize.coerceAtMost(defaultContentSize)
            }
        }
        return realSize
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        enableScroll = enabled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (enableScroll.not()) return false

        val action = event.action
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
        velocityTracker?.addMovement(event)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                scroller?.forceFinished(true)
                downX = x
                isMoved = false
                onDragListener?.invoke(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastX
                if (isMoved.not()) {
                    val dy = y - lastY
                    if (abs(dx) < abs(dy) || abs(x - downX) < touchSlop) return true
                    isMoved = true
                }
                currentDistance += -dx.toFloat()
                calculateValue()
            }

            MotionEvent.ACTION_UP -> {
                velocityTracker?.computeCurrentVelocity(1000, maxFlingVelocity.toFloat())
                val xVelocity = velocityTracker?.xVelocity?.toInt() ?: 0
                if (abs(xVelocity) >= minFlingVelocity) {
                    scroller?.fling(
                        currentDistance.toInt(),
                        0,
                        -xVelocity,
                        0,
                        0,
                        numberRangeDistance.toInt(),
                        0,
                        0
                    )
                    invalidate()
                } else {
                    scrollToGradation()
                }
                onDragListener?.invoke(false)
            }
        }
        lastX = x
        lastY = y
        return true
    }

    /**
     * 根据distance距离，计算数值
     */
    private fun calculateValue() {
        // 限定范围：在最小值与最大值之间
        currentDistance = currentDistance.coerceAtLeast(0f).coerceAtMost(numberRangeDistance)
        currentNumber = minNumber + (currentDistance / gradationGap).toInt() * numberUnit
        currentValue = currentNumber / 1000f

        valueChangedListener?.invoke(currentValue)
        invalidate()
    }

    /**
     * 滑动到最近的刻度线上
     */
    private fun scrollToGradation() {
        currentNumber = minNumber + (currentDistance / gradationGap).roundToInt() * numberUnit
        currentNumber = currentNumber.coerceAtLeast(minNumber).coerceAtMost(maxNumber)
        currentDistance = (currentNumber - minNumber) / numberUnit * gradationGap
        currentValue = currentNumber / 1000f

        valueChangedListener?.invoke(currentValue)
        invalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            if (scroller.currX != scroller.finalX) {
                currentDistance = scroller.currX.toFloat()
                calculateValue()
            } else {
                scrollToGradation()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        // 1 绘制背景色
        canvas.drawColor(bgColor)
        // 2 绘制刻度、数字
        drawGradation(canvas)
        // 3 绘制指针
        drawIndicator(canvas)
    }

    /**
     * 绘制刻度
     */
    private fun drawGradation(canvas: Canvas) {
        paint.color = gradationColor
        paint.strokeWidth = shortLineWidth
        canvas.drawLine(0f, shortLineWidth * .5f, viewWidth.toFloat(), 0f, paint)

        var startNum =
            (currentDistance.toInt() - halfWidth) / gradationGap.toInt() * numberUnit + minNumber
        // 扩展2个单位
        val expendUnit = numberUnit shl 1
        // 左侧扩展
        startNum -= expendUnit
        if (startNum < minNumber) {
            startNum = minNumber
        }
        // 右侧扩展
        var rightMaxNum = startNum + expendUnit + widthRangeNumber + expendUnit
        if (rightMaxNum > maxNumber) {
            rightMaxNum = maxNumber
        }
        // 当前绘制刻度对应控件左侧的位置
        var distance =
            halfWidth - (currentDistance - (startNum - minNumber) / numberUnit * gradationGap)
        val perUnitCount = numberUnit * numberPerCount

        while (startNum <= rightMaxNum) {
            if (startNum % perUnitCount == 0) {
                paint.strokeWidth = longLineWidth
                canvas.drawLine(distance, 0f, distance, longGradationLen, paint)

                val fNum = startNum / 1000f
                var text = fNum.toString()
                if (text.endsWith(".0")) text = text.substring(0, text.length - 2)
                val textWidth = textPaint.measureText(text)
                canvas.drawText(
                    text,
                    distance - textWidth * .5f,
                    longGradationLen + gradationNumberGap + textSize,
                    textPaint
                )
            } else {
                paint.strokeWidth = shortLineWidth
                canvas.drawLine(distance, 0f, distance, shortGradationLen, paint)
            }
            startNum += numberUnit
            distance += gradationGap
        }
    }

    /**
     * 绘制指针
     */
    private fun drawIndicator(canvas: Canvas) {
        paint.color = indicatorLineColor
        paint.strokeWidth = indicatorLineWidth
        // 圆头画笔
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(halfWidth.toFloat(), 0f, halfWidth.toFloat(), indicatorLineLen, paint)
        // 默认形状画笔
        paint.strokeCap = Paint.Cap.BUTT
    }

    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
            .toInt()
    }

    private fun sp2px(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
            .toInt()
    }

    /**
     * 设置新值
     */
    fun setValue(currentValue: Float) {
        if (this.currentValue == currentValue) {
            return
        }
        if (!scroller.isFinished) {
            scroller.forceFinished(true)
        }
        if (currentValue < minValue) {
            LogKit.e("RulerView", "数据比范围小")
            this.currentValue = minValue
        }
        if (currentValue > maxValue) {
            LogKit.e("RulerView", "数据比范围大")
            this.currentValue = maxValue
        }
        this.currentValue = currentValue
        currentNumber = (this.currentValue * 1000).toInt()
        val newDistance = (currentNumber - minNumber) / numberUnit * gradationGap
        val dx = (newDistance - currentDistance).toInt()
        // 最大2000ms
        val duration = dx * 2000 / numberRangeDistance.toInt()
        // 滑动到目标值
        scroller.startScroll(currentDistance.toInt(), 0, dx, duration)
        postInvalidate()
    }

    /**
     * 重新配置参数
     *
     * @param minValue 最小值
     * @param maxValue 最大值
     * @param curValue 当前值
     * @param unit     最小单位所代表的值
     * @param perCount 相邻两条长刻度线之间被分成的隔数量
     */
    fun setValue(minValue: Float, maxValue: Float, curValue: Float, unit: Float, perCount: Int) {
        require(minValue <= maxValue) {
            String.format(
                "The given values are invalid, check firstly: " +
                        "minValue=%f, maxValue=%f, curValue=%s", minValue, maxValue, curValue
            )
        }
        var safeCurValue = curValue
        if ((safeCurValue in minValue..maxValue).not()) {
            safeCurValue = if (curValue > maxValue) maxValue else minValue
        }
        if (!scroller.isFinished) {
            scroller.forceFinished(true)
        }
        this.minValue = minValue
        this.maxValue = maxValue
        currentValue = safeCurValue
        gradationUnit = unit
        numberPerCount = perCount
        convertValue2Number()
        valueChangedListener?.invoke(currentValue)
        postInvalidate()
    }

    fun setOnValueChangedListener(listener: ((Float) -> Unit)?) {
        valueChangedListener = listener
    }

    fun setOnViewDragListener(listener: ((Boolean) -> Unit)?) {
        onDragListener = listener
    }

    init {
        initAttrs(context, attrs)

        // 初始化final常量，必须在构造中赋初值
        val viewConfiguration = ViewConfiguration.get(context)
        touchSlop = viewConfiguration.scaledTouchSlop
        minFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity
        maxFlingVelocity = viewConfiguration.scaledMaximumFlingVelocity
        convertValue2Number()
        init(context)
    }
}