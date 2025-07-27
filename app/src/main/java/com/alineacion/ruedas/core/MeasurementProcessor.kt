package com.alineacion.ruedas.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

/**
 * Procesador de mediciones para calcular ángulos de alineación de ruedas
 * Maneja cálculos de camber, toe y caster con precisión
 */
class MeasurementProcessor(
    private val calibrationEngine: CalibrationEngine
) {
    
    // Estados de medición
    private val _measurementState = MutableStateFlow(MeasurementState.IDLE)
    val measurementState: StateFlow<MeasurementState> = _measurementState.asStateFlow()
    
    private val _currentMeasurement = MutableStateFlow<WheelMeasurement?>(null)
    val currentMeasurement: StateFlow<WheelMeasurement?> = _currentMeasurement.asStateFlow()
    
    // Filtro de Kalman simplificado para estabilización
    private val kalmanFilter = SimpleKalmanFilter()
    
    // Muestras para promediado
    private val measurementSamples = mutableListOf<MeasurementSample>()
    private val maxSamples = 30
    
    /**
     * Inicia una nueva medición
     */
    fun startMeasurement(wheelPosition: WheelPosition, measurementType: MeasurementType) {
        if (!calibrationEngine.isCalibrationValid()) {
            _measurementState.value = MeasurementState.ERROR
            return
        }
        
        _measurementState.value = MeasurementState.MEASURING
        measurementSamples.clear()
        kalmanFilter.reset()
    }
    
    /**
     * Procesa datos de sensores y actualiza la medición actual
     */
    fun processSensorData(
        accelerometerData: FloatArray,
        gyroscopeData: FloatArray,
        magnetometerData: FloatArray,
        isStable: Boolean
    ) {
        if (_measurementState.value != MeasurementState.MEASURING) return
        
        // Aplicar calibración
        val calibratedAccel = calibrationEngine.applyCalibratedAccelerometer(accelerometerData)
        val calibratedGyro = calibrationEngine.applyCalibratedGyroscope(gyroscopeData)
        
        // Solo procesar si el dispositivo está estable
        if (isStable) {
            val sample = MeasurementSample(
                timestamp = System.currentTimeMillis(),
                accelerometer = calibratedAccel,
                gyroscope = calibratedGyro,
                magnetometer = magnetometerData
            )
            
            addMeasurementSample(sample)
        }
    }
    
    /**
     * Añade una muestra de medición y actualiza los cálculos
     */
    private fun addMeasurementSample(sample: MeasurementSample) {
        measurementSamples.add(sample)
        
        // Mantener solo las últimas muestras
        if (measurementSamples.size > maxSamples) {
            measurementSamples.removeAt(0)
        }
        
        // Calcular mediciones si tenemos suficientes muestras
        if (measurementSamples.size >= 5) {
            updateCurrentMeasurement()
        }
    }
    
    /**
     * Actualiza la medición actual basada en las muestras recolectadas
     */
    private fun updateCurrentMeasurement() {
        val camberAngle = calculateCamberAngle()
        val toeAngle = calculateToeAngle()
        
        val filteredCamber = kalmanFilter.update(camberAngle)
        
        _currentMeasurement.value = WheelMeasurement(
            camber = filteredCamber,
            toe = toeAngle,
            caster = null, // Se calcula por separado
            confidence = calculateConfidence(),
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * Calcula el ángulo de camber basado en el acelerómetro
     */
    private fun calculateCamberAngle(): Double {
        val recentSamples = measurementSamples.takeLast(10)
        val angles = recentSamples.map { sample ->
            // Calcular ángulo de inclinación lateral (camber)
            atan2(sample.accelerometer[0].toDouble(), sample.accelerometer[2].toDouble())
        }
        
        val averageAngle = angles.average()
        return Math.toDegrees(averageAngle)
    }
    
    /**
     * Calcula el ángulo de toe basado en magnetómetro y acelerómetro
     */
    private fun calculateToeAngle(): Double {
        val recentSamples = measurementSamples.takeLast(10)
        val orientations = recentSamples.mapNotNull { sample ->
            calculateOrientation(sample.accelerometer, sample.magnetometer)
        }
        
        return if (orientations.isNotEmpty()) {
            orientations.average()
        } else {
            0.0
        }
    }
    
    /**
     * Calcula la orientación usando matriz de rotación
     */
    private fun calculateOrientation(accel: FloatArray, magnet: FloatArray): Double? {
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        
        val success = android.hardware.SensorManager.getRotationMatrix(
            rotationMatrix, null, accel, magnet
        )
        
        return if (success) {
            android.hardware.SensorManager.getOrientation(rotationMatrix, orientationAngles)
            Math.toDegrees(orientationAngles[0].toDouble())
        } else {
            null
        }
    }
    
    /**
     * Calcula el nivel de confianza de la medición
     */
    private fun calculateConfidence(): Double {
        if (measurementSamples.size < 5) return 0.0
        
        val recentSamples = measurementSamples.takeLast(10)
        val camberAngles = recentSamples.map { sample ->
            atan2(sample.accelerometer[0].toDouble(), sample.accelerometer[2].toDouble())
        }
        
        // Calcular varianza de las mediciones
        val mean = camberAngles.average()
        val variance = camberAngles.map { (it - mean).pow(2) }.average()
        val standardDeviation = sqrt(variance)
        
        // Convertir desviación estándar a nivel de confianza (0-1)
        val maxAcceptableDeviation = 0.017 // ~1 grado en radianes
        val confidence = maxOf(0.0, 1.0 - (standardDeviation / maxAcceptableDeviation))
        
        return minOf(1.0, confidence)
    }
    
    /**
     * Completa la medición actual
     */
    fun completeMeasurement(): WheelMeasurement? {
        val measurement = _currentMeasurement.value
        _measurementState.value = MeasurementState.COMPLETED
        return measurement
    }
    
    /**
     * Cancela la medición actual
     */
    fun cancelMeasurement() {
        _measurementState.value = MeasurementState.IDLE
        _currentMeasurement.value = null
        measurementSamples.clear()
    }
    
    /**
     * Calcula el caster usando el método de giro de dirección
     */
    suspend fun calculateCaster(
        centerMeasurement: WheelMeasurement,
        leftTurnMeasurement: WheelMeasurement,
        rightTurnMeasurement: WheelMeasurement,
        turnAngle: Double = 20.0
    ): Double {
        // Fórmula para caster: diferencia de camber dividida por el ángulo de giro
        val camberDifference = leftTurnMeasurement.camber - rightTurnMeasurement.camber
        val casterAngle = camberDifference / (2 * sin(Math.toRadians(turnAngle)))
        
        return Math.toDegrees(asin(casterAngle))
    }
}

/**
 * Estados posibles del procesador de mediciones
 */
enum class MeasurementState {
    IDLE,
    MEASURING,
    COMPLETED,
    ERROR
}

/**
 * Tipos de medición disponibles
 */
enum class MeasurementType {
    CAMBER,
    TOE,
    CASTER
}

/**
 * Posiciones de rueda
 */
enum class WheelPosition {
    FRONT_LEFT,
    FRONT_RIGHT,
    REAR_LEFT,
    REAR_RIGHT
}

/**
 * Data class para una muestra de medición
 */
data class MeasurementSample(
    val timestamp: Long,
    val accelerometer: FloatArray,
    val gyroscope: FloatArray,
    val magnetometer: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as MeasurementSample
        
        if (timestamp != other.timestamp) return false
        if (!accelerometer.contentEquals(other.accelerometer)) return false
        if (!gyroscope.contentEquals(other.gyroscope)) return false
        if (!magnetometer.contentEquals(other.magnetometer)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + accelerometer.contentHashCode()
        result = 31 * result + gyroscope.contentHashCode()
        result = 31 * result + magnetometer.contentHashCode()
        return result
    }
}

/**
 * Data class para medición de una rueda
 */
data class WheelMeasurement(
    val camber: Double,
    val toe: Double,
    val caster: Double?,
    val confidence: Double,
    val timestamp: Long
)

/**
 * Filtro de Kalman simplificado para suavizar mediciones
 */
class SimpleKalmanFilter {
    private var estimate = 0.0
    private var errorCovarianceEstimate = 1.0
    private val processNoise = 0.01
    private val measurementNoise = 0.1
    
    fun update(measurement: Double): Double {
        // Predicción
        val errorCovariancePrediction = errorCovarianceEstimate + processNoise
        
        // Actualización
        val kalmanGain = errorCovariancePrediction / (errorCovariancePrediction + measurementNoise)
        estimate += kalmanGain * (measurement - estimate)
        errorCovarianceEstimate = (1 - kalmanGain) * errorCovariancePrediction
        
        return estimate
    }
    
    fun reset() {
        estimate = 0.0
        errorCovarianceEstimate = 1.0
    }
}
