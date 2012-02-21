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

import org.moniono.util.LogTags;

import android.content.Context;
import android.util.Log;

/**
 *
 *
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class NodesDataRefreshThread extends Thread{
	
	private static final long START_DELAY = 30 * 1000;
	
	private Context ctx;
	private boolean startDelayed;
	
	public NodesDataRefreshThread(Context iniCtx, boolean delayed){
		this.ctx = iniCtx;
		this.startDelayed = delayed;
	}
	
	public void run() {
		if(this.startDelayed){
			try {
				Thread.currentThread().sleep(START_DELAY);
			} catch (InterruptedException e) {
				// Nothing to do
			}
		}
		Log.v(LogTags.DATA_REFRESH.toString(), "Refreshing search data.");
		NodesDataManager.refreshData(this.ctx);
	}

}
