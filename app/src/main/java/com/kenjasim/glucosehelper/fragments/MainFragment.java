package com.kenjasim.glucosehelper.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kenjasim.glucosehelper.R;
import com.kenjasim.glucosehelper.other.GlucoseData;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private Query databaseQuery;
    private DatabaseReference databaseReference;
    private List <Float> glucosePieChart;
    private Float Below = 0f;
    private Float Above = 0f;
    private Float On = 0f;
    private String[] xData = {"Above Target", "On Target", "Below Target"};
    private String ideallevel;
    private TextView averageTV, latestBGTV;
    PieChart pieChart;

    //The classes are declared here

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
        //The layout is inflated to the activity
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Home");
        //The title is set to home

        averageTV = (TextView) getActivity().findViewById(R.id.averageTV);
        latestBGTV = (TextView) getActivity().findViewById(R.id.latestBGTV);
        pieChart = (PieChart) getActivity().findViewById(R.id.pieChart);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //The classes are linked to the classes declared in the xml file


        glucosePieChart = new ArrayList<>();
        //Here i am declaring the list as a new array list

        databaseQuery = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("Entries").orderByChild("timestamp").limitToLast(1);
        //Here i declare a query with the timestamp to get the latest result

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idealLevelRef = database.getReference().child("users").child(user.getUid()).child("Ideal Level");
        idealLevelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String idealLevel = dataSnapshot.getValue(String.class);
                ideallevel = idealLevel;
                Log.d(GifHeaderParser.TAG, "Value is: " + idealLevel);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(GifHeaderParser.TAG, "Failed to read value.", error.toException());
            }
        });

        //Here i get the ideal level the user input

        if(ideallevel == null){
            ideallevel = "7";
        }
        //If it is null then the ideal level will be set to 7

        final Float idealLevelInt = Float.parseFloat(ideallevel);
        final Float idealLevelUBInt = idealLevelInt + 1;
        final Float idealLevelLBInt = idealLevelInt - 1;
        //This creates a range that allows the pie chart to work

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("Entries");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot glucoseDataSnapshot: dataSnapshot.getChildren()) {

                    GlucoseData glucoseData = glucoseDataSnapshot.getValue(GlucoseData.class);
                    Float add = Float.parseFloat(glucoseData.getBloodLevels());
                    glucosePieChart.add(add);

                    //This cycles through the children and adds the blood levels in those to the list


                }



                double average = 0.0;
                if (glucosePieChart.size() != 0){
                    for (int i = 0; i < glucosePieChart.size(); i++)  {
                        average += glucosePieChart.get(i);
                        //This sets the double as the sum of the items in the list
                    }

                    Double ans = average/glucosePieChart.size();
                    //It is then divided by the size of the list

                    BigDecimal bd = new BigDecimal(ans);
                    bd = bd.setScale(1, RoundingMode.HALF_UP);
                    //This rounds it down to 1dp

                    String ansS = bd.toString();
                    averageTV.setText(ansS);
                    //This sets the text view to the average

                    //This calculates the average of the data

                }

                Below = 0f;
                Above = 0f;
                On = 0f;




                for (int i=0; i<glucosePieChart.size(); i++) {
                    if (glucosePieChart.get(i) < idealLevelLBInt){
                        Below = Below + 1f;
                    }if (glucosePieChart.get(i) > idealLevelUBInt){
                        Above = Above + 1f;
                    }if (glucosePieChart.get(i) >= idealLevelLBInt || glucosePieChart.get(i) <= idealLevelUBInt){
                        On = On + 1f;
                    }
                }
                //This tells us how much of the data is on below or above



                On = On - 2;





                Float yData[] = {Below, On , Above};
                ArrayList<PieEntry> yEntries = new ArrayList<>();
                ArrayList<String> xEntries = new ArrayList<>();

                for (int i = 0; i < yData.length; i++){
                    yEntries.add(new PieEntry(yData[i], i));
                }
                for (int i = 0; i < xData.length; i++){
                    xEntries.add(xData[i]);
                }
                PieDataSet pieDataSet = new PieDataSet(yEntries, "Blood Levels");
                pieDataSet.setSliceSpace(0);
                pieDataSet.setValueTextSize(0);

                ArrayList<Integer> colours = new ArrayList<>();
                colours.add(Color.YELLOW);
                colours.add(Color.GREEN);
                colours.add(Color.RED);

                pieDataSet.setColors(colours);

                Legend legend = pieChart.getLegend();
                legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                legend.setEnabled(true);

                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.invalidate();
                pieChart.setRotationEnabled(true);
                pieChart.setHoleRadius(0);
                pieChart.setTransparentCircleAlpha(0);

                //This creates and edits the piechart




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });




        }





    @Override
    public void onStart() {
        super.onStart();
        databaseQuery.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot glucoseDataSnapshot: dataSnapshot.getChildren()){
                //This goes through the children for this

                GlucoseData glucoseData = glucoseDataSnapshot.getValue(GlucoseData.class);
                //New instance of the glucose data class is made
                latestBGTV.setText(glucoseData.getBloodLevels());
                //This sets the textView text to the blood levels
            }




        }

        @Override
        public void onCancelled(DatabaseError databaseError) {



        }
    });
    }
}



