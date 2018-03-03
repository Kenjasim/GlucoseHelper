package com.kenjasim.glucosehelper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private EditText EmailET, PasswordET;
    //EditText Class is Declared for the Email and Password
    private FirebaseAuth firebaseAuth;
    //FirebaseAuth class is declared
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    //The AuthStateListneer is Declared
    private Button LoginButton;
    // The Button class is declared
    private TextView ForgetPasswordTV, RegisterTV;
    // TextView class is declared for the Register and Forget Passowrd classes
    private SignInButton googleButton;
    // The google sign in button class is declared
    private static final int RC_SIGN_IN = 1;
    // An integer is declared
    private GoogleApiClient mGoogleApiClient;
    // The GoogleAPIClient is declared
    private static final String TAG = "Login";
    //A String is declared

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // The java class is linked to the xml design file

        EmailET = (EditText) findViewById(R.id.EmailET);
        PasswordET = (EditText) findViewById(R.id.PasswordET);
        LoginButton = (Button) findViewById(R.id.loginButton);
        ForgetPasswordTV = (TextView) findViewById(R.id.forgetPasswordTV);
        RegisterTV = (TextView) findViewById(R.id.RegisterTV);
        firebaseAuth = FirebaseAuth.getInstance();
        googleButton = (SignInButton) findViewById(R.id.googleButton);

        // The classes declared earlier are pointed to their xml counterparts

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            // A new instance of the Auth state listener is declared
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    //This checks to see if the user is already logged in, if so then it sends them to the main activity
                }

            }

        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // This declares the Google sign in options builder, takes a string token and requests the email and builds
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    //A new API client builder is created and a listener for connection failure is added
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

                        // If the connection does fail than a toast is made informing the user that the connection failed

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // otherwise the sign in api from firebase auth is added along with the google sign in options before finishing the build

        googleButton.setOnClickListener(new View.OnClickListener() { //A listener for button clicks is put on the google signin button
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                // When the user presses the button a progress dialog is made and a message is added, then its shown
                signIn();
                // the medthod sign in is called
                progressDialog.dismiss();
                // the progress dialog is then dismissed

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() { //A listener for button clicks is put on the regular sign in button
            @Override
            public void onClick(View v) {
                String email = EmailET.getText().toString();
            String password = PasswordET.getText().toString();
            // Strings are created and the contents of the edit texts are asigned to them
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            // When the user presses the button a progress dialog is made and a message is added, then its shown

                if (isValidEmail(email) == false){
                // the isValidEmail method is called on the email
                EmailET.setError("Email Formatted Badly");
                progressDialog.dismiss();
                //if it is false then the an error is shown and the progress dismissed
            }if (isValidPassword(password) == false){
                // the isValidPassword method is called on the password
                PasswordET.setError("Password Formatted Badly");
                progressDialog.dismiss();
                //if it is false then the an error is shown and the progress dismissed
                }if (isValidPassword(password) == false && isValidEmail(email) == false){
                    EmailET.setError("Error, Badly Formatted");
                    PasswordET.setError("Error, Badly Formatted");
            }else {
                firebaseAuth.signInWithEmailAndPassword(email, password) //the email and password are passed to the method
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            // then an OnCompleteListener is added
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                    // if the task isnt sucessful then a toast signifies that the login failed
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    // if the task is sucessful then they are taken to the main activity
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }


        }
        });

        RegisterTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                // An intent takes the user to the Register Activity
            }
        });

        ForgetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                // An intent takes the user to the Forget Password Activity
            }
        });
    }
    public void onStart() {
        LoginActivity.super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        // when the activity starts then the authStateListener is added
    }

    public void onStop() {
        LoginActivity.super.onStop();
        if (firebaseAuthListener != null) {
            // when the activity stops the app checks if the auth listener is null
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
            // if it is then it is removed
        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        // This declares a signin intent that takes the Sign in API and gets the intent from the client
        startActivityForResult(signInIntent, RC_SIGN_IN);
        // An activity then starts
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null); // a credental is got
        firebaseAuth.signInWithCredential(credential) //It then atempts to sign in using the credential
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            // if it doesnt work then a toast tells the user
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
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
