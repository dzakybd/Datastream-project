package split;

import java.util.List;
import java.util.Random;

public class KMeans extends Clusterer {

	Random r = new Random();
	Place[] previousMean = null;
	Place[] mean = null;
	Group group = null;
	Group[] clusters = null;
	
	public KMeans(Places places, int nCluster, int nIteration) {
		super(places, nCluster, nIteration);
	}

	public Group[] cluster(Group group) {
		int originalClusterNum = this.nCluster;
		if(group.size() < originalClusterNum)
			this.nCluster = group.size();
		
		initialize(group);
		for (int iteration = 0; iteration < nIteration; iteration++) {
			computeMean();
			if( !hasMeanChanged() )
				break;
			assignToClusters();
		}
		
		this.nCluster = originalClusterNum;
		return clusters;
	}

	private void initialize(Group g) {
		this.group = g;
		previousMean = new Place[ nCluster ];
		mean = getRandomMean();
		clusters = new Group[ nCluster ];
		for(int i=0; i<nCluster; i++)
			clusters[i] = new Group();
		assignToClusters();
	}
	
	private Place[] getRandomMean() {
		Place[] mean = new Place[ nCluster ];
		List<Integer> placeIds = group.getPlaceIds();
		int [] kRandomNumbers = genKRandomNumbers(placeIds.size(), nCluster);
		for(int i=0; i<nCluster; i++) {
			Place p = places.getPlace( placeIds.get( kRandomNumbers[i]) );
			mean[i] = new Place(p.getLat(), p.getLng()); //a pseudo place
		}
		return mean;
	}
	
	//Generate K distinct random numbers in [0,n-1]
	private int[] genKRandomNumbers(int n, int k) {
		
		int[] completeArray = new int[n];
		for(int i=0; i<n; i++) {
			completeArray[i] = i;
		}
		
		int[] result = new int[k];
		int bound = n;
		for(int i=0; i<k; i++) {
			int randNum = r.nextInt( bound ); //generate a random integer between 0~bound-1
			result[i] = completeArray[ randNum ];
			completeArray[randNum] = completeArray[ bound-1 ];
			completeArray[bound-1] = result[i];
			bound --;
		}
		return result;
	}
	
	private void computeMean() {
		//before computing, store current version of mean into previous mean
		for(int i=0; i<nCluster; i++)
			previousMean[i] = new Place( mean[i].getLat(), mean[i].getLng());
		
		for(int i=0; i<nCluster; i++) {
			double meanLat = 0;
			double meanLng = 0;
			List<Integer> placeIds = clusters[i].getPlaceIds();
			for(Integer placeId: placeIds) {
				meanLat += places.getPlace(placeId).getLat();
				meanLng += places.getPlace(placeId).getLng();
			}
			mean[i] = new Place( meanLat/placeIds.size(), meanLng/placeIds.size());
		}
	}
	
	private void assignToClusters() {
		for(int i=0; i<clusters.length; i++)
			clusters[i].clear();
		for(Integer placeId: group.getPlaceIds()) {
			int assignId = getNearestCluster( places.getPlace(placeId) );
			clusters[ assignId ].addPlace(placeId);
		}
	}
	
	private int getNearestCluster(Place p) {
		int result = 0;
		double minDist = mean[0].getDistanceTo(p);
		for(int i=1; i<nCluster; i++) {
			double dist = mean[i].getDistanceTo(p);
			if(dist <= minDist) {
				minDist = dist;
				result = i;
			}
		}
		return result;
	}
	
	private boolean hasMeanChanged() {
		for(int i=0; i<nCluster; i++) {
			if(previousMean[i].getLat()!=mean[i].getLat() || 
					previousMean[i].getLng()!=mean[i].getLng())
				return true;
		}
		return false;
	}
}
