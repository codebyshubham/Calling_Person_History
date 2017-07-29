package com.shubham.interactionlog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FloatingLayout extends Service {

	private WindowManager windowmanager;
	private View floatingview;
	public String Name = "";

	public FloatingLayout() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i("My", "CallerNumber = " + MainActivity.CallerNumber);

		floatingview = LayoutInflater.from(this).inflate(
				R.layout.floatinglayout, null);

		getCallDetails(MainActivity.CallerNumber);
		getAllSms(MainActivity.CallerNumber);
		TextView t = (TextView) floatingview.findViewById(R.id.name);
		t.setText(Name);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT; // Initially view will be
														// added to top-left
														// corner
		params.x = 0;
		params.y = 100;

		windowmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowmanager.addView(floatingview, params);

		final View collapsedView = floatingview
				.findViewById(R.id.collapse_view);
		final View expandedView = floatingview
				.findViewById(R.id.expanded_container);

		// Set the close button
		ImageView closeButtonCollapsed = (ImageView) floatingview
				.findViewById(R.id.close_btn);
		closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopSelf();
			}
		});

		TextView closeButtonExpanded = (TextView) floatingview
				.findViewById(R.id.close_box);
		closeButtonCollapsed.bringToFront();
		closeButtonExpanded.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopSelf();
			}
		});

		floatingview.findViewById(R.id.root_container).setOnTouchListener(
				new View.OnTouchListener() {
					private int initialX;
					private int initialY;
					private float initialTouchX;
					private float initialTouchY;

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:

							initialX = params.x;
							initialY = params.y;

							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							return true;
						case MotionEvent.ACTION_UP:
							int Xdiff = (int) (event.getRawX() - initialTouchX);
							int Ydiff = (int) (event.getRawY() - initialTouchY);

							if (Xdiff < 10 && Ydiff < 10) {
								if (isViewCollapsed()) {
									collapsedView.setVisibility(View.GONE);
									expandedView.setVisibility(View.VISIBLE);
								}
							}
							return true;
						case MotionEvent.ACTION_MOVE:
							params.x = initialX
									+ (int) (event.getRawX() - initialTouchX);
							params.y = initialY
									+ (int) (event.getRawY() - initialTouchY);

							windowmanager
									.updateViewLayout(floatingview, params);
							return true;
						}
						return false;
					}
				});
	}

	private boolean isViewCollapsed() {
		return floatingview == null
				|| floatingview.findViewById(R.id.collapse_view)
						.getVisibility() == View.VISIBLE;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (floatingview != null)
			windowmanager.removeView(floatingview);
	}

	private void getCallDetails(String num) {
		ListView listview1;
		List<String> number = new ArrayList();
		List<String> time = new ArrayList();
		List<String> type = new ArrayList();
		List<String> day_duration = new ArrayList();
		Cursor managedCursor = getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null,
				CallLog.Calls._ID + " DESC");
		int mnumber = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int mtype = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int mdate = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int mduration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		while (managedCursor.moveToNext()) {
			if (managedCursor.getString(mnumber).contains(num)) {

				String name;
				if (managedCursor.getString(managedCursor
						.getColumnIndex(CallLog.Calls.CACHED_NAME)) != null) {
					Name = managedCursor.getString(managedCursor
							.getColumnIndex(CallLog.Calls.CACHED_NAME));
				} else {
					Name = "Unknown";
				}

				String phNumber = managedCursor.getString(mnumber);
				String callType = managedCursor.getString(mtype);
				String callDate = managedCursor.getString(mdate);
				Date callDayTime = new Date(Long.valueOf(callDate));
				String temptime = new SimpleDateFormat("HH:mm")
						.format(callDayTime);
				String tempmonth = new SimpleDateFormat("MMM dd")
						.format(callDayTime);
				String callDuration = managedCursor.getString(mduration);
				String dir = null;
				int dircode = Integer.parseInt(callType);
				switch (dircode) {
				case CallLog.Calls.OUTGOING_TYPE:
					dir = "OUTGOING";
					break;

				case CallLog.Calls.INCOMING_TYPE:
					dir = "INCOMING";
					break;

				case CallLog.Calls.MISSED_TYPE:
					dir = "MISSED";
					break;
				}

				number.add(phNumber);
				time.add(temptime);
				type.add(dir);
				day_duration.add(tempmonth + " Duration : " + callDuration);

			}
		}
		managedCursor.close();
		listview1 = (ListView) floatingview.findViewById(R.id.listview1);
		CallLogAdapter adapter = new CallLogAdapter(floatingview.getContext(),
				number, time, type, day_duration);
		listview1.setAdapter(adapter);
	}

	public void getAllSms(String mnumber) {
		ListView listview2;
		List<String> number = new ArrayList();
		List<String> time = new ArrayList();
		List<String> day_type = new ArrayList();
		List<String> body = new ArrayList();
		Uri message = Uri.parse("content://sms/");
		Cursor c = getContentResolver().query(message, null, null, null, null);
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);

		if (c.moveToFirst()) {
			while (c.moveToNext()) {
				String tempid = c.getString(c.getColumnIndexOrThrow("_id"));
				String tempdate = c.getString(c.getColumnIndexOrThrow("date"));
				String tempaddress = c.getString(c
						.getColumnIndexOrThrow("address"));
				String tempbody = c.getString(c.getColumnIndexOrThrow("body"));
				String tempread = c.getString(c.getColumnIndex("read"));
				String temptype = c.getString(c.getColumnIndexOrThrow("type"));
				if (temptype.equals("1")) {
					temptype = "Received";
				} else {
					temptype = "Sent";
				}

				if (tempaddress.contains(mnumber)) {
					cal.setTimeInMillis(Long.parseLong(tempdate));
					// java.util.Date temptime = cal.getTime();
					if (temptype.equals("1")) {
						temptype = "Received";
					} else {
						temptype = "Sent";
					}

					String mtime = new SimpleDateFormat("HH:mm").format(cal
							.getTime());
					String mmonth = new SimpleDateFormat("MMM dd").format(cal
							.getTime());

					number.add(tempaddress);
					time.add(mtime + "");
					day_type.add(mmonth + " " + temptype);
					body.add(tempbody);
				}

			}
		}
		c.close();
		listview2 = (ListView) floatingview.findViewById(R.id.listview2);
		MessageAdapter adapter = new MessageAdapter(floatingview.getContext(),
				number, time, day_type, body);
		listview2.setAdapter(adapter);

	}

}
