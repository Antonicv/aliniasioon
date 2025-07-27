package com.alineacion.ruedas.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alineacion.ruedas.R
import com.alineacion.ruedas.core.CalibrationEngine
import com.alineacion.ruedas.core.WheelAlignmentSensorManager
import com.alineacion.ruedas.ui.calibration.CalibrationActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

/**
 * Actividad principal de la aplicación
 * Punto de entrada y dashboard de funciones principales
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var sensorManager: WheelAlignmentSensorManager
    private lateinit var calibrationEngine: CalibrationEngine
    
    // UI Components
    private lateinit var sensorStatusCard: MaterialCardView
    private lateinit var sensorStatusText: MaterialTextView
    private lateinit var calibrationStatusText: MaterialTextView
    private lateinit var startCalibrationButton: MaterialButton
    private lateinit var startMeasurementButton: MaterialButton
    private lateinit var viewHistoryButton: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeComponents()
        setupUI()
        checkSensorAvailability()
    }
    
    /**
     * Inicializa los componentes principales
     */
    private fun initializeComponents() {
        sensorManager = WheelAlignmentSensorManager(this)
        calibrationEngine = CalibrationEngine()
    }
    
    /**
     * Configura la interfaz de usuario
     */
    private fun setupUI() {
        sensorStatusCard = findViewById(R.id.sensor_status_card)
        sensorStatusText = findViewById(R.id.sensor_status_text)
        calibrationStatusText = findViewById(R.id.calibration_status_text)
        startCalibrationButton = findViewById(R.id.start_calibration_button)
        startMeasurementButton = findViewById(R.id.start_measurement_button)
        viewHistoryButton = findViewById(R.id.view_history_button)
        
        // Configurar listeners
        startCalibrationButton.setOnClickListener {
            startCalibrationProcess()
        }
        
        startMeasurementButton.setOnClickListener {
            startMeasurementProcess()
        }
        
        viewHistoryButton.setOnClickListener {
            openMeasurementHistory()
        }
        
        // Actualizar textos para reflejar que calibración es opcional
        calibrationStatusText.text = """
            📱 CALIBRACIÓN (OPCIONAL)
            
            Los sensores del smartphone son suficientemente precisos para alineación básica.
            La calibración solo es necesaria para mediciones de máxima precisión.
        """.trimIndent()
        
        startCalibrationButton.text = "🔧 Calibración Avanzada (Opcional)"
        
        // Observar estados
        observeCalibrationState()
    }
    
    /**
     * Verifica la disponibilidad de sensores
     */
    private fun checkSensorAvailability() {
        val availability = sensorManager.areSensorsAvailable()
        
        updateSensorStatusUI(availability)
        updateButtonStates(availability)
    }
    
    /**
     * Actualiza la UI del estado de sensores
     */
    private fun updateSensorStatusUI(availability: com.alineacion.ruedas.core.SensorAvailability) {
        val statusText = when {
            availability.isFullyCompatible -> {
                sensorStatusCard.setCardBackgroundColor(getColor(android.R.color.holo_green_light))
                "✓ Todos los sensores disponibles"
            }
            availability.hasAccelerometer && availability.hasGyroscope -> {
                sensorStatusCard.setCardBackgroundColor(getColor(android.R.color.holo_orange_light))
                "⚠ Funcionalidad básica disponible (sin magnetómetro)"
            }
            else -> {
                sensorStatusCard.setCardBackgroundColor(getColor(android.R.color.holo_red_light))
                "✗ Sensores requeridos no disponibles"
            }
        }
        
        sensorStatusText.text = statusText
    }
    
    /**
     * Actualiza el estado de los botones según disponibilidad
     */
    private fun updateButtonStates(availability: com.alineacion.ruedas.core.SensorAvailability) {
        startCalibrationButton.isEnabled = availability.isFullyCompatible
        
        // El botón de medición está siempre habilitado (calibración opcional)
        startMeasurementButton.isEnabled = availability.isFullyCompatible
    }
    
    /**
     * Observa el estado de calibración
     */
    private fun observeCalibrationState() {
        lifecycleScope.launch {
            calibrationEngine.calibrationState.collect { state ->
                updateCalibrationStatusUI(state)
            }
        }
    }
    
    /**
     * Actualiza la UI del estado de calibración
     */
    private fun updateCalibrationStatusUI(state: com.alineacion.ruedas.core.CalibrationState) {
        val statusText = when (state) {
            com.alineacion.ruedas.core.CalibrationState.NOT_STARTED -> {
                // Botón de medición siempre habilitado (calibración opcional)
                "📱 Calibración opcional disponible"
            }
            com.alineacion.ruedas.core.CalibrationState.COMPLETED -> {
                "✅ Calibración completada - Precisión mejorada"
            }
            com.alineacion.ruedas.core.CalibrationState.ERROR -> {
                "⚠️ Error en calibración - Medición sin calibrar disponible"
            }
            else -> {
                "🔄 Calibrando..."
            }
        }
        
        calibrationStatusText.text = statusText
    }
    
    /**
     * Inicia el proceso de calibración
     */
    private fun startCalibrationProcess() {
        val intent = Intent(this, CalibrationActivity::class.java)
        startActivity(intent)
    }
    
    /**
     * Inicia el proceso de medición
     */
    private fun startMeasurementProcess() {
        // Medición disponible sin calibración obligatoria
        val intent = Intent(this, com.alineacion.ruedas.ui.measurement.MeasurementActivity::class.java)
        startActivity(intent)
    }
    
    /**
     * Abre el historial de mediciones
     */
    private fun openMeasurementHistory() {
        // val intent = Intent(this, HistoryActivity::class.java)
        // startActivity(intent)
        
        // Por ahora mostrar mensaje
        // TODO: Implementar HistoryActivity
    }
    
    override fun onResume() {
        super.onResume()
        sensorManager.startSensorReading()
        checkSensorAvailability()
    }
    
    override fun onPause() {
        super.onPause()
        sensorManager.stopSensorReading()
    }
}
