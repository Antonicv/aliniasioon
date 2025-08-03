package com.alineacion.ruedas.ui.wheelselection

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.alineacion.ruedas.R
import com.alineacion.ruedas.ui.measurement.MeasurementActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

// Estados de las ruedas para el workflow correcto
enum class WheelState {
    PENDING,        // Gris, "Click para seleccionar"
    SELECTED,       // Azul, mostrando datos + botón "FIJAR DATOS"
    COMPLETED       // Verde, datos guardados + evaluación
}

/**
 * Actividad de selección de ruedas con interfaz visual y medición en tiempo real
 * 
 * Permite seleccionar las ruedas a medir usando una interfaz intuitiva
 * con imágenes de neumáticos distribuidas en una cuadrícula.
 * Muestra datos de medición en tiempo real cuando se selecciona una rueda.
 */
class WheelSelectionActivity : AppCompatActivity(), SensorEventListener {
    
    private lateinit var frontLeftCard: MaterialCardView
    private lateinit var frontRightCard: MaterialCardView
    private lateinit var rearLeftCard: MaterialCardView
    private lateinit var rearRightCard: MaterialCardView
    
    // TextViews para mostrar datos en tiempo real
    private lateinit var frontLeftDataText: MaterialTextView
    private lateinit var frontRightDataText: MaterialTextView
    private lateinit var rearLeftDataText: MaterialTextView
    private lateinit var rearRightDataText: MaterialTextView
    
    // Botones para fijar datos
    private lateinit var frontLeftFixButton: MaterialButton
    private lateinit var frontRightFixButton: MaterialButton
    private lateinit var rearLeftFixButton: MaterialButton
    private lateinit var rearRightFixButton: MaterialButton
    
    private lateinit var clearSelectionButton: MaterialButton
    private lateinit var startMeasurementButton: MaterialButton
    
    // Sensores para medición en tiempo real
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var currentAccelerometer = FloatArray(3)
    private var currentMagnetometer = FloatArray(3)
    private var isStable = false
    private val handler = Handler(Looper.getMainLooper())
    
    // Variables para auto-completado de medición
    private var stabilityCounter = 0
    private val requiredStabilitySeconds = 5
    private var stabilityCheckRunnable: Runnable? = null
    
    private val wheelCards = mutableMapOf<String, MaterialCardView>()
    private val wheelDataTexts = mutableMapOf<String, MaterialTextView>()
    private val wheelStabilityIndicators = mutableMapOf<String, MaterialTextView>()
    private val wheelFixButtons = mutableMapOf<String, MaterialButton>()
    private val wheelStates = mutableMapOf<String, WheelState>()
    private val wheelMeasurements = mutableMapOf<String, Pair<Float, Float>>() // Camber, Toe
    private var selectedWheelForMeasurement: String? = null
    
    private val wheelNames = mapOf(
        "frontLeft" to "Rueda Delantera Izquierda",
        "frontRight" to "Rueda Delantera Derecha",
        "rearLeft" to "Rueda Trasera Izquierda",
        "rearRight" to "Rueda Trasera Derecha"
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wheel_selection)
        
        setupViews()
        setupSensors()
        setupClickListeners()
        updateUI()
    }
    
    private fun setupViews() {
        title = "Selección de Ruedas - AliniaSoon"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        frontLeftCard = findViewById(R.id.frontLeftCard)
        frontRightCard = findViewById(R.id.frontRightCard)
        rearLeftCard = findViewById(R.id.rearLeftCard)
        rearRightCard = findViewById(R.id.rearRightCard)
        
        // Inicializar TextViews para datos en tiempo real
        frontLeftDataText = findViewById(R.id.frontLeftDataText)
        frontRightDataText = findViewById(R.id.frontRightDataText)
        rearLeftDataText = findViewById(R.id.rearLeftDataText)
        rearRightDataText = findViewById(R.id.rearRightDataText)
        
        // Inicializar botones para fijar datos
        frontLeftFixButton = findViewById(R.id.frontLeftFixButton)
        frontRightFixButton = findViewById(R.id.frontRightFixButton)
        rearLeftFixButton = findViewById(R.id.rearLeftFixButton)
        rearRightFixButton = findViewById(R.id.rearRightFixButton)
        
        clearSelectionButton = findViewById(R.id.clearSelectionButton)
        startMeasurementButton = findViewById(R.id.startMeasurementButton)
        
        // Mapear cards con sus identificadores
        wheelCards["frontLeft"] = frontLeftCard
        wheelCards["frontRight"] = frontRightCard
        wheelCards["rearLeft"] = rearLeftCard
        wheelCards["rearRight"] = rearRightCard
        
        // Mapear TextViews de datos
        wheelDataTexts["frontLeft"] = frontLeftDataText
        wheelDataTexts["frontRight"] = frontRightDataText
        wheelDataTexts["rearLeft"] = rearLeftDataText
        wheelDataTexts["rearRight"] = rearRightDataText
        
        // Mapear indicadores de estabilidad
        wheelStabilityIndicators["frontLeft"] = findViewById(R.id.frontLeftStabilityIndicator)
        wheelStabilityIndicators["frontRight"] = findViewById(R.id.frontRightStabilityIndicator)
        wheelStabilityIndicators["rearLeft"] = findViewById(R.id.rearLeftStabilityIndicator)
        wheelStabilityIndicators["rearRight"] = findViewById(R.id.rearRightStabilityIndicator)
        
        // Mapear botones de fijar datos
        wheelFixButtons["frontLeft"] = frontLeftFixButton
        wheelFixButtons["frontRight"] = frontRightFixButton
        wheelFixButtons["rearLeft"] = rearLeftFixButton
        wheelFixButtons["rearRight"] = rearRightFixButton
        
        // Inicializar estados de ruedas (todas empiezan en PENDING)
        wheelStates["frontLeft"] = WheelState.PENDING
        wheelStates["frontRight"] = WheelState.PENDING
        wheelStates["rearLeft"] = WheelState.PENDING
        wheelStates["rearRight"] = WheelState.PENDING
        
        // Inicializar textos de datos y aplicar estados visuales
        resetWheelDataTexts()
        updateAllWheelVisualStates()
    }
    
    private fun setupClickListeners() {
        wheelCards.forEach { (wheelId, card) ->
            card.setOnClickListener { onWheelCardClick(wheelId) }
        }
        
        wheelFixButtons.forEach { (wheelId, button) ->
            button.setOnClickListener { onFixDataClick(wheelId) }
        }
        
        clearSelectionButton.setOnClickListener { clearSelection() }
        startMeasurementButton.setOnClickListener { showResults() }
    }
    
    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }
    
    private fun resetWheelDataTexts() {
        wheelDataTexts.values.forEach { textView ->
            textView.text = "Toca para medir"
            textView.setTextColor(getColor(android.R.color.darker_gray))
        }
    }
    
    /**
     * Actualiza el estado visual de todas las ruedas
     */
    private fun updateAllWheelVisualStates() {
        wheelStates.forEach { (wheelId, state) ->
            updateWheelVisualState(wheelId, state)
        }
    }
    
    /**
     * Actualiza el estado visual de una rueda específica
     */
    private fun updateWheelVisualState(wheelId: String, state: WheelState) {
        val card = wheelCards[wheelId] ?: return
        val dataText = wheelDataTexts[wheelId] ?: return
        val stabilityIndicator = wheelStabilityIndicators[wheelId] ?: return
        val fixButton = wheelFixButtons[wheelId] ?: return
        
        when (state) {
            WheelState.PENDING -> {
                // Estado: Pendiente - Gris, "Click para seleccionar"
                card.strokeColor = getColor(android.R.color.darker_gray)
                card.strokeWidth = 2
                card.cardElevation = 4f
                dataText.text = "Click para seleccionar"
                dataText.setTextColor(getColor(android.R.color.darker_gray))
                stabilityIndicator.visibility = android.view.View.GONE
                fixButton.visibility = android.view.View.GONE
            }
            
            WheelState.SELECTED -> {
                // Estado: Seleccionado - Azul, datos en tiempo real + botón fijar
                card.strokeColor = getColor(android.R.color.holo_blue_bright)
                card.strokeWidth = 4
                card.cardElevation = 8f
                dataText.setTextColor(getColor(android.R.color.holo_blue_dark))
                stabilityIndicator.visibility = android.view.View.VISIBLE
                fixButton.visibility = android.view.View.VISIBLE
                // Los datos se actualizan en updateLiveData()
            }
            
            WheelState.COMPLETED -> {
                // Estado: Completado - Verde, valores guardados + evaluación
                card.strokeColor = getColor(android.R.color.holo_green_dark)
                card.strokeWidth = 3
                card.cardElevation = 6f
                dataText.setTextColor(getColor(android.R.color.holo_green_dark))
                stabilityIndicator.text = "✅"
                stabilityIndicator.visibility = android.view.View.VISIBLE
                fixButton.visibility = android.view.View.GONE
                
                // Mostrar valores guardados con evaluación
                wheelMeasurements[wheelId]?.let { (camber, toe) ->
                    val evaluation = getWheelEvaluation(camber, toe)
                    val needsAttention = needsAttention(camber, toe)
                    
                    if (needsAttention) {
                        card.strokeColor = getColor(android.R.color.holo_orange_dark)
                        dataText.setTextColor(getColor(android.R.color.holo_red_dark))
                        stabilityIndicator.text = "⚠️"
                    }
                    
                    dataText.text = """
                        Camber: ${String.format("%.2f", camber)}°
                        Convergencia: ${String.format("%.2f", toe)}°
                        $evaluation
                    """.trimIndent()
                }
            }
        }
    }
    
    /**
     * Evalúa si una rueda necesita atención basado en sus valores
     */
    private fun getWheelEvaluation(camber: Float, toe: Float): String {
        return when {
            abs(camber) <= 0.5f && abs(toe) <= 0.2f -> "Excelente"
            abs(camber) <= 1f && abs(toe) <= 0.5f -> "Aceptable"
            abs(camber) <= 2f && abs(toe) <= 1f -> "Revisar"
            else -> "Fuera de rango"
        }
    }
    
    /**
     * Determina si una rueda necesita atención basado en sus valores
     */
    private fun needsAttention(camber: Float, toe: Float): Boolean {
        return abs(camber) > 1f || abs(toe) > 0.5f
    }
    
    /**
     * Maneja el click en una card de rueda
     */
    private fun onWheelCardClick(wheelId: String) {
        val currentState = wheelStates[wheelId] ?: WheelState.PENDING
        
        when (currentState) {
            WheelState.PENDING -> {
                // Seleccionar rueda para medición
                selectWheelForMeasurement(wheelId)
            }
            
            WheelState.SELECTED -> {
                // Deseleccionar rueda
                deselectWheel(wheelId)
            }
            
            WheelState.COMPLETED -> {
                // Re-medir rueda (volver a seleccionar)
                selectWheelForMeasurement(wheelId)
            }
        }
    }
    
    /**
     * Maneja el click en el botón "FIJAR DATOS"
     */
    private fun onFixDataClick(wheelId: String) {
        if (selectedWheelForMeasurement == wheelId) {
            val camber = calculateCamber(currentAccelerometer[0], currentAccelerometer[1], currentAccelerometer[2])
            val toe = calculateToe(currentMagnetometer)
            
            // Guardar medición
            wheelMeasurements[wheelId] = Pair(camber, toe)
            wheelStates[wheelId] = WheelState.COMPLETED
            updateWheelVisualState(wheelId, WheelState.COMPLETED)
            
            // Detener sensores
            selectedWheelForMeasurement = null
            stopSensorReading()
            
            updateButtonStates()
        }
    }
    
    /**
     * Selecciona una rueda para medición
     */
    private fun selectWheelForMeasurement(wheelId: String) {
        // Deseleccionar rueda anterior si existe
        selectedWheelForMeasurement?.let { previousWheel ->
            if (wheelStates[previousWheel] == WheelState.SELECTED) {
                deselectWheel(previousWheel)
            }
        }
        
        // Seleccionar nueva rueda
        selectedWheelForMeasurement = wheelId
        wheelStates[wheelId] = WheelState.SELECTED
        updateWheelVisualState(wheelId, WheelState.SELECTED)
        
        // Iniciar sensores
        startSensorReading()
        
        updateButtonStates()
    }
    
    /**
     * Deselecciona una rueda
     */
    private fun deselectWheel(wheelId: String) {
        wheelStates[wheelId] = WheelState.PENDING
        updateWheelVisualState(wheelId, WheelState.PENDING)
        
        if (selectedWheelForMeasurement == wheelId) {
            selectedWheelForMeasurement = null
            stopSensorReading()
        }
        
        updateButtonStates()
    }
    
    private fun clearSelection() {
        // Detener cualquier medición en curso
        selectedWheelForMeasurement?.let { wheelId ->
            deselectWheel(wheelId)
        }
        
        // Resetear todos los estados a PENDING
        wheelStates.keys.forEach { wheelId ->
            wheelStates[wheelId] = WheelState.PENDING
            updateWheelVisualState(wheelId, WheelState.PENDING)
        }
        
        // Limpiar datos guardados
        wheelMeasurements.clear()
        stopSensorReading()
        
        updateButtonStates()
    }
    
    /**
     * Actualiza el estado de los botones según el progreso de mediciones
     */
    private fun updateButtonStates() {
        val completedCount = wheelStates.values.count { it == WheelState.COMPLETED }
        val totalWheels = wheelStates.size
        val isSelecting = wheelStates.values.any { it == WheelState.SELECTED }
        
        // Habilitar botón de limpiar si hay alguna medición completada o rueda seleccionada
        clearSelectionButton.isEnabled = completedCount > 0 || isSelecting
        
        // Cambiar el texto del botón principal según el contexto
        when {
            isSelecting -> {
                startMeasurementButton.text = "Midiendo... Usa FIJAR DATOS"
                startMeasurementButton.isEnabled = false
            }
            completedCount > 0 -> {
                startMeasurementButton.text = "VER RESULTADOS ($completedCount ruedas)"
                startMeasurementButton.isEnabled = true
            }
            else -> {
                startMeasurementButton.text = "Selecciona ruedas para medir"
                startMeasurementButton.isEnabled = false
            }
        }
    }
    
    /**
     * Muestra la página de resultados
     */
    private fun showResults() {
        val completedWheels = wheelStates.filterValues { it == WheelState.COMPLETED }
        
        if (completedWheels.isEmpty()) {
            return // No hay resultados que mostrar
        }
        
        // Preparar datos para la navegación
        val intent = Intent(this, com.alineacion.ruedas.ui.results.ResultsActivity::class.java)
        
        // Convertir mediciones al formato esperado por ResultsActivity
        val measurementResults = mutableMapOf<String, Map<String, Float>>()
        
        wheelMeasurements.forEach { (wheelId, measurement) ->
            val wheelName = wheelNames[wheelId] ?: wheelId
            measurementResults[wheelName] = mapOf(
                "camber" to measurement.first,
                "toe" to measurement.second
            )
        }
        
        // Serializar y enviar datos
        val resultsBundle = Bundle()
        measurementResults.forEach { (wheelName, values) ->
            val wheelBundle = Bundle()
            values.forEach { (key, value) ->
                wheelBundle.putFloat(key, value)
            }
            resultsBundle.putBundle(wheelName, wheelBundle)
        }
        
        intent.putExtra("measurement_results", resultsBundle)
        startActivity(intent)
    }
    
    private fun updateUI() {
        updateButtonStates()
        // Aquí se puede agregar lógica adicional de UI si es necesaria
    }
    
    private fun startSensorReading() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
    
    private fun stopSensorReading() {
        sensorManager.unregisterListener(this)
        // Ocultar indicador de estabilidad cuando se detiene la medición
        wheelStabilityIndicators.values.forEach { indicator ->
            runOnUiThread {
                indicator.visibility = android.view.View.GONE
            }
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    currentAccelerometer[0] = it.values[0]
                    currentAccelerometer[1] = it.values[1]
                    currentAccelerometer[2] = it.values[2]
                    
                    checkStability()
                    updateLiveData()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    currentMagnetometer[0] = it.values[0]
                    currentMagnetometer[1] = it.values[1]
                    currentMagnetometer[2] = it.values[2]
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario para esta implementación
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
    
    private fun updateLiveData() {
        selectedWheelForMeasurement?.let { wheelId ->
            val camber = calculateCamber(currentAccelerometer[0], currentAccelerometer[1], currentAccelerometer[2])
            val toe = calculateToe(currentMagnetometer)
            
            wheelDataTexts[wheelId]?.let { textView ->
                runOnUiThread {
                    textView.text = """
                        Camber: ${String.format("%.2f", camber)}°
                        Convergencia: ${String.format("%.2f", toe)}°
                        ${if (isStable) "Estable - Listo para fijar" else "Mantén quieto"}
                    """.trimIndent()
                    
                    val color = if (isStable) {
                        getColor(android.R.color.holo_green_dark)
                    } else {
                        getColor(android.R.color.holo_blue_dark)
                    }
                    textView.setTextColor(color)
                }
            }
            
            // Actualizar indicador de estabilidad
            wheelStabilityIndicators[wheelId]?.let { indicator ->
                runOnUiThread {
                    indicator.visibility = android.view.View.VISIBLE
                    indicator.text = if (isStable) "🟢" else "🟡"
                }
            }
        }
    }
    
    private fun calculateCamber(x: Float, y: Float, z: Float): Float {
        // Camber es la inclinación lateral de la rueda
        return Math.toDegrees(atan2(x.toDouble(), sqrt((y*y + z*z).toDouble()))).toFloat()
    }
    
    private fun calculateToe(magnetometer: FloatArray): Float {
        // Toe es la orientación hacia adentro/afuera
        return Math.toDegrees(atan2(magnetometer[0].toDouble(), magnetometer[1].toDouble())).toFloat()
    }
    
    private fun isWithinNormalRange(camber: Float, toe: Float): Boolean {
        return abs(camber) <= 2f && abs(toe) <= 0.5f
    }
    
    private fun getValueStatus(camber: Float, toe: Float): String {
        val camberStatus = when {
            abs(camber) <= 0.5f -> "✅ Camber excelente"
            abs(camber) <= 1f -> "⚠️ Camber aceptable"
            abs(camber) <= 2f -> "🔶 Camber revisar"
            else -> "❌ Camber fuera de rango"
        }
        
        val toeStatus = when {
            abs(toe) <= 0.2f -> "✅ Convergencia excelente"
            abs(toe) <= 0.5f -> "⚠️ Convergencia aceptable"
            else -> "❌ Convergencia fuera de rango"
        }
        
        return "$camberStatus\n$toeStatus"
    }
    
    override fun onResume() {
        super.onResume()
        if (selectedWheelForMeasurement != null) {
            startSensorReading()
        }
    }
    
    override fun onPause() {
        super.onPause()
        stopSensorReading()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
