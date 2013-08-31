package bigcamp.yok.student.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;
import bigcamp.yok.student.R;
import bigcamp.yok.student.StaticValue;
import bigcamp.yok.student.YokSherlockActivity;
import bigcamp.yok.student.YokUrl;
import bigcamp.yok.student.model.Member;
import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;

/**
 * 메인 액티비티.
 * Created by Youngjae on 13. 8. 31.
 */
public class MainActivity extends YokSherlockActivity {
	private static final int REQUESTCODE_LOGIN = 1001;
	private static final int REQUEST_WELCOME = 1002;
	SharedPreferences _sharedPrefs;
	private String _RegistationChannelId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// http://stackoverflow.com/a/10598594/361100
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}

		setContentView(R.layout.activity_main);

		if (_member.principal != null && _member.principal.length() > 0) {
			// TODO: 첫 화면 로딩
		}
		else {
			RequestLogin(MainActivity.this);
		}

		// 푸쉬로 열린 것이 아니면 푸쉬 채널을 갱신.
		_sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (_sharedPrefs.contains("push")) {
			if (_sharedPrefs.getBoolean("push", true)) {
				registerPushChannel();
			}
		}
		else {
			// 푸쉬 등록.
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sharedPrefs.edit();

			editor.putBoolean("push", true);
			editor.commit();
			registerPushChannel();
		}
	}


	private void registerPushChannel() {
		// 푸시 등록.
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		_RegistationChannelId = GCMRegistrar.getRegistrationId(this);
		if (_RegistationChannelId.equals("")) {
			GCMRegistrar.register(MainActivity.this, StaticValue.PUSH_SENDER_ID);
			_RegistationChannelId = GCMRegistrar.getRegistrationId(this);
		}

		RequestParams params = new RequestParams();
		params.put("platform", "ANDROID");
		params.put("channel", _RegistationChannelId);

		HttpClient.post(YokUrl.TOKEN.toString(), params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
			}

			@Override
			public void onFailure(Throwable throwable, String s) {
				super.onFailure(throwable, s);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	/**
	 * Note: 메시지를 다국어 및 일괄처리를 위한 리펙토링이 필요함.
	 *
	 * @param sender  메시지 표출할 콘텍스트(액티비티)
	 * @param message 토스트로 보일 메시지.
	 */
	public static void ShowToast(Context sender, String message) {
		ShowToast(sender, message, false);
	}

	public static void ShowToast(Context sender, String message, boolean isCenter) {
		Toast toast = Toast.makeText(sender, sender.getString(R.string.toastAppPrefix) + message, Toast.LENGTH_SHORT);
		if (isCenter)
			toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			// 회원가입 후 처리.
			case REQUEST_LOGIN: {
				if (resultCode == RESULT_OK) {
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();

					if (data != null) {
						editor.putString("userId", data.getStringExtra("userId"));
						if (data.getBooleanExtra("autoLogin", true)) {
							editor.putBoolean("autoLogin", true);
							editor.putString("password", data.getStringExtra("password"));
						}
						editor.commit();
						if (_member == null)
							_member = new Member();
						_member.principal = data.getStringExtra("userId");
						if (data.getBooleanExtra("showWelcome", true)) {
							Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
							startActivityForResult(intent, REQUEST_WELCOME);
						}
					}
				}
				else {
					MainActivity.this.finish();
				}
				break;
			}
			case REQUEST_WELCOME:
				// do nothing.
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}