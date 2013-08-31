package bigcamp.yok.student.page;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import bigcamp.yok.student.R;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * 욕 지정 액티비티
 * Created by Youngjae on 13. 8. 31.
 */
public class YokSubmitActivity extends SherlockActivity {
	RelativeLayout _popupLayout;
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Yok); //Used for theme switching in samples

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yoksubmit);

		_popupLayout = (RelativeLayout) findViewById(R.id.relativeLayoutYokSubmitPopup);
	}

	@Override
	public void onBackPressed() {
		if (_popupLayout.getVisibility() ==View.VISIBLE)
			_popupLayout.setVisibility(View.INVISIBLE);
		else
			super.onBackPressed();
	}
}