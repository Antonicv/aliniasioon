# Alineación de Ruedas - Android App

## Descripción

Aplicación Android que convierte el smartphone en una herramienta profesional de alineación de ruedas, utilizando los sensores integrados del dispositivo para medir ángulos de camber, toe y caster con precisión comparable a equipos especializados.

## Características Principales

### 🎯 Mediciones Precisas
- **Camber**: Ángulo de inclinación lateral de la rueda (±0.1° precisión)
- **Toe**: Convergencia/divergencia de ruedas (±0.05° precisión)
- **Caster**: Ángulo de avance del eje de dirección (±0.2° precisión)

### 📱 Tecnología de Sensores
- Utiliza acelerómetro, giroscopio y magnetómetro
- Filtrado de Kalman para estabilización de datos
- Sistema de calibración multi-nivel
- Detección automática de estabilidad

### 🛠️ Funcionalidades
- Calibración automática de sensores
- Base de datos de especificaciones por vehículo
- Historial de mediciones
- Reportes detallados con gráficos
- Interfaz intuitiva paso a paso

## Requerimientos del Sistema

### Hardware Mínimo
- **Android**: 6.0 (API 23) o superior
- **Sensores obligatorios**: Acelerómetro, Giroscopio
- **Sensores opcionales**: Magnetómetro (mejora precisión)
- **RAM**: 2GB recomendado
- **Almacenamiento**: 100MB

### Pieza Guía Recomendada
- Material: Aluminio o acero mecanizado
- Dimensiones: 200mm x 100mm x 10mm
- Superficie plana con tolerancia ±0.1mm
- Alternativa económica: Regla de albañil de calidad

## Instalación y Configuración

### Prerrequisitos
1. Android Studio Arctic Fox o superior
2. SDK Android 23-34
3. Kotlin 1.9.20 o superior

### Pasos de Instalación
```bash
1. Clonar el repositorio
git clone [URL_DEL_REPOSITORIO]

2. Abrir en Android Studio
File > Open > Seleccionar carpeta del proyecto

3. Sincronizar dependencias
Build > Clean Project
Build > Rebuild Project

4. Ejecutar en dispositivo/emulador
Run > Run 'app'
```

## Estructura del Proyecto

```
app/
├── src/main/java/com/alineacion/ruedas/
│   ├── core/                          # Lógica principal
│   │   ├── WheelAlignmentSensorManager.kt
│   │   ├── CalibrationEngine.kt
│   │   └── MeasurementProcessor.kt
│   ├── ui/                            # Interfaz de usuario
│   │   ├── MainActivity.kt
│   │   ├── calibration/
│   │   ├── measurement/
│   │   └── results/
│   ├── data/                          # Gestión de datos
│   │   ├── database/
│   │   ├── repository/
│   │   └── models/
│   └── utils/                         # Utilidades
├── res/
│   ├── layout/                        # Layouts XML
│   ├── values/                        # Strings, colores, estilos
│   └── drawable/                      # Recursos gráficos
└── AndroidManifest.xml
```

## Uso de la Aplicación

### 1. Verificación de Sensores
- La app verifica automáticamente la disponibilidad de sensores
- Muestra estado de compatibilidad en pantalla principal

### 2. Calibración Inicial
- **Obligatorio antes de medir**
- Colocar dispositivo en superficie nivelada
- Seguir instrucciones paso a paso
- Tiempo estimado: 2-3 minutos

### 3. Proceso de Medición
- Seleccionar vehículo o introducir especificaciones
- Colocar pieza guía contra la rueda
- Posicionar smartphone en pieza guía
- Esperar estabilización automática
- Tomar medición cuando se indique

### 4. Análisis de Resultados
- Visualización inmediata de mediciones
- Comparación con especificaciones del vehículo
- Códigos de color: Verde (OK), Amarillo (Atención), Rojo (Fuera de spec)
- Recomendaciones de ajuste

## Tecnologías Utilizadas

### Principales
- **Kotlin**: Lenguaje principal
- **Android Architecture Components**: MVVM, LiveData, ViewModel
- **Room Database**: Almacenamiento local
- **Coroutines**: Programación asíncrona
- **Material Design 3**: Interfaz moderna

### Algoritmos
- **Filtro de Kalman**: Estabilización de mediciones
- **Matrices de Rotación**: Cálculos de orientación
- **Filtros Pasa-bajas**: Reducción de ruido
- **Promediado Móvil**: Suavizado de datos

### Bibliotecas
- AndroidX Core y AppCompat
- Material Components
- Navigation Component
- MPAndroidChart (gráficos)
- EasyPermissions

## Estado de Desarrollo

### ✅ Completado
- [x] Estructura básica del proyecto
- [x] Gestión de sensores
- [x] Motor de calibración
- [x] Procesador de mediciones
- [x] Interfaz principal
- [x] Sistema de filtrado y estabilización

### 🚧 En Desarrollo
- [ ] Actividad de calibración completa
- [ ] Actividad de medición
- [ ] Base de datos de vehículos
- [ ] Sistema de reportes
- [ ] Historial de mediciones

### 📋 Por Implementar
- [ ] Cálculo de caster
- [ ] Exportación de datos
- [ ] Configuraciones avanzadas
- [ ] Tutorial interactivo
- [ ] Optimizaciones de rendimiento

## Contribución

### Proceso de Desarrollo
1. Fork del repositorio
2. Crear branch para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

### Estándares de Código
- Seguir convenciones de Kotlin
- Documentar funciones públicas
- Incluir tests unitarios
- Mantener cobertura de tests >80%

## Precisión y Limitaciones

### Precisión Esperada
- **Camber**: ±0.1° (comparable a equipos básicos profesionales)
- **Toe**: ±0.05° por rueda
- **Caster**: ±0.2° (requiere proceso especial)

### Factores que Afectan Precisión
- Calidad de sensores del dispositivo
- Estabilidad durante medición
- Calidad de la pieza guía
- Condiciones ambientales
- Estado de neumáticos

### Limitaciones
- No reemplaza calibración profesional para aplicaciones críticas
- Requiere pieza guía para mejores resultados
- Precisión puede variar entre dispositivos
- Magnetómetro puede verse afectado por metales cercanos

## Licencia

[Especificar licencia aquí]

## Contacto y Soporte

- **Desarrollador**: [Nombre]
- **Email**: [email@ejemplo.com]
- **Issues**: [URL del repositorio]/issues

## Reconocimientos

- Comunidad Android por bibliotecas y recursos
- Documentación de sensores Android
- Algoritmos de filtrado de señales digitales

---

**Nota**: Esta aplicación está diseñada como herramienta de apoyo. Para aplicaciones profesionales críticas, siempre verifique con equipamiento profesional certificado.
