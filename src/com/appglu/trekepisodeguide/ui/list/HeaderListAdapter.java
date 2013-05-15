/*******************************************************************************
 * Copyright 2013 AppGlu, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.appglu.trekepisodeguide.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appglu.trekepisodeguide.R;

public class HeaderListAdapter extends ArrayAdapter<String> {

	private LayoutInflater layoutInflater;
	
	public HeaderListAdapter(Context context) {
		super(context, R.layout.episode_list_header);
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.episode_list_header, parent, false);
			
			holder.title = (TextView) convertView.findViewById(R.id.episode_header_title);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(this.getItem(position));
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
	}

}