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

package org.moniono.util;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moniono.search.DataManager;

public final class JSONUtil {
	
	private JSONUtil(){
		/* Prevent instantiation */
	}
	
	public static final String getString(String key, String defaultValue,
			JSONObject json) throws JSONException {
		String result = defaultValue;
		if (json.has(key)) {
			result = json.getString(key);
		}
		return result;
	}
	
	public static final double getDouble(String key, double defaultValue,
			JSONObject json) throws JSONException {
		double result = defaultValue;
		if (json.has(key)) {
			result = json.getDouble(key);
		}
		return result;
	}

	public static final String[] getStringArray(String key,
			String[] defaultValue, JSONObject json) throws JSONException {
		String[] result = defaultValue;
		if (json.has(key)) {
			JSONArray jsonArray = json.getJSONArray(key);
			result = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				result[i] = jsonArray.getString(i);
			}
		}
		return result;
	}
	
	public static final Calendar getCalendar(String key,
			Calendar defaultValue, JSONObject json) throws JSONException {
		Calendar result = defaultValue;
		if (json.has(key)) {
			result = DataManager.fromString(json.getString(key));
		}
		return result;
	}

	public static final boolean getBoolean(String key, boolean defaultValue,
			JSONObject json) throws JSONException {
		boolean result = defaultValue;
		if (json.has(key)) {
			result = json.getBoolean(key);
		}
		return result;
	}
	
	public static final long getLong(String key, long defaultValue,
			JSONObject json) throws JSONException {
		long result = defaultValue;
		if (json.has(key)) {
			result = json.getLong(key);
		}
		return result;
	}
	
	public static final int getInteger(String key, int defaultValue,
			JSONObject json) throws JSONException {
		int result = defaultValue;
		if (json.has(key)) {
			result = json.getInt(key);
		}
		return result;
	}

}
