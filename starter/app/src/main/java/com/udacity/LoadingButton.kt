package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val TAG = "LoadingButton"
private const val DURATION_ANIMATION = 2500L

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingProgress = 0f
    private var messageTextSize =
        context.resources.getDimension(R.dimen.default_text_size)
    private var spaceBetweenTextAndCircle =
        context.resources.getDimension(R.dimen.space_text_and_circle)
    private var circleProgressRadius =
        context.resources.getDimension(R.dimen.circle_download_radius)

    private var textColor = context.getColor(R.color.white)
    private var circleColor = context.getColor(R.color.colorAccent)
    private var buttonLoadingColor = context.getColor(R.color.colorPrimaryDark)
    private var buttonBackgroundColor = context.getColor(R.color.colorPrimary)

    private val valueAnimator = ValueAnimator()
    private val textBoundRect = Rect()

    private var text = String()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                Log.i(TAG, "buttonState : Loading")
                text = context.getString(R.string.we_are_downloading)
                valueAnimator.setFloatValues(0f, 1f)
                valueAnimator.apply {
                    duration = DURATION_ANIMATION
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    addUpdateListener {
                        loadingProgress = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }

            ButtonState.Completed -> {
                Log.i(TAG, "buttonState : Completed")
                if (valueAnimator.isRunning) {
                    valueAnimator.cancel()
                }
                loadingProgress = 0f
                text = context.getString(R.string.download)
                invalidate()
            }
        }

    }

    private val paintForText = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintForColor = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        isClickable = true
        buttonState = ButtonState.Completed

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            messageTextSize =
                getDimension(
                    R.styleable.LoadingButton_textSize,
                    resources.getDimension(R.dimen.default_text_size)
                )

            circleProgressRadius =
                getDimension(
                    R.styleable.LoadingButton_circleProgressRadius,
                    resources.getDimension(R.dimen.circle_download_radius)
                )

            textColor =
                getColor(R.styleable.LoadingButton_textColor, context.getColor(R.color.white))

            circleColor = getColor(
                R.styleable.LoadingButton_circleColor,
                context.getColor(R.color.colorAccent)
            )

            buttonBackgroundColor = getColor(
                R.styleable.LoadingButton_buttonBackgroundColor,
                context.getColor(R.color.colorPrimary)
            )

            buttonLoadingColor = getColor(
                R.styleable.LoadingButton_buttonLoadingColor,
                context.getColor(R.color.colorPrimaryDark)
            )
        }

        paintForText.apply {
            style = Paint.Style.FILL
            color = textColor
            textSize = messageTextSize
            textAlign = Paint.Align.LEFT
        }

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw background of button
        paintForColor.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintForColor)

        if (buttonState == ButtonState.Loading) {
            // draw the loading progress on button
            paintForColor.color = buttonLoadingColor
            canvas.drawRect(
                0f,
                0f,
                loadingProgress * widthSize.toFloat(),
                heightSize.toFloat(),
                paintForColor
            )
        }

        // draw text and circle for state downloading and completed
        paintForText.getTextBounds(text, 0, text.length, textBoundRect)

        if (buttonState == ButtonState.Completed) {
            canvas.drawText(
                text,
                (widthSize.toFloat() - textBoundRect.width()) / 2,
                (heightSize.toFloat() - (paintForText.descent() + paintForText.ascent())) / 2,
                paintForText
            )
        } else {
            val contentSpace =
                textBoundRect.width() + spaceBetweenTextAndCircle + circleProgressRadius * 2
            canvas.drawText(
                text,
                (widthSize.toFloat() - contentSpace.toFloat()) / 2,
                (heightSize.toFloat() - (paintForText.descent() + paintForText.ascent())) / 2,
                paintForText
            )

            // draw circle progress
            paintForColor.color = circleColor
            val circleLeft =
                (widthSize.toFloat() + contentSpace) / 2 - circleProgressRadius * 2
            val circleTop = heightSize.toFloat() / 2 - circleProgressRadius
            canvas.drawArc(
                circleLeft,
                circleTop,
                circleLeft + circleProgressRadius * 2,
                circleTop + circleProgressRadius * 2,
                0f,
                loadingProgress * 360f,
                true,
                paintForColor
            )
        }

    }

    fun setLoadingState(state: ButtonState) {
        buttonState = state
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
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
