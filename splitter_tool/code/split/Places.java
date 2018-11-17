package split;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Places {

	List<Place> places = new ArrayList<Place>();
	
	/*
	 * Load Places from data file
	 * */
	public void loadFile(String filename) throws Exception {
		BufferedReader br = new BufferedReader( new FileReader(filename) );
        while(true)  {
            String line = br.readLine();
            if(line == null)
            	break;
            addPlace(line.split(","));
        }
        br.close();
	}
	
	public void addPlace(String[] placeAttributes) {
		
		int id = Integer.parseInt( placeAttributes[0] ); //place id
		double lat = Double.parseDouble( placeAttributes[1] );
		double lng = Double.parseDouble( placeAttributes[2] );
		
		List<Integer> categories = new ArrayList<Integer>();
		String[] integers = placeAttributes[placeAttributes.length - 1].split(" ");
		for(String s : integers) {
			int categoryId = Integer.parseInt( s );
			categories.add( categoryId );
		}
		
		Place place = new Place(id, lat, lng, categories);
		
		this.places.add(place);
	}

	public Place getPlace(int placeId) {
		return places.get( placeId );
	}
	
	public int getTotalPlaceNum() {
		return places.size();
	}
	
	public void setPlaceGroupIds(Groups groups) {
		for( Group group: groups.getGroups() ) {
			int groupId = group.getId();
			List<Integer> placeIds = group.getPlaceIds();
			for(Integer placeId: placeIds) {
				places.get(placeId).setGroupId(groupId);
			}
		}
	}
	
	/*
	 * Get the group id a place belongs to.
	 * */
	public int getGroupIdForPlace(int placeId) {
		return places.get(placeId).getGroupId();
	}
	
}
