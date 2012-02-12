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

public final class BandwidthUtil {

	private static final String EMPTY_STRING = "";
	private static final String DOT = ".";
	private static final String ZERO = "0";
	private static final String BYTES = "Bytes";
	private static final String KBYTES = "KBytes";
	private static final String MBYTES = "MBytes";
	private static final String GBYTES = "GBytes";
	private static final long KBYTE = 1024;
	private static final long MBYTE = 1024 * KBYTE;
	private static final long GBYTE = 1024 * MBYTE;
	private static final NumberFormat FORM = NumberFormat.getInstance();

	private BandwidthUtil() {
		/* Prevent instantiation */
	}

	public static final String getBandwidthString(long bandwidth) {
		StringBuffer buf = new StringBuffer();
		String unit = null;
		if (bandwidth >= GBYTE) {
			unit = GBYTES;
			buf.append(FORM.format((double) bandwidth / (double) GBYTE));
		} else if (unit == null && bandwidth >= MBYTE) {
			unit = MBYTES;
			buf.append(FORM.format((double) (bandwidth % GBYTE)
					/ (double) MBYTE));
		} else if (unit == null && bandwidth >= KBYTE) {
			unit = KBYTES;
			buf.append(FORM.format(((double) bandwidth % (double) MBYTE)
					/ (double) KBYTE));
		} else if (unit == null) {
			unit = BYTES;
			buf.append(bandwidth);
		}
		buf.append(" ");
		buf.append(unit);
		return buf.toString();
	}

//	private static final void fill(long count, StringBuffer buf) {
//		for (int i = 1000; i > 0 && i >= count; i = i / 10) {
//			buf.append(ZERO);
//		}
//	}

}
