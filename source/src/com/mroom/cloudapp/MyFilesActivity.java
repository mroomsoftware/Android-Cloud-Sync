package com.mroom.cloudapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyFilesActivity extends ListActivity {

	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}

	private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
	private List<String> directoryEntries = new ArrayList<String>();
	private File currentDirectory = new File("/");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.files);
		
		ListView listView = (ListView) findViewById(android.R.id.list);

		listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, directoryEntries));
		browseToRoot();
	}

	/**
	 * This function browses to the root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/"));
	}

	/**
	 * This function browses up one level 
	 * according to the
	 * field: currentDirectory Ê Ê Ê Ê Ê
	 */
	private void upOneLevel() {
		this.browseTo(this.currentDirectory.getParentFile());
	}

	private void browseTo(File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {
			OnClickListener okButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Lets start an intent to View the file, that was
					// clicked...
				}
			};
			OnClickListener cancelButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Do nothing
				}
			};

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Question");
			alertDialog.setMessage("Do you want to open that file?");
			alertDialog.setButton(0, "OK", okButtonListener);
			alertDialog.setButton(1, "Cancel", cancelButtonListener);
			alertDialog.show();
		}
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();

		// Add the "." and the ".." == 'Up one level'
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.directoryEntries.add(".");

		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add("..");

		switch (this.displayMode) {
		case ABSOLUTE:
			for (File file : files) {
				this.directoryEntries.add(file.getPath());
			}
			break;
		case RELATIVE: // On relative Mode, we have to add the current-path to
						// the beginning
			int currentPathStringLenght = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {
				this.directoryEntries.add(file.getAbsolutePath().substring(
						currentPathStringLenght));
			}
			break;
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_row, this.directoryEntries);

		this.setListAdapter(directoryList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
//		int selectionRowID = (int) this.getSelectedItemId();
		int selectionRowID = position;
		String selectedFileString = this.directoryEntries.get(selectionRowID);
		if (selectedFileString.equals(".")) {
			// Refresh
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals("..")) {
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch (this.displayMode) {
			case RELATIVE:
				clickedFile = new File(this.currentDirectory.getAbsolutePath()
						+ this.directoryEntries.get(selectionRowID));
				break;
			case ABSOLUTE:
				clickedFile = new File(
						this.directoryEntries.get(selectionRowID));
				break;
			}
			if (clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
}
