package NMeanshift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

public class NMeanShift {

	private final double SQRT_2_PI = FastMath.sqrt(2 * Math.PI);

	private int maxIteration = 100;
	private double bandwidth = 0.01;
	private double mergeWindow = 0.001;
	private List<Integer> weights = null; // weights of points
	private List<NDoubleVector> points = null; 
	private List<NDoubleVector> centers = null;
	private boolean[] converged = null;
	private int[] designation = null;

	public List<NDoubleVector> cluster(List<NDoubleVector> points, List<Integer> weights,
			double bandwidth, double mergeWindow, int maxIterations) {
		initialize(points, weights, maxIterations, bandwidth, mergeWindow);
		for (int i = 0; i < maxIterations; i++) {
			int remainingConvergence = meanShift();
			// merge if centers are within the mergeWindow
			if (remainingConvergence == 0) {
				break;
			}
		}
		assign();
		return centers;
	}
	
	//get clusters from designation
	public Map<Integer, List<Integer>> getClusters() {
		Map<Integer, List<Integer>> clusters = new HashMap<Integer, List<Integer>>(); 
		for(int i=0; i<designation.length; i++) {
			int clusterID = designation[i];
			if ( clusters.containsKey(clusterID) ) {
				clusters.get( clusterID ).add(i);
			} else {
				List<Integer> newCluster = new ArrayList<Integer>();
				newCluster.add( i );
				clusters.put( clusterID, newCluster );
			}
		}
		return clusters;
	}
	
	public int[] getDesignation() {
		return designation;
	}
	
	private void initialize(List<NDoubleVector> points, List<Integer> weights, int maxIteration,
			double bandwidth, double mergeWindow) {

		this.maxIteration = maxIteration;
		this.bandwidth = bandwidth;
		this.mergeWindow = mergeWindow;

		this.points = points;
		this.weights = weights;
		findInitialCenters();

		this.converged = new boolean[points.size()];
		Arrays.fill(this.converged, false);

		this.designation = new int[points.size()];
		Arrays.fill(this.designation, -1);

	}

	private void findInitialCenters() {
		centers = new ArrayList<NDoubleVector>();
		for (int i = 0; i < points.size(); i++) {
			NDoubleVector point = points.get(i);
			NDoubleVector center = new DenseDoubleVector(point);
			centers.add(center);
		}
	}
	
	private int meanShift() {
		int remainingConvergence = 0;
		for (int i = 0; i < centers.size(); i++) {
			// only update the centers that have not converged yet.
			if (converged[i]) {
				continue;
			}
			NDoubleVector v = centers.get(i);
			List<VectorDistanceTuple> neighbours = getNeighbors(v);
			NDoubleVector numerator = new DenseDoubleVector(v.getLength());
			double denominator = 0;
			for (VectorDistanceTuple neighbour : neighbours) {
				double weight = weights.get( neighbour.getId() );
				double normDistance = neighbour.getDistance() / bandwidth;
				double gradient = -gaussianGradient(normDistance) * weight;
				numerator = numerator.add(neighbour.getVector().multiply(gradient));
				denominator += gradient;
			}
			if (denominator > 0d) {
				NDoubleVector newCenter = numerator.divide(denominator);
				if (v.subtract(newCenter).abs().sum() > 1e-4) {
					remainingConvergence++;
				} else {
					converged[i] = true;
				}
				// apply the shift
				centers.set(i, newCenter);
			}
		}
		return remainingConvergence;
	}
	
	private List<VectorDistanceTuple> getNeighbors( NDoubleVector vec ) {
		List<VectorDistanceTuple> results = new ArrayList<NMeanShift.VectorDistanceTuple>();
		for(int i=0; i<points.size(); i++) {
			NDoubleVector otherVector = points.get( i );
			double dist = NEuclidianDistance.get().measureDistance(vec, otherVector);
			if( dist <= bandwidth ) {
				VectorDistanceTuple tuple = new VectorDistanceTuple(otherVector, i, dist);
				results.add( tuple );
			}
		}
		return results;
	}

	private double gaussianGradient(double dist) {
		return -1;
	}
	
	private void assign() {
		
		int totalPointNum = points.size();
		boolean [] assigned = new boolean[ totalPointNum ];
		Arrays.fill(assigned, false); 
        
        //Loop through and asign clusters
        int curClusterID = 0;
        boolean progress = true;
        while(progress) {
            progress = false;
            int basePos = 0;//This will be the mode of our cluster
            while( basePos < totalPointNum && assigned[basePos]  )
                basePos++;
            for(int i = basePos; i < totalPointNum; i++) {
                if( assigned[i] )
                    continue;//Already assigned
                progress = true;
                double dist = NEuclidianDistance.get().measureDistance(centers.get(basePos), centers.get(i));
                if( dist < mergeWindow ) {
                    assigned[i] = true;
                    designation[i] = curClusterID;
                }
            }
            
            curClusterID++;
        }
    }

	private class VectorDistanceTuple {

		final NDoubleVector vec;
		final int id;
		final double dist; //distance of the vector to the target vector

		public VectorDistanceTuple(NDoubleVector vec, int id, double dist) {
			this.vec = vec;
			this.id = id;
			this.dist = dist;
		}

		public double getDistance() {
			return dist;
		}

		public int getId() {
			return id;
		}
		
		public NDoubleVector getVector() {
			return vec;
		}

		@Override
		public String toString() {
			return vec + " -> " + dist;
		}
	}
}
