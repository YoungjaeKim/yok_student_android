package bigcamp.yok.student.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import bigcamp.yok.student.R;

/**
 * Created by Youngjae on 13. 8. 31.
 */
public class SplashActivity extends Activity {
	// http://stackoverflow.com/questions/15452061/android-splash-screen?rq=1
	private static long SLEEP_TIME = 800;    // Sleep for some time
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,     WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

		setContentView(R.layout.activity_splash);

		// Start timer and launch main activity
		IntentLauncher launcher = new IntentLauncher();
		launcher.start();
	}

	private class IntentLauncher extends Thread {
		@Override
		/**
		 * Sleep for some time and than start new activity.
		 */
		public void run() {
			try {
				// Sleeping
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
				Log.e("bapul", e.getMessage());
			}

			// Start main activity
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
		}
	}
}