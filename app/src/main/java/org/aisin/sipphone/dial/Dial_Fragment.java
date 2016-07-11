package org.aisin.sipphone.dial;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.GimageInfo;
import org.aisin.sipphone.dial.GImagePagerAdapter.GPSONpause;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.ContactLoadinteface;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.GetAction_reports;
import org.aisin.sipphone.tools.GetGImageMap;
import org.aisin.sipphone.tools.KeyBoardSoundPools;
import org.aisin.sipphone.tools.Num4SearchP;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.lidroid.xutils.BitmapUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Intents;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Dial_Fragment extends Fragment implements OnTouchListener,
		OnPageChangeListener, OnClickListener, OnLongClickListener,
		ContactLoadinteface, OnScrollListener, TextWatcher, GPSONpause {
	private Dial_barImageInterf dbiif;// 改变Activity bar图片回调对象
	private View dial_fragment;
	private RelativeLayout dial_guangao_layout;
	private ListView dialactivitysherchlist;
	private RelativeLayout notnetlj;
	private String ipsmstr;
	private ViewPager dial_ggtp;
	private LinearLayout dial_ggtp_title;
	private ImageView[] images;

	private EditText call_phonenum;
	private TextView guishudi;
	private RelativeLayout dial_numall_layout;
	private LinearLayout dial_numall_layout_line;
	private RelativeLayout phonenum_1;
	private RelativeLayout phonenum_2;
	private RelativeLayout phonenum_3;
	private RelativeLayout phonenum_4;
	private RelativeLayout phonenum_5;
	private RelativeLayout phonenum_6;
	private RelativeLayout phonenum_7;
	private RelativeLayout phonenum_8;
	private RelativeLayout phonenum_9;
	private RelativeLayout phonenum_x;
	private RelativeLayout phonenum_0;
	private RelativeLayout phonenum_j;
	private TextView add_call_more_img;
	private TextView phonenum_callbt;
	private TextView phonenum_bank;
	private Thread mythread;
	private BitmapUtils bitmapUtils;

	private String[] MP;// 谱
	private int MSTOP;// 当前音频已经播放到的位置位置

	private ArrayList<GimageInfo> ginfos = new ArrayList<GimageInfo>();
	private boolean ggFlag = true;// 广告自动切换开关
	private int hd_value = 0;// 滑动速度
	private GImagePagerAdapter gpa;
	private ArrayList<Contact> contacts;// 通讯录数据
	private TreeSet<Contact> contacts_scresult = new TreeSet<Contact>();// 通讯录检索结果
	private TreeSet<Contact> contacts_scresult_4s = new TreeSet<Contact>();// 用于搜索检索的集合
	private String sherchtext_old;// 前一次搜索保留的字符串
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (dial_ggtp != null && gpa != null
						&& GetGImageMap.imagelist.size() > 1) {
					// 回滚到下一个页面
					dial_ggtp.setCurrentItem(dial_ggtp.getCurrentItem() + 1);
				}
			} else if (msg.what == 2) {
				// 输入了号码触发的搜索
				String str_sherch = msg.getData().getString("sherchtext");
				if ("".equals(str_sherch)) {
					guishudi.setText("");
					// 隐藏TabBar打电话按钮
					dbiif.TabBarShow(false);
					dial_numall_layout.scrollTo(0, 0);
					if (dbiif != null) {
						// 调整图片为向上
						dbiif.Changeimage(R.drawable.dial_selected1_up);
					}
					// 如果输入的搜索文本清空了
					// 更新号码显示栏隐藏
					call_phonenum.setVisibility(View.INVISIBLE);
					// 设置添加联系人加号按钮为灰色
					add_call_more_img
							.setBackgroundResource(R.drawable.add_call_more_disable);
					// 设置广告展示的viewpager为可见
					dial_guangao_layout.setVisibility(View.VISIBLE);
					// 开启广告可循环开关
					ggFlag = true;
					// 清空搜索结构集合
					contacts_scresult.clear();
					// 清空用于搜索检索的集合
					contacts_scresult_4s.clear();
					// 置前一次搜索的字符串为空
					sherchtext_old = null;
					// 清空展示搜索结果的listview
					dialactivitysherchlist
							.setAdapter(new Dial_SherchListAdapter(
									AisinActivity.context, contacts_scresult,
									Dial_SherchListAdapter.DF));
				} else {
					// 显示TabBar打电话按钮
					dbiif.TabBarShow(true);
					// 更新号码显示栏显示
					call_phonenum.setVisibility(View.VISIBLE);
					// 设置添加联系人加号按钮为黑色
					add_call_more_img
							.setBackgroundResource(R.drawable.add_call_more_default);
					// 设置广告展示的viewpager为不可见
					dial_guangao_layout.setVisibility(View.INVISIBLE);
					// 停止广告滚动开关
					ggFlag = false;
					// 查询归属地
					if ((str_sherch.startsWith("1") && str_sherch.length() > 6 && str_sherch
							.length() <= 11)
							|| (str_sherch.startsWith("0")
									&& str_sherch.length() > 2 && str_sherch
									.length() <= 12)) {
						ArrayList<String> array = new ArrayList<String>();
						array.add(str_sherch);
						try {
							guishudi.setText(GetPhoneInfo4DB.getInfo(
									AisinActivity.context, array).get(0));
						} catch (Exception e) {
						}
					} else {
						guishudi.setText("");
					}
					// 如果通讯录数据为空 先加载通讯录数据
					if (contacts == null) {
						new Thread(new Runnable() {// 开启线程加载耗时任务
									@Override
									public void run() {
										CursorTools.loadContacts_2(
												AisinActivity.context,
												Dial_Fragment.this, false);
									}
								}).start();
					} else {
						mHandler.sendEmptyMessage(3);// 加载搜索记录
					}
				}
			} else if (msg.what == 3) {
				upAdapterdate();// 更新adapter
			} else if (msg.what == 4) {// 下降九宫格
				hd_value += 10;
				if (dial_numall_layout.getScrollY() - hd_value <= -dial_numall_layout
						.getMeasuredHeight() - 15) {
					dial_numall_layout.scrollTo(0,
							-dial_numall_layout.getHeight() - 15);
					hd_value = 0;
				} else {
					dial_numall_layout.scrollTo(0,
							(int) (dial_numall_layout.getScrollY() - hd_value));
				}
			} else if (msg.what == 5) {// 上升九宫格

				hd_value += 10;
				if (dial_numall_layout.getScrollY() + hd_value >= 0) {
					dial_numall_layout.scrollTo(0, 0);
					hd_value = 0;
				} else {
					dial_numall_layout.scrollTo(0,
							(int) (dial_numall_layout.getScrollY() + hd_value));
				}
			} else if (msg.what == 6) {// 更新广告
				if (GetGImageMap.imagelist == null
						|| GetGImageMap.imagelist.size() == 0) {
					return;
				}
				for (GimageInfo ginfo : GetGImageMap.imagelist) {
					if (ginfo.getBitmap() == null) {
						return;
					}
				}
				ginfos.clear();
				ginfos.addAll(GetGImageMap.imagelist);
				ginfos.add(0, ginfos.get(ginfos.size() - 1));
				ginfos.add(ginfos.get(1));
				// 调整广告展示viewpager的展示页
				if (gpa == null) {
					gpa = new GImagePagerAdapter(AisinActivity.context, ginfos,
							true, true);
					gpa.SetOnPauseLisen(Dial_Fragment.this);
					dial_ggtp.setAdapter(gpa);
					dial_ggtp.setCurrentItem(1);
				} else {
					gpa.notifyDataSetChanged();
				}
				dial_ggtp_title.removeAllViews();
				images = new ImageView[ginfos.size() - 2];
				ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(
						30, 20);
				for (int i = 0; i < ginfos.size() - 2; i++) {
					ImageView mViewPagerImageView = new ImageView(
							AisinActivity.context);
					mViewPagerImageView
							.setLayoutParams(viewPagerImageViewParams);
					if (i == 0) {
						mViewPagerImageView
								.setImageResource(R.drawable.bootpagerchanged);
					} else {
						mViewPagerImageView
								.setImageResource(R.drawable.bootpagerunchanged);
					}
					images[i] = mViewPagerImageView;
					dial_ggtp_title.addView(mViewPagerImageView);
				}
				if (mythread == null) {
					mythread = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								try {
									Thread.sleep(3000);
									if (ggFlag) {
										mHandler.sendEmptyMessage(1);
									}
								} catch (Exception e) {
								}
							}
						}
					});
					mythread.start();
				}

			} else if (msg.what == 7) {
				DownORHNP();// 伸缩键盘
			} else if (msg.what == 8) {
				DownORHNP(1);// listview滑动伸缩键盘
			} else if (msg.what == 9) {
				// 清除拨号盘号码
				call_phonenum.setText("");
			} else if (msg.what == 10) {
				CallPhonenew();
			} else if (msg.what == 11) {
				numbanck();
			} else if (msg.what == 12) {
				call_phonenum.setText("");// 长按
			} else if (msg.what == 13) {
				SharedPreferences sp = SharedPreferencesTools
						.getSharedPreferences_4keybackground(AisinActivity.context);
				String morenbk = sp.getString(SharedPreferencesTools.KBG_KEY,
						Constants.KEYBACK_MR);
				boolean zdyflag = sp.getBoolean(SharedPreferencesTools.KBG_ZDY,
						false);
				if (Constants.KEYBACK_WRIGHT.equals(morenbk)) {
					dial_numall_layout_line.setBackgroundColor(Color
							.parseColor("#FFFFFF"));
				} else {
					if (zdyflag) {
						bitmapUtils.display(dial_numall_layout_line, morenbk);
					} else {
						bitmapUtils.display(dial_numall_layout_line, "assets/"
								+ Constants.KEYBACK + "/" + morenbk);
					}
				}
			} else if (msg.what == 14) {
				try {
					MP = null;
					MSTOP = 0;
					String musicptext = SharedPreferencesTools
							.getSharedPreferences_4KEYMUSIC(
									AisinActivity.context).getString(
									SharedPreferencesTools.KEYMUSIC_KEY, "无");
					if ("无".equals(musicptext)) {
						return;
					}
					AssetManager asmg = AisinActivity.context.getAssets();
					InputStream inputs = asmg.open(Constants.musicp + "/"
							+ musicptext);
					StringBuffer out = new StringBuffer();
					byte[] bytes = new byte[1024];
					for (int n; (n = inputs.read(bytes)) != -1;) {
						out.append(new String(bytes, 0, n));
					}
					inputs.close();
					MP = out.toString().split("y");
				} catch (Exception e) {
				}
			} else if (msg.what == 15) {
				if (MP == null
						|| KeyBoardSoundPools
								.getSoundPools(AisinActivity.context) == null) {
					return;
				}
				try {
					for (String stt : MP[MSTOP].split("m")) {
						Integer soundsidtemp = KeyBoardSoundPools
								.getSoundsIDs().get(stt);
						if (soundsidtemp != null) {
							KeyBoardSoundPools.getSoundPools(
									AisinActivity.context).play(soundsidtemp,
									1, 1, 1, 0, 1.0f);
						}
					}
					MSTOP += 1;
					if (MSTOP >= MP.length) {
						MSTOP = 0;
					}
				} catch (Exception e) {
				}
			} else if (msg.what == 16) {
				//notnetlj.setVisibility(View.VISIBLE);
			} else if (msg.what == 17) {
				//notnetlj.setVisibility(View.GONE);
			} else if (msg.what == 18) {
				int scitem = msg.getData().getInt("scitem");
				dial_ggtp.setCurrentItem(scitem, false);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		dial_fragment = inflater.inflate(R.layout.dialactivity, container,
				false);
		dialactivitysherchlist = (ListView) dial_fragment
				.findViewById(R.id.dialactivitysherchlist);
		notnetlj = (RelativeLayout) dial_fragment.findViewById(R.id.notnetlj);
		dial_ggtp = (ViewPager) dial_fragment.findViewById(R.id.dial_ggtp);
		dial_ggtp_title = (LinearLayout) dial_fragment
				.findViewById(R.id.dial_ggtp_title);
		dial_guangao_layout = (RelativeLayout) dial_fragment
				.findViewById(R.id.dial_guangao_layout);
		call_phonenum = (EditText) dial_fragment
				.findViewById(R.id.call_phonenum);
		guishudi = (TextView) dial_fragment.findViewById(R.id.guishudi);
		dial_numall_layout = (RelativeLayout) dial_fragment
				.findViewById(R.id.dial_numall_layout);
		dial_numall_layout_line = (LinearLayout) dial_fragment
				.findViewById(R.id.dial_numall_layout_line);
		phonenum_1 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_1);
		phonenum_2 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_2);
		phonenum_3 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_3);
		phonenum_4 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_4);
		phonenum_5 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_5);
		phonenum_6 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_6);
		phonenum_7 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_7);
		phonenum_8 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_8);
		phonenum_9 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_9);
		phonenum_x = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_x);
		phonenum_0 = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_0);
		phonenum_j = (RelativeLayout) dial_fragment
				.findViewById(R.id.phonenum_j);
		add_call_more_img = (TextView) dial_fragment
				.findViewById(R.id.add_call_more_img);
		phonenum_callbt = (TextView) dial_fragment
				.findViewById(R.id.phonenum_callbt);
		phonenum_bank = (TextView) dial_fragment
				.findViewById(R.id.phonenum_bank);

		// 监听listview滑动以缩下输入键盘
		dialactivitysherchlist.setOnScrollListener(this);

		phonenum_1.setOnTouchListener(this);
		phonenum_2.setOnTouchListener(this);
		phonenum_3.setOnTouchListener(this);
		phonenum_4.setOnTouchListener(this);
		phonenum_5.setOnTouchListener(this);
		phonenum_6.setOnTouchListener(this);
		phonenum_7.setOnTouchListener(this);
		phonenum_8.setOnTouchListener(this);
		phonenum_9.setOnTouchListener(this);
		phonenum_x.setOnTouchListener(this);
		phonenum_0.setOnTouchListener(this);
		phonenum_j.setOnTouchListener(this);
		add_call_more_img.setOnTouchListener(this);
		phonenum_callbt.setOnTouchListener(this);
		phonenum_bank.setOnTouchListener(this);

		phonenum_1.setOnClickListener(this);
		phonenum_2.setOnClickListener(this);
		phonenum_3.setOnClickListener(this);
		phonenum_4.setOnClickListener(this);
		phonenum_5.setOnClickListener(this);
		phonenum_6.setOnClickListener(this);
		phonenum_7.setOnClickListener(this);
		phonenum_8.setOnClickListener(this);
		phonenum_9.setOnClickListener(this);
		phonenum_x.setOnClickListener(this);
		phonenum_0.setOnClickListener(this);
		phonenum_j.setOnClickListener(this);
		notnetlj.setOnClickListener(this);
		phonenum_x.setOnLongClickListener(this);
		phonenum_j.setOnLongClickListener(this);
		add_call_more_img.setOnClickListener(this);
		phonenum_callbt.setOnClickListener(this);
		phonenum_bank.setOnClickListener(this);
		phonenum_bank.setOnLongClickListener(this);
		bitmapUtils = new BitmapUtils(AisinActivity.context);

		// 搜索监听事件
		call_phonenum.addTextChangedListener(this);
		// 禁用输入法
		call_phonenum.setInputType(InputType.TYPE_NULL);
		// 载入viewpager广告
		dial_ggtp.setOnPageChangeListener(this);

		mHandler.sendEmptyMessage(6);
		mHandler.sendEmptyMessage(13);
		mHandler.sendEmptyMessage(14);

		if (Check_network.isNetworkAvailable(AisinActivity.context)) {
			mHandler.sendEmptyMessage(17);
		} else {
			mHandler.sendEmptyMessage(16);
		}
		return dial_fragment;
	}

	// 响应按钮按下切换背景 由于点击事件被数字占用了 父窗体只能用代码
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int id = v.getId();
		if (id == R.id.phonenum_1 || id == R.id.phonenum_2
				|| id == R.id.phonenum_3 || id == R.id.phonenum_4
				|| id == R.id.phonenum_5 || id == R.id.phonenum_6
				|| id == R.id.phonenum_7 || id == R.id.phonenum_8
				|| id == R.id.phonenum_9 || id == R.id.phonenum_x
				|| id == R.id.phonenum_0 || id == R.id.phonenum_j) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.num_pressed);
				mHandler.sendEmptyMessage(15);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.num_default);
			}
		} else if (id == R.id.add_call_more_img) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.add_call_more_default);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.add_call_more_disable);
			}
		} else if (id == R.id.phonenum_callbt) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.callbt_over);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.callbt_default);
			}
		} else if (id == R.id.phonenum_bank) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.back_disabled);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.back_default);
			}
		}
		return false;// 注意该处返回false否则OnClickListener将监听不到 被结束了
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// 监听viewpager滚动
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// 监听viewpager滚动
		if (arg1 == 0.0f) {
			ggFlag = true;
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() { //
					Message message = new Message();
					message.what = 18;
					Bundle bundle = new Bundle();
					bundle.putInt("scitem", ginfos.size() - 2);
					message.setData(bundle);
					mHandler.sendMessage(message);
				}
			}, 150);
			return;
		} else if (arg0 == ginfos.size() - 1) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() { //
					Message message = new Message();
					message.what = 18;
					Bundle bundle = new Bundle();
					bundle.putInt("scitem", 1);
					message.setData(bundle);
					mHandler.sendMessage(message);
				}
			}, 150);
			return;
		}
		// 监听viewpager滚动
		try {
			GimageInfo ginfo = ginfos.get(arg0);
			GetAction_reports.ADDARSNUM(AisinActivity.context, ginfo.getAdid(),
					ginfo.getUrlprefix() + ginfo.getImagename(), 1, 0);
			if (arg0 == 0) {
				arg0 = ginfos.size() - 2;
			} else if (arg0 == ginfos.size() - 1) {
				arg0 = 0;
			} else {
				arg0 = arg0 - 1;
			}
			for (int i = 0; i < images.length; i++) {
				if (i == arg0) {
					images[arg0].setImageResource(R.drawable.bootpagerchanged);
				} else {
					images[i].setImageResource(R.drawable.bootpagerunchanged);
				}

			}

		} catch (Exception e) {
		}
	}

	// 响应按钮点击事件
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.phonenum_1:
			call_phonenum.append("1");
			break;
		case R.id.phonenum_2:
			call_phonenum.append("2");
			break;
		case R.id.phonenum_3:
			call_phonenum.append("3");
			break;
		case R.id.phonenum_4:
			call_phonenum.append("4");
			break;
		case R.id.phonenum_5:
			call_phonenum.append("5");
			break;
		case R.id.phonenum_6:
			call_phonenum.append("6");
			break;
		case R.id.phonenum_7:
			call_phonenum.append("7");
			break;
		case R.id.phonenum_8:
			call_phonenum.append("8");
			break;
		case R.id.phonenum_9:
			call_phonenum.append("9");
			break;
		case R.id.phonenum_x:
			call_phonenum.append("*");
			break;
		case R.id.phonenum_0:
			call_phonenum.append("0");
			break;
		case R.id.phonenum_j:
			call_phonenum.append("#");
			break;
		case R.id.add_call_more_img:// 添加联系人
			String str = call_phonenum.getText().toString().trim();
			if (str.length() > 0) {
				Intent intent = new Intent(Intent.ACTION_INSERT,
						Uri.withAppendedPath(
								Uri.parse("content://com.android.contacts"),
								"contacts"));
				intent.putExtra(Intents.Insert.PHONE, str);
				startActivity(intent);
			}
			break;
		case R.id.phonenum_callbt:// 打电话
			CallPhonenew();
			break;
		case R.id.phonenum_bank:// 退格
			numbanck();
			break;
		case R.id.notnetlj:// 查看IP电话说明
			if (ipsmstr == null) {
				try {
					InputStream in = getResources().openRawResource(R.raw.ipsm);
					StringBuilder strb = new StringBuilder();
					byte[] bys = new byte[1024];
					int num = 0;
					while ((num = in.read(bys)) != -1) {
						strb.append(new String(bys, 0, num, "UTF-8"));
					}
					in.close();
					ipsmstr = strb.toString();
				} catch (Exception e) {
				}
			}
			if (ipsmstr != null) {
				Intent intent = new Intent(AisinActivity.context,
						org.aisin.sipphone.setts.ShowTextView.class);
				intent.putExtra("title_name", "资费说明");
				intent.putExtra("show_text", ipsmstr);
				startActivity(intent);
			}
			break;
		}
	}

	// 退格
	private void numbanck() {
		String str2 = call_phonenum.getText().toString().trim();
		if (str2.length() > 0) {
			call_phonenum.setText(str2.substring(0, str2.length() - 1));
		}
	}

	// 打电话
	private void CallPhonenew() {
		String phonenum = call_phonenum.getText().toString();
		if (phonenum == null || "".equals(phonenum)) {
			return;
		}
		Contact cttemp = null;
		if (contacts != null) {
			for (Contact ctt : contacts) {
				for (String phone : ctt.getPhonesList()) {
					if (phonenum.equals(phone)) {
						// 检索这个要打的电话是否在联系人中
						cttemp = ctt;
						break;
					}
				}
			}
		}
		// 打电话
		if (cttemp != null) {
			CallPhoneManage.callPhone(AisinActivity.context, cttemp, phonenum);
		} else {
			CallPhoneManage.callPhone(AisinActivity.context, null, phonenum,
					phonenum);
		}
	}

	// 退格键 长按监听事件
	@Override
	public boolean onLongClick(View v) {
		int id = v.getId();
		if (id == R.id.phonenum_bank) {
			call_phonenum.setText("");
		} else if (id == R.id.phonenum_x) {
			Intent intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetKeyboardMusic.class);
			startActivity(intent);
		} else if (id == R.id.phonenum_j) {
			Intent intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetKeyback.class);
			startActivity(intent);
		}
		return true;
	}

	private synchronized void upAdapterdate() {
		if (contacts == null) {
			return;
		}
		// 清空上一次检索结果
		contacts_scresult.clear();
		// 得到输入的号码
		String str_sc = call_phonenum.getText().toString().trim();
		if (str_sc == null || "".equals(str_sc)) {
			return;
		}

		// 检查上一次是否有搜索记录
		if (sherchtext_old != null) {
			// 如果这一次搜索的字符串不是以上一次搜索的字符串开始 否责就从上一次的结果集合中搜索
			if (!str_sc.startsWith(sherchtext_old)) {
				// 则从总集合中搜索
				contacts_scresult_4s.clear();
				contacts_scresult_4s.addAll(contacts);
			}
		} else {
			// 如果没有上一次搜索的记录 也从总联系人集合中搜索
			contacts_scresult_4s.clear();
			contacts_scresult_4s.addAll(contacts);
		}
		sherchtext_old = str_sc;// 变更前次搜索字符串

		// 对数字对应的可能拼音做检索
		TreeSet<String> lists = Num4SearchP.getN4PResult(str_sc);
		if (lists == null) {
			lists = new TreeSet<String>();
		}
		// 把号码也加入到检索集合
		lists.add(str_sc);
		for (Contact ctt : contacts_scresult_4s) {// 从用于搜索的集合中检索
			TreeSet<String> set = ctt.getSearchlist4name();// 联系人检索集合
			for (String st : set) {// 联系人检索集合
				for (String s : lists) {// 用于检索的集合数组
					if (st.indexOf(s) >= 0) {// 联系人里用于检索的字符串是否包含本次输入的搜索字符串
						if (Check_format.check_ABC(s)) {// 如果从联系人中匹配到的是拼音
														// 应该做进一步处理
							for (char car : ctt.getSpy().toCharArray()) {// 如果不是以任何一个名字的首字母开始的，就跳过
								if (s.startsWith(car + "")) {
									ctt.setMatchstr(s);
									contacts_scresult.add(ctt);
									break;
								}
							}
						} else {
							ctt.setMatchstr(s);
							contacts_scresult.add(ctt);
							break;
						}
					}
				}
			}
		}
		lists.clear();
		lists = null;

		contacts_scresult_4s.clear();
		contacts_scresult_4s.addAll(contacts_scresult);// 更新用于检索的集合

		// 重置展示搜索结果的listview
		dialactivitysherchlist.setAdapter(new Dial_SherchListAdapter(
				AisinActivity.context, contacts_scresult,
				Dial_SherchListAdapter.DF));
	}

	// listview 滑动监听
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {// 滚动了
			// 缩下输入框
			// 缩下输入框
			mHandler.sendEmptyMessage(8);
		}
	}

	// listview滑动监听
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	// listview滑动调用的下降号码框
	public synchronized void DownORHNP(int num) {
		if (call_phonenum.getText().toString().length() == 0) {
			return;
		}
		if (dbiif != null) {// 改变bar图标为向下
			dbiif.Changeimage(R.drawable.dial_selected1_down);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (dial_numall_layout.getScrollY() == 0) {// 下降
					// 开启一个线程 不停发通知给hander
					while (dial_numall_layout.getScrollY() > -dial_numall_layout
							.getHeight() - 15) {
						mHandler.sendEmptyMessage(4);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}).start();
	}

	// 接收伸缩键盘的消息 由AisinActivity调用
	public void DownORHNP_mhander() {
		mHandler.sendEmptyMessage(7);
	}

	// 由AisinActivity调用发送的hander伸缩键盘
	public synchronized void DownORHNP() {
		if (call_phonenum.length() == 0) {
			return;
		}
		if (dial_numall_layout.getScrollY() == 0) {
			if (dbiif != null) {// 改变bar图标为向下
				dbiif.Changeimage(R.drawable.dial_selected1_down);
			}
		} else {
			if (dbiif != null) {// 改变bar图标为向上
				dbiif.Changeimage(R.drawable.dial_selected1_up);
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (dial_numall_layout.getScrollY() == 0) {// 下降
					// 开启一个线程 不停发通知给hander
					while (dial_numall_layout.getScrollY() > -dial_numall_layout
							.getHeight() - 15) {
						mHandler.sendEmptyMessage(4);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}

				} else {// 上升
						// 开启一个线程 不停发通知给hander
					while (dial_numall_layout.getScrollY() < 0) {
						mHandler.sendEmptyMessage(5);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
				}

			}
		}).start();
	}

	// 设置接口实例（该实例由AisinActivity实现，用于变换拨号BAR的图标）
	public void setDial_barImageInterf(Dial_barImageInterf dbiif) {
		this.dbiif = dbiif;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ggFlag = true;// 开启广告切换
	}

	@Override
	public void onPause() {
		super.onPause();
		ggFlag = false;// 停止广告切换
	}

	@Override
	public void DoadDown_map(TreeMap<Long, Contact> cttmap) {// 非改变UI可以不用在hander中进行
		contacts = new ArrayList<Contact>();
		TreeSet<Contact> settemp = new TreeSet<Contact>();// 排序
		TreeSet<Contact> flist = new TreeSet<Contact>();
		flist.addAll(CursorTools.friendslist);
		try {
			for (Entry<Long, Contact> et : cttmap.entrySet()) {
				settemp.add(et.getValue());
				Iterator<Contact> itlist = flist.iterator();
				while (itlist.hasNext()) {
					Contact ct = itlist.next();
					for (String str_p : et.getValue().getPhonesList()) {
						if (str_p.equals(ct.getFriendphone())) {
							itlist.remove();
						}
					}
				}
			}
		} catch (Exception e) {
		}
		contacts.addAll(settemp);
		contacts.addAll(flist);
		settemp.clear();
		settemp = null;
		flist.clear();
		flist = null;
		mHandler.sendEmptyMessage(3);
	}

	public void ReSetViewPager() {// 重载ViewPager
		mHandler.sendEmptyMessage(6);
	}

	public void Clearinputnum() {
		mHandler.sendEmptyMessage(9);
	}

	public void CallPhonenew_AisinActivity() {
		mHandler.sendEmptyMessage(10);// 打电话
	}

	public void numbanck_AisinActivity(boolean b) {
		if (b) {
			mHandler.sendEmptyMessage(12);// 长按退格
		} else {
			mHandler.sendEmptyMessage(11);// 退格
		}
	}

	@Override
	public void headimagedown(boolean upflag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void upfrendsdwon(boolean upflag) {
	}

	@Override
	public void showfriends(boolean upflag) {
		// TODO Auto-generated method stub

	}

	public void UPKeyBack() {
		mHandler.sendEmptyMessage(13);
	}

	public void UPKeyBoardMusic() {
		mHandler.sendEmptyMessage(14);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("sherchtext", s.toString());
		msg.setData(data);
		msg.what = 2;
		mHandler.sendMessage(msg);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	public void UPNetWorkFlag(boolean b) {
		if (b) {// 网络可用
			mHandler.sendEmptyMessage(17);
		} else {// 网络不可用
			mHandler.sendEmptyMessage(16);
		}
	}

	@Override
	public void setStatic(boolean b) {
		// TODO Auto-generated method stub
		ggFlag = b;
	}

}
