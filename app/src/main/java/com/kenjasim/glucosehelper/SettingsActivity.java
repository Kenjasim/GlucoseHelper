package com.kenjasim.glucosehelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;


public class SettingsActivity extends AppCompatActivity{

    private String dName, dEmail, nName, nEmail, nPass;
    final Context context = this;
    private static final String TAG = "E-mail";
    private FirebaseAuth auth;
    private Button deleteButton, saveDataButton;
    private FirebaseAuth.AuthStateListener authListener;
    private ProgressDialog progressDialog;
    private String carbratio, bgRatio, ideallevel;
    private EditText carbRatioET, insSensET, idealET;
    private ListView accountLV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = FirebaseAuth.getInstance();
        deleteButton = (Button) findViewById(R.id.DeleteAccount);
        saveDataButton = (Button) findViewById(R.id.saveDataButton);
        carbRatioET = (EditText) findViewById(R.id.carbRatioET);
        insSensET = (EditText) findViewById(R.id.insSensET);
        idealET = (EditText) findViewById(R.id.idealET);
        accountLV = (ListView) findViewById(R.id.accountLV);

        retrieveRatios();
        getUserDetails();
        declareAuthListener();
        addItemsToList();




        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();

            }
        });


        //Authlistener is declared

        accountLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    changeEmail();

                }if (position == 1) {
                    changeName();


                }if (position == 2){
                    changePassword();
                }

            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() { //Creates a yes no option
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                final FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();
                                if (Fuser != null) { //checks to see if user is logged in
                                    Fuser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SettingsActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                                        signOut();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                //This deletes the account after the user is sure
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

                carbRatioET.setText(carbratio);
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
                insSensET.setText(bgRatio);
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
                idealET.setText(ideallevel);

                Log.d(TAG, "Value is: " + idealLevel);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void saveData(){
        String carbChange = carbRatioET.getText().toString();
        String insSensChange = insSensET.getText().toString();
        String idealChange = idealET.getText().toString();

        if (TextUtils.isEmpty(carbChange)){
            carbRatioET.setError("Please Enter A Number");

        }if(TextUtils.isEmpty(insSensChange)){
            insSensET.setError("Please Enter A Number");
        }if(TextUtils.isEmpty(idealChange)){
            idealET.setError("Please Enter A Number");
            //Validation
        }else{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference carbRatioRef = database.getReference("users").child(user.getUid()).child("Carbratio");
            DatabaseReference bgRatioRef = database.getReference("users").child(user.getUid()).child("BGRatio");
            DatabaseReference idealLevelRef = database.getReference("users").child(user.getUid()).child("Ideal Level");
            carbRatioRef.setValue(carbChange);
            bgRatioRef.setValue(insSensChange);
            idealLevelRef.setValue(idealChange);

            Toast.makeText(SettingsActivity.this, "Data Has Been Saved", Toast.LENGTH_SHORT).show();

        }

    }

    public final static boolean isValidEmail(CharSequence target) { //A boolean is declared
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        // if the text is not empty or is formatted properly then it returns true if not it returns false
    }

    public final static boolean isValidPassword (CharSequence target){ // A boolean is declared
        if (TextUtils.isEmpty(target)) {
            return false;
            // if the text is empty it returns false
        }if (target.length() < 6) {
            return false;
            // if the password is too short then it returns false
        }else{
            return true;
            // if the password is valid it returns true
        }

    }

    public void declareAuthListener(){

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        };
    }

    public void getUserDetails()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String name = user.getDisplayName();
            String email = user.getEmail();
            //The data that is needed is retrieved from Firebase

            dName = name;
            dEmail = email;
            if (dName == null){
                dName = "NOT SET";
            }if (dEmail == null){
                dEmail = "NOT SET";
            }
            //Validation
        }
    }

    public void changeEmail()
    {
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.prompt_email, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog = ProgressDialog.show(SettingsActivity.this, "Email Is Changing",
                                        "Please Wait...", true);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                nEmail = (userInput.getText().toString().trim());
                                if (user != null && isValidEmail(nEmail)) {
                                    user.updateEmail(userInput.getText().toString().trim())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SettingsActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                                        signOut();
                                                        progressDialog.dismiss();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                } else{
                                    Toast.makeText(SettingsActivity.this, "Email Formatted Badly", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
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

        //This allows the user to input a new email

    }

    public void changeName()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog = ProgressDialog.show(SettingsActivity.this, "Name Is Changing",
                                        "Please Wait...", true);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userInput.getText().toString().trim())
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                    progressDialog.dismiss();
                                                    Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                }else{
                                                    Log.d(TAG, "User Profile Error");
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //This allows the user to input a new name
    }

    public void changePassword()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog = ProgressDialog.show(SettingsActivity.this, "Password Is Changing",
                                        "Please Wait...", true);

                                if (isValidPassword(userInput.getText().toString()) == false){
                                    Toast.makeText(SettingsActivity.this, "Password Formatted Badly", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                }else{
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String newPassword = userInput.getText().toString().trim();

                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "User password updated.");
                                                        signOut();
                                                    }else{
                                                        Toast.makeText(SettingsActivity.this, "Password Not Updated", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();

                                                    }
                                                }
                                            });
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

    }

    public void addItemsToList()
    {
        HashMap<String,String> accountMap = new HashMap<>();
        accountMap.put("Name:", dName);
        accountMap.put("E-mail", dEmail);
        accountMap.put("Password", "********");

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.text1, R.id.text2});


        Iterator rit = accountMap.entrySet().iterator();
        while (rit.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)rit.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }
        //Items are looped through to add them to the list

        accountLV.setAdapter(adapter);
        //Items are shown in the listviews

    }

    public void signOut(){
        auth.signOut();

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);


        }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
        auth.removeAuthStateListener(authListener);
        }
    }}
