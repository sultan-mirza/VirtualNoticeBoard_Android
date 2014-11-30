package scarecrow.beta.vnb;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scarecrow.beta.vnb.R;
import scarecrow.beta.vnb.library.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity {

	UserFunctions userFunctions;
    Button btnLogout;
    ListView list;
    
    private static String KEY_DATA = "data";
    private static String KEY_SUBJECT = "subject";
    private static String KEY_MESSAGE = "message";
    private static String KEY_POSTED_BY = "posted_by";
    private static String KEY_DATE = "date";
    private static String KEY_TIME = "time";
    private static String KEY_NUMBER = "num";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userFunctions = new UserFunctions();
		
		if(userFunctions.isUserLoggedIn(getApplicationContext())) {
			setContentView(R.layout.dashboard);
			
			ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			
			NetworkInfo ni = cm.getActiveNetworkInfo();
			
			if(ni == null) {
				
				Toast.makeText(getApplicationContext(), "Can't Connect to the Internet", Toast.LENGTH_LONG).show();
				prepareNotices();
				
			} else {
				
				new LoadNotices().execute();
				
			}
			
			
			
			btnLogout = (Button) findViewById(R.id.btnLogout);
            
            btnLogout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(login, 0);
                    finish();
				}
			});
		} else {
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(login);
            
            finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		new MenuInflater(this).inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean refresh(MenuItem item) {
		Intent login = new Intent(getApplicationContext(), DashboardActivity.class);
		login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(login);
		
		return true;
	}
	
	void prepareNotices() {
		list = (ListView) findViewById(R.id.list_view);
		
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
		List<String> notices_list = db.allNotices();
		//Log.d("Count", notices_list.get(0));
		list.setAdapter(new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_list_item_1, notices_list));
		
		db.close();
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String item = ((TextView)view).getText().toString();
				Intent details = new Intent(getApplicationContext(), NoticeActivity.class);
				details.putExtra("subject", item);
				startActivity(details);
			}
		});
	}
	
	class LoadNotices extends AsyncTask<String, String, JSONObject> {

		private ProgressDialog pDialog;
	    
		@Override
	    protected void onPreExecute() {
			super.onPreExecute();
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Fetching Notices ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
	    }
		
		@Override
		protected JSONObject doInBackground(String... arg0) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.getNotices(getApplicationContext());
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			pDialog.dismiss();
			JSONObject row = null;
			String subject = "";
			String message = "";
			String posted_by = "";
			String date = "";
			String time = "";
			
			try {
				int num = json.getInt(KEY_NUMBER);
					
        		if (num > 0) {
        			JSONArray data = json.getJSONArray(KEY_DATA);
        			
        			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        			db.resetTable_Notices();
        			for(int i = 0; i < num; i++) {
        				row = data.getJSONObject(i);
        				subject = row.getString(KEY_SUBJECT);
        				message = row.getString(KEY_MESSAGE);
        				posted_by = row.getString(KEY_POSTED_BY);
        				date = row.getString(KEY_DATE);
        				time = row.getString(KEY_TIME);
        				db.addNotices(i + 1, subject, message, posted_by, date, time);
        			}
        			db.close();
        			
        		} else {
        			Log.d("Error!", "No Notices");
        		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			prepareNotices();
			
			return;
		}
		
	}	

}
