package com.mroom.cloudapp;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class LoginActivity extends DefaultActivity {
	private static final String TAG = "LoginActivity";
	private boolean mLoggedIn = false;
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login);
        
        // Display the proper UI state if logged in or not
        progressDialog = ProgressDialog.show(this, null, "Checking...");
		progressDialog.setCancelable(true);
        new Thread() {
			public void run() {
				setLoggedIn(mApi.getSession().isLinked());
			}
		}.start();
        
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(true);
            } catch (IllegalStateException ex) {
                showToast("Couldn't authenticate with Dropbox:" + ex.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", ex);
            }
        }
    }
	
	public void btnLinkClicked(View view) {
		mApi.getSession().startAuthentication(LoginActivity.this);
	}
	
	public void btnRegisterClicked(View view) {
		Intent intent  = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
    	progressDialog.dismiss();
    	mLoggedIn = loggedIn;
    	if (mLoggedIn) {
    		Intent intent  = new Intent(LoginActivity.this, MainActivity.class);
    		startActivity(intent);
    	} 
    }
    
}
