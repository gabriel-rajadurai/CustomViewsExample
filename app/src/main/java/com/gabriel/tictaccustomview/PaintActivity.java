package com.gabriel.tictaccustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gabriel.tictaccustomview.views.PaintLayout;
import com.gabriel.tictaccustomview.views.PaintView;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener {

    private PaintLayout paintLayout;
    private Button pencilBt, circleBt, rectBt, ovalBt, roundedrectBt, eraseBt, clearBt, undoBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        init();
    }

    private void init() {
        paintLayout = findViewById(R.id.pview);
        pencilBt = findViewById(R.id.pencil_bt);
        circleBt = findViewById(R.id.circle_bt);
        rectBt = findViewById(R.id.rect_bt);
        ovalBt = findViewById(R.id.oval_bt);
        roundedrectBt = findViewById(R.id.roundrect_bt);
        clearBt = findViewById(R.id.clr_bt);
        eraseBt = findViewById(R.id.erase_bt);
        undoBt = findViewById(R.id.undo_bt);

        pencilBt.setOnClickListener(this);
        circleBt.setOnClickListener(this);
        rectBt.setOnClickListener(this);
        ovalBt.setOnClickListener(this);
        roundedrectBt.setOnClickListener(this);
        clearBt.setOnClickListener(this);
        eraseBt.setOnClickListener(this);
        undoBt.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == pencilBt) {
            paintLayout.setType(PaintView.PAINT_TYPE_PENCIL);
        } else if (view == circleBt) {
            paintLayout.setType(PaintView.PAINT_TYPE_CIRCLE);
        } else if (view == rectBt) {
            paintLayout.setType(PaintView.PAINT_TYPE_RECTANGLE);
        } else if (view == ovalBt) {
            paintLayout.setType(PaintView.PAINT_TYPE_OVAL);
        } else if (view == roundedrectBt) {
            paintLayout.setType(PaintView.PAINT_TYPE_ROUNDED_RECTANGLE);
        } else if (view == clearBt) {
            paintLayout.resetPaintSpace();
        } else if (view == eraseBt) {
            paintLayout.setType(PaintView.PAINT_TYPE_ERASE);
        } else if (view == undoBt) {
            paintLayout.undoOperation();
        }
    }
}
