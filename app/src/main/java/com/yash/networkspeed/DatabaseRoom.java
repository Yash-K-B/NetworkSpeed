package com.yash.networkspeed;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseRoom extends SQLiteOpenHelper {
    private Context context;
    private SQLiteDatabase db;
    public static final String DATABASE_NAME="NetworkUsage";
    public static final String TABLE_NAME="Usage";
    public static final int DATABASE_VERSION=2;

    public DatabaseRoom(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table if not exists "+TABLE_NAME+"(id integer Primary key AUTOINCREMENT,date datetime,start_amt number(50) default 0,amount number(50))";
        //db=this.getWritableDatabase();
        db.execSQL(sql);
        Log.d("msg","table created");

    }

    void insertData(double amount){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-mm-yyyy", Locale.US);
        String sql="select * from "+TABLE_NAME+" where date='"+simpleDateFormat.format(new Date())+"';";
        db=this.getReadableDatabase();
        Cursor c=db.rawQuery(sql,null);

        Log.d("msg","Query Executed"+" value: "+c);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(sql);
        onCreate(db);
    }
}
