package com.senac.esteban.caixaremediosapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VerRemedios extends AppCompatActivity {
    ListView listViewRemedios;
    List<Remedio> listRemedio;
    MyAdapter adRemedios;
    Dao<Remedio,Integer> remedioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_remedios);
        listRemedio = new ArrayList<>();

        listViewRemedios = (ListView) findViewById(R.id.listRemediosQtd);
        try {
            remedioDao = MyORMLiteHelper.getIntance(this).getRemedioDao();
            listRemedio = remedioDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adRemedios = new MyAdapter(this, listRemedio);
        listViewRemedios.setAdapter(adRemedios);

    }
}
