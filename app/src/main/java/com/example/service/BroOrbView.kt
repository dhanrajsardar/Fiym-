package com.example.service

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import kotlin.math.cos
import kotlin.math.sin

class BroOrbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Expression {
        NORMAL,
        HAPPY_SMILE,
        WINK,
        ANNOYED,
        ANGRY,
        HEART_EYES,
        LAUGHING,
        CRYING
    }

    var currentExpression: Expression = Expression.NORMAL
        set(value) {
            field = value
            invalidate()
        }

    // 3D Head Orientation (-1.0 to 1.0)
    private var targetYaw = 0f    // Horizontal turn (-1 = far left / back, +1 = far right / back)
    private var currentYaw = 0f
    private var targetPitch = 0f  // Vertical turn (-1 = look up, +1 = look down)
    private var currentPitch = 0f

    // Pupil shift relative to eye center
    private var pupilDx = 0f
    private var pupilDy = 0f

    // Breathing and Squish-and-Stretch Animation
    private var floatOffsetY = 0f
    private var scaleXFactor = 1.0f
    private var scaleYFactor = 1.0f

    private var isBlinking = false
    private val mainHandler = Handler(Looper.getMainLooper())

    // Animators
    private var breathAnimator: ValueAnimator? = null
    private var turnAnimator: ValueAnimator? = null

    // Paint Objects
    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 190
    }
    private val eyeWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val eyeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#809CE6")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val pupilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#101524")
        style = Paint.Style.FILL
    }
    private val glintMainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val cheekPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#35FF6B8B")
        style = Paint.Style.FILL
    }
    private val featurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#181818")
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
    }
    private val fillFeaturePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#181818")
        style = Paint.Style.FILL
    }
    private val heartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF2D55")
        style = Paint.Style.FILL
    }
    private val tearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4DA2FF")
        style = Paint.Style.FILL
    }

    private val tearPath = Path()
    private val heartPath = Path()

    init {
        startBreathingAnimation()
        startIdleGazeLoop()
        startBlinkLoop()
    }

    private fun startBreathingAnimation() {
        breathAnimator = ValueAnimator.ofFloat(0f, (Math.PI * 2).toFloat()).apply {
            duration = 2400
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val progress = anim.animatedValue as Float
                floatOffsetY = sin(progress.toDouble()).toFloat() * 6f
                scaleYFactor = 1.0f + (sin(progress.toDouble()).toFloat() * 0.025f)
                scaleXFactor = 1.0f - (sin(progress.toDouble()).toFloat() * 0.025f)
                invalidate()
            }
        }
        breathAnimator?.start()
    }

    private fun startBlinkLoop() {
        val blinkRunnable = object : Runnable {
            override fun run() {
                if (currentExpression == Expression.NORMAL || currentExpression == Expression.ANNOYED) {
                    doBlink()
                }
                val nextDelay = (2500..5500).random().toLong()
                mainHandler.postDelayed(this, nextDelay)
            }
        }
        mainHandler.postDelayed(blinkRunnable, 3000L)
    }

    private fun doBlink() {
        isBlinking = true
        invalidate()
        mainHandler.postDelayed({
            isBlinking = false
            invalidate()
        }, 140L)
    }

    private fun startIdleGazeLoop() {
        val idleRunnable = object : Runnable {
            override fun run() {
                // Occasionally look around naturally if user is idle
                if (currentExpression == Expression.NORMAL) {
                    val randomChoice = (0..5).random()
                    when (randomChoice) {
                        0 -> smoothTurnTo(0f, 0f)              // Look straight ahead
                        1 -> smoothTurnTo(-0.45f, -0.1f)       // Look slightly left
                        2 -> smoothTurnTo(0.45f, -0.1f)        // Look slightly right
                        3 -> smoothTurnTo(0f, -0.4f)           // Look up curiously
                        4 -> smoothTurnTo(0.6f, 0.2f)          // Look down right
                        5 -> smoothTurnTo(-0.7f, 0f)           // Turn far left
                    }
                }
                val nextDelay = (3500..7000).random().toLong()
                mainHandler.postDelayed(this, nextDelay)
            }
        }
        mainHandler.postDelayed(idleRunnable, 4000L)
    }

    fun smoothTurnTo(yaw: Float, pitch: Float) {
        targetYaw = yaw.coerceIn(-1f, 1f)
        targetPitch = pitch.coerceIn(-1f, 1f)

        turnAnimator?.cancel()
        val startYaw = currentYaw
        val startPitch = currentPitch

        turnAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val fraction = anim.animatedFraction
                currentYaw = startYaw + (targetYaw - startYaw) * fraction
                currentPitch = startPitch + (targetPitch - startPitch) * fraction
                invalidate()
            }
        }
        turnAnimator?.start()
    }

    fun setDragOffset(dx: Float, dy: Float) {
        // Map drag displacement to 3D yaw and pitch turns
        targetYaw = (dx / 60f).coerceIn(-0.95f, 0.95f)
        targetPitch = (dy / 60f).coerceIn(-0.85f, 0.85f)
        currentYaw = targetYaw
        currentPitch = targetPitch
        pupilDx = (dx / 40f).coerceIn(-0.9f, 0.9f)
        pupilDy = (dy / 40f).coerceIn(-0.9f, 0.9f)
        invalidate()
    }

    fun releaseDrag() {
        pupilDx = 0f
        pupilDy = 0f
        smoothTurnTo(0f, 0f)
    }

    fun triggerHappyBounce() {
        currentExpression = Expression.HAPPY_SMILE
        val bounceAnim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            interpolator = OvershootInterpolator(3.0f)
            addUpdateListener { anim ->
                val fraction = anim.animatedFraction
                scaleXFactor = 1.0f + sin(fraction * Math.PI).toFloat() * 0.25f
                scaleYFactor = 1.0f - sin(fraction * Math.PI).toFloat() * 0.15f
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    scaleXFactor = 1.0f
                    scaleYFactor = 1.0f
                }
            })
        }
        bounceAnim.start()

        mainHandler.postDelayed({
            currentExpression = Expression.NORMAL
            invalidate()
        }, 2200L)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        canvas.save()

        // Apply idle floating offset & squish scale
        val cx = w / 2f
        val cy = (h / 2f) + floatOffsetY
        val baseRadius = (Math.min(w, h) / 2f) - 6f

        canvas.scale(scaleXFactor, scaleYFactor, cx, cy)

        // 1. Draw 3D Glossy Periwinkle/Lavender Blue Sphere Body (from orb.png)
        val bodyShader = RadialGradient(
            cx - baseRadius * 0.35f + (currentYaw * baseRadius * 0.2f),
            cy - baseRadius * 0.35f + (currentPitch * baseRadius * 0.2f),
            baseRadius * 1.35f,
            intArrayOf(
                Color.parseColor("#E4EDFF"), // Soft periwinkle specular top highlight
                Color.parseColor("#ADC3FF"), // Core soft lavender/blue body
                Color.parseColor("#7A95E8")  // Deep 3D bottom shading
            ),
            floatArrayOf(0.0f, 0.58f, 1.0f),
            Shader.TileMode.CLAMP
        )
        bodyPaint.shader = bodyShader
        canvas.drawCircle(cx, cy, baseRadius, bodyPaint)

        // 2. Specular Top Highlight Oval Reflection
        val specW = baseRadius * 0.62f
        val specH = baseRadius * 0.32f
        val specX = cx - baseRadius * 0.32f + (currentYaw * baseRadius * 0.1f)
        val specY = cy - baseRadius * 0.52f + (currentPitch * baseRadius * 0.1f)
        val specRect = RectF(specX - specW / 2f, specY - specH / 2f, specX + specW / 2f, specY + specH / 2f)
        canvas.drawOval(specRect, highlightPaint)

        // 3. Check if turned far away (Back angle of sphere)
        if (Math.abs(currentYaw) > 0.85f) {
            // Turned around: smooth glossy lavender sphere back view
            canvas.restore()
            return
        }

        // 4. Calculate 3D Perspective Eye Centers & Distortions based on Yaw and Pitch
        val faceCenterX = cx + (currentYaw * baseRadius * 0.55f)
        val faceCenterY = cy + (currentPitch * baseRadius * 0.45f)

        val eyeSpacing = baseRadius * 0.38f
        val eyeBaseRadius = baseRadius * 0.26f

        // 3D Spherical perspective scaling: the eye on the turned side compresses/scales down
        val leftEyePerspectiveScale = (1.0f + (currentYaw * 0.35f)).coerceIn(0.2f, 1.2f)
        val rightEyePerspectiveScale = (1.0f - (currentYaw * 0.35f)).coerceIn(0.2f, 1.2f)

        val leftEyeX = faceCenterX - (eyeSpacing * leftEyePerspectiveScale)
        val rightEyeX = faceCenterX + (eyeSpacing * rightEyePerspectiveScale)
        val eyesY = faceCenterY - (baseRadius * 0.05f)

        // 5. Cute Blush Cheeks (visible when face is forward-facing)
        val cheekAlpha = ((1.0f - Math.abs(currentYaw) * 0.8f) * 255).toInt().coerceIn(0, 255)
        cheekPaint.alpha = (cheekAlpha * 0.35f).toInt()
        val cheekY = faceCenterY + baseRadius * 0.22f
        canvas.drawCircle(leftEyeX, cheekY, baseRadius * 0.16f, cheekPaint)
        canvas.drawCircle(rightEyeX, cheekY, baseRadius * 0.16f, cheekPaint)

        // 6. Draw Eyes & Face Expressions according to `currentExpression`
        if (isBlinking) {
            // Closed blinking eyes (flat or curved line)
            featurePaint.strokeWidth = 6f
            canvas.drawLine(leftEyeX - eyeBaseRadius * 0.6f, eyesY, leftEyeX + eyeBaseRadius * 0.6f, eyesY, featurePaint)
            canvas.drawLine(rightEyeX - eyeBaseRadius * 0.6f, eyesY, rightEyeX + eyeBaseRadius * 0.6f, eyesY, featurePaint)
            canvas.restore()
            return
        }

        when (currentExpression) {
            Expression.HAPPY_SMILE -> {
                // Curved ^ ^ eyes
                featurePaint.strokeWidth = 6.5f
                drawCurvedEyeArc(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale)
                drawCurvedEyeArc(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale)

                // Open cute smiling mouth
                val mouthBox = RectF(faceCenterX - baseRadius * 0.22f, faceCenterY + baseRadius * 0.12f, faceCenterX + baseRadius * 0.22f, faceCenterY + baseRadius * 0.42f)
                canvas.drawArc(mouthBox, 10f, 160f, true, fillFeaturePaint)
            }

            Expression.WINK -> {
                // Left eye winking (^), Right eye big open round
                drawCurvedEyeArc(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale)
                drawStandard3DEye(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale, pupilDx, pupilDy)

                // Cute side smile mouth
                featurePaint.strokeWidth = 5f
                val mouthBox = RectF(faceCenterX - baseRadius * 0.15f, faceCenterY + baseRadius * 0.15f, faceCenterX + baseRadius * 0.2f, faceCenterY + baseRadius * 0.38f)
                canvas.drawArc(mouthBox, 20f, 140f, false, featurePaint)
            }

            Expression.LAUGHING -> {
                // Squeezed > < eyes
                featurePaint.strokeWidth = 6.5f
                drawAngleEye(canvas, leftEyeX, eyesY, eyeBaseRadius, true)
                drawAngleEye(canvas, rightEyeX, eyesY, eyeBaseRadius, false)

                // Big happy laughing mouth
                val mouthBox = RectF(faceCenterX - baseRadius * 0.28f, faceCenterY + baseRadius * 0.1f, faceCenterX + baseRadius * 0.28f, faceCenterY + baseRadius * 0.48f)
                canvas.drawArc(mouthBox, 0f, 180f, true, fillFeaturePaint)
            }

            Expression.ANNOYED -> {
                // Eyelids half lowered (sleepy/bored)
                drawStandard3DEye(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale, pupilDx, pupilDy)
                drawStandard3DEye(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale, pupilDx, pupilDy)

                // Draw half eyelids lid cover
                featurePaint.strokeWidth = 5f
                val lidL = RectF(leftEyeX - eyeBaseRadius * leftEyePerspectiveScale, eyesY - eyeBaseRadius * leftEyePerspectiveScale, leftEyeX + eyeBaseRadius * leftEyePerspectiveScale, eyesY)
                val lidR = RectF(rightEyeX - eyeBaseRadius * rightEyePerspectiveScale, eyesY - eyeBaseRadius * rightEyePerspectiveScale, rightEyeX + eyeBaseRadius * rightEyePerspectiveScale, eyesY)
                canvas.drawRect(lidL, fillFeaturePaint)
                canvas.drawRect(lidR, fillFeaturePaint)

                // Flat unimpressed line mouth
                featurePaint.strokeWidth = 5f
                canvas.drawLine(faceCenterX - baseRadius * 0.18f, faceCenterY + baseRadius * 0.25f, faceCenterX + baseRadius * 0.18f, faceCenterY + baseRadius * 0.25f, featurePaint)
            }

            Expression.ANGRY -> {
                drawStandard3DEye(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale, pupilDx, pupilDy)
                drawStandard3DEye(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale, pupilDx, pupilDy)

                // Tilted V eyebrows
                featurePaint.strokeWidth = 6f
                canvas.drawLine(leftEyeX - eyeBaseRadius, eyesY - eyeBaseRadius * 1.2f, leftEyeX + eyeBaseRadius * 0.8f, eyesY - eyeBaseRadius * 0.6f, featurePaint)
                canvas.drawLine(rightEyeX + eyeBaseRadius, eyesY - eyeBaseRadius * 1.2f, rightEyeX - eyeBaseRadius * 0.8f, eyesY - eyeBaseRadius * 0.6f, featurePaint)
            }

            Expression.HEART_EYES -> {
                // Big red heart pupils
                drawHeartEye(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale)
                drawHeartEye(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale)

                // Smile
                featurePaint.strokeWidth = 5f
                val mouthBox = RectF(faceCenterX - baseRadius * 0.2f, faceCenterY + baseRadius * 0.15f, faceCenterX + baseRadius * 0.2f, faceCenterY + baseRadius * 0.4f)
                canvas.drawArc(mouthBox, 20f, 140f, false, featurePaint)
            }

            Expression.CRYING -> {
                drawStandard3DEye(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale, pupilDx, pupilDy)
                drawStandard3DEye(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale, pupilDx, pupilDy)

                // Blue tear streams
                drawTear(canvas, leftEyeX, eyesY + eyeBaseRadius * 0.8f, baseRadius * 0.15f)
                drawTear(canvas, rightEyeX, eyesY + eyeBaseRadius * 0.8f, baseRadius * 0.15f)
            }

            else -> {
                // NORMAL EXPRESSION: Big round cartoon white eyes with dark pupils & double glint reflection dots
                if (leftEyePerspectiveScale > 0.35f) {
                    drawStandard3DEye(canvas, leftEyeX, eyesY, eyeBaseRadius * leftEyePerspectiveScale, pupilDx, pupilDy)
                }
                if (rightEyePerspectiveScale > 0.35f) {
                    drawStandard3DEye(canvas, rightEyeX, eyesY, eyeBaseRadius * rightEyePerspectiveScale, pupilDx, pupilDy)
                }
            }
        }

        canvas.restore()
    }

    private fun drawStandard3DEye(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        pDx: Float,
        pDy: Float
    ) {
        if (r <= 2f) return

        // 1. Sclera (Eye White)
        canvas.drawCircle(cx, cy, r, eyeWhitePaint)
        canvas.drawCircle(cx, cy, r, eyeBorderPaint)

        // 2. Pupil (Dark black center)
        val maxShift = r * 0.42f
        val px = cx + (pDx * maxShift)
        val py = cy + (pDy * maxShift)
        val pupilR = r * 0.58f

        canvas.drawCircle(px, py, pupilR, pupilPaint)

        // 3. Primary Top-Left Glint Reflection Dot
        val glint1R = pupilR * 0.38f
        canvas.drawCircle(px - pupilR * 0.32f, py - pupilR * 0.32f, glint1R, glintMainPaint)

        // 4. Secondary Bottom-Right Subtle Glint Reflection Dot
        val glint2R = pupilR * 0.22f
        canvas.drawCircle(px + pupilR * 0.35f, py + pupilR * 0.35f, glint2R, glintMainPaint)
    }

    private fun drawCurvedEyeArc(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        val arcBox = RectF(cx - r, cy - r * 0.6f, cx + r, cy + r * 0.6f)
        canvas.drawArc(arcBox, 200f, 140f, false, featurePaint)
    }

    private fun drawAngleEye(canvas: Canvas, cx: Float, cy: Float, r: Float, isLeft: Boolean) {
        if (isLeft) {
            canvas.drawLine(cx - r, cy - r * 0.5f, cx, cy, featurePaint)
            canvas.drawLine(cx - r, cy + r * 0.5f, cx, cy, featurePaint)
        } else {
            canvas.drawLine(cx + r, cy - r * 0.5f, cx, cy, featurePaint)
            canvas.drawLine(cx + r, cy + r * 0.5f, cx, cy, featurePaint)
        }
    }

    private fun drawHeartEye(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        canvas.drawCircle(cx, cy, r, eyeWhitePaint)
        canvas.drawCircle(cx, cy, r, eyeBorderPaint)

        val hr = r * 0.65f
        heartPath.reset()
        heartPath.moveTo(cx, cy + hr * 0.6f)
        heartPath.cubicTo(
            cx - hr, cy - hr * 0.2f,
            cx - hr * 0.5f, cy - hr,
            cx, cy - hr * 0.3f
        )
        heartPath.cubicTo(
            cx + hr * 0.5f, cy - hr,
            cx + hr, cy - hr * 0.2f,
            cx, cy + hr * 0.6f
        )
        canvas.drawPath(heartPath, heartPaint)
    }

    private fun drawTear(canvas: Canvas, cx: Float, cy: Float, tearSize: Float) {
        tearPath.reset()
        tearPath.moveTo(cx, cy)
        tearPath.quadTo(cx - tearSize, cy + tearSize * 1.5f, cx, cy + tearSize * 2.5f)
        tearPath.quadTo(cx + tearSize, cy + tearSize * 1.5f, cx, cy)
        canvas.drawPath(tearPath, tearPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        breathAnimator?.cancel()
        turnAnimator?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
    }
}
