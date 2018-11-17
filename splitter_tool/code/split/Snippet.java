package split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import meanshift.DenseDoubleVector;
import meanshift.DoubleVector;

public class Snippet {
	
	List<Integer> mPlaceSequence = new ArrayList<Integer>();
	Set<Integer> mVisitors= new HashSet<Integer>();
	int mLength;
	int mWeight; //number of visitors
	
	public Snippet(List<Integer> places, Set<Integer> visitors) {
		mPlaceSequence = places;
		mVisitors = visitors;
		mLength = mPlaceSequence.size();
		mWeight = mVisitors.size() ;
	} 
	
	public int getPlace(int position) {
		return mPlaceSequence.get( position );
	}
	
	public List<Integer> getPlaceSequence(){
		return mPlaceSequence;
	}
	
	public Set<Integer> getVisitors() {
		return mVisitors;
	}
	
	public int getWeight() {
		return mWeight;
	}

	public DoubleVector toDoubleVector(Places places) {
		double[] coordinates = new double[ 2 * mLength ];
		for(int i=0; i<mPlaceSequence.size(); i++) {
			int placeId = mPlaceSequence.get( i );
			Place place = places.getPlace( placeId );
			coordinates[ 2*i ] = place.getLat();
			coordinates[ 2*i+1 ] = place.getLng();
		}
		return new DenseDoubleVector( coordinates );
	}

}
