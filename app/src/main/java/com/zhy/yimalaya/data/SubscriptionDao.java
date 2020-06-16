package com.zhy.yimalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.zhy.yimalaya.base.BaseApplication;
import com.zhy.yimalaya.utils.Constants;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptionDao implements ISubscriptionDao {

    private static final String TAG = "SubscriptionDao";
    private static final SubscriptionDao instance = new SubscriptionDao();

    private final YimalayaDbHelper mDbHelper;
    private ISubscriptionDaoCallBack mCallBack;

    public static SubscriptionDao getInstance() {
        return instance;
    }


    private SubscriptionDao() {
        mDbHelper = new YimalayaDbHelper(BaseApplication.getContext());
    }

    @Override
    public synchronized void add(Album album) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            ContentValues content = new ContentValues();
            content.put(Constants.SUB_COVER_URL, album.getCoverUrlLarge());
            content.put(Constants.SUB_TITLE, album.getAlbumTitle());
            content.put(Constants.SUB_DESCRIPTION, album.getAlbumIntro());
            content.put(Constants.SUB_PLAY_COUNT, album.getPlayCount());
            content.put(Constants.SUB_TRACKS_COUNT, album.getIncludeTrackCount());
            content.put(Constants.SUB_AUTHOR_NAME, album.getAnnouncer().getNickname());
            content.put(Constants.SUB_ALBUM_ID, album.getId());

            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            long result = db.insert(Constants.SUB_TABLE_NAME, null, content);
            db.setTransactionSuccessful();
            LogUtil.d(TAG, "add result ---> " + result);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onAdd(isSuccess);
            }
        }
    }

    @Override
    public synchronized void remove(Album album) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int result = db.delete(Constants.SUB_TABLE_NAME, Constants.SUB_ALBUM_ID + "=?",
                    new String[]{String.valueOf(album.getId())});
            db.setTransactionSuccessful();
            LogUtil.d(TAG, "delete result ---> " + result);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onRemove(isSuccess);
            }
        }
    }

    @Override
    public synchronized void listAll() {
        SQLiteDatabase db = null;
        List<Album> result = null;

        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();

            Cursor cursor = db.query(Constants.SUB_TABLE_NAME, null, null, null, null, null, "_id desc");
            result = new ArrayList<>(cursor.getCount());

            LogUtil.d(TAG, "list all ---> size=" + cursor.getCount());
            while (cursor.moveToNext()) {
                result.add(parse2Album(cursor));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onListResult(result == null ? Collections.EMPTY_LIST : result);
            }
        }
    }

    private Album parse2Album(Cursor cursor) {
        Album album = new Album();
        album.setId(cursor.getInt(cursor.getColumnIndex(Constants.SUB_ALBUM_ID)));

        album.setCoverUrlLarge(cursor.getString(cursor.getColumnIndex(Constants.SUB_COVER_URL)));
        album.setCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constants.SUB_COVER_URL)));
        album.setCoverUrlMiddle(cursor.getString(cursor.getColumnIndex(Constants.SUB_COVER_URL)));

        album.setAlbumTitle(cursor.getString(cursor.getColumnIndex(Constants.SUB_TITLE)));
        album.setAlbumIntro(cursor.getString(cursor.getColumnIndex(Constants.SUB_DESCRIPTION)));
        album.setPlayCount(cursor.getInt(cursor.getColumnIndex(Constants.SUB_PLAY_COUNT)));
        album.setIncludeTrackCount(cursor.getInt(cursor.getColumnIndex(Constants.SUB_PLAY_COUNT)));
        album.setPlayCount(cursor.getInt(cursor.getColumnIndex(Constants.SUB_PLAY_COUNT)));

        Announcer announcer = new Announcer();
        announcer.setNickname(cursor.getString(cursor.getColumnIndex(Constants.SUB_PLAY_COUNT)));
        album.setAnnouncer(announcer);

        album.setId(cursor.getInt(cursor.getColumnIndex(Constants.SUB_ALBUM_ID)));
        return album;
    }


    @Override
    public synchronized void getByAlbumId(long id) {
        SQLiteDatabase db = null;
        Album result = null;
        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();

            Cursor cursor = db.query(Constants.SUB_TABLE_NAME, null, Constants.SUB_ALBUM_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.getCount() > 1) {
                throw new IllegalStateException("Album Id 重复");
            }
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = parse2Album(cursor);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onAlbumResult(result);
            }
        }
    }


    public void setCallBack(ISubscriptionDaoCallBack callBack) {
        mCallBack = callBack;
    }
}
