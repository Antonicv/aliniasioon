package com.alineacion.ruedas.ui.wheelselection;

/**
 * Actividad de selección de ruedas con interfaz visual
 *
 * Permite seleccionar las ruedas a medir usando una interfaz intuitiva
 * con imágenes de neumáticos distribuidas en una cuadrícula.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0014\u001a\u00020\u0015H\u0002J\u0012\u0010\u0016\u001a\u00020\u00152\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0014J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u0015H\u0002J\b\u0010\u001c\u001a\u00020\u0015H\u0002J\b\u0010\u001d\u001a\u00020\u0015H\u0002J\u0018\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020\f2\u0006\u0010 \u001a\u00020\u0006H\u0002J\b\u0010!\u001a\u00020\u0015H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00060\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/alineacion/ruedas/ui/wheelselection/WheelSelectionActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "clearSelectionButton", "Lcom/google/android/material/button/MaterialButton;", "frontLeftCard", "Lcom/google/android/material/card/MaterialCardView;", "frontRightCard", "rearLeftCard", "rearRightCard", "selectedWheels", "", "", "selectionInfoText", "Lcom/google/android/material/textview/MaterialTextView;", "startMeasurementButton", "wheelCards", "", "wheelNames", "", "clearSelection", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onSupportNavigateUp", "", "setupClickListeners", "setupViews", "startMeasurement", "toggleWheelSelection", "wheelId", "card", "updateUI", "app_debug"})
public final class WheelSelectionActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.google.android.material.card.MaterialCardView frontLeftCard;
    private com.google.android.material.card.MaterialCardView frontRightCard;
    private com.google.android.material.card.MaterialCardView rearLeftCard;
    private com.google.android.material.card.MaterialCardView rearRightCard;
    private com.google.android.material.textview.MaterialTextView selectionInfoText;
    private com.google.android.material.button.MaterialButton clearSelectionButton;
    private com.google.android.material.button.MaterialButton startMeasurementButton;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> selectedWheels = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.google.android.material.card.MaterialCardView> wheelCards = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> wheelNames = null;
    
    public WheelSelectionActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupViews() {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void toggleWheelSelection(java.lang.String wheelId, com.google.android.material.card.MaterialCardView card) {
    }
    
    private final void clearSelection() {
    }
    
    private final void updateUI() {
    }
    
    private final void startMeasurement() {
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
}