package finegrained;

import java.util.ArrayList;
import java.util.List;

import Database.Group;
import Database.Groups;
import Database.Itemset;
import Database.Places;
import Database.Sequence;
import Database.SequenceDatabase;
import NPrefixspan.NSnippetCluster;
import Refine.Splitter;
import Spade.Spade;
import coarsepattern.GroupSequence;
import coarsepattern.PrefixSpan;
import coarsepattern.SnippetCluster;


public class Processing {

	SequenceDatabase mDatabase = null;
	int mDataSize;
	Places mPlaces = null;
	Groups mGroups = null;

	double minSup;
	int maxInterval;
	double varThreshold;
	double tau;
	double initialBandwidth;
	private List<SnippetCluster> results = null;
	
//	String groupFile;
//	String patternFile;
//	
//	//statistics
//	double timeSplitterCoarse = 0;
//	double timeSplitterFine = 0;
//
//	int coverageSplitter = 0;
//
//	int patternCountSplitter = 0;
//
//	double varianceSplitter = 0;
	
	public void setParams(String minSup, String maxInterval, String varThreshold, String tau, String initialBandwidth) {
		this.minSup=new Double(minSup);
		this.maxInterval=Integer.parseInt(maxInterval);
		this.varThreshold=new Double(varThreshold);
		this.tau=new Double(tau);
		this.initialBandwidth=new Double(initialBandwidth);
	}
	
	public void loadDatabase(String dataFile) throws Exception {
		mDatabase = new SequenceDatabase();
		mDatabase.loadFile(dataFile);
		mDataSize = mDatabase.getSequences().size(); //database size
	}
	
	public void loadPlaces(String placeFile) throws Exception {
		mPlaces = new Places();
		mPlaces.loadFile(placeFile);
	}
	
	public void splitterMiningwithPrefix() throws Exception {
		List<SnippetCluster> coarsePatterns = mineCoarsePatterns(minSup, maxInterval);
		this.results=coarsePatterns;
//		writePatterns(coarsePatterns);
		List<SnippetCluster> refinePattern = refinePattern(coarsePatterns, (int) minSup, varThreshold,this.tau,initialBandwidth);
		System.out.println(refinePattern.size());
//		for(SnippetCluster s:refinePattern) {
//			System.out.println(s.getSnippets().size());
//		}
	}
	
	
	
	public List<SnippetCluster> refinePattern(List<SnippetCluster> snippetClusters, int minAbsSup, double varThreshold, double tau, double initialBandwidth) throws Exception {
		System.out.println("Begin Refining Coarse Patterns...");
		long start = System.currentTimeMillis();
//		System.out.println(snippetClusters.size());
//		System.out.println(minAbsSup);
//		System.out.println(varThreshold);
//		System.out.println(tau);
//		System.out.println(initialBandwidth);
//		System.out.println(mPlaces.places.size());
		
		Splitter splitter = new Splitter(minAbsSup, varThreshold, tau, mPlaces);
		List<SnippetCluster> patterns = new ArrayList<SnippetCluster>(); //fine patterns are represented as group sequences
		
		int i =0;
//		viewCoarse();
		for(SnippetCluster sc : snippetClusters) {
			if(sc.getSnippetLength() >= 3) {
//				sc.view();
//				System.out.println();
//				System.out.println(sc.getSnippetLength());
//				System.out.println(sc.getSnippets().size());
				splitter.minePatterns(sc.getSnippetLength(), sc.getSnippets(), initialBandwidth);
				patterns.addAll( splitter.getPatterns() );
			}
//			if(i==1) {
//				break;
//			}
			i++;
		}
		System.out.println(patterns.size());
		for(SnippetCluster S : patterns) {
			S.getSnippets().size();
		}
		
		long end = System.currentTimeMillis();
//		timeSplitterFine = (end - start) / 1000.0;
		System.out.println("Done Refining Coarse Patterns.");
//		System.out.println(patterns);
		return patterns;
	}
	
	
	private void writePatterns(List<SnippetCluster> patterns) throws Exception {
		for( SnippetCluster sc : patterns ) {
			sc.view();
		}
	}
	
//	public void combinedDatabasePlace() {
//		for(Sequence cc : mDatabase.getSequences()){
//			for(Itemset s : cc.itemsets) {
//				s.items.get(0).
//			}
//		}
//	}
	
	/*
	 * Evaluate Splitter.
	 */
	public void runSplitter() throws Exception {
		
//		Spade cmSpade = new Spade(minSup, false);
//		cmSpade.runAlgorithm(mDatabase,maxInterval);
		
		
//		writePatterns(coarsePatterns);
//		List<SnippetCluster> finePatterns = refinePattern(coarsePatterns, (int)(minSup * mDataSize), varThreshold, tau, initialBandwidth);
//
//		coverageSplitter = computeCoverage( finePatterns );
//		patternCountSplitter = computePatternCount( finePatterns );
//		varianceSplitter = computeVariance( finePatterns );
	}
	
	public void initializeGroups(int level) {
		mGroups = new Groups();
		Group initialGroup = new Group();
		for (int i = 0; i < mPlaces.getPlaces().size(); i++) {
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
		PrefixSpan prefixspan = new PrefixSpan(minSup, maxInterval);
//		long start = System.currentTimeMillis();
		prefixspan.runAlgorithm(mDatabase);
		
		
//		long end = System.currentTimeMillis();
//		timeSplitterCoarse = (end - start) / 1000.0;
		
		List<SnippetCluster> coarsePatterns = prefixspan.getSnippetClusters();
		System.out.println("Done Mining Coarse Patterns. Pattern Number: " + coarsePatterns.size());
		return coarsePatterns;
	}
	
	
	
	

	public int getmDataSize() {
		return mDataSize;
	}
	
	public List<SnippetCluster> getCoarsePattern(){
		return this.results;
	}
	public void viewCoarse() {
		for(SnippetCluster s:this.results) {
			s.view();
			System.out.println();
		}
	}
	


	
}
