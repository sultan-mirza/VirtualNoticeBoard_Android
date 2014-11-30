package scarecrow.beta.vnb;

import scarecrow.beta.vnb.library.DatabaseHandler;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class NoticeActivity extends Activity {

	TextView subject_view, message_view, date_view, posted_view;
	
	String subject_share, posted_by;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice);
		
		Intent dashboard = getIntent();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		String subject = dashboard.getStringExtra("subject");
		
		subject_view = (TextView)findViewById(R.id.subject1);
		message_view = (TextView)findViewById(R.id.message1);
		date_view = (TextView)findViewById(R.id.Date1);
		posted_view = (TextView)findViewById(R.id.posted_by1);
		
		String[] details = db.getDetails(subject);
		
		date_view.setText(details[3] + ", " + details[4]);
		subject_view.setText(details[0]);
		message_view.setText(details[1]);
		posted_view.setText( details[2]);
		
		subject_share = details[0];
		posted_by = details[2];
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		new MenuInflater(this).inflate(R.menu.notice, menu);
		return true;
	}
	
	public boolean share(MenuItem item) {
		Intent sharing = new Intent(android.content.Intent.ACTION_SEND);
		sharing.setType("text/plain");
		String sharebody = "New Notice for MCE Branch: \nSubject: " + subject_share + "\nPosted By: " + posted_by;
		sharing.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared stuff from Virtual Notice Board");
		sharing.putExtra(android.content.Intent.EXTRA_TEXT, sharebody);
		startActivity(Intent.createChooser(sharing, "Share via"));
		
		return true;
	}

}
