package com.alineacion.ruedas.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Motor de calibración para establecer referencias precisas
 * antes de realizar mediciones de alineación
 */
class CalibrationEngine {
    
    // Estados de calibración
    private val _calibrationState = MutableStateFlow(CalibrationState.NOT_STARTED)
    val calibrationState: StateFlow<CalibrationState> = _calibrationState.asStateFlow()
    
    private val _calibrationProgress = MutableStateFlow(0f)
    val calibrationProgress: StateFlow<Float> = _calibrationProgress.asStateFlow()
    
    private val _calibrationMessage = MutableStateFlow("")
    val calibrationMessage: StateFlow<String> = _calibrationMessage.asStateFlow()
    
    // Valores de calibración
    private var accelerometerOffset = FloatArray(3)
    private var gyroscopeOffset = FloatArray(3)
    private var surfaceReference = FloatArray(3)
    
    // Referencias para validación
    private val calibrationSamples = mutableListOf<FloatArray>()
    private var samplesCollected = 0
    private val requiredSamples = 50
    
    /**
     * Inicia el proceso de calibración
     */
    suspend fun startCalibration(sensorManager: WheelAlignmentSensorManager) {
        _calibrationState.value = CalibrationState.CHECKING_SENSORS
        _calibrationMessage.value = "Verificando sensores disponibles..."
        _calibrationProgress.value = 0.1f
        
        val availability = sensorManager.areSensorsAvailable()
        if (!availability.isFullyCompatible) {
            _calibrationState.value = CalibrationState.ERROR
            _calibrationMessage.value = "Sensores requeridos no disponibles"
            return
        }
        
        // Fase 1: Calibración de superficie de referencia
        calibrateSurfaceReference(sensorManager)
        
        // Fase 2: Calibración de offset de sensores
        calibrateSensorOffsets(sensorManager)
        
        // Fase 3: Validación
        validateCalibration(sensorManager)
    }
    
    /**
     * Calibra la superficie de referencia
     */
    private suspend fun calibrateSurfaceReference(sensorManager: WheelAlignmentSensorManager) {
        _calibrationState.value = CalibrationState.SURFACE_REFERENCE
        _calibrationMessage.value = "Coloque el dispositivo en superficie nivelada y mantenga estable"
        _calibrationProgress.value = 0.2f
        
        calibrationSamples.clear()
        samplesCollected = 0
        
        // Colectar muestras de la superficie de referencia
        sensorManager.accelerometerData.collect { accelData ->
            if (sensorManager.isStable.value && samplesCollected < requiredSamples) {
                calibrationSamples.add(accelData.copyOf())
                samplesCollected++
                
                val progress = 0.2f + (samplesCollected.toFloat() / requiredSamples) * 0.3f
                _calibrationProgress.value = progress
                
                if (samplesCollected >= requiredSamples) {
                    // Calcular promedio para referencia de superficie
                    surfaceReference = calculateAverage(calibrationSamples)
                    return@collect
                }
            }
        }
    }
    
    /**
     * Calibra los offsets de los sensores
     */
    private suspend fun calibrateSensorOffsets(sensorManager: WheelAlignmentSensorManager) {
        _calibrationState.value = CalibrationState.SENSOR_OFFSET
        _calibrationMessage.value = "Calibrando sensores..."
        _calibrationProgress.value = 0.5f
        
        // Calibrar offset del acelerómetro
        sensorManager.accelerometerData.collect { accelData ->
            if (sensorManager.isStable.value) {
                accelerometerOffset = floatArrayOf(
                    accelData[0] - surfaceReference[0],
                    accelData[1] - surfaceReference[1],
                    accelData[2] - surfaceReference[2]
                )
                return@collect
            }
        }
        
        // Calibrar offset del giroscopio (debe ser cerca de cero cuando está estable)
        sensorManager.gyroscopeData.collect { gyroData ->
            if (sensorManager.isStable.value) {
                gyroscopeOffset = gyroData.copyOf()
                return@collect
            }
        }
        
        _calibrationProgress.value = 0.8f
    }
    
    /**
     * Valida la calibración realizada
     */
    private suspend fun validateCalibration(sensorManager: WheelAlignmentSensorManager) {
        _calibrationState.value = CalibrationState.VALIDATION
        _calibrationMessage.value = "Validando calibración..."
        _calibrationProgress.value = 0.9f
        
        // Realizar medición de prueba y verificar consistencia
        val testSamples = mutableListOf<FloatArray>()
        var testSamplesCollected = 0
        val testSamplesRequired = 10
        
        sensorManager.accelerometerData.collect { accelData ->
            if (sensorManager.isStable.value && testSamplesCollected < testSamplesRequired) {
                val correctedData = floatArrayOf(
                    accelData[0] - accelerometerOffset[0],
                    accelData[1] - accelerometerOffset[1],
                    accelData[2] - accelerometerOffset[2]
                )
                testSamples.add(correctedData)
                testSamplesCollected++
                
                if (testSamplesCollected >= testSamplesRequired) {
                    val variance = calculateVariance(testSamples)
                    
                    if (variance < 0.05) { // Umbral de aceptación
                        _calibrationState.value = CalibrationState.COMPLETED
                        _calibrationMessage.value = "Calibración completada exitosamente"
                        _calibrationProgress.value = 1.0f
                    } else {
                        _calibrationState.value = CalibrationState.ERROR
                        _calibrationMessage.value = "Calibración falló - Demasiada variación en mediciones"
                    }
                    return@collect
                }
            }
        }
    }
    
    /**
     * Aplica la calibración a datos de acelerómetro
     */
    fun applyCalibratedAccelerometer(rawData: FloatArray): FloatArray {
        return floatArrayOf(
            rawData[0] - accelerometerOffset[0],
            rawData[1] - accelerometerOffset[1],
            rawData[2] - accelerometerOffset[2]
        )
    }
    
    /**
     * Aplica la calibración a datos de giroscopio
     */
    fun applyCalibratedGyroscope(rawData: FloatArray): FloatArray {
        return floatArrayOf(
            rawData[0] - gyroscopeOffset[0],
            rawData[1] - gyroscopeOffset[1],
            rawData[2] - gyroscopeOffset[2]
        )
    }
    
    /**
     * Verifica si la calibración está completa y es válida
     */
    fun isCalibrationValid(): Boolean {
        return _calibrationState.value == CalibrationState.COMPLETED
    }
    
    /**
     * Resetea la calibración
     */
    fun resetCalibration() {
        _calibrationState.value = CalibrationState.NOT_STARTED
        _calibrationProgress.value = 0f
        _calibrationMessage.value = ""
        accelerometerOffset.fill(0f)
        gyroscopeOffset.fill(0f)
        surfaceReference.fill(0f)
        calibrationSamples.clear()
        samplesCollected = 0
    }
    
    /**
     * Calcula el promedio de un conjunto de muestras
     */
    private fun calculateAverage(samples: List<FloatArray>): FloatArray {
        val average = FloatArray(3)
        
        for (sample in samples) {
            for (i in sample.indices) {
                average[i] += sample[i]
            }
        }
        
        for (i in average.indices) {
            average[i] /= samples.size
        }
        
        return average
    }
    
    /**
     * Calcula la varianza de un conjunto de muestras
     */
    private fun calculateVariance(samples: List<FloatArray>): Double {
        val average = calculateAverage(samples)
        var variance = 0.0
        
        for (sample in samples) {
            val diff = kotlin.math.sqrt(
                (sample[0] - average[0]) * (sample[0] - average[0]) +
                (sample[1] - average[1]) * (sample[1] - average[1]) +
                (sample[2] - average[2]) * (sample[2] - average[2])
            )
            variance += diff * diff
        }
        
        return variance / samples.size
    }
}

/**
 * Estados posibles del proceso de calibración
 */
enum class CalibrationState {
    NOT_STARTED,
    CHECKING_SENSORS,
    SURFACE_REFERENCE,
    SENSOR_OFFSET,
    VALIDATION,
    COMPLETED,
    ERROR
}
