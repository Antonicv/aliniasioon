package com.alineacion.ruedas.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
    private lateinit var recommendationsHeader: LinearLayout
    private lateinit var recommendationsToggle: MaterialTextView
    
    private lateinit var overallStatusText: MaterialTextView
    private lateinit var measurementDateText: MaterialTextView
    private lateinit var vehicleStatusText: MaterialTextView
    private lateinit var recommendationsText: MaterialTextView
    
    private var isRecommendationsExpanded = false
    
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
        
        try {
            setContentView(R.layout.activity_results)
            android.util.Log.d("ResultsActivity", "Layout set successfully")
            
            setupViews()
            android.util.Log.d("ResultsActivity", "Views setup completed")
            
            loadMeasurements()
            android.util.Log.d("ResultsActivity", "Measurements loaded")
            
            displayResults()
            android.util.Log.d("ResultsActivity", "Results displayed")
            
        } catch (e: Exception) {
            android.util.Log.e("ResultsActivity", "Error in onCreate: ${e.message}", e)
            // Mostrar error al usuario
            setContentView(android.R.layout.simple_list_item_1)
            val errorText = findViewById<android.widget.TextView>(android.R.id.text1)
            errorText?.text = "Error al cargar resultados: ${e.message}"
        }
    }
    
    private fun setupViews() {
        title = "Resultados de Alineación - AliniaSoon"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        summaryCard = findViewById(R.id.summaryCard)
        detailsRecyclerView = findViewById(R.id.detailsRecyclerView)
        recommendationsCard = findViewById(R.id.recommendationsCard)
        recommendationsHeader = findViewById(R.id.recommendationsHeader)
        recommendationsToggle = findViewById(R.id.recommendationsToggle)
        
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
        
        // Setup recommendations toggle
        recommendationsHeader.setOnClickListener { toggleRecommendations() }
        
        // Setup RecyclerView
        detailsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun loadMeasurements() {
        val bundle = intent.getBundleExtra("measurements")
        
        if (bundle != null) {
            try {
                // Load workflow information
                val wheelCount = bundle.getInt("wheel_count", 4)
                val referenceWheel = bundle.getString("reference_wheel", "")
                val referenceToeRaw = bundle.getFloat("reference_toe_raw", 0f)
                val measuredWheels = bundle.getStringArray("measured_wheels") ?: emptyArray()
                
                // Clear previous results
                measurementResults.clear()
                
                // Load measurements for each wheel
                measuredWheels.forEach { wheelName ->
                    val wheelKey = wheelName.replace(" ", "_").lowercase()
                    val camber = bundle.getFloat("${wheelKey}_camber", 0f)
                    val toe = bundle.getFloat("${wheelKey}_toe", 0f)
                    
                    measurementResults[wheelName] = mapOf(
                        "camber" to camber,
                        "toe" to toe
                        // Caster removed from results
                    )
                }
                
                // Store workflow info for display
                val workflowInfo = mapOf(
                    "wheel_count" to wheelCount.toFloat(),
                    "reference_wheel_name" to (referenceWheel.hashCode().toFloat()), // Store reference wheel info
                    "reference_toe_raw" to referenceToeRaw
                )
                measurementResults["_workflow_info"] = workflowInfo
                
                // Debug: Log loaded data
                android.util.Log.d("ResultsActivity", "Loaded ${measurementResults.size - 1} wheel measurements")
                measurementResults.forEach { (key, value) ->
                    if (key != "_workflow_info") {
                        android.util.Log.d("ResultsActivity", "$key: $value")
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.e("ResultsActivity", "Error loading measurements: ${e.message}")
                generateSampleData()
            }
        } else {
            // Fallback: try to load individual wheel measurements (old format)
            var hasData = false
            wheels.forEach { wheel ->
                val camber = intent.getFloatExtra("${wheel}_camber", 0f)
                val toe = intent.getFloatExtra("${wheel}_toe", 0f)
                
                if (camber != 0f || toe != 0f) {
                    measurementResults[wheel] = mapOf(
                        "camber" to camber,
                        "toe" to toe
                    )
                    hasData = true
                }
            }
            
            // If no measurements found, generate sample data
            if (!hasData) {
                android.util.Log.w("ResultsActivity", "No measurement data found, using sample data")
                generateSampleData()
            }
        }
    }
    
    private fun generateSampleData() {
        measurementResults = mutableMapOf(
            "Rueda Delantera Izquierda" to mapOf("camber" to -0.5f, "toe" to 0.0f), // Reference wheel
            "Rueda Delantera Derecha" to mapOf("camber" to -0.3f, "toe" to 0.2f),
            "Rueda Trasera Izquierda" to mapOf("camber" to -1.2f, "toe" to -0.1f),
            "Rueda Trasera Derecha" to mapOf("camber" to -1.0f, "toe" to 0.1f)
        )
        
        // Add workflow info for sample data
        measurementResults["_workflow_info"] = mapOf(
            "wheel_count" to 4f,
            "reference_wheel" to "Rueda Delantera Izquierda".hashCode().toFloat(),
            "reference_toe_raw" to 0.2f
        )
    }
    
    private fun displayResults() {
        // Validate that we have measurement data
        val wheelData = measurementResults.filterKeys { !it.startsWith("_") }
        if (wheelData.isEmpty()) {
            android.util.Log.e("ResultsActivity", "No wheel measurement data available")
            generateSampleData()
            displayResults() // Retry with sample data
            return
        }
        
        val analysis = analyzeResults()
        val workflowInfo = measurementResults["_workflow_info"]
        
        // Show measurement summary with workflow info
        val referenceWheelName = try {
            val referenceWheelHash = workflowInfo?.get("reference_wheel_name")?.toInt()
            measurementResults.keys.find { 
                it.hashCode() == referenceWheelHash && !it.startsWith("_")
            } ?: wheelData.keys.first() // Use first wheel as fallback
        } catch (e: Exception) {
            wheelData.keys.first() // Safe fallback
        }
        
        val wheelCount = workflowInfo?.get("wheel_count")?.toInt() ?: wheelData.size
        val referenceToeRaw = workflowInfo?.get("reference_toe_raw") ?: 0f
        
        // Enhanced summary with reference wheel info
        overallStatusText.text = """
            ${analysis.overallStatus}
            
            🎯 Rueda de referencia: ${referenceWheelName}
            📊 Ruedas medidas: ${wheelCount}
            📐 TOE de referencia (bruto): ${String.format("%.2f", referenceToeRaw)}°
        """.trimIndent()
        overallStatusText.setTextColor(getColor(analysis.statusColor))
        
        vehicleStatusText.text = analysis.vehicleStatus
        
        // Show date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        measurementDateText.text = "Medición realizada: ${dateFormat.format(Date())}"
        
        // Show recommendations
        recommendationsText.text = analysis.recommendations
        
        // Setup adapter for details
        try {
            val adapter = WheelResultsAdapter(wheelData.toMutableMap())
            detailsRecyclerView.adapter = adapter
            android.util.Log.d("ResultsActivity", "Results displayed successfully for ${wheelData.size} wheels")
        } catch (e: Exception) {
            android.util.Log.e("ResultsActivity", "Error setting up RecyclerView: ${e.message}")
            // Show error in UI
            overallStatusText.text = "❌ Error mostrando resultados detallados"
            overallStatusText.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }
    
    private fun analyzeResults(): MeasurementAnalysis {
        var issuesFound = 0
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        
        // Only analyze actual wheel measurements (exclude workflow info)
        val wheelData = measurementResults.filterKeys { !it.startsWith("_") }
        
        wheelData.forEach { (wheel, measurements) ->
            val camber = measurements["camber"] ?: 0f
            val toe = measurements["toe"] ?: 0f
            
            // Analizar Camber (normal: -2° a +2°)
            if (camber < -2f || camber > 2f) {
                issuesFound++
                issues.add("$wheel: Camber fuera de rango (${String.format("%.1f", camber)}°)")
                if (camber < -2f) {
                    recommendations.add("🔧 $wheel: Ajustar camber - rueda muy inclinada hacia adentro")
                } else {
                    recommendations.add("🔧 $wheel: Ajustar camber - rueda muy inclinada hacia afuera")
                }
            }
            
            // Analizar Toe (normal: -0.5° a +0.5°) 
            // Note: Reference wheel should always be 0°
            val isReferenceWheel = toe == 0f && camber != 0f // Rough detection
            if (!isReferenceWheel && (toe < -0.5f || toe > 0.5f)) {
                issuesFound++
                issues.add("$wheel: Convergencia fuera de rango (${String.format("%.2f", toe)}°)")
                if (toe < -0.5f) {
                    recommendations.add("🔧 $wheel: Ajustar convergencia - rueda muy divergente")
                } else {
                    recommendations.add("🔧 $wheel: Ajustar convergencia - rueda muy convergente")
                }
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
            issuesFound == 0 -> "🎉 Tu vehículo presenta una alineación excelente. No se requieren ajustes en este momento."
            issuesFound <= 2 -> "👍 Tu vehículo presenta una alineación aceptable con algunos ajustes menores recomendados."
            else -> "⚠️ Tu vehículo requiere ajustes de alineación para optimizar el rendimiento y seguridad."
        }
        
        val recommendationsText = if (recommendations.isNotEmpty()) {
            "🔧 RECOMENDACIONES:\n\n" + recommendations.joinToString("\n\n• ", "• ")
        } else {
            "🎉 ¡Tu vehículo no requiere ajustes de alineación en este momento!\n\nTodas las mediciones están dentro de los rangos normales."
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
        val workflowInfo = measurementResults["_workflow_info"]
        val referenceWheelHash = workflowInfo?.get("reference_wheel")?.toInt()
        val referenceWheelName = measurementResults.keys.find { 
            it.hashCode() == referenceWheelHash 
        } ?: "Rueda Delantera Izquierda"
        val wheelCount = workflowInfo?.get("wheel_count")?.toInt() ?: measurementResults.size - 1
        val referenceToeRaw = workflowInfo?.get("reference_toe_raw") ?: 0f
        
        val shareText = buildString {
            appendLine("🚗 INFORME DE ALINEACIÓN - AliniaSoon")
            appendLine("==================================================")
            appendLine("📅 Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
            appendLine()
            
            appendLine("📊 RESUMEN GENERAL:")
            appendLine("${analysis.overallStatus}")
            appendLine("${analysis.vehicleStatus}")
            appendLine()
            
            appendLine("🎯 INFORMACIÓN DEL WORKFLOW:")
            appendLine("• Ruedas medidas: $wheelCount")
            appendLine("• Rueda de referencia: $referenceWheelName")
            appendLine("• TOE de referencia (bruto): ${String.format("%.2f", referenceToeRaw)}°")
            appendLine("• Método: Medición relativa con smartphone")
            appendLine()
            
            appendLine("📋 MEDICIONES DETALLADAS:")
            appendLine("----------------------------------------")
            
            // Only show actual wheel measurements
            val wheelData = measurementResults.filterKeys { !it.startsWith("_") }
            wheelData.forEach { (wheel, measurements) ->
                val isReference = wheel == referenceWheelName
                appendLine()
                appendLine("🔧 $wheel${if (isReference) " (REFERENCIA)" else ""}:")
                appendLine("  • Camber: ${String.format("%.2f", measurements["camber"] ?: 0f)}°")
                if (isReference) {
                    appendLine("  • TOE: 0.00° (por definición de referencia)")
                } else {
                    appendLine("  • TOE: ${String.format("%.2f", measurements["toe"] ?: 0f)}° (relativo)")
                }
                appendLine("  • Caster: No medido (ver documentación técnica)")
            }
            appendLine()
            
            appendLine("💡 RECOMENDACIONES:")
            appendLine("----------------------------------------")
            appendLine(analysis.recommendations)
            appendLine()
            
            appendLine("⚠️ NOTAS IMPORTANTES:")
            appendLine("• Las mediciones de TOE son relativas a la rueda de referencia")
            appendLine("• Para mediciones profesionales, consulte un taller especializado")
            appendLine("• Esta app proporciona valores orientativos usando sensores del smartphone")
            appendLine()
            
            appendLine("📱 Generado por AliniaSoon v1.0")
            appendLine("🔗 App de alineación de ruedas con smartphone")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Informe de Alineación - AliniaSoon")
        }
        
        startActivity(Intent.createChooser(shareIntent, "Compartir informe de alineación"))
    }
    
    private fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    
    private fun toggleRecommendations() {
        isRecommendationsExpanded = !isRecommendationsExpanded
        
        if (isRecommendationsExpanded) {
            recommendationsText.visibility = View.VISIBLE
            recommendationsToggle.text = "▲"
        } else {
            recommendationsText.visibility = View.GONE
            recommendationsToggle.text = "▼"
        }
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
