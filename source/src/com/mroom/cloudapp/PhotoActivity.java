package com.mroom.cloudapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.mroom.request.PhotoData;
import com.mroom.request.TaskListener;
import com.mroom.request.UploadPicture;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoActivity extends DefaultActivity implements TaskListener {
	private static final String TAG = "PhotoActivity";
	private Context mContext;
	private PhotoData photoData;
	private GridView g;
	private ImageAdapter adapter;
	private ArrayList<String> arrImages = new ArrayList<String>();
	private final String PHOTO_DIR = "/Photos/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		mContext = this;

		photoData = new PhotoData(mContext, mApi, PhotoActivity.this);
		g = (GridView) findViewById(R.id.myGrid);
		g.setOnItemClickListener(itemClickListener);
		
		adapter = ImageAdapter.getInstance();
		getEntry();
	}
	
	private void getEntry() {
		new Thread() {
			public void run() {
				try {
					photoData.getEntry();
				} catch (DropboxException e) {
					showToast(e.toString());
				}
			}
		}.start();
	}

	public static class ImageAdapter extends BaseAdapter {
		private static ImageAdapter iAdapter = null;
		
		public static ImageAdapter getInstance() {
			if(iAdapter == null) {
				iAdapter = new ImageAdapter();
			} 
			return iAdapter;
		}
		
		private ImageAdapter() {}
		
		private ImageAdapter(Context c, List<Entry> entries) {
			mContext = c;
			this.entries = entries;
		}

		public Context getmContext() {
			return mContext;
		}

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		public List<Entry> getEntries() {
			return entries;
		}

		public void setEntries(List<Entry> entries) {
			if(entries == null) {
				this.entries.clear();
			} else {
				this.entries = entries;
			}
		}

		public List<String> getArrImages() {
			return arrImages;
		}

		public void setArrImages(List<String> arrImages) {
			if(arrImages == null) {
				this.arrImages.clear();
			} else {
				this.arrImages = arrImages;
			}
		}

		public int getCount() {
			return entries.size() + arrImages.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(72, 72));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
				imageView.setImageResource(R.drawable.cp_loading);
				if(position < entries.size()) {
					Entry entry = entries.get(position);
					PhotoData photoData = new PhotoData(mContext, mApi, imageView, entry);
					photoData.loadImage();
				} else if(position < arrImages.size() + entries.size()) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					// will results in a much smaller image than the original
					options.inSampleSize = 1;

					final Bitmap b = BitmapFactory.decodeFile(arrImages.get(position - entries.size()) , options);
					imageView.setImageBitmap(b);
				}
				
			} else {
				imageView = (ImageView) convertView;
			}
			
			return imageView;
		}

		private Context mContext;
		private List<Entry> entries = new ArrayList<Entry>();
		private List<String> arrImages = new ArrayList<String>();
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.photo_menu, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 	
		if(item.getItemId() == R.id.gallery){
			Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
    		return true;
    	} else if(item.getItemId() == R.id.capture) {
    		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, 0);
    		return true;
    	}
    	return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
    
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
    
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            // String picturePath contains the path of selected Image
            arrImages.add(picturePath);
            adapter.setArrImages(arrImages);
            adapter.notifyDataSetChanged();
            g.setAdapter(adapter);
            
            // upload file
            File file = new File(picturePath);
            UploadPicture upload = new UploadPicture(this, mApi, PHOTO_DIR, file, PhotoActivity.this);
            upload.execute();
        }
	}
	
	@Override
	public void onTaskCompleted(final Object response, final int taskId) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(taskId == 0) {
					adapter.setmContext(mContext);
					adapter.setEntries(((Entry)response).contents);
					g.setAdapter(adapter);
				} else if(taskId == 1) {
					clearApapter();
					getEntry();
				}
			}
		});
	}
	
	private void clearApapter() {
		adapter.setArrImages(null);
		adapter.setEntries(null);
	}
}
