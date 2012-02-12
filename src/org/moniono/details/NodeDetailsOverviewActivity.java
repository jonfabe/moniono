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

public class NodeDetailsOverviewActivity extends Activity {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private TextView mFingerprintView;
	private TextView mNicknameView;
	private String mFingerprint;
	private DetailsData relayData;
	private NodeType mType;
	private ImageView favIcon;
	private ImageView editIcon;
	private ImageView saveIcon;
	private ImageView resetIcon;
	private EditText nicknameEdit;

	private long mDbId = -1;

	private NodesDbAdapter dBase = null;

	private boolean editMode = false;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.dBase = new NodesDbAdapter(this);
		setContentView(R.layout.relay_details);
		Bundle extras = getIntent().getExtras();

		
		relayData = (DetailsData) extras.get(NODE_DATA.toString());
		
		
		
		this.mNicknameView = (TextView) findViewById(R.id.relay_details_nickname);
		this.mNicknameView.setText(relayData.getNickname());
		
		if (extras.getBoolean(IS_RELAY.toString())) {
			this.mType = NodeType.RELAY;
		} else {
			this.mType = NodeType.BRIDGE;
		}
		this.nicknameEdit = (EditText) findViewById(R.id.nickname_input);

		this.mFingerprint = relayData.getFingerprint();
		this.mFingerprintView = (TextView) findViewById(R.id.relay_details_hash);
		this.mFingerprintView.setText(this.mFingerprint.substring(0, 20));
		if(relayData != null){ 
			this.setValue(relayData.getContact(), R.id.relay_details_contact, R.id.relay_details_contact_row);
			this.setValue(relayData.getPlatform(), R.id.relay_details_platform, R.id.relay_details_platform_row);
			this.setValue(relayData.getAddresses(), R.id.relay_details_ip, R.id.relay_details_ip_row);
			this.setValue(relayData.getDirAddress(), R.id.relay_details_dir_address, R.id.relay_details_dir_address_row);
			this.setValue(relayData.getRestartTimestamp(), R.id.relay_details_restart_time, R.id.relay_details_restart_time_row);
			this.setValue(relayData.getAdvertisedBandwidth(), R.id.relay_details_bandwidth, R.id.relay_details_bandwidth_row);
			this.setValue(relayData.getCountry(), R.id.relay_details_country, R.id.relay_details_country_row);
			this.setValue(relayData.getPoolAssignment(), R.id.relay_details_pool_assignment, R.id.relay_details_pool_assignment_row);
		
			if(relayData.isRunning()){
				this.mNicknameView.setTextColor(getResources().getColor(R.color.online));
			}else{
				this.mNicknameView.setTextColor(getResources().getColor(R.color.offline));
			}
		}
		
		String[] family = null;
		if (relayData != null) {
			family = relayData.getFamily();
		}
		if (family != null) {
			MatrixCursor cursor = new MatrixCursor(new String[] { "_id", "entry" },
					family.length);
			Log.i(LogTags.VIEW.toString(), family.length+" family entries");
			for(int i = 0; i < family.length;i++){
				cursor.addRow(new String[]{new Integer(i).toString(),family[i]});
				Log.i(LogTags.VIEW.toString(),family[i]+" added");
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

		this.mDbId = this.dBase.fetchNodeIdByFingerprint(this.mFingerprint);

		this.favIcon = (ImageView) findViewById(R.id.bookmark_icon_3);
		this.favIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dBase.open();
				if (mDbId > 0) {
					dBase.open();
					dBase.deleteNode(mDbId);
					dBase.close();
					mDbId = -1;
				} else {
					mDbId = dBase.createNode(mFingerprint, relayData.getNickname(),
							mType.getIdentifier());
				}
				dBase.close();
				editMode = false;
				setGui();
			}
		});

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
				relayData.setNickname(nicknameEdit.getText().toString());
				dBase.open();
				dBase.updateNode(mDbId, mFingerprint, relayData.getNickname(), mType);
				dBase.close();
				editMode = false;
				setGui();
			}
		});
		this.resetIcon = (ImageView) findViewById(R.id.reset_icon);
		this.resetIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.i("Rest icon","Resetting to "+relayData.getOriginalNickname());
				relayData.setNickname(relayData.getOriginalNickname());
				dBase.open();
				dBase.updateNode(mDbId, mFingerprint, relayData.getNickname(), mType);
				dBase.close();
				editMode = false;
				setGui();
			}
		});
		setGui();

	}
	
	private void setValue(String text,int viewId, int rowId){
		if(text != null && !text.equals("") && !text.equals("null")){
			TextView view = (TextView) findViewById(viewId);
			view.setText(text);
			TableRow row = (TableRow) findViewById(rowId);
			row.setVisibility(View.VISIBLE);
		}
	}
	
	private void setValue(Calendar cal,int viewId, int rowId){
		String calText = null;
		if(cal != null){
			StringBuilder timeStringBuilder = new StringBuilder( DATE_FORMAT.format( cal.getTime() ) );
			calText = timeStringBuilder.toString();
		}
		this.setValue(calText, viewId, rowId);
	}
	
	private void setValue(String[] textElements,int viewId, int rowId){
		String text = null;
		if(textElements != null && textElements.length > 0){
			boolean first = true;
			for(String next:textElements){
				if(next != null && !next.equals("") && !next.equals("null")){
					if(first){
						text = next;
						first = false;
					}else{
						text += ", "+next;
					}
				}
			}
		}
		this.setValue(text, viewId, rowId);
	}
	
	private void setGui() {
		if (this.mDbId > 0) {
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
		if(this.editMode){
			this.nicknameEdit.setText(relayData.getNickname());
			this.nicknameEdit.setVisibility(View.VISIBLE);
			this.mNicknameView.setVisibility(View.GONE);
		}else{
			this.mNicknameView.setText(relayData.getNickname());
			this.nicknameEdit.setVisibility(View.GONE);
			this.mNicknameView.setVisibility(View.VISIBLE);
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

}
