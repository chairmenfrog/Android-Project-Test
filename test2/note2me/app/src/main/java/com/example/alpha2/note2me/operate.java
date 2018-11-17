package com.example.alpha2.note2me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class operate {
    private Context mContext = null;

    private SQLiteDatabase mSQLiteDatabase = null;// 用于操作数据库的对象
    private noteDB dh = null;// 用于创建数据库的对象

    private String dbName = "notes.db";
    private int dbVersion = 1;

    public operate(Context context) {
        mContext = context;
    }

    public operate() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 打开数据库
     */
    public void open() {

        try {
            dh = new noteDB(mContext, dbName, null, dbVersion);
            if (dh == null) {
                // Log.v("msg", "is null");
                return;
            }
            mSQLiteDatabase = dh.getWritableDatabase();
            // dh.onOpen(mSQLiteDatabase);

        } catch (SQLiteException se) {
            se.printStackTrace();
        }
    }

    /**
     * 关闭数据库
     */
    public void close() {

        mSQLiteDatabase.close();
        dh.close();

    }

    // 获取列表
    public Cursor selectAll(boolean sort_desc) {
        Cursor cursor = null;
        String sql = null;
        try {
            sql = "select * from notes order by noteTime "
                    + (sort_desc != true ? "" : "desc");// 倒序
            cursor = mSQLiteDatabase.rawQuery(sql, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            cursor = null;
        }
        return cursor;
    }



    // 根据内容查找
    public Cursor selectWord(String word) {
        Cursor cursor = null;
        System.out.println("---data----word" + word);
        try {
            String sql = " select * from notes where noteTitle like '" + "%"
                    + word + "%' or noteContent like '" + "%" + word
                    + "%' order by noteTime desc";
            cursor = mSQLiteDatabase.rawQuery(sql, null);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            cursor = null;
        }
        return cursor;
    }

    // 插入数据
    public long insert(String Title, String Content, String Time) {

        long flag = -1;
        try {
            ContentValues cv = new ContentValues();
            cv.put("noteTitle", Title);
            cv.put("noteContent", Content);
            cv.put("noteTime", Time);
            System.out.println("----insert"+Time);
            flag = mSQLiteDatabase.insert("notes", null, cv);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flag;

    }

    // 删除数据
    public int delete(long id) {
        int affect = 0;
        try {
            affect = mSQLiteDatabase.delete("notes", "id=?", new String[] { id
                    + "" });
        } catch (Exception ex) {
            ex.printStackTrace();
            affect = -1;
        }

        return affect;
    }

    // 修改数据
    public int update(int id, String Title, String Content, String Time) {
        int affect = 0;
        try {
            ContentValues cv = new ContentValues();

            cv.put("noteTitle", Title);
            cv.put("noteContent", Content);
            cv.put("noteTime", Time);
            String w[] = { id + "" };
            affect = mSQLiteDatabase.update("notes", cv, "id=?", w);
        } catch (Exception ex) {
            ex.printStackTrace();
            affect = -1;
        }
        return affect;
    }

}