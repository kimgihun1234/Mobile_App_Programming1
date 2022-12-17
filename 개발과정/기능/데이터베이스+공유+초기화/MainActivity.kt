package com.example.savetest2

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.savetest2.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

var count = 0
class MainActivity : AppCompatActivity() {
    val long_now = System.currentTimeMillis()
    val t_date = Date(long_now)
    val t_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
    val date = t_dateFormat.format(t_date)
    var dbName = "test5.db"
    var tableName = "member"
    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cnt.text=count.toString()
        binding.btn.setOnClickListener {
            count++
            binding.cnt.text=count.toString()
        }

        //데이터베이스 파일 생성 또는 열기
        db = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)

        //테이블 생성
        //CREATE~ 없으면 만든다. / 괄호 안에 SQL언어
        db.execSQL("CREATE TABLE IF NOT EXISTS $tableName(num integer primary key autoincrement, date text not null, count integer);")

        //날짜 지나면 초기화 부분
        val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                //db에 데이터 삽입
                val insertValue = binding.cnt.text.toString().toInt()
                db.execSQL("INSERT INTO $tableName(date, count) VALUES('$date','$insertValue');")
            }
        }
        registerReceiver(receiver, intentFilter)


        //권한 설정
        verifyStoragePermission(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    fun clickInsert(view: View?) {
        //db에 데이터 삽입
        db.execSQL("INSERT INTO $tableName(date, count) VALUES('$date','$count');")
    }

    fun clickSelectAll(view: View?) {
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) ?: return //두 번째 파라미터: 겁색 조건
        //결과 table 참조
        val buffer = StringBuffer()
        while (cursor.moveToNext()) {
            val num = cursor.getInt(0)
            val date = cursor.getString(1)
            val count = cursor.getInt(2)
            buffer.append("$num  $date  $count\n")
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
        db.execSQL("UPDATE $tableName SET count=$count WHERE date=?", arrayOf(date))
    }

    fun clickDelete(view: View?) {
        db.execSQL("DELETE FROM $tableName WHERE date=?", arrayOf(date))
    }

    fun ScreenshotButton(view: View?) {
        val rootView = window.decorView //현재 화면 지정
        val screenShot = ScreenShot(rootView)
        //사진 저장
        if (screenShot != null) {
            sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(screenShot)
                )
            ) //갤러리에 추가
        }
        //공유 기능
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenShot))
        shareIntent.type = "image/png"
        startActivity(Intent.createChooser(shareIntent, "test"))
    }

    //스크린샷 함수
    fun ScreenShot(view: View): File? {
        view.isDrawingCacheEnabled = true //캐시 읽기
        val screenBitmap = view.drawingCache //비트맵으로 변환
        val filename = "screenshot" + System.currentTimeMillis() + ".png" //스크린샷 파일 이름 설정
        val file = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures",
            filename
        ) //스크린샷 저장 경로 설정
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os) //비트맵 > PNG파일
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        view.isDrawingCacheEnabled = false
        return file
    }

    companion object {
        //권한 설정
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //권한 설정
        fun verifyStoragePermission(activity: Activity?) {
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}
