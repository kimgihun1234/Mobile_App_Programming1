package com.example.mobile_app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobile_app.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), AccessDB{
    var dbName = "test1.db"
    var tableName = "member"
    private lateinit var db: SQLiteDatabase
    lateinit var toggle: ActionBarDrawerToggle

    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        val fragments: List<Fragment>
        init {
            fragments= listOf(InitialFragment(), RecordFragment(),RewardFragment())
        }
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //데이터베이스 파일 생성 또는 열기
        db = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)

        //테이블 생성
        //CREATE~ 없으면 만든다. / 괄호 안에 SQL언어
        db.execSQL("CREATE TABLE IF NOT EXISTS $tableName(num integer primary key autoincrement, date text not null, count integer);")

        val adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter
        val tabLayout: TabLayout = binding.tabs
        val tab1: TabLayout.Tab = tabLayout.newTab()
        tab1.text="오늘"
        tabLayout.addTab(tab1)
        val tab2: TabLayout.Tab = tabLayout.newTab()
        tab2.text="기록"
        tabLayout.addTab(tab2)
        val tab3: TabLayout.Tab = tabLayout.newTab()
        tab3.text="보상 뱃지"
        tabLayout.addTab(tab3)
        TabLayoutMediator(binding.tabs, binding.viewpager){ tab, position ->
            if(position==0){
                tab.text = "오늘"
            }else if(position==1){
                tab.text = "기록"
            }
            else{
                tab.text = "보상 뱃지"
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when(tab?.text){
                    "오늘"->binding.collasingtoolbar.title="오늘"
                    "기록"->binding.collasingtoolbar.title="기록"
                    "보상 뱃지"->binding.collasingtoolbar.title="보상 뱃지"
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //권한 설정
        verifyStoragePermission(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
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

    override fun insert(date: String, count: Int) {
        db.execSQL("INSERT INTO $tableName(date, count) VALUES('$date','$count');")
    }

    override fun inputHash(hash: HashMap<String, Int>) {
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) ?: return //두 번째 파라미터: 겁색 조건
        while (cursor.moveToNext()) {
            val num = cursor.getInt(0)
            val date = cursor.getString(1)
            val count = cursor.getInt(2)
            hash.put(date,count)
        }
    }
}
