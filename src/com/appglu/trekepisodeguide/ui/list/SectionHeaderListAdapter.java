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

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

public class SectionHeaderListAdapter extends BaseAdapter implements SectionIndexer {
	
	public final static int TYPE_SECTION_HEADER = 0;
	public final static int TYPE_LIST_ITEM = 1;

	private final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	private final ArrayAdapter<String> headerAdapter;
	
	public SectionHeaderListAdapter(Context context, ArrayAdapter<String> headerAdapter) {
		this.headerAdapter = headerAdapter;
	}

	public void addSection(String section, Adapter adapter) {
		this.headerAdapter.add(section);
		this.sections.put(section, adapter);
	}
	
	public void clear() {
		this.headerAdapter.clear();
		this.sections.clear();
	}

	@Override
	public Object getItem(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0) {
				return section;
			}
			if ((position > 0) && (position < size)) {
				return adapter.getItem(position - 1);
			}  

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	@Override
	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values()) {
			total += adapter.getCount() + 1;
		}
		return total;
	}

	@Override
	public int getViewTypeCount() {
		// assume that headers and items have only one layout each
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0) {
				return TYPE_SECTION_HEADER;
			}
			if (position < size) {
				return TYPE_LIST_ITEM;
			}
			
			// otherwise jump into next section
			position -= size;
		}
		return -1;
	}

	@Override
	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionIndex = 0;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return headerAdapter.getView(sectionIndex, convertView, parent);
			if (position < size)
				return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionIndex++;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[headerAdapter.getCount()];
		
		for (int i = 0; i < headerAdapter.getCount(); i++) {
			sections[i] = headerAdapter.getItem(i);
		}
		
		return sections;
	}
	
	@Override
	public int getPositionForSection(int sectionIndex) {
		int position = 0;
		
		//If the section is out of bounds, it must be clipped to fall within the size of the list
		if (sectionIndex <= 0) {
			return 0;
		}
		if (sectionIndex >= headerAdapter.getCount()) {
			return getCount() - 1;
		}
		
		String letter = headerAdapter.getItem(sectionIndex);
		
		for (String section : this.sections.keySet()) {
			if (section.equals(letter)) {
				break;
			}
			
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			position += size;
		}
		
		return position;
	}

	@Override
	public int getSectionForPosition(int position) {
		//If the position is out of bounds, it must be clipped to fall within the size of the list
		if (position <= 0) {
			return 0;
		}
		if (position >= this.getCount()) {
			return headerAdapter.getCount() - 1;
		}
		
		int sectionIndex = 0;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			if (position < size) {
				break;
			}

			position -= size;
			sectionIndex++;
		}
		return sectionIndex;
	}

}
