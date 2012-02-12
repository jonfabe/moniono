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

import java.util.LinkedHashMap;
import java.util.Map;

public class CachingHashMap<S,T> extends LinkedHashMap<S,T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2434555424705681049L;
	private int maxEntries = 100;
	
	public CachingHashMap(int iniMaxEntries){
		this.maxEntries = iniMaxEntries;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.size() > this.maxEntries;
     }

}
