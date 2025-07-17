package com.example.project_procast.IsolatedFeatureTest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class PostureDetector(private val context: Context) : SensorEventListener {
    enum class PostureState { STANDING, SITTING, LYING_DOWN, WALKING, UNKNOWN }

    data class PostureData(
        val posture: PostureState,
        val confidence: Float,
        val tiltAngle: Float,
        val movement: Float,
        val timestamp: Long
    )

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private val _currentPosture = MutableLiveData<PostureState>(PostureState.UNKNOWN)
    val currentPosture: LiveData<PostureState> = _currentPosture

    private val _postureData = MutableLiveData<PostureData>()
    val postureData: LiveData<PostureData> = _postureData

    private var lastAccel = FloatArray(3)
    private var lastGyro = FloatArray(3)
    private var lastTimestamp = 0L

    fun areSensorsAvailable(): Boolean {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        return accelerometer != null && gyroscope != null
    }

    fun getSensorInfo(): String {
        val acc = accelerometer?.name ?: "Not available"
        val gyro = gyroscope?.name ?: "Not available"
        return "Accelerometer: $acc\nGyroscope: $gyro"
    }

    fun startDetection() {
        if (areSensorsAvailable()) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopDetection() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val now = System.currentTimeMillis()
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                lastAccel = event.values.clone()
            }
            Sensor.TYPE_GYROSCOPE -> {
                lastGyro = event.values.clone()
            }
        }
        // Only process if both sensors have data
        if (lastAccel.any { it != 0f } && lastGyro.any { it != 0f }) {
            val tiltAngle = getTiltAngle(lastAccel)
            val movement = sqrt(lastGyro[0]*lastGyro[0] + lastGyro[1]*lastGyro[1] + lastGyro[2]*lastGyro[2])
            val posture = inferPosture(tiltAngle, movement)
            val confidence = getConfidence(tiltAngle, movement, posture)
            _currentPosture.value = posture
            _postureData.value = PostureData(posture, confidence, tiltAngle, movement, now)
            lastTimestamp = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun getTiltAngle(accel: FloatArray): Float {
        // Angle between gravity vector and device z-axis
        val gX = accel[0]
        val gY = accel[1]
        val gZ = accel[2]
        val norm = sqrt(gX * gX + gY * gY + gZ * gZ)
        return Math.toDegrees(atan2(gZ.toDouble(), sqrt(gX * gX + gY * gY).toDouble())).toFloat()
    }

    private fun inferPosture(tilt: Float, movement: Float): PostureState {
        return when {
            movement > 1.5f -> PostureState.WALKING
            tilt > 60f -> PostureState.LYING_DOWN
            tilt > 30f -> PostureState.SITTING
            tilt > 0f -> PostureState.STANDING
            else -> PostureState.UNKNOWN
        }
    }

    private fun getConfidence(tilt: Float, movement: Float, posture: PostureState): Float {
        return when (posture) {
            PostureState.WALKING -> (movement / 3f).coerceIn(0f, 1f)
            PostureState.LYING_DOWN -> ((tilt - 60f) / 30f).coerceIn(0f, 1f)
            PostureState.SITTING -> ((tilt - 30f) / 30f).coerceIn(0f, 1f)
            PostureState.STANDING -> (tilt / 30f).coerceIn(0f, 1f)
            else -> 0f
        }
    }
} 