package org.moniono.details.overview;

import org.moniono.db.NodeType;
import org.moniono.db.NodesDbAdapter;
import org.moniono.details.NodeDetailsOverviewActivity;
import org.moniono.search.DetailsData;

import android.view.View;
import android.view.View.OnClickListener;

public class FavIconClickListener implements OnClickListener {

	/**
	 * Connection to database (abstraction).
	 */
	private NodesDbAdapter db = null;

	/**
	 * The unique identifier of the node within the favorite nodes table, that
	 * is, its primary key.
	 */
	private long dbId = -1;

	/**
	 * Detail information of the currently displayed node.
	 */
	private DetailsData details;

	/**
	 * Type of the currently displayed node.
	 */
	private NodeType type;

	/**
	 * Reference to corresponding activity.
	 */
	private NodeDetailsOverviewActivity activity;

	public FavIconClickListener(NodesDbAdapter iniDb, long iniDbId,
			DetailsData iniDetails, NodeType iniType,
			NodeDetailsOverviewActivity iniActivity) {
		this.db = iniDb;
		this.dbId = iniDbId;
		this.details = iniDetails;
		this.type = iniType;
		this.activity = iniActivity;
	}

	public void onClick(@SuppressWarnings("unused") View v) {
		this.db.open();
		if (this.dbId > 0) {
			this.db.open();
			this.db.deleteNode(this.dbId);
			this.db.close();
			this.dbId = -1;
		} else {
			this.dbId = this.db.createNode(this.details.getFingerprint(),
					this.details.getNickname(), this.type.getIdentifier());
		}
		this.db.close();
		this.activity.setDbId(this.dbId);
	}

}
