package Refine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Database.Places;
import coarsepattern.Snippet;
import coarsepattern.SnippetCluster;

public class Splitter {
	
	double mSupThreshold = 10; //support threshold
	double mVarThreshold = 1.0; //variance threshold
	double mTau = 0.8; //dampening factor
	double mMergeWindow = 2e-3;
	int mMaxIter = 100; // max iteration for mean shift
	
	int mSnippetLength = 1;
	Places mPlaces = null; // places
	List<SnippetCluster> patternList = null; //result sequential patterns
	
	public Splitter(int supThreshold, double varThreshold, double tau, Places places) {
		this.mSupThreshold = supThreshold;
		this.mVarThreshold = varThreshold;
		this.mTau = tau;
		mPlaces = places;
	}
	
	public void minePatterns(int snippetLength, List<Snippet> snippets, double bandwidth ) throws Exception {
		patternList = new ArrayList<SnippetCluster>();
		mSnippetLength = snippetLength;
		mMergeWindow = computeDimensionInvariant(snippetLength, mMergeWindow);
		double dimensionInvariantBandwidth = computeDimensionInvariant(snippetLength, bandwidth);
//		System.out.println(mSnippetLength);
//		System.out.println(mMergeWindow);
//		System.out.println(dimensionInvariantBandwidth);
		splitPatternWithPruning(snippets, dimensionInvariantBandwidth);
			}
	
	public List<SnippetCluster> getPatterns() {
		return patternList;
	}
	
	
	//k: snippet length
	private double computeDimensionInvariant(int k, double value) {
		if(k==1) 
			return value;
		return value;
		/*
		int factorial = 1;
		for(int i=1; i<=k; i++)
			factorial *= i;
		return pow( factorial*value*value * pow(PI, (1-k) ), 0.5/k);
		*/
	}
	
	private void splitPattern(List<Snippet> snippets, double bandwidth ) {
		
		if(bandwidth < 5e-4) 
			return;
		
		List<SnippetCluster> snippetClusters = cluster(snippets, bandwidth);
		
		List<Snippet> remainingSnippets = new ArrayList<Snippet>();
		for(SnippetCluster sc : snippetClusters) {
			int support = sc.computeSuppport();
			if( support >= mSupThreshold && sc.computeVaraince( mPlaces ) <= mVarThreshold ) {
				patternList.add( sc );
			} else {
				remainingSnippets.addAll( sc.getSnippets() );
			}
		}
		splitPattern(remainingSnippets, bandwidth*mTau);
	}
	
	//this is just for visualization purpose.
	private void splitPatternVis(List<Snippet> snippets, double bandwidth ) throws Exception {
		System.out.println("Snippet Number: " + snippets.size() + " Bandwidth:" + bandwidth);
		if(bandwidth < 1e-3) 
			return;
		
		String goodClusterFile = "/Users/ZC/Dataset/Sequential/clean/results/goodClusters.txt";
		String remainingClustersFile = "/Users/ZC/Dataset/Sequential/clean/results/remainingClusters.txt";
		String prunedClustersFile = "/Users/ZC/Dataset/Sequential/clean/results/prunedPoints.txt";
		BufferedWriter bwg = new BufferedWriter( new FileWriter(goodClusterFile, true) );
		BufferedWriter bw = new BufferedWriter( new FileWriter(remainingClustersFile, true) );
		BufferedWriter bwp = new BufferedWriter( new FileWriter(prunedClustersFile, true) );
		bwg.append("#\n");
		bw.append("#\n");
		bwp.append("#\n");
		
		List<SnippetCluster> snippetClusters = cluster(snippets, bandwidth);
		List<Snippet> remainingSnippets = new ArrayList<Snippet>();
		for(SnippetCluster sc : snippetClusters) {
			int support = sc.computeSuppport();
			if( support >= mSupThreshold && sc.computeVaraince( mPlaces ) <= mVarThreshold ) {
				patternList.add( sc );
				System.out.println( "Pattern Support:" + support);
				for( Snippet s : sc.getSnippets() )
					bwg.append( s.getPlaceSequence().get(0) + " " );
				bwg.append("\n");
				
			} else if (sc.computeSuppport() >= mSupThreshold) {
				remainingSnippets.addAll( sc.getSnippets() );
			} else {
				for( Snippet s : sc.getSnippets() )
					bwp.append( s.getPlaceSequence().get(0) + " " );
			}
		}
		
		for(Snippet s : remainingSnippets)
			bw.append( s.getPlaceSequence().get(0) + " " );
		bw.append("\n");
		bwp.append("\n");
		
		bw.close();
		bwg.close();
		bwp.close();
		
		splitPatternVis(remainingSnippets, bandwidth*mTau);
	}

	private void splitPatternWithPruning(List<Snippet> snippets, double bandwidth ) {
		if(bandwidth < 1e-3) 
			return;
		
		
		List<SnippetCluster> snippetClusters = cluster(snippets, bandwidth);
//		for(SnippetCluster ss : snippetClusters) {
//			System.out.println(ss.getSnippets().size());
//		}
//		
		for(SnippetCluster sc : snippetClusters) {
////			System.out.println("+"+sc.computeVaraince( mPlaces ));
//			System.out.println("======================");
//			System.out.println(sc.computeSuppport());
//			System.out.println(sc.computeVaraince(mPlaces));
//			System.out.println("======================");
			if( sc.computeSuppport() >= mSupThreshold && sc.computeVaraince( mPlaces ) <= mVarThreshold ) {
				patternList.add( sc );
			} else if ( sc.computeSuppport() >= mSupThreshold) {
				splitPattern( sc.getSnippets(), bandwidth*mTau);
			}
//			break;
		}
		
	}
	
	//cluster a set of snippets using weighted mean shift
	private List<SnippetCluster> cluster(List<Snippet> snippets, double bandwidth) {
		List<DoubleVector> points = toPoints( snippets ); // snippet -> point
//		System.out.println(snippets.size());
//		System.out.println(points);
		List<Integer> weights = getWeights(snippets); // weight: number of snippet visitors
//		System.out.println(weights);
//		System.out.println(bandwidth);
//		System.out.println(mMergeWindow);
//		System.out.println(mMaxIter);
		MeanShift ms = new MeanShift();
		ms.cluster(points, weights, bandwidth, mMergeWindow, mMaxIter);
		Map<Integer, List<Integer>> clusters = ms.getClusters();
//		System.out.println(clusters);
		return getSnippetClusters( snippets, clusters );
	}
	
	private List<DoubleVector> toPoints(List<Snippet> snippets) {
		List<DoubleVector> points = new ArrayList<DoubleVector> (); // snippet -> point
		for(Snippet s: snippets) {
			points.add( s.toDoubleVector(mPlaces) );
		}
		return points;
	}
	
	private List<Integer> getWeights(List<Snippet> snippets) {
		List<Integer> weights = new ArrayList<Integer>(); // weight: number of snippet visitors
		for(Snippet s: snippets) {
			weights.add( s.getWeight() );
		}
		return weights;
	}
	
	private List<SnippetCluster> getSnippetClusters(List<Snippet> snippets, Map<Integer, List<Integer>> clusters ) {
		List<SnippetCluster> result = new ArrayList<SnippetCluster>();
		Iterator iter = clusters.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			SnippetCluster sc = new SnippetCluster(mSnippetLength); 
			List<Integer> indices = (List<Integer>)entry.getValue(); //indices of snippets in the cluster
			for(Integer snippetIndex: indices) 
				sc.addSnippet( snippets.get( snippetIndex ) );
			result.add( sc );
		}
		return result;
	}
}
