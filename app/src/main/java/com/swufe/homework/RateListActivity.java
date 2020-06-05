package com.swufe.homework;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {

    String list_data[]={"one","two","three"};
    int msgWhat = 3;
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";
    private CharSequence IOUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_rate_list);

        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");

        List<String> list1 =new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list1);
        setListAdapter(adapter);

        Thread t = new Thread( this);
        t.start();



        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 5){
                    List<String> retList = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,retList);
                    setListAdapter(adapter);
                    Log.i("handler","reset list...");
                }
                super.handleMessage(msg);
            }
        };


    }
   /* public void run() {
        Log.i("thread","run.....");
        List<String> rateList = new ArrayList<String>();
        try {
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();

           Elements tables =doc.getElementsByTag("table");
         for(Element table:tables){
                Log.i(TAG, "run: table["+i+"]="+table);
                i++;
            }
            Element table6=tables.get(1);
           //获取td中的数据
           Elements tds=table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
        Element td=tds.get(i);
        Element td2=tds.get(i+5);
       //Log.i(TAG, "run: "+td1.text()+"==>"+td2.text());
        String tdStr=td.text();
        String pStr=td2.text();
        rateList.add(tdStr + "=>" + pStr);
                Log.i("td",tdStr + "=>" + pStr);
            }
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(5);

        msg.obj = rateList;
        handler.sendMessage(msg);

        Log.i("thread","sendMessage.....");
    }
    */
   public void run() {
       Log.i("List","run...");
       List<String> retList = new ArrayList<String>();
       Message msg = handler.obtainMessage();
       //String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new data());有问题
       String curDateStr = null;
       Log.i("run","curDateStr:" + curDateStr + " logDate:" + logDate);
       if(curDateStr.equals(logDate)){
           //如果相等，则不从网络中获取数据
           Log.i("run","日期相等，从数据库中获取数据");
           DBManager dbManager = new DBManager(RateListActivity.this);
           for(RateItem rateItem : dbManager.listAll()){
               retList.add(rateItem.getCurName() + "=>" + rateItem.getCurRate());
           }
       }else{
           Log.i("run","日期相等，从网络中获取在线数据");
           //获取网络数据
           try {
               List<RateItem> rateList = new ArrayList<RateItem>();
               URL url = new URL("http://www.usd-cny.com/bankofchina.htm");
               HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
               InputStream in = httpConn.getInputStream();
               //String retStr = IOUtils.toString(in,"gb2312");有问题

               //Log.i("WWW","retStr:" + retStr);
               //需要对获得的html字串进行解析，提取相应的汇率数据...

               String retStr = null;
               Document doc = Jsoup.parse(retStr);
               Elements tables  = doc.getElementsByTag("table");

               Element retTable = tables.get(5);
               Elements tds = retTable.getElementsByTag("td");
               int tdSize = tds.size();
               for(int i=0;i<tdSize;i+=8){
                   Element td1 = tds.get(i);
                   Element td2 = tds.get(i+5);
                   //Log.i("www","td:" + td1.text() + "->" + td2.text());
                   float val = Float.parseFloat(td2.text());
                   val = 100/val;
                   retList.add(td1.text() + "->" + val);

                   RateItem rateItem = new RateItem(td1.text(),td2.text());
                   rateList.add(rateItem);
               }
               DBManager dbManager = new DBManager(RateListActivity.this);
               dbManager.deleteAll();
               Log.i("db","删除所有记录");
               dbManager.addAll(rateList);
               Log.i("db","添加新记录集");

           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }

           //更新记录日期
           SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
           SharedPreferences.Editor edit = sp.edit();
           edit.putString(DATE_SP_KEY, curDateStr);
           edit.commit();
           Log.i("run","更新日期结束：" + curDateStr);
       }

       msg.obj = retList;
       msg.what = msgWhat;
       handler.sendMessage(msg);
   }


}
