package com.alineacion.ruedas.ui.wheelselection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alineacion.ruedas.R
import com.alineacion.ruedas.ui.measurement.MeasurementActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

/**
 * Actividad de selección de ruedas con interfaz visual
 * 
 * Permite seleccionar las ruedas a medir usando una interfaz intuitiva
 * con imágenes de neumáticos distribuidas en una cuadrícula.
 */
class WheelSelectionActivity : AppCompatActivity() {
    
    private lateinit var frontLeftCard: MaterialCardView
    private lateinit var frontRightCard: MaterialCardView
    private lateinit var rearLeftCard: MaterialCardView
    private lateinit var rearRightCard: MaterialCardView
    
    private lateinit var selectionInfoText: MaterialTextView
    private lateinit var clearSelectionButton: MaterialButton
    private lateinit var startMeasurementButton: MaterialButton
    
    private val selectedWheels = mutableSetOf<String>()
    private val wheelCards = mutableMapOf<String, MaterialCardView>()
    
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
        
        selectionInfoText = findViewById(R.id.selectionInfoText)
        clearSelectionButton = findViewById(R.id.clearSelectionButton)
        startMeasurementButton = findViewById(R.id.startMeasurementButton)
        
        // Mapear cards con sus identificadores
        wheelCards["frontLeft"] = frontLeftCard
        wheelCards["frontRight"] = frontRightCard
        wheelCards["rearLeft"] = rearLeftCard
        wheelCards["rearRight"] = rearRightCard
    }
    
    private fun setupClickListeners() {
        wheelCards.forEach { (wheelId, card) ->
            card.setOnClickListener { toggleWheelSelection(wheelId, card) }
        }
        
        clearSelectionButton.setOnClickListener { clearSelection() }
        startMeasurementButton.setOnClickListener { startMeasurement() }
    }
    
    private fun toggleWheelSelection(wheelId: String, card: MaterialCardView) {
        val wheelName = wheelNames[wheelId] ?: return
        
        if (selectedWheels.contains(wheelName)) {
            // Deseleccionar rueda
            selectedWheels.remove(wheelName)
            card.strokeColor = getColor(android.R.color.transparent)
            card.setCardBackgroundColor(getColor(R.color.md_theme_light_surfaceVariant))
        } else {
            // Seleccionar rueda
            selectedWheels.add(wheelName)
            card.strokeColor = getColor(R.color.md_theme_light_primary)
            card.setCardBackgroundColor(getColor(R.color.md_theme_light_primaryContainer))
        }
        
        updateUI()
    }
    
    private fun clearSelection() {
        selectedWheels.clear()
        wheelCards.values.forEach { card ->
            card.strokeColor = getColor(android.R.color.transparent)
            card.setCardBackgroundColor(getColor(R.color.md_theme_light_surfaceVariant))
        }
        updateUI()
    }
    
    private fun updateUI() {
        val selectionCount = selectedWheels.size
        
        // Actualizar texto informativo
        selectionInfoText.text = when (selectionCount) {
            0 -> "Selecciona al menos 2 ruedas para continuar"
            1 -> "Rueda seleccionada: ${selectedWheels.first()}\nSelecciona al menos 1 rueda más"
            else -> {
                val wheelsList = selectedWheels.joinToString("\n• ", "• ")
                "Ruedas seleccionadas ($selectionCount):\n$wheelsList\n\n🎯 Primera rueda = Referencia (TOE = 0°)"
            }
        }
        
        // Habilitar/deshabilitar botones
        clearSelectionButton.isEnabled = selectionCount > 0
        startMeasurementButton.isEnabled = selectionCount >= 2
        
        // Actualizar texto del botón de inicio
        startMeasurementButton.text = when (selectionCount) {
            0, 1 -> "Iniciar Medición"
            2 -> "Medir 2 Ruedas"
            3 -> "Medir 3 Ruedas"
            4 -> "Medir 4 Ruedas"
            else -> "Iniciar Medición"
        }
    }
    
    private fun startMeasurement() {
        if (selectedWheels.size < 2) {
            return
        }
        
        val intent = Intent(this, MeasurementActivity::class.java)
        
        // Pasar las ruedas seleccionadas como array
        intent.putExtra("selected_wheels", selectedWheels.toTypedArray())
        intent.putExtra("wheel_count", selectedWheels.size)
        
        startActivity(intent)
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
