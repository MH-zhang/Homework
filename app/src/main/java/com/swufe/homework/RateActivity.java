package com.swufe.homework;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class RateActivity extends AppCompatActivity implements Runnable {

    EditText rmb;
    TextView show;
    Handler handler;

    private  final  String TAG="Rate";

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


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");
                    Log.i(TAG, "handleMessage: dollarRate:" + dollarRate);
                    Log.i(TAG, "handleMessage: euroRate:" + euroRate);
                    Log.i(TAG, "handleMessage: wonRate:" + wonRate);

                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected( MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openConfig();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

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

            Bundle bundle = new Bundle();
            //获取Msg对象用于返回主线程
            Message msg = handler.obtainMessage(5);
            //msg.what= 5 ;
            //msg.obj="Hello from run()";
            msg.obj = bundle;

            //内容发送到队列中
            handler.sendMessage(msg);


            //获取网络数据
        /*URL url =null;
        Document doc = null;
        String html = null;
        try {
             url =new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in =http.getInputStream();

             html =inputStream2String(in);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


            Document doc = null;

            try {
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
                //doc =Jsoup.parse(html);
                Log.i(TAG, "run:" + doc.title());
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);
                Elements tds = table.getElementsByTag("td");
                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "run: " + str1 + "==>" + val);
                    float v = 100f / Float.parseFloat(val);
                    if ("美元".equals(str1)) {
                        bundle.putFloat("dollar-rate", v);
                    } else if ("欧元".equals(str1)) {
                        bundle.putFloat("euro-rate", v);
                    } else if ("韩元".equals(str1)) {
                        bundle.putFloat("won-rate", v);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }













    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(;;){
            int rsz = in.read(buffer, 0, buffer.length) ;
            if(rsz<0)
                break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }

    }

