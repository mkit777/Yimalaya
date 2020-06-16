package com.zhy.yimalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zhy.yimalaya.utils.Constants;


public class YimalayaDbHelper extends SQLiteOpenHelper {

    public YimalayaDbHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建订阅记录表
        String sql = "create  table " + Constants.SUB_TABLE_NAME + " (" +
                Constants.SUB_ID + " integer primary key autoincrement," +
                Constants.SUB_COVER_URL + " varchar," +
                Constants.SUB_TITLE + " varchar," +
                Constants.SUB_DESCRIPTION + " varchar," +
                Constants.SUB_PLAY_COUNT + " integer," +
                Constants.SUB_TRACKS_COUNT + " integer," +
                Constants.SUB_AUTHOR_NAME + " varchar," +
                Constants.SUB_ALBUM_ID + " integer)";
        db.execSQL(sql);


        // 创建历史记录里表
        sql = "create  table " + Constants.HIS_TABLE_NAME + " (" +
                Constants.HIS_ID + " integer primary key autoincrement," +
                Constants.HIS_TRACK_TITLE + " varchar," +
                Constants.HIS_PLAY_COUNT + " integer," +
                Constants.HIS_TRACK_DURATION + " integer," +
                Constants.HIS_TRACK_CREATED_TIME + " varchar," +
                Constants.HIS_TRACK_ID + " integer," +
                Constants.HIST_TRACK_COVER + " varchar," +
                Constants.HIS_AUTHOR_NAME + " varchar" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
