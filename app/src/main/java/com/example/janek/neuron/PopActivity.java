package com.example.janek.neuron;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class PopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        TextView number = (TextView) findViewById(R.id.number_textview);
        TextView prob = (TextView) findViewById(R.id.probability_textview);

        //Skalowanie okna 70% - jako popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.7));

        //Pobranie wyniku
        Intent popup = getIntent();
        Bundle bundle = popup.getExtras();

        //Wyswietlenie wyniku
        if (bundle != null) {
            Point answer = bundle.getParcelable("Answer");
            number.setText(Integer.toString(answer.x));
            prob.setText(Integer.toString(answer.y) + "%");
        }

    }
}
