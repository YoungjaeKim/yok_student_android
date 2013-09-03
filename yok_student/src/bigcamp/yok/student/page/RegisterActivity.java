package bigcamp.yok.student.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import bigcamp.yok.student.R;
import bigcamp.yok.student.YokUrl;
import bigcamp.yok.student.model.Avatar;
import bigcamp.yok.student.model.Group;
import bigcamp.yok.student.model.Member;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
	private EditText _editTextPassword;
	private EditText _editTextPassword2;
	private EditText _editTextName;
	private TextView _textViewGroup;

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
		_editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		_editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
		_editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);

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
				_member.group = (Group) adapterView.getItemAtPosition(position);
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
		if (q.length() < 1 || q.length() > 4)
			return;

		// TODO: 검색 json 쿼리
		RequestParams params = new RequestParams();
		MainActivity.HttpClient.get(YokUrl.GROUP.toString() + q, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String s) {
				try {
					JSONArray jsonArray = new JSONArray(s);
					// [{"teacher_id":1,"name":"이문초등학교 1학년 1반","size_of_team":null,"url":"http://yok-server.cloudapp.net:8080/groups/1.json"}]
					final ArrayList<Group> groups = new ArrayList<Group>();
					if (jsonArray.length() > 0) {

						for (int i = 0; i < jsonArray.length(); i++) {
							groups.add(Group.FromJson(jsonArray.getJSONObject(i)));
						}

						RegisterActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								ArrayAdapter<Group> aAdapter = new ArrayAdapter<Group>(getApplicationContext(), R.layout.item_group_searched, groups);
								_groupAutoCompleteTextView.setAdapter(aAdapter);
								aAdapter.notifyDataSetChanged();
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Log.d("Yok", "json: " + s);
//				try {

//					bapulTopicList = BapulTopicList.FromJson(message);
//					if (bapulTopicList.isSuccess) {
//						QuestionPostActivity.this.runOnUiThread(new Runnable() {
//							public void run() {
//								aAdapter = new ArrayAdapter<Topic>(getApplicationContext(), R.layout.item_topic_searched, bapulTopicList.topicArrayList);
//								_topicCustomTextView.setAdapter(aAdapter);
//								aAdapter.notifyDataSetChanged();
//							}
//						});
//					}
//				} catch (JSONException e) {
//					Log.e("Bapul", "searchTopics" + e.getMessage());
//				}
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
			_member.name = _editTextName.getText().toString();
			_member.principal = _editTextPhone.getText().toString();

			_member.avatar = new Avatar();
			_member.avatar.head = String.valueOf(_avatar_index_head);
			_member.avatar.torso = String.valueOf(_avatar_index_torso);
			_member.avatar.leg = String.valueOf(_avatar_index_leg);

			try {
				if (!_editTextPassword.getText().toString().equals(_editTextPassword2.getText().toString()))
					throw new IllegalArgumentException(getString(R.string.validationPasswordAreNotEqual));

				_member.password = _editTextPassword.getText().toString();
				validate(_member);
			} catch (IllegalArgumentException e) {
				MainActivity.ShowToast(RegisterActivity.this, e.getMessage(), true);
				return super.onOptionsItemSelected(item);
			}

			processRegister(_member);
		}
		// 홈버튼 클릭시.
		if (item.getItemId() == android.R.id.home) {
			RegisterActivity.this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void validate(Member member) {
		if (member.principal.length() == 0) {
			throw new IllegalArgumentException(getResources().getString(R.string.validationUserId));
		}
		if (member.password.length() < 6) {
			throw new IllegalArgumentException(getResources().getString(R.string.validationUserIdLength6));
		}
		if (member.password.length() == 0) {
			throw new IllegalArgumentException(getResources().getString(R.string.validationUserPasswd));
		}
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
		params.put("user[name]", m.name);
		params.put("user[phonenumber]", m.principal); // 폰번호를 ID로.
		params.put("user[password]", m.password);
		params.put("user[password_confirmation]", m.password);
		params.put("user[role]", "student");
		params.put("avatar[head]", m.avatar.head);
		params.put("avatar[torso]", m.avatar.torso);
		params.put("avatar[leg]", m.avatar.leg);
		params.put("group_id", m.group.teacher);

		MainActivity.HttpClient.post(YokUrl.REGISTER.toString(), params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String s) {
				super.onSuccess(s);
				try {
					JSONObject jsonObject = new JSONObject(s);
					MainActivity._token = jsonObject.getString("token");

					Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
					intent.putExtra("principal", m.principal);
					intent.putExtra("password", m.password);
					setResult(RESULT_OK, intent);
					RegisterActivity.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				setSupportProgressBarIndeterminateVisibility(false);

			}
		});
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