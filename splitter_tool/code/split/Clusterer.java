package split;

public abstract class Clusterer {

	int nCluster;
	int nIteration;
	Places places;
	
	public Clusterer(Places places, int nCluster, int nIteration) {
		this.places = places;
		this.nCluster = nCluster;
		this.nIteration = nIteration;
		
	}
	
	public int getClusterNum() {
		return nCluster;
	}
	
	public abstract Group[] cluster(Group group);
	
}
