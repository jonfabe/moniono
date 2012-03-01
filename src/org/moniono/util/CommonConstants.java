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

/**
 * This class encapsulates commonly used constants.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha *
 */
public final class CommonConstants {

	/**
	 * SO timeout in milliseconds for HTTP-requests. This value should always
	 * set for connections in order to ensure centralized control of connection
	 * properties.
	 */
	public static final int HTTP_SO_TIMEOUT = 5 * 1000;

	/**
	 * Connection timeout in milliseconds for HTTP-requests. This value should
	 * always set for connections in order to ensure centralized control of
	 * connection properties.
	 */
	public static final int HTTP_CONNECTION_TIMEOUT = 5 * 1000;

	/**
	 * Constant representing an empty string.
	 */
	public static final String EMPTY_STRING = "";

	/** The base URL to the Google Chart API (POST). */
	public static final String CHART_URL = "https://chart.googleapis.com/chart";

	/** The base URL to the restful Onionoo service. */
	public static final String NODE_DATA_BASE_URL = "http://onionoo.torproject.org/";

	/** The URL to request Onionoo summary information on nodes. */
	public static final String NODES_LIST_URL = NODE_DATA_BASE_URL
			+ "/summary/all";

	/**
	 * The URL to request Onionoo node details information. This URL is intended
	 * to be extended with the (hashed) fingerprint of the node for which
	 * details should be requested. The following sample show how the definitive
	 * details URL for a particular node can be constructed:
	 * 
	 * String myNodeDetailsUrlString =
	 * NODE_DETAILS_BASE_URL+myNodeFingerprint.toUpperCase();
	 * 
	 * In the example myNodeFingerprint must be a String.
	 */
	public static final String NODE_DETAILS_BASE_URL = NODE_DATA_BASE_URL
			+ "/details/lookup/";

	/**
	 * The URL to request Onionoo node bandwidth history information. This URL
	 * is intended to be extended with the (hashed) fingerprint of the node for
	 * which bandwidth information should be requested. The following sample
	 * show how the definitive details URL for a particular node can be
	 * constructed:
	 * 
	 * String myNodeBandwidthUrlString =
	 * NODE_BANDWIDTH_BASE_URL+myNodeFingerprint.toUpperCase();
	 * 
	 * In the example myNodeFingerprint must be a String.
	 */
	public static final String NODE_BANDWIDTH_BASE_URL = NODE_DATA_BASE_URL
			+ "/bandwidth/lookup/";

	/**
	 * This constructor is only provided to prevent unintended instantiation of
	 * this class.
	 */
	private CommonConstants() {
		/* Prevent instantiation. */
	}

}
