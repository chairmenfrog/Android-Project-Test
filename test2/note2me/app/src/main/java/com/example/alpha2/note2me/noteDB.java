package com.example.alpha2.note2me;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class noteDB extends SQLiteOpenHelper{

    private String tableName="notes";//表名
    private Context mContext=null;
    //主键id
    private String sql="create table if not exists "+tableName+
            "( id Integer primary key autoincrement , "+
            "noteTitle varchar(30) NOT NULL,"+//标题
            "noteContent text NOT NULL ,"+//内容
            "noteTime varchar(30))";//时间
    public noteDB(Context context, String name, CursorFactory factory,
                  int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //创建表
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

}
 
 

