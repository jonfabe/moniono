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

package org.moniono.details;

import static org.moniono.util.CommonExtraId.IS_RELAY;
import static org.moniono.util.CommonExtraId.NODE_DATA;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.moniono.R;
import org.moniono.db.NodeType;
import org.moniono.db.NodesDbAdapter;
import org.moniono.details.overview.FavIconClickListener;
import org.moniono.search.DetailsData;
import org.moniono.util.LogTags;

import android.app.Activity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * This class implements the control of the details overview tab as part of the
 * node details activity ({@link NodeDetailsTabsActivity}). Its implementation
 * is responsible to initially setup view elements w.r.t. the establishment of
 * connections to data elements.
 * 
 * As foundation for successful application, the node's details information must
 * be set at {@link org.moniono.util.CommonExtraId#NODE_DATA} within intent
 * extras on invocation of {@link #onCreate(Bundle)}.
 * 
 * @author Jens Bruhn
 * @version 0.1 - alpha
 */
public class NodeDetailsOverviewActivity extends Activity {

	/**
	 * Data format being used to layout time information.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm");

	/**
	 * Text view being used to display the node's nickname if exists.
	 */
	private TextView nicknameText;
	/**
	 * Edit text field supporting the editing of a node's nickname. This field
	 * is only shown if the node is added as favorite and nickname editing is
	 * activated.
	 */
	private EditText nicknameEdit;
	/**
	 * The (hashed) fingerprint of the node.
	 */
	private String fingerprint;
	/**
	 * Detail information of the currently displayed node.
	 */
	private DetailsData details;
	/**
	 * Type of the currently displayed node.
	 */
	private NodeType type;
	/**
	 * Favorite icon allowing to add/remove the currently displayed node to from
	 * the list of favorite nodes.
	 */
	private ImageView favIcon;
	/**
	 * Edit icon allowing to switch nickname edit mode on/off.
	 */
	private ImageView editIcon;
	/**
	 * Save icon which is used to confirm nickname editing, that is, storing
	 * changes.
	 */
	private ImageView saveIcon;
	/**
	 * Reset icon which is used to reset a favorite node's nickname to the one
	 * provided by Onionoo.
	 */
	private ImageView resetIcon;

	/**
	 * The unique identifier of the node within the favorite nodes table, that
	 * is, its primary key.
	 */
	private long dbId = -1;

	/**
	 * Connection to database (abstraction).
	 */
	private NodesDbAdapter db = null;

	/**
	 * Boolean flag indicating if nickname edit mode is active (true) or not
	 * (false).
	 */
	private boolean editMode = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Switch to the history layout. */
		setContentView(R.layout.relay_details);

		/* Receive node details information from intent extras. */
		Bundle extras = getIntent().getExtras();
		this.details = (DetailsData) extras.get(NODE_DATA.toString());

		/*
		 * Set nickname text field value. This field is special, because its
		 * value will change if the node is chosen as favorite and the nickname
		 * is edited.
		 */
		this.nicknameText = (TextView) findViewById(R.id.relay_details_nickname);
		this.nicknameText.setText(this.details.getNickname());

		this.nicknameEdit = (EditText) findViewById(R.id.nickname_input);
		
		/* Determine node type. */
		if (extras.getBoolean(IS_RELAY.toString())) {
			this.type = NodeType.RELAY;
		} else {
			this.type = NodeType.BRIDGE;
		}
		
		this.fingerprint = this.details.getFingerprint();
		
		/* Set field values through execution of helper method. */
		if (this.details != null) {
			this.setValue(this.fingerprint,
					R.id.relay_details_hash, R.id.relay_details_contact_row);
			this.setValue(this.details.getContact(),
					R.id.relay_details_contact, R.id.relay_details_contact_row);
			this.setValue(this.details.getPlatform(),
					R.id.relay_details_platform,
					R.id.relay_details_platform_row);
			this.setValue(this.details.getAddresses(), R.id.relay_details_ip,
					R.id.relay_details_ip_row);
			this.setValue(this.details.getDirAddress(),
					R.id.relay_details_dir_address,
					R.id.relay_details_dir_address_row);
			this.setValue(this.details.getRestartTimestamp(),
					R.id.relay_details_restart_time,
					R.id.relay_details_restart_time_row);
			this.setValue(this.details.getAdvertisedBandwidth(),
					R.id.relay_details_bandwidth,
					R.id.relay_details_bandwidth_row);
			this.setValue(this.details.getCountry(),
					R.id.relay_details_country, R.id.relay_details_country_row);
			this.setValue(this.details.getPoolAssignment(),
					R.id.relay_details_pool_assignment,
					R.id.relay_details_pool_assignment_row);
			this.setValue(this.details.getAs(), R.id.relay_details_as,
					R.id.relay_details_as_row);
			this.setValue(this.details.getCity(), R.id.relay_details_city,
					R.id.relay_details_city_row);
			if (this.details.hasGeoInformation()) {
				this.setValue(
						Double.toString(this.details.getGeoLatitude())
								+ "/"
								+ Double.toString(this.details
										.getGeoLongitude()),
						R.id.relay_details_geo, R.id.relay_details_geo_row);
			}

			/* Set online state  */
			TextView onlineState = (TextView) findViewById(R.id.relay_details_online);
			if (this.details.isRunning()) {
				this.setValue(getResources().getString(R.string.online), R.id.relay_details_online,
						R.id.relay_details_online_row);
				onlineState.setTextColor(getResources().getColor(
						R.color.online));
			} else {
				this.setValue(getResources().getString(R.string.offline), R.id.relay_details_online,
						R.id.relay_details_online_row);
				onlineState.setTextColor(getResources().getColor(
						R.color.offline));
			}
		}

		String[] family = null;
		if (this.details != null) {
			family = this.details.getFamily();
		}
		if (family != null) {
			MatrixCursor cursor = new MatrixCursor(new String[] { "_id",
					"entry" }, family.length);
			for (int i = 0; i < family.length; i++) {
				cursor.addRow(new String[] { new Integer(i).toString(),
						family[i] });
				Log.i(LogTags.VIEW.toString(), family[i] + " added");
			}
			startManagingCursor(cursor);

			ListView familyList = (ListView) findViewById(R.id.relay_details_family_list);
			String[] from = new String[] { "entry" };
			int[] to = new int[] { R.id.relay_details_family_entry };
			SimpleCursorAdapter relayFamilies = new SimpleCursorAdapter(this,
					R.layout.relay_family_row, cursor, from, to);
			familyList.setAdapter(relayFamilies);
			TableRow row = (TableRow) findViewById(R.id.relay_details_family_row);
			row.setVisibility(View.VISIBLE);
		}

		this.db = new NodesDbAdapter(this);

		this.dbId = this.db.fetchNodeIdByFingerprint(this.fingerprint);

		this.favIcon = (ImageView) findViewById(R.id.bookmark_icon_3);
		this.favIcon.setOnClickListener(new FavIconClickListener(this.db,
				this.dbId, this.details, this.type, this));

		this.editIcon = (ImageView) findViewById(R.id.edit_name_icon);
		this.editIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				editMode = !editMode;
				setGui();
			}
		});

		this.saveIcon = (ImageView) findViewById(R.id.save_icon);
		this.saveIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				details.setNickname(nicknameEdit.getText().toString());
				db.open();
				db.updateNode(dbId, fingerprint, details.getNickname(), type);
				db.close();
				editMode = false;
				setGui();
			}
		});
		this.resetIcon = (ImageView) findViewById(R.id.reset_icon);
		this.resetIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.i("Rest icon",
						"Resetting to " + details.getOriginalNickname());
				details.setNickname(details.getOriginalNickname());
				db.open();
				db.updateNode(dbId, fingerprint, details.getNickname(), type);
				db.close();
				editMode = false;
				setGui();
			}
		});
		setGui();

	}

	private void setValue(String text, int viewId, int rowId) {
		TableRow row = (TableRow) findViewById(rowId);
		if (text != null && !text.equals("") && !text.equals("null")) {
			TextView view = (TextView) findViewById(viewId);
			view.setText(text);
			row.setVisibility(View.VISIBLE);
		}else{
			row.setVisibility(View.GONE);
		}
	}

	private void setValue(Calendar cal, int viewId, int rowId) {
		String calText = null;
		if (cal != null) {
			StringBuilder timeStringBuilder = new StringBuilder(
					DATE_FORMAT.format(cal.getTime()));
			calText = timeStringBuilder.toString();
		}
		this.setValue(calText, viewId, rowId);
	}

	private void setValue(String[] textElements, int viewId, int rowId) {
		String text = null;
		if (textElements != null && textElements.length > 0) {
			boolean first = true;
			for (String next : textElements) {
				if (next != null && !next.equals("") && !next.equals("null")) {
					if (first) {
						text = next;
						first = false;
					} else {
						text += ", " + next;
					}
				}
			}
		}
		this.setValue(text, viewId, rowId);
	}

	private void setGui() {
		if (this.dbId > 0) {
			this.favIcon.setImageResource(R.drawable.bookmark);
			this.editIcon.setVisibility(View.VISIBLE);
			if (this.editMode) {
				this.saveIcon.setVisibility(View.VISIBLE);
			} else {
				this.saveIcon.setVisibility(View.GONE);
			}
			this.resetIcon.setVisibility(View.VISIBLE);
		} else {
			this.favIcon.setImageResource(R.drawable.bookmark_inactive);
			this.editIcon.setVisibility(View.GONE);
			this.saveIcon.setVisibility(View.GONE);
			this.resetIcon.setVisibility(View.GONE);
		}
		if (this.editMode) {
			this.nicknameEdit.setText(details.getNickname());
			this.nicknameEdit.setVisibility(View.VISIBLE);
			this.nicknameText.setVisibility(View.GONE);
			this.editIcon.setImageResource(R.drawable.edit_active);
		} else {
			this.nicknameText.setText(details.getNickname());
			this.nicknameEdit.setVisibility(View.GONE);
			this.nicknameText.setVisibility(View.VISIBLE);
			this.editIcon.setImageResource(R.drawable.edit);
		}
		HorizontalScrollView hScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		hScrollView.fullScroll(ScrollView.FOCUS_LEFT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	public void setDbId(long newDbId) {
		this.dbId = newDbId;
		this.editMode = false;
		this.setGui();
	}

}
