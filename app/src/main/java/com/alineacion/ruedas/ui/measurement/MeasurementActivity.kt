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
    
    // New UI components for wheel selection
    private lateinit var select2WheelsButton: MaterialButton
    private lateinit var select4WheelsButton: MaterialButton
    private lateinit var wheelSelectionButtons: List<MaterialButton>
    private lateinit var startMeasurementButton: MaterialButton
    
    // Measurement state
    private var currentWheel = 0
    private var isMeasuring = false
    private var isContinuousMeasuring = false
    private var measurementResults = mutableMapOf<String, Map<String, Float>>()
    private var currentLiveMeasurement = mutableMapOf<String, Float>()
    
    // New workflow variables
    private var selectedWheelsCount = 4 // 2 or 4 wheels
    private var referenceWheelIndex = 0 // Index of reference wheel
    private var selectedWheels = mutableListOf<String>()
    private var isWheelSelectionMode = true
    private var referenceToeMeasurement = 0f // Reference toe value
    
    private val allWheels = listOf(
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
        
        // Verificar si llegamos desde WheelSelectionActivity
        val selectedWheelsArray = intent.getStringArrayExtra("selected_wheels")
        if (selectedWheelsArray != null) {
            // Usar ruedas seleccionadas desde WheelSelectionActivity
            selectedWheels.clear()
            selectedWheels.addAll(selectedWheelsArray)
            selectedWheelsCount = selectedWheels.size
            isWheelSelectionMode = false
            startMeasurementProcess()
        } else {
            // Modo legacy: mostrar selección de ruedas en la misma actividad
            showWheelSelection()
        }
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
    
    private fun showWheelSelection() {
        title = "Selección de Ruedas - AliniaSoon"
        
        wheelTitle.text = "🚗 Selecciona cuántas ruedas medir"
        instructionText.text = "Elige el tipo de medición que deseas realizar según tus necesidades"
        statusText.text = """
            🔸 2 Ruedas: Medición rápida (solo delanteras)
            🔸 4 Ruedas: Medición completa del vehículo
            
            La primera rueda que elijas será la REFERENCIA (TOE = 0°)
        """.trimIndent()
        
        // Hide measurement cards, show selection
        measurementCard.visibility = android.view.View.GONE
        resultsCard.visibility = android.view.View.GONE
        wheelSelectionCard.visibility = android.view.View.VISIBLE
        
        // Setup wheel count selection with visual icons
        measureButton.text = "2 RUEDAS DELANTERAS"
        measureButton.icon = getDrawable(R.drawable.ic_wheels_2)
        measureButton.iconGravity = MaterialButton.ICON_GRAVITY_TOP
        measureButton.iconPadding = 16
        
        nextWheelButton.text = "4 RUEDAS COMPLETAS"
        nextWheelButton.icon = getDrawable(R.drawable.ic_wheels_4)
        nextWheelButton.iconGravity = MaterialButton.ICON_GRAVITY_TOP
        nextWheelButton.iconPadding = 16
        nextWheelButton.visibility = android.view.View.VISIBLE
        
        finishButton.visibility = android.view.View.GONE
        backButton.text = "⬅️ Atrás"
        
        measureButton.setOnClickListener { selectWheelCount(2) }
        nextWheelButton.setOnClickListener { selectWheelCount(4) }
    }
    
    private fun selectWheelCount(count: Int) {
        selectedWheelsCount = count
        selectedWheels.clear()
        
        if (count == 2) {
            selectedWheels.addAll(listOf(
                "Rueda Delantera Izquierda",
                "Rueda Delantera Derecha"
            ))
        } else {
            selectedWheels.addAll(allWheels)
        }
        
        showReferenceWheelSelection()
    }
    
    private fun showReferenceWheelSelection() {
        wheelTitle.text = "🎯 Selecciona la rueda de REFERENCIA"
        instructionText.text = """
            Esta será la primera rueda a medir y definirá el punto de referencia para todas las demás.
            
            ⚠️ IMPORTANTE: La rueda de referencia tendrá TOE = 0° por definición.
            Las demás ruedas mostrarán su convergencia relativa a esta.
        """.trimIndent()
        
        statusText.text = "Selecciona con qué rueda comenzar las mediciones:"
        
        // Show available wheels for selection with better UI
        val wheelButtons = mutableListOf<MaterialButton>()
        
        // Clear previous listeners
        measureButton.setOnClickListener(null)
        nextWheelButton.setOnClickListener(null)
        finishButton.setOnClickListener(null)
        
        if (selectedWheels.size >= 1) {
            measureButton.text = "1️⃣ ${selectedWheels[0]}"
            measureButton.setOnClickListener { selectReferenceWheel(0) }
            wheelButtons.add(measureButton)
        }
        
        if (selectedWheels.size >= 2) {
            nextWheelButton.text = "2️⃣ ${selectedWheels[1]}"
            nextWheelButton.visibility = android.view.View.VISIBLE
            nextWheelButton.setOnClickListener { selectReferenceWheel(1) }
            wheelButtons.add(nextWheelButton)
        }
        
        if (selectedWheels.size >= 3) {
            finishButton.text = "3️⃣ ${selectedWheels[2]}"
            finishButton.visibility = android.view.View.VISIBLE
            finishButton.setOnClickListener { selectReferenceWheel(2) }
            wheelButtons.add(finishButton)
        }
        
        // If we have 4 wheels, we need to show the 4th option differently
        if (selectedWheels.size >= 4) {
            // We'll use the status text to show the 4th option
            statusText.text = statusText.text.toString() + "\n\n4️⃣ ${selectedWheels[3]} - Presiona el botón 'Atrás' temporalmente"
            backButton.text = "4️⃣ ${selectedWheels[3]}"
            backButton.setOnClickListener { selectReferenceWheel(3) }
        } else {
            backButton.text = "⬅️ Volver"
            backButton.setOnClickListener { showWheelSelection() }
        }
    }
    
    private fun selectReferenceWheel(index: Int) {
        referenceWheelIndex = index
        
        // Reorder wheels so reference is first
        val referenceWheel = selectedWheels[index]
        selectedWheels.removeAt(index)
        selectedWheels.add(0, referenceWheel)
        
        // Start measurement process
        currentWheel = 0
        isWheelSelectionMode = false
        startMeasurementProcess()
    }
    
    private fun updateWheelDisplay() {
        if (isWheelSelectionMode) return
        
        val currentWheelName = selectedWheels[currentWheel]
        val isReference = currentWheel == 0
        
        // Enhanced wheel title with progress and reference indicator
        if (isReference) {
            wheelTitle.text = "🎯 ${currentWheel + 1}/${selectedWheels.size} - ${currentWheelName}"
            val titleText = wheelTitle.text.toString() + "\n(RUEDA DE REFERENCIA)"
            wheelTitle.text = titleText
        } else {
            wheelTitle.text = "📐 ${currentWheel + 1}/${selectedWheels.size} - ${currentWheelName}"
        }
        
        val wheelIndex = allWheels.indexOf(currentWheelName)
        instructionText.text = """
            📍 PASO ${currentWheel + 1}:
            
            ${wheelInstructions[wheelIndex]}
            
            ⚠️ Asegúrate de que el dispositivo esté:
            • Perfectamente vertical
            • Centrado en la llanta
            • Firmemente apoyado
        """.trimIndent()
        
        statusText.text = if (isReference) {
            """
            🎯 MIDIENDO RUEDA DE REFERENCIA
            Esta rueda define el punto de referencia (TOE = 0°)
            Las demás ruedas se medirán relativamente a esta
            """.trimIndent()
        } else {
            """
            📐 MIDIENDO RUEDA RELATIVA
            Convergencia se calculará relativamente a la rueda de referencia
            Referencia: ${selectedWheels[0]}
            """.trimIndent()
        }
        
        // Reset measurement display
        camberValue.text = "-- °"
        toeValue.text = "-- °"
        casterValue.text = "No disponible"
        casterValue.setTextColor(getColor(android.R.color.darker_gray))
        
        // Reset measurement state
        isContinuousMeasuring = false
        currentLiveMeasurement.clear()
        
        // Setup buttons with proper styling
        measureButton.isEnabled = true
        measureButton.text = "🚀 INICIAR MEDICIÓN"
        measureButton.icon = getDrawable(android.R.drawable.ic_media_play)
        
        nextWheelButton.visibility = android.view.View.GONE
        finishButton.visibility = android.view.View.GONE
        backButton.text = "⬅️ Volver"
        backButton.setOnClickListener { 
            if (currentWheel > 0) {
                // Go back to previous wheel
                currentWheel--
                updateWheelDisplay()
            } else {
                // Go back to wheel selection
                showWheelSelection()
            }
        }
        
        // Reset button listeners for measurement
        measureButton.setOnClickListener { startMeasurement() }
        nextWheelButton.setOnClickListener { nextWheel() }
        finishButton.setOnClickListener { finishMeasurement() }
        
        // Show measurement and results cards
        wheelSelectionCard.visibility = android.view.View.VISIBLE
        measurementCard.visibility = android.view.View.GONE
        resultsCard.visibility = android.view.View.GONE
    }
    
    private fun startMeasurement() {
        if (isContinuousMeasuring) {
            // Si ya está midiendo continuamente, guardar la medición actual
            saveMeasurement()
            return
        }
        
        // Iniciar medición directa - NO requiere calibración
        // Los sensores del smartphone son suficientemente precisos para alineación básica
        isContinuousMeasuring = true
        measureButton.text = "💾 GUARDAR MEDICIÓN"
        measureButton.icon = getDrawable(android.R.drawable.ic_menu_save)
        
        measurementCard.visibility = android.view.View.VISIBLE
        resultsCard.visibility = android.view.View.VISIBLE
        
        val currentWheelName = selectedWheels[currentWheel]
        val isReference = currentWheel == 0
        
        statusText.text = if (isReference) {
            """
            🔄 MIDIENDO RUEDA DE REFERENCIA EN TIEMPO REAL
            
            Posiciona el dispositivo correctamente y mantén estable.
            Esta medición definirá TOE = 0° para las demás ruedas.
            
            Presiona 'GUARDAR' cuando la medición sea estable.
            """.trimIndent()
        } else {
            """
            🔄 MIDIENDO EN TIEMPO REAL
            
            Posiciona el dispositivo correctamente y mantén estable.
            La convergencia se mostrará relativa a: ${selectedWheels[0]}
            
            Presiona 'GUARDAR' cuando la medición sea estable.
            """.trimIndent()
        }
        statusText.setTextColor(getColor(android.R.color.holo_blue_light))
    }
    
    private fun saveMeasurement() {
        if (!isStable) {
            statusText.text = "⚠️ Estabiliza el dispositivo antes de guardar"
            statusText.setTextColor(getColor(android.R.color.holo_orange_dark))
            return
        }
        
        val currentWheelName = selectedWheels[currentWheel]
        val isReference = currentWheel == 0
        
        // Get current measurements
        val adjustedMeasurement = currentLiveMeasurement.toMutableMap()
        
        if (isReference) {
            // For reference wheel: save raw toe value and set display toe to 0
            referenceToeMeasurement = currentLiveMeasurement["toe"] ?: 0f
            adjustedMeasurement["toe"] = 0f // Reference wheel toe is ALWAYS 0 by definition
            
            statusText.text = "✅ RUEDA DE REFERENCIA guardada (TOE = 0° por definición)"
            statusText.setTextColor(getColor(android.R.color.holo_green_dark))
            
            // Update display to show toe = 0 for reference
            runOnUiThread {
                toeValue.text = "0.00°"
                toeValue.setTextColor(getColor(android.R.color.holo_green_dark))
            }
        } else {
            // For other wheels: calculate relative toe to reference
            val rawToe = currentLiveMeasurement["toe"] ?: 0f
            val relativeToe = rawToe - referenceToeMeasurement
            adjustedMeasurement["toe"] = relativeToe
            
            statusText.text = "✅ Medición guardada (TOE relativo a referencia: ${String.format("%.2f", relativeToe)}°)"
            statusText.setTextColor(getColor(android.R.color.holo_green_dark))
            
            // Update display to show relative toe
            runOnUiThread {
                toeValue.text = "${String.format("%.2f", relativeToe)}°"
                updateValueColor(toeValue, relativeToe, -0.5f, 0.5f)
            }
        }
        
        // Save the adjusted measurement
        measurementResults[currentWheelName] = adjustedMeasurement.toMap()
        
        // Update UI state
        isContinuousMeasuring = false
        measureButton.text = "✅ MEDICIÓN GUARDADA"
        measureButton.isEnabled = false
        
        // Show navigation buttons
        if (currentWheel < selectedWheels.size - 1) {
            nextWheelButton.text = "➡️ SIGUIENTE RUEDA"
            nextWheelButton.visibility = android.view.View.VISIBLE
        } else {
            finishButton.text = "🏁 VER RESULTADOS"
            finishButton.visibility = android.view.View.VISIBLE
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
        
        // Recolectar datos durante 3 segundos con actualización en tiempo real
        for (i in 1..30) {
            delay(100)
            measurements.add(currentAccelerometer.clone())
            
            val progress = (i * 100) / 30
            statusText.text = "Midiendo... $progress%"
            
            // Mostrar mediciones en tiempo real cada 5 iteraciones
            if (i % 5 == 0) {
                val tempResults = processMeasurements(measurements)
                displayLiveResults(tempResults)
            }
        }
        
        // Procesar mediciones finales
        val results = processMeasurements(measurements)
        
        // Mostrar resultados finales
        displayResults(results)
        
        // Guardar resultados - este método ya no se usa con el nuevo workflow
        // measurementResults[selectedWheels[currentWheel]] = results
        
        isMeasuring = false
        measureButton.text = "Medir Nuevamente"
        measureButton.isEnabled = true
        
        resultsCard.visibility = android.view.View.VISIBLE
        
        if (currentWheel < selectedWheels.size - 1) {
            nextWheelButton.visibility = android.view.View.VISIBLE
        } else {
            finishButton.visibility = android.view.View.VISIBLE
        }
        
        statusText.text = "Medición completada ✓"
    }
    
    private fun displayLiveResults(results: Map<String, Float>) {
        // Mostrar solo camber y toe en tiempo real
        runOnUiThread {
            val camber = results["camber"] ?: 0f
            val toe = results["toe"] ?: 0f
            
            resultsCard.visibility = android.view.View.VISIBLE
            camberValue.text = "Camber: ${String.format("%.2f", camber)}°"
            toeValue.text = "Convergencia: ${String.format("%.2f", toe)}°"
            casterValue.text = "Caster: No disponible"
            
            // Colorear según valores normales
            camberValue.setTextColor(getColorForValue(camber, -2f, 2f))
            toeValue.setTextColor(getColorForValue(toe, -0.5f, 0.5f))
            casterValue.setTextColor(getColor(android.R.color.darker_gray))
        }
    }
    
    private fun calculateCasterForAllWheels() {
        // Caster calculation removed - see CALCULO_CASTER.md for proper implementation
        // This method is kept for compatibility but does nothing
    }
    
    private fun calculateCaster(wheelName: String): Float {
        // Caster calculation removed - not accurate with accelerometer only
        // See CALCULO_CASTER.md for proper implementation methods
        return 0f
    }
    
    private fun getColorForValue(value: Float, minNormal: Float, maxNormal: Float): Int {
        return if (value in minNormal..maxNormal) {
            getColor(android.R.color.holo_green_dark)
        } else {
            getColor(android.R.color.holo_orange_dark)
        }
    }
    
    private fun processMeasurements(measurements: List<FloatArray>): Map<String, Float> {
        // Promediar las mediciones
        val avgX = measurements.map { it[0] }.average().toFloat()
        val avgY = measurements.map { it[1] }.average().toFloat()
        val avgZ = measurements.map { it[2] }.average().toFloat()
        
        // Calcular solo camber y toe
        val camber = calculateCamber(avgX, avgY, avgZ)
        val toe = calculateToe(currentMagnetometer)
        
        return mapOf(
            "camber" to camber,
            "toe" to toe
            // Caster removed - see CALCULO_CASTER.md
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
    
    private fun displayResults(results: Map<String, Float>) {
        camberValue.text = "${String.format("%.2f", results["camber"] ?: 0f)}°"
        toeValue.text = "${String.format("%.2f", results["toe"] ?: 0f)}°"
        casterValue.text = "No disponible"
        casterValue.setTextColor(getColor(android.R.color.darker_gray))
        
        // Colorear camber y toe según valores normales
        updateValueColor(camberValue, results["camber"] ?: 0f, -2f, 2f)
        updateValueColor(toeValue, results["toe"] ?: 0f, -0.5f, 0.5f)
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
        // Advance to next wheel
        currentWheel++
        
        // Reset UI state for next wheel
        nextWheelButton.visibility = android.view.View.GONE
        finishButton.visibility = android.view.View.GONE
        measurementCard.visibility = android.view.View.GONE
        resultsCard.visibility = android.view.View.GONE
        
        // Reset measurement state
        isContinuousMeasuring = false
        currentLiveMeasurement.clear()
        
        // Update display for new wheel
        updateWheelDisplay()
        
        // Provide transition feedback
        statusText.text = """
            ✅ Rueda anterior completada
            
            Ahora posiciónate para medir: ${selectedWheels[currentWheel]}
        """.trimIndent()
        statusText.setTextColor(getColor(android.R.color.holo_green_dark))
    }
    
    private fun finishMeasurement() {
        // Verificar que todas las ruedas han sido medidas
        if (measurementResults.size != selectedWheels.size) {
            statusText.text = """
                ⚠️ MEDICIONES INCOMPLETAS
                
                Has medido ${measurementResults.size} de ${selectedWheels.size} ruedas.
                Completa todas las mediciones antes de continuar.
            """.trimIndent()
            statusText.setTextColor(getColor(android.R.color.holo_red_dark))
            return
        }
        
        val intent = Intent(this, ResultsActivity::class.java)
        
        // Crear bundle con todos los resultados
        val resultsBundle = Bundle()
        
        // Pasar información del workflow
        resultsBundle.putInt("wheel_count", selectedWheels.size)
        resultsBundle.putString("reference_wheel", selectedWheels[0])
        resultsBundle.putFloat("reference_toe_raw", referenceToeMeasurement)
        
        // Pasar resultados de cada rueda medida
        measurementResults.forEach { (wheelName, measurements) ->
            val wheelKey = wheelName.replace(" ", "_").lowercase()
            resultsBundle.putFloat("${wheelKey}_camber", measurements["camber"] ?: 0f)
            resultsBundle.putFloat("${wheelKey}_toe", measurements["toe"] ?: 0f)
            // No incluir caster ya que fue removido
        }
        
        // Pasar lista de ruedas medidas
        resultsBundle.putStringArray("measured_wheels", selectedWheels.toTypedArray())
        
        intent.putExtra("measurements", resultsBundle)
        
        // Mostrar mensaje de finalización
        statusText.text = """
            🎉 MEDICIONES COMPLETADAS
            
            Todas las ruedas han sido medidas exitosamente.
            Preparando informe de resultados...
        """.trimIndent()
        statusText.setTextColor(getColor(android.R.color.holo_green_dark))
        
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
                    
                    // Mostrar mediciones en tiempo real si no está midiendo activamente
                    if (!isMeasuring) {
                        showLivePreview()
                    }
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    currentMagnetometer[0] = it.values[0]
                    currentMagnetometer[1] = it.values[1]
                    currentMagnetometer[2] = it.values[2]
                }
            }
        }
    }
    
    private fun showLivePreview() {
        if (isContinuousMeasuring) {
            // Modo de medición continua - actualizar constantemente
            val camber = calculateCamber(currentAccelerometer[0], currentAccelerometer[1], currentAccelerometer[2])
            val toe = calculateToe(currentMagnetometer)
            
            // Actualizar medición actual
            currentLiveMeasurement["camber"] = camber
            currentLiveMeasurement["toe"] = toe
            
            runOnUiThread {
                // Actualizar UI con valores en tiempo real
                camberValue.text = "${String.format("%.2f", camber)}°"
                toeValue.text = "${String.format("%.2f", toe)}°"
                casterValue.text = "No disponible"
                casterValue.setTextColor(getColor(android.R.color.darker_gray))
                
                // Colorear según valores normales
                updateValueColor(camberValue, camber, -2f, 2f)
                updateValueColor(toeValue, toe, -0.5f, 0.5f)
                
                // Mostrar indicador de estabilidad
                if (isStable) {
                    statusText.text = "✅ Listo para guardar - El dispositivo está estable"
                    statusText.setTextColor(getColor(android.R.color.holo_green_dark))
                    measureButton.isEnabled = true
                } else {
                    statusText.text = "📡 Midiendo en tiempo real - Estabiliza para guardar"
                    statusText.setTextColor(getColor(android.R.color.holo_blue_light))
                    measureButton.isEnabled = true // Permitir guardar incluso si no está perfectamente estable
                }
            }
        } else {
            // Modo de vista previa normal
            if (isStable) {
                val camber = calculateCamber(currentAccelerometer[0], currentAccelerometer[1], currentAccelerometer[2])
                val toe = calculateToe(currentMagnetometer)
                
                runOnUiThread {
                    val previewText = """
                        📡 Vista Previa:
                        Camber: ${String.format("%.2f", camber)}°
                        Convergencia: ${String.format("%.2f", toe)}°
                        Caster: No disponible
                    """.trimIndent()
                    
                    statusText.text = previewText
                    statusText.setTextColor(getColor(android.R.color.holo_blue_light))
                }
            } else {
                runOnUiThread {
                    statusText.text = "Presiona 'Iniciar Medición' para comenzar"
                    statusText.setTextColor(getColor(android.R.color.holo_orange_light))
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
