package com.example.alpha2.note2me;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.SimpleFormatter;



import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Update extends Activity {
    private LinearLayout layout;		//布局
    private TextView update_title;		//标题栏
    private EditText update_content;		//输入框
    private ImageButton backBtn;	//返回
    private ImageButton deleteBtn;	//删除
    private ImageButton confirmBtn;	//确认

    private Dialog delDialog;		//删除对话框
    private Integer s_id;			//记事ID
    private String title;			//标题
    private String content;			//内容
    private operate dm = null;// 数据库管理对象
    private Cursor cursor = null;
    private int background;//背景
    private  SharedPreferences sp;//存储数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);


        layout = (LinearLayout) findViewById(R.id.update);
        dm = new operate(this);// 数据库操作对象
        update_title = (TextView) findViewById(R.id.update_title);
        update_content = (EditText) findViewById(R.id.update_content);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        deleteBtn = (ImageButton) findViewById(R.id.delete_btn);
        confirmBtn = (ImageButton) findViewById(R.id.confirm_btn);
        Intent intent = getIntent();        //恢复未保存数据
        HashMap<String, Object> map = (HashMap<String, Object>) intent.getSerializableExtra("data");
        title = (String) map.get("title");
        content = (String) map.get("content");
        s_id = (Integer) map.get("id");//s_id表示笔记id
        update_title.setText(title);
        update_content.setText(content);

        ImageButton[] btns = {backBtn, deleteBtn, confirmBtn};
        for (ImageButton btn : btns)
            btn.setOnClickListener(click);

    }
    private OnClickListener click=new OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.back_btn:
                    back();
                    break;
                case R.id.delete_btn:
                    delete();
                    break;
                case R.id.confirm_btn:
                    save();
                    break;
            }
        }

    };

    private void save(){
        String noteContent=update_content.getText().toString().trim();
        //简单的日期获取
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=df.format(new Date());
        if(noteContent!=null){
            dm.open();
            int flag=dm.update(s_id, title, noteContent, time);
            if(flag>0){
                Toast.makeText(Update.this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Update.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(Update.this, R.string.note_null, Toast.LENGTH_SHORT).show();//弹出消息
            }
        }else{
            Toast.makeText(Update.this, R.string.note_null, Toast.LENGTH_SHORT).show();
        }


    }

    private void delete(){		//删除记事
        View deleteView = View.inflate(this, R.layout.deletenote, null);
        delDialog=new Dialog(this,R.style.dialog);
        delDialog.setContentView(deleteView);
        Button yesBtn=(Button)deleteView.findViewById(R.id.delete_yes);
        Button noBtn=(Button)deleteView.findViewById(R.id.delete_no);
        yesBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                dm.open();
                dm.delete(s_id);
                Toast.makeText(Update.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                delDialog.dismiss();
                Intent intent=new Intent(Update.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        noBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                delDialog.dismiss();
            }
        });
        delDialog.show();
    }

    private void back(){
        Intent intent=new Intent(Update.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
}
