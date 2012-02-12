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

import org.moniono.R;
import org.moniono.search.DetailsData;
import org.moniono.search.NodeFlag;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

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

		TabHost.TabSpec overviewSpec = tabHost
				.newTabSpec("common")
				.setIndicator(getResources().getString(R.string.common),
						res.getDrawable(R.drawable.details))
				.setContent(overviewIntent);
		tabHost.addTab(overviewSpec);

		NodeFlag[] flags = data.getFlags();
		if (flags != null && flags.length > 0) {
			Intent flagsIntent = new Intent().setClass(this,
					NodeDetailsFlagsActivity.class);

			flagsIntent.putExtra(NODE_DATA.toString(), data);
			flagsIntent.putExtra(IS_RELAY.toString(),
					extras.getBoolean(IS_RELAY.toString()));

			// Initialize a TabSpec for each tab and add it to the TabHost
			TabHost.TabSpec flagsSpec = tabHost
					.newTabSpec("flags")
					.setIndicator(getResources().getString(R.string.flags),
							res.getDrawable(R.drawable.flags))
					.setContent(flagsIntent);
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
			TabHost.TabSpec policySpec = tabHost
					.newTabSpec("policy")
					.setIndicator(getResources().getString(R.string.policy),
							res.getDrawable(R.drawable.policy))
					.setContent(policyIntent);
			tabHost.addTab(policySpec);
		}

		Intent historyIntent = new Intent().setClass(this,
				NodeDetailsHistoryActivity.class);
		historyIntent.putExtra(NODE_HASH.toString(), data.getFingerprint());
		historyIntent.putExtra(IS_RELAY.toString(),
				extras.getBoolean(IS_RELAY.toString()));

		// Initialize a TabSpec for each tab and add it to the TabHost
		TabHost.TabSpec policySpec = tabHost
				.newTabSpec("history")
				.setIndicator(getResources().getString(R.string.history),
						res.getDrawable(R.drawable.history))
				.setContent(historyIntent);
		tabHost.addTab(policySpec);

		tabHost.setCurrentTab(0);

	}

}
