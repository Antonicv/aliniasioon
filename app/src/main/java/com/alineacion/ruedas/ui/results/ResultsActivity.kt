package com.alineacion.ruedas.ui.results

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alineacion.ruedas.R
import com.alineacion.ruedas.ui.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Actividad de resultados finales
 * 
 * Muestra un resumen completo de todas las mediciones realizadas,
 * análisis de los valores y recomendaciones.
 */
class ResultsActivity : AppCompatActivity() {
    
    private lateinit var summaryCard: MaterialCardView
    private lateinit var detailsRecyclerView: RecyclerView
    private lateinit var recommendationsCard: MaterialCardView
    
    private lateinit var overallStatusText: MaterialTextView
    private lateinit var measurementDateText: MaterialTextView
    private lateinit var vehicleStatusText: MaterialTextView
    private lateinit var recommendationsText: MaterialTextView
    
    private lateinit var newMeasurementButton: MaterialButton
    private lateinit var shareResultsButton: MaterialButton
    private lateinit var backToMainButton: MaterialButton
    
    private var measurementResults = mutableMapOf<String, Map<String, Float>>()
    
    private val wheels = listOf(
        "Rueda Delantera Izquierda",
        "Rueda Delantera Derecha", 
        "Rueda Trasera Izquierda",
        "Rueda Trasera Derecha"
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        
        setupViews()
        loadMeasurements()
        displayResults()
    }
    
    private fun setupViews() {
        title = "Resultados de Alineación"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        summaryCard = findViewById(R.id.summaryCard)
        detailsRecyclerView = findViewById(R.id.detailsRecyclerView)
        recommendationsCard = findViewById(R.id.recommendationsCard)
        
        overallStatusText = findViewById(R.id.overallStatusText)
        measurementDateText = findViewById(R.id.measurementDateText)
        vehicleStatusText = findViewById(R.id.vehicleStatusText)
        recommendationsText = findViewById(R.id.recommendationsText)
        
        newMeasurementButton = findViewById(R.id.newMeasurementButton)
        shareResultsButton = findViewById(R.id.shareResultsButton)
        backToMainButton = findViewById(R.id.backToMainButton)
        
        newMeasurementButton.setOnClickListener { startNewMeasurement() }
        shareResultsButton.setOnClickListener { shareResults() }
        backToMainButton.setOnClickListener { backToMain() }
        
        // Setup RecyclerView
        detailsRecyclerView.layoutManager = LinearLayoutManager(this)
        
        // Set measurement date
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        measurementDateText.text = "Medición realizada: $currentDate"
    }
    
    private fun loadMeasurements() {
        val bundle = intent.getBundleExtra("measurements")
        
        if (bundle != null) {
            wheels.forEach { wheel ->
                val camber = bundle.getFloat("${wheel}_camber", 0f)
                val toe = bundle.getFloat("${wheel}_toe", 0f)
                val caster = bundle.getFloat("${wheel}_caster", 0f)
                
                measurementResults[wheel] = mapOf(
                    "camber" to camber,
                    "toe" to toe,
                    "caster" to caster
                )
            }
        } else {
            // Datos de ejemplo si no hay mediciones reales
            generateSampleData()
        }
    }
    
    private fun generateSampleData() {
        measurementResults = mutableMapOf(
            "Rueda Delantera Izquierda" to mapOf("camber" to -0.5f, "toe" to 0.2f, "caster" to 3.5f),
            "Rueda Delantera Derecha" to mapOf("camber" to -0.3f, "toe" to 0.1f, "caster" to 3.7f),
            "Rueda Trasera Izquierda" to mapOf("camber" to -1.2f, "toe" to -0.1f, "caster" to 0f),
            "Rueda Trasera Derecha" to mapOf("camber" to -1.0f, "toe" to 0.0f, "caster" to 0f)
        )
    }
    
    private fun displayResults() {
        val analysis = analyzeResults()
        
        // Mostrar estado general
        overallStatusText.text = analysis.overallStatus
        overallStatusText.setTextColor(getColor(analysis.statusColor))
        
        vehicleStatusText.text = analysis.vehicleStatus
        
        // Mostrar recomendaciones
        recommendationsText.text = analysis.recommendations
        
        // Setup adapter for details
        val adapter = WheelResultsAdapter(measurementResults)
        detailsRecyclerView.adapter = adapter
    }
    
    private fun analyzeResults(): MeasurementAnalysis {
        var issuesFound = 0
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        
        measurementResults.forEach { (wheel, measurements) ->
            val camber = measurements["camber"] ?: 0f
            val toe = measurements["toe"] ?: 0f
            val caster = measurements["caster"] ?: 0f
            
            // Analizar Camber (normal: -2° a +2°)
            if (camber < -2f || camber > 2f) {
                issuesFound++
                issues.add("$wheel: Camber fuera de rango (${String.format("%.1f", camber)}°)")
                if (camber < -2f) {
                    recommendations.add("$wheel: Ajustar camber - rueda inclinada hacia adentro")
                } else {
                    recommendations.add("$wheel: Ajustar camber - rueda inclinada hacia afuera")
                }
            }
            
            // Analizar Toe (normal: -0.5° a +0.5°)
            if (toe < -0.5f || toe > 0.5f) {
                issuesFound++
                issues.add("$wheel: Toe fuera de rango (${String.format("%.1f", toe)}°)")
                if (toe < -0.5f) {
                    recommendations.add("$wheel: Ajustar convergencia - demasiado toe-out")
                } else {
                    recommendations.add("$wheel: Ajustar convergencia - demasiado toe-in")
                }
            }
            
            // Analizar Caster solo para ruedas delanteras (normal: 1° a 7°)
            if (wheel.contains("Delantera") && (caster < 1f || caster > 7f)) {
                issuesFound++
                issues.add("$wheel: Caster fuera de rango (${String.format("%.1f", caster)}°)")
                recommendations.add("$wheel: Verificar caster - puede afectar la estabilidad")
            }
        }
        
        val overallStatus = when {
            issuesFound == 0 -> "✅ Alineación EXCELENTE"
            issuesFound <= 2 -> "⚠️ Alineación BUENA - Ajustes menores"
            issuesFound <= 4 -> "⚠️ Alineación REGULAR - Requiere ajustes"
            else -> "❌ Alineación MALA - Ajuste urgente necesario"
        }
        
        val statusColor = when {
            issuesFound == 0 -> android.R.color.holo_green_dark
            issuesFound <= 2 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_red_dark
        }
        
        val vehicleStatus = when {
            issuesFound == 0 -> "Tu vehículo presenta una alineación excelente. No se requieren ajustes en este momento."
            issuesFound <= 2 -> "Tu vehículo presenta una alineación aceptable con algunos ajustes menores recomendados."
            else -> "Tu vehículo requiere ajustes de alineación para optimizar el rendimiento y seguridad."
        }
        
        val recommendationsText = if (recommendations.isNotEmpty()) {
            "Recomendaciones:\n\n" + recommendations.joinToString("\n\n• ", "• ")
        } else {
            "Tu vehículo no requiere ajustes de alineación en este momento. ¡Excelente!"
        }
        
        return MeasurementAnalysis(
            overallStatus = overallStatus,
            statusColor = statusColor,
            vehicleStatus = vehicleStatus,
            recommendations = recommendationsText,
            issuesCount = issuesFound
        )
    }
    
    private fun startNewMeasurement() {
        finish()
    }
    
    private fun shareResults() {
        val analysis = analyzeResults()
        val shareText = buildString {
            appendLine("📱 Resultados de Alineación de Ruedas")
            appendLine("📅 ${measurementDateText.text}")
            appendLine()
            appendLine("📊 RESUMEN:")
            appendLine(analysis.overallStatus)
            appendLine()
            appendLine("📋 MEDICIONES DETALLADAS:")
            measurementResults.forEach { (wheel, measurements) ->
                appendLine()
                appendLine("🔧 $wheel:")
                appendLine("  • Camber: ${String.format("%.2f", measurements["camber"])}°")
                appendLine("  • Toe: ${String.format("%.2f", measurements["toe"])}°")
                if (wheel.contains("Delantera")) {
                    appendLine("  • Caster: ${String.format("%.2f", measurements["caster"])}°")
                }
            }
            appendLine()
            appendLine("💡 ${analysis.recommendations}")
            appendLine()
            appendLine("📱 Medido con la App de Alineación de Ruedas")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Resultados de Alineación de Ruedas")
        }
        
        startActivity(Intent.createChooser(shareIntent, "Compartir resultados"))
    }
    
    private fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        backToMain()
        return true
    }
    
    data class MeasurementAnalysis(
        val overallStatus: String,
        val statusColor: Int,
        val vehicleStatus: String,
        val recommendations: String,
        val issuesCount: Int
    )
}
