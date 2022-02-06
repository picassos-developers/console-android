package com.picassos.mint.console.android.libraries.smartcolorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ColorPickerSquare : View {
    private var mPaint: Paint? = null
    private var mLuar: Shader? = null
    val color = floatArrayOf(1f, 1f, 1f)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mPaint == null) {
            mPaint = Paint()
            mLuar = LinearGradient(0f, 0f, 0f, this.measuredHeight.toFloat(), -0x1, -0x1000000, Shader.TileMode.CLAMP)
        }
        val rgb = Color.HSVToColor(color)
        val dalam: Shader =
            LinearGradient(0f, 0f, this.measuredWidth.toFloat(), 0f, -0x1, rgb, Shader.TileMode.CLAMP)
        val shader = ComposeShader(mLuar!!, dalam, PorterDuff.Mode.MULTIPLY)
        mPaint!!.shader = shader
        canvas.drawRect(
            0f,
            0f,
            this.measuredWidth.toFloat(),
            this.measuredHeight.toFloat(),
            mPaint!!
        )
    }

    fun setHue(hue: Float) {
        color[0] = hue
        invalidate()
    }
}