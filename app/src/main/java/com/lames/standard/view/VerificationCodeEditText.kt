package com.lames.standard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.lames.standard.R
import com.lames.standard.tools.getScreenWidth
import java.util.Timer
import java.util.TimerTask

class VerificationCodeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatEditText(context, attrs, defStyleAttr), VerificationAction, TextWatcher {

    companion object {
        private const val DEFAULT_CURSOR_DURATION = 400
    }

    private var figures = 0 //需要输入的位数
    private var verCodeMargin = 0 //验证码之间的间距
    private var bottomSelectedColor = 0 //底部选中的颜色
    private var bottomNormalColor = 0 //未选中的颜色
    private var bottomLineHeight = 0f //底线的高度
    private var selectedBackgroundColor = 0 //选中的背景颜色
    private var cursorWidth = 0 //光标宽度
    private var cursorColor = 0 //光标颜色
    private var cursorDuration = 0 //光标闪烁间隔
    private var onCodeChangedListener: VerificationAction.OnVerificationCodeChangedListener? = null
    private var currentPosition = 0
    private var eachRectLength = 0 //每个矩形的边长
    private lateinit var selectedBackgroundPaint: Paint
    private lateinit var normalBackgroundPaint: Paint
    private lateinit var bottomSelectedPaint: Paint
    private lateinit var bottomNormalPaint: Paint
    private lateinit var cursorPaint: Paint

    // 控制光标闪烁
    private var isCursorShowing = false
    private var cursorTimerTask: TimerTask? = null
    private var cursorTimer: Timer? = null

    init {
        initAttrs(attrs)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent)) //防止出现下划线
        initPaint()
        initCursorTimer()
        isFocusableInTouchMode = true
        super.addTextChangedListener(this)
    }

    /**
     * 初始化paint
     */
    private fun initPaint() {
        selectedBackgroundPaint = Paint().apply {
            color = selectedBackgroundColor
        }
        normalBackgroundPaint = Paint().apply {
            color = getColor(android.R.color.transparent)
        }
        bottomNormalPaint = Paint().apply {
            color = bottomNormalColor
            strokeWidth = bottomLineHeight
        }
        bottomSelectedPaint = Paint().apply {
            color = bottomSelectedColor
            strokeWidth = bottomLineHeight
        }
        cursorPaint = Paint().apply {
            isAntiAlias = true
            color = cursorColor
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = cursorWidth.toFloat()
        }
    }

    /**
     * 初始化Attrs
     */
    private fun initAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VerificationCodeEditText)
        figures = ta.getInteger(R.styleable.VerificationCodeEditText_figures, 4)
        verCodeMargin =
            ta.getDimension(R.styleable.VerificationCodeEditText_verCodeMargin, 0f).toInt()
        bottomSelectedColor = ta.getColor(
            R.styleable.VerificationCodeEditText_bottomLineSelectedColor,
            currentTextColor
        )
        bottomNormalColor = ta.getColor(
            R.styleable.VerificationCodeEditText_bottomLineNormalColor,
            getColor(android.R.color.darker_gray)
        )
        bottomLineHeight = ta.getDimension(
            R.styleable.VerificationCodeEditText_bottomLineHeight,
            dp2px(5).toFloat()
        )
        selectedBackgroundColor = ta.getColor(
            R.styleable.VerificationCodeEditText_selectedBackgroundColor,
            getColor(android.R.color.darker_gray)
        )
        cursorWidth =
            ta.getDimension(R.styleable.VerificationCodeEditText_cursorWidth, dp2px(1).toFloat())
                .toInt()
        cursorColor = ta.getColor(
            R.styleable.VerificationCodeEditText_cursorColor,
            getColor(android.R.color.darker_gray)
        )
        cursorDuration = ta.getInteger(
            R.styleable.VerificationCodeEditText_cursorDuration,
            DEFAULT_CURSOR_DURATION
        )
        ta.recycle()
        layoutDirection = LAYOUT_DIRECTION_LTR
    }

    private fun initCursorTimer() {
        cursorTimerTask = object : TimerTask() {
            override fun run() {
                // 通过光标间歇性显示实现闪烁效果
                isCursorShowing = !isCursorShowing
                postInvalidate()
            }
        }
        cursorTimer = Timer()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // 启动定时任务，定时刷新实现光标闪烁
        cursorTimer?.scheduleAtFixedRate(cursorTimerTask, 0, cursorDuration.toLong())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cursorTimer?.cancel()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthResult = 0
        var heightResult = 0
        //最终的宽度
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        widthResult = if (widthMode == MeasureSpec.EXACTLY) widthSize else context.getScreenWidth()
        //每个矩形形的宽度
        eachRectLength = (widthResult - verCodeMargin * (figures - 1)) / figures
        //最终的高度
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        heightResult = if (heightMode == MeasureSpec.EXACTLY) heightSize else eachRectLength
        setMeasuredDimension(widthResult, heightResult)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            requestFocus()
            setSelection(text?.length ?: 0)
            showKeyBoard(context)
            return false
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        currentPosition = text!!.length
        val width = eachRectLength - paddingLeft - paddingRight
        val height = measuredHeight - paddingTop - paddingBottom
        for (i in 0 until figures) {
            canvas.save()
            val start = width * i + i * verCodeMargin
            val end = width + start
            //画一个矩形
            if (i == currentPosition) canvas.drawRect(
                start.toFloat(),
                0f,
                end.toFloat(),
                height.toFloat(),
                selectedBackgroundPaint
            ) //选中的下一个状态
            else canvas.drawRect(
                start.toFloat(),
                0f,
                end.toFloat(),
                height.toFloat(),
                normalBackgroundPaint
            )
            canvas.restore()
        }
        //绘制文字
        val value = text.toString()
        for (i in value.indices) {
            canvas.save()
            val start = width * i + i * verCodeMargin
            val x = (start + width / 2).toFloat()
            val paint = paint
            paint.textAlign = Paint.Align.CENTER
            paint.color = currentTextColor
            val fontMetrics = paint.fontMetrics
            val baseline = ((height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top)
            canvas.drawText(value[i].toString(), x, baseline, paint)
            canvas.restore()
        }
        //绘制底线
        for (i in 0 until figures) {
            canvas.save()
            val lineY = height - bottomLineHeight / 2
            val start = width * i + i * verCodeMargin
            val end = width + start
            if (i < currentPosition) canvas.drawLine(
                start.toFloat(),
                lineY,
                end.toFloat(),
                lineY,
                bottomSelectedPaint
            )
            else canvas.drawLine(start.toFloat(), lineY, end.toFloat(), lineY, bottomNormalPaint)
            canvas.restore()
        }
        //绘制光标
        if (!isCursorShowing && isCursorVisible && currentPosition < figures && hasFocus()) {
            canvas.save()
            val startX = currentPosition * (width + verCodeMargin) + width / 2
            val startY = height / 4
            val endY = height - height / 4
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                startX.toFloat(),
                endY.toFloat(),
                cursorPaint
            )
            canvas.restore()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        currentPosition = text?.length ?: 0
        postInvalidate()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        currentPosition = text?.length ?: 0
        postInvalidate()
        if (onCodeChangedListener != null) {
            onCodeChangedListener!!.onVerCodeChanged(text, start, before, count)
        }
    }

    override fun afterTextChanged(s: Editable) {
        currentPosition = text?.length ?: 0
        postInvalidate()
        if (text!!.length == figures) onCodeChangedListener?.onInputCompleted(text)
        else if (text?.length ?: 0 > figures) text!!.delete(figures, text?.length ?: 0)
    }

    override fun setFigures(figures: Int) {
        this.figures = figures
        postInvalidate()
    }

    override fun setVerCodeMargin(margin: Int) {
        verCodeMargin = margin
        postInvalidate()
    }

    override fun setBottomSelectedColor(@ColorRes bottomSelectedColor: Int) {
        this.bottomSelectedColor = getColor(bottomSelectedColor)
        postInvalidate()
    }

    override fun setBottomNormalColor(@ColorRes bottomNormalColor: Int) {
        bottomSelectedColor = getColor(bottomNormalColor)
        postInvalidate()
    }

    override fun setSelectedBackgroundColor(@ColorRes selectedBackground: Int) {
        selectedBackgroundColor = getColor(selectedBackground)
        postInvalidate()
    }

    override fun setBottomLineHeight(bottomLineHeight: Int) {
        this.bottomLineHeight = bottomLineHeight.toFloat()
        postInvalidate()
    }

    override fun setOnVerificationCodeChangedListener(listener: VerificationAction.OnVerificationCodeChangedListener?) {
        onCodeChangedListener = listener
    }

    private fun getColor(@ColorRes color: Int): Int = ContextCompat.getColor(context, color)

    private fun dp2px(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()

    fun showKeyBoard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }
}