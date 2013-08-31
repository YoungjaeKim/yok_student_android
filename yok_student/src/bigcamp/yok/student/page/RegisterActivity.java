package bigcamp.yok.student.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import bigcamp.yok.student.R;
import bigcamp.yok.student.model.Avatar;
import bigcamp.yok.student.model.Member;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.RequestParams;

/**
 * 가입 액티비티
 * Created by Youngjae on 13. 8. 31.
 */
public class RegisterActivity extends SherlockActivity {
	ImageView _imageViewHead;
	ImageView _imageViewTorso;
	ImageView _imageViewLeg;
	int _avatar_index_head = 0;
	int _avatar_index_torso = 0;
	int _avatar_index_leg = 0;
	int _avatar_max_head = 20;
	int _avatar_max_torso = 20;
	int _avatar_max_leg = 20;
	Member _member;
	private AutoCompleteTextView _groupAutoCompleteTextView;
	private EditText _editTextPhone;
	private EditText _editTextName;

	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Yok); //Used for theme switching in samples

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("가입하기");
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		if (_member == null)
			_member = new Member();

		_imageViewHead = (ImageView) findViewById(R.id.imageViewHead);
		_imageViewTorso = (ImageView) findViewById(R.id.imageViewTorso);
		_imageViewLeg = (ImageView) findViewById(R.id.imageViewLeg);
		_editTextName = (EditText) findViewById(R.id.editTextName);
		_editTextPhone = (EditText) findViewById(R.id.editTextPhone);

		// 아바타 기본값 설정.
		_imageViewHead.setImageResource(R.drawable.h_0);
		_imageViewTorso.setImageResource(R.drawable.t_0);
		_imageViewLeg.setImageResource(R.drawable.l_0);


		// 핸드폰 번호 자동 기입.
		try {
			TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneNumber = tMgr.getLine1Number();
			if (mPhoneNumber != null && mPhoneNumber.length() > 5) {
				_editTextPhone.setText(mPhoneNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 토픽뷰 설정
		_groupAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewGroup);
		_groupAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

			}
		});

		_groupAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				String newText = charSequence.toString();
				SearchGroup(newText);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}

	/**
	 * 그룹 검색
	 *
	 * @param q 토픽 검색어.
	 */
	public void SearchGroup(String q) {
		q = q.replaceAll("[ㄱ-ㅣ]", ""); // 단모음,단자음 입력부분 제거.
		if (q.length() < 1)
			return;

		// TODO: 검색 json 쿼리
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.buttonRegisterSend)
				.setIcon(R.drawable.ic_content_edit)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 액션바 메뉴 선택
		if (item.getTitle() == getString(R.string.buttonRegisterSend)) {

			if (_member == null)
				_member = new Member();
			_member.avatar = new Avatar();
			_member.avatar.head = String.valueOf(_avatar_index_head);
			_member.avatar.torso = String.valueOf(_avatar_index_torso);
			_member.avatar.leg = String.valueOf(_avatar_index_leg);
			item.setEnabled(false);
			processRegisterFake(_member);
		}
		// 홈버튼 클릭시.
		if (item.getItemId() == android.R.id.home) {
			RegisterActivity.this.finish();
		}
		return super.onOptionsItemSelected(item);
	}


	private void processRegisterFake(final Member m) {
		Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
		intent.putExtra("principal", m.principal);
		intent.putExtra("password", m.password);
		setResult(RESULT_OK, intent);
		RegisterActivity.this.finish();
	}

	/**
	 * 회원 등록 수행.
	 *
	 * @param m {@link Member} 오브젝트.
	 */
	private void processRegister(final Member m) {
		setSupportProgressBarIndeterminateVisibility(true);

		RequestParams params = new RequestParams();
		// 필수
		params.put("principal", m.principal);

//		MainActivity.HttpClient.post(YokUrl.REGISTER.toString(), params, new JsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(JSONObject message) {
//				setSupportProgressBarIndeterminateVisibility(false);
//				try {
//					YokMessage bpMessage = YokMessage.FromJson(message);
//					if (bpMessage.isSuccess) {
//						// 사용자 등록 성공 메시지 출력.
//						MainActivity.ShowToast(RegisterActivity.this, getString(R.string.toastWelcome), true);
//						// 바로 로그인 수행을 위해 인텐트보내고 닫는다.
//						Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//						intent.putExtra("principal", m.principal);
//						intent.putExtra("password", m.password);
//						setResult(RESULT_OK, intent);
//						RegisterActivity.this.finish();
//					} else {
//						// 로그인 실패
//						MainActivity.ShowToast(RegisterActivity.this, getString(R.string.toastRegistrationError), true);
//					}
//				} catch (JSONException e) {
//					Log.e("Bapul", "RegisterActivity.processRegister" + e.getMessage());
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable throwable, String s) {
//				setSupportProgressBarIndeterminateVisibility(false);
//				super.onFailure(throwable, s);
//			}
//		});
	}


	/**
	 * 아바타 수정.
	 *
	 * @param v
	 */
	public void clickChangeAvatar(View v) {
		int resourceId;
		switch (v.getId()) {
			case R.id.buttonLeftHead:
				--_avatar_index_head;
				if (_avatar_index_head < 0)
					_avatar_index_head = 0;
				resourceId = this.getResources().getIdentifier("h_" + _avatar_index_head, "drawable", this.getPackageName());
				_imageViewHead.setImageResource(resourceId);
				break;
			case R.id.buttonLeftTorso:
				--_avatar_index_torso;
				if (_avatar_index_torso < 0)
					_avatar_index_torso = 0;
				resourceId = this.getResources().getIdentifier("t_" + _avatar_index_torso, "drawable", this.getPackageName());
				_imageViewTorso.setImageResource(resourceId);
				break;
			case R.id.buttonLeftLeg:
				--_avatar_index_leg;
				if (_avatar_index_leg < 0)
					_avatar_index_leg = 0;
				resourceId = this.getResources().getIdentifier("l_" + _avatar_index_leg, "drawable", this.getPackageName());
				_imageViewLeg.setImageResource(resourceId);
				break;
			case R.id.buttonRightHead:
				++_avatar_index_head;
				if (_avatar_index_head > _avatar_max_head)
					_avatar_index_head = _avatar_max_head;
				resourceId = this.getResources().getIdentifier("h_" + _avatar_index_head, "drawable", this.getPackageName());
				_imageViewHead.setImageResource(resourceId);
				break;
			case R.id.buttonRightTorso:
				++_avatar_index_torso;
				if (_avatar_index_torso > _avatar_max_torso)
					_avatar_index_torso = _avatar_max_torso;
				resourceId = this.getResources().getIdentifier("t_" + _avatar_index_torso, "drawable", this.getPackageName());
				_imageViewTorso.setImageResource(resourceId);
				break;
			case R.id.buttonRightLeg:
				++_avatar_index_leg;
				if (_avatar_index_leg > _avatar_max_leg)
					_avatar_index_leg = _avatar_max_leg;
				resourceId = this.getResources().getIdentifier("l_" + _avatar_index_leg, "drawable", this.getPackageName());
				_imageViewLeg.setImageResource(resourceId);
				break;
		}
	}
}