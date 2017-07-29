package com.shubham.interactionlog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
	public static String CallerNumber = "XXXXXXXXXX";

	public Intent intentService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentService = new Intent(this, MyService.class);
		if (!MyService.isOn) {
			startService(intentService);
		}

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				String numbert = incomingNumber;
				if (state == TelephonyManager.CALL_STATE_RINGING) {
					CallerNumber = incomingNumber;
					initializeView();
				}
			}
		};

		telephonyManager.listen(phoneStateListener,
				phoneStateListener.LISTEN_CALL_STATE);
		checkRunTimePermission();
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& !Settings.canDrawOverlays(getApplicationContext())) {

			Intent intent = new Intent(
					Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);

		} else {
			// initializeView();
		}

	}

	private void checkRunTimePermission() {
		String[] permissionArrays = new String[] {
				Manifest.permission.READ_CALL_LOG,
				Manifest.permission.READ_SMS,
				Manifest.permission.READ_PHONE_STATE };

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(permissionArrays, 11111);
		}
	}

	private void initializeView() {
		startService(new Intent(MainActivity.this, FloatingLayout.class));
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

			if (resultCode == RESULT_OK) {
				initializeView();
			} else {
				finish();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		boolean openActivityOnce = true;
		boolean openDialogOnce = true;
		boolean isPermitted;
		if (requestCode == 11111) {
			for (int i = 0; i < grantResults.length; i++) {
				String permission = permissions[i];

				isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

				if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
					// user rejected the permission
					boolean showRationale = shouldShowRequestPermissionRationale(permission);
					if (!showRationale) {
						// never ask again req
					} else {
						if (openDialogOnce) {
							checkRunTimePermission();
						}
					}
				}
			}

		}
	}

}
