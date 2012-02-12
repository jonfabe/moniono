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

import static org.moniono.util.CommonConstants.NODE_DETAILS_BASE_URL;

import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

public class DetailsDataManager extends DataManager {

	private static DetailsDataManager managerSingleton;

	private static final String RESULT_RELAYS = "relays";
	private static final String RESULT_BRIDGES = "bridges";

	private static final int MAX_CACHE_SIZE = 100;

	private Map<String, DetailsData> knownDetails = Collections
			.synchronizedMap(new CachingHashMap<String, DetailsData>(
					MAX_CACHE_SIZE));

	public synchronized static final DetailsDataManager getInstance(
			Context iniCtx) {
		if (managerSingleton == null) {
			managerSingleton = new DetailsDataManager(iniCtx);
		}
		return managerSingleton;
	}

	private DetailsDataManager(Context iniCtx) {
		super(iniCtx);
	}

	public DetailsData getData(String name, String hash) {
		DetailsData result = this.knownDetails.get(hash);
		if (result == null || !result.isValid()) {
			JSONObject json = this.getBaseObject(hash);
			if (json != null) {
				result = new DetailsData(json, name);
				this.knownDetails.put(hash, result);
			} else {
				result = new DetailsData(name, hash);
			}
		}
		result.setNickname(name);
		return result;
	}

	private JSONObject getBaseObject(String hashValue) {

		JSONTokener tokener = getJson(NODE_DETAILS_BASE_URL + hashValue.toUpperCase());
		try {
			JSONObject json = (JSONObject) tokener.nextValue();
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
