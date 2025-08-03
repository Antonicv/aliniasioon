package com.alineacion.ruedas.ui.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alineacion.ruedas.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

/**
 * Adapter para mostrar los resultados detallados de cada rueda
 */
class WheelResultsAdapter(
    private val measurementResults: Map<String, Map<String, Float>>
) : RecyclerView.Adapter<WheelResultsAdapter.WheelResultViewHolder>() {
    
    // Filtrar solo las ruedas reales, excluyendo entradas especiales como "_workflow_info"
    private val wheels = measurementResults.keys.filter { !it.startsWith("_") }.toList()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WheelResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wheel_result, parent, false)
        return WheelResultViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: WheelResultViewHolder, position: Int) {
        val wheel = wheels[position]
        val measurements = measurementResults[wheel] ?: return
        
        holder.bind(wheel, measurements)
    }
    
    override fun getItemCount(): Int = wheels.size
    
    class WheelResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wheelPositionText: MaterialTextView = itemView.findViewById(R.id.wheelPositionText)
        private val wheelStatusText: MaterialTextView = itemView.findViewById(R.id.wheelStatusText)
        private val camberValueText: MaterialTextView = itemView.findViewById(R.id.camberValueText)
        private val camberStatusText: MaterialTextView = itemView.findViewById(R.id.camberStatusText)
        private val toeValueText: MaterialTextView = itemView.findViewById(R.id.toeValueText)
        private val toeStatusText: MaterialTextView = itemView.findViewById(R.id.toeStatusText)
        private val casterValueText: MaterialTextView = itemView.findViewById(R.id.casterValueText)
        private val casterStatusText: MaterialTextView = itemView.findViewById(R.id.casterStatusText)
        private val casterLayout: View = itemView.findViewById(R.id.casterLayout)
        private val deviationText: MaterialTextView = itemView.findViewById(R.id.deviationText)
        
        fun bind(wheelName: String, measurements: Map<String, Float>) {
            wheelPositionText.text = wheelName
            
            val camber = measurements["camber"] ?: 0f
            val toe = measurements["toe"] ?: 0f
            
            // Determine if this is the reference wheel (toe = 0)
            val isReferenceWheel = toe == 0f && camber != 0f
            
            // Determinar el estado general de la rueda (sin caster)
            val overallStatus = determineWheelStatus(camber, toe, isReferenceWheel)
            wheelStatusText.text = overallStatus.first
            wheelStatusText.setTextColor(overallStatus.second)
            
            // Mostrar valores
            camberValueText.text = "${String.format("%.2f", camber)}°"
            if (isReferenceWheel) {
                toeValueText.text = "0.00° (REF)"
            } else {
                toeValueText.text = "${String.format("%.2f", toe)}°"
            }
            casterValueText.text = "No medido"
            
            // Mostrar iconos de estado para cada valor
            camberStatusText.text = getStatusIcon(camber, -2f, 2f)
            toeStatusText.text = if (isReferenceWheel) "🎯" else getStatusIcon(toe, -0.5f, 0.5f)
            casterStatusText.text = "ℹ️"
            
            // Hide caster layout since it's not measured
            casterLayout.visibility = View.GONE
            
            // Calcular desviación solo con camber y toe
            val avgDeviation = (kotlin.math.abs(camber) + 
                               if (!isReferenceWheel) kotlin.math.abs(toe) else 0f) / 
                               if (!isReferenceWheel) 2 else 1
            
            val statusText = if (isReferenceWheel) {
                "Rueda de referencia - Desviación camber: ${String.format("%.1f", kotlin.math.abs(camber))}°"
            } else {
                "Desviación promedio: ${String.format("%.1f", avgDeviation)}° (${getDeviationQuality(avgDeviation)})"
            }
            
            deviationText.text = statusText
        }
        
        private fun determineWheelStatus(camber: Float, toe: Float, isReference: Boolean): Pair<String, Int> {
            val context = itemView.context
            
            if (isReference) {
                return when {
                    kotlin.math.abs(camber) < 0.5f -> 
                        Pair("🎯 REFERENCIA EXCELENTE", context.getColor(android.R.color.holo_green_dark))
                    kotlin.math.abs(camber) < 1f -> 
                        Pair("🎯 REFERENCIA BUENA", context.getColor(android.R.color.holo_orange_dark))
                    else -> 
                        Pair("🎯 REFERENCIA (AJUSTAR)", context.getColor(android.R.color.holo_red_dark))
                }
            }
            
            return when {
                kotlin.math.abs(camber) < 0.5f && kotlin.math.abs(toe) < 0.2f -> 
                    Pair("✅ EXCELENTE", context.getColor(android.R.color.holo_green_dark))
                kotlin.math.abs(camber) < 1f && kotlin.math.abs(toe) < 0.3f -> 
                    Pair("✓ BUENO", context.getColor(android.R.color.holo_orange_dark))
                else -> 
                    Pair("⚠️ REQUIERE AJUSTE", context.getColor(android.R.color.holo_red_dark))
            }
        }
        
        private fun getStatusIcon(value: Float, minNormal: Float, maxNormal: Float): String {
            return if (value in minNormal..maxNormal) "✅" else "⚠️"
        }
        
        private fun getDeviationQuality(deviation: Float): String {
            return when {
                deviation < 0.5f -> "Excelente"
                deviation < 1f -> "Bueno"
                deviation < 2f -> "Regular"
                else -> "Requiere ajuste"
            }
        }
    }
}
