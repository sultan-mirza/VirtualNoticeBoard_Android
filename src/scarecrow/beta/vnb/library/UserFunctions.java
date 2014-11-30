package scarecrow.beta.vnb.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {

	private JSONParser jsonParser;
    
    /*private static String loginURL = "http://10.0.2.2/vnb/android/user_activity.php";
    private static String registerURL = "http://10.0.2.2/vnb/android/user_activity.php";
    private static String noticesURL = "http://10.0.2.2/vnb/android/get_data.php";*/
    
    private static String loginURL = "http://dcetech.com/sagnik/vnb/android/user_activity.php";
    private static String registerURL = "http://dcetech.com/sagnik/vnb/android/user_activity.php";
    private static String noticesURL = "http://dcetech.com/sagnik/vnb/android/get_data.php";
     
    private static String login_tag = "login";
    private static String register_tag = "register";
	
	public UserFunctions() {
		jsonParser = new JSONParser();
	}
	
	public JSONObject loginUser(String email, String password){
        
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        
        return json;
    }
	
	public JSONObject registerUser(String name, String email, String password, String regId, String year){
        
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("regId", regId));
        params.add(new BasicNameValuePair("year", year));
         
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }
	
	public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            return true;
        }
        return false;
    }
	
	public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTable_Login();
        return true;
    }
	
	public JSONObject getNotices(Context context) {
		
		DatabaseHandler db = new DatabaseHandler(context);
		String year = db.getYear();
		db.close();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("year", year));
		JSONObject json = jsonParser.getJSONFromUrl(noticesURL, params);
		return json;
	}

}
