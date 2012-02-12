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

package org.moniono.view.binder;

import static org.moniono.search.SearchThread.CURSOR_FIELD_FAVICON;
import static org.moniono.search.SearchThread.CURSOR_FIELD_FINGERPRINT;
import static org.moniono.search.SearchThread.CURSOR_FIELD_ONLINE;

import java.util.Set;

import org.moniono.search.SearchThread;
import org.moniono.R;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

/**
 * This binder is used to process node lists presented as part of the main
 * activity of the app on search results. In particular, the binder sets the
 * image in front of each list entry, sets the color of each entry depending in
 * the corresponding online state, and shows or hides the favorite icon behind
 * each node name depending on if a favorite is given or not.
 * 
 * 
 * @author Jens Bruhn
 * @version 0.1
 */
public class NodeListViewBinder implements ViewBinder {

	/**
	 * Set of hash values of all favorite nodes.
	 */
	private Set<String> favoriteHashes;
	/**
	 * Context of the view binder. It is required, e.g., to access resources.
	 */
	private Context ctx;
	/**
	 * Search thread which is responsible to fill the node list the binder is
	 * associated with.
	 */
	private SearchThread worker;

	/**
	 * Constructor of binder accepting initial values for all fields.
	 * 
	 * @param iniFavoriteHashes
	 *            Initial value for {@link #favoriteHashes}.
	 * @param iniCtx
	 *            Initial value for {@link #ctx}. Must not be null, otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param iniWorker
	 *            Initial value for {@link #worker}. Must not be null, otherwise
	 *            an {@link IllegalArgumentException} is thrown.
	 */
	public NodeListViewBinder(Set<String> iniFavoriteHashes, Context iniCtx,
			SearchThread iniWorker) {
		this.favoriteHashes = iniFavoriteHashes;
		if (iniCtx == null) {
			throw new IllegalArgumentException(
					"Context must be set on creation of "
							+ this.getClass().getName());
		}
		if (iniWorker == null) {
			throw new IllegalArgumentException(
					"Worker (Search thread) must be set on creation of "
							+ this.getClass().getName());
		}
		this.ctx = iniCtx;
		this.worker = iniWorker;
	}

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		int onlineColumnIndex = cursor.getColumnIndex(CURSOR_FIELD_ONLINE);
		if (view instanceof ImageView) {
			/*
			 * If current column is covering the value to indicate that a node
			 * is online or offline and the corresponding view is an image view
			 * set the image in front of the row to bridge or relay depending on
			 * the node type. Please note that this is some kind of workaround.
			 */
			if (columnIndex == onlineColumnIndex) {
				ImageView typeImage = (ImageView) view;
				int id = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex("_id")));
				if (this.worker.isRelay(id)) {
					typeImage.setImageResource(R.drawable.relay);
				} else {
					typeImage.setImageResource(R.drawable.bridge);
				}
				return true;
			} else if (columnIndex == cursor
					.getColumnIndex(CURSOR_FIELD_FAVICON)) {
				/*
				 * If current column is covering the value to indicate that a
				 * node is a favorite or not and the corresponding view is an
				 * image view set the visibility of the view to VISIBLE if the
				 * node is a favorite or to INVISIBLE of no favorite is given.
				 */
				ImageView img = (ImageView) view;
				String fingerprint = cursor.getString(cursor
						.getColumnIndex(CURSOR_FIELD_FINGERPRINT));
				if (this.favoriteHashes.contains(fingerprint)) {
					img.setVisibility(View.VISIBLE);
				} else {
					img.setVisibility(View.INVISIBLE);
				}
				return true;
			}
		}
		/*
		 * If the view is a text view set its color to online or offline,
		 * depending on the state of the represented node.
		 */
		if (view instanceof TextView) {
			TextView tView = (TextView) view;
			if (cursor.getString(onlineColumnIndex) != null) {
				tView.setTextColor(this.ctx.getResources().getColor(
						R.color.online));
			} else {
				tView.setTextColor(this.ctx.getResources().getColor(
						R.color.offline));
			}
		}
		return false;
	}

}
