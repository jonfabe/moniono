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
	
	public static final int HTTP_SO_TIMEOUT = 5 * 1000;
	public static final int HTTP_CONNECTION_TIMEOUT = 5 * 1000;
	
	/**
	 * Constant representing an empty string.
	 */
	public static final String EMPTY_STRING = "";
	
	public static final String CHART_URL = "https://chart.googleapis.com/chart";
	
	public static final String NODE_DATA_BASE_URL = "http://onionoo.torproject.org/";
	
	public static final String NODES_LIST_URL = NODE_DATA_BASE_URL+"/summary/all";
	
	public static final String NODE_DETAILS_BASE_URL = NODE_DATA_BASE_URL+"/details/lookup/";
	
	public static final String NODE_BANDWIDTH_BASE_URL = NODE_DATA_BASE_URL+"/bandwidth/lookup/";

	/**
	 * This constructor is only provided to prevent unintended instantiation of
	 * this class.
	 */
	private CommonConstants() {
		/* Prevent instantiation. */
	}

}
