package NPrefixspan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import NDatabase.NDatabase;
import NDatabase.NPlace;
import NMeanshift.DenseDoubleVector;
import NMeanshift.NDoubleVector;

public class NSnippet {
	public List<NPlace> mPlaceSequence = new ArrayList<NPlace>();
	public Set<Integer> mVisitors = new HashSet<Integer>();
	public int mLength;
	public int mWeight; // number of visitors

	public NSnippet(List<NPlace> places, Set<Integer> visitors) {
		mPlaceSequence = places;
		mVisitors = visitors;
		mLength = mPlaceSequence.size();
		mWeight = mVisitors.size();
	}

	public void view() {
//		System.out.println("Length : "+mLength);
//		System.out.println("mWeight : "+ mWeight);
		System.out.print("mPlaceSeq : ");
		for (NPlace i : mPlaceSequence) {
			System.out.print(i.placeId + " - ");
		}
//		System.out.println();
//		System.out.print("mVisitor : ");
//		for (Integer i : mVisitors) {
//			System.out.print(i + " - ");
//		}
		System.out.println();
	}
	
	
	//////////////////////////////////////////
	public NDoubleVector toDoubleVector(NSnippet s, NDatabase N) {
		double[] coordinates = new double[ 2 * s.mLength ];
		for(int i=0; i<s.mPlaceSequence.size(); i++) {
//			int placeId = s.mPlaceSequence.get( i );
			NPlace place = s.mPlaceSequence.get(i);
			coordinates[ 2*i ] = place.getLat();
			coordinates[ 2*i+1 ] = place.getLng();
		}
		return new DenseDoubleVector( coordinates );
	}

}
