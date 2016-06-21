package org.aisin.sipphone;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.directcall.AisinService;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class HttpTask_CheckUpdate extends AsyncTask<String, String, String> {
	private Context context;
	private int flag;
	private ProgressDialog prd;
	private Handler mHandler;

	private String ver;
	private String update_addr;
	private String update_tips;

	public HttpTask_CheckUpdate(Context context, int flag, ProgressDialog prd,
			Handler mHandler) {
		this.context = context;
		this.flag = flag;
		this.prd = prd;
		this.mHandler = mHandler;
	}

	@Override
	protected String doInBackground(String... paramArrayOfParams) {
		// 获取更新URL
		String url = URLTools.GetHttpURL_4CheckUpdate(context);
		String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
		try {
			// 存储信息
			SharedPreferences shared = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(context);
			Editor edit = shared.edit();
			JSONObject json = new JSONObject(result);
			int temp = json.optInt("result", -104);
			if (temp != 0) {
				return null;
			}
			String invite_title = json
					.optString(SharedPreferencesTools.SPF_msglist_date_INVITE_TITLE);
			String invite_app = json
					.optString(SharedPreferencesTools.SPF_msglist_date_INVITE_APP);
			String invite_sms_message = json
					.optString(SharedPreferencesTools.SPF_msglist_date_INVITE_SMS_MESSAGE);
			String invite_sns_message = json
					.optString(SharedPreferencesTools.SPF_msglist_date_INVITE_SNS_MESSAGE);
			String invite_url = json
					.optString(SharedPreferencesTools.SPF_msglist_date_INVITE_URL);
			String fee_rate = json
					.optString(SharedPreferencesTools.SPF_msglist_date_FEERATE);
			String shopmall_title = json
					.optString(SharedPreferencesTools.SPF_msglist_date_SHOPMALL_TITLE);
			String shopmall_url = json
					.optString(SharedPreferencesTools.SPF_msglist_date_SHOPMALL_URL);
			ver = json.optString(SharedPreferencesTools.upAPPVer);
			String vps = json
					.optString(SharedPreferencesTools.SPF_msglist_date_VPS);
			String service_phone = json
					.optString(SharedPreferencesTools.SPF_msglist_date_service_phone);
			String giftshare_url = json
					.optString(SharedPreferencesTools.SPF_msglist_date_giftshare_url);
			String nearby_shop = json
					.optString(SharedPreferencesTools.SPF_msglist_date_nearby_shop);
			String direct_open = json
					.optString(SharedPreferencesTools.SPF_msglist_date_direct_open);
			String ipcall_prefix = json
					.optString(SharedPreferencesTools.SPF_msglist_date_ipcall_prefix);
			String direct_fee_rate = json
					.optString(SharedPreferencesTools.SPF_msglist_date_direct_fee_rate);
			if ("0".equals(direct_open)) {
				SharedPreferencesTools
						.getSharedPreferences_ALLSWITCH(context)
						.edit()
						.putBoolean(SharedPreferencesTools.SPF_NETWORK_3G_4G,
								false)
						.putBoolean(SharedPreferencesTools.SPF_NETWORK_WIFI,
								false).commit();
			} else if ("1".equals(direct_open)) {
				SharedPreferencesTools
						.getSharedPreferences_ALLSWITCH(context)
						.edit()
						.putBoolean(SharedPreferencesTools.SPF_NETWORK_3G_4G,
								true)
						.putBoolean(SharedPreferencesTools.SPF_NETWORK_WIFI,
								true).commit();
			}
			if (invite_title != null && !"".equals(invite_title)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_INVITE_TITLE,
						invite_title);
			}
			if (invite_app != null && !"".equals(invite_app)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_INVITE_APP,
						invite_app);
			}
			if (invite_sms_message != null && !"".equals(invite_sms_message)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_INVITE_SMS_MESSAGE,
						invite_sms_message);
			}
			if (invite_sns_message != null && !"".equals(invite_sns_message)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_INVITE_SNS_MESSAGE,
						invite_sns_message);
			}
			if (invite_url != null && !"".equals(invite_url)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_INVITE_URL,
						invite_url);
			}
			if (fee_rate != null && !"".equals(fee_rate)) {
				edit.putString(SharedPreferencesTools.SPF_msglist_date_FEERATE,
						fee_rate);
			}
			if (shopmall_title != null && !"".equals(shopmall_title)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_SHOPMALL_TITLE,
						shopmall_title);
			}
			if (shopmall_url != null && !"".equals(shopmall_url)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_SHOPMALL_URL,
						shopmall_url);
			}
			if (direct_fee_rate != null && !"".equals(direct_fee_rate)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_direct_fee_rate,
						direct_fee_rate);
			}
			if (vps != null && !"".equals(vps)) {
				edit.putString(SharedPreferencesTools.SPF_msglist_date_VPS, vps);
			}
			if (ver != null && !"".equals(ver)) {
				edit.putString(SharedPreferencesTools.upAPPVer, ver);
			} else {
				PackageManager manager = context.getPackageManager();
				PackageInfo info;
				String version = "";
				try {
					info = manager.getPackageInfo(context.getPackageName(), 0);
					version = info.versionName;
				} catch (NameNotFoundException e) {
				}
				edit.putString(SharedPreferencesTools.upAPPVer, version);
			}
			if (service_phone != null && !"".equals(service_phone)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_service_phone,
						service_phone);
			}
			if (giftshare_url != null && !"".equals(giftshare_url)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_giftshare_url,
						giftshare_url);
			}
			if (nearby_shop != null && !"".equals(nearby_shop)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_nearby_shop,
						nearby_shop);
			}
			if (ipcall_prefix != null && !"".equals(ipcall_prefix)) {
				edit.putString(
						SharedPreferencesTools.SPF_msglist_date_ipcall_prefix,
						ipcall_prefix);
			}
			edit.commit();
			// 保存agent_id
			try {
				String agentid = json.getString("agent_id");
				SharedPreferences spf = SharedPreferencesTools
						.getSharedPreferences_4agent_id(context);
				if (agentid != null && spf != null) {
					spf.edit()
							.putString(SharedPreferencesTools.agent_id,
									agentid.trim()).commit();
				}
			} catch (Exception e) {
			}

		} catch (JSONException e) {
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result == null) {
			return;
		}
		try {
			// 发送广播通知更新标志
			context.sendBroadcast(new Intent(Constants.BrandName
					+ ".redcedbd.upreddate.cannotcheck"));
			if (mHandler != null) {
				mHandler.sendEmptyMessage(1);
			}
			// 存储更新的时间
			SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
			String date = sdf.format(new Date());
			SharedPreferences sft = SharedPreferencesTools
					.getSharedPreferences_msglist_uptime_share(context);
			sft.edit()
					.putString(
							SharedPreferencesTools.SPF_msglist_uptime_UPTIME,
							date).commit();

			JSONObject jsonobject = new JSONObject(result.trim());
			update_addr = jsonobject.getString("update_addr");
			if (update_addr == null || "".equals(update_addr)) {
				if (flag == 1) {
					if (prd != null) {
						prd.dismiss();
					}
					new AisinBuildDialog(context, "提示", "当前已是最新版本！");

				}
				return;
			} else {
				if (prd != null) {
					prd.dismiss();
				}
				update_tips = jsonobject.getString("update_tips");

				AisinBuildDialog mybuild = new AisinBuildDialog(context);
				mybuild.setTitle("发现新版本");
				mybuild.setMessage("新版本更新内容如下:\n" + update_tips);
				mybuild.setOnDialogCancelListener("下次更新", null);
				mybuild.setOnDialogConfirmListener("确定更新",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								 Intent uppdate = new Intent(
								 "android.intent.action.VIEW", Uri
								 .parse(update_addr));
								 context.startActivity(uppdate);
//								if (android.os.Build.VERSION.SDK_INT < 12) {
//									Intent intent = new Intent(
//											context,
//											org.aisin.sipphone.DwonNewApps.class);
//									intent.putExtra("durl", update_addr);
//									context.startActivity(intent);
//								} else {
//									Intent service = new Intent(
//											context,
//											org.aisin.sipphone.DwonNewAppService.class);
//									service.putExtra("ver", ver);
//									service.putExtra("update_addr", update_addr);
//									service.putExtra("update_tips", update_tips);
//									context.startService(service);
//								}
							}
						});
				mybuild.dialogShow();
			}
		} catch (Exception e) {
		}
	}
}
