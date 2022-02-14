package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progress = 0f

    //Base button attributes________________________
    private val baseButton = Rect()
    private val baseButtonColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimary, context.theme)
    private val baseButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = baseButtonColor
    }

    //Loading layer attributes______________________
    private val loadingLayer = Rect()
    private val loadingLayerColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, context.theme)
    private val loadingLayerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = loadingLayerColor
    }

    //Loading arch attributes_______________________
    private val loadingArc = RectF()
    private val loadingArcColor =
        ResourcesCompat.getColor(resources, R.color.colorAccent, context.theme)
    private val loadingArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = loadingArcColor
    }
    //Button text attributes________________________
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, context.theme)
    private val btnTextSize = 75.0f
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textAlign = Paint.Align.CENTER
        textSize = btnTextSize
    }

    // Button state switcher
    private var valueAnimator = ValueAnimator()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, buttonState ->
        when (buttonState) {
            ButtonState.Clicked -> {}
            ButtonState.Loading -> {
                // Launch animations
                valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                    .setDuration(2000)
                    .apply {
                        addUpdateListener {
                            progress = it.animatedValue as Float
                            repeatMode = ValueAnimator.RESTART
                            repeatCount = ValueAnimator.INFINITE
                            invalidate()
                        }
                    }
                valueAnimator.start()
                isClickable = false
            }
            ButtonState.Completed -> {
                // Cancel animations
                valueAnimator.cancel()
                progress = 0f
                isClickable = true
                invalidate()
            }
        }
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawMainButton(canvas)
        drawLoadingLayer(canvas)
        drawLoadingArc(canvas)
        drawTextOnButton(canvas)
    }

    // Main button fun
    private fun drawMainButton(canvas: Canvas?) {
        baseButton.set(0,0,widthSize,heightSize)
        canvas?.drawRect(baseButton,baseButtonPaint)
    }

    // Loading layer fun
    private fun drawLoadingLayer(canvas: Canvas?) {
        loadingLayer.set(
            0,
            0,
            (widthSize*progress).toInt(),
            heightSize
        )
        canvas?.drawRect(loadingLayer,loadingLayerPaint)
    }

    // Loading arc fun
    private fun drawLoadingArc(canvas: Canvas?) {
        canvas?.getClipBounds(baseButton)
        val clipH = baseButton.height()
        val clipW = baseButton.width()
        textPaint.getTextBounds(
            resources.getString(R.string.button_loading),
            0,
            resources.getString(R.string.button_loading).length,
            baseButton
        )

        loadingArc.set(
            (clipW /2f + baseButton.width() /2f) - baseButton.height() /1.5f,
            (clipH /2f) - baseButton.height() /1.5f,
            (clipW /2f + baseButton.width() /2f) + baseButton.height() /1.5f,
            (clipH /2f) + baseButton.height() /1.5f
        )
        canvas?.drawArc(
            loadingArc,
            0f,
            progress * 360f,
            true,
            loadingArcPaint
        )
    }

    // Text fun
    private fun drawTextOnButton(canvas: Canvas?) {
        val string = if (buttonState == ButtonState.Loading) {
            resources.getString(R.string.button_loading)
        } else {
            resources.getString(R.string.button_name)
        }

        canvas?.drawText(
            string,
            (canvas.width / 2).toFloat(),
            canvas.height.div(2)
                .minus(((textPaint.descent()
                        + textPaint.ascent()) / 2)),
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}