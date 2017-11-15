package com.example.hellvox.pset3_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main4Activity extends AppCompatActivity {

    JSONObject arra;
    ArrayList<String> allDishes = new ArrayList<String>();
    ArrayList<String> orderArray = new ArrayList<String>();
    ArrayAdapter<String> name;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Your Order");
        loadFromSharedPrefs();
        Context context = getApplicationContext();
        CharSequence text = "Something went wrong, try restarting the app";
        int duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, duration);
        name = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, orderArray);

        String url = "https://resto.mprog.nl/menu";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("items");
                        for(int i=0; i<array.length(); i++) {
                            arra = array.optJSONObject(i);
                            allDishes.add(arra.optString("name"));
                        }
                        loadFromSharedPrefs();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        ListView list = (ListView) findViewById(R.id.orders);
        list.setAdapter(name);
    }

    public void loadFromSharedPrefs() {

        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        for(int i=0; i<allDishes.size(); i++) {
            String temp = allDishes.get(i);
            String s = prefs.getString(temp,  null);
            if(s !=null) {
                orderArray.add(s);
                name.notifyDataSetChanged();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
