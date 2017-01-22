package com.example.abhishek.ai;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private SensorManager msensorManager;
    private Sensor msensor;
    private long lastupdate=0;
    TextView xvalue,yvalue,zvalue, fileText;
    Spinner action_spinner;
    double[] gravity,linear_acceleration;
    private static final float NS2S = 1.0f / 1000000000.0f;
//    private float timestamp;
    SensorEventListener mAccelerometerSensorListener;
    Button start, stop, read, clear;
    private static final String TAG = MainActivity.class.getSimpleName();
    EditText name;

    FileOutputStream outputStreamWriter = null;
    File path;
    File file;
    String fileName = "";
    String activity = "";
    boolean startFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapping();
        msensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gravity=new double[3];
        linear_acceleration=new double[3];
        msensor=msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensorManager.registerListener(mAccelerometerSensorListener,msensor,SensorManager.SENSOR_DELAY_NORMAL);

//        List<Sensor> mList= msensorManager.getSensorList(Sensor.TYPE_ALL);
//
//        for (int i = 1; i < mList.size(); i++) {
//            xvalue.append("\n" + mList.get(i).getName() + "\n" + mList.get(i).getVendor() + "\n" + mList.get(i).getVersion());
//        }
        ArrayAdapter<CharSequence> actionAdapter = ArrayAdapter.createFromResource(this, R.array.hostel_list, android.R.layout.simple_spinner_item);

        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        action_spinner.setAdapter(actionAdapter);

        action_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity = parent.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                activity = "idle";
            }
        });

        mAccelerometerSensorListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor mysensor=event.sensor;
                if(mysensor==null)
                Log.i("abcd","my sensor null");
//                Log.i("abcd","Inside");

                if(mysensor.getType()==Sensor.TYPE_ACCELEROMETER && startFlag){

                    final double alpha = 0.8;

                    // Isolate the force of gravity with the low-pass filter.
//                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                    // Remove the gravity contribution with the high-pass filter.
//                    linear_acceleration[0] = (event.values[0] - gravity[0]);
//                    linear_acceleration[1] = (event.values[1] - gravity[1]);
//                    linear_acceleration[2] = (event.values[2] - gravity[2]);

                    try {
                        if (outputStreamWriter !=null)
                        {outputStreamWriter.write((event.values[0]+", "+event.values[1]+", "+event.values[2]+"\n").getBytes());
//                        Log.i(TAG,"Writing");
//                            Toast.makeText(MainActivity.this,"Writing", Toast.LENGTH_SHORT).show();

                        }
                    }
                    catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                    xvalue.setText(event.values[0]+"");
                    yvalue.setText(event.values[1]+"");
                    zvalue.setText(event.values[2]+"");

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStreamWriter.close();
                    startFlag=false;
                    Toast.makeText(MainActivity.this,"Stopped", Toast.LENGTH_SHORT).show();
                    outputStreamWriter = null;

                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Can't close file");
                }
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fileName = activity+"_"+System.currentTimeMillis()+"_"+name.getText().toString()+".txt";
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/AI"), fileName);
                    file.createNewFile();
                    outputStreamWriter = new FileOutputStream(file);
                    startFlag = true;
                } catch (Exception e){
                    Toast.makeText(MainActivity.this,"abhishek "+e.toString()+":::"+Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"File not found, path: "+Environment.DIRECTORY_DOWNLOADS);
                    path = getApplicationContext().getFilesDir();
//                    File file = new File(path, fileName);
                }
//
//                if(!startFlag){
//                    try {
//                        outputStreamWriter = openFileOutput(fileName, MODE_APPEND);
//                        startFlag = true;
//                    } catch (Exception e){
//                        Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_LONG).show();
//                        Log.e(TAG,"File not found again, path: "+path);
//                    }
//                }
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = getApplicationContext().getFilesDir();
                Toast.makeText(MainActivity.this, fileName, Toast.LENGTH_LONG).show();
                File file = new File(Environment.DIRECTORY_DOWNLOADS+"/AI", fileName);
                int length = (int) file.length();
                byte[] bytes = new byte[length];
                Toast.makeText(MainActivity.this,"Length: "+length, Toast.LENGTH_SHORT).show();

                try{
//                FileInputStream in = new FileInputStream(file);
                    FileInputStream in = openFileInput(fileName);
                    in.read(bytes);
                    in.close();
                } catch (Exception e){

                }
                String contents = new String(bytes);
                fileText.setText(contents);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openFileOutput(fileName, MODE_PRIVATE).close();
                } catch (Exception e){

                }
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void mapping(){
        xvalue=(TextView)findViewById(R.id.xvaluedisplay);
        yvalue=(TextView)findViewById(R.id.yvaluedisplay);
        zvalue=(TextView)findViewById(R.id.zvaluedisplay);
        read = (Button)  findViewById(R.id.read);
        start=(Button)findViewById(R.id.startsending);
        stop=(Button)findViewById(R.id.stopsending);
        clear = (Button) findViewById(R.id.clearButton);
        action_spinner = (Spinner) findViewById(R.id.action_spinner);
        fileText = (TextView) findViewById(R.id.fileText);
        name = (EditText) findViewById(R.id.name);
        xvalue.setText(0+" ");
        yvalue.setText(0+" ");
        zvalue.setText(0+" ");

    }


    @Override
    protected void onResume() {
        super.onResume();
        msensorManager.registerListener(mAccelerometerSensorListener,msensor,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        msensorManager.unregisterListener(mAccelerometerSensorListener);

    }

}
