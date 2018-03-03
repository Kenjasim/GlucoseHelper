package com.kenjasim.glucosehelper.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenjasim.glucosehelper.R;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class CalcFragment extends Fragment  {
    private EditText editText;
    private Spinner spinner;
    private Button calcButton;
    private String carbratio, bgRatio, ideallevel ;
    private TextView answerET;
    //Classes are declared

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calc, container, false);
        //The layout is inflated
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getActivity().setTitle("Calculator");
        super.onActivityCreated(savedInstanceState);
        editText = (EditText) getView().findViewById(R.id.editText);
        calcButton = (Button) getView().findViewById(R.id.calcButton);
        spinner = (Spinner) getView().findViewById(R.id.spinner);
        answerET = (TextView) getView().findViewById(R.id.Answer);
        //The classes are linked to the ones that were declared in the xml file
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.calculator_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //The spinner is loaded with items

        retrieveRatios();

        //ratios retrieved

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                if (position == 0){
                    calcButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Float answer = Float.parseFloat(editText.getText().toString());
                            String answerStr = answer.toString();
                            if (TextUtils.isEmpty(answerStr)){
                                editText.setError("Enter Something");
                            }
                            Float carb = Float.parseFloat(carbratio);
                            Float carbFloat = (1 / carb);

                            Float number = answer * carbFloat;
                            String numberStr = number.toString();

                            answerET.setText(numberStr);

                            //This calculates the amount for the carbs and setting the text
                        }
                    });



                }if (position == 1){

                    calcButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Float answer = Float.parseFloat(editText.getText().toString());
                            String answerStr = answer.toString();
                            if (TextUtils.isEmpty(answerStr)){
                                editText.setError("Enter Something");
                            }
                            Float BG = Float.parseFloat(bgRatio);
                            Float BGFloat = (1 / BG);
                            Float idealFloat = Float.parseFloat(ideallevel);

                            Float idealtakeactual = (answer - idealFloat);
                            Float BSAns = (idealtakeactual * BGFloat);

                            answerET.setText(BSAns.toString());

                            //This calculates the amount for the blood sugars and setting the text



                        }
                    });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });






    }
    private void retrieveRatios(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference carbRatioRef = database.getReference("users").child(user.getUid()).child("Carbratio");
        DatabaseReference bgRatioRef = database.getReference("users").child(user.getUid()).child("BGRatio");
        DatabaseReference idealLevelRef = database.getReference("users").child(user.getUid()).child("Ideal Level");

        carbRatioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String carbRatio = dataSnapshot.getValue(String.class);
                if (carbRatio == null){
                    carbratio = "10";
                }else{
                    carbratio = carbRatio;
                }
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
                if (BGRatio == null){
                    bgRatio = "1";
                }else{
                    bgRatio = BGRatio;
                }
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
                if (idealLevel == null){
                    ideallevel = "7";
                }else{
                    ideallevel = idealLevel;
                }

                Log.d(TAG, "Value is: " + idealLevel);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
