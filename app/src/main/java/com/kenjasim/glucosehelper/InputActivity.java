package com.kenjasim.glucosehelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenjasim.glucosehelper.other.GlucoseData;

import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class InputActivity extends AppCompatActivity {

    private EditText bgET, carbET, insulinET, notesET;
    private Button saveButton,button;
    private DatabaseReference dataRef;
    private ProgressDialog progress;
//    private Float bgInsulin, carbInsulin, Insulin;
    private String carbratio, bgRatio, ideallevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        bgET = (EditText) findViewById(R.id.bgET);
        carbET = (EditText) findViewById(R.id.carbsET);
        insulinET = (EditText) findViewById(R.id.insulinET);
        saveButton = (Button) findViewById(R.id.saveButton);
        notesET = (EditText) findViewById(R.id.notesET);
        button = (Button) findViewById(R.id.Button);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference carbRatioRef = database.getReference("CarbRatio");
        DatabaseReference bgRatioRef = database.getReference("BGRatio");
        DatabaseReference idealLevelRef = database.getReference("Ideal Level");

        carbRatioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String carbRatio = dataSnapshot.getValue(String.class);
                carbratio = carbRatio;
                Log.d(TAG, "Value is: " + carbRatio);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        bgRatioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String BGRatio = dataSnapshot.getValue(String.class);
                bgRatio = BGRatio;
                Log.d(TAG, "Value is: " + BGRatio);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        idealLevelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String idealLevel = dataSnapshot.getValue(String.class);
                ideallevel = idealLevel;
                Log.d(TAG, "Value is: " + idealLevel);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });






        dataRef = FirebaseDatabase.getInstance().getReference("Entries");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgET == null){
                    bgET.setError("Please Enter Something!!");
                }if (carbET == null){
                    carbET.setError("Please Enter Something!!");
                }else{
                    Float BG = Float.parseFloat(bgET.getText().toString());

                    Float carb = Float.parseFloat(carbET.getText().toString());

                    Float bgRat = Float.parseFloat(bgRatio);

                    Float bgRatioFloat = (1/ bgRat);

                    Float carbratioFloat = (1/ Float.parseFloat(carbratio));

                    Float bgInsulin = (BG - Float.parseFloat(ideallevel)) * bgRatioFloat;

                    Float carbInsulin = (carb * carbratioFloat);

                    Float Insulin = (bgInsulin + carbInsulin);

                    insulinET.setText(String.valueOf(Insulin));
                }



            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgET == null){
                    bgET.setError("Please Enter Something!!");
                }if (carbET == null) {
                    carbET.setError("Please Enter Something!!");
                }if (insulinET == null){
                    insulinET.setError("Please Enter Something!!");
                }if (notesET == null){
                    notesET.setText("N/A");
                }else{
                    progress = ProgressDialog.show(InputActivity.this, "Data Saving",
                            "Please Wait...", true);
                    addReading();

                }

            }
        });



            }
    private void addReading(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String BloodLevels = (bgET.getText().toString());
        String Carbohydrates = (carbET.getText().toString());
        String InsulinTaken = (insulinET.getText().toString());
        String DateandTime = DateFormat.getDateTimeInstance().format(new Date());
        String Notes = (notesET.getText().toString());

        String id = dataRef.push().getKey();

        GlucoseData glucoseData = new GlucoseData(BloodLevels, Carbohydrates, InsulinTaken, DateandTime, id, ts, Notes);

        dataRef.child(id).setValue(glucoseData);
        progress.dismiss();
        Intent i = new Intent(InputActivity.this, MainActivity.class);
        startActivity(i);


    }





    }

