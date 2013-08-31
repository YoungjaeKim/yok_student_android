package bigcamp.yok.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import bigcamp.yok.student.model.Member;
import bigcamp.yok.student.page.LoginActivity;
import com.actionbarsherlock.app.SherlockActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

/**
 * Created by Youngjae on 13. 8. 31.
 */
public class YokSherlockActivity extends SherlockActivity {
	public static final int REQUEST_LOGIN = 1001;

	public static AsyncHttpClient HttpClient;
	public static PersistentCookieStore YokCookieStore;
	public static Member _member = new Member();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Yok); //Used for theme switching in samples

		super.onCreate(savedInstanceState);
		InitializeHttpClient();

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (HttpClient == null){
			InitializeHttpClient();
		}
	}

	/**
	 * 로그인 요청.
	 * @param context
	 */
	public void RequestLogin(Context context){
		Intent intent = new Intent(context, LoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	/**
	 * HttpClient 초기화.
	 * 오랜 시간 후에 리쥼할 때 null이 되는 문제를 해결한다.
	 */
	public void InitializeHttpClient(){
		HttpClient = new AsyncHttpClient();
		HttpClient.addHeader("X-Requested-With", "XMLHttpRequest");

		SharedPreferences _sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// 자동로그인이면 로그인 설정.
		if (_sharedPrefs.contains("autoLogin") && _sharedPrefs.getBoolean("autoLogin", true)
				&& _sharedPrefs.contains("userId") && _sharedPrefs.getString("userId", "").length() > 0
				&& _sharedPrefs.contains("password") && _sharedPrefs.getString("password", "").length() > 0) {
			HttpClient.setBasicAuth(_sharedPrefs.getString("userId", ""), _sharedPrefs.getString("password", ""));

			if (_member == null)
				_member = new Member();
			_member.principal = _sharedPrefs.getString("userId", "");
		}
		YokCookieStore = new PersistentCookieStore(this);
		HttpClient.setCookieStore(YokCookieStore);
	}
}
