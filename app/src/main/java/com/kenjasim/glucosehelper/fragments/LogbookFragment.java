package com.kenjasim.glucosehelper.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenjasim.glucosehelper.MainActivity;
import com.kenjasim.glucosehelper.R;
import com.kenjasim.glucosehelper.other.DataAdapter;
import com.kenjasim.glucosehelper.other.GlucoseData;

import java.util.ArrayList;
import java.util.List;

public class LogbookFragment extends Fragment {

    private DatabaseReference databaseReference;
    private ListView entriesList;
    private List<GlucoseData> glucoseDataList;
    private ProgressDialog progressDialog;
    //Classes are declared

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logbook, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getActivity().setTitle("Logbook");
        super.onActivityCreated(savedInstanceState);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("Entries");
        entriesList = (ListView) getActivity().findViewById(R.id.entriesList);
        //The classes are linked to the xml classes


        glucoseDataList = new ArrayList<>();
        final CharSequence options[] = new CharSequence[] {"Edit Selection", "Delete Selection"};
        //The options for the alert dialog is made and added to an array



        entriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            GlucoseData glucoseData = glucoseDataList.get(position);
                            final String dataid = glucoseData.getDataID();
                            String oldBloodReading = glucoseData.getBloodLevels();
                            String oldCarbs = glucoseData.getCarbohydrates();
                            String oldInsulin = glucoseData.getInsulin();
                            String oldNotes = glucoseData.getNotes();
                            final String dateTime = glucoseData.getDateTime();
                            final String timestamp = glucoseData.getTimestamp();
                            LayoutInflater li = LayoutInflater.from(getActivity());
                            final View promptsView = li.inflate(R.layout.prompt_input, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    getActivity());

                            alertDialogBuilder.setView(promptsView);

                            final EditText bloodLevelET = (EditText) promptsView.findViewById(R.id.bloodLevelET);
                            final EditText carbsET = (EditText) promptsView.findViewById(R.id.carbsET);
                            final EditText insulinET = (EditText) promptsView.findViewById(R.id.insulinET);
                            final EditText notesET = (EditText) promptsView.findViewById(R.id.notesET);
                            final Button calcButton = (Button) promptsView.findViewById(R.id.calcButton);

                            calcButton.setVisibility(View.GONE);

                            bloodLevelET.setText(oldBloodReading);
                            carbsET.setText(oldCarbs);
                            insulinET.setText(oldInsulin);
                            notesET.setText(oldNotes);

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    progressDialog = ProgressDialog.show(getActivity(), "Data Is Changing",
                                                            "Please Wait...", true);
                                                    String newBloodReading = bloodLevelET.getText().toString();
                                                    String newCarbs = carbsET.getText().toString();
                                                    String newInsulin = insulinET.getText().toString();
                                                    String newNotes = notesET.getText().toString();
                                                    if (TextUtils.isEmpty(newBloodReading)){
                                                       newBloodReading = "0";
                                                    }if (TextUtils.isEmpty(newCarbs)){
                                                        newCarbs ="0";
                                                    }if (TextUtils.isEmpty(newInsulin)){
                                                        newInsulin = "0";
                                                    }if (TextUtils.isEmpty(newNotes)){
                                                        newNotes = "Not Set";
                                                    }else{

                                                        GlucoseData glucoseData = new GlucoseData(newBloodReading, newCarbs, newInsulin, dateTime, dataid, timestamp, newNotes);
                                                        databaseReference.child(dataid).setValue(glucoseData);
                                                        dialog.cancel();
                                                        progressDialog.dismiss();
                                                        Intent i = new Intent(getActivity(), MainActivity.class);
                                                        glucoseDataList.clear();
                                                        startActivity(i);


                                                    }


                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();
                            //This allows the user to edit the data

                        }else{
                            GlucoseData glucoseData = glucoseDataList.get(position);
                            String dataid = glucoseData.getDataID();
                            databaseReference.child(dataid).removeValue();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            glucoseDataList.clear();
                            startActivity(i);

                            //This allows the user to delete an item

                        }
                    }
                });
                builder.show();

            }
        });

        }




    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                glucoseDataList.clear();
                //This clears the list

                for (DataSnapshot glucoseDataSnapshot: dataSnapshot.getChildren()){

                    GlucoseData glucoseData = glucoseDataSnapshot.getValue(GlucoseData.class);
                    glucoseDataList.add(glucoseData);
                    //This cycles trough the data and adds them through the list

                }

                DataAdapter adapter = new DataAdapter(getActivity(), glucoseDataList);
                entriesList.setAdapter(adapter);

                //This gets the adapter class and sets the adapter to the list


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });
    }
}



