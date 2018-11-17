package com.example.alpha2.note2me;

import java.text.SimpleDateFormat;
import java.util.Date;



import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Add extends Activity{

    private LinearLayout layout;         //布局容器
    private EditText add_content;        //内容框
    private EditText add_title;          //标题框
    private ImageButton backBtn;      //返回
    private ImageButton clearBtn;     //清空
    private ImageButton saveBtn;      //保存

    private operate dm = null;// 数据库管理对象
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

        layout=(LinearLayout)findViewById(R.id.add);
        add_content=(EditText)findViewById(R.id.add_content);
        add_title=(EditText)findViewById(R.id.add_title);
        dm = new operate(this);

        saveBtn=(ImageButton)findViewById(R.id.save_btn);
        backBtn=(ImageButton)findViewById(R.id.back_btn);
        clearBtn=(ImageButton)findViewById(R.id.clear_btn);
        ImageButton[] btns={backBtn,clearBtn,saveBtn};
        for(ImageButton btn:btns)
            btn.setOnClickListener(click);
    }

    private OnClickListener click=new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.back_btn:
                    back();
                    break;
                case R.id.clear_btn:
                    clear();
                    break;
                case R.id.save_btn:
                    save();
                default:
                    break;
            }
        }
        //清空内容
        private void clear() {
            // TODO Auto-generated method stub
            View deleteView=View.inflate(Add.this, R.layout.deletenote, null);
            final Dialog clearDialog=new Dialog(Add.this,R.style.dialog);
            clearDialog.setContentView(deleteView);
            Button yesBtn=(Button)deleteView.findViewById(R.id.delete_yes);
            Button noBtn=(Button)deleteView.findViewById(R.id.delete_no);
            yesBtn.setText(R.string.clear_note);
            noBtn.setText(R.string.clear_cancel);
            yesBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    add_title.setText("");
                    add_content.setText("");
                    clearDialog.dismiss();
                }
            });
            noBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    clearDialog.dismiss();
                }
            });
            clearDialog.show();
        }
        //返回主界面
        private void back() {
            // TODO Auto-generated method stub
            Intent intent=new Intent(Add.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        //增加记事记录
        private void save() {
            // 去除文字前后无用控制符
            String noteTitle=add_title.getText().toString().trim();
            if(noteTitle.length()==0)
                noteTitle="无标题";
            String noteContent=add_content.getText().toString().trim();
            //格式化的时间字符保存在日期列中
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String noteTime=df.format(new Date());
            if(noteContent.trim().length()>0){
                dm.open();
                long flag=dm.insert(noteTitle, noteContent, noteTime);
                Toast.makeText(Add.this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Add.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(Add.this, R.string.note_null, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}