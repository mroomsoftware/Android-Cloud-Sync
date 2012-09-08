package com.mroom.cloudapp;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mroom.notepad.NotesList;
import com.mroom.widget.MyAccountView;
import com.mroom.widget.ViewContent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends DefaultActivity implements ViewContent.OnItemSelectionListener {
	private static final String TAG = "MainActivity";
	
	GoogleAnalyticsTracker tracker;
	Facebook facebook = new Facebook("363860253684384");
	final static int RQS_RECORDING = 1;
	Context mContext;
	LinearLayout layout;
	ViewContent vContent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		
		/** GA **/
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-34492615-1", 10, this);

		View banner = (View) this.findViewById(R.id.relaBanner);
		AdView adView = (AdView) banner.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		
		layout = (LinearLayout)findViewById(R.id.linearContent);
		
		// Make my account as default
		vContent = new MyAccountView(this, mApi);
		((MyAccountView)vContent).setOnItemSelectionListener(this);
		layout.addView(vContent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RQS_RECORDING) {
			// savedUri = data.getData();
			// Toast.makeText(AndroidIntentAudioRecording.this, "Saved: " +
			// savedUri.getPath(),
			// Toast.LENGTH_LONG).show();
		} else {
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 	
    	if(item.getItemId() == R.id.exit){
    		//close the Activity
    		this.finish();
    		return true;
    	} else if(item.getItemId() == R.id.facebook) {
    		facebook.authorize(this, new String[] { "user_checkins",
    				"friends_checkins", "publish_checkins", "email",
    				"publish_stream", "read_stream", "offline_access" },
    				new DialogListener() {
    					@Override
    					public void onComplete(Bundle values) {
    						Log.i("accesstoken =", "" + facebook.getAccessToken());
    					}

    					@Override
    					public void onFacebookError(FacebookError error) {
    					}

    					@Override
    					public void onError(DialogError e) {
    					}

    					@Override
    					public void onCancel() {
    					}
    				});
    		return true;
    	} else if(item.getItemId() == R.id.note) {
    		tracker.trackPageView("/Notes");
    		
    		Intent intent  = new Intent(MainActivity.this, NotesList.class);
    		startActivity(intent);
    		return true;
    	} else if (item.getItemId() == R.id.photo) {
    		tracker.trackPageView("/Photos");
    		
    		Intent intent  = new Intent(MainActivity.this, PhotoActivity.class);
    		startActivity(intent);
    		return true;
    	} else if (item.getItemId() == R.id.voice) {
    		tracker.trackPageView("/Voices");
			Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
			startActivityForResult(intent, RQS_RECORDING);
    	}
    	return false;
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	finish();
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onItemSelected(int action) {
		// TODO Auto-generated method stub
		if(action == 1) {
			logOut();
			finish();
		}
	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    // Stop the tracker when it is no longer needed.
	    tracker.stopSession();
	  }
	
}
