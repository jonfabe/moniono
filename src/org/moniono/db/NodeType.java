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

package org.moniono.db;

/**
 * Literals of this enum represent the different node types (bridge or relay).
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public enum NodeType {

	/**
	 * Relay literal.
	 */
	RELAY(1, "Relay"),
	/**
	 * Bridge literal.
	 */
	BRIDGE(2, "Bridge");

	/**
	 * This constant represents a key within, for instance, activity extras. It
	 * is intended to be used for unification purposes.
	 */
	public static final String NODE_TYPE_KEY = "NODE_TYPE";

	/**
	 * Identifier of the corresponding node type. The identifier is intended to
	 * be used as part of database entries.
	 */
	private final int identifier;
	/**
	 * The title of the node type. This value is used as human readable
	 * representation of the particular type.
	 * 
	 * TODO: Use this as key for resources. This would allow language specific
	 * naming of node types.
	 */
	private final String title;

	/**
	 * Constructor for enum literals. the constructor accepts values for
	 * {@link #identifier} and {@link #title}.
	 * 
	 * @param iniIdentifier Initial value for {@link #identifier}.
	 * @param iniTitle Initial value for {@link #title}.
	 */
	private NodeType(int iniIdentifier, String iniTitle) {
		this.identifier = iniIdentifier;
		this.title = iniTitle;
	}

	/**
	 * Getter for {@link #title}.
	 * 
	 * @return Value of {@link #title}.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Getter for @link {@link #identifier}}.
	 * 
	 * @return Value of @link {@link #identifier}}.
	 */
	public int getIdentifier() {
		return this.identifier;
	}

	@Override
	public String toString() {
		return this.getTitle();
	}

	/**
	 * This method delivers the node type for the submitted string representation. 
	 * 
	 * @param stringRepresentation String representation ({@link #getTitle()}).
	 * @return Node type for string representation.
	 */
	public static NodeType fromString(String stringRepresentation) {
		for (NodeType next : NodeType.values()) {
			if (next.getTitle().equals(stringRepresentation)) {
				return next;
			}
		}
		return null;
	}

	/**
	 * This method delivers the node type for the identifier. 
	 * 
	 * @param identifier Identifier ({@link #getIdentifier()}).
	 * @return Node type for identifier.
	 */
	public static NodeType fromInt(int identifier) {
		for (NodeType next : NodeType.values()) {
			if (next.getIdentifier() == identifier) {
				return next;
			}
		}
		return null;
	}

}
