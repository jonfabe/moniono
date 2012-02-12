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

import org.moniono.db.DbHelper;
import org.moniono.db.NodeType;
import org.moniono.R;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * This binder is used to process favorite lists presented as part of the main
 * activity of the app. The binder sets the image in front of each list entry
 * depending on its type.
 * 
 * @author Jens Bruhn
 * @version 0.1
 */
public class FavoriteListViewBinder implements ViewBinder {

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		/*
		 * If current column is covering the value to indicate the node type and
		 * the corresponding view is an image view set the image in front of the
		 * row to bridge or relay depending on the node type.
		 */
		if (view instanceof ImageView
				&& columnIndex == cursor
						.getColumnIndex(DbHelper.NODES_KEY_TYPE)) {
			ImageView typeImage = (ImageView) view;
			int id = cursor.getInt(cursor
					.getColumnIndex(DbHelper.NODES_KEY_TYPE));
			switch (NodeType.fromInt(id)) {
			case RELAY:
				typeImage.setImageResource(R.drawable.relay);
				break;
			case BRIDGE:
				typeImage.setImageResource(R.drawable.bridge);
				break;
			default:
				throw new IllegalArgumentException("Unhandled node type.");
			}
			return true;
		}
		return false;
	}

}
