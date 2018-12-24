package Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Group {
	private int id; // group id, starting from 0

	static int mToAssignGroupId = 0;
	
	private List<Integer> placeIds = null;
	
	public Group() {
		this.id = mToAssignGroupId++;
		this.placeIds = new ArrayList<Integer>();
	}
	
	public void addPlace(int i) {
		this.placeIds.add(i);
	}
	
	public int getId() {
		return id;
	}

	public List<Integer> getPlaceIds() {
		return placeIds;
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
		return new ArrayList<Group>(subgroups.values());
//		return convertToList(subgroups);
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
	
	private int getCategoryId(Place place, int level) {
		int maxLevel = place.getMaxCategoryLevel();
		int possibleLevel = level < maxLevel ? level : maxLevel;
		return place.getCategory(possibleLevel);
	}
	
	public void view() {
		System.out.println();
		System.out.println("Id : "+this.id);
		System.out.println(this.mToAssignGroupId);
		for(Integer i:this.placeIds) {
			System.out.print(i);
		}
	}
	
	//////////////////////////////////////////////////////////////////
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
	

}
