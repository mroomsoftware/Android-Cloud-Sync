package com.mroom.request;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.mroom.cloudapp.CloudComputing;

public class PhotoData {
	private DropboxAPI<AndroidAuthSession> mApi;
	private TaskListener taskListener;
	private ImageView imageView;
	private Entry entry;
	private Context mContext;
	
	public PhotoData(Context mContext, DropboxAPI<AndroidAuthSession> mApi, TaskListener taskListener) {
		this.mContext = mContext;
		this.mApi = mApi;
		this.taskListener = taskListener;
	}
	
	public PhotoData(Context mContext, DropboxAPI<AndroidAuthSession> mApi, ImageView imageView, Entry entry) {
		this.mContext = mContext;
		this.mApi = mApi;
		this.imageView = imageView;
		this.entry = entry;
	}
	
	public void loadImage() {
		new Thread() {
			public void run() {
				try {
					BitmapFactory.Options bmOptions;
					bmOptions = new BitmapFactory.Options();
					bmOptions.inSampleSize = 1;
					Bitmap bm = BitmapFactory.decodeStream(mApi.getFileStream(entry.path, null), null, bmOptions);
					BitmapDisplayer db = new BitmapDisplayer(bm, imageView);
					((Activity)mContext).runOnUiThread(db);
				} catch (DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void getEntry() throws DropboxException {
		Entry existingEntry = mApi.metadata("/Photos", CloudComputing.nDownloadedFiles, null, true, null);
		this.taskListener.onTaskCompleted(existingEntry, 0);
	}
	
	class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView image;
        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b; image = i;}
        public void run()
        {
            if(bitmap!=null)
            	image.setImageBitmap(bitmap);
        }
    }
	
}
