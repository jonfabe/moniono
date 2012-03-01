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

import static org.moniono.util.CommonConstants.EMPTY_STRING;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moniono.search.DataManager;

import android.util.Log;

/**
 * This class encapsulates static features being related to JSON processing.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public final class JSONUtil {

	/**
	 * Constant being used for any message if a required key is not submitted.
	 */
	private static final String CONTRACT_VIOLATION_MSG_KEY_NOT_SUBMITTED = "Contract violation: Key must be submitted!";

	/**
	 * Constant being used for any message if a required {@link JSONObject} is
	 * not submitted.
	 */
	private static final String CONTRACT_VIOLATION_MSG_JSONOBJECT_NOT_SUBMITTED = "Contract violation: JSON object must be submitted!";

	/**
	 * Constructor is only implemented to prevent external class instantiation.
	 */
	private JSONUtil() {
		/* Prevent instantiation */
	}

	/**
	 * This method returns a string value for a submitted key. The key is
	 * resolved in the context of the {@link JSONObject} being also submitted.
	 * The submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing, e.g., if the
	 *             found value could not be converted to a string.
	 */
	public static final String getString(String key, String defaultValue,
			JSONObject json) throws JSONException {
		checkParameters(key, json);
		String result = defaultValue;
		if (json.has(key)) {
			result = json.getString(key);
		}
		return result;
	}

	/**
	 * This method returns a double value for a submitted key. The key is
	 * resolved in the context of the {@link JSONObject} being also submitted.
	 * The submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing, e.g., if the
	 *             found value could not be converted to double.
	 */
	public static final double getDouble(String key, double defaultValue,
			JSONObject json) throws JSONException {
		checkParameters(key, json);
		double result = defaultValue;
		if (json.has(key)) {
			result = json.getDouble(key);
		}
		return result;
	}

	/**
	 * This method returns a string array value for a submitted key. The key is
	 * resolved in the context of the {@link JSONObject} being also submitted.
	 * The submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing, e.g., if the
	 *             found value could not be converted into a string array.
	 */
	public static final String[] getStringArray(String key,
			String[] defaultValue, JSONObject json) throws JSONException {
		checkParameters(key, json);
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

	/**
	 * This method returns a calendar value for a submitted key. The key is
	 * resolved in the context of the {@link JSONObject} being also submitted.
	 * The submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing.
	 */
	public static final Calendar getCalendar(String key, Calendar defaultValue,
			JSONObject json) throws JSONException {
		checkParameters(key, json);
		Calendar result = defaultValue;
		if (json.has(key)) {
			result = DataManager.fromString(json.getString(key));
		}
		return result;
	}

	/**
	 * This method returns a boolean array value for a submitted key. The key is
	 * resolved in the context of the {@link JSONObject} being also submitted.
	 * The submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing, e.g., if the
	 *             found value could not be converted into boolean.
	 */
	public static final boolean getBoolean(String key, boolean defaultValue,
			JSONObject json) throws JSONException {
		checkParameters(key, json);
		boolean result = defaultValue;
		if (json.has(key)) {
			result = json.getBoolean(key);
		}
		return result;
	}

	/**
	 * This method returns a long value for a submitted key. The key is resolved
	 * in the context of the {@link JSONObject} being also submitted. The
	 * submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing, e.g., if the
	 *             found value could not be converted into long.
	 */
	public static final long getLong(String key, long defaultValue,
			JSONObject json) throws JSONException {
		checkParameters(key, json);
		long result = defaultValue;
		if (json.has(key)) {
			result = json.getLong(key);
		}
		return result;
	}

	/**
	 * This method returns an integer value for a submitted key. The key is
	 * resolved in the context of the {@link JSONObject} being also submitted.
	 * The submitted default value is returned if the key is not set.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param defaultValue
	 *            Default value for the key. This value is returned if no value
	 *            could be found for the key in the context of the JSON object.
	 * @param json
	 *            JSON object within which context a value for the submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Found value or default value if no value could be found for key.
	 * @throws JSONException
	 *             This exception is thrown during JSON processing, e.g., if the
	 *             found value could not be converted into integer.
	 */
	public static final int getInteger(String key, int defaultValue,
			JSONObject json) throws JSONException {
		checkParameters(key, json);
		int result = defaultValue;
		if (json.has(key)) {
			result = json.getInt(key);
		}
		return result;
	}

	/**
	 * This method is used internally only by multiple other methods in order to
	 * verify that parameters are submitted correctly. An
	 * {@link IllegalArgumentException} is thrown if this is not the case.
	 * 
	 * @param key
	 *            Key for which a value should be determined. The key must be
	 *            set and must not be an empty string. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 * @param json
	 *            JSON object within which context a value for a submitted key
	 *            should be determined. The object must be set. Otherwise an
	 *            {@link IllegalArgumentException} is thrown.
	 */
	private static final void checkParameters(String key, JSONObject json) {
		if (key == null || key.equals(EMPTY_STRING)) {
			Log.e(LogTags.JSON_PROCESSING.toString(),
					CONTRACT_VIOLATION_MSG_KEY_NOT_SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_KEY_NOT_SUBMITTED);
		}
		if (json == null) {
			Log.e(LogTags.JSON_PROCESSING.toString(),
					CONTRACT_VIOLATION_MSG_JSONOBJECT_NOT_SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_JSONOBJECT_NOT_SUBMITTED);
		}
	}

}
