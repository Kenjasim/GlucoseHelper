package com.kenjasim.glucosehelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText EmailET;
    private Button ResetPasswordButton;
    private FirebaseAuth firebaseAuth;
    // The classes are declared


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        // The java class is linked to the xml design file
        EmailET = (EditText) findViewById(R.id.EmailET);
        ResetPasswordButton = (Button) findViewById(R.id.ResetPasswordButton);
        // The classes declared earlier are pointed to their xml counterparts

        firebaseAuth = FirebaseAuth.getInstance();
        //The authentication instance is retrived
        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(ForgetPasswordActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                // When the user presses the button a progress dialog is made and a message is added, then its shown
                String email = EmailET.getText().toString().trim();
                // String is made and the contents of the edit text is placed in it
                if (isValidEmail(email) == false){
                    // the isValidEmail method is called on the email
                    EmailET.setError("Email Formatted Badly");
                    progressDialog.dismiss();
                    //if it is false then the an error is shown and the progress dismissed
                }else{
                    firebaseAuth.sendPasswordResetEmail(email)//the email is passed to the method
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(ForgetPasswordActivity.this, "Reset Email Sent Sucessfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        progressDialog.dismiss();
                                        // If it was successful then a toast is made and an intent is declared

                                    }else{
                                        Toast.makeText(ForgetPasswordActivity.this, "Reset Email Can Not Be Sent", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        // if the task isn't successful then a toast signifies that the it failed
                                    }
                                }
                            });

                }

            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) { //A boolean is declared
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        // if the text is not empty or is formatted properly then it returns true if not it returns false
    }
}
