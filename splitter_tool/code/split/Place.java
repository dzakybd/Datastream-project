package split;

import java.util.List;

public class Place {
	
	private final int id;
	private int groupId = 0;
	private final double lat;
	private final double lng;
	private List<Integer> categories = null;
	
	public Place(double lat, double lng) {
		this.id = -1;
		this.lat = lat;
		this.lng = lng;
	}
	
	public Place(int id, double lat, double lng, List<Integer> categories) {
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.categories = categories;
	}
	
	public int getId() {
		return id;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public double getDistanceTo(Place p) {
		double latDiff = this.lat - p.getLat();
		double lngDiff = this.lng - p.getLng();
		return Math.sqrt( latDiff*latDiff + lngDiff*lngDiff );
	}
	
	// Get the category for requested level, level starts from 0
	public int getCategory(int level) {
		return categories.get( level );
	}
	
	public int getMaxCategoryLevel() {
		return categories.size() - 1;
	}
	
}
