package sander.bylemans.organizer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeekActivity extends ActionBarActivity {

	private int i;
	public static final String PREFS_NAME = "MyPrefsFile";
	private List<EditText> productsBoxes = new ArrayList<EditText>();
	private List<EditText> pricesBoxes = new ArrayList<EditText>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_week);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		i=1;
		
		final TextView date = (TextView) findViewById(R.id.WeekDate);
		final String title  = getIntent().getStringExtra("title");
		date.setText(title);
		date.setClickable(false);
		setSavedProdsAndPrices(title);
		final Button add = (Button) findViewById(R.id.AddNewProduct);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addNewProduct(i);
				i++;
			}
		});
		
		final Button save = (Button) findViewById(R.id.Save);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveAllCreatedBoxes();
				finish();
			}

			private void saveAllCreatedBoxes() {
				
				String FILENAME = title.replace("/", "-");
				String string;

				FileOutputStream fos = null;
				
				try {
					fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
					fos.write(productsBoxes.size());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for(int i = 0 ; i < productsBoxes.size();i++){
					try {
						string = productsBoxes.get(i).getText().toString() + "/SPLIT/" + pricesBoxes.get(i).getText().toString();
						fos.write((string+"\n").getBytes());
						string = productsBoxes.get(i).getWidth() + "/SPLIT/" + productsBoxes.get(i).getHeight();
						fos.write((string+"\n").getBytes());
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
		});
	}

	private void setSavedProdsAndPrices(String title) {
		FileInputStream fis = null;
        BufferedReader reader = null;
        
        title = title.replace("/", "-");
		try {
			fis = openFileInput(title);
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
			String[] pr = string.split("/SPLIT/");
			String[] result = new String[2];
			if(pr.length < 1){
				result[0] ="";
				result[1]="";
			}
			else if(pr.length < 2){
				result[0] = pr[0];
				result[1]="";
			} else result = pr;
			
			String[] sizers = sizes.split("/SPLIT/");
			int[] sizesReal = new int[2];
			int t = 0;
			for(String s : sizers){
				int p = Integer.parseInt(s);
				sizesReal[t] = p;
				t++;
			}
			addProduct(i,result[0],result[1], sizesReal[0],sizesReal[1]);
			this.i++;
		}
		
		try {
			fis.close();
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addProduct(int i, String string, String string2, int sizesReal, int sizesReal2) {
		
		RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
		LinearLayout layout = new LinearLayout(container.getContext());
		EditText product = new EditText(this);
		product.setLayoutParams(new LayoutParams(sizesReal, sizesReal2));
		if(!string.equals(""))
			product.setText(string);
		else
			product.setHint("Product");
		RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT,
	            RelativeLayout.LayoutParams.WRAP_CONTENT);
		EditText price = new EditText(this);
		if(!string2.equals(""))
			price.setText(string2);
		else
			price.setHint("Price in €");
		price.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		price.setLayoutParams(new LayoutParams(sizesReal, sizesReal2));
		layout.addView(product);
		layout.addView(price);
		product.setId("prod".hashCode()+i);
		price.setId("price".hashCode()+i);
		
		productsBoxes.add(product);
		pricesBoxes.add(price);
		
		layout.setId(i);
		if(i==1){
			TextView title = (TextView) findViewById(R.id.WeekDate);
			lprams.addRule(RelativeLayout.BELOW,title.getId());
		}
		else
			lprams.addRule(RelativeLayout.BELOW,i-1);
		layout.setLayoutParams(lprams);
		container.addView(layout,i);
		
		Button add = (Button) findViewById(R.id.AddNewProduct);
		RelativeLayout.LayoutParams addprams = (RelativeLayout.LayoutParams) add.getLayoutParams();
		addprams.addRule(RelativeLayout.BELOW,i);
		
	}

	protected void addNewProduct(int i) {
		RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
		LinearLayout layout = new LinearLayout(container.getContext());
		EditText product = new EditText(this);
		product.setHint("Product");
		RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT,
	            RelativeLayout.LayoutParams.WRAP_CONTENT);
		product.setLayoutParams(new LayoutParams(container.getWidth()/2, LayoutParams.WRAP_CONTENT));
		EditText price = new EditText(this);
		price.setHint("Price in €");
		price.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		price.setLayoutParams(new LayoutParams(container.getWidth()/2, LayoutParams.WRAP_CONTENT));
		layout.addView(product);
		layout.addView(price);
		product.setId("prod".hashCode()+i);
		price.setId("price".hashCode()+i);
		
		productsBoxes.add(product);
		pricesBoxes.add(price);
		
		layout.setId(i);
		if(i==1){
			TextView title = (TextView) findViewById(R.id.WeekDate);
			lprams.addRule(RelativeLayout.BELOW,title.getId());
		}
		else
			lprams.addRule(RelativeLayout.BELOW,i-1);
		layout.setLayoutParams(lprams);
		container.addView(layout,i);
		
		Button add = (Button) findViewById(R.id.AddNewProduct);
		RelativeLayout.LayoutParams addprams = (RelativeLayout.LayoutParams) add.getLayoutParams();
		addprams.addRule(RelativeLayout.BELOW,i);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.week, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_week, container,
					false);
			return rootView;
		}
	}

	//TODO: Overwritten zodat de gecreërde lijst van producten terug komt.
	@Override
	public void onSaveInstanceState(Bundle savedThings){
		super.onSaveInstanceState(savedThings);
		
	}
}
