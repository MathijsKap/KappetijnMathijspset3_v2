package com.example.hellvox.KappetijnMathijspset3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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


public class MainActivity extends AppCompatActivity {

    ArrayList<String> menuArray = new ArrayList<String>();
    ArrayAdapter<String> name;
    TextView textCartItemCount;
    int mCartItemCount;

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
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.buttonCart);
        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buttonCart: {
                Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        mCartItemCount = loadFromSharedPrefs();
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public int loadFromSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        return prefs.getInt("total",  0);
    }

    public void onResume() {
        // After a pause OR at startup
        super.onResume();
        setupBadge();
    }
}


///Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();