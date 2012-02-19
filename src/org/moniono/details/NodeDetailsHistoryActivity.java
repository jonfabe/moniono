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

import static org.moniono.util.CommonExtraId.NODE_HASH;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.moniono.R;
import org.moniono.search.BandwidthData;
import org.moniono.search.BandwidthDataManager;
import org.moniono.search.BandwidthIntervalTimespan;
import org.moniono.util.LogTags;

import android.app.Activity;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

/**
 * This class implements the control of the history tab as part of the node
 * details activity ({@link NodeDetailsTabsActivity}). Its implementation is
 * responsible to initially setup view elements w.r.t. the establishment of
 * connections to data elements.
 * 
 * As foundation for successful application, the node's (hashed) fingerprint
 * must be set at {@link org.moniono.util.CommonExtraId#NODE_HASH} within intent
 * extras on invocation of {@link #onCreate(Bundle)}.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class NodeDetailsHistoryActivity extends Activity {

	/**
	 * Constant being used for any message if an invalid index for the chart to
	 * display is submitted.
	 */
	private static final String CONTRACT_VIOLATION_MSG_INVALID_CHART_INDEX = "Contract violation: Invalid index for chart to display submitted!";

	/**
	 * List of charts which is used as foundation for switching charts.
	 */
	private List<Bitmap> chartsList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Comment body.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		Bundle extras = getIntent().getExtras();

		String hash = extras.getString(NODE_HASH.toString());

		BandwidthData bData = BandwidthDataManager.getInstance(this).getData(
				hash);
		Set<BandwidthIntervalTimespan> timespans = bData.getTimespans();

		MatrixCursor cursor = new MatrixCursor(new String[] { "_id", "entry" },
				timespans.size());
		this.chartsList = new ArrayList<Bitmap>(timespans.size());
		int i = 0;
		for (BandwidthIntervalTimespan next : timespans) {
			int quantity = next.getQuantity();
			String intervalString = quantity + " ";
			if (quantity == 1) {
				intervalString += getResources().getString(
						next.getType().getSingularKey());
			} else {
				intervalString += getResources().getString(
						next.getType().getPluralKey());
			}
			byte[] imageByteArray = bData.getChart(next);
			this.chartsList.add(BitmapFactory.decodeByteArray(imageByteArray,
					0, imageByteArray.length));
			cursor.addRow(new String[] { new Integer(i).toString(),
					intervalString });
		}
		startManagingCursor(cursor);

		String[] from = new String[] { "entry" };
		int[] to = new int[] { android.R.id.text1 };
		SimpleCursorAdapter relayResults = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor, from, to);
		relayResults.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) findViewById(R.id.historySpinner);
		spinner.setAdapter(relayResults);
		if (this.chartsList.size() > 0) {
			this.setChartAt(0);
		}
		spinner.setOnItemSelectedListener(new HistoryChartSelectListener(this));
	}

	/**
	 * This method can be used to switch the displayed chart.
	 * 
	 * @param pos
	 *            Position of chart to display in {@link #chartsList}. An
	 *            {@link IllegalArgumentException} is thrown if an invalid value
	 *            is submitted.
	 */
	public void setChartAt(int pos) {
		if (this.chartsList == null || pos >= this.chartsList.size() || pos < 0) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_INVALID_CHART_INDEX);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_INVALID_CHART_INDEX);
		}
		ImageView iView = (ImageView) findViewById(R.id.history_chart);
		iView.setImageBitmap(this.chartsList.get(pos));
	}

}
