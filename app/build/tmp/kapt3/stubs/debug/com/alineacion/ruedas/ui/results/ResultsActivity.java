package com.alineacion.ruedas.ui.results;

/**
 * Actividad de resultados finales
 *
 * Muestra un resumen completo de todas las mediciones realizadas,
 * análisis de los valores y recomendaciones.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0010$\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001,B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020 H\u0002J\b\u0010!\u001a\u00020 H\u0002J\b\u0010\"\u001a\u00020 H\u0002J\b\u0010#\u001a\u00020 H\u0002J\u0012\u0010$\u001a\u00020 2\b\u0010%\u001a\u0004\u0018\u00010&H\u0014J\b\u0010\'\u001a\u00020\bH\u0016J\b\u0010(\u001a\u00020 H\u0002J\b\u0010)\u001a\u00020 H\u0002J\b\u0010*\u001a\u00020 H\u0002J\b\u0010+\u001a\u00020 H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R&\u0010\u000b\u001a\u001a\u0012\u0004\u0012\u00020\r\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000f0\u000e0\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\r0\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/alineacion/ruedas/ui/results/ResultsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "backToMainButton", "Lcom/google/android/material/button/MaterialButton;", "detailsRecyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "isRecommendationsExpanded", "", "measurementDateText", "Lcom/google/android/material/textview/MaterialTextView;", "measurementResults", "", "", "", "", "newMeasurementButton", "overallStatusText", "recommendationsCard", "Lcom/google/android/material/card/MaterialCardView;", "recommendationsHeader", "Landroid/widget/LinearLayout;", "recommendationsText", "recommendationsToggle", "shareResultsButton", "summaryCard", "vehicleStatusText", "wheels", "", "analyzeResults", "Lcom/alineacion/ruedas/ui/results/ResultsActivity$MeasurementAnalysis;", "backToMain", "", "displayResults", "generateSampleData", "loadMeasurements", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onSupportNavigateUp", "setupViews", "shareResults", "startNewMeasurement", "toggleRecommendations", "MeasurementAnalysis", "app_debug"})
public final class ResultsActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.google.android.material.card.MaterialCardView summaryCard;
    private androidx.recyclerview.widget.RecyclerView detailsRecyclerView;
    private com.google.android.material.card.MaterialCardView recommendationsCard;
    private android.widget.LinearLayout recommendationsHeader;
    private com.google.android.material.textview.MaterialTextView recommendationsToggle;
    private com.google.android.material.textview.MaterialTextView overallStatusText;
    private com.google.android.material.textview.MaterialTextView measurementDateText;
    private com.google.android.material.textview.MaterialTextView vehicleStatusText;
    private com.google.android.material.textview.MaterialTextView recommendationsText;
    private boolean isRecommendationsExpanded = false;
    private com.google.android.material.button.MaterialButton newMeasurementButton;
    private com.google.android.material.button.MaterialButton shareResultsButton;
    private com.google.android.material.button.MaterialButton backToMainButton;
    @org.jetbrains.annotations.NotNull()
    private java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Float>> measurementResults;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> wheels = null;
    
    public ResultsActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupViews() {
    }
    
    private final void loadMeasurements() {
    }
    
    private final void generateSampleData() {
    }
    
    private final void displayResults() {
    }
    
    private final com.alineacion.ruedas.ui.results.ResultsActivity.MeasurementAnalysis analyzeResults() {
        return null;
    }
    
    private final void startNewMeasurement() {
    }
    
    private final void shareResults() {
    }
    
    private final void backToMain() {
    }
    
    private final void toggleRecommendations() {
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J;\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\r\u00a8\u0006\u001c"}, d2 = {"Lcom/alineacion/ruedas/ui/results/ResultsActivity$MeasurementAnalysis;", "", "overallStatus", "", "statusColor", "", "vehicleStatus", "recommendations", "issuesCount", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;I)V", "getIssuesCount", "()I", "getOverallStatus", "()Ljava/lang/String;", "getRecommendations", "getStatusColor", "getVehicleStatus", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class MeasurementAnalysis {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String overallStatus = null;
        private final int statusColor = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String vehicleStatus = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String recommendations = null;
        private final int issuesCount = 0;
        
        public MeasurementAnalysis(@org.jetbrains.annotations.NotNull()
        java.lang.String overallStatus, int statusColor, @org.jetbrains.annotations.NotNull()
        java.lang.String vehicleStatus, @org.jetbrains.annotations.NotNull()
        java.lang.String recommendations, int issuesCount) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getOverallStatus() {
            return null;
        }
        
        public final int getStatusColor() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getVehicleStatus() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getRecommendations() {
            return null;
        }
        
        public final int getIssuesCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        public final int component5() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.alineacion.ruedas.ui.results.ResultsActivity.MeasurementAnalysis copy(@org.jetbrains.annotations.NotNull()
        java.lang.String overallStatus, int statusColor, @org.jetbrains.annotations.NotNull()
        java.lang.String vehicleStatus, @org.jetbrains.annotations.NotNull()
        java.lang.String recommendations, int issuesCount) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}