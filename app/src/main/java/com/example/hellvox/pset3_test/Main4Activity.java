package com.example.hellvox.pset3_test;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Main4Activity extends AppCompatActivity {

    ArrayList<String> orderArray = new ArrayList<String>();
    ArrayAdapter<String> name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        loadFromSharedPrefs();

        name = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, orderArray);

        ListView list = (ListView) findViewById(R.id.orders);
        list.setAdapter(name);
    }

    public void loadFromSharedPrefs() {

        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        String s = prefs.getString("dishname",  null);
        if(s !=null) {
            orderArray.add(s);
        }
    }
}
