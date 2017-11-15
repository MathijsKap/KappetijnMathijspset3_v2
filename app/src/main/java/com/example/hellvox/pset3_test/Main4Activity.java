package com.example.hellvox.pset3_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main4Activity extends AppCompatActivity {

    JSONObject arra;
    ArrayList<Integer> allDishesIDS = new ArrayList<>();
    ArrayList<Integer> allDishesPrice = new ArrayList<>();
    ArrayList<String> allDishes = new ArrayList<>();
    ArrayList<String> orderArray = new ArrayList<>();
    ArrayList<Food> foodList = new ArrayList<>();
    FoodListAdapater adapter;
    Toast errorToast;
    Toast orderToast;
    Toast removeToast;
    int totalCosts = 0;


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
        errorToast = Toast.makeText(context, text, duration);
        CharSequence text3 = "Item removed!";
        removeToast = Toast.makeText(context, text3, duration);
        ListView list = findViewById(R.id.orders);
        adapter = new FoodListAdapater(this, R.layout.adapter_view_layout, foodList);
        list.setAdapter(adapter);
        final SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        String url = "https://resto.mprog.nl/menu";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("items");
                        for(int i=0; i<array.length(); i++) {
                            arra = array.optJSONObject(i);
                            allDishesPrice.add(arra.optInt("price"));
                            allDishesIDS.add(arra.optInt("id"));
                            allDishes.add(arra.optString("name"));
                        }
                        loadFromSharedPrefs();
                        TextView total = findViewById(R.id.total);
                        total.setText("Total costs: € " + totalCosts);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorToast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Food chosen_entry= (Food) parent.getAdapter().getItem(position);
                String name = chosen_entry.getName();
                int price = chosen_entry.getPrice();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(name, null);
                editor.apply();
                foodList.remove(chosen_entry);
                totalCosts -= price;
                TextView total = (TextView) findViewById(R.id.total);
                total.setText("Total costs: € " + totalCosts);
                saveToSharedPrefs();
                adapter.notifyDataSetChanged();
                removeToast.show();
                return true;
            }
        });
        final Button button = findViewById(R.id.buttonOrder);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text2 = "Your order has been placed!";
                orderToast = Toast.makeText(context, text2, duration);
                orderToast.show();

                foodList.clear();
                adapter.notifyDataSetChanged();

            }
        });
    }

    public void saveToSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("total", foodList.size());
        editor.apply();
    }

    public void loadFromSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        for(int i=0; i<allDishes.size(); i++) {
            int tempPrice = allDishesPrice.get(i);
            int tempint = allDishesIDS.get(i);
            String temp = allDishes.get(i);
            String s = prefs.getString(temp,  null);
            if(s !=null) {
                foodList.add(new Food(s, tempPrice, tempint));
                totalCosts += tempPrice;
                orderArray.add(s);
                adapter.notifyDataSetChanged();
                saveToSharedPrefs();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
