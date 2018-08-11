package com.example.gabriela.evaluacionfinal.activities;

import android.database.sqlite.SQLiteDatabase;
import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.gabriela.evaluacionfinal.R;
import com.example.gabriela.evaluacionfinal.cupboardclasses.OpenHelper;
import com.example.gabriela.evaluacionfinal.item.Info;
import com.example.gabriela.evaluacionfinal.utils.InfoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ListActivity extends AppCompatActivity{

    public static final int REQUEST_ADD_TO_DO = 100;
    public static SQLiteDatabase db;
    OpenHelper dbHelper;

    private InfoAdapter myAdapter;
    private List<Info> infoList;

    @BindView(R.id.myInfoList) RecyclerView myInfoList;
    //@BindView(R.id.imvInfo) ImageView imvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);
        //infoList.setHasFixedSize(true);

        dbHelper = new OpenHelper(this);

        this.infoList = new ArrayList<>();
        this.myAdapter = new InfoAdapter(this, infoList, R.layout.list_item);

        consult();

        this.myInfoList.setLayoutManager(new LinearLayoutManager(this));
        this.myInfoList.setAdapter(this.myAdapter);
        this.myInfoList.setItemAnimator(new DefaultItemAnimator());

        //flecha hacia atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onResume() {
        consult();
        super.onResume();
    }

    //Metodo para mostras flecha hacia atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void consult() {
        infoList.clear();
        db = dbHelper.getReadableDatabase();

        try {
            List<Info> itr = cupboard().withDatabase(db).query(Info.class).list();
            this.infoList.addAll(itr);
        } finally {
            db.close();
        }
        myAdapter.notifyDataSetChanged();
    }
}
