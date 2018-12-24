package NPrefixspan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import NDatabase.NDatabase;
import NDatabase.NPlace;
import NDatabase.NSequence;
import NDatabase.NTrajectory;

public class NPrefixspanAlgo {
	private List<NSnippetCluster> results = null;
	int patternlength;

	// minimum support
	private double minsup;
	private int maxInterval;
	// absolute value of minimum support
	private int minsuppRelative;

	public NPrefixspanAlgo(double minsup, int maxInterval,int patternlength) {
		this.minsup = minsup;
		this.maxInterval = maxInterval;
		this.patternlength = patternlength;
	}

	public void runAlgorithm(NDatabase database) {
		results = new ArrayList<NSnippetCluster>();
		this.minsuppRelative = (int) minsup;
		if (this.minsuppRelative == 0) { // protection
			this.minsuppRelative = 1;
		}

		InitialProjection(database);
	}

	public void InitialProjection(NDatabase d) {
		Map<NSequence, Set<Integer>> frequent = getFrequentItem(d);
	
		for(Entry<NSequence, Set<Integer>> entry : frequent.entrySet()){
			if(entry.getValue().size() >= this.minsuppRelative){
				// build the projected context
				NSequence item = entry.getKey();
				NSequence newItem = new NSequence();
				newItem.place=item.place;
				newItem.time=item.time;
				newItem.pattern.add(item.place);
				NProjectedDatabase S = new NProjectedDatabase();
				Map<NPlace,List<Integer>> cluster = S.projection(d,newItem);
				createCluster(newItem,cluster);
				prefixSpan(newItem,2, S, this.maxInterval);
			}
		}
		
	}
	
	public void prefixSpan(NSequence seq, int level, NProjectedDatabase S, int time) {
		Map<NSequence, Set<Integer>> frequent = getFrequentPair(S,time);
		for(Entry<NSequence, Set<Integer>> entry : frequent.entrySet()){
				if(entry.getValue().size() >= this.minsuppRelative){
					// build the projected context
					NSequence item = entry.getKey();
					NSequence newItem = new NSequence();
					newItem.pattern.addAll(seq.pattern);
					newItem.place=seq.place;
					newItem.time=seq.time;
					newItem.pattern.add(item.place);
					
					NProjectedDatabase Sa = new NProjectedDatabase();
					Map<List<NPlace>,List<Integer>> cluster = Sa.pseudoProjection(S,item,time);
					createClusterPrefix(newItem,cluster);
					prefixSpan(newItem, level+1, Sa, time);
				}
				
			}
	}
	
	
	
	public void createClusterPrefix(NSequence ns, Map<List<NPlace>,List<Integer>> ss){
		NSnippetCluster nsc = new NSnippetCluster();
		for(Entry<List<NPlace>, List<Integer>> entry : ss.entrySet()){
			List<NPlace> places = new ArrayList<NPlace>();
			places.addAll(entry.getKey());
			Set<Integer> mVisitors = new HashSet<Integer>();
			mVisitors.addAll(entry.getValue());
			nsc.mSnippets.add(new NSnippet(places, mVisitors));
		}
	
		for(NPlace n:ns.pattern) {
			nsc.mGroupSequence.add(n.category.categoryId);
		}
		if(nsc.mGroupSequence.size()>=this.patternlength) {
			results.add(nsc);	
		}	
	}
	
	public void createCluster(NSequence ns, Map<NPlace,List<Integer>> ss){
		NSnippetCluster nsc = new NSnippetCluster();
		for(Entry<NPlace, List<Integer>> entry : ss.entrySet()){
			List<NPlace> places = new ArrayList<NPlace>();
			places.add(entry.getKey());
			Set<Integer> mVisitors = new HashSet<Integer>();
			mVisitors.addAll(entry.getValue());
			nsc.mSnippets.add(new NSnippet(places, mVisitors));
		}
	
		for(NPlace n:ns.pattern) {
			nsc.mGroupSequence.add(n.category.categoryId);
		}
		
		if(nsc.mGroupSequence.size()>=this.patternlength) {
			results.add(nsc);	
		}		
	}
	
	public Map<NSequence, Set<Integer>> getFrequentPair(NProjectedDatabase s, int time){
		Set<Integer> alreadyCounted = new HashSet<Integer>();
		Map<NSequence, Set<Integer>> frequent = new HashMap<NSequence, Set<Integer>>();		
		for(NTrajectory t : s.trajectories) {
			alreadyCounted.clear();
			for(NSequence ns : t.trajectory) {
				if (!alreadyCounted.contains(ns.place.getCategory().categoryId)) {
						if((ns.getTime()-t.baseline.get(t.baseline.size()-1).getTime())<=time) {
							Set<Integer> groub = frequent.get(getSequenceforFreq(frequent, ns));
							if (groub == null) {
								groub = new HashSet<Integer>();
								frequent.put(ns, groub);
							}
							groub.add(t.trajectoryId);
							alreadyCounted.add(ns.place.category.categoryId);	
						}
				}
			}
		
		}
		return frequent;
	}

	public Map<NSequence, Set<Integer>> getFrequentItem(NDatabase db) {
		Set<Integer> alreadyCounted = new HashSet<Integer>();
		Map<NSequence, Set<Integer>> frequent = new HashMap<NSequence, Set<Integer>>();
		for (NTrajectory t : db.trajectories) {
			alreadyCounted.clear();
			for (NSequence sc : t.trajectory) {
				if (!alreadyCounted.contains(sc.place.getCategory().categoryId)) {
					Set<Integer> groub = frequent.get(getSequenceforFreq(frequent, sc));
					if (groub == null) {
						groub = new HashSet<Integer>();
						frequent.put(sc, groub);
					}
					groub.add(t.trajectoryId);
					alreadyCounted.add(sc.place.category.categoryId);					
				}
			}
		}
		

		return frequent;
	}
	
	
	public NSequence getSequenceforFreq(Map<NSequence, Set<Integer>> db, NSequence c){
		for(NSequence x:db.keySet()) {
			if (x.equals(c)) {
				return x;
			}
		}
		return null;
	}
	
	public List<Integer> getFrequent(List<NTrajectory> trajectories, int threshold){
		Map<Integer, Integer> frequent = new HashMap<Integer, Integer>();
		for (NTrajectory t : trajectories) {
			List<Integer> counted = new ArrayList<Integer>();
			for (NSequence c : t.trajectory) {
				if (!counted.contains(c.place.category.categoryId)) {
					if (!frequent.containsKey(c.place.category.categoryId)) {
						frequent.put(c.place.category.categoryId, 0);

					} else {
						frequent.replace(c.place.category.categoryId,
								frequent.get(c.place.category.categoryId).intValue() + 1);
					}
					counted.add(c.place.category.categoryId);
				}

			}
		}

		frequent.values().removeIf(e -> (e < threshold));
		List<Integer> list = new ArrayList<Integer>(frequent.keySet());
		return list;
	}
	
	public List<NSnippetCluster> getCoarsePattern() {
		return this.results;
	}
	
	public void view() {
		for(NSnippetCluster p : results) {
			p.view();
		}
	}
}
