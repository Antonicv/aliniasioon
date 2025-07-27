package com.alineacion.ruedas.core;

/**
 * Estados posibles del proceso de calibración
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lcom/alineacion/ruedas/core/CalibrationState;", "", "(Ljava/lang/String;I)V", "NOT_STARTED", "CHECKING_SENSORS", "SURFACE_REFERENCE", "SENSOR_OFFSET", "VALIDATION", "COMPLETED", "ERROR", "app_debug"})
public enum CalibrationState {
    /*public static final*/ NOT_STARTED /* = new NOT_STARTED() */,
    /*public static final*/ CHECKING_SENSORS /* = new CHECKING_SENSORS() */,
    /*public static final*/ SURFACE_REFERENCE /* = new SURFACE_REFERENCE() */,
    /*public static final*/ SENSOR_OFFSET /* = new SENSOR_OFFSET() */,
    /*public static final*/ VALIDATION /* = new VALIDATION() */,
    /*public static final*/ COMPLETED /* = new COMPLETED() */,
    /*public static final*/ ERROR /* = new ERROR() */;
    
    CalibrationState() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.alineacion.ruedas.core.CalibrationState> getEntries() {
        return null;
    }
}