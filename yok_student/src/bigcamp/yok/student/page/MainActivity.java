package bigcamp.yok.student.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bigcamp.yok.student.R;
import bigcamp.yok.student.StaticValue;
import bigcamp.yok.student.YokSherlockActivity;
import bigcamp.yok.student.YokUrl;
import bigcamp.yok.student.model.Avatar;
import bigcamp.yok.student.model.Member;
import bigcamp.yok.student.model.Mission;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;

import java.util.Random;

/**
 * 메인 액티비티.
 * Created by Youngjae on 13. 8. 31.
 */
public class MainActivity extends YokSherlockActivity {
	private static final int REQUESTCODE_LOGIN = 1001;
	private static final int REQUEST_WELCOME = 1002;
	private static final int REQUEST_SUBMIT_YOK = 1003;
	private static final int REQUEST_SHOWSTATISTICS = 1004;
	/**
	 * 인증용 쿠키. HTTPClient에 언제나 보내야 함.
	 */
	public static String _token;
	SharedPreferences _sharedPrefs;
	private String _RegistationChannelId;
	TextView _textViewMission;
	public static Mission Mission;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// http://stackoverflow.com/a/10598594/361100
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}

		setContentView(R.layout.activity_main);

		_textViewMission = (TextView) findViewById(R.id.textViewMission);

		if (_member.principal != null && _member.principal.length() > 0) {
			// TODO: 첫 화면 로딩
			load();
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 액션바 메뉴 선택
		// 로그아웃.
		if (item.getTitle() == getString(R.string.menuLogout)) {

			if (_token == null){
				RequestLogin(MainActivity.this);
				return true;
			}

			HttpClient.delete(YokUrl.LOGOUT.toString() + _token, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable throwable, String s) {
					super.onFailure(throwable, s);
				}

				@Override
				public void onFinish() {
					super.onFinish();
				}

				@Override
				public void onSuccess(String s) {
					super.onSuccess(s);

					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();
					if (editor != null && sharedPrefs.contains("autoLogin") && sharedPrefs.getBoolean("autoLogin", true)) {
						editor.remove("autoLogin");
						editor.commit();
					}

					RequestLogin(MainActivity.this);
				}
			});
		}

		return true;
	}


	/**
	 * 셋팅 정보들 출력
	 */
	private void load() {
		RelativeLayout avatarLayout1 = (RelativeLayout) findViewById(R.id.avatar1);
		RelativeLayout avatarLayout2 = (RelativeLayout) findViewById(R.id.avatar2);
		RelativeLayout avatarLayout3 = (RelativeLayout) findViewById(R.id.avatar3);
		RelativeLayout avatarLayout4 = (RelativeLayout) findViewById(R.id.avatar4);

		setAvatar(avatarLayout1, get_random_avatar());
		setAvatar(avatarLayout2, get_random_avatar());
		setAvatar(avatarLayout3, get_random_avatar());
		setAvatar(avatarLayout4, get_random_avatar());

	}

	private Avatar get_random_avatar(){
		Avatar avatar = new Avatar();
		avatar.head = String.valueOf(get_random_number(0, 20));
		avatar.torso = String.valueOf(get_random_number(0, 20));
		avatar.leg = String.valueOf(get_random_number(0, 20));
		return avatar;
	}

	private int get_random_number(int start, int last) {
		// http://stackoverflow.com/a/6029518/361100
		Random r = new Random();
		int random_number = r.nextInt(last + 1 - start) + start;
		return random_number;
	}

	/**
	 * 아바타 셋업.
	 *
	 * @param avatarLayout
	 * @param avatar
	 */
	private void setAvatar(RelativeLayout avatarLayout, Avatar avatar) {
		int resourceId;
		// Head
		ImageView imageViewHead = (ImageView) avatarLayout.findViewById(R.id.imageViewHead);
		resourceId = this.getResources().getIdentifier("h_" + avatar.head, "drawable", this.getPackageName());
		imageViewHead.setImageResource(resourceId);

		// Torso
		ImageView imageViewTorso = (ImageView) avatarLayout.findViewById(R.id.imageViewTorso);
		resourceId = this.getResources().getIdentifier("t_" + avatar.torso, "drawable", this.getPackageName());
		imageViewTorso.setImageResource(resourceId);

		// Leg
		ImageView imageViewLeg = (ImageView) avatarLayout.findViewById(R.id.imageViewLeg);
		resourceId = this.getResources().getIdentifier("l_" + avatar.leg, "drawable", this.getPackageName());
		imageViewLeg.setImageResource(resourceId);
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
						load();
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
			case REQUEST_SUBMIT_YOK:
				if (resultCode == RESULT_OK)
					MainActivity.ShowToast(MainActivity.this, " 늉을 접수했습니다.", true);
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void click(View view) {
		switch (view.getId()) {
			case R.id.imageButtonSubmitYok:
				startActivityForResult(new Intent(MainActivity.this, YokSubmitActivity.class), REQUEST_SUBMIT_YOK);
				break;
			case R.id.imageButtonShowStatistics:
				startActivityForResult(new Intent(MainActivity.this, StatisticsActivity.class), REQUEST_SHOWSTATISTICS);
				break;
			case R.id.avatar1:
				ShowToast(MainActivity.this, "아야", true);
				break;
			case R.id.avatar2:
				ShowToast(MainActivity.this, "오잉", true);
				break;
		}
	}
}