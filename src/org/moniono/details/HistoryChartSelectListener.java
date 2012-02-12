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

import org.moniono.util.LogTags;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * This class implements an {@link OnItemSelectedListener}. Its instances are
 * used to initiate the switch of bandwidth history charts within the history
 * view of a nodes.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class HistoryChartSelectListener implements OnItemSelectedListener {

	/**
	 * Constant being used for any message if a required activity reference is
	 * not specified during construction of class instance.
	 */
	private static final String CONTRACT_VIOLATION_MSG_ACTIVITY_MUST_BE_SET = "Contract violation: Reference to activity must be set on construction!";

	/**
	 * Reference to corresponding history activity.
	 */
	private NodeDetailsHistoryActivity activity;

	/**
	 * Constructor which expects an initial value for {@link #activity}. If the
	 * value is not set, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param iniActivity
	 *            Initial value for {@link #activity}.
	 */
	public HistoryChartSelectListener(NodeDetailsHistoryActivity iniActivity) {
		if (iniActivity == null) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_ACTIVITY_MUST_BE_SET);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_ACTIVITY_MUST_BE_SET);
		}
		this.activity = iniActivity;
	}

	/**
	 * {@inheritDoc} 
	 * {@link NodeDetailsHistoryActivity#setChartAt(int)} is
	 * invoked in order to switch bandwidth history chart.
	 */
	public void onItemSelected(
			@SuppressWarnings("unused") AdapterView<?> parent,
			@SuppressWarnings("unused") View view, int pos,
			@SuppressWarnings("unused") long id) {
		this.activity.setChartAt(pos);
	}

	/**
	 * {@inheritDoc} Nothing to do in method body.
	 */
	public void onNothingSelected(
			@SuppressWarnings("unused") AdapterView<?> view) {
		/* Nothing to do here. */
	}

}
