package com.alineacion.ruedas.ui.calibration;

/**
 * Actividad de calibración del sistema de alineación
 *
 * Esta actividad guía al usuario a través del proceso de calibración
 * de los sensores para obtener lecturas precisas.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u0014\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\"\u001a\u00020#H\u0002J\b\u0010$\u001a\u00020#H\u0002J\b\u0010%\u001a\u00020#H\u0002J\b\u0010&\u001a\u00020#H\u0002J\b\u0010\'\u001a\u00020#H\u0002J\u001a\u0010(\u001a\u00020#2\b\u0010)\u001a\u0004\u0018\u00010\u00052\u0006\u0010*\u001a\u00020\u0011H\u0016J\u0012\u0010+\u001a\u00020#2\b\u0010,\u001a\u0004\u0018\u00010-H\u0014J\b\u0010.\u001a\u00020#H\u0014J\b\u0010/\u001a\u00020#H\u0014J\b\u00100\u001a\u00020#H\u0014J\u0012\u00101\u001a\u00020#2\b\u00102\u001a\u0004\u0018\u000103H\u0016J\b\u00104\u001a\u00020\u0018H\u0016J\b\u00105\u001a\u00020#H\u0002J\b\u00106\u001a\u00020#H\u0002J\b\u00107\u001a\u00020#H\u0002J\b\u00108\u001a\u00020#H\u0002J\u0010\u00109\u001a\u00020#2\u0006\u0010:\u001a\u00020\rH\u0002J\b\u0010;\u001a\u00020#H\u0002J\b\u0010<\u001a\u00020#H\u0002J\b\u0010=\u001a\u00020#H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006>"}, d2 = {"Lcom/alineacion/ruedas/ui/calibration/CalibrationActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Landroid/hardware/SensorEventListener;", "()V", "accelerometer", "Landroid/hardware/Sensor;", "calibrationData", "", "", "calibrationEngine", "Lcom/alineacion/ruedas/core/CalibrationEngine;", "calibrationSteps", "", "", "cancelButton", "Lcom/google/android/material/button/MaterialButton;", "currentStep", "", "gyroscope", "handler", "Landroid/os/Handler;", "instructionText", "Lcom/google/android/material/textview/MaterialTextView;", "isCollectingData", "", "nextButton", "progressBar", "Landroid/widget/ProgressBar;", "sensorManager", "Landroid/hardware/SensorManager;", "statusCard", "Lcom/google/android/material/card/MaterialCardView;", "statusText", "titleText", "collectLeftTiltData", "", "collectReferenceData", "collectRightTiltData", "finalizeCalibration", "nextStep", "onAccuracyChanged", "sensor", "accuracy", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onPause", "onResume", "onSensorChanged", "event", "Landroid/hardware/SensorEvent;", "onSupportNavigateUp", "registerSensorListeners", "setupCalibration", "setupSensors", "setupViews", "showError", "message", "startCalibrationProcess", "startDataCollection", "unregisterSensorListeners", "app_debug"})
public final class CalibrationActivity extends androidx.appcompat.app.AppCompatActivity implements android.hardware.SensorEventListener {
    private com.alineacion.ruedas.core.CalibrationEngine calibrationEngine;
    private android.hardware.SensorManager sensorManager;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.Sensor accelerometer;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.Sensor gyroscope;
    private com.google.android.material.textview.MaterialTextView titleText;
    private com.google.android.material.textview.MaterialTextView instructionText;
    private com.google.android.material.textview.MaterialTextView statusText;
    private android.widget.ProgressBar progressBar;
    private com.google.android.material.button.MaterialButton nextButton;
    private com.google.android.material.button.MaterialButton cancelButton;
    private com.google.android.material.card.MaterialCardView statusCard;
    private int currentStep = 0;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<float[]> calibrationData;
    private boolean isCollectingData = false;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> calibrationSteps = null;
    
    public CalibrationActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupViews() {
    }
    
    private final void setupSensors() {
    }
    
    private final void setupCalibration() {
    }
    
    private final void startCalibrationProcess() {
    }
    
    private final void nextStep() {
    }
    
    private final void startDataCollection() {
    }
    
    private final void collectReferenceData() {
    }
    
    private final void collectLeftTiltData() {
    }
    
    private final void collectRightTiltData() {
    }
    
    private final void finalizeCalibration() {
    }
    
    private final void registerSensorListeners() {
    }
    
    private final void unregisterSensorListeners() {
    }
    
    @java.lang.Override()
    public void onSensorChanged(@org.jetbrains.annotations.Nullable()
    android.hardware.SensorEvent event) {
    }
    
    @java.lang.Override()
    public void onAccuracyChanged(@org.jetbrains.annotations.Nullable()
    android.hardware.Sensor sensor, int accuracy) {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}