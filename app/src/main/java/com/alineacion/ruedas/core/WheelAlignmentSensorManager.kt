package com.alineacion.ruedas.core

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor principal de sensores del dispositivo
 * Maneja acelerómetro, giroscopio y magnetómetro para mediciones de alineación
 */
class WheelAlignmentSensorManager(private val context: Context) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Sensores principales
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    
    // Estados de los sensores
    private val _accelerometerData = MutableStateFlow(FloatArray(3))
    val accelerometerData: StateFlow<FloatArray> = _accelerometerData.asStateFlow()
    
    private val _gyroscopeData = MutableStateFlow(FloatArray(3))
    val gyroscopeData: StateFlow<FloatArray> = _gyroscopeData.asStateFlow()
    
    private val _magnetometerData = MutableStateFlow(FloatArray(3))
    val magnetometerData: StateFlow<FloatArray> = _magnetometerData.asStateFlow()
    
    private val _isStable = MutableStateFlow(false)
    val isStable: StateFlow<Boolean> = _isStable.asStateFlow()
    
    // Variables para filtrado y estabilización
    private var lastAccelTime = 0L
    private var lastGyroTime = 0L
    private val stabilityThreshold = 0.1f
    private val stabilityTimeWindow = 1000L // 1 segundo
    private var lastStableTime = 0L
    
    // Arrays para filtrado de ruido
    private val accelFilteredData = FloatArray(3)
    private val gyroFilteredData = FloatArray(3)
    private val magnetFilteredData = FloatArray(3)
    
    // Filtro pasa-bajas simple
    private val alpha = 0.8f
    
    /**
     * Verifica si los sensores necesarios están disponibles
     */
    fun areSensorsAvailable(): SensorAvailability {
        return SensorAvailability(
            hasAccelerometer = accelerometer != null,
            hasGyroscope = gyroscope != null,
            hasMagnetometer = magnetometer != null
        )
    }
    
    /**
     * Inicia la lectura de sensores
     */
    fun startSensorReading() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
    
    /**
     * Detiene la lectura de sensores
     */
    fun stopSensorReading() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    processAccelerometerData(sensorEvent.values)
                    lastAccelTime = System.currentTimeMillis()
                }
                Sensor.TYPE_GYROSCOPE -> {
                    processGyroscopeData(sensorEvent.values)
                    lastGyroTime = System.currentTimeMillis()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    processMagnetometerData(sensorEvent.values)
                }
            }
            
            updateStabilityStatus()
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Manejar cambios en la precisión del sensor si es necesario
    }
    
    /**
     * Procesa datos del acelerómetro con filtrado
     */
    private fun processAccelerometerData(values: FloatArray) {
        for (i in values.indices) {
            accelFilteredData[i] = alpha * accelFilteredData[i] + (1 - alpha) * values[i]
        }
        _accelerometerData.value = accelFilteredData.copyOf()
    }
    
    /**
     * Procesa datos del giroscopio con filtrado
     */
    private fun processGyroscopeData(values: FloatArray) {
        for (i in values.indices) {
            gyroFilteredData[i] = alpha * gyroFilteredData[i] + (1 - alpha) * values[i]
        }
        _gyroscopeData.value = gyroFilteredData.copyOf()
    }
    
    /**
     * Procesa datos del magnetómetro con filtrado
     */
    private fun processMagnetometerData(values: FloatArray) {
        for (i in values.indices) {
            magnetFilteredData[i] = alpha * magnetFilteredData[i] + (1 - alpha) * values[i]
        }
        _magnetometerData.value = magnetFilteredData.copyOf()
    }
    
    /**
     * Actualiza el estado de estabilidad del dispositivo
     */
    private fun updateStabilityStatus() {
        val currentTime = System.currentTimeMillis()
        
        // Verificar si el giroscopio indica movimiento mínimo
        val gyroMagnitude = kotlin.math.sqrt(
            gyroFilteredData[0] * gyroFilteredData[0] +
            gyroFilteredData[1] * gyroFilteredData[1] +
            gyroFilteredData[2] * gyroFilteredData[2]
        )
        
        val isCurrentlyStable = gyroMagnitude < stabilityThreshold
        
        if (isCurrentlyStable) {
            if (lastStableTime == 0L) {
                lastStableTime = currentTime
            } else if (currentTime - lastStableTime >= stabilityTimeWindow) {
                _isStable.value = true
            }
        } else {
            lastStableTime = 0L
            _isStable.value = false
        }
    }
    
    /**
     * Calcula el ángulo de inclinación (camber) basado en el acelerómetro
     */
    fun calculateCamberAngle(): Double {
        val accelData = _accelerometerData.value
        
        // Calcular ángulo de inclinación lateral (camber)
        // Usando atan2 para obtener el ángulo en radianes y convertir a grados
        val angleRadians = kotlin.math.atan2(accelData[0].toDouble(), accelData[2].toDouble())
        return Math.toDegrees(angleRadians)
    }
    
    /**
     * Calcula la orientación para medición de toe
     */
    fun calculateToeOrientation(): Double {
        val accelData = _accelerometerData.value
        val magnetData = _magnetometerData.value
        
        // Calcular la orientación horizontal usando acelerómetro y magnetómetro
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        
        SensorManager.getRotationMatrix(rotationMatrix, null, accelData, magnetData)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        
        // Devolver el azimuth en grados
        return Math.toDegrees(orientationAngles[0].toDouble())
    }
}

/**
 * Data class para información de disponibilidad de sensores
 */
data class SensorAvailability(
    val hasAccelerometer: Boolean,
    val hasGyroscope: Boolean,
    val hasMagnetometer: Boolean
) {
    val isFullyCompatible: Boolean
        get() = hasAccelerometer && hasGyroscope
    
    val canMeasureToe: Boolean
        get() = hasAccelerometer && hasMagnetometer
}
