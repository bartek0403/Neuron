package com.example.janek.neuron;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //Wysokość i szerokość analoziwanej bitmapy w px
    int inputSize = CustomCanvas.SMALL_BITMAP_HEIGHT;
    CustomCanvas customCanvas;
    DigitClassifier digitClassifier = null;
    //Zawiera dane o predycji i pradopodobieństwie
    Point answer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Ladowanie sieci do pamięci z folderu assets - operacja asynchroniczna
        loadNet();
        customCanvas = (CustomCanvas) findViewById(R.id.customCanvas);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customCanvas.storeImage();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customCanvas.clearCanvas();
            }
        });

        Button detectButton = (Button) findViewById(R.id.detect);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (digitClassifier != null) {
                    //Wprowadzenie danych do sieci i otrzymanie wyniku
                    answer = digitClassifier.detect(customCanvas.getPixel());

                    if (answer != null) {
                        //Popup z wynikiem
                        Intent popup = new Intent(MainActivity.this, PopActivity.class);
                        popup.putExtra("Answer", answer);
                        startActivity(popup);
                    }
                }
            }
        });


    }

    //Ladowanie sieci do pamięci z folderu assets - operacja asynchroniczna
    private void loadNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                digitClassifier = new DigitClassifier();
                digitClassifier.create(getAssets(), inputSize);
            }
        }).start();
    }
}
