package org.aisin.sipphone.setts;

import org.aisin.sipphone.R;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowRedUserTextView extends Activity implements OnClickListener {
	private LinearLayout showtextview_linlayout;
	private ImageView setting_showtextview_back;
	private TextView setting_showtextview_text;
	private TextView showtextview_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String showkey = getIntent().getStringExtra("showkey");
		if (showkey == null || "".equals(showkey)) {
			finish();
			return;
		}
		setContentView(R.layout.showtextview);
		showtextview_linlayout = (LinearLayout) this
				.findViewById(R.id.showtextview_linlayout);
		setting_showtextview_back = (ImageView) this
				.findViewById(R.id.setting_showtextview_back);
		setting_showtextview_text = (TextView) this
				.findViewById(R.id.setting_showtextview_text);
		showtextview_text = (TextView) this
				.findViewById(R.id.showtextview_text);
		setting_showtextview_back.setOnClickListener(this);
		setting_showtextview_text.setText("玩法介绍");
		// showtextview_text.setText(show_text);
		Spanned spnd = null;
		if ("kouling".equals(showkey)) {
			spnd = Html.fromHtml("<h6>" + this.getString(R.string.ru_hbkl)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_hbkljs) + "</font>" + "<h6>"
					+ this.getString(R.string.ru_tocreatkltitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_tocreatkltext) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_tousercodetitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_tousercodetext) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_tousercodegztitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_tousercodegztext_1) + "<br>"
					+ this.getString(R.string.ru_tousercodegztext_2)
					+ "</font>");

		} else if ("geren".equals(showkey)) {
			spnd = Html.fromHtml("<h6>"
					+ this.getString(R.string.ru_grusertitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_grusertext) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_gruserfhbtitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_gruserfhbtext_1) + "<br>"
					+ this.getString(R.string.ru_gruserfhbtext_2) + "<br>"
					+ this.getString(R.string.ru_gruserfhbtext_3) + "<br>"
					+ this.getString(R.string.ru_gruserfhbtext_4) + "<br>"
					+ this.getString(R.string.ru_gruserfhbtext_5) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_gruserlqhbtitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_gruserlqhbtext) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_gruserthhbtitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_gruserthhbtext) + "</font>");
		} else if ("qun".equals(showkey)) {
			spnd = Html.fromHtml("<h6>"
					+ this.getString(R.string.ru_qunusertitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_qunusertxt) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_gruserfhbtitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_qunuserfhbtext_1) + "<br>"
					+ this.getString(R.string.ru_qunuserfhbtext_2) + "<br>"
					+ this.getString(R.string.ru_qunuserfhbtext_3) + "<br>"
					+ this.getString(R.string.ru_qunuserfhbtext_4) + "<br>"
					+ this.getString(R.string.ru_qunuserfhbtext_5) + "<br>"
					+ this.getString(R.string.ru_qunuserfhbtext_6) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_gruserlqhbtitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_qunuserlqhbtext) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_qunusertbtstitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_qunusertbtstext) + "</font>"
					+ "<h6>" + this.getString(R.string.ru_gruserthhbtitle)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.ru_qunuserthhbtext) + "</font>");
		} else if ("zensong".equals(showkey)) {
			spnd = Html
					.fromHtml("<h6>"
							+ this.getString(R.string.ru_zensongjiliredtitle)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.ru_zensongjiliredtext)
							+ "</font>" + "<h6>"
							+ this.getString(R.string.ru_zensongdqedtitle)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.ru_zensongdqedtext_1)
							+ "<br>"
							+ this.getString(R.string.ru_zensongdqedtext_2)
							+ "<br>"
							+ this.getString(R.string.ru_zensongdqedtext_3)
							+ "<br>"
							+ this.getString(R.string.ru_zensongdqedtext_4)
							+ "<br>"
							+ this.getString(R.string.ru_zensongdqedtext_5)
							+ "</font>");
		} else if ("bzzx".equals(showkey)) {
			setting_showtextview_text.setText("使用说明");
			spnd = Html.fromHtml("<h6>"
					+ this.getString(R.string.aboutaisin_bzzx1_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx1) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_bzzx2_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx2) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_bzzx3_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx3) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_bzzx4_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx4) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_bzzx5_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx5) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_bzzx6_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx6) + "</font>"
					/*+ "<h6>" + this.getString(R.string.aboutaisin_bzzx7_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_bzzx7_1) + "<br>"
					+ this.getString(R.string.aboutaisin_bzzx7_2) + "<br>"
					+ this.getString(R.string.aboutaisin_bzzx7_3) + "<br>"
					+ this.getString(R.string.aboutaisin_bzzx7_4) + "<br>"
					+ this.getString(R.string.aboutaisin_bzzx7_5) + "</font>"*/);
		} else if ("zfsm".equals(showkey)) {
			setting_showtextview_text.setText("资费说明");
			spnd = Html
					.fromHtml("<h6>"
							+ this.getString(R.string.aboutaisin_zfsm1_t)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.aboutaisin_zfsm1)
							+ "</font>"
							+ "<h6>"
							+ this.getString(R.string.aboutaisin_zfsm2_t)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.aboutaisin_zfsm2_1)
							+ SharedPreferencesTools
									.getSharedPreferences_msglist_date_share(
											this)
									.getString(
											SharedPreferencesTools.SPF_msglist_date_FEERATE,
											"")
							+ this.getString(R.string.aboutaisin_zfsm2_2)
							+ "</font>"
							+ "<h6>"
							+ this.getString(R.string.aboutaisin_zfsm3_t)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.aboutaisin_zfsm3)
							+ SharedPreferencesTools
									.getSharedPreferences_msglist_date_share(
											this)
									.getString(
											SharedPreferencesTools.SPF_msglist_date_direct_fee_rate,
											"") + "元/分钟" + "</font>" + "<h6>"
							+ this.getString(R.string.aboutaisin_zfsm4_t)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.aboutaisin_zfsm4)
							+ "</font>" + "<h6>"
							+ this.getString(R.string.aboutaisin_zfsm5_t)
							+ "</h6><font color=#808080>"
							+ this.getString(R.string.aboutaisin_zfsm5)
							+ "</font>");
		} else if ("czsm".equals(showkey)) {
			setting_showtextview_text.setText("充值说明");
			spnd = Html.fromHtml("<h6>"
					+ this.getString(R.string.aboutaisin_czsm1_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_czsm1) + "</font>"
					/*+ "<h6>" + this.getString(R.string.aboutaisin_czsm2_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_czsm2) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_czsm3_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_czsm3_1) + "<br>"
					+ this.getString(R.string.aboutaisin_czsm3_2) + "</font>"
					+ "<h6>" + this.getString(R.string.aboutaisin_czsm4_t)
					+ "</h6><font color=#808080>"
					+ this.getString(R.string.aboutaisin_czsm4_1) + "<br>"
					+ this.getString(R.string.aboutaisin_czsm4_2) + "<br>"
					+ this.getString(R.string.aboutaisin_czsm4_3) + "</font>"*/);
		}
		if (spnd != null) {
			showtextview_text.setText(spnd);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setting_showtextview_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (showtextview_linlayout != null) {
			RecoveryTools.unbindDrawables(showtextview_linlayout);// 回收容
		}
	}

}
