package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import prefixspan.AlgoPrefixSpan;
import prefixspan.SequenceDatabase;
import split.Group;
import split.GroupSequence;
import split.Groups;
import split.Places;
import split.SnippetCluster;
import split.Splitter;

public class Assesser {
	
	SequenceDatabase mDatabase = null;
	int mDataSize;
	Places mPlaces = null;
	Groups mGroups = null;

	String groupFile;
	String patternFile;
	
	//statistics
	double timeSplitterCoarse = 0;
	double timeSplitterFine = 0;

	int coverageSplitter = 0;

	int patternCountSplitter = 0;

	double varianceSplitter = 0;

	public Assesser(String groupFile, String patternFile) throws Exception {
		this.groupFile = groupFile;
		this.patternFile = patternFile;
	}
	
	public void loadDatabase(String dataFile) throws Exception {
		mDatabase = new SequenceDatabase();
		mDatabase.loadFile(dataFile);
		mDataSize = mDatabase.size(); //database size
	}
	
	public void loadPlaces(String placeFile) throws Exception {
		mPlaces = new Places();
		mPlaces.loadFile(placeFile);
	}
	
	public int getDataSize() {
		return mDataSize;
	}
	
	public void writeStats(String statFile) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(statFile, true)); // append
		bw.append( timeSplitterCoarse + " " + timeSplitterFine + " "
				 	  + patternCountSplitter + " " + coverageSplitter + " "  + "\n");
		bw.close();
	}
	
	

	/*
	 * Evaluate Splitter.
	 */
	public void runSplitter(double minSup, int maxInterval, double varThreshold, double tau, double initialBandwidth) throws Exception {
		initializeGroups(1); //split by category level 1
		List<SnippetCluster> coarsePatterns = mineCoarsePatterns(minSup, maxInterval);
		//writePatterns(coarsePatterns);
		List<SnippetCluster> finePatterns = refinePattern(coarsePatterns, (int)(minSup * mDataSize), varThreshold, tau, initialBandwidth);
		writePatterns( finePatterns );
		coverageSplitter = computeCoverage( finePatterns );
		patternCountSplitter = computePatternCount( finePatterns );
		varianceSplitter = computeVariance( finePatterns );
	}
	
	private void initializeGroups(int level) {
		mGroups = new Groups();
		Group initialGroup = new Group();
		for (int i = 0; i < mPlaces.getTotalPlaceNum(); i++) {
			initialGroup.addPlace(i);
		}
		mGroups.addGroup(initialGroup);
		
		mGroups.splitByCategory(mPlaces, level);
		mPlaces.setPlaceGroupIds(mGroups); // mark which group each place belongs to
		mDatabase.setItemIds(mPlaces);	// mark which group each place belongs to in the sequence database
	}
	
	//Step I
	private List<SnippetCluster> mineCoarsePatterns(double minSup, int maxInterval) throws Exception {
		System.out.println("Begin Mining Coarse Patterns...");
		AlgoPrefixSpan spmalgo = new AlgoPrefixSpan(minSup, maxInterval);
		long start = System.currentTimeMillis();
		spmalgo.runAlgorithm(mDatabase);
		long end = System.currentTimeMillis();
		timeSplitterCoarse = (end - start) / 1000.0;
		
		List<SnippetCluster> coarsePatterns = spmalgo.getSnippetClusters();
		System.out.println("Done Mining Coarse Patterns. Pattern Number: " + coarsePatterns.size());
		return coarsePatterns;
	}

	//Step II
	public List<SnippetCluster> refinePattern(List<SnippetCluster> snippetClusters, int minAbsSup, double varThreshold, double tau, double initialBandwidth) throws Exception {
		System.out.println("Begin Refining Coarse Patterns...");
		long start = System.currentTimeMillis();
		
		Splitter splitter = new Splitter(minAbsSup, varThreshold, tau, mPlaces);
		List<SnippetCluster> patterns = new ArrayList<SnippetCluster>(); //fine patterns are represented as group sequences
		for(SnippetCluster sc : snippetClusters) {
			if(sc.getSnippetLength() >= 2) {
				splitter.minePatterns(sc.getSnippetLength(), sc.getSnippets(), initialBandwidth);
				patterns.addAll( splitter.getPatterns() );
				
			}
		}
		
		long end = System.currentTimeMillis();
		timeSplitterFine = (end - start) / 1000.0;
		System.out.println("Done Refining Coarse Patterns.");
		return patterns;
	}

	private void writePatterns(List<SnippetCluster> patterns) throws Exception {
		for( SnippetCluster sc : patterns ) {
			if( sc.getSnippetLength()>=2 ) {
				GroupSequence gs = sc.toGroupSequence();
				gs.write(groupFile, patternFile);
			}
		}
	}
	
	// Only count patterns with a length larger than 2.
	private int computePatternCount(List<SnippetCluster> patterns) {
		int count = 0;
		for(SnippetCluster sc: patterns)
			if(sc.getSnippetLength()>=2)
				count += 1;
		return count;
	}
	
	private int computeCoverage(List<SnippetCluster> patterns) {
		int coverage = 0;
		for(SnippetCluster sc: patterns)
			if(sc.getSnippetLength()>=2)
				coverage += sc.computeSuppport();
		return coverage;
	}
	
	//average variance
	private double computeVariance(List<SnippetCluster> patterns) {
		double variance = 0;
		int patternCount = computePatternCount(patterns);
		for(SnippetCluster sc: patterns)
			if(sc.getSnippetLength()>=2) {
				variance += sc.computeVaraince(mPlaces);
			}
		return variance / patternCount;
	}


}
