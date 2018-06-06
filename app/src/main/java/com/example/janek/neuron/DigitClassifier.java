package com.example.janek.neuron;

import android.content.res.AssetManager;
import android.graphics.Point;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janusz Tracz on 22.02.2018.
 */

public class DigitClassifier {

    // Lista zawierajaca liczby 0-9
    private List<String> labels;

    //Komunikator aplikacja -> sieć
    private TensorFlowInferenceInterface inferenceInterface;

    //Tabela zawierająca wyjściowe prawdopodobieństwa
    private float[] output;

    //Nazwa komórek wejściowych
    private String input_node_name = "conv2d_1_input";

    //Nazwa komórek wyjściowych
    private String output_node_name = "dense_2/Softmax";
    private String[] outputNames = new String[]{output_node_name};

    //Tworzy pomost do komunikacji z siecią, ładuje liczby do listy.
    public DigitClassifier create(AssetManager am, int inputSize) {
        DigitClassifier digitClassifier = new DigitClassifier();
        inferenceInterface = new TensorFlowInferenceInterface(am, "net.pb");
        labels = loadLabels();
        return digitClassifier;
    }

    //Zwraca obiekt Point, gdzie x - "przewidziana" liczba, y - prawdopodobieństwo
    public Point detect(final float[] pixels) {
        // Prawdopodobieństwo - początkowe 0
        float prob = 0;
        // Liczba "przewidziana".
        int ans = -1;
        //Ladowanie danych do sieci neuronowej
        inferenceInterface.feed(input_node_name, pixels, 1, CustomCanvas.SMALL_BITMAP_HEIGHT, CustomCanvas.SMALL_BITMAP_HEIGHT, 1);
        //Przygotowanie tablicy wyjściowej
        inferenceInterface.run(outputNames);
        output = new float[10];
        //Uruchomienie przetwarzania
        inferenceInterface.fetch(output_node_name, output);
        //Poszukiwanie największego prawdopodobieństwa i sparowanie go z daną liczbą
        for (int i = 0; i < output.length; i++) {
            if (output[i] > prob) {
                prob = output[i];
                ans = Integer.valueOf(labels.get(i));
            }
        }
        return new Point(ans, (int) (prob * 100));
    }

    private static List<String> loadLabels() {
        List<String> labels = new ArrayList<String>();
        for (int i = 0; i < 10; i++)
            labels.add(Integer.toString(i));
        return labels;
    }

}
