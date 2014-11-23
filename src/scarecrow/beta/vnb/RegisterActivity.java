package scarecrow.beta.vnb;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import scarecrow.beta.vnb.library.DatabaseHandler;
import scarecrow.beta.vnb.library.config;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import scarecrow.beta.vnb.library.UserFunctions;

public class RegisterActivity extends Activity {
	Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    Spinner inputYear;
    TextView registerErrorMsg;
    
    GoogleCloudMessaging gcm;
    Context context;
	String regId;

	public static final String REG_ID = "regId";
	//private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";
    
    private static String KEY_SUCCESS = "success";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_YEAR = "year";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
 
        // Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        inputYear = (Spinner) findViewById(R.id.registerYear);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				
				NetworkInfo ni = cm.getActiveNetworkInfo();
				
				if(ni == null) {
					
					Toast.makeText(getApplicationContext(), "Can't Connect to the Internet", Toast.LENGTH_LONG).show();
					
				} else {
					new AttemptRegister().execute();
				}
			}
		});
        
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
    }
    
    /*public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();
		}
		return regId;
	}*/

	/*private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				DashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		/*int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}*/

	/*private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}*/

	

	/*private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				DashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}*/
	
	class AttemptRegister extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {            
            
			String name = inputFullName.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String year = inputYear.getSelectedItem().toString();
            UserFunctions userFunction = new UserFunctions();
            
			String msg = "";
			context = getApplicationContext();
            try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regId = gcm.register(config.GOOGLE_PROJECT_ID);
				Log.d("RegisterActivity", "registerInBackground - regId: "
						+ regId);
				msg = "Device registered, registration ID=" + regId;

				//storeRegistrationId(context, regId);
			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				Log.d("RegisterActivity", "Error: " + msg);
			}
			Log.d("RegisterActivity", "AsyncTask completed: " + msg);
			
			JSONObject json = userFunction.registerUser(name, email, password, regId, year);
			
			try {
            	if (json.getString(KEY_SUCCESS) != null) {
            		//registerErrorMsg.setText("");
            		String res = json.getString(KEY_SUCCESS);
            		if (Integer.parseInt(res) == 1) {
            			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            			JSONObject json_user = json.getJSONObject("user");
            			
            			userFunction.logoutUser(getApplicationContext());
            			db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_YEAR));
            			db.close();
            			
            			Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
            			
            			dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            			startActivity(dashboard);
            			
            			finish();
            		} else {
            			//registerErrorMsg.setText("Error Occurred During Registration");
            		}
            	}
            } catch (JSONException e) {
            	e.printStackTrace();
            }
			
			return null;
            
		}
		
		@Override
		protected void onPostExecute(String msg) {
                       
            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show();
            
		}
    	
    }
}