package com.example.hj.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.hardware.Camera;

public class MainActivity extends AppCompatActivity implements SensorEventListener {



    private SensorManager sm;
    private boolean color = false;
    private View view;
    private long lastUpdate;

    public Camera camera;
    Camera.Parameters parameters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},50);
        }
        else
        {

        }


        camera = Camera.open();

        parameters = camera.getParameters();





        view = findViewById(R.id.layout);
        view.setBackgroundColor(Color.GREEN);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            getAccelerometer(event);
        }

    }




    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }



    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accSqRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        long actualTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(),String.valueOf(accSqRoot)+" "+SensorManager.GRAVITY_EARTH,Toast.LENGTH_LONG).show();

        if(accSqRoot >= 2)
        {
            if(actualTime - lastUpdate <200)
            {
                return;
            }

            lastUpdate = actualTime;

            if(color)
            {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
                color=!color;
            }
            else
            {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                color=!color;
            }

        }



    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}