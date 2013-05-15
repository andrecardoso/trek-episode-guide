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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TrekService {
	
	private static final String NUMBERS_SECTION = "0-9";

	private TrekSQLiteRepository trekSQLiteRepository;
	
	private List<Episode> allEpisodes;
	
	private List<Episode> allEpisodesOrderedByTitle;
	
	private List<Episode> allEpisodesOrderedByStardate;
	
	private List<Episode> allEpisodesOrderedByAirdate;
	
	private Map<String, List<Episode>> episodesGroupedByTitle;
	
	private Map<String, List<Episode>> episodesGroupedByStardate;
	
	private Map<String, List<Episode>> episodesGroupedByAirdate;

	public TrekService(TrekSQLiteRepository trekSQLiteRepository) {
		this.trekSQLiteRepository = trekSQLiteRepository;
	}
	
	public boolean hasLoadedEpisodes() {
		return this.allEpisodes != null && !this.allEpisodes.isEmpty();
	}
	
	private void invalidateEpisodes() {
		this.allEpisodes = null;
		
		this.allEpisodesOrderedByTitle = null;
		this.allEpisodesOrderedByStardate = null;
		this.allEpisodesOrderedByAirdate = null;
		
		this.episodesGroupedByTitle = null;
		this.episodesGroupedByStardate = null;
		this.episodesGroupedByAirdate = null;
	}
	
	public void loadAllEpisodes() {
		this.invalidateEpisodes();
		
		this.allEpisodes = this.trekSQLiteRepository.getAllEpisodes();
		
		if (this.hasLoadedEpisodes()) {
			this.sortEpisodesByTitle();
			this.sortEpisodesByStardate();
			this.sortEpisodesByAirdate();
			
			this.groupEpisodesOrderedByTitle(this.allEpisodesOrderedByTitle);
			this.groupEpisodesOrderedByStardate(this.allEpisodesOrderedByStardate);
			this.groupEpisodesOrderedByAirdate(this.allEpisodesOrderedByAirdate);
		}
	}
	
	public int findIndexForEpisodeId(EpisodeListOrder listOrder, int id) {
		List<Episode> episodes = this.getAllEpisodes(listOrder);
		
		if (episodes != null) {
			for (int i = 0; i < episodes.size(); i++) {
				Episode episode = episodes.get(i);
				if (id == episode.getId()) {
					return i;
				}
			}
		}
		
		return 0;
	}
	
	public Integer findIdForEpisodeCode(String code) {
		if (this.hasLoadedEpisodes()) {
			for (Episode episode : this.allEpisodes) {
				if (code.equals(episode.getCode())) {
					return episode.getId();
				}
			}
		}
		return null;
	}
	
	public String getEpisodeDescription(int id) {
		return this.trekSQLiteRepository.getEpisodeDescription(id);
	}
	
	public List<Episode> getAllEpisodes(EpisodeListOrder listOrder) {
		if (!this.hasLoadedEpisodes()) {
			this.loadAllEpisodes();
		}
		
		if (listOrder == EpisodeListOrder.TITLE) {
			return this.sortEpisodesByTitle();
		}
		
		if (listOrder == EpisodeListOrder.STARDATE) {
			return this.sortEpisodesByStardate();
		}
		
		if (listOrder == EpisodeListOrder.AIRDATE) {
			return this.sortEpisodesByAirdate();
		}
		
		return this.sortEpisodesByTitle();
	}

	public Map<String, List<Episode>> getAllEpisodesGroupedByListOrder(EpisodeListOrder listOrder) {
		List<Episode> episodes = this.getAllEpisodes(listOrder);
		
		if (listOrder == EpisodeListOrder.TITLE) {
			return this.groupEpisodesOrderedByTitle(episodes);
		}
		
		if (listOrder == EpisodeListOrder.STARDATE) {
			return this.groupEpisodesOrderedByStardate(episodes);
		}
		
		if (listOrder == EpisodeListOrder.AIRDATE) {
			return this.groupEpisodesOrderedByAirdate(episodes);
		}
		
		return this.groupEpisodesOrderedByTitle(episodes);
	}
	
	private List<Episode> sortEpisodesByTitle() {
		if (this.allEpisodesOrderedByTitle != null) {
			return this.allEpisodesOrderedByTitle;
		}
		
		this.allEpisodesOrderedByTitle = new ArrayList<Episode>(this.allEpisodes);
		
		Collections.sort(this.allEpisodesOrderedByTitle, new Comparator<Episode>() {
			public int compare(Episode lhs, Episode rhs) {
				return lhs.getTitleSort().compareTo(rhs.getTitleSort());
			}
		});
		
		return this.allEpisodesOrderedByTitle;
	}
	
	private List<Episode> sortEpisodesByStardate() {
		if (this.allEpisodesOrderedByStardate != null) {
			return this.allEpisodesOrderedByStardate;
		}
		
		this.allEpisodesOrderedByStardate = new ArrayList<Episode>(this.allEpisodes);
		
		Collections.sort(this.allEpisodesOrderedByStardate, new Comparator<Episode>() {
			public int compare(Episode lhs, Episode rhs) {
				return lhs.getStardateSort().compareTo(rhs.getStardateSort());
			}
		});
		
		return this.allEpisodesOrderedByStardate;
	}
	
	private List<Episode> sortEpisodesByAirdate() {
		if (this.allEpisodesOrderedByAirdate != null) {
			return this.allEpisodesOrderedByAirdate;
		}
		
		this.allEpisodesOrderedByAirdate = new ArrayList<Episode>(this.allEpisodes);
		
		Collections.sort(this.allEpisodesOrderedByAirdate, new Comparator<Episode>() {
			public int compare(Episode lhs, Episode rhs) {
				return lhs.getAirdateSort().compareTo(rhs.getAirdateSort());
			}
		});
		
		return this.allEpisodesOrderedByAirdate;
	}

	private Map<String, List<Episode>> groupEpisodesOrderedByTitle(List<Episode> episodes) {
		if (this.episodesGroupedByTitle != null) {
			return this.episodesGroupedByTitle;
		}
		
		Map<String, List<Episode>> episodesOrderedByTitle = new LinkedHashMap<String, List<Episode>>();
		
		for (Episode episode : episodes) {
			String section;
			
			if (episode.getTitleFirstLetterIsANumber()) {
				section = NUMBERS_SECTION;
			} else {
				section = episode.getTitleFirstLetter();
			}
			
			this.addEpisodeToSection(episodesOrderedByTitle, episode, section);
		}
		
		this.episodesGroupedByTitle = episodesOrderedByTitle;
		
		return episodesOrderedByTitle;
	}
	
	private Map<String, List<Episode>> groupEpisodesOrderedByStardate(List<Episode> episodes) {
		if (this.episodesGroupedByStardate != null) {
			return this.episodesGroupedByStardate;
		}
		
		Map<String, List<Episode>> episodesOrderedByStardate = new LinkedHashMap<String, List<Episode>>();
		
		for (Episode episode : episodes) {
			this.addEpisodeToSection(episodesOrderedByStardate, episode, episode.getStardateSection());
		}
		
		this.episodesGroupedByStardate = episodesOrderedByStardate;
		
		return episodesOrderedByStardate;
	}
	
	private Map<String, List<Episode>> groupEpisodesOrderedByAirdate(List<Episode> episodes) {
		if (this.episodesGroupedByAirdate != null) {
			return this.episodesGroupedByAirdate;
		}
		
		Map<String, List<Episode>> episodesOrderedByAirdate = new LinkedHashMap<String, List<Episode>>();
		
		for (Episode episode : episodes) {
			this.addEpisodeToSection(episodesOrderedByAirdate, episode, episode.getAirdateSection());
		}
		
		this.episodesGroupedByAirdate = episodesOrderedByAirdate;
		
		return episodesOrderedByAirdate;
	}

	private void addEpisodeToSection(Map<String, List<Episode>> episodesGroupedBySection, Episode episode, String section) {
		if (section != null) {
			List<Episode> episodesWithLetter = episodesGroupedBySection.get(section);
			if (episodesWithLetter == null) {
				episodesWithLetter = new ArrayList<Episode>();
			}
			episodesWithLetter.add(episode);
			episodesGroupedBySection.put(section, episodesWithLetter);
		}
	}
	
}
