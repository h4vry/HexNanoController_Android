package com.hexairbot.hexmini;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.CamcorderProfile;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hexairbot.hexmini.HexMiniApplication.AppStage;
import com.hexairbot.hexmini.modal.OSDCommon;
import com.hexairbot.hexmini.modal.Transmitter;

public class MainExActivity extends FragmentActivity implements
		SettingsDialogDelegate, OnTouchListener,
		HudViewControllerDelegate {

	private static final String TAG = "MainExActivity";
	public static final int REQUEST_ENABLE_BT = 1;
	private static final int DIALOG_WIFI_DISABLE = 1000;

	private SettingsDialog settingsDialog;
	private HudExViewController hudVC;
	boolean isFirstRun = true;

	private ImageButton btnHome;
	private ImageButton btnSetting;
	private ImageButton btnPictures;
	private ImageButton btnVideos;

	TextView ssid;
	TextView connectState;
	private boolean isStarted = false;

	private long lastToastTime = 0;

	private LinearLayout splash;
	private static final int STOPSPLASH = 0;
	private static final long SPLASHTIME = 1000;

	// private Handler splashHandler = new Handler() {
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case STOPSPLASH:
	// SystemClock.sleep(4000);
	// splash.setVisibility(View.GONE);
	// break;
	// }
	// super.handleMessage(msg);
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "----onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		showSystemInfo();
		
		
		
		/*
		 * // ----------just for debug connectState = (TextView)
		 * this.findViewById(R.id.connect_state); View serverSelect =
		 * this.findViewById(R.id.server_select); if
		 * (DebugHandler.showServerSelect) {
		 * connectState.setVisibility(View.VISIBLE);
		 * serverSelect.setVisibility(View.VISIBLE); ssid = (TextView)
		 * this.findViewById(R.id.ssid); refreshWifiInfo(); Spinner s1 =
		 * (Spinner) findViewById(R.id.spinner1); final ArrayAdapter<String>
		 * adapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_expandable_list_item_1);
		 * adapter.add("请�?择服务器:");
		 * adapter.add("rtmp://192.168.1.1/live/stream");
		 * adapter.add("rtmp://10.0.14.153/live/stream");
		 * adapter.add("rtmp://10.0.12.191/live/stream");
		 * s1.setAdapter(adapter); s1.setOnItemSelectedListener(new
		 * OnItemSelectedListener() {
		 * 
		 * @Override public void onItemSelected(AdapterView<?> parent, View
		 * view, int position, long id) { Log.d(TAG, "Spinner1: position=" +
		 * position + " id=" + id); if (id > 0)
		 * connectIPC(adapter.getItem(position)); }
		 * 
		 * @Override public void onNothingSelected(AdapterView<?> parent) { //
		 * showToast("Spinner1: unselected"); } }); } else {
		 * serverSelect.setVisibility(View.GONE); } // //////////
		 */
		// setContentView(R.layout.hud_view_controller_framelayout);
		// splash = (LinearLayout) findViewById(R.id.splash);
		//
		// Message msg = new Message();
		// msg.what = STOPSPLASH;
		// splashHandler.sendMessageDelayed(msg, SPLASHTIME);

		hudVC = new HudExViewController(this, this);
		hudVC.onCreate();
		hudVC.onResume();
	}

    private void showSystemInfo() {
		Display wm = this.getWindow().getWindowManager().getDefaultDisplay();
		Log.d(TAG, "screen.w=" + wm.getWidth());
		Log.d(TAG, "screen.w=" + wm.getHeight());
		Log.d(TAG, "screen.getPixelFormat=" + wm.getPixelFormat());
		dumpVideoCapabilitiesInfo();
	}

	@SuppressLint("NewApi")
	private void dumpVideoCapabilitiesInfo() {
		// Here we try to use different methods to determine the maximum video
		// frame size
		// that device supports

//		Log.i(TAG, "=== DEVICE VIDEO SUPPORT ====>>>>>>>>>");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			Log.i(TAG, "Codecs available to the system: ");
			for (int i = 0; i < MediaCodecList.getCodecCount(); ++i) {
				MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);

				String[] supportedTypes = info.getSupportedTypes();
				StringBuilder supportedTypesBuilder = new StringBuilder();

				for (int j = 0; j < supportedTypes.length; ++j) {
					supportedTypesBuilder.append(supportedTypes[j]);
					if (j < (supportedTypes.length - 1)) {
						supportedTypesBuilder.append(", ");
					}
				}

				Log.i(TAG, info.getName() + " , supported types: "
						+ supportedTypesBuilder.toString());
				;
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
				Log.i(TAG, "Device supports HD video [720p]");
			} else if (CamcorderProfile
					.hasProfile(CamcorderProfile.QUALITY_480P)) {
				Log.i(TAG, "Device supports regular video [480p]");
			} else if (CamcorderProfile
					.hasProfile(CamcorderProfile.QUALITY_QVGA)) {
				Log.i(TAG, "Device supports low quality video [240p]");
			} else {
				Log.w(TAG, "Can't determine video support of this device.");
			}
		}

		CamcorderProfile prof = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		if (prof != null) {
			Log.i(TAG, "Highest video frame size for this device is ["
					+ prof.videoFrameWidth + ", " + prof.videoFrameHeight + "]");
		} else {
			Log.w(TAG, "Unable to determine highest possible video frame size.");
		}

		Log.i(TAG, "<<<<<<<<<=== DEVICE VIDEO SUPPORT ===");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Log.e("onDestroy", "");

		if (Transmitter.sharedTransmitter().getBleConnectionManager() != null) {
			Transmitter.sharedTransmitter().transmmitSimpleCommand(
					OSDCommon.MSPCommnand.MSP_DISARM);
			Transmitter.sharedTransmitter().getBleConnectionManager().close();
		}

		hudVC.onDestroy();
		hudVC = null;

		Thread destroy = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		destroy.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// mConnectStateManager.pause();
		super.onPause();
		//hudVC.onPause();
		
		Log.e("onPause", "onPause");
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		Log.e("onRestart", "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HexMiniApplication.sharedApplicaion().setAppStage(AppStage.HUD);
		//hudVC.onResume();
		
		Log.e("onResume", "onResume");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isStarted = true;
		// checkWifiEnable();
		//initBroadcastReceiver();

		hudVC.viewWillAppear();
		
		Log.e("onStart", "");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isStarted = false;
		//destroyBroadcastReceiver();
		HexMiniApplication.sharedApplicaion().setAppStage(AppStage.UNKNOWN);
		
		Log.e("onStop()", "onStop");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.d(TAG, "----onConfigurationChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Nothing need to be done here

		} else {
			// Nothing need to be done here
		}
	}

	@Override
	public void prepareDialog(SettingsDialog dialog) {

	}

	@Override
	public void onDismissed(SettingsDialog settingsDialog) {
		
		hudVC.setSettingsButtonEnabled(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void settingsBtnDidClick(View settingsBtn) {
		hudVC.setSettingsButtonEnabled(false);
		showSettingsDialog();
	}

	public ViewController getViewController() {
		return hudVC;
	}

	protected void showSettingsDialog() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.addToBackStack(null);

		if (settingsDialog == null) {
			Log.d(TAG, "settingsDialog is null");
			settingsDialog = new SettingsDialog(this, this);
		}

		settingsDialog.show(ft, "settings");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
