package com.alineacion.ruedas.ui.measurement;

/**
 * Actividad de medición de alineación de ruedas
 *
 * Permite al usuario medir los ángulos de alineación (camber, toe, caster)
 * usando los sensores calibrados del dispositivo.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u009c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010%\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u001b\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J \u0010<\u001a\u00020\u00122\u0006\u0010=\u001a\u00020\u00122\u0006\u0010>\u001a\u00020\u00122\u0006\u0010?\u001a\u00020\u0012H\u0002J\u0010\u0010@\u001a\u00020\u00122\u0006\u0010A\u001a\u00020\bH\u0002J\b\u0010B\u001a\u00020CH\u0002J\u0010\u0010D\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u000fH\u0002J\b\u0010E\u001a\u00020CH\u0002J\u001c\u0010F\u001a\u00020C2\u0012\u0010G\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00120\'H\u0002J\u001c\u0010H\u001a\u00020C2\u0012\u0010G\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00120\'H\u0002J\b\u0010I\u001a\u00020CH\u0002J \u0010J\u001a\u00020\u00152\u0006\u0010K\u001a\u00020\u00122\u0006\u0010L\u001a\u00020\u00122\u0006\u0010M\u001a\u00020\u0012H\u0002J\b\u0010N\u001a\u00020CH\u0002J\u001a\u0010O\u001a\u00020C2\b\u0010P\u001a\u0004\u0018\u00010\u00052\u0006\u0010Q\u001a\u00020\u0015H\u0016J\u0012\u0010R\u001a\u00020C2\b\u0010S\u001a\u0004\u0018\u00010TH\u0014J\b\u0010U\u001a\u00020CH\u0014J\b\u0010V\u001a\u00020CH\u0014J\b\u0010W\u001a\u00020CH\u0014J\u0012\u0010X\u001a\u00020C2\b\u0010Y\u001a\u0004\u0018\u00010ZH\u0016J\b\u0010[\u001a\u00020\u001cH\u0016J\u000e\u0010\\\u001a\u00020CH\u0082@\u00a2\u0006\u0002\u0010]J\"\u0010^\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00120\'2\f\u0010_\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007H\u0002J\b\u0010`\u001a\u00020CH\u0002J\b\u0010a\u001a\u00020CH\u0002J\u0010\u0010b\u001a\u00020C2\u0006\u0010c\u001a\u00020\u0015H\u0002J\u0010\u0010d\u001a\u00020C2\u0006\u0010e\u001a\u00020\u0015H\u0002J\b\u0010f\u001a\u00020CH\u0002J\b\u0010g\u001a\u00020CH\u0002J\b\u0010h\u001a\u00020CH\u0002J\u0010\u0010i\u001a\u00020C2\u0006\u0010j\u001a\u00020\bH\u0002J\b\u0010k\u001a\u00020CH\u0002J\b\u0010l\u001a\u00020CH\u0002J\b\u0010m\u001a\u00020CH\u0002J\b\u0010n\u001a\u00020CH\u0002J\b\u0010o\u001a\u00020CH\u0002J\b\u0010p\u001a\u00020CH\u0002J(\u0010q\u001a\u00020C2\u0006\u0010r\u001a\u00020\f2\u0006\u0010K\u001a\u00020\u00122\u0006\u0010L\u001a\u00020\u00122\u0006\u0010M\u001a\u00020\u0012H\u0002J\b\u0010s\u001a\u00020CH\u0002J\u000e\u0010t\u001a\u00020\u001cH\u0082@\u00a2\u0006\u0002\u0010]R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u0082.\u00a2\u0006\u0002\n\u0000R&\u0010&\u001a\u001a\u0012\u0004\u0012\u00020\b\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00120\'0\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020#X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010.\u001a\b\u0012\u0004\u0012\u00020\b0/X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u000202X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00106\u001a\u000207X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u00108\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u00109\u001a\b\u0012\u0004\u0012\u00020\n0\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010:\u001a\u00020#X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010;\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006u"}, d2 = {"Lcom/alineacion/ruedas/ui/measurement/MeasurementActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Landroid/hardware/SensorEventListener;", "()V", "accelerometer", "Landroid/hardware/Sensor;", "allWheels", "", "", "backButton", "Lcom/google/android/material/button/MaterialButton;", "camberValue", "Lcom/google/android/material/textview/MaterialTextView;", "casterValue", "currentAccelerometer", "", "currentLiveMeasurement", "", "", "currentMagnetometer", "currentWheel", "", "finishButton", "gyroscope", "handler", "Landroid/os/Handler;", "instructionText", "isContinuousMeasuring", "", "isMeasuring", "isStable", "isWheelSelectionMode", "magnetometer", "measureButton", "measurementCard", "Lcom/google/android/material/card/MaterialCardView;", "measurementProcessor", "Lcom/alineacion/ruedas/core/MeasurementProcessor;", "measurementResults", "", "nextWheelButton", "referenceToeMeasurement", "referenceWheelIndex", "resultsCard", "select2WheelsButton", "select4WheelsButton", "selectedWheels", "", "selectedWheelsCount", "sensorManager", "Landroid/hardware/SensorManager;", "startMeasurementButton", "statusText", "toeValue", "wheelAlignmentSensorManager", "Lcom/alineacion/ruedas/core/WheelAlignmentSensorManager;", "wheelInstructions", "wheelSelectionButtons", "wheelSelectionCard", "wheelTitle", "calculateCamber", "x", "y", "z", "calculateCaster", "wheelName", "calculateCasterForAllWheels", "", "calculateToe", "checkStability", "displayLiveResults", "results", "displayResults", "finishMeasurement", "getColorForValue", "value", "minNormal", "maxNormal", "nextWheel", "onAccuracyChanged", "sensor", "accuracy", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onPause", "onResume", "onSensorChanged", "event", "Landroid/hardware/SensorEvent;", "onSupportNavigateUp", "performMeasurement", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "processMeasurements", "measurements", "registerSensorListeners", "saveMeasurement", "selectReferenceWheel", "index", "selectWheelCount", "count", "setupMeasurement", "setupSensors", "setupViews", "showError", "message", "showLivePreview", "showReferenceWheelSelection", "showWheelSelection", "startMeasurement", "startMeasurementProcess", "unregisterSensorListeners", "updateValueColor", "textView", "updateWheelDisplay", "waitForStability", "app_debug"})
public final class MeasurementActivity extends androidx.appcompat.app.AppCompatActivity implements android.hardware.SensorEventListener {
    private android.hardware.SensorManager sensorManager;
    private com.alineacion.ruedas.core.MeasurementProcessor measurementProcessor;
    private com.alineacion.ruedas.core.WheelAlignmentSensorManager wheelAlignmentSensorManager;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.Sensor accelerometer;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.Sensor gyroscope;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.Sensor magnetometer;
    private com.google.android.material.card.MaterialCardView wheelSelectionCard;
    private com.google.android.material.card.MaterialCardView measurementCard;
    private com.google.android.material.card.MaterialCardView resultsCard;
    private com.google.android.material.textview.MaterialTextView wheelTitle;
    private com.google.android.material.textview.MaterialTextView instructionText;
    private com.google.android.material.textview.MaterialTextView statusText;
    private com.google.android.material.textview.MaterialTextView camberValue;
    private com.google.android.material.textview.MaterialTextView toeValue;
    private com.google.android.material.textview.MaterialTextView casterValue;
    private com.google.android.material.button.MaterialButton measureButton;
    private com.google.android.material.button.MaterialButton nextWheelButton;
    private com.google.android.material.button.MaterialButton finishButton;
    private com.google.android.material.button.MaterialButton backButton;
    private com.google.android.material.button.MaterialButton select2WheelsButton;
    private com.google.android.material.button.MaterialButton select4WheelsButton;
    private java.util.List<? extends com.google.android.material.button.MaterialButton> wheelSelectionButtons;
    private com.google.android.material.button.MaterialButton startMeasurementButton;
    private int currentWheel = 0;
    private boolean isMeasuring = false;
    private boolean isContinuousMeasuring = false;
    @org.jetbrains.annotations.NotNull()
    private java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Float>> measurementResults;
    @org.jetbrains.annotations.NotNull()
    private java.util.Map<java.lang.String, java.lang.Float> currentLiveMeasurement;
    private int selectedWheelsCount = 4;
    private int referenceWheelIndex = 0;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.String> selectedWheels;
    private boolean isWheelSelectionMode = true;
    private float referenceToeMeasurement = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> allWheels = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> wheelInstructions = null;
    @org.jetbrains.annotations.NotNull()
    private float[] currentAccelerometer;
    @org.jetbrains.annotations.NotNull()
    private float[] currentMagnetometer;
    private boolean isStable = false;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    
    public MeasurementActivity() {
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
    
    private final void setupMeasurement() {
    }
    
    private final void startMeasurementProcess() {
    }
    
    private final void showWheelSelection() {
    }
    
    private final void selectWheelCount(int count) {
    }
    
    private final void showReferenceWheelSelection() {
    }
    
    private final void selectReferenceWheel(int index) {
    }
    
    private final void updateWheelDisplay() {
    }
    
    private final void startMeasurement() {
    }
    
    private final void saveMeasurement() {
    }
    
    private final java.lang.Object waitForStability(kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    private final java.lang.Object performMeasurement(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void displayLiveResults(java.util.Map<java.lang.String, java.lang.Float> results) {
    }
    
    private final void calculateCasterForAllWheels() {
    }
    
    private final float calculateCaster(java.lang.String wheelName) {
        return 0.0F;
    }
    
    private final int getColorForValue(float value, float minNormal, float maxNormal) {
        return 0;
    }
    
    private final java.util.Map<java.lang.String, java.lang.Float> processMeasurements(java.util.List<float[]> measurements) {
        return null;
    }
    
    private final float calculateCamber(float x, float y, float z) {
        return 0.0F;
    }
    
    private final float calculateToe(float[] magnetometer) {
        return 0.0F;
    }
    
    private final void displayResults(java.util.Map<java.lang.String, java.lang.Float> results) {
    }
    
    private final void updateValueColor(com.google.android.material.textview.MaterialTextView textView, float value, float minNormal, float maxNormal) {
    }
    
    private final void nextWheel() {
    }
    
    private final void finishMeasurement() {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    private final void registerSensorListeners() {
    }
    
    private final void unregisterSensorListeners() {
    }
    
    @java.lang.Override()
    public void onSensorChanged(@org.jetbrains.annotations.Nullable()
    android.hardware.SensorEvent event) {
    }
    
    private final void showLivePreview() {
    }
    
    private final void checkStability() {
    }
    
    @java.lang.Override()
    public void onAccuracyChanged(@org.jetbrains.annotations.Nullable()
    android.hardware.Sensor sensor, int accuracy) {
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