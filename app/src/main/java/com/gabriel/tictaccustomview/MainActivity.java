package com.gabriel.tictaccustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.gabriel.tictaccustomview.views.TicTacView;

public class MainActivity extends AppCompatActivity implements TicTacView.SquarePressedListener {

    private TicTacView ticTacView;
    private TextView result;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.result_tv);
        ticTacView=findViewById(R.id.tictac_view);
        ticTacView.registerListener(this);
    }

    @Override
    public void onSquarePressed() {

    }

    @Override
    public void onMatchComplete(String result) {
        Log.d(TAG, "Match Outcome:  " + result);
        this.result.setText("Match Outcome: " + result);
    }

    public void resetGame(View view)
    {
        result.setText("");
        ticTacView.resetGame();
    }
}
