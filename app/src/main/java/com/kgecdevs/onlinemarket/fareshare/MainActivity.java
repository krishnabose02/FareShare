package com.kgecdevs.onlinemarket.fareshare;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_LENGTH = 1000;
    private final int RC_SIGN_IN=123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView welcome = findViewById(R.id.welcometext);
        if(!welcome.isInEditMode())
        {
            Typeface tf = Typeface.createFromAsset(this.getAssets(), "monthoers.ttf");
            welcome.setTypeface(tf);
        }
        final FirebaseAuth mauth=FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(mauth.getCurrentUser()==null)
                {
                    showSignInOption();
                }
                else
                {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void showSignInOption() {
        findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGoogleSignIn(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                showSnackbar("Sign In successful");
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                    setGoogleAccount(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    //Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(this, "Google SignIn failed", Toast.LENGTH_SHORT).show();
                    // ...
                }
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar("Data connection lost");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar("Unknown response");
                    return;
                }
            }
            showSnackbar("weird stuff, something's not right");
        }
    }

    private void setGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential);
    }


    public void showSnackbar(String text)
    {
        Snackbar bar = Snackbar.make(findViewById(R.id.rootofmain), text, Snackbar.LENGTH_SHORT);
        bar.show();
    }

    public void startGoogleSignIn(View view) {
        Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }
}
