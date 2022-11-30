package com.example.myapp2

import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import com.example.myapp2.MainActivity.ValueHandler
import android.os.Bundle
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import com.example.myapp2.R
import com.example.myapp2.MainActivity.BackgroundThread
import android.hardware.SensorEvent
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp2.databinding.ActivityMainBinding
import java.lang.Exception
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), SensorEventListener {
    var sm: SensorManager? = null
    var sensor_accelerometer: Sensor? = null
    var myTime1: Long = 0
    var myTime2: Long = 0
    var x = 0f
    var y = 0f
    var z = 0f
    var lastX = 0f
    var lastY = 0f
    var lastZ = 0f
    val walkThreshold = 455 // 걷기 인식 임계 값
    var acceleration = 0.0
    var startTime: Long = 0
    var currentTime: Long = 0
    var gameFirstStart = 0
    var gameIng = 0
    var gameOver = 1
    var Width = 0
    var Height = 0
    var mWeight = 79
    var calorie = 0.0
    var elapsedTime = 0 //경과시간
    var min = 0
    var sec = 0
    var format: DecimalFormat? = null
    var tv_title: TextView? = null
    var tv_weight: TextView? = null
    var tv_time: TextView? = null
    var tv_kcal: TextView? = null
    var tv_step: TextView? = null
    var btn_plus: Button? = null
    var btn_minus: Button? = null
    var btn_start: Button? = null
    var btn_stop: Button? = null
    var handler = ValueHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityMainBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //화면을 세로로 설정
        setContentView(binding.root)
        tv_title = findViewById(R.id.tv_time)
        tv_weight = findViewById(R.id.tv_weight)
        tv_time = findViewById(R.id.tv_time)
        tv_kcal = findViewById(R.id.tv_kcal)
        tv_step = findViewById(R.id.tv_step)
        btn_plus = findViewById(R.id.btn_plus)
        btn_minus = findViewById(R.id.btn_minus)
        btn_start = findViewById(R.id.btn_start)
        btn_stop = findViewById(R.id.btn_stop)
        sm = getSystemService(SENSOR_SERVICE) as SensorManager //센서에 접근
        sensor_accelerometer = sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // 가속도 센서
        binding.tvWeight.setText("몸무게: 79kg")
        binding.btnStart.setOnClickListener(View.OnClickListener {
            startTime = System.currentTimeMillis()
            format = DecimalFormat("0.000")

            //!!!!! 스레드 만들어 넣기 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            startTime = System.currentTimeMillis()
            val thread = BackgroundThread()
            thread.start()
        })
        binding.btnStop.setOnClickListener(View.OnClickListener {
            if (elapsedTime / 900 < 60) binding.tvTime.setText("운동한 시간: " + elapsedTime / 900 + " 초") else {
                min = elapsedTime / 900 / 60
                sec = elapsedTime / 900 % 60
                binding.tvTime.setText("운동한 시간: $min 분$sec 초")
                calorie = 3.5 * 3.5 * mWeight / 200 / 120 * walkingCount
                binding.tvKcal.setText("소비한 칼로리: " + format!!.format(calorie) + " kcal")
            }
            binding.tvStep.setText(walkingCount.toString() + "")
            walkingCount = 0
        })
        binding.btnPlus.setOnClickListener(View.OnClickListener {
            mWeight++
            binding.tvWeight.setText("몸무게: $mWeight")
        })
        binding.btnMinus.setOnClickListener(View.OnClickListener {
            mWeight--
            binding.tvWeight.setText("몸무게: $mWeight")
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return false
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        sm!!.registerListener(
            this,
            sensor_accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        ) //센서등록, 센서 읽어오는 속도
    }

    override fun onPause() {
        super.onPause()
        sm!!.unregisterListener(this) //센서 리스너 해제
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) { //센서의 타입이 가속도 센서일 경우
            myTime2 = System.currentTimeMillis()
            val gab = myTime2 - myTime1 //시간차
            if (gab > 90) {
                myTime1 = myTime2
                x = event.values[0]
                y = event.values[1]
                z = event.values[2]
                acceleration =
                    (Math.abs(x + y + z - lastX - lastY - lastZ) / gab * 9000).toDouble() //이동속도공식
                if (acceleration > walkThreshold) {
                    walkingCount += 1.0.toInt()
                }
                lastX = event.values[0]
                lastY = event.values[1]
                lastZ = event.values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    // 스레드 클래스 생성
    internal inner class BackgroundThread : Thread() {
        var value = 0
        var running = false
        override fun run() {
            running = true
            while (running) {
                value += 1
                val message = handler.obtainMessage()
                val bundle = Bundle()
                bundle.putInt("value", value)
                message.data = bundle
                handler.sendMessage(message)
                elapsedTime = currentTime.toInt() - startTime.toInt()
                currentTime = System.currentTimeMillis()
                try {
                    sleep(1000)
                } catch (e: Exception) {
                }
            }
        }
    }

    inner class ValueHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            val value = bundle.getInt("value")
            // textView.setText("현재 값 : " + value);
            if (elapsedTime / 900 < 60) {
                tv_time!!.text = "운동한 시간: " + elapsedTime / 900 + " 초"
            } else {
                min = elapsedTime / 900 / 60
                sec = elapsedTime / 900 % 60
                tv_time!!.text = "운동한 시간: $min 분$sec 초"
            }

            //운동강도 Mets = 3.5
            // 느리게 걷기:2, 조금 빨리 걷기: 3~3.5, 매우빨리걷기: 6
            //소비되는 칼로리 공식 kcal/min = Mets x 3.5 x 체중 / 200 / 1초에 걸음 수(1초에 2걸음이면 120)
            calorie = 3.5 * 3.5 * mWeight / 200 / 120 * walkingCount
            tv_kcal!!.text = "소비한 칼로리: " + format!!.format(calorie) + " kcal"
            tv_step!!.text = walkingCount.toString() + ""
        }
    }

    companion object {
        var walkingCount = 0
    }
}