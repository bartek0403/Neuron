package com.example.janek.neuron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Janusz Tracz on 20.02.2018.
 */

//Klasa odpowiadająca za pisanie po ekranie. Zasada działania polega na:
//1. Przechwyceniu punktów dotyku z CustomCanvas
//2. Przeskalowaniu do bitmapy mBitmap 28x28 px (w celu redukcji czasu obliczeń)
//3. Narysowaniu mPath na mBitmap
//4. Narysowaniu mBitmap na canvasie przypisanym do CustomCanvas.
public class CustomCanvas extends View {

    // Wartość w dp. Wysokość i szerokość okna rysunku.
    public static final int HEIGHT = 360;
    public static final int WIDTH = 360;

    //Wartość w px. Wysokość małej bitmapy
    public static final int SMALL_BITMAP_HEIGHT = 28;
    private Bitmap mBitmap;
    //Canvas do małej bitmapy
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;

    private Context mContext;

    private final String TAG = "CUSTOMCANVAS.java";

    //Zmienne lokalizacji dotknięcia
    private float touchX;
    private float touchY;
    private Point p;

    //Rect obejmujący całe okno malowania
    private Rect rect;


    public CustomCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        //Scieżka odpowiadająca rysowanej liczbie
        mPath = new Path();

        //Ustawienia pędzla
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1f);

        //rect obejący obszarem dokładnie całe okno pisania
        rect = new Rect(0, 0, convertDpToPx(WIDTH), convertDpToPx(HEIGHT));

    }

    // Odpowiada za "przepisanie" małej bitmapy do większej.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(mBitmap, null, rect, mPaint);

    }

    // Obsługa dotyku, rysowanie ścieżki
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                stopTouch(touchX, touchY);
                break;
        }
        return true;
    }

    //DOKUMENTACJA : onSizeChanged() is called when your view is first assigned a size, and again if
    // the size of your view changes for any reason. Calculate positions, dimensions, and any other
    // values related to your view's size in onSizeChanged(), instead of recalculating them every time
    // you draw.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    //Zwraca tablcę jednowymiarową. Piksel czarny konwertuje na 1, biały na 0.
    public float[] getPixel() {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pixels0_255 = new int[width * height];
        mBitmap.getPixels(pixels0_255, 0, width, 0, 0, width, height);
        float[] retPixels = new float[pixels0_255.length];

        for (int i = 0; i < pixels0_255.length; i++) {
            if (pixels0_255[i] != 0)
                retPixels[i] = 1;
        }
        return retPixels;
    }

    private void startTouch(float x, float y) {
        p = scaleXY(new Point((int) x, (int) y));
        mPath.moveTo(p.x, p.y);
        invalidate();

    }

    private void moveTouch(float x, float y) {
        p = scaleXY(new Point((int) x, (int) y));
        mPath.lineTo(p.x, p.y);
        invalidate();
    }

    private void stopTouch(float x, float y) {
        p = scaleXY(new Point((int) x, (int) y));
        mPath.lineTo(p.x, p.y);
        invalidate();
    }

    public void clearCanvas() {
        mPath.reset();
        mCanvas.drawPath(mPath, mPaint);
        mBitmap.eraseColor(Color.TRANSPARENT);

        invalidate();
    }

    // Zapis grafiki
    public void storeImage() {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            Bitmap mmBitmap = Bitmap.createScaledBitmap(mBitmap, convertDpToPx(HEIGHT), convertDpToPx(WIDTH), false);
            mmBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(mContext, "Bitmap saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    //Utworzenie pliku o dopwiedniej nazwie do zapisau grafiki
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Pictures/"
                + getContext().getPackageName());

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    // Konwersja dp do px
    private int convertDpToPx(int dp) {
        int density = (int) getResources().getDisplayMetrics().density;
        return dp * density;
    }

    // Skalowanie punktów dotyku do małej bitmapy mBitmap 28x28 px
    private Point scaleXY(Point p) {
        int x = p.x;
        int y = p.y;
        int ratio = Math.round(convertDpToPx(HEIGHT) / mBitmap.getHeight());
        return new Point(x / ratio, y / ratio);
    }
}
