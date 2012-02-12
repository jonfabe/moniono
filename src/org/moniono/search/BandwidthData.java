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

import static org.moniono.util.CommonConstants.CHART_URL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moniono.search.BandwidthIntervalTimespan.TimespanType;
import org.moniono.util.BandwidthUtil;
import org.moniono.util.JSONUtil;
import org.moniono.util.LogTags;

import android.util.Log;

public class BandwidthData implements Serializable {

	private class ImageRequestor implements Runnable {

		private BandwidthHistory readHistory;
		private BandwidthHistory writeHistory;
		private Calendar start;
		private Calendar end;
		private byte[] chartData;

		private ImageRequestor(BandwidthHistory iniReadHistory,
				BandwidthHistory iniWriteHistory) {
			if(iniReadHistory == null && iniWriteHistory == null){
				throw new IllegalArgumentException("At least read or write history must be set.");
			}
			this.readHistory = iniReadHistory;
			this.writeHistory = iniWriteHistory;
			if (this.readHistory == null && this.writeHistory != null) {
				this.start = this.writeHistory.getFirstTimestamp();
				this.end = this.writeHistory.getLastTimestamp();
			} else if (this.writeHistory == null && this.readHistory != null) {
				this.start = this.readHistory.getFirstTimestamp();
				this.end = this.readHistory.getLastTimestamp();
			} else if(this.readHistory != null && this.writeHistory != null){
				Calendar readStart = this.readHistory.getFirstTimestamp();
				Calendar readEnd = this.readHistory.getLastTimestamp();
				Calendar writeStart = this.writeHistory.getFirstTimestamp();
				Calendar writeEnd = this.writeHistory.getLastTimestamp();
				this.start = readStart.before(writeStart) ? readStart
						: writeStart;
				this.end = readEnd.after(writeEnd) ? readEnd : writeEnd;
			}
		}

		public void run() {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					CHART_URL);

			try {
				/* Preparation of data structure for parameters. */
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						13);

				/* Visible axes: chxt=<axis_1>,... */
				NameValuePair chxt = new BasicNameValuePair("chxt", "x,y");
				nameValuePairs.add(chxt);

				/* Chart Type: cht=<width>x<height> */
				NameValuePair cht = new BasicNameValuePair("cht", "lxy");
				nameValuePairs.add(cht);

				/*
				 * Scale for text format with custom range: chds=
				 * <series_1_min>,<series_1_max>,...
				 */
				NameValuePair chds = new BasicNameValuePair("chds", "a");
				nameValuePairs.add(chds);

				/* Chart size: chs=<width>x<height> */
				NameValuePair chs = new BasicNameValuePair("chs", "750x375");
				nameValuePairs.add(chs);

				/*
				 * Series colors: chco=
				 * <series_1_element_1>|...|<series_1_element_n>,
				 * <series_2>,...,<series_m>
				 */
				NameValuePair chco = new BasicNameValuePair("chco",
						"FF0000,00FF00");
				nameValuePairs.add(chco);

				/*
				 * Chart legend text and style: chdl=
				 * <data_series_1_label>|...|<data_series_n_label>
				 */
				NameValuePair chdl = new BasicNameValuePair("chdl",
						"Read|Write");
				nameValuePairs.add(chdl);

				/*
				 * Chart legend text and style:
				 * chdlp=<opt_position>|<opt_label_order>
				 */
				NameValuePair chdlp = new BasicNameValuePair("chdlp", "b");
				nameValuePairs.add(chdlp);

				/*
				 * Line styles: chls=
				 * <line_1_thickness>,<opt_dash_length>,<opt_space_length>|...
				 */
				NameValuePair chls = new BasicNameValuePair("chls", "1|1");
				nameValuePairs.add(chls);

				/*
				 * Chart Margins: chma=
				 * <left_margin>,<right_margin>,<top_margin>
				 * ,<bottom_margin>|<opt_legend_width>,<opt_legend_height>
				 */
				NameValuePair chma = new BasicNameValuePair("chma", "5,5,5,25");
				nameValuePairs.add(chma);

				long diff = this.end.getTimeInMillis()
						- this.start.getTimeInMillis();

				/*
				 * Axis label positions: <axis_index>,<label_1_position>,
				 * ...,<label_n_position>
				 */
				NameValuePair chxp = new BasicNameValuePair("chxp", "0,0,"
						+ diff);
				nameValuePairs.add(chxp);

				Double[] readValues = this.readHistory.getValues();
				long firstRead = this.readHistory.getFirstTimestamp()
						.getTimeInMillis();
				long lastRead = this.readHistory.getLastTimestamp()
						.getTimeInMillis();
				long interval = (lastRead - firstRead) / readValues.length;
				long readPosition = firstRead - this.start.getTimeInMillis();
				long max = 0;
				String readXString = "";
				String readYString = "";
				boolean first = true;
				for (Double value : readValues) {
					if (!first) {
						readXString += ",";
						readYString += ",";
					} else {
						first = false;
					}
					readXString += readPosition;
					readPosition += interval;
					if (value == null) {
						readYString += "_";
					} else {
						readYString += value.longValue();
						if (value.longValue() > max) {
							max = value.longValue();
						}
					}
				}

				Double[] writeValues = this.writeHistory.getValues();
				long firstWrite = this.writeHistory.getFirstTimestamp()
						.getTimeInMillis();
				long lastWrite = this.writeHistory.getLastTimestamp()
						.getTimeInMillis();
				long writeInterval = (lastWrite - firstWrite)
						/ writeValues.length;
				long writePosition = firstWrite - this.start.getTimeInMillis();
				String writeXString = "";
				String writeYString = "";
				boolean isFirstWrite = true;
				for (Double value : writeValues) {
					if (!isFirstWrite) {
						writeXString += ",";
						writeYString += ",";
					} else {
						isFirstWrite = false;
					}
					writeXString += writePosition;
					writePosition += writeInterval;
					if (value == null) {
						writeYString += "_";
					} else {
						writeYString += value.longValue();
						if (value.longValue() > max) {
							max = value.longValue();
						}
					}
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"dd.MM.yyyy HH:mm");
				/* Axis labels: <axis_index>:|<label_1>|... */
				NameValuePair chxl = new BasicNameValuePair("chxl", "0:|"
						+ dateFormat.format(this.start.getTime()) + "|"
						+ dateFormat.format(this.end.getTime()) + "|" + "1:|"
						+ 0 + "|" + BandwidthUtil.getBandwidthString(max / 4)
						+ "|" + BandwidthUtil.getBandwidthString(max / 2) + "|"
						+ BandwidthUtil.getBandwidthString((max / 4) * 3) + "|"
						+ BandwidthUtil.getBandwidthString(max));
				nameValuePairs.add(chxl);

				/*
				 * Axis ranges:
				 * chxr=<axis_index>,<start_val>,<end_val>,<opt_step>|...
				 */
				NameValuePair chxr = new BasicNameValuePair("chxr", "0,0,"
						+ diff + "|1,0," + max);
				nameValuePairs.add(chxr);

				/* Chart data string: chd= t:s,e,r,i,e,s,1|s,e,r,i,e,s,2|... */
				NameValuePair chd = new BasicNameValuePair("chd", "t:"
						+ readXString + "|" + readYString + "|" + writeXString
						+ "|" + writeYString);
				nameValuePairs.add(chd);

				String params = "?";
				for (NameValuePair next : nameValuePairs) {
					params += next.getName() + "=" + next.getValue() + "&";
				}
				Log.v("Params", params);

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				InputStream is = response.getEntity().getContent();
				byte[] buffer = new byte[8192];
				int bytesRead;
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				while ((bytesRead = is.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
				}
				this.chartData = output.toByteArray();

			} catch (ClientProtocolException e) {
				Log.e("Google Chart API", "Error requesting image.", e);
			} catch (IOException e) {
				Log.e("Google Chart API", "Error requesting image.", e);
			}
		}

		public byte[] getChartData() {
			if (this.chartData == null) {
				this.run();
			}
			return this.chartData;
		}

	}

	private static final String READ_HISTORY_KEY = "read_history";
	private static final String WRITE_HISTORY_KEY = "write_history";

	private static final String FIRST_KEY = "first";
	private static final String LAST_KEY = "last";
	private static final String FACTOR_KEY = "factor";
	private static final String VALUES_KEY = "values";

	private static final String READ_COLOR = "3072F3";
	private static final String WRITE_COLOR = "FF0000";

	private static final String NULL_VALUE = "null";

	private String fingerprint;
	private Calendar validUntil;

	private TreeMap<BandwidthIntervalTimespan, BandwidthHistory> writeHistory;
	private TreeMap<BandwidthIntervalTimespan, BandwidthHistory> readHistory;
	private TreeMap<BandwidthIntervalTimespan, byte[]> charts;

	public BandwidthData(JSONObject jsonDetails, String hash,
			Calendar iniValidUntil) {
		this.fingerprint = hash;
		this.validUntil = iniValidUntil;
		this.readHistory = new TreeMap<BandwidthIntervalTimespan, BandwidthHistory>();
		this.writeHistory = new TreeMap<BandwidthIntervalTimespan, BandwidthHistory>();
		this.charts = new TreeMap<BandwidthIntervalTimespan, byte[]>();
		if (jsonDetails.has(READ_HISTORY_KEY)) {
			try {
				JSONObject jsonReadHistories = jsonDetails
						.getJSONObject(READ_HISTORY_KEY);
				this.processHistory(jsonReadHistories, true);
			} catch (JSONException e) {
				Log.w(LogTags.JSON_PROCESSING.toString(),
						"Exception processing read history of node bandwidth data.",
						e);
			}
		}
		if (jsonDetails.has(WRITE_HISTORY_KEY)) {
			try {
				JSONObject jsonWriteHistories = jsonDetails
						.getJSONObject(WRITE_HISTORY_KEY);
				this.processHistory(jsonWriteHistories, false);
			} catch (JSONException e) {
				Log.w(LogTags.JSON_PROCESSING.toString(),
						"Exception processing write history of node bandwidth data.",
						e);
			}
		}
		for (BandwidthIntervalTimespan next : getTimespans()) {
			ImageRequestor requestor = new ImageRequestor(
					this.readHistory.get(next),
					this.writeHistory.get(next));
			byte[] result = requestor.getChartData();
			this.charts.put(next, result);
		}
		Log.v(LogTags.NETWORK.toString(), "Charts created.");
	}

	private final void processHistory(JSONObject histories, boolean isRead) {
		Iterator<String> keys = histories.keys();
		String nextKey = null;
		
		while (keys.hasNext()) {
			nextKey = keys.next();
			BandwidthIntervalTimespan timespan = getTimespan(nextKey);
			this.charts.put(timespan, null);
			if (timespan != null) {
				try {
					JSONObject jsonHistory;
					jsonHistory = histories.getJSONObject(nextKey);
					BandwidthHistory nextHistory = new BandwidthHistory();
					nextHistory.setTimespan(timespan);
					nextHistory.setFirstTimestamp(JSONUtil.getCalendar(
							FIRST_KEY, null, jsonHistory));
					nextHistory.setLastTimestamp(JSONUtil.getCalendar(LAST_KEY,
							null, jsonHistory));
					double factor = JSONUtil.getDouble(FACTOR_KEY, 1,
							jsonHistory);
					JSONArray jsonValues = jsonHistory.getJSONArray(VALUES_KEY);
					Double[] values = new Double[jsonValues.length()];
					for (int i = 0; i < values.length; i++) {
						String value = jsonValues.getString(i);
						Double dValue = null;
						if (!value.equals(NULL_VALUE)) {
							dValue = Double.parseDouble(value) * factor;
						}
						values[i] = dValue;
					}
					nextHistory.setValues(values);
					if (isRead) {
						this.readHistory.put(timespan, nextHistory);
					} else {
						this.writeHistory.put(timespan, nextHistory);
					}
				} catch (JSONException e) {
					Log.w(LogTags.JSON_PROCESSING.toString(),
							"Exception processing write history of node bandwidth data.",
							e);
				}
			}
		}
	}

	private static final BandwidthIntervalTimespan getTimespan(String key) {
		String[] keyParts = key.split("_");
		if (keyParts.length != 2) {
			return null;
		}
		String quantity = keyParts[0];
		String type = keyParts[1];

		BandwidthIntervalTimespan timespan = new BandwidthIntervalTimespan(
				TimespanType.valueOfString(type), Integer.parseInt(quantity));
		return timespan;
	}

	public boolean isValid() {
		Calendar now = new GregorianCalendar();
		return this.validUntil != null && now.compareTo(this.validUntil) <= 0;
	}

	public Set<BandwidthIntervalTimespan> getTimespans() {
		return this.charts.keySet();
	}

	public byte[] getChart(BandwidthIntervalTimespan timespan) {
		byte[] result = this.charts.get(timespan);
		if (result == null) {
			ImageRequestor requestor = new ImageRequestor(
					this.readHistory.get(timespan),
					this.writeHistory.get(timespan));
			result = requestor.getChartData();
			this.charts.put(timespan, result);
		}
		return result;
	}

}
