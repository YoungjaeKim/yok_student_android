package bigcamp.yok.student.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import bigcamp.yok.student.R;
import bigcamp.yok.student.page.YokSubmitActivity;

/**
 * 욕 위젯
 * Created by Youngjae on 13. 8. 31.
 */
public class YokWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	                     int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, YokSubmitActivity.class);
			PendingIntent pi = PendingIntent.getActivity(
					context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_yok);
			views.setOnClickPendingIntent(R.id.buttonWidgetYokSubmit, pi);

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

}

