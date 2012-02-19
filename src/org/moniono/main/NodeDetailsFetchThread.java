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

import org.moniono.search.BandwidthData;
import org.moniono.search.BandwidthDataManager;
import org.moniono.search.DetailsData;
import org.moniono.search.DetailsDataManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

public class NodeDetailsFetchThread implements Runnable {

	private Context ctx;
	private String hash;
	private String name;
	private boolean relay;
	private Handler handler = null;
	private int code = -1;
	private DetailsData data = null;

	NodeDetailsFetchThread(MonionoActivity iniCtx, String iniHash,
			String iniName, boolean iniIsRelay, int iniCode,
			ProgressDialog dialog) {
		this.ctx = iniCtx;
		this.hash = iniHash;
		this.name = iniName;
		this.relay = iniIsRelay;
		this.handler = new DetailsResultHandler(iniCtx, this, dialog);
		this.code = iniCode;
	}

	public void run() {
		BandwidthData bdata = BandwidthDataManager.getInstance(this.ctx).getData(hash);
		this.data = DetailsDataManager.getInstance(this.ctx).getData(
				this.name, this.hash,bdata);
		this.handler.sendEmptyMessage(0);
	}

	public int getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public String getHash() {
		return this.hash;
	}

	public boolean isRelay() {
		return this.relay;
	}

	public DetailsData getData() {
		return this.data;
	}

}
