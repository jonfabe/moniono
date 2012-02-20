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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONTokener;
import org.moniono.R;
import org.moniono.util.LogTags;

import android.content.Context;
import android.util.Log;

public abstract class DataManager {
	
	protected Context ctx;
	
	protected DataManager(Context iniCtx) {
		this.ctx = iniCtx;
	}
	
	protected InputStream getHttpStream(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = null;
		try {
			Log.v(LogTags.NETWORK.toString(), "Requesting URL: "+url);
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			Log.w(LogTags.NETWORK.toString(), "Error during network access.",e);
		} catch (IOException e) {
			Log.w(LogTags.NETWORK.toString(), "Error during network access.",e);
		}
		if (response != null) {
			try {
				return response.getEntity().getContent();
			} catch (IllegalStateException e) {
				Log.w(LogTags.NETWORK.toString(), "Error during processing results from network access.",e);
			} catch (IOException e) {
				Log.w(LogTags.NETWORK.toString(), "Error during processing results from network access.",e);
			}
		}
		return null;
	}

	protected BufferedReader getRawReader(InputStream in) {
		if(in == null){
			Log.w(LogTags.STREAM_PROCESSING.toString(), "No input stream provided: Unable too create reader.");
			return null;
		}
		return new BufferedReader(new InputStreamReader(in));
	}

	protected BufferedReader getZipReader(InputStream in) {
		ZipInputStream zin = new ZipInputStream(in);
		try {
			ZipEntry nextze = zin.getNextEntry();
			Log.i("SOME", nextze.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedReader(new InputStreamReader(zin));
	}

	protected JSONTokener getJson(String url) {
		InputStream in = this.getHttpStream(url);
		if(in == null){
			Log.w(LogTags.NETWORK.toString(), "No results from network access.");
			return null;
		}
		BufferedReader reader = this.getRawReader(in);
		StringBuffer jsonBuffer = new StringBuffer();
		String nextString = null;
		try {
			nextString = reader.readLine();
		} catch (IOException e) {
			Log.w(LogTags.STREAM_PROCESSING.toString(), "Error during result processing from network access.");
		}
		while (nextString != null) {
			jsonBuffer.append(nextString);
			try {
				nextString = reader.readLine();
			} catch (IOException e) {
				Log.w(LogTags.STREAM_PROCESSING.toString(), "Error during result processing from network access.");
			}
		}
		return new JSONTokener(jsonBuffer.toString());
	}
	
	public static GregorianCalendar fromString(String calString){
		String[] timestampElements = calString.split(" ");
		String dateString = timestampElements[0];
		String timeString = timestampElements[1];
		String[] dateElements = dateString.split("-");
		String[] timeElements = timeString.split(":");
		TimeZone myTimeZone = TimeZone.getDefault();
		int offset = myTimeZone.getRawOffset();
		GregorianCalendar result = new GregorianCalendar();
		result.set(Calendar.YEAR, Integer.parseInt(dateElements[0]));
		result.set(Calendar.MONTH, Integer.parseInt(dateElements[1]) - 1);
		result.set(Calendar.DAY_OF_MONTH,
				Integer.parseInt(dateElements[2]));
		result.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timeElements[0]));
		result.set(Calendar.MINUTE, Integer.parseInt(timeElements[1]));
		result.set(Calendar.SECOND, Integer.parseInt(timeElements[2]));
		result.add(Calendar.MILLISECOND, offset);
		return result;
	}

}
