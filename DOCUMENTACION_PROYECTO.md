# Documentación del Proyecto: AliniaSoon - App Android

## 1. RESUMEN EJECUTIVO

### Concepto
Aplicación móvil Android que convierte el smartphone en una herramienta profesional de alineación de ruedas, utilizando los sensores integrados del dispositivo para medir ángulos de camber, toe y caster con precisión comparable a equipos especializados.

### Objetivo Principal
Democratizar el proceso de alineación de ruedas, permitiendo a mecánicos, talleres pequeños y entusiastas del motor realizar mediciones precisas sin necesidad de equipamiento costoso.

## 2. ANÁLISIS TÉCNICO DETALLADO

### 2.1 Sensores del Móvil Utilizados

#### Acelerómetro
- **Función**: Detectar la orientación gravitacional
- **Uso**: Medir ángulos de inclinación (camber)
- **Precisión típica**: ±0.1° con calibración adecuada
- **Ventajas**: Presente en todos los dispositivos Android modernos

#### Giroscopio
- **Función**: Detectar velocidad angular y rotación
- **Uso**: Estabilizar mediciones y detectar movimientos no deseados
- **Beneficio**: Compensar vibraciones menores durante la medición

#### Magnetómetro (Brújula)
- **Función**: Detectar orientación magnética
- **Uso**: Referencia direccional para medidas de toe
- **Consideración**: Puede verse afectado por componentes metálicos del vehículo

### 2.2 Mediciones Requeridas

#### Camber (Ángulo de Caída)
- **Definición**: Inclinación de la rueda vista desde el frente del vehículo
- **Rango típico**: -3° a +3°
- **Método de medición**: Acelerómetro en posición vertical contra la rueda
- **Precisión requerida**: ±0.1°

#### Toe (Convergencia/Divergencia)
- **Definición**: Ángulo de las ruedas visto desde arriba
- **Medición**: Diferencia angular entre ruedas del mismo eje
- **Método**: Comparación de orientaciones magnetométricas o uso de referencias visuales
- **Precisión requerida**: ±0.05°

#### Caster (Ángulo de Avance)
- **Definición**: Inclinación del eje de dirección vista lateralmente
- **Complejidad**: Requiere medición durante giro de la dirección
- **Método**: Medición de cambio angular al girar la rueda 20° a cada lado
- **Precisión requerida**: ±0.1°

## 3. ARQUITECTURA DE LA APLICACIÓN

### 3.1 Estructura Modular

```
App Alineación Ruedas/
├── Core/
│   ├── SensorManager
│   ├── CalibrationEngine
│   └── MeasurementProcessor
├── UI/
│   ├── MainActivity
│   ├── CalibrationActivity
│   ├── MeasurementActivity
│   └── ResultsActivity
├── Data/
│   ├── VehicleDatabase
│   ├── MeasurementHistory
│   └── ConfigurationSettings
└── Utils/
    ├── MathUtils
    ├── FilterUtils
    └── ExportUtils
```

### 3.2 Componentes Principales

#### SensorManager
- Gestión de sensores del dispositivo
- Filtrado de ruido y estabilización de datos
- Calibración automática y manual
- Detección de movimiento no deseado

#### CalibrationEngine
- Calibración de sensores antes de cada sesión
- Compensación de irregularidades del suelo
- Establecimiento de referencias de nivel
- Validación de la posición del dispositivo

#### MeasurementProcessor
- Algoritmos de cálculo de ángulos
- Procesamiento de datos en tiempo real
- Aplicación de filtros digitales
- Generación de resultados finales

## 4. FLUJO DE TRABAJO DE LA APLICACIÓN

### 4.1 Proceso de Calibración Inicial
1. **Verificación del dispositivo**: Comprobar disponibilidad de sensores
2. **Calibración de superficie**: Establecer referencia de nivel usando superficie conocida
3. **Calibración de sensores**: Ajustar offset y sensibilidad de sensores
4. **Validación**: Confirmar precisión con mediciones de referencia

### 4.2 Proceso de Medición

#### Preparación
1. Seleccionar vehículo o introducir especificaciones
2. Verificar que el vehículo esté en superficie nivelada
3. Verificar presión de neumáticos
4. Colocar la pieza guía en la rueda

#### Medición por Rueda
1. **Posicionamiento**: Colocar móvil en pieza guía contra la rueda
2. **Estabilización**: Esperar a que los sensores se estabilicen
3. **Captura**: Tomar medición cuando el sistema confirme estabilidad
4. **Validación**: Repetir medición para confirmar consistencia
5. **Almacenamiento**: Guardar datos con timestamp y referencia de rueda

#### Cálculo de Caster (Proceso Especial)
1. Centrar la dirección y medir posición inicial
2. Girar rueda 20° a la izquierda y medir
3. Girar rueda 20° a la derecha y medir
4. Calcular caster basado en la diferencia de camber

### 4.3 Análisis y Resultados
1. **Comparación con especificaciones**: Contrastar con valores de fábrica
2. **Identificación de problemas**: Detectar desalineaciones significativas
3. **Recomendaciones**: Sugerir ajustes necesarios
4. **Generación de reporte**: Crear informe detallado con gráficos

## 5. INTERFAZ DE USUARIO

### 5.1 Pantalla Principal
- Dashboard con acceso rápido a funciones principales
- Historial de mediciones recientes
- Estado de calibración del dispositivo
- Acceso a configuraciones y ayuda

### 5.2 Asistente de Medición
- Guía paso a paso para el proceso completo
- Indicadores visuales de posicionamiento correcto
- Alertas de movimiento o problemas de estabilidad
- Progreso visual del proceso de medición

### 5.3 Visualización de Resultados
- Gráficos intuitivos de ángulos medidos
- Comparación con especificaciones del vehículo
- Códigos de color para identificar problemas (verde/amarillo/rojo)
- Recomendaciones textuales claras

## 6. ESPECIFICACIONES TÉCNICAS

### 6.1 Requerimientos de Hardware
- **Android**: Versión 6.0 (API 23) o superior
- **Sensores obligatorios**: Acelerómetro, Giroscopio
- **Sensores opcionales**: Magnetómetro (mejora precisión de toe)
- **RAM**: Mínimo 2GB recomendado
- **Almacenamiento**: 100MB para app + datos

### 6.2 Precisión Esperada
- **Camber**: ±0.1° (comparable a equipos básicos profesionales)
- **Toe**: ±0.05° por rueda
- **Caster**: ±0.2° (mayor tolerancia debido a complejidad del proceso)

### 6.3 Tiempo de Medición
- **Por rueda**: 30-60 segundos
- **Vehículo completo**: 5-8 minutos incluyendo setup
- **Calibración inicial**: 2-3 minutos

## 7. ALGORITMOS CLAVE

### 7.1 Filtro de Kalman
- Implementación para fusión de datos de múltiples sensores
- Reducción de ruido y mejora de estabilidad
- Predicción y corrección de mediciones en tiempo real

### 7.2 Compensación de Deriva
- Algoritmo para detectar y corregir deriva de sensores
- Recalibración automática durante mediciones largas
- Validación cruzada entre sensores

### 7.3 Detección de Movimiento
- Algoritmo para detectar cuando el dispositivo está estable
- Umbral adaptativo basado en condiciones ambientales
- Invalidación automática de mediciones durante movimiento

## 8. BASE DE DATOS DE VEHÍCULOS

### 8.1 Información Almacenada
- Especificaciones de alineación por marca/modelo/año
- Tolerancias aceptables para cada parámetro
- Procedimientos específicos cuando sea necesario
- Actualizaciones periódicas desde servidor

### 8.2 Estructura de Datos
```
Vehículo {
    id: string
    marca: string
    modelo: string
    año: int
    camber_delantero: {min, max, ideal}
    camber_trasero: {min, max, ideal}
    toe_delantero: {min, max, ideal}
    toe_trasero: {min, max, ideal}
    caster: {min, max, ideal}
    notas_especiales: string
}
```

## 9. SISTEMA DE CALIBRACIÓN

### 9.1 Calibración de Fábrica
- Valores predeterminados basados en sensores típicos
- Compensación de sesgo común de acelerómetros
- Algoritmo de auto-corrección inicial

### 9.2 Calibración de Campo
- Proceso guiado para establecer referencias locales
- Uso de superficies conocidas como referencia
- Corrección de irregularidades del terreno de trabajo

### 9.3 Validación de Calibración
- Pruebas automáticas de consistencia
- Comparación con mediciones de referencia conocidas
- Alertas cuando la calibración parece incorrecta

## 10. CONSIDERACIONES DE PRECISIÓN

### 10.1 Factores que Afectan la Precisión

#### Factores del Dispositivo
- Calidad y calibración de sensores
- Temperatura del dispositivo
- Interferencias electromagnéticas
- Edad y desgaste de componentes

#### Factores Ambientales
- Vibrations del entorno
- Temperatura ambiente
- Campos magnéticos cercanos
- Superficie de trabajo no nivelada

#### Factores de Uso
- Calidad de la pieza guía utilizada
- Estabilidad durante la medición
- Correcta colocación del dispositivo
- Estado de los neumáticos del vehículo

### 10.2 Estrategias de Mejora
- Promediado de múltiples mediciones
- Filtrado digital avanzado
- Compensación automática de temperatura
- Validación cruzada entre sensores

## 11. PIEZA GUÍA RECOMENDADA

### 11.1 Especificaciones
- **Material**: Aluminio o acero mecanizado
- **Dimensiones**: 200mm x 100mm x 10mm
- **Superficie**: Plana con tolerancia ±0.1mm
- **Bordes**: Rectos y perpendiculares
- **Peso**: 200-300g para estabilidad

### 11.2 Características de Diseño
- Base magnética opcional para neumáticos de acero
- Ranuras para diferentes tamaños de dispositivo
- Nivel de burbuja integrado como referencia visual
- Superficies antideslizantes

### 11.3 Alternativas
- Uso de regla de albañil de calidad (económica)
- Pieza personalizada impresa en 3D
- Adaptador universal comercial

## 12. PLAN DE DESARROLLO

### 12.1 Fase 1: Prototipo Básico (4-6 semanas)
- Lectura básica de sensores
- Interfaz simple de medición
- Algoritmos fundamentales de cálculo
- Pruebas de concepto

### 12.2 Fase 2: Funcionalidad Completa (6-8 semanas)
- Implementación de todos los ángulos
- Sistema de calibración
- Base de datos de vehículos básica
- Interfaz completa

### 12.3 Fase 3: Optimización (4-6 semanas)
- Mejora de algoritmos
- Optimización de precisión
- Pruebas extensivas en campo
- Refinamiento de UI/UX

### 12.4 Fase 4: Lanzamiento (2-4 semanas)
- Testing final
- Documentación de usuario
- Preparación para distribución
- Marketing y lanzamiento

## 13. MONETIZACIÓN Y DISTRIBUCIÓN

### 13.1 Modelos de Negocio
- **Freemium**: Funcionalidad básica gratuita, funciones avanzadas de pago
- **Suscripción**: Acceso completo por cuota mensual/anual
- **Compra única**: Pago único por aplicación completa
- **B2B**: Licencias para talleres y concesionarios

### 13.2 Funciones Premium Potenciales
- Base de datos extendida de vehículos
- Generación de reportes profesionales
- Sincronización en la nube
- Soporte técnico prioritario
- Actualizaciones automáticas de especificaciones

## 14. CONSIDERACIONES LEGALES Y DE SEGURIDAD

### 14.1 Limitaciones de Responsabilidad
- Disclaimers claros sobre precisión
- Recomendación de verificación profesional
- Limitación de uso a fines informativos
- Términos de uso específicos

### 14.2 Certificaciones
- Investigar requerimientos de certificación para herramientas automotrices
- Cumplimiento con estándares de la industria
- Validación por organizaciones técnicas reconocidas

## 15. FUTURAS EXPANSIONES

### 15.1 Funcionalidades Adicionales
- Medición de altura de vehículo
- Análisis de desgaste de neumáticos
- Integración con sistemas de gestión de taller
- Realidad aumentada para guías visuales

### 15.2 Plataformas Adicionales
- Versión iOS
- Aplicación web complementaria
- API para integración con terceros

Este documento proporciona una base sólida para el desarrollo de la aplicación de alineación de ruedas, considerando tanto los aspectos técnicos como comerciales del proyecto.
