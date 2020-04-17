package com.swufe.homework;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        score = findViewById(R.id.score);

    }
    public void btnAdd1(View btn){
        show(1);
    }
    public void btnAdd2(View btn){
        show(2);

    }
    public void btnAdd3(View btn){
        show(3);

    }
    public void btnReset(View btn){
        score.setText("0");

    }
    private void show(int inc){
        String oldScore = (String) score.getText();
        int newScore=Integer.parseInt(oldScore)+inc;
        score.setText(""+newScore);

    }
}
