package NDatabase;


public class NPlace {
	public int placeId;
	public double lat;
	public double lng;
	public NCategory category;
	
	public NPlace(int placeId, double lat, double lng, NCategory category) {
		super();
		this.placeId = placeId;
		this.lat = lat;
		this.lng = lng;
		this.category = category;
	}
	
	public int getPlaceId() {
		return placeId;
	}
	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}

	public NCategory getCategory() {
		return category;
	}
	public void setCategory(NCategory category) {
		this.category = category;
	}
	
	public void view() {
		System.out.print("Pid:"+ this.placeId+",");
		this.category.view();
	}
	
	
	///////////////Splitter//////////////////
	public NPlace(double lat, double lng) {
		this.placeId = -1;
		this.lat = lat;
		this.lng = lng;
	}
	
	@Override 
	public boolean equals(Object object){
		NPlace item = (NPlace) object;
		if((item.placeId==placeId)){
			return true;
		}
		return false;
	}
}
