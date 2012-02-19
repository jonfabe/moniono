/*
 * mOnionO - The mobile Onion Observer
 * Copyright (C) 2012 Jens Bruhn (moniono@gmx.net)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.moniono.details;

import static org.moniono.util.CommonExtraId.IS_RELAY;
import static org.moniono.util.CommonExtraId.NODE_DATA;
import static org.moniono.util.CommonExtraId.NODE_HASH;

import java.util.Set;

import org.moniono.R;
import org.moniono.search.BandwidthData;
import org.moniono.search.BandwidthDataManager;
import org.moniono.search.BandwidthIntervalTimespan;
import org.moniono.search.DetailsData;
import org.moniono.search.NodeFlag;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class NodeDetailsTabsActivity extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relay_details_tabs);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost

		Bundle extras = getIntent().getExtras();

		DetailsData data = (DetailsData) extras.get(NODE_DATA.toString());

		// Create an Intent to launch an Activity for the tab (to be reused)
		Intent overviewIntent = new Intent().setClass(this,
				NodeDetailsOverviewActivity.class);

		overviewIntent.putExtra(NODE_DATA.toString(), data);
		overviewIntent.putExtra(IS_RELAY.toString(),
				extras.getBoolean(IS_RELAY.toString()));

		// Initialize a TabSpec for each tab and add it to the TabHost
		View commonIndicator = LayoutInflater.from(this).inflate(
				R.layout.moniono_tab_widget, getTabWidget(), false);
		TextView commonTitle = (TextView) commonIndicator
				.findViewById(R.id.title);
		commonTitle.setText(getResources().getString(R.string.common));
		ImageView commonIcon = (ImageView) commonIndicator
				.findViewById(R.id.icon);
		commonIcon.setImageResource(R.drawable.ic_tab_common);

		TabHost.TabSpec overviewSpec = tabHost.newTabSpec("common")
				.setIndicator(commonIndicator).setContent(overviewIntent);
		tabHost.addTab(overviewSpec);

		NodeFlag[] flags = data.getFlags();
		if (flags != null && flags.length > 0) {
			Intent flagsIntent = new Intent().setClass(this,
					NodeDetailsFlagsActivity.class);

			flagsIntent.putExtra(NODE_DATA.toString(), data);
			flagsIntent.putExtra(IS_RELAY.toString(),
					extras.getBoolean(IS_RELAY.toString()));

			// Initialize a TabSpec for each tab and add it to the TabHost
			View flagsIndicator = LayoutInflater.from(this).inflate(
					R.layout.moniono_tab_widget, getTabWidget(), false);
			TextView flagsTitle = (TextView) flagsIndicator
					.findViewById(R.id.title);
			flagsTitle.setText(getResources().getString(R.string.flags));
			ImageView flagsIcon = (ImageView) flagsIndicator
					.findViewById(R.id.icon);
			flagsIcon.setImageResource(R.drawable.ic_tab_flags);

			TabHost.TabSpec flagsSpec = tabHost.newTabSpec("flags")
					.setIndicator(flagsIndicator).setContent(flagsIntent);
			tabHost.addTab(flagsSpec);
		}

		String[] exitPolicy = data.getExitPolicy();
		if (exitPolicy != null && exitPolicy.length > 0) {
			Intent policyIntent = new Intent().setClass(this,
					NodeDetailsPolicyActivity.class);

			policyIntent.putExtra(NODE_DATA.toString(), data);
			policyIntent.putExtra(IS_RELAY.toString(),
					extras.getBoolean(IS_RELAY.toString()));

			// Initialize a TabSpec for each tab and add it to the TabHost
			View policyIndicator = LayoutInflater.from(this).inflate(
					R.layout.moniono_tab_widget, getTabWidget(), false);
			TextView policyTitle = (TextView) policyIndicator
					.findViewById(R.id.title);
			policyTitle.setText(getResources().getString(R.string.policy));
			ImageView policyIcon = (ImageView) policyIndicator
					.findViewById(R.id.icon);
			policyIcon.setImageResource(R.drawable.ic_tab_policy);

			TabHost.TabSpec policySpec = tabHost.newTabSpec("policy")
					.setIndicator(policyIndicator).setContent(policyIntent);
			tabHost.addTab(policySpec);
		}

		BandwidthDataManager bManager = BandwidthDataManager.getInstance(this);
		BandwidthData bData = bManager.getData(data.getFingerprint());
		Set<BandwidthIntervalTimespan> intervals = bData.getTimespans();
		if (intervals != null && intervals.size() > 0) {

			Intent historyIntent = new Intent().setClass(this,
					NodeDetailsHistoryActivity.class);
			historyIntent.putExtra(NODE_HASH.toString(), data.getFingerprint());
			historyIntent.putExtra(NODE_DATA.toString(), data);
			historyIntent.putExtra(IS_RELAY.toString(),
					extras.getBoolean(IS_RELAY.toString()));

			// Initialize a TabSpec for each tab and add it to the TabHost
			View historyIndicator = LayoutInflater.from(this).inflate(
					R.layout.moniono_tab_widget, getTabWidget(), false);
			TextView historyTitle = (TextView) historyIndicator
					.findViewById(R.id.title);
			historyTitle.setText(getResources().getString(R.string.history));
			ImageView historyIcon = (ImageView) historyIndicator
					.findViewById(R.id.icon);
			historyIcon.setImageResource(R.drawable.ic_tab_history);

			TabHost.TabSpec policySpec = tabHost.newTabSpec("history")
					.setIndicator(historyIndicator).setContent(historyIntent);
			tabHost.addTab(policySpec);
		}

		tabHost.setCurrentTab(0);

		TabWidget tabWidget = getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			RelativeLayout tabLayout = (RelativeLayout) tabWidget.getChildAt(i);
			tabLayout.setBackgroundDrawable(res
					.getDrawable(R.drawable.tab_selector));
		}

	}

}
