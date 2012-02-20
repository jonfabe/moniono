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

package org.moniono.main;

import static org.moniono.search.SearchThread.CURSOR_FIELD_FAVICON;
import static org.moniono.search.SearchThread.CURSOR_FIELD_ONLINE;
import static org.moniono.util.CommonExtraId.IS_RELAY;
import static org.moniono.util.CommonExtraId.NODE_DATA;
import static org.moniono.util.CommonExtraId.NODE_HASH;
import static org.moniono.util.CommonExtraId.NODE_NAME;

import org.moniono.R;
import org.moniono.db.DbHelper;
import org.moniono.db.NodeType;
import org.moniono.db.NodesDbAdapter;
import org.moniono.details.AboutActivity;
import org.moniono.details.HelpActivity;
import org.moniono.details.LicenseActivity;
import org.moniono.details.NodeDetailsOverviewActivity;
import org.moniono.details.NodeDetailsTabsActivity;
import org.moniono.search.DetailsData;
import org.moniono.search.NodeFlag;
import org.moniono.search.NodesDataManager;
import org.moniono.search.NodesDataRefreshThread;
import org.moniono.search.SearchThread;
import org.moniono.util.LogTags;
import org.moniono.view.binder.FavoriteListViewBinder;
import org.moniono.view.binder.NodeListViewBinder;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MonionoActivity extends ListActivity {

	private NodesDbAdapter dBase = null;

	public static final int ACTIVITY_NODE_SEARCH = 2;
	public static final int ACTIVITY_NODE_DETAILS = 3;
	public static final int ACTIVITY_NODE_VIEW = 4;

	public static final int SEARCH_ID = Menu.FIRST;
	public static final int FAVORITES_ID = SEARCH_ID + 1;

	private ListView mResultsList;
	private TextView mSearchLabel;
	private EditText mSearchString;
	private ImageView mSearchButton;

	private ImageView mRelayMenuEntry;
	private ImageView mBridgeMenuEntry;

	private TextView searchStringLabel;

	private boolean relaysActive = true;
	private boolean bridgesActive = true;

	private String currentSearchString = null;
	private SearchThread st = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nodes_list);
		this.dBase = new NodesDbAdapter(this);
		Intent intent = getIntent();
		this.mResultsList = (ListView) findViewById(android.R.id.list);
		this.mSearchLabel = (TextView) findViewById(R.id.search_label);
		this.mSearchString = (EditText) findViewById(R.id.search_input);
		this.mSearchButton = (ImageView) findViewById(R.id.ic_menu_search);
		this.searchStringLabel = (TextView) findViewById(R.id.search_string_label);
		Log.i("CREATION", this.mResultsList + " " + this.mSearchLabel + " "
				+ this.mSearchString + " " + this.mSearchButton);
		SearchButtonClickListener sbcl = new SearchButtonClickListener(this);
		this.mSearchButton.setOnClickListener(sbcl);

		ImageView searchImage = (ImageView) findViewById(R.id.search_icon_2);
		SearchSwitchClickListener sscl = new SearchSwitchClickListener(this);
		searchImage.setOnClickListener(sscl);
		ImageView favoriteImage = (ImageView) findViewById(R.id.bookmark_icon_2);
		FavoritesClickListener fcl = new FavoritesClickListener(this);
		favoriteImage.setOnClickListener(fcl);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			search(query);
		} else {
			this.fillList();
		}
		registerForContextMenu(getListView());
		this.mBridgeMenuEntry = (ImageView) findViewById(R.id.bridge_icon_2);
		BridgesClickListener bcl = new BridgesClickListener(this);
		this.mBridgeMenuEntry.setOnClickListener(bcl);
		this.mRelayMenuEntry = (ImageView) findViewById(R.id.relay_icon_2);
		RelaysClickListener rcl = new RelaysClickListener(this);
		this.mRelayMenuEntry.setOnClickListener(rcl);
		if(!NodesDataManager.hasManager()){
			new NodesDataRefreshThread(this).start();
		}
	}

	private void switchRelayMenuEntryWithoutRefresh() {
		if (this.relaysActive) {
			this.mRelayMenuEntry.setImageResource(R.drawable.relay_inactive);
			if (!this.bridgesActive) {
				this.switchBridgeMenuEntryWithouRefresh();
			}
		} else {
			this.mRelayMenuEntry.setImageResource(R.drawable.relay);
		}
		this.relaysActive = !this.relaysActive;
	}

	void switchRelayMenuEntry() {
		switchRelayMenuEntryWithoutRefresh();
		if (this.currentSearchString != null) {
			this.search(this.currentSearchString);
		} else {
			this.fillList();
		}
	}

	private void switchBridgeMenuEntryWithouRefresh() {
		if (this.bridgesActive) {
			this.mBridgeMenuEntry.setImageResource(R.drawable.bridge_inactive);
			if (!this.relaysActive) {
				this.switchRelayMenuEntryWithoutRefresh();
			}
		} else {
			this.mBridgeMenuEntry.setImageResource(R.drawable.bridge);
		}
		this.bridgesActive = !this.bridgesActive;
	}

	void switchBridgeMenuEntry() {
		switchBridgeMenuEntryWithouRefresh();
		if (this.currentSearchString != null) {
			this.search(this.currentSearchString);
		} else {
			this.fillList();
		}
	}

	void switchSearch() {
		if (this.mSearchLabel.getVisibility() == View.GONE) {
			this.mSearchLabel.setVisibility(View.VISIBLE);
			this.mSearchString.setVisibility(View.VISIBLE);
			this.mSearchButton.setVisibility(View.VISIBLE);
		} else {
			this.mSearchLabel.setVisibility(View.GONE);
			this.mSearchString.setVisibility(View.GONE);
			this.mSearchButton.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(
			@SuppressWarnings("unused") int requestCode,
			@SuppressWarnings("unused") int resultCode,
			@SuppressWarnings("unused") Intent intent) {
		if (this.currentSearchString == null
				|| this.currentSearchString.equals("")) {
			fillList();
		} else {
			search(this.currentSearchString);
		}
	}

	void fillList() {
		this.searchStringLabel.setVisibility(View.GONE);
		this.dBase.open();
		Cursor c = null;
		if (this.bridgesActive && this.relaysActive) {
			c = this.dBase.fetchAllNodes();
		} else if (this.bridgesActive) {
			c = this.dBase.fetchAllOfType(NodeType.BRIDGE.getIdentifier());
		} else {
			c = this.dBase.fetchAllOfType(NodeType.RELAY.getIdentifier());
		}
		startManagingCursor(c);

		String[] from = new String[] { DbHelper.NODES_KEY_NAME,
				DbHelper.NODES_KEY_FINGERPRINT_HASH, DbHelper.NODES_KEY_TYPE };
		int[] to = new int[] { R.id.main_list_name, R.id.main_list_fingerprint,
				R.id.node_type_icon };

		SimpleCursorAdapter nodes = new SimpleCursorAdapter(this,
				R.layout.nodes_row, c, from, to);
		nodes.setViewBinder(new FavoriteListViewBinder());
		setListAdapter(nodes);
		this.dBase.close();
		ListView mainList = (ListView) findViewById(android.R.id.list);
		OnItemClickListener listener = new OnNodeClickListener(this,
				this.dBase, ACTIVITY_NODE_VIEW);
		mainList.setOnItemClickListener(listener);
		this.currentSearchString = null;
	}

	void search(String searchString) {
		this.searchStringLabel.setVisibility(View.GONE);
		if (this.currentSearchString == null
				|| !searchString.equals(this.currentSearchString)) {
			Log.i(LogTags.SEARCH.toString(), "Searching for " + searchString);
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getResources().getString(R.string.dialog_loading));
			dialog.setIndeterminate(true);
			dialog.show();
			SearchThread t = new SearchThread(this, this.dBase, searchString,
					this.bridgesActive, this.relaysActive, dialog);
			this.st = t;
			new Thread(t).start();
		} else {
			processResults();
		}
	}

	public void setDialogMessage(int ressouceId, ProgressDialog dialog) {
		dialog.setMessage(getResources().getString(ressouceId));
	}

	public void handleSearchResults(ProgressDialog dialog) {
		Log.i(LogTags.SEARCH.toString(), "Dismissing dialog.");
		dialog.dismiss();
		Log.i(LogTags.DIALOG.toString(), "Dialog dismissed.");
		this.processResults();
	}

	public void openNodeView(long id, int code) {

		String name = null;
		String hash = null;
		boolean isRelay = false;
		switch (code) {
		case ACTIVITY_NODE_VIEW:

			this.dBase.open();
			Cursor c = this.dBase.fetchNode(id);
			name = c.getString(c.getColumnIndexOrThrow(DbHelper.NODES_KEY_NAME));
			hash = c.getString(c
					.getColumnIndexOrThrow(DbHelper.NODES_KEY_FINGERPRINT_HASH));
			isRelay = NodeType.RELAY.getIdentifier() == c.getInt(c
					.getColumnIndexOrThrow(DbHelper.NODES_KEY_TYPE));
			this.dBase.close();
			break;
		case ACTIVITY_NODE_DETAILS:
			NodesDataManager man = NodesDataManager.getInstance(this);
			int identifier = new Long(id).intValue();
			isRelay = man.isRelay(identifier);
			if (isRelay) {
				name = man.getRelayName(identifier);
			}
			hash = man.getFingerprint(identifier);
		}

		ProgressDialog progDialog = ProgressDialog.show(MonionoActivity.this,
				"", getResources().getString(R.string.dialog_loading_details),
				true);

		new Thread(new NodeDetailsFetchThread(this, hash, name, isRelay, code,
				progDialog)).start();
	}

	public void handleDataFetch(ProgressDialog dialog,
			NodeDetailsFetchThread thread) {
		DetailsData data = thread.getData();
		NodeFlag[] flags = data.getFlags();
		String[] policy = data.getExitPolicy();
		Intent i = null;
		if ((flags == null || flags.length == 0)
				&& (policy == null || policy.length == 0)) {
			i = new Intent(this, NodeDetailsOverviewActivity.class);
		} else {
			i = new Intent(this, NodeDetailsTabsActivity.class);
		}
		i.putExtra(NODE_NAME.toString(), thread.getName());
		i.putExtra(NODE_HASH.toString(), thread.getHash());
		i.putExtra(IS_RELAY.toString(), thread.isRelay());
		i.putExtra(NODE_DATA.toString(), data);
		Log.i("Activity", "Dismissing dialog");
		dialog.dismiss();
		startActivityForResult(i, thread.getCode());
	}

	public void processResults() {
		Log.i(LogTags.ACTIVITY.toString(), "Processing result.");
		Cursor c = this.st.getResultingCursor(this.bridgesActive,
				this.relaysActive);
		startManagingCursor(c);

		String[] from = new String[] { CURSOR_FIELD_ONLINE, "name",
				"fingerprint", CURSOR_FIELD_FAVICON };
		int[] to = new int[] { R.id.node_type_icon, R.id.main_list_name,
				R.id.main_list_fingerprint, R.id.bookmark_icon };
		SimpleCursorAdapter relayResults = new SimpleCursorAdapter(this,
				R.layout.nodes_row, c, from, to);

		relayResults.setViewBinder(new NodeListViewBinder(this.st
				.getFavorites(), this, this.st));
		this.mResultsList.setAdapter(relayResults);
		this.mResultsList.setTextFilterEnabled(true);
		this.mResultsList
				.setOnItemClickListener(new OnNodeClickListener(this,
						new NodesDbAdapter(this),
						MonionoActivity.ACTIVITY_NODE_DETAILS));
		this.currentSearchString = this.st.getSearchString();

		this.searchStringLabel.setText(this.getResources().getString(
				R.string.search_string)
				+ " '" + this.currentSearchString + "'");
		this.searchStringLabel.setVisibility(View.VISIBLE);
	}

	EditText getMSearchString() {
		return this.mSearchString;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		MenuItem licenseMenuItem = menu.add(Menu.NONE, 0, 0, "License");
		licenseMenuItem.setIcon(R.drawable.license);
		licenseMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(
					@SuppressWarnings("unused") MenuItem item) {
				startActivity(new Intent(MonionoActivity.this,
						LicenseActivity.class));
				return true;
			}
		});
		MenuItem infoMenuItem = menu.add(Menu.NONE, 1, 1, "Information");
		infoMenuItem.setIcon(R.drawable.information);
		infoMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(
					@SuppressWarnings("unused") MenuItem item) {
				startActivity(new Intent(MonionoActivity.this,
						AboutActivity.class));
				return true;
			}
		});
		MenuItem helpMenuItem = menu.add(Menu.NONE, 2, 2, "Help");
		helpMenuItem.setIcon(R.drawable.questionmark);
		helpMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(
					@SuppressWarnings("unused") MenuItem item) {
				startActivity(new Intent(MonionoActivity.this,
						HelpActivity.class));
				return true;
			}
		});
		return result;
	}

}