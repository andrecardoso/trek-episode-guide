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
package com.appglu.trekepisodeguide.model;

import java.io.Serializable;
import java.util.Locale;

import com.appglu.impl.util.StringUtils;

public class Episode implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private String code;

	private String stardateSection;

	private String stardateDisplay;

	private Float stardateSort;

	private String titleSort;

	private String titleDisplay;

	private String airdateSection;

	private String airdateDisplay;

	private Long airdateSort;

	private String description;

	private String gridImageUrl;

	private String thumbnailUrl1;

	private String thumbnailUrl2;

	private String thumbnailUrl3;

	private String imageUrl1;

	private String imageUrl2;

	private String imageUrl3;
	
	public Episode() {
		
	}
	
	public Episode(int id) {
		this.id = id;
	}

	public boolean getTitleFirstLetterIsANumber() {
		if (titleSort == null || titleSort.length() == 0) {
			return false;
		}
		return Character.isDigit(titleSort.charAt(0));
	}
	
	public boolean getTitleFirstLetterIsALetter() {
		if (titleSort == null || titleSort.length() == 0) {
			return false;
		}
		return Character.isLetter(titleSort.charAt(0));
	}

	public String getTitleFirstLetter() {
		if (titleSort == null || titleSort.length() == 0) {
			return null;
		}
		return titleSort.substring(0, 1).toUpperCase(Locale.US);
	}
	
	public boolean hasImageUrlAtIndex(int index) {
		return StringUtils.isNotEmpty(this.getImageUrlAtIndex(index));
	}

	public String getImageUrlAtIndex(int index) {
		if (index == 0) {
			return getImageUrl1();
		}
		if (index == 1) {
			return getImageUrl2();
		}
		if (index == 2) {
			return getImageUrl3();
		}
		return null;
	}
	
	public boolean hasThumbnailUrlAtIndex(int index) {
		return StringUtils.isNotEmpty(this.getThumbnailUrlAtIndex(index));
	}
	
	public String getThumbnailUrlAtIndex(int index) {
        if (index == 0) {
            return getThumbnailUrl1();
        }
        if (index == 1) {
            return getThumbnailUrl2();
        }
        if (index == 2) {
            return getThumbnailUrl3();
        }
        return null;
    }
	
	public boolean isValid() {
		if (!this.getTitleFirstLetterIsANumber() && !this.getTitleFirstLetterIsALetter()) {
			return false;
		}
		if (this.getStardateSort() == 0f) {
			return false;
		}
		if (this.getAirdateSort() == 0l) {
			return false;
		}
		return true;
	}
	
	private String[] getCodeInParts() {
		if (StringUtils.isEmpty(this.code)) {
			return null;
		}
		
		String[] parts = this.code.split("\\s");
		
		if (parts.length != 3) {
			return null;
		}
		return parts;
	}
	
	public String getSeries() {
		String[] parts = this.getCodeInParts();
		return parts[0];
	}
	
	public String getSeason() {
		String[] parts = this.getCodeInParts();
		return parts[1];
	}

	public String getEpisode() {
		String[] parts = this.getCodeInParts();
		return parts[2];
	}

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getStardateSection() {
		return stardateSection;
	}

	public String getStardateDisplay() {
		return stardateDisplay;
	}

	public Float getStardateSort() {
		if (stardateSort == null) {
			return 0f;
		}
		return stardateSort;
	}

	public String getTitleSort() {
		if (titleSort == null) {
			return "";
		}
		return titleSort;
	}

	public String getTitleDisplay() {
		return titleDisplay;
	}

	public String getAirdateSection() {
		return airdateSection;
	}

	public String getAirdateDisplay() {
		return airdateDisplay;
	}

	public Long getAirdateSort() {
		if (airdateSort == null) {
			return 0l;
		}
		return airdateSort;
	}

	public String getDescription() {
		return description;
	}

	public String getGridImageUrl() {
		return gridImageUrl;
	}

	public String getThumbnailUrl1() {
		return thumbnailUrl1;
	}

	public String getThumbnailUrl2() {
		return thumbnailUrl2;
	}

	public String getThumbnailUrl3() {
		return thumbnailUrl3;
	}

	public String getImageUrl1() {
		return imageUrl1;
	}

	public String getImageUrl2() {
		return imageUrl2;
	}

	public String getImageUrl3() {
		return imageUrl3;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setStardateSection(String stardateSection) {
		this.stardateSection = stardateSection;
	}

	public void setStardateDisplay(String stardateDisplay) {
		this.stardateDisplay = stardateDisplay;
	}

	public void setStardateSort(Float stardateSort) {
		this.stardateSort = stardateSort;
	}

	public void setTitleSort(String titleSort) {
		this.titleSort = titleSort;
	}

	public void setTitleDisplay(String titleDisplay) {
		this.titleDisplay = titleDisplay;
	}

	public void setAirdateSection(String airdateSection) {
		this.airdateSection = airdateSection;
	}

	public void setAirdateDisplay(String airdateDisplay) {
		this.airdateDisplay = airdateDisplay;
	}

	public void setAirdateSort(Long airdateSort) {
		this.airdateSort = airdateSort;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGridImageUrl(String gridImageUrl) {
		this.gridImageUrl = gridImageUrl;
	}

	public void setThumbnailUrl1(String thumbnailUrl1) {
		this.thumbnailUrl1 = thumbnailUrl1;
	}

	public void setThumbnailUrl2(String thumbnailUrl2) {
		this.thumbnailUrl2 = thumbnailUrl2;
	}

	public void setThumbnailUrl3(String thumbnailUrl3) {
		this.thumbnailUrl3 = thumbnailUrl3;
	}

	public void setImageUrl1(String imageUrl1) {
		this.imageUrl1 = imageUrl1;
	}

	public void setImageUrl2(String imageUrl2) {
		this.imageUrl2 = imageUrl2;
	}

	public void setImageUrl3(String imageUrl3) {
		this.imageUrl3 = imageUrl3;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Episode other = (Episode) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Episode [" + titleDisplay + "]";
	}

}