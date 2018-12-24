package NMeanshift;

import java.util.ArrayList;
import java.util.List;

import NDatabase.NDatabase;
import NDatabase.NPlace;


public class NGroup {
	public List<Integer> placeIds = new ArrayList<Integer>();
	
	public void view() {
		System.out.println();
		for(Integer i:this.placeIds) {
			System.out.print(i);
		}
	}
	
	public double getVariance(NDatabase d) {
		NPlace mean = getMean(d);
		double variance = 0;
		for (Integer placeId : placeIds) {
			NPlace place = d.getPlace().get(placeId);
			double latDiff = place.getLat() - mean.getLat();
			double lngDiff = place.getLng() - mean.getLng();
			variance += (latDiff * latDiff + lngDiff * lngDiff);
		}
		return variance / placeIds.size();
	}

	public NPlace getMean(NDatabase d) {
		double meanLat = 0;
		double meanLng = 0;
		for (Integer placeId : placeIds) {
			meanLat += d.getPlace().get(placeId).getLat();
			meanLng += d.getPlace().get(placeId).getLng();
		}
		return new NPlace(meanLat / placeIds.size(), meanLng / placeIds.size());
	}
	
}
