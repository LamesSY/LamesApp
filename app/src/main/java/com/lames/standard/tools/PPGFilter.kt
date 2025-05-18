package com.lames.standard.tools

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.lames.standard.R
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin

class PPGFilter {

    private val lineSize = 500

    private val fullPPGList = mutableListOf<Float>()
    private val ppgList = mutableListOf<Float>()
    private val simulateList = mutableListOf<Entry>()

    fun initFilter() {
        fullPPGList.clear()
        ppgList.clear()
        simulateList.clear()
        lastPPG = 9f
    }

    /**
     * 模拟正弦波形
     */
    fun simulatePPGList(lineChart: LineChart, list: List<Float>) {
        if (lineChart.data == null) {
            val lds = LineDataSet(mutableListOf(), "").also { it.setStyle() }
            lineChart.data = LineData(lds)
        }
        val lastIndex = lineChart.data.entryCount + simulateList.size.toFloat()
        repeat(list.size) {
            val i = lastIndex + it
            val y = (sin(2 * PI * i / (100)) * 100) + (-3..3).random()
            simulateList.add(Entry(i, y.toFloat()))
        }
        if (simulateList.size < 55) return
        simulateList.forEach { lineChart.data.addEntry(it, 0) }
        lineChart.notifyDataSetChanged()
        lineChart.setVisibleXRange(500f, 500f)
        lineChart.moveViewToX(lastIndex + list.size)
        lineChart.invalidate()
        simulateList.clear()
    }

    private var lastPPG = 9f
    fun cusAppendPPGList(lineChart: LineChart, list: List<Float>) {
        ppgList.addAll(list)
        //先进行均值滤波
        ppgList.avgFilter(3)
        if (ppgList.isPeriodWave(3).not()) return
        //积累到一个完整周期的波形后进行归一化处理
        val min = ppgList.minOf { it }
        val max = ppgList.maxOf { it }
        repeat(ppgList.size) { ppgList[it] = -((ppgList[it] - min) / (max - min)) }
        //波形落差过大则插入值，后续可以增加曲率优化视觉效果
        val firstPPG = ppgList.first()
        if (lastPPG != 9f && (firstPPG - lastPPG).absoluteValue > 0.2f) {
            var temp = firstPPG
            if (lastPPG > firstPPG) do {
                temp += 0.02f
                ppgList.add(0, temp)
            } while (temp < lastPPG)
            else do {
                temp -= 0.02f
                ppgList.add(0, temp)
            } while (temp > lastPPG)
        }
        lastPPG = ppgList.last()
        if (lineChart.data == null) {
            val lds = LineDataSet(mutableListOf(), "").also { it.setStyle() }
            lineChart.data = LineData(lds)
        }
        val lastIndex = lineChart.data.entryCount.toFloat()
        ppgList.forEachIndexed { i, f ->
            lineChart.data.addEntry(Entry(lastIndex + i, f), 0)
        }
        lineChart.notifyDataSetChanged()
        lineChart.setVisibleXRange(500f, 500f)
        lineChart.moveViewToX(lastIndex + ppgList.size)
        ppgList.clear()
    }

    fun appendPPGList(lineChart: LineChart, list: List<Float>) {
        ppgList.addAll(list)
        val newList = if (ppgList.size < lineSize) ppgList
        else {
            repeat(ppgList.size - lineSize) { ppgList.removeFirst() }
            //removeBaselineDriftAndNoise(ppgList, 500, 100000f)
            removeBaseline(ppgList)
            //syncMaxMin(ppgList)
        }

        val entryList = newList.mapIndexed { i, fl -> Entry(i.toFloat(), fl) }
        val lds = LineDataSet(entryList, "").also { it.setStyle() }
        lineChart.data = LineData(lds)
        lineChart.xAxis.axisMaximum = lineSize.toFloat()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun syncMaxMin(ppgList: List<Float>): List<Float> {
        val s = ppgList.size
        val newList = ppgList.toMutableList()
        val sortList = ppgList.mapIndexed { i, f -> i to f }.sortedBy { it.second }
        for (i in 0..3) newList[sortList[i].first] = sortList[0].second
        for (i in s - 1 downTo s - 3) newList[sortList[i].first] = sortList[0].second
        return newList
    }

    private fun MutableList<Float>.avgFilter(windowSize: Int) {
        if (windowSize > this.size - 1) return
        val d = windowSize / 2
        for (i in d until this.size - d) {
            val sl = List(windowSize) { this[i - d + it] }.average().toFloat()
            this[i] = sl
        }
    }

    private fun MutableList<Float>.centerFilter(windowSize: Int) {
        if (windowSize > this.size - 1) return
        val d = windowSize / 2
        for (i in d until this.size - d) {
            val sl = List(windowSize) { this[i - d + it] }.sorted()[d]
            this[i] = sl
        }
    }

    private fun removeBaseline(ppgList: List<Float>): List<Float> {
        val min = ppgList.minOf { it }
        val max = ppgList.maxOf { it }
        val newList = mutableListOf<Float>()
        newList.addAll(ppgList)
        repeat(newList.size) { newList[it] = (newList[it] - min) / (max - min) * 10 }
        return newList
    }

    private fun removeBaselineDrift(signal: FloatArray, windowSize: Int): FloatArray {
        val result = FloatArray(signal.size)

        // Apply a moving average filter to the signal
        for (i in signal.indices) {
            val startIndex = maxOf(0, i - windowSize / 2)
            val endIndex = minOf(signal.size - 1, i + windowSize / 2)

            // Calculate the average within the window
            var sum = 0.0f
            for (j in startIndex..endIndex) {
                sum += signal[j]
            }
            val average = sum / (endIndex - startIndex + 1)

            // Subtract the average from the current value to remove baseline drift
            result[i] = signal[i] - average
        }

        return result
    }

    private fun filterPPGData(ppgData: List<Float>, windowSize: Int): List<Float> {
        val filteredData = mutableListOf<Float>()

        for (i in ppgData.indices) {
            val startIndex = maxOf(0, i - windowSize / 2)
            val endIndex = minOf(ppgData.size - 1, i + windowSize / 2)

            // Apply a moving average filter to the signal
            val average = ppgData.subList(startIndex, endIndex + 1).average().toFloat()

            // Apply a median filter to the signal
            val sortedWindow = ppgData.subList(startIndex, endIndex + 1).sorted()
            val median = if (sortedWindow.size % 2 == 0) {
                (sortedWindow[sortedWindow.size / 2 - 1] + sortedWindow[sortedWindow.size / 2]) / 2.0f
            } else {
                sortedWindow[sortedWindow.size / 2]
            }

            // Combine moving average and median to filter the signal
            val filteredValue = (average + median) / 2.0f
            filteredData.add(filteredValue)
        }

        return filteredData
    }

    private fun removeBaselineDriftAndNoise(
        ppgData: List<Float>,
        windowSize: Int,
        threshold: Float,
    ): List<Float> {
        val filteredData = mutableListOf<Float>()

        for (i in ppgData.indices) {
            val startIndex = maxOf(0, i - windowSize / 2)
            val endIndex = minOf(ppgData.size - 1, i + windowSize / 2)

            // Apply a high-pass filter to remove baseline drift
            val baselineDriftRemoved =
                ppgData[i] - ppgData.subList(startIndex, endIndex + 1).average().toFloat()

            // Apply a median filter to reduce noise
            val sortedWindow = ppgData.subList(startIndex, endIndex + 1).sorted()
            val median = if (sortedWindow.size % 2 == 0) {
                (sortedWindow[sortedWindow.size / 2 - 1] + sortedWindow[sortedWindow.size / 2]) / 2.0f
            } else {
                sortedWindow[sortedWindow.size / 2]
            }

            // Check if the difference between the original value and the median is below the threshold
            // If yes, use the median value to further reduce baseline drift
            val filteredValue = if ((ppgData[i] - median).absoluteValue < threshold) {
                median
            } else {
                baselineDriftRemoved
            }

            filteredData.add(filteredValue)
        }

        return filteredData
    }

    private fun List<Float>.hasCrestOrTrough(windowSize: Int): Boolean {
        if (this.size < 100) return false
        for (i in windowSize until this.size - windowSize) {
            val tl = this.subList(i - 20, i + windowSize)
            if (tl[10] <= tl.minOf { it } || tl[10] >= tl.maxOf { it }) return true
        }
        return false
    }

    private fun List<Float>.isPeriodWave(ws: Int): Boolean {
        if (this.size <= 40) return false
        val minMax = floatArrayOf(0f, 0f)
        for (i in ws until this.size - ws) {
            val tl = this.subList(i - ws, i + ws).sorted()
            if (tl.minOf { it } == this[i]) minMax[0] = tl[ws]
            if (tl.maxOf { it } == this[i]) minMax[1] = tl[ws]
            if (minMax[0] != 0f && minMax[1] != 0f) return true
        }
        return false
    }

    private fun List<Float>.findPeak(ws: Int): Int {
        if (this.size < 10 || ws !in 0..5) return -1
        val c = ws / 2
        for (i in c until this.size - c) {
            val tl = this.subList(i - c, i + c).sorted()
            if (tl.maxOf { it } == this[i]) return i
        }
        return -1
    }

    private fun List<Float>.findTrough(ws: Int): Int {
        if (this.size < 10 || ws !in 0..5) return -1
        val c = ws / 2
        for (i in c until this.size - c) {
            val tl = this.subList(i - c, i + c).sorted()
            if (tl.minOf { it } == this[i]) return i
        }
        return -1
    }

    private fun LineDataSet.setStyle() {
        mode = LineDataSet.Mode.CUBIC_BEZIER
        axisDependency = YAxis.AxisDependency.RIGHT
        setDrawValues(false)
        setDrawCircles(false)
        setDrawFilled(false)
        lineWidth = 2f
        color = forColor(R.color.label_normal)
    }

}