package com.example.gabriela.evaluacionfinal.cupboardclasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DB {
    DatabaseManager manager;
    SQLiteDatabase db;

    public DB(Context context){
        DatabaseManager.initializeInstance(new OpenHelper(context));
        this.manager = DatabaseManager.getInstance();
        this.db = manager.openDatabase();
    }

    public DatabaseCompartment cb(){
        return cupboard().withDatabase(this.db);
    }
    public SQLiteDatabase db(){ return this.db; }

    public void close(){
        this.manager.closeDatabase();
    }
}
