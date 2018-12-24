package coarsepattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Database.Place;
import Database.Places;
import Refine.DenseDoubleVector;
import Refine.DoubleVector;

public class Snippet {
	private List<Integer> mPlaceSequence = new ArrayList<Integer>();
	private Set<Integer> mVisitors= new HashSet<Integer>();
	private int mLength;
	private int mWeight; //number of visitors
	
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
	
	public void view() {
		System.out.print("mPlaceSeq : ");
		for(Integer i : mPlaceSequence) {
			System.out.print(i +" - ");
		}
		System.out.println();
		System.out.print("mVisitor : ");
		for(Integer i : mVisitors) {
			System.out.print(i +" - ");
		}
		System.out.println();
	}
	
	//////////////////////////////////////////////////////////////
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
