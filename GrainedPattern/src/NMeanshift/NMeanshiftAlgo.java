package NMeanshift;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import NDatabase.NDatabase;
import NDatabase.NPlace;
import NPrefixspan.NSnippet;
import NPrefixspan.NSnippetCluster;
import Refine.MeanShift;
import coarsepattern.Snippet;
import coarsepattern.SnippetCluster;

public class NMeanshiftAlgo {
	double mSupThreshold; // support threshold
	double mVarThreshold; // variance threshold
	double mTau; // dampening factor
	double bandwidth;

	double mMergeWindow = 2e-3;
	int mMaxIter = 100; // max iteration for mean shift

	int mSnippetLength = 1;
	NDatabase database = null;
	List<NSnippetCluster> patternList = null; // result sequential patterns

	public List<NSnippetCluster> getPatterns() {
		return patternList;
	}
	
	
	public NMeanshiftAlgo(double mSupThreshold, double mVarThreshold, double mTau, double bandwidth, NDatabase d) {
		super();
		this.mSupThreshold = mSupThreshold;
		this.mVarThreshold = mVarThreshold;
		this.mTau = mTau;
		this.bandwidth = bandwidth;
		this.database = d;
	}

	public void minePatterns(int snippetLength, List<NSnippet> snippets) {
		patternList = new ArrayList<NSnippetCluster>();
		mSnippetLength = snippetLength;
		mMergeWindow = computeDimensionInvariant(snippetLength, mMergeWindow);
		double dimensionInvariantBandwidth = computeDimensionInvariant(snippetLength, bandwidth);
//		System.out.println(mSnippetLength);
//		System.out.println(mMergeWindow);
//		System.out.println(dimensionInvariantBandwidth);
		splitPatternWithPruning(snippets,dimensionInvariantBandwidth);
	}

	private void splitPatternWithPruning(List<NSnippet> snippets,double bandwidth) {
		if (bandwidth < 1e-3)
			return;

		List<NSnippetCluster> snippetClusters = cluster(snippets,bandwidth);

		
		for (NSnippetCluster sc : snippetClusters) {
			if (sc.computeSuppport() >= mSupThreshold && sc.computeVariance(database) <= mVarThreshold) {
				patternList.add(sc);
			} else if (sc.computeSuppport() >= mSupThreshold) {
				splitPattern(sc.getSnippet(), bandwidth * mTau);
			}
		}

	}

	// ======================================cluster============================
	// cluster a set of snippets using weighted mean shift
	private List<NSnippetCluster> cluster(List<NSnippet> snippets, double bandwidth) {
		List<NDoubleVector> points = toPoints(snippets); // snippet -> point
//		System.out.println(points);
		List<Integer> weights = getWeights(snippets); // weight: number of snippet visitors
//		System.out.println(weights);
//		System.out.println(bandwidth);
//		System.out.println(mMergeWindow);
//		System.out.println(mMaxIter);
		NMeanShift ms = new NMeanShift();
		ms.cluster(points, weights, bandwidth, mMergeWindow, mMaxIter);
		Map<Integer, List<Integer>> clusters = ms.getClusters();
//		System.out.println(clusters);
		return getSnippetClusters(snippets, clusters);
	}

	private List<NDoubleVector> toPoints(List<NSnippet> snippets) {
		List<NDoubleVector> points = new ArrayList<NDoubleVector>(); // snippet -> point
		for (NSnippet s : snippets) {
			points.add(s.toDoubleVector(s, this.database));
		}
		return points;
	}

	private List<Integer> getWeights(List<NSnippet> snippets) {
		List<Integer> weights = new ArrayList<Integer>(); // weight: number of snippet visitors
		for (NSnippet s : snippets) {
			weights.add(s.mWeight);
		}
		return weights;
	}

	// k: snippet length
	private double computeDimensionInvariant(int k, double value) {
		if (k == 1)
			return value;
		return value;
	}

	private List<NSnippetCluster> getSnippetClusters(List<NSnippet> snippets, Map<Integer, List<Integer>> clusters) {
		List<NSnippetCluster> result = new ArrayList<NSnippetCluster>();
		Iterator iter = clusters.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			NSnippetCluster sc = new NSnippetCluster();
			sc.setSnippetLength(mSnippetLength);
			List<Integer> indices = (List<Integer>) entry.getValue(); // indices of snippets in the cluster
			for (Integer snippetIndex : indices)
				sc.addSnippet(snippets.get(snippetIndex));
			result.add(sc);
		}
		return result;
	}

	// ============================================Split pattern
	private void splitPattern(List<NSnippet> snippets, double bandwidth) {

		if (bandwidth < 5e-4)
			return;

		List<NSnippetCluster> snippetClusters = cluster(snippets, bandwidth);
		
		List<NSnippet> remainingSnippets = new ArrayList<NSnippet>();
		for (NSnippetCluster sc : snippetClusters) {
			int support = sc.computeSuppport();
			if (support >= mSupThreshold && sc.computeVariance(database) <= mVarThreshold) {
				patternList.add(sc);
			} else {
				remainingSnippets.addAll(sc.getSnippet());
			}
		}
		splitPattern(remainingSnippets, bandwidth * mTau);
	}

}
