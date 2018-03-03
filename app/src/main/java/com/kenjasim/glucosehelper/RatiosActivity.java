package com.kenjasim.glucosehelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class RatiosActivity extends AppCompatActivity {

    private EditText carbRatio, bgRatio, idealLevel;
    private Button saveButton;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference carbReference, bgReference, idealReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratios);

        carbRatio = (EditText) findViewById(R.id.carbRatio);
        bgRatio = (EditText) findViewById(R.id.bgRatio);
        idealLevel = (EditText) findViewById(R.id.idealLevel);
        saveButton = (Button) findViewById(R.id.saveButton);
        carbReference = FirebaseDatabase.getInstance().getReference().child("CarbRatio");
        bgReference = FirebaseDatabase.getInstance().getReference().child("BGRatio");
        idealReference = FirebaseDatabase.getInstance().getReference().child("Ideal Level");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        carbReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String cRatio = dataSnapshot.getValue().toString();
//
//                carbRatio.setText(cRatio);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        bgReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String bgratio = dataSnapshot.getValue().toString();
//
//                bgRatio.setText(bgratio);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        idealReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String iLevel = dataSnapshot.getValue().toString();
//
//                idealLevel.setText(iLevel);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference carbRatioRef = database.getReference("CarbRatio");
                DatabaseReference bgRatioRef = database.getReference("BGRatio");
                DatabaseReference idealLevelRef = database.getReference("Ideal Level");

                carbRatioRef.setValue(carbRatio.getText().toString());
                bgRatioRef.setValue(bgRatio.getText().toString());
                idealLevelRef.setValue(idealLevel.getText().toString());

            }
        });















    }


}
