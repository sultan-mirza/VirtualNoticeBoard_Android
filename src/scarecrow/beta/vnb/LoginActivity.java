package scarecrow.beta.vnb;

import scarecrow.beta.vnb.R;
import scarecrow.beta.vnb.library.*;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity{

	Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;

    private static String KEY_SUCCESS = "success";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.login);
    	
    	inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new AttemptLogin().execute();
			}
		});
        
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
    }
    
    class AttemptLogin extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);
            
            try {
            	if (json.getString(KEY_SUCCESS) != null) {
            		//loginErrorMsg.setText("");
            		String res = json.getString(KEY_SUCCESS);
            		if (Integer.parseInt(res) == 1) {
            			
            			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            			JSONObject json_user = json.getJSONObject("user");
            			
            			userFunction.logoutUser(getApplicationContext());
            			db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL));
            			db.close();
            			
            			Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
            			
            			dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            			startActivity(dashboard);
            			
            			finish();
            		} else {
            			Log.d("Error!", "Incorrect Username/Password");
            		}
            	}
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
            return null;
		}
    	
    }
}