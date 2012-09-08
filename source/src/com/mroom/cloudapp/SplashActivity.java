package com.mroom.cloudapp;

import com.mroom.lib.Rotate3dAnimation;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {
	
	private LinearLayout layout;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        layout = (LinearLayout) findViewById(R.id.splash_activity_layout);
        new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) { }
				
				runOnUiThread( new Runnable() {					
					@Override
					public void run() {
						applyRotation(0, 90);
						
					}
				});
			}			
		}.start();
    }
    
    
    private void applyRotation(float start, float end) {
		final float centerX = layout.getWidth() / 2.0f;
		final float centerY = layout.getHeight() / 2.0f;
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(700);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView());
		layout.startAnimation(rotation);
	}
    
    private final class DisplayNextView implements Animation.AnimationListener {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			layout.post(new SwapViews());
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}
    
    private final class SwapViews implements Runnable {
		public void run() {
			final float centerX = layout.getWidth() / 2.0f;
			final float centerY = layout.getHeight() / 2.0f;
			Rotate3dAnimation rotation;
			rotation = new Rotate3dAnimation(90, 180, centerX, centerY, 310.0f,false);
			rotation.setDuration(700);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			layout.startAnimation(rotation);
			
			Intent intent  = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}
}