package com.example.alpha2.note2me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LinearLayout layout;// 布局
    private Integer s_id;// 记事d，id属性用来在view中操作
    private TextView titleTxt;// 标题
    private Dialog delDialog;// 删除对话框
    private ImageButton addBtn;// 添加
    private ImageButton searchBtn;// 搜索
    private ListView notesList;// 记事列表
    private boolean sort;// 排序标识
    private HashMap<Integer, Integer> idMap;// 存放记事ID列表
    private operate dm = null;// 数据库管理对象
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sort = true;
        idMap = new HashMap<Integer, Integer>();// 获取记事ID列表
        layout = (LinearLayout) findViewById(R.id.main);
        titleTxt = (TextView) findViewById(R.id.title_main);
        addBtn = (ImageButton) findViewById(R.id.add_btn);
        searchBtn = (ImageButton) findViewById(R.id.search_btn);
        notesList = (ListView) findViewById(R.id.notes_lis);

        dm = new operate(this);// 数据库操作对象
        // 添加按钮事件监听
        ImageButton[] btns = {addBtn, searchBtn};
        for (ImageButton btn : btns)
            btn.setOnClickListener(click);
        showNotes(sort);
        if(titleTxt==null||idMap==null||layout==null||titleTxt==null||notesList==null){
            Log.e("onCreate: ", "view error");
        }
    }

    // 删除记事
    private void deletes() {
        View deleteView = View.inflate(this, R.layout.deletenote, null);
        delDialog = new Dialog(this, R.style.dialog);
        delDialog.setContentView(deleteView);
        Button yesBtn = (Button) deleteView.findViewById(R.id.delete_yes);
        Button noBtn = (Button) deleteView.findViewById(R.id.delete_no);

        yesBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dm.open();
                dm.delete(s_id);
                Toast.makeText(MainActivity.this, R.string.note_deleted,
                        Toast.LENGTH_SHORT).show();
                delDialog.dismiss();
                showNotes(sort);
            }
        });
        noBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delDialog.dismiss();
            }
        });
        delDialog.show();
        }

        // 显示记事信息
    private void showNotes(boolean model) {
        // TODO Auto-generated method stub

        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                getData(sort), R.layout.listitem, new String[] { "id",
                "title", "content", "time" },
                new int[] { R.id.id, R.id.title, R.id.content, R.id.time});
        notesList.setVisibility(View.VISIBLE);
        notesList.setAdapter(adapter); // 生成记事列表
        notesList.setOnItemClickListener(listCheck);// 查看信息
        notesList.setOnItemLongClickListener(longclick);// 长按事件


    }

    // 点击视图的单个记事事件，查看信息
    private OnItemClickListener listCheck = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            ListView listView = (ListView) parent;
            HashMap<String, Object> map = (HashMap<String, Object>) listView
                    .getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, Update.class);
            intent.putExtra("data", map);
            startActivity(intent);
            finish();
        }
    };

    // 长按事件
    public OnItemLongClickListener longclick = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            // TODO Auto-generated method stub
            s_id = idMap.get(position);
            deletes();
            return false;
        }



    };

    // 获取记事信息
    private List<Map<String, Object>> getData(boolean sort_desc) {
        // TODO Auto-generated method stub
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        dm.open();
        cursor = dm.selectAll(sort_desc);
        if(cursor==null){
            Log.e("getData", "cursor error " );
        }
        int pos = 0;
        //游标遍历表内所有元素

        while (cursor.moveToNext()) {
            int n_id = cursor.getInt(cursor.getColumnIndex("id"));
            idMap.put(pos, n_id);
            pos += 1;
            String n_title = cursor.getString(cursor.getColumnIndex("noteTitle"));
            String n_content = cursor.getString(cursor
                    .getColumnIndex("noteContent"));
            String n_time = cursor.getString(cursor.getColumnIndex("noteTime"));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", n_id);
            map.put("title", n_title);
            map.put("content", n_content);
            map.put("time", n_time);

            list.add(map);
        }
        if(list.isEmpty()){
            Log.e("geData","cursor error");
        }
        cursor.close();
        dm.close();

        return list;
    }

    // 点击事件监听
    private OnClickListener click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_btn:
                    add_btn();
                    break;
                case R.id.search_btn:
                    search_btn();
                    break;
            }
        }

        private void search_btn() {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MainActivity.this, Search.class);
            startActivity(intent);
            finish();
        }

        private void add_btn() {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MainActivity.this, Add.class);
            if (getIntent().hasExtra("title"))
                intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        }
    };


}
