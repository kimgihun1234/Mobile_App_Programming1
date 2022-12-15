package com.example.savetest2

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.savetest2.R
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity() {
    lateinit var date: EditText
    lateinit var count: EditText
    lateinit var calorie: EditText
    var dbName = "test2.db"
    var tableName = "member"
    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        date = findViewById(R.id.date)
        count = findViewById(R.id.count)
        calorie = findViewById(R.id.calorie)

        //데이터베이스 파일 생성 또는 열기
        db = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)

        //테이블 생성
        //CREATE~ 없으면 만든다. / 괄호 안에 SQL언어
        db.execSQL("CREATE TABLE IF NOT EXISTS $tableName(num integer primary key autoincrement, date text not null, count integer, calorie integer);")
    }

    fun clickInsert(view: View?) {
        val date = date.text.toString()
        val count = count.text.toString().toInt()
        val calorie = calorie.text.toString().toInt()

        //db에 데이터 삽입
        db.execSQL("INSERT INTO $tableName(date, count, calorie) VALUES('$date','$count','$calorie');")
        this.date.setText("")
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
        val date = date.text.toString()
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
        val date = date.text.toString()
        db.execSQL("UPDATE $tableName SET count=30, calorie=500 WHERE date=?", arrayOf(date))
    }

    fun clickDelete(view: View?) {
        val date = date.text.toString()
        db.execSQL("DELETE FROM $tableName WHERE date=?", arrayOf(date))
    }

}