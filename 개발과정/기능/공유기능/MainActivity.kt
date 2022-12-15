package com.example.sharetest

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.os.Bundle
import com.example.sharetest.R
import com.example.sharetest.MainActivity
import android.os.StrictMode.VmPolicy
import android.os.StrictMode
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var down: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //권한 설정
        verifyStoragePermission(this)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        down = findViewById(R.id.down)
    }

    //버튼 클릭 시 호출
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