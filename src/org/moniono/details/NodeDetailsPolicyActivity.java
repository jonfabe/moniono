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

import static org.moniono.util.CommonExtraId.NODE_DATA;

import org.moniono.R;
import org.moniono.search.DetailsData;
import org.moniono.util.LogTags;

import android.app.ListActivity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class NodeDetailsPolicyActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relay_policy);
		Bundle extras = getIntent().getExtras();
		DetailsData relayData = (DetailsData) extras.get(NODE_DATA.toString());

		String[] policy = null;
		if (relayData != null) {
			policy = relayData.getExitPolicy();
		}
		if (policy == null) {
			policy = new String[0];
		}
		MatrixCursor cursor = new MatrixCursor(new String[] { "_id", "entry" },
				policy.length);
		Log.i(LogTags.VIEW.toString(), policy.length+" entries");
		for(int i = 0; i < policy.length;i++){
			cursor.addRow(new String[]{new Integer(i).toString(),policy[i]});
			Log.i(LogTags.VIEW.toString(),policy[i]+" added");
		}
		startManagingCursor(cursor);
		String[] from = new String[] { "entry" };
		int[] to = new int[] { R.id.policy_entry };
		SimpleCursorAdapter relayResults = new SimpleCursorAdapter(this,
				R.layout.relay_policy_row, cursor, from, to);
		setListAdapter(relayResults);
	}

}
