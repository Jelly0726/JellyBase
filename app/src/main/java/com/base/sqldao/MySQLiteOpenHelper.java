package com.base.sqldao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import systemdb.SearchHistoryDao;

/**
 * Created by Growth on 2016/3/3.
 */
public class MySQLiteOpenHelper extends systemdb.DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, systemdb.LoginDao.class, systemdb.PositionEntityDao.class, SearchHistoryDao.class);
    }
}
