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

import static org.moniono.db.DbHelper.NODES_KEY_FINGERPRINT_HASH;
import static org.moniono.db.DbHelper.NODES_KEY_NAME;
import static org.moniono.db.DbHelper.NODES_KEY_ROWID;
import static org.moniono.db.DbHelper.NODES_KEY_TYPE;
import static org.moniono.db.DbHelper.NODES_TABLE;
import static org.moniono.util.CommonConstants.EMPTY_STRING;

import org.moniono.util.LogTags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class encapsulates database interactions by means of CRUD-operations.
 * Additionally, it provides possibilities to request lists of nodes, that is,
 * bridges or relays.
 * 
 * On creation of an instance of this class a {@link Context} reference must be
 * provided. This is required and used to establish the original database
 * connection.
 * 
 * Before the first execution of another method, the method {@link #open()} must
 * be executed.
 * 
 * It is recommended that the connection to the database is closed through
 * execution of the {@link NodesDbAdapter#close()} method when an adapter
 * instance is not needed anymore.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class NodesDbAdapter {

	/**
	 * Constant being used for any message if a required context reference is
	 * not specified during construction of class instance.
	 */
	private static final String CONTRACT_VIOLATION_MSG_CONTEXT_MUST_BE_SET = "Contract violation: Reference to context must be set on construction!";

	/**
	 * Constant being used for any message if a required value for a fingerprint
	 * (hash) is not specified on a method invocation.
	 */
	private static final String CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED = "Contract violation: Fingerprint (hash) must be subimtted!";

	/**
	 * Constant being used for any message if a required value for a node type
	 * is not specified on a method invocation.
	 */
	private static final String CONTRACT_VIOLATION_MSG_NODE_TYPE_MUST_BE_SUBMITTED = "Contract violation: Node type must be subimtted!";

	/**
	 * Constant being used for any message if an illegal value for a node type
	 * is subnmitted on a method invocation.
	 */
	private static final String CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED = "Contract violation: Node type must be subimtted!";

	/**
	 * This reference to a context is used to establish a connection to the
	 * original database through the {@link #helper}.
	 */
	private final Context ctx;

	/**
	 * Instance of helper class used to initially prepare DB-interactions.
	 */
	private DbHelper helper;

	/**
	 * Reference to database being used to execute CRUD-operations on.
	 */
	private SQLiteDatabase db;

	/**
	 * Constructor accepting an initial value for {@link #ctx}. The submitted
	 * value must not be null. Otherwise, an {@link IllegalArgumentException} is
	 * thrown.
	 * 
	 * @param iniCtx
	 *            Initial value for {@link #ctx}.
	 */
	public NodesDbAdapter(Context iniCtx) {
		if (iniCtx == null) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_CONTEXT_MUST_BE_SET);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_CONTEXT_MUST_BE_SET);
		}
		this.ctx = iniCtx;
	}

	/**
	 * This method MUST be invoked before the first usage of a CRUD-method being
	 * provided by this class.
	 * 
	 * @return Self-reference. The return value is provided in order to allow
	 *         builder-like method invocations
	 */
	public NodesDbAdapter open() {
		this.helper = new DbHelper(this.ctx);
		this.db = this.helper.getWritableDatabase();
		return this;
	}

	/**
	 * This method SHOULD be invoked when neither the nodes adapter nor returned
	 * database cursors are needed anymore.
	 */
	public void close() {
		this.helper.close();
		this.db = null;
	}

	/**
	 * This operation creates a new node or updates an existing one based on the
	 * submitted information within the database. The operation returns the
	 * primary key of new database entry after successful execution.
	 * 
	 * Remark: Database must be opened before execution.
	 * 
	 * @param fingerprintHash
	 *            Fingerprint of relay or hashed fingerprint of bridge. The
	 *            value must be set. Otherwise, an
	 *            {@link IllegalArgumentException} is thrown. If there already
	 *            exist a node with that (hashed) fingerprint, the existing
	 *            entry is updated.
	 * @param name
	 *            Node name.
	 * @param nodeType
	 *            Type of node. The value must be {@link NodeType#BRIDGE} or @link
	 *            {@link NodeType#RELAY}. Otherwise, an
	 *            {@link IllegalArgumentException} is thrown.
	 * @return Primary key of newly created or updated database entry.
	 */
	public long createNode(String fingerprintHash, String name, Integer nodeType) {
		/*
		 * Validate contract adherence: Fingerprint and node type must be set.
		 * If contract is not fulfilled, an exception is thrown and an log
		 * message is written.
		 */
		if (fingerprintHash == null || fingerprintHash.equals(EMPTY_STRING)) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED);
		}
		if (nodeType == null) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_NODE_TYPE_MUST_BE_SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_NODE_TYPE_MUST_BE_SUBMITTED);
		}
		if (nodeType.intValue() != NodeType.BRIDGE.getIdentifier()
				&& nodeType.intValue() != NodeType.RELAY.getIdentifier()) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED);
		}

		/*
		 * Execute database query in order to find a potentially existing node
		 * entry in database.
		 */
		Cursor c = this.db.query(true, NODES_TABLE,
				new String[] { NODES_KEY_ROWID }, NODES_KEY_FINGERPRINT_HASH
						+ "='" + fingerprintHash + "'", null, null, null, null,
				null);

		/*
		 * If an existing entry is found, update that entry and return from
		 * method execution.
		 */
		if (c.getCount() != 0) {
			c.moveToFirst();
			long nodeId = c.getLong(0);
			this.updateNode(nodeId, fingerprintHash, name,
					NodeType.fromInt(nodeType.intValue()));
			return nodeId;
		}

		/*
		 * If no existing value was found in the previous steps, create a new
		 * entry in database based on the submitted values.
		 */
		ContentValues initialValues = new ContentValues();
		if (name != null) {
			initialValues.put(NODES_KEY_NAME, name);
		}
		initialValues.put(NODES_KEY_FINGERPRINT_HASH, fingerprintHash);
		initialValues.put(NODES_KEY_TYPE, nodeType);
		return this.db.insert(NODES_TABLE, null, initialValues);
	}

	/**
	 * The execution of this operation leads to the removal of the database
	 * entry with the submitted row identifier. The return value is directly
	 * derived from execution of the corresponding database operation. It
	 * indicates if the deletion was successful.
	 * 
	 * Remark: Database must be opened before execution.
	 * 
	 * @param rowId
	 *            Row identifier of the database entry to delete.
	 * @return Return value from execution of database operation. This value
	 *         indicates of the deletion was successful.
	 */
	public boolean deleteNode(long rowId) {
		return this.db.delete(NODES_TABLE, NODES_KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * This method can be used to request a database representation of a node.
	 * The node is identified by its node identifier which needs to be submitted
	 * as parameter. The method delivers a cursor returned from underlying query
	 * execution. The cursor points to the first entry (
	 * {@link Cursor#moveToFirst()}) if a node entry could be found. Otherwise,
	 * null is returned.
	 * 
	 * @param nodeId
	 *            Identifier of node which needs to be found in database.
	 * @return The cursor covering the requested node representation within the
	 *         database. The cursor points to the first entry (
	 *         {@link Cursor#moveToFirst()}) if a node entry could be found.
	 *         Otherwise, null is returned.
	 */
	public Cursor fetchNode(long nodeId) {
		Cursor mCursor = this.db.query(true, NODES_TABLE, new String[] {
				NODES_KEY_ROWID, NODES_KEY_FINGERPRINT_HASH, NODES_KEY_NAME,
				NODES_KEY_TYPE }, NODES_KEY_ROWID + "=" + nodeId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * This method returns a cursor covering all nodes being found in the
	 * database. The cursor elements are sorted ascending by the node type (
	 * {@link NodeType#getIdentifier()}) and their fingerprints (hashes).
	 * 
	 * @return cursor Cursor covering all nodes within the database.
	 */
	public Cursor fetchAllNodes() {
		return this.db.query(NODES_TABLE, new String[] { NODES_KEY_ROWID,
				NODES_KEY_NAME, NODES_KEY_FINGERPRINT_HASH, NODES_KEY_TYPE },
				null, null, null, null, NODES_KEY_TYPE + " ASC, "
						+ NODES_KEY_FINGERPRINT_HASH + " ASC");
	}

	/**
	 * This method returns a cursor covering all nodes of the submitted type
	 * being found in the database. The cursor elements are sorted ascending by
	 * their fingerprints (hashes).
	 * 
	 * Type must either be {@link NodeType#BRIDGE} or {@link NodeType#RELAY}.
	 * Otherwise, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param type
	 *            Type of requested nodes.
	 * 
	 * @return cursor Cursor covering all nodes of the submitted type within the
	 *         database.
	 */
	public Cursor fetchAllOfType(int type) {
		if (type != NodeType.BRIDGE.getIdentifier()
				&& type != NodeType.RELAY.getIdentifier()) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED);
		}
		return this.db.query(NODES_TABLE, new String[] { NODES_KEY_ROWID,
				NODES_KEY_NAME, NODES_KEY_FINGERPRINT_HASH, NODES_KEY_TYPE },
				NODES_KEY_TYPE + "=" + type, null, null, null, NODES_KEY_TYPE
						+ " ASC, " + NODES_KEY_FINGERPRINT_HASH + " ASC");
	}

	/**
	 * This method returns a cursor covering all fingerprint(s) (hashes) being
	 * found in the database. The cursor elements are sorted ascending by their
	 * fingerprints (hashes).
	 * 
	 * @return cursor Cursor covering all fingerprint(s) (hashes) within the
	 *         database.
	 */
	public Cursor fetchAllFingerprints() {
		return this.db.query(NODES_TABLE,
				new String[] { NODES_KEY_FINGERPRINT_HASH }, null, null, null,
				null, NODES_KEY_FINGERPRINT_HASH + " ASC");
	}

	/**
	 * This method can be used to request an identifier of a node. The node is
	 * identified by its fingerprint (hash) which needs to be submitted as
	 * parameter. A value '-1' is retruned if no entry could be found for the
	 * submitted fingerprint (hash).
	 * 
	 * @param hashedFingerprint
	 *            Fingerprint (hash) for which the corresponding node identifier
	 *            should be returned. An {@link IllegalArgumentException} is
	 *            thrown if this parameter is not set or contains an empty
	 *            string.
	 * 
	 * @return The identifier of the database entry for the submitted
	 *         fingerprint (hash). A value '-1' is returned if no matching entry
	 *         could be found.
	 */
	public long fetchNodeIdByFingerprint(String hashedFingerprint) {
		if (hashedFingerprint == null || hashedFingerprint.equals(EMPTY_STRING)) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED);
		}
		long result = -1;
		this.open();
		Cursor c = this.db.query(NODES_TABLE, new String[] { NODES_KEY_ROWID },
				NODES_KEY_FINGERPRINT_HASH + "='" + hashedFingerprint + "'",
				null, null, null, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			result = c.getLong(0);
		}
		this.close();
		return result;
	}

	/**
	 * This method updates an existing database entry with the submitted values.
	 * An {@link IllegalArgumentException} is thrown if null or an empty string
	 * is submitted as fingerprint (hash), or null is submitted as node type.
	 * 
	 * @param nodeId
	 *            Identifier of database entry to update.
	 * @param hashedFingerprint
	 *            New fingerprint (hash). An {@link IllegalArgumentException} is
	 *            thrown if null or an empty string is submitted for this
	 *            parameter.
	 * @param newName
	 *            New value for the name of the database entry to update.
	 * @param newType
	 *            New type if database entry. An
	 *            {@link IllegalArgumentException} is thrown if null is
	 *            submitted for this parameter.
	 * @return Result from database on execution of the requested update.
	 */
	public boolean updateNode(long nodeId, String hashedFingerprint,
			String newName, NodeType newType) {
		if (hashedFingerprint == null || hashedFingerprint.equals(EMPTY_STRING)) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_FINGERPRINT_MUST_BE_SUBMITTED);
		}
		if (newType == null) {
			Log.e(LogTags.CONTRACT.toString(),
					CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED);
			throw new IllegalArgumentException(
					CONTRACT_VIOLATION_MSG_ILLEGAL_NODE_TYPE__SUBMITTED);
		}
		ContentValues args = new ContentValues();
		args.put(NODES_KEY_FINGERPRINT_HASH, hashedFingerprint);
		args.put(NODES_KEY_NAME, newName);
		args.put(NODES_KEY_TYPE, new Integer(newType.getIdentifier()));
		return this.db.update(NODES_TABLE, args,
				NODES_KEY_ROWID + "=" + nodeId, null) > 0;
	}

}
