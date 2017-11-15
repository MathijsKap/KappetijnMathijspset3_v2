package com.example.hellvox.pset3_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> menuArray = new ArrayList<String>();
    ArrayAdapter<String> name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Restaurent");

        name = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray);
        String url = "https://resto.mprog.nl/categories";
        Context context = getApplicationContext();
        CharSequence text = "Something went wrong, try restarting the app";
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(context, text, duration);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("categories");
                        for(int i=0; i<array.length(); i++) {
                            menuArray.add(array.optString(i));
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
        ListView list = (ListView) findViewById(R.id.menus);
        list.setAdapter(name);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String chosen_entry= (String) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("menu_item", chosen_entry);
                startActivity(intent);
            }
        });

        final Button button = findViewById(R.id.buttonCart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
                startActivity(intent);
            }
        });

    }

}


///Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();