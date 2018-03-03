package com.kenjasim.glucosehelper.other;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kenjasim.glucosehelper.R;


import java.util.List;

public class CarbAdapter extends ArrayAdapter<CarbData> {
    private Activity context;
    private List<CarbData> carbDataList;
    public CarbAdapter(Activity context, List<CarbData> carbDataList){
        super(context, R.layout.list_item_food, carbDataList);
        this.context = context;
        this.carbDataList = carbDataList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_item_food, null, true);

        TextView foodNameTV = (TextView) listViewItem.findViewById(R.id.foodNameTV);
        TextView carbsTV = (TextView) listViewItem.findViewById(R.id.carbsTV);
        TextView amountTV = (TextView) listViewItem.findViewById(R.id.amountTV);

        CarbData carbData = carbDataList.get(position);

        String carbFront = "Carbs: ";
        String amountFront = "Amount: ";

        foodNameTV.setText(carbData.getFoodName());
        carbsTV.setText(carbFront + carbData.getCarbs());
        amountTV.setText(amountFront + carbData.getAmount());

        return listViewItem;
    }
}






