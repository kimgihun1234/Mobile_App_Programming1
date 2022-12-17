package com.example.savetest2

import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.savetest2.R
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.savetest2.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    val long_now = System.currentTimeMillis()
    val t_date = Date(long_now)
    val t_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
    val date = t_dateFormat.format(t_date)
    lateinit var count: EditText
    lateinit var calorie: EditText
    var dbName = "tests.db"
    var tableName = "member"
    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        count = binding.count
        calorie = binding.calorie

        val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {

            }
        }
        registerReceiver(receiver, intentFilter)
        //데이터베이스 파일 생성 또는 열기
        db = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)

        //테이블 생성
        //CREATE~ 없으면 만든다. / 괄호 안에 SQL언어
        db.execSQL("CREATE TABLE IF NOT EXISTS $tableName(num integer primary key autoincrement, date text not null, count integer, calorie integer);")
    }

    fun clickInsert(view: View?) {
        val count = count.text.toString().toInt()
        val calorie = calorie.text.toString().toInt()

        //db에 데이터 삽입
        db.execSQL("INSERT INTO $tableName(date, count, calorie) VALUES('$date','$count','$calorie');")
        this.count.setText("")
        this.calorie.setText("")
    }

    fun clickSelectAll(view: View?) {
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) ?: return //두 번째 파라미터: 겁색 조건
        //결과 table 참조
        val buffer = StringBuffer()
        while (cursor.moveToNext()) {
            val num = cursor.getInt(0)
            val date = cursor.getString(1)
            val count = cursor.getInt(2)
            val calorie = cursor.getInt(3)
            buffer.append("$num  $date  $count  $calorie\n")
        }
        AlertDialog.Builder(this).setMessage(buffer.toString()).setPositiveButton("OK", null)
            .create().show()
    }

    fun clickSelectByName(view: View?) {
        val cursor = db.rawQuery("SELECT date, count FROM $tableName WHERE date=?", arrayOf(date))
            ?: return

        //총 레코드 수(줄,행(row)수)
        val rowNum = cursor.count //데이터의 행의 수
        val buffer = StringBuffer()
        while (cursor.moveToNext()) {
            val da = cursor.getString(0)
            val count = cursor.getInt(1)
            buffer.append("$da  $count\n")
            Toast.makeText(this, buffer.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun clickUpdate(view: View?) {
        db.execSQL("UPDATE $tableName SET count=30, calorie=500 WHERE date=?", arrayOf(date))
    }

    fun clickDelete(view: View?) {
        db.execSQL("DELETE FROM $tableName WHERE date=?", arrayOf(date))
    }

}
