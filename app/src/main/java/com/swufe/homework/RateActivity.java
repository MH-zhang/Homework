package com.swufe.homework;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity implements Runnable {

    EditText rmb;
    TextView show;
    Handler handler;

     float dollarRate=0.1f;
     float euroRate=0.2f;
     float wonRate =0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);

        //获取SP里地保存数据
        SharedPreferences sharedPreferences =getSharedPreferences("myrate", Activity.MODE_PRIVATE);//myrate存储数据的空间
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);获取数据第二种方法，高版本SDK才能用
        dollarRate=sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate=sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate=sharedPreferences.getFloat("won_rate",0.0f);//默认值

        //开启子线程
        Thread t =new Thread(this);//一定记得this
        t.start();

        handler =new Handler(){
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    String str = (String) msg.obj;
                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        };



    }
    public void onClick(View btn){
        String str =rmb.getText().toString();
        float r=0;
        if(str.length()>0){
            r =Float.parseFloat(str);
        }else{
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        float val=0;

        if(btn.getId()==R.id.btn_dollar){
            val =r*dollarRate;

        }else if(btn.getId()==R.id.btn_euro){
            val =r*euroRate;

        }else{
             val =r*wonRate;

        }
        float v =(float)(Math.round(val*100))/100;
        show.setText(String.valueOf(v));
    }
    public void openOne(View btn){
        openConfig();

    }



    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openConfig();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("key_euro", 0.2f);
            wonRate = bundle.getFloat("key_won", 0.3f);

            //将新设置的汇率写到SP
            SharedPreferences sharedPreferences =getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);

            //保存
            editor.commit();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        for (int i=1;i<6;i++){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //获取Msg对象用于返回主线程
        Message msg =handler.obtainMessage(5);
        //msg.what= 5 ;
        msg.obj="Hello from run()";

        //内容发送到队列中
        handler.sendMessage(msg);

    }
}
