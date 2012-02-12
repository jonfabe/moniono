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

import static org.moniono.util.LogTags.DB_HELPER;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class extends the {@link SQLiteOpenHelper} class and is responsible to
 * create or upgrade the database of the app.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class DbHelper extends SQLiteOpenHelper {

	/**
	 * Name of the app-DB.
	 */
	private static final String DATABASE_NAME = "moniono_data";
	/**
	 * Version of the DB.
	 */
	private static final int DATABASE_VERSION = 6;
	/**
	 * Name of the favorite nodes table.
	 */
	public static final String NODES_TABLE = "nodes";
	/**
	 * Name of the primary key column of the table.
	 */
	public static final String NODES_KEY_ROWID = "_id";
	/**
	 * Name of the fingerprint hash column of the table.
	 */
	public static final String NODES_KEY_FINGERPRINT_HASH = "n_fingerprint_hash";
	/**
	 * Name of the type column of the table.
	 */
	public static final String NODES_KEY_TYPE = "n_type";
	/**
	 * Name of the nickname column of the table.
	 */
	public static final String NODES_KEY_NAME = "n_name";

	/**
	 * Node table creation SQL statement.
	 */
	private static final String NODES_TABLE_CREATE = "create table "
			+ NODES_TABLE + " (" + NODES_KEY_ROWID
			+ " integer primary key autoincrement, "
			+ NODES_KEY_FINGERPRINT_HASH + " text not null, " + NODES_KEY_NAME
			+ " text, " + NODES_KEY_TYPE + " integer(1) not null,UNIQUE("
			+ NODES_KEY_FINGERPRINT_HASH + "));";
	/**
	 * Node table removal SQL statement.
	 */
	private static final String NODES_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ NODES_TABLE + ";";

	/**
	 * Constructor accepting an initial value for the context within which
	 * database operations are planned to be performed.
	 * 
	 * @param context Initial value for execution context.
	 */
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(NODES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(DB_HELPER.toString(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ". This will destroy all data.");
		db.execSQL(NODES_TABLE_DROP);
		onCreate(db);
	}

}
