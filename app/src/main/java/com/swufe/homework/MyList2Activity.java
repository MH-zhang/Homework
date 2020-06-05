package com.swufe.homework;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements AdapterView.OnItemClickListener {

    Handler handler;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器
    private int msgWhat = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2);

        initListView();
        this.setListAdapter(listItemAdapter);

        MyAdapter myAdapter = new MyAdapter(this,R.layout.activity_my_list2,listItems);
        this.setListAdapter(myAdapter);

        getListView().setOnItemClickListener(this);

        Thread t = new Thread((Runnable) this); // 创建新线程
        t.start();

       /*handler=(Handler)handleMessage(msg) {
            if(msg.what == msgWhat){
                List<HashMap<String, String>> list2 = (List<HashMap<String, String>>) msg.obj;
                SimpleAdapter adapter = new SimpleAdapter(RateListActivity.this, list2, // listItems数据源
                        R.layout.activity_my_list2, // ListItem的XML布局实现
                        new String[] { "ItemTitle", "ItemDetail" },
                        new int[] { R.id.itemTitle, R.id.itemDetail });
                setListAdapter(adapter);
                Log.i("handler","reset list...");
            }
            super.handleMessage(msg);
        }

        */

    }
    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "Rate： " + i); // 标题文字
            map.put("ItemDetail", "detail" + i); // 详情描述
            listItems.add(map);
        }
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
                R.layout.activity_my_list2, // ListItem的XML布局实现
                new String[] { "ItemTitle", "ItemDetail" },
                new int[] { R.id.itemTitle, R.id.itemDetail }
        );
    }
    public void run() {
        Log.i("thread","run.....");
        List<String> rateList = new ArrayList<String>();
        try {
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();

            Elements tables =doc.getElementsByTag("table");
         /* for(Element table:tables){
                Log.i(TAG, "run: table["+i+"]="+table);
                i++;
            } */
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

        Message msg = handler.obtainMessage(7);

        msg.obj = rateList;
        handler.sendMessage(msg);

        Log.i("thread","sendMessage.....");
    }
    /*public void handleMessage(Message msg) {
        if(msg.what == msgWhat){
            List<HashMap<String, String>> retList = (List<HashMap<String, String>>) msg.obj;
            SimpleAdapter adapter = new SimpleAdapter(RateListActivity.this, retList, // listItems数据源
                    R.layout.activity_my_list2, // ListItem的XML布局实现
                    new String[] { "ItemTitle", "ItemDetail" },
                    new int[] { R.id.itemTitle, R.id.itemDetail });
            setListAdapter(adapter);
            Log.i("handler","reset list...");
        }
        super.handleMessage(msg);
     }
     */






    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Log.i(TAG, "onItemClick: parent=" + parent);
        Log.i(TAG, "onItemClick: view=" + view);
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: id=" + id);

         */

        //从ListView中获取选中数据
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        /*Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: detailStr=" + detailStr);

         */

        //从View中获取选中数据
        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        /*Log.i(TAG, "onItemClick: title2=" + title2);
        Log.i(TAG, "onItemClick: detail2=" + detail2);

         */

        //打开新的页面，传入参数
        Intent rateCalc = new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }
}
