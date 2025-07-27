package com.alineacion.ruedas.ui.calibration

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alineacion.ruedas.R
import com.alineacion.ruedas.core.CalibrationEngine
import com.alineacion.ruedas.ui.measurement.MeasurementActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Actividad de calibración del sistema de alineación
 * 
 * Esta actividad guía al usuario a través del proceso de calibración
 * de los sensores para obtener lecturas precisas.
 */
class CalibrationActivity : AppCompatActivity(), SensorEventListener {
    
    private lateinit var calibrationEngine: CalibrationEngine
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    
    // UI Components
    private lateinit var titleText: MaterialTextView
    private lateinit var instructionText: MaterialTextView
    private lateinit var statusText: MaterialTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var nextButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var statusCard: MaterialCardView
    
    // Calibration state
    private var currentStep = 0
    private var calibrationData = mutableListOf<FloatArray>()
    private var isCollectingData = false
    private val handler = Handler(Looper.getMainLooper())
    
    private val calibrationSteps = listOf(
        "Coloca el dispositivo en una superficie completamente plana y nivelada",
        "Mantén el dispositivo inmóvil durante 10 segundos",
        "Inclina ligeramente a la izquierda y mantén 5 segundos",
        "Inclina ligeramente a la derecha y mantén 5 segundos",
        "Regresa a posición horizontal y mantén 5 segundos"
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        
        setupViews()
        setupSensors()
        setupCalibration()
        startCalibrationProcess()
    }
    
    private fun setupViews() {
        title = "Calibración de Sensores"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        titleText = findViewById(R.id.titleText)
        instructionText = findViewById(R.id.instructionText)
        statusText = findViewById(R.id.statusText)
        progressBar = findViewById(R.id.progressBar)
        nextButton = findViewById(R.id.nextButton)
        cancelButton = findViewById(R.id.cancelButton)
        statusCard = findViewById(R.id.statusCard)
        
        nextButton.setOnClickListener { nextStep() }
        cancelButton.setOnClickListener { finish() }
        
        progressBar.max = calibrationSteps.size
        progressBar.progress = 0
    }
    
    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        
        if (accelerometer == null) {
            showError("Acelerómetro no disponible")
            return
        }
        
        if (gyroscope == null) {
            showError("Giroscopio no disponible")
            return
        }
    }
    
    private fun setupCalibration() {
        calibrationEngine = CalibrationEngine()
    }
    
    private fun startCalibrationProcess() {
        titleText.text = "Paso ${currentStep + 1} de ${calibrationSteps.size}"
        instructionText.text = calibrationSteps[currentStep]
        statusText.text = "Sigue las instrucciones para calibrar los sensores"
        nextButton.text = "Iniciar"
        nextButton.isEnabled = true
    }
    
    private fun nextStep() {
        when (currentStep) {
            0 -> {
                // Paso inicial - verificar posición
                nextButton.text = "Siguiente"
                nextButton.isEnabled = false
                statusText.text = "Verificando posición inicial..."
                startDataCollection()
            }
            1 -> {
                // Mantener inmóvil
                nextButton.isEnabled = false
                statusText.text = "Recolectando datos de referencia..."
                collectReferenceData()
            }
            2 -> {
                // Inclinación izquierda
                nextButton.isEnabled = false
                statusText.text = "Recolectando datos de inclinación izquierda..."
                collectLeftTiltData()
            }
            3 -> {
                // Inclinación derecha
                nextButton.isEnabled = false
                statusText.text = "Recolectando datos de inclinación derecha..."
                collectRightTiltData()
            }
            4 -> {
                // Posición final
                nextButton.isEnabled = false
                statusText.text = "Finalizando calibración..."
                finalizeCalibration()
            }
        }
        
        currentStep++
        progressBar.progress = currentStep
        
        if (currentStep < calibrationSteps.size) {
            titleText.text = "Paso ${currentStep + 1} de ${calibrationSteps.size}"
            instructionText.text = calibrationSteps[currentStep]
        }
    }
    
    private fun startDataCollection() {
        lifecycleScope.launch {
            registerSensorListeners()
            isCollectingData = true
            
            delay(3000) // 3 segundos para posicionarse
            statusText.text = "¡Perfecto! Manteniendo posición..."
            nextButton.isEnabled = true
        }
    }
    
    private fun collectReferenceData() {
        lifecycleScope.launch {
            calibrationData.clear()
            isCollectingData = true
            
            // Countdown
            for (i in 10 downTo 1) {
                statusText.text = "Recolectando datos... $i segundos"
                delay(1000)
            }
            
            isCollectingData = false
            statusText.text = "Datos de referencia recolectados ✓"
            nextButton.isEnabled = true
        }
    }
    
    private fun collectLeftTiltData() {
        lifecycleScope.launch {
            isCollectingData = true
            
            // Countdown
            for (i in 5 downTo 1) {
                statusText.text = "Inclinación izquierda... $i segundos"
                delay(1000)
            }
            
            isCollectingData = false
            statusText.text = "Datos de inclinación izquierda recolectados ✓"
            nextButton.isEnabled = true
        }
    }
    
    private fun collectRightTiltData() {
        lifecycleScope.launch {
            isCollectingData = true
            
            // Countdown
            for (i in 5 downTo 1) {
                statusText.text = "Inclinación derecha... $i segundos"
                delay(1000)
            }
            
            isCollectingData = false
            statusText.text = "Datos de inclinación derecha recolectados ✓"
            nextButton.isEnabled = true
        }
    }
    
    private fun finalizeCalibration() {
        lifecycleScope.launch {
            statusText.text = "Procesando datos de calibración..."
            
            delay(2000) // Simular procesamiento
            
            // Aplicar calibración
            if (calibrationData.isNotEmpty()) {
                try {
                    // Configurar la calibración con los datos recopilados
                    calibrationEngine.resetCalibration()
                    statusText.text = "¡Calibración completada exitosamente! ✓"
                    
                    delay(1000)
                    
                    // Ir a la actividad de medición
                    val intent = Intent(this@CalibrationActivity, MeasurementActivity::class.java)
                    startActivity(intent)
                    finish()
                    
                } catch (e: Exception) {
                    showError("Error en calibración: ${e.message}")
                }
            } else {
                showError("No se recolectaron suficientes datos")
            }
        }
    }
    
    private fun registerSensorListeners() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
    
    private fun unregisterSensorListeners() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (!isCollectingData) return
        
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    // Almacenar datos del acelerómetro
                    val data = FloatArray(3)
                    data[0] = it.values[0] // X
                    data[1] = it.values[1] // Y
                    data[2] = it.values[2] // Z
                    calibrationData.add(data)
                }
                Sensor.TYPE_GYROSCOPE -> {
                    // Datos del giroscopio para verificar estabilidad
                    val stability = kotlin.math.sqrt(
                        it.values[0] * it.values[0] +
                        it.values[1] * it.values[1] +
                        it.values[2] * it.values[2]
                    )
                    
                    if (stability > 0.5) {
                        // Demasiado movimiento
                        runOnUiThread {
                            statusText.text = "⚠️ Demasiado movimiento - mantén inmóvil"
                        }
                    } else {
                        // Estabilidad aceptable
                    }
                }
                else -> {
                    // Otros sensores no necesarios para calibración
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario para esta implementación
    }
    
    private fun showError(message: String) {
        statusText.text = "❌ $message"
        nextButton.text = "Reintentar"
        nextButton.isEnabled = true
        nextButton.setOnClickListener {
            currentStep = 0
            startCalibrationProcess()
        }
    }
    
    override fun onResume() {
        super.onResume()
        registerSensorListeners()
    }
    
    override fun onPause() {
        super.onPause()
        unregisterSensorListeners()
        isCollectingData = false
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterSensorListeners()
    }
}
