package bigcamp.yok.student.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import bigcamp.yok.student.R;
import bigcamp.yok.student.model.Avatar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

import java.util.Random;

/**
 * 욕 지정 액티비티
 * Created by Youngjae on 13. 8. 31.
 */
public class YokSubmitActivity extends SherlockActivity {
	RelativeLayout _popupLayout;
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Yok); //Used for theme switching in samples
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("글 보기");
		actionBar.setIcon(R.drawable.ic_launcher);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true); // TODO: 푸쉬로 볼 때는 다른 처리가 필요하겠지...

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yoksubmit);
		setSupportProgressBarIndeterminateVisibility(false);

		_popupLayout = (RelativeLayout) findViewById(R.id.relativeLayoutYokSubmitPopup);

		load();
	}

	@Override
	public void onBackPressed() {
		if (_popupLayout.getVisibility() ==View.VISIBLE)
			_popupLayout.setVisibility(View.INVISIBLE);
		else
			super.onBackPressed();
	}


	public void click(View view) {
		switch (view.getId()) {
			case R.id.avatar1:
				_popupLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.avatar2:
				_popupLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.avatar3:
				_popupLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.avatar4:
				_popupLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.buttonSubmit:
				processSubmit();
				break;
		}
	}

	private void processSubmit() {
		setResult(RESULT_OK);
		Intent upIntent = new Intent(this, MainActivity.class);
		if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
			// This activity is not part of the application's task, so create a new task
			// with a synthesized back stack.
			TaskStackBuilder.from(this)
					.addNextIntent(new Intent(this, MainActivity.class))
					.addNextIntent(upIntent)
					.startActivities();
			YokSubmitActivity.this.finish();
		}else{
			NavUtils.navigateUpTo(this, upIntent);
		}
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


}