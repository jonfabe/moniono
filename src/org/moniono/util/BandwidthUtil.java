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

import java.text.NumberFormat;

import android.util.Log;

/**
 * This class encapsulates static features being related to bandwidth
 * information.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public final class BandwidthUtil {

	/**
	 * Constant representing the word "Bytes".
	 */
	private static final String BYTES = "Bytes";
	/**
	 * Constant representing the word "KBytes".
	 */
	private static final String KBYTES = "KBytes";
	/**
	 * Constant representing the word "MBytes".
	 */
	private static final String MBYTES = "MBytes";
	/**
	 * Constant representing the word "GBytes".
	 */
	private static final String GBYTES = "GBytes";
	/**
	 * Constant representing bytes per kilobyte.
	 */
	private static final double KBYTE = 1024;
	/**
	 * Constant representing bytes per megabyte.
	 */
	private static final double MBYTE = 1024 * KBYTE;
	/**
	 * Constant representing bytes per gigabyte.
	 */
	private static final double GBYTE = 1024 * MBYTE;
	/**
	 * Constant covering the local number formats.
	 */
	private static final NumberFormat FORM = NumberFormat.getInstance();

	/**
	 * Constant being used for any message if a submitted bandwidth value is
	 * less than 0.
	 */
	private static final String CONTRACT_VIOLATION_MSG_BANDWITH_LESS_THAN_ZERO = "Contract violation: Bandwidth information must be greater or equal to 0!";

	/**
	 * Constructor is only implemented to prevent external class instantiation.
	 */
	private BandwidthUtil() {
		/* Prevent instantiation */
	}

	/**
	 * This method can be used to calculate a human readable String representing
	 * the bandwidth submitted in bytes. The string has the following structure:
	 * 
	 * {@link #FORM}.format((double)bandwidth / (double) <unit-size>) + " " +
	 * <unit-name>
	 * 
	 * where <unit-size> is the maximum unit size which is smaller than or equal
	 * to the bandwidth in bytes ({@link #GBYTE}, {@link #MBYTE} or
	 * {@link #KBYTE}). A value of "1" is taken if bandwidth is smaller than
	 * {@link #KBYTE}. The <unit-name> is the corresponding, human-readable unit
	 * name for <unit-size> that is {@link #GBYTES}, {@link #MBYTES},
	 * {@link #KBYTES} or {@link #BYTES}.
	 * 
	 * @param bandwidth
	 *            Bandwidth in bytes which should be converted into a human
	 *            readable string. The value must be greater or equal to zero.
	 *            Otherwise an {@link IllegalArgumentException} is thrown.
	 * @return Human readable string.
	 */
	@SuppressWarnings("null")
	public static final String getBandwidthString(long bandwidth) {
		if (bandwidth < 0) {
			Log.e(LogTags.DATA_PROCESSING.toString(),
					CONTRACT_VIOLATION_MSG_BANDWITH_LESS_THAN_ZERO);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_BANDWITH_LESS_THAN_ZERO);
		}
		StringBuffer buf = new StringBuffer();
		String unit = null;
		@SuppressWarnings("cast")
		double bandwidthDouble = (double) bandwidth;
		if (bandwidth >= GBYTE) {
			unit = GBYTES;
			buf.append(FORM.format(bandwidthDouble / GBYTE));
		} else if (unit == null && bandwidth >= MBYTE) {
			unit = MBYTES;
			buf.append(FORM.format(bandwidthDouble / MBYTE));
		} else if (unit == null && bandwidth >= KBYTE) {
			unit = KBYTES;
			buf.append(FORM.format(bandwidthDouble / KBYTE));
		} else if (unit == null) {
			unit = BYTES;
			buf.append(bandwidth);
		}
		buf.append(" ");
		buf.append(unit);
		return buf.toString();
	}
}
