package com.example.alpha2.note2me;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity {

    private LinearLayout layout; // 布局容器
    private ImageButton back_btn;// 返回按钮
    private EditText search_text;// 搜索框
    private ListView search_lis;// 显示查找信息的listview
    private HashMap<Integer, Integer> idMap;// IDMap
    private Integer s_id;// 记事Id;

    private Dialog delDialog;// 删除对话框
    private operate dm = null;// 数据库管理对象
    private Cursor cursor = null;
    private String word = null;
    private int background;// 背景
    private SharedPreferences sp;// 存储数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        layout = (LinearLayout) findViewById(R.id.search);
        back_btn = (ImageButton) findViewById(R.id.back);
        search_text = (EditText) findViewById(R.id.search_text);
        search_lis = (ListView) findViewById(R.id.search_lis);
        idMap = new HashMap<Integer, Integer>();// 获取记事ID列表

        back_btn.setOnClickListener(click);
        search_text.addTextChangedListener(searchs);// 搜索事件
        dm = new operate(this);// 数据库操作对象
        dm.open();// 连接数据库

    }

    // 搜索事件
    private TextWatcher searchs = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
            // TODO Auto-generated method stub
            word = search_text.getText().toString().trim();
            if (word.length() > 0)
                // 显示查找的记事
                showNotes(word);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }
    };

    private void showNotes(String word) {
        // TODO Auto-generated method stub
        SimpleAdapter adapter = new SimpleAdapter(Search.this, getData(true,
                word), R.layout.listitem, new String[] { "id", "title",
                "content", "time" }, new int[] { R.id.id, R.id.title,
                R.id.content, R.id.time });
        search_lis.setVisibility(View.VISIBLE);
        search_lis.setAdapter(adapter); // 生成记事列表
        search_lis.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) parent;
                HashMap<String, Object> map = (HashMap<String, Object>) listView
                        .getItemAtPosition(position);
                Intent intent = new Intent(Search.this, Update.class);
                intent.putExtra("data", map);
                startActivity(intent);
                finish();

            }
        });
        search_lis.setOnItemLongClickListener(longclick);// 长按删除

    }

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

    private void deletes() { // 删除记事
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
                Toast.makeText(Search.this, R.string.note_deleted,
                        Toast.LENGTH_SHORT).show();
                delDialog.dismiss();
                showNotes(word);
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

    private List<Map<String, Object>> getData(boolean desc, String word) {
        // TODO Auto-generated method stub
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        dm.open();
        cursor = dm.selectWord(word);
        int pos = 0;
        while (cursor.moveToNext()) {
            //笔记的行标取得对应字符存放键值对
            int n_id = cursor.getInt(cursor.getColumnIndex("id"));
            idMap.put(pos, n_id);
            pos += 1;
            String noteTitle = cursor.getString(cursor.getColumnIndex("noteTitle"));
            String noteContent = cursor.getString(cursor
                    .getColumnIndex("noteContent"));
            String noteTime = cursor.getString(cursor.getColumnIndex("noteTime"));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", n_id);
            map.put("title", noteTitle);
            map.put("content", noteContent);
            map.put("time", noteTime);
            list.add(map);
        }
        cursor.close();
        dm.close();
        return list;
    }

    private OnClickListener click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.back:
                    back();
                    break;

                default:
                    break;
            }
        }

        private void back() {
            // TODO Auto-generated method stub
            Intent intent = new Intent(Search.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
