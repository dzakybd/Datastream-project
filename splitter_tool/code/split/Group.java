package split;

import java.util.*;

/*
 * A group of places.
 * */
public class Group {

	int id; // group id, starting from 0

	static int mToAssignGroupId = 0;
	
	List<Integer> placeIds = null;

	public Group() {
		this.id = mToAssignGroupId++;
		this.placeIds = new ArrayList<Integer>();
	}

	public Group(List<Integer> places) {
		this.id = mToAssignGroupId++;
		this.placeIds = places;
	}
	
	public Group(int groupId) {
		this.id = groupId;
		this.placeIds = new ArrayList<Integer>();
	}
	
	public Group(int groupId, List<Integer> places) {
		this.id = groupId;
		this.placeIds = places;
	}

	public int size() {
		return placeIds.size();
	}

	public int getId() {
		return id;
	}

	public List<Integer> getPlaceIds() {
		return placeIds;
	}

	public void addPlace(int placeId) {
		placeIds.add(placeId);
	}

	public void clear() {
		placeIds = new ArrayList<Integer>();
	}

	/*
	 * Split all groups by the categories of places.
	 */
	public List<Group> splitByCategory(Places places, int level) {

		Map<Integer, Group> subgroups = new HashMap<Integer, Group>();

		for (Integer placeId : placeIds) {

			Place place = places.getPlace(placeId);
			int categoryId = getCategoryId(place, level);

			if (subgroups.containsKey(categoryId)) {
				subgroups.get(categoryId).addPlace(placeId);
			} else {
				Group newGroup = new Group();
				newGroup.addPlace(placeId);
				subgroups.put(categoryId, newGroup);
			}
		}

		return convertToList(subgroups);
	}

	private int getCategoryId(Place place, int level) {
		int maxLevel = place.getMaxCategoryLevel();
		int possibleLevel = level < maxLevel ? level : maxLevel;
		return place.getCategory(possibleLevel);
	}

	private List<Group> convertToList(Map<Integer, Group> map) {
		List<Group> result = new ArrayList<Group>();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			result.add((Group) entry.getValue());
		}
		return result;
	}

	public double getVariance(Places places) {
		Place mean = getMean(places);
		double variance = 0;
		for (Integer placeId : placeIds) {
			Place place = places.getPlace(placeId);
			double latDiff = place.getLat() - mean.getLat();
			double lngDiff = place.getLng() - mean.getLng();
			variance += (latDiff * latDiff + lngDiff * lngDiff);
		}
		return variance / placeIds.size();
	}

	public Place getMean(Places places) {
		double meanLat = 0;
		double meanLng = 0;
		for (Integer placeId : placeIds) {
			meanLat += places.getPlace(placeId).getLat();
			meanLng += places.getPlace(placeId).getLng();
		}
		return new Place(meanLat / placeIds.size(), meanLng / placeIds.size());
	}

	// trim the group to only preserve the places that are in patternedPlaces
	public Group trim(Set<Integer> patternedPlaces) {
		List<Integer> trimmedPlaces = new ArrayList<Integer>();
		for (Integer placeId : placeIds)
			if (patternedPlaces.contains(placeId))
				trimmedPlaces.add(placeId);
		this.placeIds = trimmedPlaces;
		return this;
	}

	// trim the group to only preserve the places that are not in
	// patternedPlaces
	public List<Integer> getToPreservePlaces(Set<Integer> discardPlaces) {
		List<Integer> preserve = new ArrayList<Integer>();
		for (Integer placeId : placeIds)
			if (!discardPlaces.contains(placeId))
				preserve.add(placeId);
		return preserve;
	}

	public String toString() {
		StringBuffer r = new StringBuffer("Group ");
		r.append(id + ", Size " + placeIds.size() + ":");
		for (Integer i : placeIds) {
			r.append(" " + i);
		}
		// return the string
		return r.toString();
	}

	//randomly split this group into K groups
	public List<Group> randomSplit( int K ) {
		List<Group> result = new ArrayList<Group>();
		for(int i=0; i<K; i++) {
			Group g = new Group();
			result.add( g );
		}
		for(Integer placeId: placeIds) {
			int groupIndex = random.nextInt( K );
			result.get(groupIndex).addPlace(placeId);
		}
		return result;
	}
	
	Random random = new Random();
	public List<Integer> getRandomPlaces(int N) {
		List<Integer> result = new ArrayList<Integer>();
		for(int i=0; i<N; i++) {
			int index = random.nextInt( placeIds.size() );
			result.add( placeIds.get(index) );
		}
		return result;
	}
	
	// get top N most faraway places
	public List<Integer> getTopKFarawayPlaces(Places places, int N) {
		Place mean = getMean(places);
		
		PriorityQueue<PlaceDistCell> candidateHeap = initializeCandidateHeap();
		for(Integer placeId : placeIds) {
			double dist = mean.getDistanceTo( places.getPlace(placeId) );
			PlaceDistCell c = new PlaceDistCell(placeId, dist);
			if( candidateHeap.size() < N ) {
				candidateHeap.offer( c );
			}
			else if( dist > candidateHeap.peek().getDist() ) {
				candidateHeap.poll();
				candidateHeap.offer( c );
			}
		}
		
		List<Integer> result = new ArrayList<Integer>();
		for(PlaceDistCell c: candidateHeap) {
			if( Math.random() > 0.5)
				result.add( c.getId() );
		}
		return result;
	}

	private PriorityQueue<PlaceDistCell> initializeCandidateHeap() {
		// min-heap, ascending order of dist
		PriorityQueue<PlaceDistCell> minScoreHeap = new PriorityQueue<PlaceDistCell>(
				10, new Comparator<PlaceDistCell>() {
					public int compare(PlaceDistCell e1, PlaceDistCell e2) {
						if (e1.getDist() > e2.getDist())
							return 1;
						else if (e1.getDist() == e2.getDist())
							return 0;
						else
							return -1;
					}
				});
		return minScoreHeap;
	}
	
	private class PlaceDistCell {
		int placeId;
		double dist; // distance to the mean

		public PlaceDistCell(int placeId, double dist) {
			this.placeId = placeId;
			this.dist = dist;
		}
		
		public int getId() {
			return placeId;
		}

		public double getDist() {
			return dist;
		}
	}
}
