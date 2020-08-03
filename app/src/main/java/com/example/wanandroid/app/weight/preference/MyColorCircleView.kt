package com.example.wanandroid.app.weight.preference

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.example.wanandroid.R

/**
 *@ author: lkw
 *created on:2020/7/16 17:32
 *description: 自定义圆形头像
 *email:lkw@mantoo.com.cn
 */
class MyColorCircleView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val strokePaint = Paint()
    private val fillPaint = Paint()
    private val borderWith = dimenPx(R.dimen.color_circle_view_border)

    private var transparentGrid: Drawable? = null

    init {
        setWillNotDraw(false)
        strokePaint.style = Paint.Style.STROKE
        strokePaint.isAntiAlias = true
        strokePaint.color = Color.BLACK
        strokePaint.strokeWidth = borderWith.toFloat()
        fillPaint.style = Paint.Style.FILL
        fillPaint.isAntiAlias = true
        fillPaint.color = Color.GRAY
    }

    @ColorInt
    var color: Int = Color.BLACK
        set(value) {
            field = value
            fillPaint.color = value
            invalidate()
        }

    @ColorInt
    var border: Int = Color.DKGRAY
        set(value) {
            field = value
            strokePaint.color = value
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (color == Color.TRANSPARENT) {
            if (transparentGrid == null) {
                transparentGrid = ContextCompat.getDrawable(context, R.drawable.transparentgrid)
            }
            transparentGrid?.setBounds(0, 0, measuredWidth, measuredHeight)
            transparentGrid?.draw(canvas)
        } else {
            canvas.drawCircle(
                measuredWidth / 2f,
                measuredHeight / 2f,
                (measuredWidth / 2f) - borderWith,
                fillPaint
            )
        }
        canvas.drawCircle(
            measuredWidth / 2f,
            measuredHeight / 2f,
            (measuredWidth / 2f) - borderWith,
            strokePaint
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        transparentGrid = null
    }

    fun setView(parseColor: Int) {
        color = parseColor
        border = parseColor
    }

    fun setViewSelect(parseColor: Int) {
        color = parseColor
        border = Color.DKGRAY
    }
}