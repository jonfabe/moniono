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


public enum NodeFlag{
	
	Authority("Authority"), 	
	BadDirectory("BadDirectory"), 	
	BadExit("BadExit"),
	Exit("Exit"), 	
	Fast("Fast"), 	
	Guard("Guard"), 	
	HsDirectory("HSDir"), 	
	Named("Named"),
	Stable("Stable"), 	
	Running("Running"),
	Unnamed("Unnamed"),
	Valid("Valid"),
	V2Dir("V2Dir"),
	V3Dir("V3Dir");
	
	private String abbrev;
	
	public static final NodeFlag fromAbbrev(String reqAbbrev){
		for(NodeFlag next:NodeFlag.values()){
			if(next.getAbbrev().equals(reqAbbrev)){
				return next;
			}
		}
		throw new IllegalArgumentException("Unknown abbreviation "+reqAbbrev);
	}
	
	NodeFlag(String iniAbbrev){
		this.abbrev = iniAbbrev;
	}

	public String getAbbrev() {
		return this.abbrev;
	}

}
