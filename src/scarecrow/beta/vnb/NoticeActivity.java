package scarecrow.beta.vnb;

import scarecrow.beta.vnb.library.DatabaseHandler;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class NoticeActivity extends Activity {

	TextView subject_view, message_view, date_view, posted_view;
	
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notice, menu);
		return true;
	}

}
