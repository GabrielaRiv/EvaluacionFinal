package com.example.gabriela.evaluacionfinal.cupboardclasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gabriela.evaluacionfinal.item.Info;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class OpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Info.db";
    private static final int DATABASE_VERSION = 2;

    static {
        cupboard().register(Info.class);
    }

    public OpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
