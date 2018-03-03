package com.kenjasim.glucosehelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {
    private EditText EmailET, PasswordET;
    private Button RegisterButton;
    private FirebaseAuth firebaseAuth;
    private TextView LoginTV;
    // The classes are declared

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // The java class is linked to the xml design file

        firebaseAuth =  FirebaseAuth.getInstance();

        RegisterButton = (Button) findViewById(R.id.RegisterButton);
        EmailET = (EditText) findViewById(R.id.EmailET);
        PasswordET = (EditText) findViewById(R.id.PasswordET);
        LoginTV = (TextView) findViewById(R.id.LoginTV);
        // The classes declared earlier are pointed to their xml counterparts

        LoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                // An intent takes the user to the Login Activity
            }
        });



        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailET.getText().toString().trim();
                String password = PasswordET.getText().toString().trim();
                // Strings are created and the contents of the edit texts are asigned to them
                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();
                // When the user presses the button a progress dialog is made and a message is added, then its shown


                if (!isValidEmail(email)){
                    // the isValidEmail method is called on the email
                    EmailET.setError("Email Formatted Badly");
                    progressDialog.dismiss();
                    //if it is false then the an error is shown and the progress dismissed
                }if (!isValidPassword(password)){
                    // the isValidPassword method is called on the password
                    PasswordET.setError("Password Formatted Badly");
                    progressDialog.dismiss();
                    //if it is false then the an error is shown and the progress dismissed
                }else{
                    registerUser(email, password);
                    progressDialog.dismiss();
                }

            }
        });
    }

    public void registerUser(String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password) //the email and password are passed to the method
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // then an OnCompleteListener is added
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Failed!" + task.getException(), Toast.LENGTH_SHORT).show();
                            // if the task isnt sucessful then a toast signifies that the registration failed

                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Complete!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                            // if the task is sucessful then they are taken to the Login activity
                        }}
                });
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
}

