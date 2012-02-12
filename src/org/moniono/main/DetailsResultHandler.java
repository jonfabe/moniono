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

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DetailsResultHandler extends Handler {
	
	private NodeDetailsFetchThread thread;
	private MonionoActivity activity;
	private ProgressDialog dialog;
	
	public DetailsResultHandler(MonionoActivity iniActivity,NodeDetailsFetchThread iniThread,ProgressDialog iniDialog){
		this.activity = iniActivity;
		this.thread = iniThread;
		this.dialog = iniDialog;
	}

	public void handleMessage(Message msg) {
		Log.i("Result handler","Handling result");
		activity.handleDataFetch(dialog, thread);
    }
	
}
