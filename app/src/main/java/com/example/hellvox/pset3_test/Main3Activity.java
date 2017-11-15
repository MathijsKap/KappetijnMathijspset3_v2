package com.example.hellvox.pset3_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class Main3Activity extends AppCompatActivity {

    JSONObject arra;
    int dish_id;
    int dish_price;
    String dish_discrip;
    String dish_url_image;
    String dish_name;
    Toast toast;
    Toast toast_added;
    Toast toast_already;
    TextView textCartItemCount;
    int mCartItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent = getIntent();
        dish_name = intent.getStringExtra("dish");
        setTitle(dish_name);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Context context = getApplicationContext();
        CharSequence text = "Something went wrong, try restarting the app";
        int duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, duration);
        CharSequence text_rdy = "Item added to your order!";
        toast_added = Toast.makeText(context, text_rdy, duration);
        CharSequence text_alr = "Item already in your order!";
        toast_already = Toast.makeText(context, text_alr, duration);

        String url = "https://resto.mprog.nl/menu";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("items");
                        for(int i=0; i<array.length(); i++) {
                            arra = array.optJSONObject(i);
                            if (arra.optString("name").equals(dish_name)) {
                                dish_id = arra.optInt("id");
                                dish_price = arra.optInt("price");
                                dish_url_image = arra.optString("image_url");
                                dish_discrip = arra.optString("description");
                                TextView textname = findViewById(R.id.textName);
                                TextView textprice = findViewById(R.id.textPrice);
                                TextView textdisc = findViewById(R.id.textDesc);
                                textname.setText(dish_name);
                                textprice.setText("€ "+ dish_price);
                                textdisc.setText(dish_discrip);
                                dish_url_image = dish_url_image.replace("http", "https");
                                new DownloadImageTask((ImageView) findViewById(R.id.imageFood))
                                        .execute(dish_url_image);
                            }

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        final Button button = findViewById(R.id.buttonAdd);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (loadFromSharedPrefs()) {
                    toast_already.show();
                } else saveToSharedPrefs(v);
            }
        });


    }

    public void saveToSharedPrefs(View view) {
            SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(dish_name, dish_name);
            editor.commit();
            toast_added.show();

    }
    public boolean loadFromSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        String s = prefs.getString(dish_name,  null);
        if(s != null) return true;
        return false;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        switch (item.getItemId()) {
            case R.id.buttonCart: {
                Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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

    private void setupBadge() {
        mCartItemCount = loadFromSharedPrefs2();
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

    public int loadFromSharedPrefs2() {
        SharedPreferences prefs = this.getSharedPreferences("orders", MODE_PRIVATE);
        return prefs.getInt("total",  0);
    }

}
