package com.realtime_draw.realtimedraw.app.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import java.lang.String;

public class deseneDataSourceClass{

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.column_id,
            MySQLiteHelper.column_DrawName, MySQLiteHelper.column_isPublic,MySQLiteHelper.column_isGruop };

    public deseneDataSourceClass(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Desen createDesen(String desen, String isPublic, String isGroup) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.column_DrawName, desen);
        values.put(MySQLiteHelper.column_isPublic, isPublic);
        values.put(MySQLiteHelper.column_isGruop, isGroup);
        long insertId = database.insert(MySQLiteHelper.table_draws, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.table_draws,
                allColumns, MySQLiteHelper.column_id + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Desen newDesen = cursorToDesen(cursor);




        cursor.close();
        return newDesen;
    }

    public void deleteDesen(Desen desen) {
        long id = desen.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.table_draws, MySQLiteHelper.column_id
                + " = " + id, null);
    }



    public List<Desen> getAllDesene() {
        List<Desen> desene = new ArrayList<Desen>();

        Cursor cursor = database.query(MySQLiteHelper.table_draws,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Desen desen = cursorToDesen(cursor);
            desene.add(desen);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return desene;
    }

    private Desen cursorToDesen(Cursor cursor) {
        Desen desen = new Desen();
        desen.setId(cursor.getLong(0));
        desen.setName(cursor.getString(1));
        return desen;
    }


    public boolean existaDesen(String nume) {
        List<Desen> desene = getAllDesene();
        for(Desen desen:desene){
            if(desen.getName().equals(nume))
                return true;
        }
        return false;
    }
}


