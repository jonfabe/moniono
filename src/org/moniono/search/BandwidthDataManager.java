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

import static org.moniono.util.CommonConstants.NODE_BANDWIDTH_BASE_URL;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.moniono.util.JSONUtil;
import org.moniono.util.LogTags;

import android.content.Context;
import android.util.Log;

public class BandwidthDataManager extends DataManager {

	private static final String RESULT_RELAYS = "relays";
	private static final String RESULT_BRIDGES = "bridges";
	private static final String RELAYS_PUBLISHED_KEY = "relays_published";
	private static final String BRIDGES_PUBLISHED_KEY = "bridges_published";
	private static final int MAX_CACHE_SIZE = 100;

	private Map<String, BandwidthData> knownDetails = Collections
			.synchronizedMap(new CachingHashMap<String, BandwidthData>(
					MAX_CACHE_SIZE));

	private static BandwidthDataManager managerSingleton;

	public synchronized static final BandwidthDataManager getInstance(
			Context iniCtx) {
		if (managerSingleton == null) {
			managerSingleton = new BandwidthDataManager(iniCtx);
		}
		return managerSingleton;
	}

	private BandwidthDataManager(Context iniCtx) {
		super(iniCtx);
	}

	public BandwidthData getData(String hash) {
		BandwidthData result = this.knownDetails.get(hash);
		if (result == null || !result.isValid()) {
			JSONTokener tokener = getJson(NODE_BANDWIDTH_BASE_URL + hash.toUpperCase());
			JSONObject json = null;
			try {
				json = (JSONObject) tokener.nextValue();
				Calendar validUntil = new GregorianCalendar();
				Log.v(LogTags.JSON_PROCESSING.toString(),"Fresh until: "+validUntil);
				JSONObject jsonNode = null;
				if (json.has(RESULT_RELAYS)) {
					JSONArray relayResults = json.getJSONArray(RESULT_RELAYS);
					if (relayResults.length() > 0) {
						jsonNode = relayResults.getJSONObject(0);
						validUntil = JSONUtil.getCalendar(RELAYS_PUBLISHED_KEY,
								new GregorianCalendar(), json);
						validUntil.add(Calendar.HOUR_OF_DAY, 2);
					}
				}
				if (jsonNode == null && json.has(RESULT_BRIDGES)) {
					Log.w(LogTags.HISTORY.toString(), "Bridges results given");
					JSONArray bridgeResults = json.getJSONArray(RESULT_BRIDGES);
					if (bridgeResults.length() > 0) {
						jsonNode = bridgeResults.getJSONObject(0);
						validUntil = JSONUtil.getCalendar(BRIDGES_PUBLISHED_KEY,
								new GregorianCalendar(), json);
						validUntil.add(Calendar.HOUR_OF_DAY, 2);
					}else{
						Log.w(LogTags.HISTORY.toString(), "Empty bridges result set.");
					}
				}
				if (jsonNode != null) {
					Log.v("hash", hash);
					result = new BandwidthData(jsonNode, hash, validUntil);
					this.knownDetails.put(hash, result);
				}else{
					Log.w(LogTags.HISTORY.toString(), "No element for history found with key: "+hash);
				}
			} catch (JSONException e) {
				Log.w(LogTags.JSON_PROCESSING.toString(),"Processing details data.",e);
				return null;
			}
		}
		return result;
	}

	private JSONObject getBaseObject(String hashValue) {
		JSONTokener tokener = getJson(NODE_BANDWIDTH_BASE_URL + hashValue.toUpperCase());
		JSONObject json = null;
		try {
			json = (JSONObject) tokener.nextValue();
		} catch (JSONException e) {
			Log.e(LogTags.JSON_PROCESSING.toString(), "Error processingd bandwidth data.",e);
			return null;
		}
		try {
			JSONArray relayResults = json.getJSONArray(RESULT_RELAYS);
			if (relayResults.length() > 0) {
				return relayResults.getJSONObject(0);
			}
			JSONArray bridgeResults = json.getJSONArray(RESULT_BRIDGES);
			if (bridgeResults.length() > 0) {
				return bridgeResults.getJSONObject(0);
			}
		} catch (JSONException e) {
			return null;
		}
		return null;
	}

}
