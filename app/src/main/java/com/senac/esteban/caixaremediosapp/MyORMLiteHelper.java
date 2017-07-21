package com.senac.esteban.caixaremediosapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by vita on 22-11-2016.
 */
public class MyORMLiteHelper extends OrmLiteSqliteOpenHelper {

    private  static  final String DATABASE_NAME = "bancoRemedio.dc";
    private static final int DATABASE_VERSION = 10;
    private static MyORMLiteHelper mIntance = null;
    private Dao<Remedio, Integer> remedioDao = null;

    public MyORMLiteHelper(Context context) {super(context,DATABASE_NAME, null,DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,Remedio.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource,Remedio.class,true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(sqLiteDatabase,connectionSource);
    }

    public Dao<Remedio, Integer> getRemedioDao() throws SQLException {
        if(remedioDao==null){
            remedioDao = getDao(Remedio.class);
        }
        return remedioDao;
    }

    public static MyORMLiteHelper getIntance(Context context){
        if(mIntance==null){
            mIntance = new MyORMLiteHelper(context.getApplicationContext());
        }
        return mIntance;
    }
}
