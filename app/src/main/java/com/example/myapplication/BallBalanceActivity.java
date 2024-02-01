package com.example.myapplication;

import static java.lang.Math.abs;
import android.graphics.RectF;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;


public class BallBalanceActivity extends AppCompatActivity implements SensorEventListener{
    private ImageView imageView;
    private ImageView bubbleImageView;
    private TextView pointsView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private int width;
    private int height;
    private int points = 0;
    private int k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ball_balance);
        imageView = findViewById(R.id.fishImageView);
        bubbleImageView = findViewById(R.id.bubbleImageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mediaPlayer = MediaPlayer.create(this, R.raw.pop);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        pointsView = findViewById(R.id.pointsText);
        k=0;

    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];

        float newX = imageView.getX() - 10*x;
        float newY = imageView.getY() + 10*y;

        RectF fishRect = new RectF(newX, newY, newX + imageView.getWidth(), newY + imageView.getHeight());
        RectF knifeRect = new RectF(bubbleImageView.getX(), bubbleImageView.getY(), bubbleImageView.getX() + bubbleImageView.getWidth(), bubbleImageView.getY() + bubbleImageView.getHeight());

        //håller figuren inom displayens dimentioner
        //tittar om nästa steg är utanför displayen eller inte
        if (newX > 0 && newX < width - 150) {
            imageView.setX(newX);
        } else if (newX < 0) {
            imageView.setX(0);
        } else if (newX > width - 150) {
            imageView.setX(width - 150);
        }
        if (newY > 0 && newY < height - 350) {
            imageView.setY(newY);
        }else if(newY < 0){
            imageView.setY(0);
        } else if (newY > height - 350) {
            imageView.setY(height - 350);
        }
        //om den kolliderar
        if (fishRect.intersect(knifeRect)) {
            if (k<1) {
                k=1;
                mediaPlayer.start();
                vibrator.vibrate(50);
                moveBubble();
                points +=1;
                pointsView.setText(String.valueOf(points));
                k=0;
            }
        }else{
            k=0;
        }
    }
    public float getRandomHeight(){
        float min = 50;
        float max = height - 300;
        return (float) (Math.random() * (max - min)) + min;
    }
    public float getRandomWidth(){
        float min = 50;
        float max = width - 300;
        return (float) (Math.random() * (max - min)) + min;
    }
    public void moveBubble() {
        float newX = getRandomWidth();
        float newY = getRandomHeight();

        while(isInProximity(newX, newY)) {
            newX = getRandomWidth();
            newY = getRandomHeight();
        }
        bubbleImageView.setY(newY);
        bubbleImageView.setX(newX);
    }
    public boolean isInProximity(float newX, float newY){
       if(abs(imageView.getX() - newX) < 300 && abs(imageView.getY() - newY) < 300) {
           return true;
       }else {
           return false;
       }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
