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
import org.moniono.search.NodeFlag;

import android.app.ListActivity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Instances of this class are used to control the flags view of node details.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class NodeDetailsFlagsActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* Switch to the flags layout. */
		setContentView(R.layout.relay_flags);

		/* Receive node details information from intent extras. */
		Bundle extras = getIntent().getExtras();
		DetailsData relayData = (DetailsData) extras.get(NODE_DATA.toString());
		NodeFlag[] flags = null;

		/*
		 * Request flags information from node details if there are node details
		 * provided as part of the intent extras.
		 */
		if (relayData != null) {
			flags = relayData.getFlags();
		}

		/*
		 * Set an empty flags array as flags data if no flag information could
		 * be determined.
		 */
		if (flags == null) {
			flags = new NodeFlag[0];
		}

		/*
		 * Construct a cursor for the flags ListView being the main part of the
		 * flags layout.
		 */
		MatrixCursor cursor = new MatrixCursor(new String[] { "_id", "flag" },
				flags.length);
		for (int i = 0; i < flags.length; i++) {
			cursor.addRow(new String[] { new Integer(i).toString(),
					flags[i].toString() });
		}
		startManagingCursor(cursor);

		/*
		 * Associate the cursor through an adapter with the ListView of the
		 * flags layout.
		 */
		String[] from = new String[] { "flag" };
		int[] to = new int[] { R.id.flag_entry };
		SimpleCursorAdapter flagResults = new SimpleCursorAdapter(this,
				R.layout.relay_flag_row, cursor, from, to);
		setListAdapter(flagResults);
	}

}
