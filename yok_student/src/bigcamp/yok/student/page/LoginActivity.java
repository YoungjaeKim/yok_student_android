package bigcamp.yok.student.page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import bigcamp.yok.student.R;
import bigcamp.yok.student.YokUrl;
import com.actionbarsherlock.app.SherlockActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 로그인 액티비티
 * Created by Youngjae on 13. 8. 31.
 */
public class LoginActivity extends SherlockActivity {
	private static final int REQUESTCODE_REGISTER = 1001;

	private EditText _editTextUserId;
	private EditText _editTextPassword;
	private CheckBox _checkBoxAutoLogin;

	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Yok); //Used for theme switching in samples

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setLogo(R.drawable.ic_launcher);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		_editTextUserId = (EditText) findViewById(R.id.editId);
		_editTextPassword = (EditText) findViewById(R.id.editPw);
		_checkBoxAutoLogin = (CheckBox) findViewById(R.id.checkBoxRememberCredential);

		// 기존 로그인 정보 표시.
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (sharedPrefs.contains("userid"))
			_editTextUserId.setText(sharedPrefs.getString("userId", ""));


	}

	public void click(View v) {
		switch (v.getId()){
			case R.id.btnLogin:
				String id = _editTextUserId.getText().toString();
				String password = _editTextPassword.getText().toString();
				processLoginFake(id, password);
				break;
			case R.id.btnRegister:
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivityForResult(intent, REQUESTCODE_REGISTER);

				break;
		}
	}


	/**
	 * ID, Password 최소기준 충족 여부 확인.
	 *
	 * @return
	 */
	private boolean checkIdPassword(String id, String passwd) {
//		if (id.length() == 0) {
//			throw new IllegalArgumentException(getResources().getString(R.string.validationUserId));
//		} else if (passwd.length() == 0) {
//			throw new IllegalArgumentException(getResources().getString(R.string.validationUserPasswd));
//		}
		return true;
	}

	private void processLoginFake(final String id, final String password){
		Intent intent = new Intent();
		intent.putExtra("autoLogin", _checkBoxAutoLogin.isChecked());
		intent.putExtra("userId", id);
		intent.putExtra("password", password);
		intent.putExtra("showWelcome", true);
		setResult(RESULT_OK, intent);
		LoginActivity.this.finish();
	}


	/**
	 * 로그인 처리.
	 *
	 * @param id
	 * @param password
	 */
	private void processLogin(final String id, final String password) {
		try {
			checkIdPassword(id, password);
		} catch (IllegalArgumentException e) {
			MainActivity.ShowToast(this, e.getMessage(), true);
			return;
		}

		RequestParams params = new RequestParams();
		params.put("application", YokUrl.ApplicationName);
		params.put("principal", id);
		params.put("rawPassword", password);

		MainActivity.HttpClient.addHeader("_spring_security_remember_me", "true");
		MainActivity.HttpClient.get(YokUrl.LOGIN.toString(), params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					if (jsonObject.getBoolean("success")) {
						super.onSuccess(jsonObject);
						// 로그인 정보 삽입.
						MainActivity.HttpClient.setBasicAuth(id, password);
						Intent intent = new Intent();
						intent.putExtra("autoLogin", _checkBoxAutoLogin.isChecked());
						intent.putExtra("userId", id);
						intent.putExtra("password", password);
						intent.putExtra("showWelcome", true);
						setResult(RESULT_OK, intent);
						LoginActivity.this.finish();
					} else {
						throw new AuthenticationException();
					}
				} catch (JSONException e) {
					MainActivity.ShowToast(LoginActivity.this, getString(R.string.errorConnection), true);
				} catch (AuthenticationException e) {
					MainActivity.ShowToast(LoginActivity.this, getString(R.string.errorLogin), true);
				}
			}

			@Override
			public void onFailure(Throwable throwable, String s) {
				super.onFailure(throwable, s);
			}

			@Override
			public void onFailure(Throwable throwable, JSONObject jsonObject) {
				super.onFailure(throwable, jsonObject);
				MainActivity.ShowToast(LoginActivity.this, getString(R.string.errorConnection), true);
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			// 회원가입 후 처리.
			case REQUESTCODE_REGISTER: {
				if (resultCode == RESULT_OK) {
					// 회원가입 완료 후 바로 로그인.
					String u = data.getExtras().getString("principal");
					String p = data.getExtras().getString("password");
					processLoginFake(u, p);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}