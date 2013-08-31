package bigcamp.yok.student;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import bigcamp.yok.student.page.MainActivity;
import com.google.android.gcm.GCMBaseIntentService;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: Youngjae
 * Date: 13. 5. 17
 * Time: 오후 10:39
 */
public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(StaticValue.PUSH_SENDER_ID);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		// 로그인 상태인지 확인

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// 자동로그인이면 로그인 설정.
		if (!(sharedPrefs.contains("push") && sharedPrefs.getBoolean("push", false)
				&& sharedPrefs.contains("autoLogin") && sharedPrefs.getBoolean("autoLogin", false)
				&& sharedPrefs.contains("userId") && sharedPrefs.getString("userId", "").length() > 0
				&& sharedPrefs.contains("password") && sharedPrefs.getString("password", "").length() > 0))
			return;


		JSONObject jsonObject = null;
		String title = "새 알림이 왔어요!";
		String message = "";
		String targetId;
		String ticker = "";
		try {
			jsonObject = new JSONObject(intent.getStringExtra("message"));
			if (!jsonObject.has("type"))
				throw new JSONException("");
		} catch (JSONException e) {
			message = intent.getStringExtra("message");
			e.printStackTrace();
		}

		Intent resultIntent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// JSON 메시지 해석.
		try {
			if (jsonObject != null) {
				resultIntent.putExtra("PushType", jsonObject.getString("type"));

				if (jsonObject.getString("type").equalsIgnoreCase("Reply")) {
					targetId = jsonObject.getString("questionId");
					ticker = "오늘은 누가 긴장해야 할까요?";
					title = "늉!";
					message = jsonObject.getString("text");
					resultIntent.putExtra("questionId", targetId);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		int dot = 200;      // Length of a Morse Code "dot" in milliseconds
		int dash = 500;     // Length of a Morse Code "dash" in milliseconds
		int short_gap = 200;    // Length of Gap Between dots/dashes
		int medium_gap = 500;   // Length of Gap Between Letters
		int long_gap = 1000;    // Length of Gap Between Words
		long[] pattern = {
				0,  // Start immediately
				dot, short_gap, dot,    // 진동패턴.
				medium_gap,
		};

		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker(ticker)
						.setLargeIcon(bm)
						.setContentTitle(title)
						.setContentText(message)
						.setContentIntent(resultPendingIntent).setAutoCancel(true).setSound(soundUri).setVibrate(pattern);

		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(0, mBuilder.getNotification());
	}

	@Override
	protected void onError(Context context, String s) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected void onRegistered(Context context, String s) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected void onUnregistered(Context context, String s) {
		//To change body of implemented methods use File | Settings | File Templates.

	}

}
