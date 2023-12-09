package com.personalproject.personallibrary.lock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.personalproject.personallibrary.R
import kotlin.math.pow

class PatternView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var dotRadius = 30f
    private var dotSpacing = 150f
    private val paint = Paint()
    private val path = Path()
    private var dots = Array(3) { FloatArray(3) }
    private var isDrawing = false
    private var numRows = 3
    private var numColumns = 3
    private var radiusMultiplier = 1.0f
    private var valueOfPath:String=""
    private val selectedPoints = mutableListOf<Pair<Int, Int>>()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatternView)
        dotRadius = typedArray.getDimension(R.styleable.PatternView_dotRadius, dotRadius)
        dotSpacing = typedArray.getDimension(R.styleable.PatternView_dotSpacing, dotSpacing)
        numRows = typedArray.getInt(R.styleable.PatternView_numRows, numRows)
        numColumns = typedArray.getInt(R.styleable.PatternView_numColumns, numColumns)
        radiusMultiplier =
            typedArray.getFloat(R.styleable.PatternView_radiusMultiplier, radiusMultiplier)
        dots= Array(numRows) { FloatArray(numColumns) }




        paint.apply {
            color = typedArray.getColor(R.styleable.PatternView_lineColor, Color.BLACK)
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = typedArray.getDimension(R.styleable.PatternView_lineWidth, 5f)
        }
        typedArray.recycle()
        initializeDots()
    }


    private fun initializeDots() {
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                dots[i][j] = dotSpacing * (j + 1) * radiusMultiplier
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawDots(canvas)
        drawPath(canvas)
    }

    private fun drawDots(canvas: Canvas?) {
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                canvas?.drawCircle(dots[i][j], dotSpacing * (i + 1), dotRadius, paint)
            }
        }
    }

    private fun drawPath(canvas: Canvas?) {
        canvas?.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = startDrawing(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDrawing) {
                    continueDrawing(event.x, event.y)
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (isDrawing) {
                    stopDrawing()
                    isDrawing = false
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startDrawing(x: Float, y: Float): Boolean {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val dotX = dots[i][j]
                val dotY = dotSpacing * (i + 1)
                val distance =
                    Math.sqrt((x - dotX).toDouble().pow(2) + (y - dotY).toDouble().pow(2))

                if (distance < dotRadius) {
                    path.moveTo(dotX, dotY)
                    selectedPoints.add(Pair(i, j))
                    return true
                }
            }
        }
        return false
    }

    private fun continueDrawing(x: Float, y: Float) {
       /* val snappedX = snapToDot(x)
        val snappedY = snapToDot(y)

        path.lineTo(snappedX, snappedY)
        // Determine the index of the dot being connected
        val i = ((y / dotSpacing) / radiusMultiplier).toInt()
        val j = ((x / dotSpacing) / radiusMultiplier).toInt()

        // Check if the point is not already in the list
        if (!selectedPoints.contains(Pair(i, j))) {
            selectedPoints.add(Pair(i, j))
        }
        invalidate()

        */
        // new code here
        val snappedX = snapToDot(x)
        val snappedY = snapToDot(y)

        path.lineTo(snappedX, snappedY)

        // Check if the point is adjacent to the last point in the list
        val lastIndex = selectedPoints.size - 1
        if (lastIndex >= 0) {
            val lastPoint = selectedPoints[lastIndex]
            val i = ((y / dotSpacing) / radiusMultiplier).toInt()
            val j = ((x / dotSpacing) / radiusMultiplier).toInt()

            // Check if the new point is adjacent to the last point
            if (isAdjacent(lastPoint, Pair(i, j))) {
                // Check if the point is not already in the list
                if (!selectedPoints.contains(Pair(i, j))) {
                    selectedPoints.add(Pair(i, j))
                }
            }
        }

        invalidate()
    }
    private fun isAdjacent(point1: Pair<Int, Int>, point2: Pair<Int, Int>): Boolean {
        // Implement logic to check if two points are adjacent
        val rowDiff = Math.abs(point1.first - point2.first)
        val colDiff = Math.abs(point1.second - point2.second)
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)
    }

    private fun stopDrawing() {
        // Perform any actions needed when the drawing is complete
        // Perform any actions needed when the drawing is complete
        // Log the selected points
        // Notify the listener with the completed pattern

        patternListener?.onPatternCompleted(selectedPoints)
//        val selectedPointsString = selectedPoints.joinToString { "(i:${it.first}, j:${it.second})" }
//        Log.d("PatternView", "Selected Points: $selectedPointsString")
    }
    fun clearPattern() {
        path.reset()
        invalidate()
        selectedPoints.clear()
        println("PatternView cleared")
    }
    private fun snapToDot(coordinate: Float): Float {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val dotX = dots[i][j]
                if (Math.abs(coordinate - dotX) < dotSpacing / 2) {
                    return dotX
                }
            }
        }
        return coordinate
    }
    private var patternListener: PatternViewListener? = null

    fun setPatternListener(listener: PatternViewListener) {
        patternListener = listener
    }
}

