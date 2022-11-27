package com.example.myapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    Sensor sensor_accelerometer;
    long myTime1, myTime2;

    float x, y, z;
    float lastX, lastY, lastZ;

    final int walkThreshold = 455; // 걷기 인식 임계 값
    double acceleration = 0;



    long startTime;
    long currentTime;
    int gameFirstStart = 0;
    int gameIng = 0;
    int gameOver = 1;
    int Width, Height;


    int mWeight = 79;

    double calorie;

    static int walkingCount = 0;
    int elapsedTime = 0; //경과시간
    int min, sec;

    DecimalFormat format;

    TextView tv_title, tv_weight,tv_time,tv_kcal,tv_step;
    Button btn_plus, btn_minus, btn_start, btn_stop;

    ValueHandler handler = new ValueHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//화면을 세로로 설정
        setContentView(R.layout.activity_main);

        tv_title = findViewById(R.id.tv_time);
        tv_weight = findViewById(R.id.tv_weight);
        tv_time = findViewById(R.id.tv_time);
        tv_kcal = findViewById(R.id.tv_kcal);
        tv_step =findViewById(R.id.tv_step);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);

        sm = (SensorManager)getSystemService(SENSOR_SERVICE); //센서에 접근
        sensor_accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 가속도 센서

        tv_weight.setText("몸무게: 79kg");

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                format = new DecimalFormat("0.000");

                //!!!!! 스레드 만들어 넣기 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                startTime = System.currentTimeMillis();

                BackgroundThread thread = new BackgroundThread();
                thread.start();



            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(elapsedTime / 900 < 60)
                    tv_time.setText("운동한 시간: "+elapsedTime / 900 + " 초");
                else{
                    min = elapsedTime / 900 / 60;
                    sec = (elapsedTime / 900) % 60;
                    tv_time.setText("운동한 시간: "+min + " 분"+ sec + " 초");

                    calorie = 3.5 * 3.5 * mWeight / 200 / 120 * walkingCount;
                    tv_kcal.setText("소비한 칼로리: " + format.format(calorie)+ " kcal");
                }
                tv_step.setText(walkingCount+"");

                walkingCount = 0;




            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeight++;
                tv_weight.setText("몸무게: "+mWeight);
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeight--;
                tv_weight.setText("몸무게: "+mWeight);

            }
        });
    }






    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return false;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,sensor_accelerometer,SensorManager.SENSOR_DELAY_NORMAL);//센서등록, 센서 읽어오는 속도
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this); //센서 리스너 해제
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){ //센서의 타입이 가속도 센서일 경우
            myTime2 = System.currentTimeMillis();
            long gab = myTime2 - myTime1; //시간차

            if(gab > 90){
                myTime1 = myTime2;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                acceleration = Math.abs(x + y + z - lastX - lastY - lastZ)/gab * 9000; //이동속도공식

                if(acceleration > walkThreshold){
                    walkingCount += 1.0;
                }

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 스레드 클래스 생성
    class BackgroundThread extends Thread {
        int value = 0;
        boolean running = false;
        public void run() {
            running = true;
            while(running) {
                value += 1;
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value",value);
                message.setData(bundle);
                handler.sendMessage(message);

                elapsedTime = (int)currentTime - (int)startTime;
                currentTime = System.currentTimeMillis();



                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }
        }
    }

    class ValueHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            int value = bundle.getInt("value");
            // textView.setText("현재 값 : " + value);

            if(elapsedTime / 900 < 60){
                tv_time.setText("운동한 시간: "+elapsedTime / 900 + " 초");
            }else{
                min = elapsedTime / 900 / 60;
                sec = (elapsedTime / 900) % 60;
                tv_time.setText("운동한 시간: "+min + " 분"+ sec + " 초");
            }

            //운동강도 Mets = 3.5
            // 느리게 걷기:2, 조금 빨리 걷기: 3~3.5, 매우빨리걷기: 6
            //소비되는 칼로리 공식 kcal/min = Mets x 3.5 x 체중 / 200 / 1초에 걸음 수(1초에 2걸음이면 120)
            calorie = 3.5 * 3.5 * mWeight / 200 / 120 * walkingCount;
            tv_kcal.setText("소비한 칼로리: " + format.format(calorie)+ " kcal");

            tv_step.setText(walkingCount+"");
        }


    }

}