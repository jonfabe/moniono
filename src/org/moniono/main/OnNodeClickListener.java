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

import org.moniono.db.NodesDbAdapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class OnNodeClickListener implements OnItemClickListener {

	private MonionoActivity ctx;
	private NodesDbAdapter dBase = null;
	private int code;

	OnNodeClickListener(MonionoActivity iniCtx, NodesDbAdapter iniDBase,
			int iniCode) {
		this.ctx = iniCtx;
		this.dBase = iniDBase;
		this.code = iniCode;
	}

	public void onItemClick(@SuppressWarnings("unused") AdapterView<?> parent,
			@SuppressWarnings("unused") View view,
			@SuppressWarnings("unused") int position,
			long id) {
		this.ctx.openNodeView(id,this.code);
	}
}
