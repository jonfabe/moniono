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
 * This enum covers literals representing log tags.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public enum LogTags {

	/**
	 * Logging in the context of a database helper.
	 */
	DB_HELPER,
	/**
	 * Logging in the context stream processing, e.g., during processing of
	 * streams received from HTTP requets.
	 */
	STREAM_PROCESSING,
	/**
	 * Logging in the context JSON processing.
	 */
	JSON_PROCESSING,
	/**
	 * Logging in the context view elements.
	 */
	VIEW,
	/**
	 * Logging in the context of bandwidth history information.
	 */
	HISTORY,
	/**
	 * Logging in the context of search execution.
	 */
	SEARCH,
	/**
	 * Logging in the context of dialog handling.
	 */
	DIALOG,
	/**
	 * Logging in the context of activity execution.
	 */
	ACTIVITY,
	/**
	 * Logging in the context network connection handling.
	 */
	NETWORK,
	/**
	 * Logging in the context of interface contracts. This tag is mostly used in
	 * cases where a contract is violated.
	 */
	CONTRACT,
	/**
	 * Used for temporary logging.
	 */
	TEMP,
	/**
	 * Logging in the context data refresh.
	 */
	DATA_REFRESH;
}
