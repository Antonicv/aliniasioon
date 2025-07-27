# 🔄 Nuevo Workflow de Medición - AliniaSoon

## 📋 **Resumen de Cambios Implementados**

### 🎯 **Características Principales:**
1. **Selección de 2 o 4 ruedas** - Medición flexible
2. **Rueda de referencia** - La primera rueda define TOE = 0°
3. **Mediciones relativas** - Todas las demás ruedas se miden respecto a la referencia
4. **Caster removido** - Solo camber y toe (más precisos)
5. **UI mejorada** - Workflow paso a paso claro

---

## 🚀 **Nuevo Flujo de Trabajo**

### **Paso 1: Selección de Ruedas**
```
┌─────────────────────────────────┐
│     AliniaSoon - Selección      │
├─────────────────────────────────┤
│                                 │
│  Selecciona cuántas ruedas      │
│  medir                          │
│                                 │
│  [🚗 2 Ruedas (Delanteras)]    │
│  [🚙 4 Ruedas (Completo)]      │
│                                 │
└─────────────────────────────────┘
```

**Opciones:**
- **2 Ruedas**: Solo delanteras (medición rápida)
- **4 Ruedas**: Medición completa

### **Paso 2: Selección de Rueda de Referencia**
```
┌─────────────────────────────────┐
│    Selecciona Rueda Referencia  │
├─────────────────────────────────┤
│                                 │
│  Esta será TOE = 0°             │
│                                 │
│  [ Delantera Izquierda ]        │
│  [ Delantera Derecha ]          │
│  [ Trasera Izquierda ]   (4R)   │
│  [ Trasera Derecha ]     (4R)   │
│                                 │
└─────────────────────────────────┘
```

**Funcionalidad:**
- La rueda seleccionada se mide primero
- Su valor de TOE se establece como 0°
- Las demás ruedas se miden relativamente

### **Paso 3: Medición Secuencial**
```
┌─────────────────────────────────┐
│   1/2 - Delantera Izq (REF)    │
├─────────────────────────────────┤
│  🎯 RUEDA DE REFERENCIA         │
│  Esta rueda define TOE = 0°     │
│                                 │
│  Camber: -1.2°                  │
│  TOE: 0.0° (referencia)         │
│  Caster: No disponible          │
│                                 │
│  [💾 Guardar Medición]          │
└─────────────────────────────────┘
```

```
┌─────────────────────────────────┐
│   2/2 - Delantera Derecha      │
├─────────────────────────────────┤
│  📐 Relativo a referencia       │
│                                 │
│  Camber: -0.8°                  │
│  TOE: +0.3° (rel. a ref)        │
│  Caster: No disponible          │
│                                 │
│  [💾 Guardar Medición]          │
└─────────────────────────────────┘
```

---

## ⚙️ **Implementación Técnica**

### **Variables de Estado:**
```kotlin
// Configuración del workflow
private var selectedWheelsCount = 4 // 2 or 4
private var referenceWheelIndex = 0 // Índice de rueda referencia
private var selectedWheels = mutableListOf<String>() // Ruedas a medir
private var isWheelSelectionMode = true // Modo selección vs medición

// Medición de referencia
private var referenceToeMeasurement = 0f // Valor TOE de referencia
```

### **Métodos Principales:**
```kotlin
// 1. Selección inicial
fun showWheelSelection() // 2 vs 4 ruedas
fun selectWheelCount(count: Int) // Guardar selección

// 2. Selección de referencia  
fun showReferenceWheelSelection() // Mostrar ruedas disponibles
fun selectReferenceWheel(index: Int) // Establecer referencia

// 3. Medición
fun updateWheelDisplay() // UI para rueda actual
fun saveMeasurement() // Guardar con ajuste relativo
```

### **Cálculo de TOE Relativo:**
```kotlin
private fun saveMeasurement() {
    val isReference = currentWheel == 0
    val adjustedMeasurement = currentLiveMeasurement.toMutableMap()
    
    if (isReference) {
        // Rueda de referencia: TOE = 0°
        referenceToeMeasurement = currentLiveMeasurement["toe"] ?: 0f
        adjustedMeasurement["toe"] = 0f
    } else {
        // Otras ruedas: TOE relativo
        val rawToe = currentLiveMeasurement["toe"] ?: 0f
        val relativeToe = rawToe - referenceToeMeasurement
        adjustedMeasurement["toe"] = relativeToe
    }
}
```

---

## 🎯 **Ventajas del Nuevo Sistema**

### **1. Medición más Precisa:**
- **Referencia consistente**: Una rueda como punto cero
- **Errores relativos**: Se cancelan inconsistencias del sensor
- **TOE más confiable**: Medición diferencial vs absoluta

### **2. Flexibilidad:**
- **2 ruedas**: Diagnóstico rápido delanteras
- **4 ruedas**: Análisis completo
- **Selección de referencia**: Usuario elige rueda más accesible

### **3. UI Mejorada:**
- **Workflow claro**: Paso a paso sin confusión
- **Indicadores visuales**: REF, relativo, progreso
- **Menos opciones**: Enfoque en camber/toe precisos

### **4. Eliminación de Caster:**
- **Más honesto**: No valores incorrectos
- **Documentación**: CALCULO_CASTER.md explica limitaciones
- **Enfoque**: Camber/toe que sí son medibles con precisión

---

## 📊 **Interpretación de Resultados**

### **TOE con Sistema de Referencia:**
```
Rueda Referencia:     TOE = 0.0°  (por definición)
Rueda Delantera Der:  TOE = +0.3° (0.3° más "toe-in" que referencia)
Rueda Trasera Izq:    TOE = -0.1° (0.1° más "toe-out" que referencia)  
Rueda Trasera Der:    TOE = +0.2° (0.2° más "toe-in" que referencia)
```

**Interpretación:**
- **Positivo (+)**: Rueda apunta más hacia adentro que referencia
- **Negativo (-)**: Rueda apunta más hacia afuera que referencia
- **Cerca de 0**: Rueda alineada similar a referencia

### **Recomendaciones Mejoradas:**
- **Diferencias grandes** (>±0.5°): Requiere ajuste
- **Lado a lado**: Comparar izquierda vs derecha
- **Patrón**: Identificar tendencias (todas hacia un lado)

---

## 🔧 **Archivos Modificados**

### **MeasurementActivity.kt:**
- ✅ Workflow de selección implementado
- ✅ Sistema de rueda de referencia
- ✅ Cálculo de TOE relativo
- ✅ Caster removido completamente
- ✅ UI actualizada para nuevo flujo

### **Documentación:**
- ✅ **CALCULO_CASTER.md**: Por qué no funciona caster
- ✅ **NUEVO_WORKFLOW.md**: Este archivo explicativo

---

## 🎉 **Estado Actual**

### ✅ **Completado:**
1. Selección de 2 o 4 ruedas
2. Selección de rueda de referencia  
3. TOE relativo (referencia = 0°)
4. Caster removido
5. Documentación técnica

### 🔄 **Próximos Pasos Sugeridos:**
1. **Métricas visuales** en ResultsActivity
2. **Gráficos** de alineación
3. **Comparación lado a lado**
4. **Histórico** de mediciones
5. **Exportar** resultados

---

## 💡 **Beneficios para el Usuario**

### **Mecánico Profesional:**
- Medición rápida de 2 ruedas para diagnóstico
- Referencia consistente para comparación
- Resultados más confiables

### **Entusiasta:**
- Proceso guiado paso a paso
- Entendimiento claro de cada medición
- Sin valores confusos (caster eliminado)

### **Taller Pequeño:**
- Herramienta práctica para pre-diagnóstico
- Documentación clara de limitaciones
- Base para decidir si necesita alineación profesional

El nuevo workflow hace que **AliniaSoon** sea más precisa, honesta y fácil de usar! 🎯
