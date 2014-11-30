package scarecrow.beta.vnb.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "vnb";
 	private static final String TABLE_LOGIN = "login";
 	private static final String TABLE_NOTICES = "notices";
 
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_YEAR = "year";
    
    
    private static final String KEY_NID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_POSTED_BY = "posted_by";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_YEAR + " TEXT " + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        
        String CREATE_NOTICE_TABLE = "CREATE TABLE " + TABLE_NOTICES + "("
                + KEY_NID + " INTEGER PRIMARY KEY,"
                + KEY_SUBJECT + " TEXT,"
                + KEY_POSTED_BY + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT" + ")";
        db.execSQL(CREATE_NOTICE_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICES);
        onCreate(db);
    }
 
    public void addUser(String name, String email, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_YEAR, year);
 
        db.insert(TABLE_LOGIN, null, values);
        db.close();
    }
     
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
          
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("year", cursor.getString(3));
            user.put("uid", cursor.getString(4));
        }
        cursor.close();
        db.close();
        
        return user;
    }
    
    public void addNotices(int id, String subject, String message, String posted_by, String date, String time) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
        values.put(KEY_NID, id);
        values.put(KEY_SUBJECT, subject);
        values.put(KEY_MESSAGE, message);
        values.put(KEY_POSTED_BY, posted_by);
        values.put(KEY_DATE, date);
        values.put(KEY_TIME, time);
        
        db.insert(TABLE_NOTICES, null, values);
        
    	db.close();
    }
    
    public List<String> allNotices() {
    	SQLiteDatabase db = this.getReadableDatabase();
    	String query = "SELECT * FROM " + TABLE_NOTICES;
    	Cursor cursor = db.rawQuery(query, null);
    	cursor.moveToFirst();
    	List<String> subjects = new ArrayList<String>();
    	if(cursor.getCount() > 0) {
    		do {
    			subjects.add(cursor.getString(1));
    		} while(cursor.moveToNext());
    	}
    	return subjects;
    }
 
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        cursor.close();
        db.close();
         
        return rowCount;
    }
     
    public String[] getDetails(String subject) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] values = new String[5];
		Cursor cursor = db.query(TABLE_NOTICES, new String[] { KEY_SUBJECT, KEY_MESSAGE, KEY_POSTED_BY, KEY_DATE, KEY_TIME }, KEY_SUBJECT + " LIKE ?",
				new String[] { "%" + subject + "%" }, null, null, null, null);;
		cursor.moveToFirst();
		if(cursor.getCount() > 0) {
			values[0] = cursor.getString(0);
			values[1] = cursor.getString(1);
			values[2] = cursor.getString(2);
			values[3] = cursor.getString(3);
			values[4] = cursor.getString(4);
		}
		cursor.close();
		db.close();
    	return values;
    }
    
    public String getYear() {
    	SQLiteDatabase db = this.getReadableDatabase();
		String year = "";
		Cursor cursor = db.query(TABLE_LOGIN, new String[] { KEY_YEAR }, null,
				null, null, null, null, null);;
		cursor.moveToFirst();
		if(cursor.getCount() > 0) {
			year = cursor.getString(0);
		}
		cursor.close();
		db.close();
    	return year;
    }
    
    public void resetTable_Login(){
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
    
    public void resetTable_Notices() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	db.delete(TABLE_NOTICES, null, null);
    	db.close();
    }

}
