package sander.bylemans.organizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectWeeks extends ActionBarActivity {
	
	private ArrayList<String> weeks = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_weeks);
		
		setWeeks();
		Button calculate = (Button) findViewById(R.id.calculate);
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				double sum = 0;
				for(String title : weeks){
					int i = weeks.indexOf(title);
					CheckBox box = (CheckBox) findViewById("s".hashCode() + i+1);
					if(box.isChecked())
						sum += sumPrices(title);
					
				}
				Intent show = new Intent(SelectWeeks.this, ShowSum.class);
				show.putExtra("SUM", sum);
				startActivity(show);
			}

			private double sumPrices(String title) {
				double sum = 0;
				String FILENAME = title.replace("/", "-");

				FileInputStream fis = null;
				BufferedReader reader = null;
				try {
					fis = openFileInput(FILENAME);
					reader  = new BufferedReader(new InputStreamReader(fis));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int s = 0;
				try {
					s = reader.read();
				} catch (NullPointerException | IOException e1) {
					return 0;
				}
				for(int i = 0 ; i < s;i++){
					try {
						String penp = reader.readLine();
						String[] pr = penp.split("/SPLIT/");
						String[] result = new String[2];
						if(pr.length < 1){
							result[0] ="";
							result[1]="0";
						}
						else if(pr.length < 2){
							result[0] = pr[0];
							result[1]="0";
						} else result = pr;
						System.out.println(result[1]);
						sum += Double.parseDouble(result[1]);
						reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return sum;
			}
		});

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void setWeeks() {
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
				weeks.add(string);
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
			addWeek(i,string,p);
		}
		
		try {
			fis.close();
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addWeek(int i, String string, int p) {
		RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
		LinearLayout layout = new LinearLayout(this);
		final TextView week = new TextView(this);
		week.setText(string);
		week.setTextSize(48);
		week.setId("w".hashCode() + i);
		week.setLayoutParams(new LayoutParams(p,LayoutParams.WRAP_CONTENT));
		final CheckBox select = new CheckBox(this);
		select.setId("s".hashCode() + i);
		layout.setId("l".hashCode() + i);
		RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT,
	            RelativeLayout.LayoutParams.WRAP_CONTENT);
		if(i==1){
			Button calculate = (Button) findViewById(R.id.calculate);
			lprams.addRule(RelativeLayout.BELOW,calculate.getId());
		}
		else
			lprams.addRule(RelativeLayout.BELOW,"l".hashCode() + i - 1);
		layout.setLayoutParams(lprams);
		layout.addView(select);
		layout.addView(week);
		container.addView(layout);
		week.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean checked = select.isChecked();
				select.setChecked(!checked);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_weeks, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_select_weeks,
					container, false);
			return rootView;
		}
	}

}
