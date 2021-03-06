package org.aisin.sipphone;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.SwitchState;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.directcall.AisinManager;
import org.aisin.sipphone.directcall.AisinOutCallActivity;
import org.aisin.sipphone.directcall.AisinService;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.InitIsDoubleTelephone;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.linphone.core.LinphoneCoreException;

import com.android.internal.telephony.ITelephony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

public class CallPhoneManage {

	private final static String reg = "^[\\+]?[0-9\\-\\ ]{6,32}$";
	private static SharedPreferences sharepreferens;
	private static String ipcall_prefix;

	public static synchronized void callPhone(final Context context,
			final Contact contact) {

		int nums = contact.getPhonesList().size();
		if (nums > 1) {
			ArrayList<String> list = contact.getPhonesList();
			String[] cities_temp = new String[nums];
			for (int i = 0; i < nums; i++) {
				cities_temp[i] = list.get(i);
			}
			final String[] cities = cities_temp;

			AisinBuildDialog mybuild = new AisinBuildDialog(context);
			mybuild.setTitle("请选择要拨打的电话!");
			mybuild.setListViewItem(cities, new onMyItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					callPhone(context, contact, cities[position]);
				}
			});
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.dialogShow();

		} else {
			callPhone(context, contact, contact.getPhonesList().get(0));
		}
	}

	public static synchronized void callPhone(Context context, Contact contact,
			String phonenum) {
		byte[] bitmapByte = null;
		if (contact.getTx() != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			contact.getTx().compress(Bitmap.CompressFormat.PNG, 100, baos);
			bitmapByte = baos.toByteArray();
		}
		callPhone(context, bitmapByte, contact.getRemark(), phonenum);
	}

	public static synchronized void callPhone(final Context context,
			byte[] bitmapByte, String name, String phonenum) {
		sharepreferens = SharedPreferencesTools
				.getSharedPreferences_ALLSWITCH(context);
		ipcall_prefix = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(context).getString(
						SharedPreferencesTools.SPF_msglist_date_ipcall_prefix,
						"");
		if (!"".equals(ipcall_prefix) && phonenum.startsWith(ipcall_prefix)) {
			phonenum = phonenum.substring(ipcall_prefix.length());
		}
		// 检测特殊号码
		if (!Check_format.check_SPnum(phonenum) || phonenum.length() < 6
				|| phonenum.length() > 20) {
			final String phonenumf = phonenum;
			final Context contextf = context;
			AisinBuildDialog mybuild = new AisinBuildDialog(context);
			mybuild.setTitle("提示");
			mybuild.setMessage("使用系统呼叫?");
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							String actionstr = null;
							if (InitIsDoubleTelephone
									.GetinitIsDoubleTelephone(contextf)) {
								actionstr = Intent.ACTION_DIAL;
							} else {
								actionstr = Intent.ACTION_CALL;
							}
							Uri uri = Uri.parse("tel:" + phonenumf);
							Intent call = new Intent(actionstr, uri); // 直接播出电话
							contextf.startActivity(call);
						}
					});
			mybuild.dialogShow();
			return;
		}

		// 对号码进行验证
		if (phonenum == null || "".equals(phonenum) || !phonenum.matches(reg)) {
			new AisinBuildDialog(context, "提示", "手机号码不正确!");
			return;
		}

		// 检查固话
		if (phonenum.length() >= 6 && phonenum.length() <= 8) {
			new AisinBuildDialog(context, "提示", "如果是固话,请在号码前加拨区号!");
			return;
		}

		if (Check_network.isNetworkAvailable(context)) {
			// 检测直拨
			boolean ZBZLB = SharedPreferencesTools.getSharedPreferences_4ZBZL(
					context).getBoolean(SharedPreferencesTools.zbzlling, true);
			if (!ZBZLB) {
				call_gsm(context, bitmapByte, name, phonenum);
				return;
			}
			switch (Check_network.getNetworkClass(context)) {
			case 2:
				call_gsm(context, bitmapByte, name, phonenum);
				break;
			case 3:
				if (sharepreferens.getBoolean(
						SharedPreferencesTools.SPF_NETWORK_3G_4G, false)) {
					// 流量直拨
					redict_call(context, phonenum, name, bitmapByte);
				} else {
					// 传统回拨
					call_gsm(context, bitmapByte, name, phonenum);
				}
				break;
			case 4:
				if (sharepreferens.getBoolean(
						SharedPreferencesTools.SPF_NETWORK_3G_4G, false)) {
					// 流量直拨
					redict_call(context, phonenum, name, bitmapByte);
				} else {
					// 传统回拨
					call_gsm(context, bitmapByte, name, phonenum);
				}
				break;
			case 1:
				if (sharepreferens.getBoolean(
						SharedPreferencesTools.SPF_NETWORK_WIFI, false)) {
					// 流量直拨
					redict_call(context, phonenum, name, bitmapByte);
				} else {
					// 传统回拨
					call_gsm(context, bitmapByte, name, phonenum);
				}
				break;

			default:
				call_gsm(context, bitmapByte, name, phonenum);
				break;
			}

		} else {

			Pattern p = Pattern.compile("1[3-578][0-9]{9}");
			final Matcher m = p.matcher(phonenum);
			if (!"".equals(ipcall_prefix) && m.find()) {
				AisinBuildDialog mybuild = new AisinBuildDialog(context);
				mybuild.setTitle("网络不可用!");
				mybuild.setMessage(Html
						.fromHtml("<font color=#808080><small>爱信免流量回拨:只支持广东移动/联通用户、国内其它部分地区移动用户</small></font>"));
				mybuild.setListViewItem(new String[] { "尝试爱信免流量回拨", "手机拨打" },
						new onMyItemClickListener() {
							private boolean answallflag;
							private int answallswitch;
							private TelephonyManager managr;
							private myPhoneReceiverCall mpreceive;

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								String actionstr = null;
								if (InitIsDoubleTelephone
										.GetinitIsDoubleTelephone(context)) {
									actionstr = Intent.ACTION_DIAL;
								} else {
									actionstr = Intent.ACTION_CALL;
								}
								if (position == 0) {
									answallswitch = SharedPreferencesTools
											.getSharedPreferences_ALLSWITCH(
													context)
											.getInt(SharedPreferencesTools.SPF_ALLSWITCH_ANSWALLFLAG,
													SwitchState.ANSWALL_Y);
									answallflag = true;
									managr = (TelephonyManager) context
											.getSystemService(Context.TELEPHONY_SERVICE);
									mpreceive = new myPhoneReceiverCall();
									managr.listen(
											mpreceive,
											PhoneStateListener.LISTEN_CALL_STATE);// 自动接听
									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												Thread.sleep(10000);
											} catch (InterruptedException e) {
											}
											// 置监听器为空
											managr.listen(
													mpreceive,
													PhoneStateListener.LISTEN_NONE);
											managr = null;
										}
									}).start();

									Uri uri = Uri.parse("tel:" + ipcall_prefix
											+ m.group(0));
									Intent call = new Intent(actionstr, uri); // 直接播出电话
									context.startActivity(call);
								} else if (position == 1) {
									Uri uri = Uri.parse("tel:" + m.group(0));
									Intent call = new Intent(actionstr, uri); // 直接播出电话
									context.startActivity(call);
								}
							}

							class myPhoneReceiverCall extends
									PhoneStateListener {
								@SuppressLint("HandlerLeak")
								private Handler mHandler_s = new Handler() {
									@SuppressWarnings({ "unchecked",
											"static-access", "rawtypes" })
									public void handleMessage(Message msg) {
										super.handleMessage(msg);
										if (msg.what == 1) {
											try {
												try {
													TelephonyManager tm = (TelephonyManager) context
															.getSystemService(context.TELEPHONY_SERVICE);
													Class c = Class.forName(tm
															.getClass()
															.getName());
													Method m = c
															.getDeclaredMethod("getITelephony");
													m.setAccessible(true);
													ITelephony telephonyService;
													telephonyService = (ITelephony) m
															.invoke(tm);
													telephonyService
															.answerRingingCall();
												} catch (Exception e) {
													Intent intent = new Intent(
															"android.intent.action.MEDIA_BUTTON");
													KeyEvent keyEvent = new KeyEvent(
															KeyEvent.ACTION_DOWN,
															KeyEvent.KEYCODE_HEADSETHOOK);
													intent.putExtra(
															"android.intent.extra.KEY_EVENT",
															keyEvent);
													context.sendOrderedBroadcast(
															intent,
															"android.permission.CALL_PRIVILEGED");
													intent = new Intent(
															"android.intent.action.MEDIA_BUTTON");
													keyEvent = new KeyEvent(
															KeyEvent.ACTION_UP,
															KeyEvent.KEYCODE_HEADSETHOOK);
													intent.putExtra(
															"android.intent.extra.KEY_EVENT",
															keyEvent);
													context.sendOrderedBroadcast(
															intent,
															"android.permission.CALL_PRIVILEGED");
												}
											} catch (Exception e) {
											} catch (Error e) {
											}
										}
									}
								};

								@Override
								public void onCallStateChanged(int state,
										String incomingNumber) {
									// TODO Auto-generated method stub
									super.onCallStateChanged(state,
											incomingNumber);
									switch (state) {
									case TelephonyManager.CALL_STATE_RINGING:// 电话呼入
										if (answallflag) {
											answallflag = false;
										} else {
											break;
										}
										// 自动接听
										if (answallswitch == SwitchState.ANSWALL_Y
												&& PhoneInfo.SDKVersion < 20) {
											mHandler_s.sendEmptyMessage(1);
										}
										break;
									case TelephonyManager.CALL_STATE_IDLE:// 电话挂断
										break;
									}
								}
							}
						});
				mybuild.setOnDialogCancelListener("取消", null);
				mybuild.dialogShow();
			} else {
				new AisinBuildDialog(context, "提示", "网络连接不可用,请检查网络!");
			}
		}

	}

	private static void call_gsm(Context context, byte[] bitmapByte,
			String name, String phonenum) {
		// 传统回拨
		// 启动等待界面
		Intent intent = null;
		String startflag = SharedPreferencesTools.getSharedPreferences_4YCSZ(
				context).getString(SharedPreferencesTools.ycsz_outv,
				Constants.calloutactivity);
		if ("1".equals(startflag)) {
			intent = new Intent(context,
					org.aisin.sipphone.TraditionalDialBackActivity.class);
		} else if ("2".equals(startflag)) {
			intent = new Intent(context,
					org.aisin.sipphone.TraditionalDialBackActivity_v2.class);
		}
		if (intent == null) {
			return;
		}
		if (bitmapByte != null) {
			intent.putExtra("Contact.tx", bitmapByte);
		}
		intent.putExtra("Contact.name", name);
		intent.putExtra("Contact.phonenum", phonenum);
		context.startActivity(intent);
	}

	public static String fomartphonenum(String to) {
		if (ipcall_prefix != null && !"".equals(ipcall_prefix)) {
			if (to.startsWith(ipcall_prefix)) {
				to = to.substring(ipcall_prefix.length());
			}
		}
		if (to.startsWith("+")) {
			to = "00" + to.substring(1);
		}
		if (to.startsWith("86")) {
			to = to.substring(2);
		} else if (to.startsWith("086") || to.startsWith("+86")) {
			to = to.substring(3);
		} else if (to.startsWith("0086") || to.startsWith("+086")) {
			to = to.substring(4);
		} else if (to.startsWith("12593") || to.startsWith("12580")
				|| to.startsWith("17909") || to.startsWith("17951")
				|| to.startsWith("17950") || to.startsWith("10193")
				|| to.startsWith("17911")) {
			to = to.substring(5);
		}
		if (to.startsWith("1") && !to.startsWith("0")) {
			to = "0" + to;
		}
		return to;
	}

	private static void redict_call(final Context context,
			final String phonenum, final String name, final byte[] bitmapByte) {
		UserInfo userInfo = UserInfo_db.getUserInfo(context);
		if (phonenum.length() <= 5) {
			new AisinBuildDialog(context, "提示", "号码错误,输入的号码长度不够!");
			return;
		} else if (phonenum.indexOf(userInfo.getPhone()) != -1) {
			new AisinBuildDialog(context, "提示", "无法拨打当前登录号码!");
			return;
		}

		Intent intents = new Intent(context,
				org.aisin.sipphone.directcall.AisinService.class);
		context.stopService(intents);
		context.startService(intents);

		// if (!AisinManager.isInstanciated())
		// AisinManager.createAndStart(context);
		// if (!AisinService.isReady()) {
		// Intent intent = new Intent(context, AisinService.class);
		// context.startService(intent);
		// }

		// 已放入AisinService中注册
		// try {
		// // 注册
		// AisinManager.getInstance().initAccount();
		// } catch (LinphoneCoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Intent intent = new Intent(context, AisinOutCallActivity.class);
		if (bitmapByte != null) {
			intent.putExtra("Contact.tx", bitmapByte);
		}
		intent.putExtra("Contact.name", name);
		intent.putExtra("Contact.phonenum", phonenum);
		context.startActivity(intent);

	}

}
