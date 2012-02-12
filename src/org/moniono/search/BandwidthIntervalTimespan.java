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

import org.moniono.R;

public final class BandwidthIntervalTimespan implements Comparable<BandwidthIntervalTimespan>{

	public enum TimespanType {

		DAYS((byte) 0, R.string.day, R.string.days,"day", "days"), WEEKS((byte) 1, R.string.week, R.string.weeks,"week", "weeks"), MONTHS(
				(byte) 2, R.string.month, R.string.months,"month", "months"), YEARS((byte) 3, R.string.year, R.string.years,"year", "years");

		private byte position;
		private int singularKey;
		private int pluralKey;
		private String jsonKeySingular;
		private String jsonKeyPlural;

		private TimespanType(byte iniPosition, int iniSingularKey,
				int iniPluralKey, String iniJsonKeySingular,
				String iniJsonKeyPlural) {
			this.position = iniPosition;
			this.singularKey = iniSingularKey;
			this.pluralKey = iniPluralKey;
			this.jsonKeySingular = iniJsonKeySingular;
			this.jsonKeyPlural = iniJsonKeyPlural;
		}

		public byte getPosition() {
			return position;
		}

		public int getSingularKey() {
			return singularKey;
		}

		public int getPluralKey() {
			return pluralKey;
		}
		
		public static TimespanType valueOfString(String stringRepresentation){
			for(TimespanType next:TimespanType.values()){
				if(next.jsonKeySingular.equalsIgnoreCase(stringRepresentation) || next.jsonKeyPlural.equalsIgnoreCase(stringRepresentation)){
					return next;
				}
			}
			return null;
		}

	}
	
	private TimespanType type;
	private int quantity;
	
	public BandwidthIntervalTimespan(TimespanType iniType,int iniQuantity){
		if(iniType == null){
			throw new IllegalArgumentException("Timespan type must be set.");
		}
		this.type = iniType;
		if(iniQuantity <= 0){
			throw new IllegalArgumentException("Quantity must be greater zero.");
		}
		this.quantity = iniQuantity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.quantity;
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BandwidthIntervalTimespan other = (BandwidthIntervalTimespan) obj;
		if (this.quantity != other.quantity)
			return false;
		if (this.type != other.type)
			return false;
		return true;
	}

	public int compareTo(BandwidthIntervalTimespan another) {
		if(another == null){
			throw new NullPointerException("Submitted value must not be null.");
		}
		if(this.type.getPosition() < another.getType().getPosition()){
			return -1;
		}
		if(this.type.getPosition() > another.getType().getPosition()){
			return 1;
		}
		if(this.quantity < another.getQuantity()){
			return -1;
		}
		if(this.quantity > another.getQuantity()){
			return 1;
		}
		return 0;
	}

	public TimespanType getType() {
		return type;
	}

	public void setType(TimespanType type) {
		this.type = type;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public String toString(){
		return this.quantity+" "+this.type;
	}

}
