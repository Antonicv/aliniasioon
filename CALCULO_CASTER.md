# 📐 Cálculo Correcto del Caster - Documentación Técnica

## 🎯 ¿Qué es el Caster?

El **caster** es el ángulo de inclinación del eje de dirección (kingpin) cuando se ve desde el lateral del vehículo. Es uno de los tres ángulos principales de alineación junto con camber y toe.

### 📊 Características del Caster:
- **Positivo**: Eje inclinado hacia atrás (parte superior más atrás que la inferior)
- **Negativo**: Eje inclinado hacia adelante (parte superior más adelante que la inferior)
- **Rango típico**: 1° a 7° positivo (la mayoría de vehículos modernos: 3-5°)

## 🔬 Métodos de Medición Correctos

### 🏆 **Método 1: Medición Directa del Kingpin (Profesional)**

```
Equipamiento requerido:
- Alineador profesional con cabezales en las ruedas
- Sensores angulares de precisión
- Sistema de referencia láser o mecánico

Proceso:
1. Montar cabezales en las 4 ruedas
2. Establecer línea de empuje del vehículo
3. Girar volante 20° a izquierda, medir camber
4. Girar volante 20° a derecha, medir camber
5. Caster = (Camber_izq - Camber_der) / (Sen(20°) * 2)
```

### 🎯 **Método 2: Método de Giro de Rueda (Semi-profesional)**

```
Equipamiento requerido:
- Inclinómetro digital de alta precisión
- Platos giratorios para ruedas delanteras
- Superficie nivelada

Proceso:
1. Nivelar vehículo en superficie plana
2. Colocar inclinómetro en rueda delantera
3. Girar rueda 20° hacia afuera, medir inclinación
4. Girar rueda 20° hacia adentro, medir inclinación
5. Aplicar fórmula trigonométrica para obtener caster
```

### 📱 **Método 3: Smartphone con Adaptaciones (Limitado)**

```
Equipamiento requerido:
- Smartphone con giroscopio y acelerómetro
- Soporte rígido para fijar a la rueda
- App con cálculos trigonométricos avanzados
- Platos giratorios o sistema para girar ruedas

Limitaciones:
- Precisión limitada (±1-2°)
- Requiere múltiples mediciones
- Sensible a vibaciones y movimientos
- No considera geometría exacta del kingpin
```

## 🧮 Fórmulas y Cálculos

### **Fórmula Principal del Caster:**
```
Caster = arctan((Camber_20°_out - Camber_20°_in) / sin(40°))

Donde:
- Camber_20°_out: Camber con rueda girada 20° hacia afuera
- Camber_20°_in: Camber con rueda girada 20° hacia adentro
- 40° = diferencia total de giro (20° + 20°)
```

### **Método Simplificado (menos preciso):**
```
Caster ≈ (Camber_20°_out - Camber_20°_in) / 0.643

Donde 0.643 = sin(40°)
```

### **Consideraciones Geométricas:**
```
1. Offset del kingpin respecto al centro de la rueda
2. Ángulo SAI (Steering Axis Inclination)
3. Radio de scrub y trail
4. Geometría específica de suspensión
```

## ⚠️ **Por Qué NO Funciona con Solo Acelerómetro**

### **Limitaciones Físicas:**
1. **El caster NO es una inclinación directa** que el acelerómetro pueda medir
2. **Requiere movimiento de dirección** para ser calculado correctamente
3. **Depende de la geometría específica** del eje de dirección (kingpin)
4. **No es una medición estática** como el camber

### **Problemas del Método Actual:**
```kotlin
// ❌ INCORRECTO - No mide caster real
private fun calculateCaster(x: Float, y: Float, z: Float): Float {
    return Math.toDegrees(atan2(y.toDouble(), sqrt((x*x + z*z).toDouble()))).toFloat()
}

// ❌ INCORRECTO - Diferencia de camber no es caster
val angleDiff = currentWheel["camber"]!! - rearLeft["camber"]!!
return 3.2f + (angleDiff * 0.5f)
```

## 🛠️ **Implementación Correcta para App**

### **Opción 1: Método de Giro Asistido**
```kotlin
class CasterMeasurement {
    fun measureCasterWithTurning(): Float {
        // 1. Medir camber con rueda recta
        val camberStraight = measureCamber()
        
        // 2. Pedir al usuario girar volante 20° izquierda
        val camberLeft = measureCamberAfterTurn(-20f)
        
        // 3. Pedir al usuario girar volante 20° derecha
        val camberRight = measureCamberAfterTurn(20f)
        
        // 4. Calcular caster usando fórmula trigonométrica
        return calculateCasterFromCamberChange(camberLeft, camberRight)
    }
    
    private fun calculateCasterFromCamberChange(
        camberLeft: Float, 
        camberRight: Float
    ): Float {
        val camberDifference = camberLeft - camberRight
        val turnAngle = 40f // 20° + 20°
        return Math.toDegrees(
            atan(camberDifference / sin(Math.toRadians(turnAngle.toDouble())))
        ).toFloat()
    }
}
```

### **Opción 2: Medición con Referencias Externas**
```kotlin
class CasterWithReference {
    fun measureCasterWithVehicleReference(): Float {
        // Requiere conocer:
        // 1. Línea de empuje del vehículo
        // 2. Geometría específica de suspensión
        // 3. Múltiples puntos de referencia
        
        // No viable con solo smartphone
        return 0f // Placeholder
    }
}
```

## 📊 **Valores de Referencia por Tipo de Vehículo**

| **Tipo de Vehículo** | **Caster Típico** | **Tolerancia** |
|----------------------|-------------------|----------------|
| Sedanes/Compactos | 3.0° - 5.0° | ±0.5° |
| SUVs/Crossovers | 2.5° - 4.5° | ±0.5° |
| Deportivos | 4.0° - 7.0° | ±0.3° |
| Camiones | 2.0° - 4.0° | ±0.7° |
| Vehículos Clásicos | 0° - 3.0° | ±1.0° |

## 🎯 **Recomendación para AliniaSoon**

### **Implementación Sugerida:**
1. **Remover cálculo automático** del caster (actual implementación incorrecta)
2. **Agregar modo "Medición Asistida"** para caster:
   - Guiar al usuario para girar volante
   - Medir camber en 3 posiciones (centro, izq, der)
   - Calcular usando fórmula trigonométrica correcta
3. **Indicar limitaciones** claramente al usuario
4. **Proporcionar valores de referencia** por tipo de vehículo

### **Mensaje para el Usuario:**
```
⚠️ CASTER - Medición Avanzada
El caster requiere girar el volante y múltiples mediciones.
Para resultados profesionales, consulte un alineador especializado.

¿Desea intentar medición asistida? (Menos precisa)
[ Sí, continuar ] [ Omitir caster ]
```

## 📚 **Referencias Técnicas**

1. **SAE J1208** - Wheel Alignment Specifications
2. **NATEF/ASE** - Standards for Wheel Alignment
3. **Hunter Engineering** - Alignment Theory and Procedures
4. **Bee Line Company** - Wheel Alignment Guidelines

## 💡 **Conclusión**

El caster **NO puede medirse correctamente** con solo un acelerómetro estático. Requiere:
- Movimiento de dirección controlado
- Múltiples mediciones angulares
- Cálculos trigonométricos complejos
- Conocimiento de geometría vehicular específica

**Para AliniaSoon**: Enfocarse en **camber y toe** que sí pueden medirse con precisión usando sensores del smartphone.
