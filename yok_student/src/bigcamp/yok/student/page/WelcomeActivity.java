package bigcamp.yok.student.page;

import android.os.Bundle;
import bigcamp.yok.student.R;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * 웰컴 액티비티.
 * Created by Youngjae on 13. 8. 31.
 */
public class WelcomeActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setLogo(R.drawable.ic_launcher);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
}
