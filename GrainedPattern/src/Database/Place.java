package Database;

import java.util.List;

public class Place {
	private final int id;
	private int groupId = 0;
	private final double lat;
	private final double lng;
	private List<Integer> categories = null;
	
	public Place(int id, double lat, double lng, List<Integer> categories) {
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.categories = categories;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public int getMaxCategoryLevel() {
		return categories.size() - 1;
	}
	
	
	public void view() {
		System.out.print("("+id+","+groupId+","+lat+","+lng+"<");
		for(Integer i:categories) {
			System.out.print(i+"-");
		}
		System.out.print(">");
	}
	
	// Get the category for requested level, level starts from 0
	public int getCategory(int level) {
		return categories.get( level );
	}
	
	public List<Integer> getCategories() {
		return categories;
	}

	public int getId() {
		return id;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public void setCategories(List<Integer> categories) {
		this.categories = categories;
	}
	
	////////////////////////////////////////////////////////////////
	public Place(double lat, double lng) {
		this.id = -1;
		this.lat = lat;
		this.lng = lng;
	}
	
}
