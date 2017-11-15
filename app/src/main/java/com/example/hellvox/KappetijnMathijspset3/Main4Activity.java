package com.example.hellvox.KappetijnMathijspset3;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class Main4Activity extends AppCompatActivity {

    JSONObject arra;
    Button button;
    TextView total;
    TextView last;
    ArrayList<Integer> allDishesIDS = new ArrayList<>();
    ArrayList<Integer> allDishesPrice = new ArrayList<>();
    ArrayList<String> allDishes = new ArrayList<>();
    ArrayList<String> orderArray = new ArrayList<>();
    ArrayList<Food> foodList = new ArrayList<>();
    FoodListAdapater adapter;
    Toast errorToast;
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

        total = findViewById(R.id.total);
        last = findViewById(R.id.orderVieww);
        button = findViewById(R.id.buttonOrder);

        Context context = getApplicationContext();
        CharSequence text = "Something went wrong, try restarting the app";
        int duration = Toast.LENGTH_SHORT;
        errorToast = Toast.makeText(context, text, duration);

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
                TextView total = findViewById(R.id.total);
                total.setText("Total costs: € " + totalCosts);
                saveToSharedPrefs();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),name + " removed!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (foodList.size() > 0) {
                    SharedPreferences.Editor editor = prefs.edit();
                    String url = "https://resto.mprog.nl/order";
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int time = response.optInt("preparation_time");
                            last.setVisibility(View.VISIBLE);
                            last.setText("Your order is coming in: " + time + " Minutes!");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorToast.show();
                        }
                    });
                    // Access the RequestQueue through your singleton class.
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
                    for (int i = 0; i < allDishes.size(); i++) {
                        String temp = allDishes.get(i);
                        editor.putString(temp, null);
                    }
                    editor.apply();
                    total.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Your order has been placed!", Toast.LENGTH_LONG).show();
                    foodList.clear();
                    saveToSharedPrefs();
                    adapter.notifyDataSetChanged();
                    button.setVisibility(View.INVISIBLE);
                } else {Toast.makeText(getApplicationContext(), "You have an empty cart!", Toast.LENGTH_SHORT).show();}
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

    // Code to restore the element visibility for rotation.
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        button.setVisibility(savedInstanceState.getInt("Button"));
        total.setVisibility(savedInstanceState.getInt("Total"));
        last.setVisibility(savedInstanceState.getInt("Last"));
        last.setText(savedInstanceState.getString("lastt"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    // Code to save the element visibility for rotation.
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("Button", button.getVisibility());
        savedInstanceState.putInt("Total", total.getVisibility());
        savedInstanceState.putInt("Last", last.getVisibility());
        savedInstanceState.putString("lastt", last.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }
}
