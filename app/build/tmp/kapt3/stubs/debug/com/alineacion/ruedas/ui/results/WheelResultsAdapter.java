package com.alineacion.ruedas.ui.results;

/**
 * Adapter para mostrar los resultados detallados de cada rueda
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0014B%\u0012\u001e\u0010\u0003\u001a\u001a\u0012\u0004\u0012\u00020\u0005\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u00040\u0004\u00a2\u0006\u0002\u0010\u0007J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000bH\u0016J\u0018\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0016R&\u0010\u0003\u001a\u001a\u0012\u0004\u0012\u00020\u0005\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/alineacion/ruedas/ui/results/WheelResultsAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/alineacion/ruedas/ui/results/WheelResultsAdapter$WheelResultViewHolder;", "measurementResults", "", "", "", "(Ljava/util/Map;)V", "wheels", "", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "WheelResultViewHolder", "app_debug"})
public final class WheelResultsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.alineacion.ruedas.ui.results.WheelResultsAdapter.WheelResultViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Float>> measurementResults = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> wheels = null;
    
    public WheelResultsAdapter(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.util.Map<java.lang.String, java.lang.Float>> measurementResults) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.alineacion.ruedas.ui.results.WheelResultsAdapter.WheelResultViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.alineacion.ruedas.ui.results.WheelResultsAdapter.WheelResultViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\"\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00160\u0015J,\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\u0010\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\u0016H\u0002J \u0010 \u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u00162\u0006\u0010\"\u001a\u00020\u00162\u0006\u0010#\u001a\u00020\u0016H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/alineacion/ruedas/ui/results/WheelResultsAdapter$WheelResultViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "camberStatusText", "Lcom/google/android/material/textview/MaterialTextView;", "camberValueText", "casterLayout", "casterStatusText", "casterValueText", "deviationText", "toeStatusText", "toeValueText", "wheelPositionText", "wheelStatusText", "bind", "", "wheelName", "", "measurements", "", "", "determineWheelStatus", "Lkotlin/Pair;", "", "camber", "toe", "isReference", "", "getDeviationQuality", "deviation", "getStatusIcon", "value", "minNormal", "maxNormal", "app_debug"})
    public static final class WheelResultViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView wheelPositionText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView wheelStatusText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView camberValueText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView camberStatusText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView toeValueText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView toeStatusText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView casterValueText = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView casterStatusText = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.View casterLayout = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.textview.MaterialTextView deviationText = null;
        
        public WheelResultViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View itemView) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        java.lang.String wheelName, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.Float> measurements) {
        }
        
        private final kotlin.Pair<java.lang.String, java.lang.Integer> determineWheelStatus(float camber, float toe, boolean isReference) {
            return null;
        }
        
        private final java.lang.String getStatusIcon(float value, float minNormal, float maxNormal) {
            return null;
        }
        
        private final java.lang.String getDeviationQuality(float deviation) {
            return null;
        }
    }
}