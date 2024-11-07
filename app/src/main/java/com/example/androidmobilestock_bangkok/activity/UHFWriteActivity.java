package com.example.androidmobilestock_bangkok.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.fragment.UHFWriteFragment;
import com.example.androidmobilestock_bangkok.widget.NoScrollViewPager;
import com.rscja.utility.StringUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * UHF使用demo
 * 
 * 1、使用前请确认您的机器已安装此模块。 
 * 2、要正常使用模块需要在\libs\armeabi\目录放置libDeviceAPI.so文件，同时在\libs\目录下放置DeviceAPIver20160728.jar文件。 
 * 3、在操作设备前需要调用 init()打开设备，使用完后调用 free() 关闭设备
 * 
 * 
 * 更多函数的使用方法请查看API说明文档
 * 
 * @author
 * 更新于 2016年8月9日
 */
public class UHFWriteActivity extends BaseTabFragmentActivity {

	ACDatabase db;
	private final static String TAG = "MainActivity";
	AC_Class.RFIDDtlList itDtl;

	public ArrayList<HashMap<String, String>> tagList;
	public SimpleAdapter adapter;

	LinearLayout layout12,linearLayout4;
	public NoScrollViewPager pager;

	int mode = 0;

	public ListView LvTags;
	private HashMap<String, String> map;
//	public AppContext appContext;// ȫ��Context
//
	// public Reader mReader;
//	public RFIDWithUHF mReader;

//	public void playSound(int id) {
//		appContext.playSound(id);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main_rfid);
			ActionBar actionBar = getSupportActionBar();
			actionBar.setTitle("RFID");
			actionBar.setDisplayHomeAsUpEnabled(true);
	        Log.wtf("Intent Start:","UHFMAINACTIVITY");

	        pager = (NoScrollViewPager) findViewById(R.id.pager);

			LvTags = (ListView) findViewById(R.id.LvTagsMain);
			tagList = new ArrayList<HashMap<String, String>>();
			adapter = new SimpleAdapter(UHFWriteActivity.this, tagList, R.layout.listtag_items,
					new String[]{"tagUii", "tagCount","tagStatus"},
					new int[]{R.id.TvTagUii2, R.id.TvTagCount,R.id.tvTagStatus});

			db = new ACDatabase(this);

			mode = getIntent().getIntExtra("mode",0);

//		if (Build.VERSIiON.SDK_INT > 21) {
//
//			//读写内存权限
//			if (ContextCompat.checkSelfPermission(this,
//					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//				// 请求权限
//				ActivityCompat
//						.requestPermissions(
//								this,
//								new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//								1);
//			}
//
//			int checkCallPhonePermission = ContextCompat.checkSelfPermission(
//					this, Manifest.permission.READ_EXTERNAL_STORAGE);
//			if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//				ActivityCompat.requestPermissions(this, new String[]{
//						Manifest.permission.WRITE_EXTERNAL_STORAGE,
//						Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
//				return;
//			} else {
//				// 已申请权限直接跳转到下一个界面
//
//
//			}
//		}

//			appContext = (AppContext) getApplication();

			initSound();
	        initUHF();//��ʼ��
			initViewPageData();
	        initViewPager();
	        //initTabs();
	}

	@Override
	public void onBackPressed() {
		Intent newIntent = new Intent();
		setResult(99,newIntent);
		mReader.free();
		finish();

		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				onBackPressed();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	    protected void initViewPageData() {
		if(mode == 3)
		{
			lstFrg.add(new UHFWriteFragment());
		}
		 	//Commenting out Kill and Lock Tabs
	        /*
	        //lstFrg.add(new UHFKillFragment());
	        //lstFrg.add(new UHFLockFragment());

	        //lstTitles.add(getString(R.string.uhf_msg_tab_kill));
	        //lstTitles.add(getString(R.string.uhf_msg_tab_lock));
	        */
	    }

	@Override
	protected void onDestroy() {
		if (mReader != null) {
			mReader.free();
		}
		super.onDestroy();
	}

	/**
	 * �豸�ϵ��첽��
	 *
	 * @author liuruifeng
	 */
	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog mypDialog;
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return mReader.init();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mypDialog.cancel();

			if (!result) {
				Toast.makeText(UHFWriteActivity.this, "init fail",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mypDialog = new ProgressDialog(UHFWriteActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("init...");
			mypDialog.setCanceledOnTouchOutside(false);
			mypDialog.show();
		}
	}
	/**
	 * ��֤ʮ����������Ƿ���ȷ
	 *
	 * @param str
	 * @return
	 */
	public boolean vailHexInput(String str) {

		if (str == null || str.length() == 0) {
			return false;
		}

		// ���ȱ�����ż��
		if (str.length() % 2 == 0) {
			return StringUtility.isHexNumberRex(str);
		}

		return false;
	}
	HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
	private SoundPool soundPool;
	private float volumnRatio;
	private AudioManager am;
	private void initSound(){
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
		soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
		am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
	}
	/**
	 * 播放提示音
	 *
	 * @param id 成功1，失败2
	 */
	public void playSound(int id) {

		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
		volumnRatio = audioCurrentVolumn / audioMaxVolumn;
		try {
			soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
					volumnRatio, // 右声道音量
					1, // 优先级，0为最低
					0, // 循环次数，0无不循环，-1无永远循环
					1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
			);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}