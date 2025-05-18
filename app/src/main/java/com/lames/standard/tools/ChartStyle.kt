package com.lames.standard.tools

import android.app.Activity
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.lames.standard.R

/**
 * 基础样式1，
 * 左Y轴为主轴, 不绘制基线，绘制横线，禁止放大
 * 隐藏description、legend
 * X轴底部为主，不绘制x轴基线、垂直线，允许放大
 */
fun setLeftSimpleChart(aty: Activity, candleStickChart: BarLineChartBase<*>) {
    candleStickChart.setDrawGridBackground(false)
    candleStickChart.setTouchEnabled(true)
    candleStickChart.setNoDataText("暂无数据")
    candleStickChart.setNoDataTextColor(forAttrColor(aty, R.attr.themeColor))
    candleStickChart.extraBottomOffset = 10f
    candleStickChart.description.isEnabled = false
    candleStickChart.legend.isEnabled = false
    candleStickChart.isScaleYEnabled = false
    candleStickChart.axisRight.isEnabled = false
    candleStickChart.axisLeft.isEnabled = true
    candleStickChart.axisLeft.textSize = 12f
    candleStickChart.axisLeft.textColor = forAttrColor(aty, R.attr.text_4)
    candleStickChart.axisLeft.setDrawAxisLine(false)
    candleStickChart.axisLeft.gridColor = forAttrColor(aty, R.attr.text_4)
    candleStickChart.axisLeft.gridLineWidth = 0.5f

    candleStickChart.xAxis.isEnabled = true
    candleStickChart.xAxis.granularity = 1f
    candleStickChart.xAxis.setDrawGridLines(false)
    candleStickChart.xAxis.textSize = 12f
    candleStickChart.xAxis.textColor = forAttrColor(aty, R.attr.text_4)

    candleStickChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
}

fun buildLimitLine(limit: Float, label: String) = LimitLine(limit, label).also {
    it.textSize = 14f
    it.textColor = forColor(R.color.label_warning)
    it.lineColor = forColor(R.color.label_warning)
    it.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
    it.enableDashedLine(10f, 5f, 10f)
}

class TimeAxisValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "%02d:%02d".format(value.toInt() / 60, value.toInt() % 60)
    }
}