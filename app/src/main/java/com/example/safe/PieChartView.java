package com.example.safe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private int ameliorations = 0;
    private int reclamations = 0;

    private Paint paintAmeliorations;
    private Paint paintReclamations;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintAmeliorations = new Paint();
        paintAmeliorations.setColor(0xFFFF8F00); // orange clair
        paintAmeliorations.setAntiAlias(true);

        paintReclamations = new Paint();
        paintReclamations.setColor(0xFFFF6F00); // orange foncé
        paintReclamations.setAntiAlias(true);
    }

    public void setData(int ameliorations, int reclamations) {
        this.ameliorations = ameliorations;
        this.reclamations = reclamations;
        invalidate(); // rafraîchir
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int total = ameliorations + reclamations;
        if (total == 0) return;

        float angleAmeliorations = (ameliorations * 360f) / total;
        float angleReclamations = (reclamations * 360f) / total;

        int size = Math.min(getWidth(), getHeight());
        int padding = 20;

        float left = padding;
        float top = padding;
        float right = size - padding;
        float bottom = size - padding;

        // Dessin Améliorations
        canvas.drawArc(left, top, right, bottom, 0, angleAmeliorations, true, paintAmeliorations);

        // Dessin Réclamations
        canvas.drawArc(left, top, right, bottom, angleAmeliorations, angleReclamations, true, paintReclamations);
    }
}
