package com.example.hellvox.pset3_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class Main2Activity extends AppCompatActivity {

    ArrayList<String> dishesArray = new ArrayList<String>();
    ArrayAdapter<String> name;
    JSONObject arra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        final String menu_value = intent.getStringExtra("menu_item");
        setTitle(menu_value);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        loadFromSharedPrefs();
        name = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dishesArray);
        String url = "https://resto.mprog.nl/menu";

        Context context = getApplicationContext();
        CharSequence text = "Something went wrong, try restarting the app";
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(context, text, duration);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("items");
                        for(int i=0; i<array.length(); i++) {
                            arra = array.optJSONObject(i);
                                if (arra.optString("category").equals(menu_value)) {
                                        dishesArray.add(arra.optString("name"));
                                }
                        }
                        name.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        ListView list = (ListView) findViewById(R.id.dishes);
        list.setAdapter(name);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String chosen_entry= (String) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                intent.putExtra("dish", chosen_entry);
                startActivity(intent);
            }
        });

    }
    public void saveToSharedPrefs(View view) {

    }

    public void loadFromSharedPrefs() {

        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        String s = prefs.getString("dishname",  null);
        if(s !=null) {
            TextView textView = findViewById(R.id.textView2);
            textView.setText(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
