package com.zhy.yimalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhy.yimalaya.base.BaseApplication;
import com.zhy.yimalaya.utils.Constants;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private static IHistoryDao instance = null;

    public static IHistoryDao getInstance() {
        if (instance == null) {
            synchronized (HistoryDao.class) {
                if (instance == null) {
                    instance = new HistoryDao();
                }
            }
        }
        return instance;
    }


    private YimalayaDbHelper mDbHelper;
    private IHistoryDaoCallback mCallback;

    private HistoryDao() {
        mDbHelper = new YimalayaDbHelper(BaseApplication.getContext());
    }


    @Override
    public synchronized void addHistory(Track track) {
        SQLiteDatabase db = null;
        boolean success = false;

        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues data = new ContentValues();
            data.put(Constants.HIS_TRACK_TITLE, track.getTrackTitle());
            data.put(Constants.HIS_PLAY_COUNT, track.getPlayCount());
            data.put(Constants.HIS_TRACK_DURATION, track.getDuration());
            data.put(Constants.HIS_TRACK_CREATED_TIME, track.getCreatedAt());
            data.put(Constants.HIS_TRACK_ID, track.getDataId());
            data.put(Constants.HIST_TRACK_COVER, track.getAlbum().getCoverUrlLarge());
            data.put(Constants.HIS_AUTHOR_NAME, track.getAnnouncer().getNickname());

            db.insert(Constants.HIS_TABLE_NAME, null, data);

            db.setTransactionSuccessful();
            success = true;
            LogUtil.d(TAG, "addHistory " + success);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onAddHistoryResult(success);
            }
        }
    }

    @Override
    public synchronized void deleteHistory(Track track) {
        SQLiteDatabase db = null;
        boolean success = false;

        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.HIS_TABLE_NAME, Constants.HIS_TRACK_ID + "=?", new String[]{String.valueOf(track.getDataId())});
            db.setTransactionSuccessful();
            success = delete == 1;
            LogUtil.d(TAG, "deleteHistory " + success);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onDeleteHistoryResult(success);
            }
        }
    }


    @Override
    public synchronized void getAllHistory() {
        SQLiteDatabase db = null;
        List<Track> result = null;
        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.query(Constants.HIS_TABLE_NAME, null, null, null, null, null, Constants.HIS_ID + " desc");

            if (cursor.getCount() == 0) {
                result = Collections.emptyList();
            } else {
                result = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {

                    Track track = new Track();

                    track.setTrackTitle(cursor.getString(cursor.getColumnIndex(Constants.HIS_TRACK_TITLE)));
                    track.setPlayCount(cursor.getInt(cursor.getColumnIndex(Constants.HIS_PLAY_COUNT)));
                    track.setDuration(cursor.getInt(cursor.getColumnIndex(Constants.HIS_TRACK_DURATION)));
                    track.setCreatedAt(cursor.getLong(cursor.getColumnIndex(Constants.HIS_TRACK_CREATED_TIME)));
                    track.setDataId(cursor.getLong(cursor.getColumnIndex(Constants.HIS_TRACK_ID)));

                    String coverUrl = cursor.getString(cursor.getColumnIndex(Constants.HIST_TRACK_COVER));
                    SubordinatedAlbum album = new SubordinatedAlbum();
                    album.setCoverUrlMiddle(coverUrl);
                    album.setCoverUrlLarge(coverUrl);
                    album.setCoverUrlSmall(coverUrl);
                    track.setAlbum(album);

                    Announcer announcer = new Announcer();
                    announcer.setNickname(cursor.getString(cursor.getColumnIndex(Constants.HIS_AUTHOR_NAME)));
                    track.setAnnouncer(announcer);
                    result.add(track);
                }
            }
            db.setTransactionSuccessful();
            LogUtil.d(TAG, "getAllHistory size=" + cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onGetAllHistory(result);
            }
        }
    }

    @Override
    public synchronized void clearHistory() {
        SQLiteDatabase db = null;
        boolean success = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.HIS_TABLE_NAME, null, null);
            db.setTransactionSuccessful();
            success = delete != -1;
            LogUtil.d(TAG, "clearHistory " + success);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onClearAllHistory(success);
            }
        }
    }

    @Override
    public void setCallBack(IHistoryDaoCallback callBack) {
        this.mCallback = callBack;
    }
}
