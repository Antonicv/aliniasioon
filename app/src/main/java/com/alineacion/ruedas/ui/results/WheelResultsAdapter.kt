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
    
    private val wheels = measurementResults.keys.toList()
    
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
            val caster = measurements["caster"] ?: 0f
            
            // Determinar el estado general de la rueda
            val overallStatus = determineWheelStatus(camber, toe, caster)
            wheelStatusText.text = overallStatus.first
            wheelStatusText.setTextColor(overallStatus.second)
            
            // Mostrar valores
            camberValueText.text = "${String.format("%.2f", camber)}°"
            toeValueText.text = "${String.format("%.2f", toe)}°"
            casterValueText.text = "${String.format("%.2f", caster)}°"
            
            // Mostrar iconos de estado para cada valor
            camberStatusText.text = getStatusIcon(camber, -2f, 2f)
            toeStatusText.text = getStatusIcon(toe, -0.5f, 0.5f)
            casterStatusText.text = getStatusIcon(caster, 1f, 7f)
            
            // Caster solo para ruedas delanteras
            if (wheelName.contains("Delantera")) {
                casterLayout.visibility = View.VISIBLE
            } else {
                casterLayout.visibility = View.GONE
            }
            
            // Calcular desviación promedio
            val avgDeviation = (kotlin.math.abs(camber) + kotlin.math.abs(toe) + 
                               if (wheelName.contains("Delantera")) kotlin.math.abs(caster - 4f) else 0f) / 
                               if (wheelName.contains("Delantera")) 3 else 2
            
            deviationText.text = "Desviación promedio: ${String.format("%.1f", avgDeviation)}° (${getDeviationQuality(avgDeviation)})"
        }
        
        private fun determineWheelStatus(camber: Float, toe: Float, caster: Float): Pair<String, Int> {
            val context = itemView.context
            return when {
                kotlin.math.abs(camber) < 0.5f && kotlin.math.abs(toe) < 0.2f && (caster in 2f..6f) -> 
                    Pair("✅ EXCELENTE", context.getColor(android.R.color.holo_green_dark))
                kotlin.math.abs(camber) < 1f && kotlin.math.abs(toe) < 0.3f && (caster in 1.5f..6.5f) -> 
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
