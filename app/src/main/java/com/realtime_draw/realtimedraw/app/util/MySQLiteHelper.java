package com.realtime_draw.realtimedraw.app.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String table_draws = "desene";
    public static final String column_id = "_id";
    public static final String column_DrawName = "nume_desen";
    public static final String column_isPublic = "isPublic";
    public static final String column_isGruop = "isGroup";

    private static final String DATABASE_NAME = "desene.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + table_draws + "(" + column_id
            + " integer primary key autoincrement, " + column_DrawName
            + " text not null, "+ column_isPublic + " text not null, "
            + column_isGruop + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + table_draws);
        onCreate(db);
    }

}
