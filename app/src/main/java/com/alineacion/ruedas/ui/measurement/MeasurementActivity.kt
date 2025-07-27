package com.alineacion.ruedas.ui.measurement

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alineacion.ruedas.R
import com.alineacion.ruedas.core.CalibrationEngine
import com.alineacion.ruedas.core.MeasurementProcessor
import com.alineacion.ruedas.core.WheelAlignmentSensorManager
import com.alineacion.ruedas.ui.results.ResultsActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Actividad de medición de alineación de ruedas
 * 
 * Permite al usuario medir los ángulos de alineación (camber, toe, caster)
 * usando los sensores calibrados del dispositivo.
 */
class MeasurementActivity : AppCompatActivity(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private lateinit var measurementProcessor: MeasurementProcessor
    private lateinit var wheelAlignmentSensorManager: WheelAlignmentSensorManager
    
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    
    // UI Components
    private lateinit var wheelSelectionCard: MaterialCardView
    private lateinit var measurementCard: MaterialCardView
    private lateinit var resultsCard: MaterialCardView
    
    private lateinit var wheelTitle: MaterialTextView
    private lateinit var instructionText: MaterialTextView
    private lateinit var statusText: MaterialTextView
    
    private lateinit var camberValue: MaterialTextView
    private lateinit var toeValue: MaterialTextView
    private lateinit var casterValue: MaterialTextView
    
    private lateinit var measureButton: MaterialButton
    private lateinit var nextWheelButton: MaterialButton
    private lateinit var finishButton: MaterialButton
    private lateinit var backButton: MaterialButton
    
    // Measurement state
    private var currentWheel = 0
    private var isMeasuring = false
    private var measurementResults = mutableMapOf<String, Map<String, Float>>()
    
    private val wheels = listOf(
        "Rueda Delantera Izquierda",
        "Rueda Delantera Derecha", 
        "Rueda Trasera Izquierda",
        "Rueda Trasera Derecha"
    )
    
    private val wheelInstructions = listOf(
        "Coloca el dispositivo contra la llanta delantera izquierda, asegurándote de que esté perfectamente vertical y centrado",
        "Coloca el dispositivo contra la llanta delantera derecha, asegurándote de que esté perfectamente vertical y centrado",
        "Coloca el dispositivo contra la llanta trasera izquierda, asegurándote de que esté perfectamente vertical y centrado",
        "Coloca el dispositivo contra la llanta trasera derecha, asegurándote de que esté perfectamente vertical y centrado"
    )
    
    // Current sensor values
    private var currentAccelerometer = FloatArray(3)
    private var currentMagnetometer = FloatArray(3)
    private var isStable = false
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)
        
        setupViews()
        setupSensors()
        setupMeasurement()
        startMeasurementProcess()
    }
    
    private fun setupViews() {
        title = "Medición de Alineación"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        wheelSelectionCard = findViewById(R.id.wheelSelectionCard)
        measurementCard = findViewById(R.id.measurementCard)
        resultsCard = findViewById(R.id.resultsCard)
        
        wheelTitle = findViewById(R.id.wheelTitle)
        instructionText = findViewById(R.id.instructionText)
        statusText = findViewById(R.id.statusText)
        
        camberValue = findViewById(R.id.camberValue)
        toeValue = findViewById(R.id.toeValue)
        casterValue = findViewById(R.id.casterValue)
        
        measureButton = findViewById(R.id.measureButton)
        nextWheelButton = findViewById(R.id.nextWheelButton)
        finishButton = findViewById(R.id.finishButton)
        backButton = findViewById(R.id.backButton)
        
        measureButton.setOnClickListener { startMeasurement() }
        nextWheelButton.setOnClickListener { nextWheel() }
        finishButton.setOnClickListener { finishMeasurement() }
        backButton.setOnClickListener { finish() }
        
        // Initially hide some elements
        measurementCard.visibility = android.view.View.GONE
        resultsCard.visibility = android.view.View.GONE
        nextWheelButton.visibility = android.view.View.GONE
        finishButton.visibility = android.view.View.GONE
    }
    
    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        
        wheelAlignmentSensorManager = WheelAlignmentSensorManager(this)
    }
    
    private fun setupMeasurement() {
        val calibrationEngine = CalibrationEngine()
        measurementProcessor = MeasurementProcessor(calibrationEngine)
    }
    
    private fun startMeasurementProcess() {
        updateWheelDisplay()
        registerSensorListeners()
    }
    
    private fun updateWheelDisplay() {
        wheelTitle.text = "${currentWheel + 1}/4 - ${wheels[currentWheel]}"
        instructionText.text = wheelInstructions[currentWheel]
        statusText.text = "Posiciona el dispositivo según las instrucciones"
        
        // Reset measurement display
        camberValue.text = "-- °"
        toeValue.text = "-- °"
        casterValue.text = "-- °"
        
        measureButton.isEnabled = true
        measureButton.text = "Iniciar Medición"
    }
    
    private fun startMeasurement() {
        if (isMeasuring) return
        
        isMeasuring = true
        measureButton.isEnabled = false
        measureButton.text = "Midiendo..."
        
        measurementCard.visibility = android.view.View.VISIBLE
        
        lifecycleScope.launch {
            statusText.text = "Preparando medición..."
            delay(1000)
            
            statusText.text = "Mantén el dispositivo inmóvil y estable"
            delay(2000)
            
            if (waitForStability()) {
                performMeasurement()
            } else {
                showError("No se pudo estabilizar la medición. Inténtalo de nuevo.")
            }
        }
    }
    
    private suspend fun waitForStability(): Boolean {
        var stableCount = 0
        val requiredStableCount = 10 // 1 segundo a 10Hz
        
        for (i in 1..50) { // 5 segundos máximo
            delay(100)
            
            if (isStable) {
                stableCount++
                statusText.text = "Estabilizando... ${stableCount * 10}%"
                
                if (stableCount >= requiredStableCount) {
                    return true
                }
            } else {
                stableCount = 0
                statusText.text = "Esperando estabilidad..."
            }
        }
        
        return false
    }
    
    private suspend fun performMeasurement() {
        statusText.text = "Realizando medición..."
        
        val measurements = mutableListOf<FloatArray>()
        
        // Recolectar datos durante 3 segundos
        for (i in 1..30) {
            delay(100)
            measurements.add(currentAccelerometer.clone())
            
            val progress = (i * 100) / 30
            statusText.text = "Midiendo... $progress%"
        }
        
        // Procesar mediciones
        val results = processMeasurements(measurements)
        
        // Mostrar resultados
        displayResults(results)
        
        // Guardar resultados
        measurementResults[wheels[currentWheel]] = results
        
        isMeasuring = false
        measureButton.text = "Medir Nuevamente"
        measureButton.isEnabled = true
        
        resultsCard.visibility = android.view.View.VISIBLE
        
        if (currentWheel < wheels.size - 1) {
            nextWheelButton.visibility = android.view.View.VISIBLE
        } else {
            finishButton.visibility = android.view.View.VISIBLE
        }
        
        statusText.text = "Medición completada ✓"
    }
    
    private fun processMeasurements(measurements: List<FloatArray>): Map<String, Float> {
        // Promediar las mediciones
        val avgX = measurements.map { it[0] }.average().toFloat()
        val avgY = measurements.map { it[1] }.average().toFloat()
        val avgZ = measurements.map { it[2] }.average().toFloat()
        
        // Calcular ángulos
        val camber = calculateCamber(avgX, avgY, avgZ)
        val toe = calculateToe(currentMagnetometer)
        val caster = calculateCaster(avgX, avgY, avgZ)
        
        return mapOf(
            "camber" to camber,
            "toe" to toe,
            "caster" to caster
        )
    }
    
    private fun calculateCamber(x: Float, y: Float, z: Float): Float {
        // Camber es la inclinación lateral de la rueda
        return Math.toDegrees(atan2(x.toDouble(), sqrt((y*y + z*z).toDouble()))).toFloat()
    }
    
    private fun calculateToe(magnetometer: FloatArray): Float {
        // Toe es la orientación hacia adentro/afuera
        // Simplificado - en un caso real necesitaríamos la orientación del vehículo
        return Math.toDegrees(atan2(magnetometer[0].toDouble(), magnetometer[1].toDouble())).toFloat()
    }
    
    private fun calculateCaster(x: Float, y: Float, z: Float): Float {
        // Caster es la inclinación hacia adelante/atrás
        return Math.toDegrees(atan2(y.toDouble(), sqrt((x*x + z*z).toDouble()))).toFloat()
    }
    
    private fun displayResults(results: Map<String, Float>) {
        camberValue.text = "${String.format("%.2f", results["camber"])}°"
        toeValue.text = "${String.format("%.2f", results["toe"])}°"
        casterValue.text = "${String.format("%.2f", results["caster"])}°"
        
        // Colorear según valores normales
        updateValueColor(camberValue, results["camber"] ?: 0f, -2f, 2f)
        updateValueColor(toeValue, results["toe"] ?: 0f, -0.5f, 0.5f)
        updateValueColor(casterValue, results["caster"] ?: 0f, 1f, 7f)
    }
    
    private fun updateValueColor(textView: MaterialTextView, value: Float, minNormal: Float, maxNormal: Float) {
        val color = if (value in minNormal..maxNormal) {
            getColor(android.R.color.holo_green_dark)
        } else {
            getColor(android.R.color.holo_red_dark)
        }
        textView.setTextColor(color)
    }
    
    private fun nextWheel() {
        currentWheel++
        nextWheelButton.visibility = android.view.View.GONE
        measurementCard.visibility = android.view.View.GONE
        resultsCard.visibility = android.view.View.GONE
        updateWheelDisplay()
    }
    
    private fun finishMeasurement() {
        val intent = Intent(this, ResultsActivity::class.java)
        
        // Pasar resultados a la actividad de resultados
        val resultsBundle = Bundle()
        measurementResults.forEach { (wheel, measurements) ->
            resultsBundle.putFloat("${wheel}_camber", measurements["camber"] ?: 0f)
            resultsBundle.putFloat("${wheel}_toe", measurements["toe"] ?: 0f)
            resultsBundle.putFloat("${wheel}_caster", measurements["caster"] ?: 0f)
        }
        intent.putExtra("measurements", resultsBundle)
        
        startActivity(intent)
        finish()
    }
    
    private fun showError(message: String) {
        statusText.text = "❌ $message"
        isMeasuring = false
        measureButton.isEnabled = true
        measureButton.text = "Reintentar"
    }
    
    private fun registerSensorListeners() {
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
    
    private fun unregisterSensorListeners() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    currentAccelerometer[0] = it.values[0]
                    currentAccelerometer[1] = it.values[1]
                    currentAccelerometer[2] = it.values[2]
                    
                    // Verificar estabilidad
                    checkStability()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    currentMagnetometer[0] = it.values[0]
                    currentMagnetometer[1] = it.values[1]
                    currentMagnetometer[2] = it.values[2]
                }
            }
        }
    }
    
    private fun checkStability() {
        // Calcular variación total
        val totalVariation = sqrt(
            currentAccelerometer[0] * currentAccelerometer[0] +
            currentAccelerometer[1] * currentAccelerometer[1] +
            currentAccelerometer[2] * currentAccelerometer[2]
        )
        
        // Considerar estable si la variación está cerca de 9.8 (gravedad)
        isStable = abs(totalVariation - 9.8f) < 0.5f
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario para esta implementación
    }
    
    override fun onResume() {
        super.onResume()
        registerSensorListeners()
    }
    
    override fun onPause() {
        super.onPause()
        unregisterSensorListeners()
        isMeasuring = false
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
