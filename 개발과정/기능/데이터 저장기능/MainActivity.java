package com.example.savetest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText date, count, calorie;

    String dbName="test.db";
    String tableName="member";

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date =findViewById(R.id.date);
        count =findViewById(R.id.count);
        calorie =findViewById(R.id.calorie);

        //데이터베이스 파일 생성 또는 열기
        db= this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        //테이블 생성
        //CREATE~ 없으면 만든다. / 괄호 안에 SQL언어
        db.execSQL("CREATE TABLE IF NOT EXISTS "+tableName+"(num integer primary key autoincrement, date text not null, count integer, calorie integer);");
    }
    public void clickInsert(View view) {

        String date= this.date.getText().toString();
        int count=Integer.parseInt(this.count.getText().toString());
        int calorie=Integer.parseInt(this.calorie.getText().toString());

        //db에 데이터 삽입
        db.execSQL("INSERT INTO "+tableName+"(date, count, calorie) VALUES('"+date+"','"+count+"','"+calorie+"');");

        this.date.setText("");
        this.count.setText("");
        this.calorie.setText("");

    }
    public void clickSelectAll(View view) {
        Cursor cursor =db.rawQuery("SELECT * FROM "+tableName+"",null); //두 번째 파라미터: 겁색 조건
        //결과 table 참조
        if(cursor==null) return;

        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext()){
            int num=cursor.getInt(0);
            String date=cursor.getString(1);
            int count= cursor.getInt(2);
            int calorie= cursor.getInt(3);
            buffer.append(num+"  "+date+"  "+count+"  "+calorie+"\n");
        }
        new AlertDialog.Builder(this).setMessage(buffer.toString()).setPositiveButton("OK",null).create().show();
    }
    public void clickSelectByName(View view) {
        String date= this.date.getText().toString();

        Cursor cursor=db.rawQuery("SELECT date, count FROM "+tableName+" WHERE date=?",new String[]{date});
        if(cursor==null) return;

        //총 레코드 수(줄,행(row)수)
        int rowNum= cursor.getCount(); //데이터의 행의 수

        StringBuffer buffer= new StringBuffer();
        while(cursor.moveToNext()){

            String da=cursor.getString(0);
            int count= cursor.getInt(1);

            buffer.append(da+"  "+count+"\n");
            Toast.makeText(this, buffer.toString(), Toast.LENGTH_SHORT).show();

        }
    }
    public void clickUpdate(View view) {
        String date = this.date.getText().toString();
        db.execSQL("UPDATE "+tableName+" SET count=30, calorie=500 WHERE date=?",new String[]{date});
    }
    public void clickDelete(View view) {
        String date = this.date.getText().toString();
        db.execSQL("DELETE FROM "+tableName+" WHERE date=?",new String[]{date});
    }
}