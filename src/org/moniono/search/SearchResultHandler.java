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

import org.moniono.main.MonionoActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SearchResultHandler extends Handler {
	
	private SearchThread thread;
	private MonionoActivity activity;
	private ProgressDialog dialog;
	
	public SearchResultHandler(MonionoActivity iniActivity,SearchThread iniThread,ProgressDialog iniDialog){
		this.activity = iniActivity;
		this.thread = iniThread;
		this.dialog = iniDialog;
	}

	public void handleMessage(Message msg) {
		Log.i("Result handler","Handling result");
		Bundle b = msg.getData();
		if(b != null && b.containsKey("rid")){
			this.activity.setDialogMessage(b.getInt("rid"), this.dialog);
		}else{
			this.activity.handleSearchResults(this.dialog);
		}
    }
	
}
