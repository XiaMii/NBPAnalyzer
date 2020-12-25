package com.example.nbpanalyzer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nbpanalyzer.Constant;

//数据库帮助器SQLiteOpenHelper（安全方便地打开、升级数据库）
public class MySqliteHelper extends SQLiteOpenHelper {

    public MySqliteHelper(Context context,
                          String name,
                          SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }
    public MySqliteHelper(Context context){
        super(context, Constant.DATABASE_NAME,null,Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO 创建数据库后，对数据库的操作

//        db.execSQL("create table if not exists Nbpdata(_id integer primary key autoincrement," +
//                "user text not null,date text not null,sysPressure integer not null," +
//                "disPressure integer not null,nbpPulse integer not null)");
//        db.execSQL("insert into Nbpdata(user,date,sysPressure,disPressure,nbpPulse)values('default'," +
//                "'2020-4-26 17:18',92,114,92)");
        String sql = "create table if not exists "+Constant.TABLE_NAME+"("+
                Constant.ID+" Integer primary key ,"+
                Constant.USER+" text,"+
                Constant.DATE+" text,"+
                Constant.TIME+" text,"+
                Constant.SYS+" Integer,"+
                Constant.DIS+" Integer,"+
                Constant.PR+" Integer)";
        db.execSQL(sql);
//
//        String sql1 = "create table "+Constant.TABLE1_NAME+" ("+
//                Constant.ID+" Integer primary key ,"+
//                Constant.CITY+" varchar(20)) ";
//        sqLiteDatabase.execSQL(sql1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 更改数据库版本的操作,根据新旧版本号进行表结构变更处理
        db.execSQL("drop table if exists " + Constant.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打开数据库后首先被执行
    }
}