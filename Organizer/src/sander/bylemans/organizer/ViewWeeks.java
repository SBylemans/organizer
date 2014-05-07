package sander.bylemans.organizer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ViewWeeks extends ActionBarActivity {

	final int activator = 123;
	private String name;
	private int i = 1;
	private ArrayList<Button> weeks;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_weeks);
		
		weeks = new ArrayList<Button>();

		setSavedWeeks();
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		Button add = (Button) findViewById(R.id.Add);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(ViewWeeks.this, NameWeekActivity.class), activator );
				}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == activator){
			if(resultCode == RESULT_OK){
				name = data.getStringExtra("RESULT_STRING");
				addNewWeek(i++,name);
			}
		}
	}

	protected void addNewWeek(int i, String name) {
		LinearLayout container = (LinearLayout) findViewById(R.id.container);
		final LinearLayout layout = new LinearLayout(container.getContext());
		final Button week = new Button(this);
		layout.setId("l".hashCode()+i);
		week.setId("w".hashCode()+i);
		week.setText(name);
		week.setLayoutParams(new LayoutParams(container.getWidth()-75, LayoutParams.WRAP_CONTENT));
		final Button remove = new Button(this);
		remove.setId("r".hashCode()+i);
		remove.setText("X");
		remove.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.addView(week);
		layout.addView(remove);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		container.addView(layout,i);
		
		weeks.add(week);

		final Intent weekIntent = new Intent(ViewWeeks.this,WeekActivity.class);
		week.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weekIntent.putExtra("title", week.getText());
				startActivity(weekIntent);
			}
		});
		
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layout.removeAllViews();
				weeks.remove(week);
				String name = week.getText().toString().replace("/", "-");
				v.getContext().deleteFile(name);
				}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_weeks, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_view_weeks,
					container, false);
			return rootView;
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		saveAllWeeks();
	}

	private void saveAllWeeks() {

		this.deleteFile("ViewWeeks");
		String FILENAME = "ViewWeeks";
		String string;

		FileOutputStream fos = null;
		
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(weeks.size());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i = 0 ; i < weeks.size();i++){
			try {
				string = weeks.get(i).getText().toString() + "\n";
				fos.write(string.getBytes());
				string = weeks.get(i).getWidth() + "\n";
				fos.write(string.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setSavedWeeks(){
		FileInputStream fis = null;
		BufferedReader reader = null;

		try {
			fis = openFileInput("ViewWeeks");
			reader = new BufferedReader(new InputStreamReader(fis));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		int size = 0;
		try {
			size = reader.read();
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}		

		for(int i = 1; i<size+1; i++){
			String string = "Leeg";
			try {
				string = reader.readLine();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
			String sizes = "";
			try {
				sizes = reader.readLine();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
			int p = Integer.parseInt(sizes);
			addWeek(i,string, p);
			this.i++;
		}

		try {
			fis.close();
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addWeek(int i, String string, int width) {
		LinearLayout container = (LinearLayout) findViewById(R.id.container);
		final LinearLayout layout = new LinearLayout(container.getContext());
		final Button week = new Button(this);
		layout.setId("l".hashCode()+i);
		week.setId("w".hashCode()+i);
		week.setText(string);
		week.setLayoutParams(new LayoutParams(width, LayoutParams.WRAP_CONTENT));
		final Button remove = new Button(this);
		remove.setId("r".hashCode()+i);
		remove.setText("X");
		remove.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.addView(week);
		layout.addView(remove);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		container.addView(layout,i);
		
		weeks.add(week);

		final Intent weekIntent = new Intent(ViewWeeks.this,WeekActivity.class);
		week.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weekIntent.putExtra("title", week.getText());
				startActivity(weekIntent);
			}
		});
		
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layout.removeAllViews();
				weeks.remove(week);
				v.getContext().deleteFile(week.getText().toString().replace("/", "-"));
			}
		});
	}

}
