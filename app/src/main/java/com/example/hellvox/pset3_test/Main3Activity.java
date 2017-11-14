package com.example.hellvox.pset3_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent = getIntent();
        final String dish_name = intent.getStringExtra("dish");
        setTitle(dish_name);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Context context = getApplicationContext();
        CharSequence text = "Something went wrong, try restarting the app";
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(context, text, duration);

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
                        /*for(int i=0; i<array.length(); i++) {
                            dishesArray.add(array.optString(i));
                        }
                        TextView textView = findViewById(R.id.textView2);
                        textView.setText("" + array.length());*/

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
}
