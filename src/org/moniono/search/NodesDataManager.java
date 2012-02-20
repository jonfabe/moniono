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

import static org.moniono.util.CommonConstants.NODES_LIST_URL;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.moniono.util.LogTags;

import android.content.Context;
import android.util.Log;

public class NodesDataManager extends DataManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7095158355420917185L;
	
	private static int MIN_MINUTES_REFRESH_DELAY = 15;
	private static int MINUTES_PUBLICATION_REFRESH_DELAY = 60;

	private static NodesDataManager manager;
	
	private static Pattern IP_PATTERN = Pattern.compile("(\\.{0,1}[0-9]{1,3}\\.{0,1})+");
	private static Pattern HASH_PATTERN = Pattern.compile("[0-9a-f]+");
	private static Pattern NAME_PATTERN = Pattern.compile("[0-9a-z]+");

	private static final String RELAYS_PUBLISHED_KEY = "relays_published";
	private static final String BRIDGES_PUBLISHED_KEY = "bridges_published";
	private static final String RELAYS_JSON_KEY = "relays";
	private static final String RELAY_NICKNAME_JSON_KEY = "n";
	private static final String RELAY_FINGERPRINT_JSON_KEY = "f";
	private static final String RELAY_ADDRESSES_JSON_KEY = "a";
	private static final String RELAY_RUNING_JSON_KEY = "r";
	private static final String BRIDGES_JSON_KEY = "bridges";
	private static final String BRIDGE_FINGERPRINT_JSON_KEY = "h";
	private static final String BRIDGE_RUNING_JSON_KEY = "r";
	public static final String EMPTY_STRING = "";

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private Calendar refreshTime;
	private String[] relayNames;
	private String[] relayLowerNames;
	private String[] relayFingerprintHashes;
	private String[] relayLowerFingerprintHashes;
	private boolean[] runningRelays;
	private String[][] relayAddresses;

	private String[] bridgeFingerprintHashes;
	private String[] bridgeLowerFingerprintHashes;
	private boolean[] runningBridges;

	public synchronized static void refreshData(Context iniCtx) {
		NodesDataManager newManager = new NodesDataManager(iniCtx);
		manager = newManager;
		Log.i(LogTags.JSON_PROCESSING.toString(), "Data refreshed");
	}

	private NodesDataManager(Context iniCtx) {
		super(iniCtx);
		this.fillSearchDatabase();
	}

	public synchronized static NodesDataManager getInstance(Context iniCtx) {
		if (manager == null) {
			manager = new NodesDataManager(iniCtx);
		}else{
			Calendar now = new GregorianCalendar();
			Log.v(LogTags.TEMP.toString(), "Now "+now.getTime().toString()+" => "+manager.refreshTime.getTime().toString()+" => "+manager.refreshTime.compareTo(now));
			if(manager.refreshTime.compareTo(now) < 0){
				new NodesDataRefreshThread(iniCtx).start();
			}
		}
		return manager;
	}
	
	public synchronized static boolean hasManager(){
		return manager != null;
	}

	private void fillSearchDatabase() {
		JSONTokener tokener = this.getJson(NODES_LIST_URL);
		try {
			JSONObject json = (JSONObject) tokener.nextValue();

			JSONArray relays = json.getJSONArray(RELAYS_JSON_KEY);
			long start = System.currentTimeMillis();
			if (relays != null && relays.length() > 0) {
				this.relayNames = new String[relays.length()];
				this.relayLowerNames = new String[relays.length()];
				this.relayFingerprintHashes = new String[relays.length()];
				this.relayLowerFingerprintHashes = new String[relays.length()];
				this.relayAddresses = new String[relays.length()][];// [];
				this.runningRelays = new boolean[relays.length()];
				for (int i = 0; i < relays.length(); i++) {
					JSONObject relay = relays.getJSONObject(i);
					String nextName = "";

					if (relay.has(RELAY_NICKNAME_JSON_KEY)) {
						nextName = relay.getString(RELAY_NICKNAME_JSON_KEY);
					}
					this.relayNames[i] = nextName;
					this.relayLowerNames[i] = this.relayNames[i].toLowerCase();
					this.relayFingerprintHashes[i] = relay
							.getString(RELAY_FINGERPRINT_JSON_KEY);
					this.relayLowerFingerprintHashes[i] = this.relayFingerprintHashes[i]
							.toLowerCase();
					this.runningRelays[i] = relay.getBoolean(RELAY_RUNING_JSON_KEY);
					String[]/* [] */addrs = EMPTY_STRING_ARRAY;
					if (relay.has(RELAY_ADDRESSES_JSON_KEY)) {
						JSONArray addressesArray = relay
								.getJSONArray(RELAY_ADDRESSES_JSON_KEY);
						addrs = new String[addressesArray.length()];// [];
						for (int j = 0; j < addrs.length; j++) {
							addrs[j] = addressesArray.getString(j);// .split(".");
						}
						this.relayAddresses[i] = addrs;
					}
				}
			}else{
				this.relayNames = new String[0];
				this.relayLowerNames = new String[0];
				this.relayFingerprintHashes = new String[0];
				this.relayLowerFingerprintHashes = new String[0];
				this.relayAddresses = new String[0][];// [];
				this.runningRelays = new boolean[0];
			}

			JSONArray bridges = json.getJSONArray(BRIDGES_JSON_KEY);
			if (bridges != null && bridges.length() > 0) {
				this.bridgeFingerprintHashes = new String[bridges.length()];
				this.bridgeLowerFingerprintHashes = new String[bridges.length()];
				this.runningBridges = new boolean[bridges.length()];
				for (int i = 0; i < bridges.length(); i++) {
					JSONObject bridge = bridges.getJSONObject(i);
					this.bridgeFingerprintHashes[i] = bridge
							.getString(BRIDGE_FINGERPRINT_JSON_KEY);
					this.bridgeLowerFingerprintHashes[i] = this.bridgeFingerprintHashes[i]
							.toLowerCase();
					this.runningBridges[i] = bridge
							.getBoolean(BRIDGE_RUNING_JSON_KEY);
				}
			}else{
				this.bridgeFingerprintHashes = new String[0];
				this.bridgeLowerFingerprintHashes = new String[0];
				this.runningBridges = new boolean[0];
			}

			long elapsed = System.currentTimeMillis() - start;
			Log.i("SOME", this.relayNames.length + " relays added in "
					+ elapsed + " milliseconds.");
			
			String relaysPublished = json.getString(RELAYS_PUBLISHED_KEY);
			String bridgesPublished = json.getString(BRIDGES_PUBLISHED_KEY);
			GregorianCalendar relaysPublishedCal = fromString(relaysPublished);
			GregorianCalendar bridgesPublishedCal = fromString(bridgesPublished);
			GregorianCalendar earliestPublishedCal = relaysPublishedCal.before(bridgesPublishedCal) ? relaysPublishedCal : bridgesPublishedCal;
			earliestPublishedCal.add(Calendar.MINUTE, MINUTES_PUBLICATION_REFRESH_DELAY);
			GregorianCalendar defaultRefreshTimestamp = new GregorianCalendar();
			defaultRefreshTimestamp.add(Calendar.MINUTE, MIN_MINUTES_REFRESH_DELAY);
			this.refreshTime = earliestPublishedCal.compareTo(defaultRefreshTimestamp) >= 0 ? earliestPublishedCal : defaultRefreshTimestamp; 
		} catch (JSONException e) {
			Log.e(LogTags.JSON_PROCESSING.toString(), "Error procssing JSON data.",e);
		}
	}

	public List<String[]> search(String submittedSearchString,
			Set<String> favs) {
		int nextFavPos = 0;
		List<String[]> results = new LinkedList<String[]>();
		if (submittedSearchString != null && !submittedSearchString.equals(EMPTY_STRING)) {
			String searchString = submittedSearchString.toLowerCase();
			String[] searchStringParts = searchString.split(" ");
			Set<Integer> ids = null;
			for (String nextPart : searchStringParts) {
				Matcher ipMatcher = IP_PATTERN.matcher(nextPart);
				Matcher hashMatcher = HASH_PATTERN.matcher(nextPart);
				Matcher nameMatcher = NAME_PATTERN.matcher(nextPart);
				ids = this.searchPart(nextPart, nameMatcher.matches(),
						hashMatcher.matches(), ipMatcher.matches(), ids);
			}
			int bridgesStart = this.relayNames.length;
			for (int next : ids) {
				if (next < this.relayNames.length) {
					String online = null;
					if (this.runningRelays[next]) {
						online = "";
					}
					if (favs != null
							&& favs.contains(relayFingerprintHashes[next])) {
						results.add(nextFavPos++,
								new String[] { new Long(next).toString(),
										relayNames[next],
										relayFingerprintHashes[next], null,
										online });
					} else {
						results.add(new String[] { new Long(next).toString(),
								relayNames[next], relayFingerprintHashes[next],
								null, online });
					}
				} else {
					int bridgeIndex = next - bridgesStart;
					String online = null;
					if (this.runningBridges[bridgeIndex]) {
						online = EMPTY_STRING;
					}
					if (favs != null
							&& favs.contains(bridgeFingerprintHashes[bridgeIndex])) {
						results.add(nextFavPos++,
								new String[] { new Long(next).toString(),
										EMPTY_STRING,
										bridgeFingerprintHashes[bridgeIndex],
										null, online });
					} else {
						results.add(new String[] { new Long(next).toString(),
								EMPTY_STRING,
								bridgeFingerprintHashes[bridgeIndex], null,
								online });
					}
				}
			}
		}
		return results;
	}

	private Set<Integer> searchPart(String searchStringPart, boolean name,
			boolean hash, boolean ip, Set<Integer> foundKeys) {
		Set<Integer> result = foundKeys;
		if (result == null) {
			result = this.startSearch(searchStringPart, name, hash, ip);
		} else {
			result = this.continueSearch(searchStringPart, name, hash, ip,
					result);
		}
		return result;
	}

	private Set<Integer> continueSearch(String part, boolean name,
			boolean hash, boolean ip, Set<Integer> foundKeys) {
		Set<Integer> result = new TreeSet<Integer>();
		for (Integer next : foundKeys) {
			if (this.matches(part, next, name, hash, ip)) {
				result.add(next);
			}
		}
		return result;
	}

	private Set<Integer> startSearch(String part, boolean name, boolean hash,
			boolean ip) {
		Set<Integer> result = new TreeSet<Integer>();
		int start = 0;
		int end = this.relayNames.length + this.bridgeFingerprintHashes.length;
		for (int i = start; i < end; i++) {
			if (this.matches(part, i, name, hash, ip)) {
				result.add(i);
			}
		}
		return result;
	}

	private boolean matches(String part, int index, boolean name, boolean hash,
			boolean ip) {
		if (index < this.relayAddresses.length) {
			if (hash
					&& this.relayLowerFingerprintHashes[index].startsWith(part)) {
				return true;
			}
			if (name && this.relayLowerNames[index].contains(part)) {
				return true;
			}
			if (ip) {
				for (String nodeIp : this.relayAddresses[index]) {
					if (nodeIp.startsWith(part)) {
						return true;
					}
				}
			}
		} else {
			int bridgeIndex = index - relayNames.length;
			if (hash
					&& this.bridgeLowerFingerprintHashes[bridgeIndex]
							.startsWith(part)) {
				return true;
			}
		}
		return false;
	}

	public String getRelayName(int index) {
		return this.relayNames[index];
	}

	public String getFingerprint(int index) {
		if(isRelay(index)){
			return this.relayFingerprintHashes[index];
		}
		return this.bridgeFingerprintHashes[index-this.relayFingerprintHashes.length];
	}

	public boolean isRunning(int index) {
		if(isRelay(index)){
		return this.runningRelays[index];
		}
		return this.runningBridges[index-this.relayFingerprintHashes.length];
	}

	public boolean isRelay(int index) {
		return index < this.relayNames.length;
	}
	
	public int getNodeCount(){
		if(this.relayNames == null && this.bridgeFingerprintHashes == null){
			return 0;
		}
		if(this.relayNames != null && this.bridgeFingerprintHashes == null){
			return this.relayNames.length;
		}
		if(this.relayNames == null && this.bridgeFingerprintHashes != null){
			return this.bridgeFingerprintHashes.length;
		}
		return this.relayNames.length + this.bridgeFingerprintHashes.length;
	}
	
}
