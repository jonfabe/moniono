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

package org.moniono.search;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.moniono.db.NodesDbAdapter;
import org.moniono.main.MonionoActivity;
import org.moniono.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SearchThread implements Runnable {

	public static final String CURSOR_FIELD_ONLINE = "online";
	public static final String CURSOR_FIELD_FAVICON = "favicon";
	public static final String CURSOR_FIELD_FINGERPRINT = "fingerprint";

	private Context ctx;
	private NodesDbAdapter dBase = null;
	private Set<String> favorites = null;
	private String searchString;
	private MatrixCursor resultingBridgesCursor = null;
	private MatrixCursor resultingRelaysCursor = null;
	private MatrixCursor resultingCombinedCursor = null;
	private Handler handler = null;
	private NodesDataManager man = null;

	public SearchThread(MonionoActivity iniCtx, NodesDbAdapter iniDBase,
			String iniSearchString, boolean iniCondiderBridges,
			boolean iniConsiderRelays, ProgressDialog dialog) {
		this.ctx = iniCtx;
		this.dBase = iniDBase;
		this.searchString = iniSearchString;
		this.handler = new SearchResultHandler(iniCtx, this, dialog);
	}

	/**
	 * The original search execution.
	 */
	public void run() {
		this.dBase.open();
		Cursor favoritesCursor = this.dBase.fetchAllFingerprints();
		if (favoritesCursor.getCount() <= 0) {
			this.favorites = Collections.emptySet();
		} else {
			this.favorites = new HashSet<String>();
			favoritesCursor.moveToFirst();
			while (!favoritesCursor.isAfterLast()) {
				this.favorites.add(favoritesCursor.getString(0));
				favoritesCursor.moveToNext();
			}
		}
		this.dBase.close();

		this.man = NodesDataManager.getInstance(this.ctx);

		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt("rid", R.string.dialog_searching);
		msg.setData(b);
		this.handler.sendMessage(msg);

		List<String[]> results = this.man.search(
				this.searchString.toLowerCase(), this.favorites);

		this.resultingCombinedCursor = new MatrixCursor(new String[] { "_id",
				"name", CURSOR_FIELD_FINGERPRINT, CURSOR_FIELD_FAVICON,
				CURSOR_FIELD_ONLINE }, results.size());
		List<String[]> resultsRelays = new LinkedList<String[]>();
		List<String[]> resultsBridges = new LinkedList<String[]>();
		for (String[] result : results) {
			this.resultingCombinedCursor.addRow(result);
			int index = Integer.parseInt(result[0]);
			if (this.isRelay(index)) {
				resultsRelays.add(result);
			} else {
				resultsBridges.add(result);
			}
		}
		this.resultingRelaysCursor = new MatrixCursor(new String[] { "_id",
				"name", CURSOR_FIELD_FINGERPRINT, CURSOR_FIELD_FAVICON,
				CURSOR_FIELD_ONLINE }, resultsRelays.size());
		for (String[] result : resultsRelays) {
			this.resultingRelaysCursor.addRow(result);
		}
		this.resultingBridgesCursor = new MatrixCursor(new String[] { "_id",
				"name", CURSOR_FIELD_FINGERPRINT, CURSOR_FIELD_FAVICON,
				CURSOR_FIELD_ONLINE }, resultsBridges.size());
		for (String[] result : resultsBridges) {
			this.resultingBridgesCursor.addRow(result);
		}
		this.handler.sendEmptyMessage(0);
	}

	/**
	 * This method returns a set of strings representing hashes of favorites.
	 * 
	 * @return Hashes of favorites.
	 */
	public Set<String> getFavorites() {
		return this.favorites;
	}

	/**
	 * The method returns the search string which builds or will build the
	 * foundation of a search.
	 * 
	 * @return Search string building the foundation of search.
	 */
	public String getSearchString() {
		return this.searchString;
	}

	/**
	 * This method returns a cursor consisting of search results.
	 * 
	 * The method throws an {@link IllegalArgumentException} if both parameters
	 * are set to false.
	 * 
	 * @param bridges
	 *            true if bridges should be contained in search result cursor.
	 * @param relays
	 *            true if relays should be contained in search result cursor.
	 * @return Cursor consisting of search results.
	 */
	public MatrixCursor getResultingCursor(boolean bridges, boolean relays) {
		if (bridges && relays) {
			return this.resultingCombinedCursor;
		}
		if (bridges) {
			return this.resultingBridgesCursor;
		}
		if (relays) {
			return this.resultingRelaysCursor;
		}
		throw new IllegalArgumentException(
				"At least one parameter must be set to ture.");
	}

	/**
	 * This method provides information on if the node belonging to the
	 * submitted index is a bridge or a relay.
	 * 
	 * The method throws an {@link IllegalStateException} if no search was
	 * executed in advance and an {@link IndexOutOfBoundsException} if an index
	 * is submitted which is greater than or equal to the number of known nodes.
	 * 
	 * @param index
	 *            Index of the node.
	 * @return true if the node is a relay, false otherwise.
	 */
	public boolean isRelay(int index) {
		if (this.man == null) {
			throw new IllegalStateException(
					"Search must be finished before this method can be executed successfully.");
		}
		if (this.man.getNodeCount() <= index) {

			throw new IndexOutOfBoundsException(
					"Illegal index provided. Index must lower or equal to number of known nodes.");
		}
		return this.man.isRelay(index);
	}

}
