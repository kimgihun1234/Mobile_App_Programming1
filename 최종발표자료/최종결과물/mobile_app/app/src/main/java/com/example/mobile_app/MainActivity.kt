package com.example.mobile_app

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.*
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobile_app.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener, AccessDB, NavigationView.OnNavigationItemSelectedListener{
    val long_now = System.currentTimeMillis()
    val t_date = Date(long_now)
    val t_dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = t_dateFormat.format(t_date)
    var dbName = "Final.db"
    var tableName = "member"
    private lateinit var db: SQLiteDatabase
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    //????????? ?????? ????????? ??????
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
    val walkThreshold = 455 // ?????? ?????? ?????? ???
    var acceleration = 0.0
    var startTime: Long = 0
    var currentTime: Long = 0
    var elapsedTime = 0 //????????????
    var handler = ValueHandler()
    var walkstart:String?= "0"
    var initTime = 0L
    var pauseTime = 0L
    //?????????

    //????????? ??????
    var tall = 170
    var weight = 60

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //?????????????????? ?????? ?????? ?????? ??????
        db = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)
        //????????? ??????
        //CREATE~ ????????? ?????????. / ?????? ?????? SQL??????
        db.execSQL("CREATE TABLE IF NOT EXISTS $tableName(num integer primary key autoincrement, date text not null, count integer, km float);")

        insert("2022-11-25", 1800, 1.26f)
        insert("2022-11-26", 3500, 2.45f)
        insert("2022-11-27", 6700, 4.69f)
        insert("2022-12-03", 8000, 5.6f)
        insert("2022-12-16", 9000, 6.3f)
        insert("2022-12-17", 3000, 2.1f)
        insert("2022-12-18", 4500, 3.15f)

        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.opened, R.string.closed)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
        val navigationView=binding.navView
        navigationView.setNavigationItemSelectedListener(this)
        //?????? ????????? ????????? ??????
        val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                toast("????????? ?????????????????????.")
                //db??? ????????? ??????
                val cnt = walkingCount
                val fmt = DecimalFormat("#.##")
                fmt.roundingMode = RoundingMode.HALF_UP
                val km = fmt.format(walkingCount.toFloat()*(tall*0.45)/100000)
                if(findByDateExists(date)) {
                    updateByDate(date, cnt, km.toFloat())
                } else {
                    db.execSQL("INSERT INTO $tableName(date, count, km) VALUES('$date', '$cnt', '$km')")
                }
                walkingCount=0

                binding.chronometer.text="00:00"
                myTime1= 0
                myTime2= 0
                acceleration = 0.0
                startTime = 0
                currentTime = 0
                elapsedTime = 0 //????????????
                walkstart = "0"
                initTime = 0L
                pauseTime = 0L

                //????????? ?????????????????? ?????????
                val initialFragment = InitialFragment()
                val bundle = Bundle()
                bundle.putInt("key", walkingCount)
                initialFragment.arguments=bundle
                val transaction=supportFragmentManager.beginTransaction()
                transaction.replace(R.id.initialLayout, initialFragment)
                transaction.commit()
            }
        }
        registerReceiver(receiver, intentFilter)

        val adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter
        val tabLayout: TabLayout = binding.tabs
        val tab1: TabLayout.Tab = tabLayout.newTab()
        tab1.text="??????"
        tab1.setIcon(R.drawable.initial)
        tabLayout.addTab(tab1)
        val tab2: TabLayout.Tab = tabLayout.newTab()
        tab2.setIcon(R.drawable.record)
        tab2.text="??????"
        tabLayout.addTab(tab2)
        val tab3: TabLayout.Tab = tabLayout.newTab()
        tab3.setIcon(R.drawable.reward)
        tab3.text="?????? ??????"
        tabLayout.addTab(tab3)
        TabLayoutMediator(binding.tabs, binding.viewpager){ tab, position ->
            if(position==0){
                tab.text = "??????"
                tab.setIcon(R.drawable.initial)
            }else if(position==1){
                tab.text = "??????"
                tab.setIcon(R.drawable.record)
            }
            else{
                tab.text = "?????? ??????"
                tab.setIcon(R.drawable.reward)
            }
        }.attach()



        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when(tab?.text){
                    "??????"->binding.collasingtoolbar.title="??????"
                    "??????"->binding.collasingtoolbar.title="??????"
                    "?????? ??????"->binding.collasingtoolbar.title="?????? ??????"
                }
                when(tab?.text){
                    "??????"->{
                        binding.chronometer.visibility = View.VISIBLE
                    }
                    "??????"->{
                        binding.chronometer.visibility = View.INVISIBLE
                    }
                    "?????? ??????"->{
                        binding.chronometer.visibility = View.INVISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        //?????? ?????? ??????

        //????????? ?????? ???????????? ??????
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //????????? ????????? ??????
        setContentView(binding.root)
        tabLayout.setOnClickListener(){
        }

        sm = getSystemService(SENSOR_SERVICE) as SensorManager //????????? ??????
        sensor_accelerometer = sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // ????????? ??????
        supportFragmentManager.setFragmentResultListener("requestKey", this) { requestKey, bundle ->
            var isstart = bundle.getString("bundleKey")
            walkstart = isstart
            startTime = System.currentTimeMillis()
            //!!!!! ????????? ????????? ?????? !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            val thread = BackgroundThread()
            thread.start()
            if(isstart=="1"){
                binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
                binding.chronometer.start()
            }
            else{
                pauseTime = binding.chronometer.base - SystemClock.elapsedRealtime()
                binding.chronometer.stop()
                val cnt = walkingCount
                val fmt = DecimalFormat("#.##")
                fmt.roundingMode = RoundingMode.HALF_UP
                val km = fmt.format(walkingCount.toFloat()*(tall*0.45)/100000)
                if(findByDateExists(date)) {
                    updateByDate(date, cnt, km.toFloat())
                } else {
                    db.execSQL("INSERT INTO $tableName(date, count, km) VALUES('$date', '$cnt', '$km')")
                }
            }
        }
        //?????????

        binding.navView.setNavigationItemSelectedListener(this)
        val nav_header_view = binding.navView.getHeaderView(0)
        val nav_header_id_text = nav_header_view.findViewById<View>(R.id.namename) as TextView
        nav_header_id_text.setText(intent.getStringExtra("name"))

        //?????? ??????
        verifyStoragePermission(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    //?????? ?????? ?????????
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return false
        }
        return false
    }
    override fun onResume() {
        super.onResume()
        sm!!.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_NORMAL) //????????????, ?????? ???????????? ??????
    }

    override fun onPause() {
        super.onPause()
        sm!!.unregisterListener(this) //?????? ????????? ??????
    }

    override fun onSensorChanged(event: SensorEvent) {
        val initialFragment = InitialFragment()
        val recordFragment = RecordFragment()
        val bundle = Bundle()
        tall = intent.getIntExtra("height", 170)
        weight = intent.getIntExtra("weight", 70)
        bundle.putInt("key", walkingCount)
        bundle.putInt("tall", tall)
        bundle.putInt("weight", weight)
        initialFragment.arguments=bundle
        val transaction=supportFragmentManager.beginTransaction()

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) { //????????? ????????? ????????? ????????? ??????
            myTime2 = System.currentTimeMillis()
            val gab = myTime2 - myTime1 //?????????
            if (gab > 90) {
                myTime1 = myTime2
                x = event.values[0]
                y = event.values[1]
                z = event.values[2]
                acceleration =
                    (Math.abs(x + y + z - lastX - lastY - lastZ) / gab * 9000).toDouble() //??????????????????
                if (acceleration > walkThreshold) {
                    if(walkstart=="1"){
                        walkingCount += 1.0.toInt()
                        //activity?????? fragment??? ?????? ??????
                        transaction.replace(R.id.initialLayout, initialFragment)
                        transaction.commit()
                    }
                }
                lastX = event.values[0]
                lastY = event.values[1]
                lastZ = event.values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    // ????????? ????????? ??????
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
            //???????????? Mets = 3.5
            // ????????? ??????:2, ?????? ?????? ??????: 3~3.5, ??????????????????: 6
            //???????????? ????????? ?????? kcal/min = Mets x 3.5 x ?????? / 200 / 1?????? ?????? ???(1?????? 2???????????? 120)
        }
    }

    fun ScreenshotButton(view: View?) {
        val rootView = window.decorView //?????? ?????? ??????
        val screenShot = ScreenShot(rootView)
        //?????? ??????
        if (screenShot != null) {
            sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(screenShot)
                )
            ) //???????????? ??????
        }
        //?????? ??????
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenShot))
        shareIntent.type = "image/png"
        startActivity(Intent.createChooser(shareIntent, "????????????"))
        val snsvalue = "1"
        db.execSQL("INSERT INTO $tableName(date, count, km) VALUES('$snsvalue', '0', '0')")
        refreshFragment(RewardFragment(), supportFragmentManager)
    }

    //???????????? ??????
    fun ScreenShot(view: View): File? {
        view.isDrawingCacheEnabled = true //?????? ??????
        val screenBitmap = view.drawingCache //??????????????? ??????
        val filename = "screenshot" + System.currentTimeMillis() + ".png" //???????????? ?????? ?????? ??????
        val file = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures",
            filename
        ) //???????????? ?????? ?????? ??????
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os) //????????? > PNG??????
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        view.isDrawingCacheEnabled = false
        return file
    }
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    fun findByDateExists(dat: String) : Boolean{
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) ?: return false //??? ?????? ????????????: ?????? ??????
        while (cursor.moveToNext()) {
            val date = cursor.getString(1)
            if(date == dat){
                return true
            }
        }
        return false
    }

    fun updateByDate(dat: String, count: Int, km: Float) {
        var contentValues= ContentValues()
        contentValues.put("count", count)
        contentValues.put("km",km)
        var arr : Array<String> = arrayOf(dat)
        db.update("$tableName", contentValues, "date=?", arr)
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        var walkingCount = 0
        private val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //?????? ?????? ??????
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

    override fun insert(date: String, count: Int, km: Float) {
        if(date=="") {
            val currentdate = this.date
            db.execSQL("INSERT INTO $tableName(date, count, km) VALUES('$currentdate', '$count', '$km')")
        } else {
            db.execSQL("INSERT INTO $tableName(date, count, km) VALUES('$date','$count', '$km');")
        }
    }

    override fun inputHash(hash: HashMap<String, Int>) {
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) ?: return //??? ?????? ????????????: ?????? ??????
        while (cursor.moveToNext()) {
            val date = cursor.getString(1)
            val count = cursor.getInt(2)
            hash.put(date,count)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean=when(item.itemId) {

        R.id.info->{
            toast("?????? : ${intent.getStringExtra("name")} \n" +
            "??? : ${intent.getStringExtra("height")}cm " +
            "????????? : ${intent.getStringExtra("weight")}kg")
            true
        }
        R.id.aboutus -> {
            toast("We are Group 6")
            true
        }
        R.id.contact -> {
            toast("please contact 031-120")
            true
        }
        R.id.infochange -> {
            val intent=Intent(this,DetailActivity::class.java)
            startActivity(intent)
            true
        }
        else -> false
    }


    override fun inputFloatHash(hash: HashMap<String, Float>) {
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) ?: return //??? ?????? ????????????: ?????? ??????
        while (cursor.moveToNext()) {
            val date = cursor.getString(1)
            val km = cursor.getFloat(3)
            hash.put(date,km)
        }
    }

    override fun FindKmSum() : Float{
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) //??? ?????? ????????????: ?????? ??????
        var sum = 0.0F
        while (cursor.moveToNext()) {
            val km = cursor.getFloat(3)
            sum +=km
        }
        return sum
    }

    override fun FindCountSum() : Int{
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) //??? ?????? ????????????: ?????? ??????
        var sum = 0
        while (cursor.moveToNext()) {
            val count = cursor.getInt(2)
            sum +=count
        }
        return sum
    }

    override fun FindSnsValue() : Int{
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) //??? ?????? ????????????: ?????? ??????
        while (cursor.moveToNext()) {
            val date = cursor.getString(1)
            if(date == "1"){
                return 1
            }
        }
        return 0
    }

    override fun FindidxSum() : Int{
        val cursor = db.rawQuery("SELECT * FROM $tableName", null) //??? ?????? ????????????: ?????? ??????
        var idx = 0
        while (cursor.moveToNext()) {
            idx +=1
        }
        return idx
    }

    override fun toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
