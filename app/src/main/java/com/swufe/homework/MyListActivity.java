package com.swufe.homework;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<String>  data =new ArrayList<String>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

       ListView listview = findViewById(R.id.mylist);

        /*for(int i=0;i<10;i++){
            data.add("item"+i);
        }

         */

        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listview.setAdapter(adapter);
        listview.setEmptyView(findViewById(R.id.nodata));



        listview.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
        adapter.remove(listview.getItemAtPosition(position));
        //adapter.notifyDataSetChanged();
    }
}
