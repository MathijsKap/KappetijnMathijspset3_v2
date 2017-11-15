package com.example.hellvox.KappetijnMathijspset3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HellVox on 15-11-2017.
 */

public class FoodListAdapater extends ArrayAdapter<Food> {

    private Context mContext;
    int mResource;

    public FoodListAdapater(Context context, int resource, ArrayList<Food> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        int price = getItem(position).getPrice();
        int menuid = 0;

        Food food = new Food(name,price, menuid);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.textView5);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.textView6);

        tvName.setText(name);
        tvPrice.setText("€ "+price);

        return convertView;
    }
}
