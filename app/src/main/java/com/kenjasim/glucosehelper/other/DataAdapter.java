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



public class DataAdapter extends ArrayAdapter<GlucoseData> {

    private Activity context;
    private List<GlucoseData> glucoseDataList;

    public DataAdapter(Activity context, List<GlucoseData> glucoseDataList){
        super(context, R.layout.list_item_log, glucoseDataList);
        this.context = context;
        this.glucoseDataList = glucoseDataList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_item_log, null, true);

        TextView dateTime = (TextView) listViewItem.findViewById(R.id.dateTime);
        TextView bgReading = (TextView) listViewItem.findViewById(R.id.bgReading);
        TextView carbohydrate = (TextView) listViewItem.findViewById(R.id.carbohydrate);
        TextView insulin = (TextView) listViewItem.findViewById(R.id.insulin);
        TextView notes = (TextView) listViewItem.findViewById(R.id.notes);

        GlucoseData glucoseData = glucoseDataList.get(position);

        String bgFront = "Blood Sugar: ";
        String carbFront = "Carbohydrates: ";
        String insulinFront = "Insulin: ";
        String notesFront = "Notes: ";

        dateTime.setText(glucoseData.getDateTime());
        bgReading.setText(bgFront + glucoseData.getBloodLevels());
        carbohydrate.setText(carbFront + glucoseData.getCarbohydrates());
        insulin.setText(insulinFront+ glucoseData.getInsulin());
        notes.setText(notesFront+ glucoseData.getNotes());

        return listViewItem;
    }
}



