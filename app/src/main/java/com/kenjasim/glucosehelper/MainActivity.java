package com.kenjasim.glucosehelper;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenjasim.glucosehelper.fragments.CalcFragment;
import com.kenjasim.glucosehelper.fragments.FoodsFragment;
import com.kenjasim.glucosehelper.fragments.LogbookFragment;
import com.kenjasim.glucosehelper.fragments.MainFragment;
import com.kenjasim.glucosehelper.other.CarbData;
import com.kenjasim.glucosehelper.other.CircleTransform;
import com.kenjasim.glucosehelper.other.GlucoseData;


import java.util.Date;



import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kenjasim.glucosehelper.R.mipmap.ic_launcher;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private NavigationView navigationView;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private ProgressDialog progressDialog;
    final Context context = this;
    private DatabaseReference dataRef, dataRefFood;
    private String carbratio, bgRatio, ideallevel;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;

//Here all the classes that are needed are decalred




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("Entries");
        dataRefFood = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Foods");
        if (savedInstanceState == null) {
            displayScreen(R.id.nav_home);
        }
        // This makes the default fragment the home fragment

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);

        //Links the FAB to the xml decalred classes

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               inputDialog();

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInputDialog();
            }
        });

        //Code for what happens when the FABs are pressed


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    //if the user is not logged in then the app takes them to the login screen
                }else{
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    //the users name and email are passed to strings

                }
            }
        };


        retrieveRatios();
        //The blood level ratios are retrieved


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //This declares the drawer layout and navigation drawer

        loadNavHeader();
        // load nav menu header data

    }

        private void loadNavHeader() {
            // name, website
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {

                    String name = profile.getDisplayName();
                    String email = profile.getEmail();
                    Uri photoUrl = profile.getPhotoUrl();
                    //The name the email and the photoURL is retrieved and passed to strings

                    if (name == null){
                        txtName.setText("NAME NOT SET");
                        //If the name is null then the name is set to Name not set
                    }else{
                        txtName.setText(name);
                        //If not then the name is set
                    }

                    if (photoUrl == null){
                        Glide.with(this).load(ic_launcher)
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(this))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgProfile);
                        //If the photo is not there then the app logo is set

                    }else{
                    Glide.with(this).load(photoUrl)
                            .crossFade()
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(this))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                        //Else the users profile pic is set
                }}


            }
        }

    @Override
    public void onBackPressed() {
        //This allows the user when the drawer is open to press a back button to close the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            signOut();
            return true;
            //If the item 'logout' is pressed then the sign out method is excecuted
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayScreen(item.getItemId());
        //when a particular item is pressed that item id is passes to the display screen method
        return true;
    }

    private void displayScreen(int itemID){
        Fragment fragment = null;
        //first a new instance of the fragment class is made and is set as null
        switch(itemID){
            case R.id.nav_home:
                fragment = new MainFragment();
                break;
            case R.id.nav_food:
                fragment = new FoodsFragment();
                break;
            case R.id.nav_calc:
                fragment = new CalcFragment();
                break;
            case R.id.nav_logbook:
                fragment = new LogbookFragment();
                break;
            //This checks through each of the classes and if they are the partcular item then the fragmet is set as which ever fragment is relevant
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            // If the case is for the settings item then the settings activity is opened
            case R.id.nav_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String shareBody = "Get Glucose Helper";
                i.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                i.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(i, "Share using"));
                break;
            //if the case is the share item then the share action is opened


       }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FrameContent, fragment);
            ft.commit();
            //this starts the fragment transaction when the fragment isnt null
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        //This just links the drawer layout to the method
    }



    public void signOut() {
        firebaseAuth.signOut();
        //This is for signing out
    }

    private void displayInputDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.prompt_food, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText foodNameET = (EditText) promptsView.findViewById(R.id.foodNameET);
        final EditText carbsET = (EditText) promptsView.findViewById(R.id.carbsET);
        final EditText amountET = (EditText) promptsView.findViewById(R.id.amountET);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog = ProgressDialog.show(MainActivity.this, "Data Is Being Added",
                                        "Please Wait...", true);
                                String foodName = foodNameET.getText().toString();
                                String carbs = carbsET.getText().toString();
                                String amount = amountET.getText().toString();

                                if (TextUtils.isEmpty(foodName)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(carbs)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(amount)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }else{
                                    String dataid = dataRefFood.push().getKey();

                                    CarbData carbData = new CarbData(foodName, carbs, amount, dataid);
                                    dataRefFood.child(dataid).setValue(carbData);
                                    progressDialog.dismiss();
                                    dialog.cancel();
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

        //This shows the alert dialog for the inputing of food data

    }

    private void inputDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.prompt_input, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);
        // Here the alert dialog is linked to the xml design file I made for it
        final EditText bloodLevelET = (EditText) promptsView.findViewById(R.id.bloodLevelET);
        final EditText carbsET = (EditText) promptsView.findViewById(R.id.carbsET);
        final EditText insulinET = (EditText) promptsView.findViewById(R.id.insulinET);
        final EditText notesET = (EditText) promptsView.findViewById(R.id.notesET);
        Button calcButton = (Button) promptsView.findViewById(R.id.calcButton);
        //Here the classes I declared in the xml files are linked to classes I declared here



        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bloodLevel = bloodLevelET.getText().toString();
                String carbs = carbsET.getText().toString();

                if (TextUtils.isEmpty(bloodLevel)){
                    bloodLevelET.setError("Please Enter Something!!");
                }if (TextUtils.isEmpty(carbs)){
                    carbsET.setError("Please Enter Something!!");
                }else{
                    Float BG = Float.parseFloat(bloodLevelET.getText().toString());

                    Float carb = Float.parseFloat(carbsET.getText().toString());

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

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog = ProgressDialog.show(MainActivity.this, "Data Is Being Added",
                                        "Please Wait...", true);
                                String bloodReading = bloodLevelET.getText().toString();
                                String carbs = carbsET.getText().toString();
                                String insulin = insulinET.getText().toString();
                                String notes = notesET.getText().toString();

                                if (TextUtils.isEmpty(bloodReading)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(carbs)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(insulin)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(notes)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }else{
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();
                                    String dateTime = DateFormat.getDateTimeInstance().format(new Date());
                                    String dataid = dataRef.push().getKey();

                                    GlucoseData glucoseData = new GlucoseData(bloodReading, carbs, insulin, dateTime, dataid, ts, notes);
                                    dataRef.child(dataid).setValue(glucoseData);
                                    progressDialog.dismiss();
                                    dialog.cancel();





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

        //This shows the alert dialog for the inputing of data

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


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
        //This adds the auth state listener
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
            //and if the user isn't null then it removes the listener
        }
    }

}

