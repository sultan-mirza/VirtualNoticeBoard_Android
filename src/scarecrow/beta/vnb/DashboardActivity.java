package scarecrow.beta.vnb;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scarecrow.beta.vnb.R;
import scarecrow.beta.vnb.library.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DashboardActivity extends Activity {

	UserFunctions userFunctions;
    Button btnLogout;
    ListView list;
    
    private static String KEY_DATA = "data";
    private static String KEY_SUBJECT = "subject";
    private static String KEY_MESSAGE = "message";
    private static String KEY_NUMBER = "num";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userFunctions = new UserFunctions();
		
		if(userFunctions.isUserLoggedIn(getApplicationContext())) {
			setContentView(R.layout.dashboard);
			
			new LoadNotices().execute();
			
			list = (ListView) findViewById(R.id.list_view);
			
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			
			List<String> notices_list = db.allNotices();
			Log.d("Count", notices_list.get(0));
			list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notices_list));
			
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
			
			btnLogout = (Button) findViewById(R.id.btnLogout);
            
            btnLogout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
				}
			});
		} else {
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(login);
            
            finish();
		}
	}
	
	class LoadNotices extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.getNotices();
			JSONObject row = null;
			String subject = "";
			String message = "";
			
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
        				db.addNotices(i + 1, subject, message);
        			}
        			db.close();
        			
        		} else {
        			Log.d("Error!", "No Notices");
        		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}

}
