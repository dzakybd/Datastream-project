//package Spade;
//
//import java.io.IOException;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import Database.Item;
//import Database.Itemset;
//import Database.Sequence;
//import Database.SequenceDatabase;
//import coarsepattern.Sequences;
//
//public class Spade {
//	// The sequential patterns that are found
//	private Sequences patterns = null;
//	
//	private final List<Sequence> sequences = new ArrayList<Sequence>();
//	
//	private List<SpadeGroups> tempDatabase = new ArrayList<SpadeGroups>();
//	
//	
//	private int intersectionCounter = 0;
//	/**
//	 * the minimum support threshold
//	 */
//	protected double minSupRelative;
//	/**
//	 * The absolute minimum support threshold, i.e. the minimum number of sequences
//	 * where the patterns have to be
//	 */
//	public double minSupAbsolute;
//	/**
//	 * Flag indicating if we want a depth-first search when true. Otherwise we say
//	 * that we want a breadth-first search
//	 */
//	protected boolean dfs;
//
//	/**
//	 * Saver variable to decide where the user want to save the results, if it the
//	 * case
//	 */
//
//	public Spade(double support, boolean dfs) {
//
//		this.minSupRelative = support;
//		this.dfs = dfs;
//	}
//
//	public void runAlgorithm(SequenceDatabase database, int time) throws IOException {
//		// If we do no have any file path
//
////		this.minSupAbsolute = (int) Math.ceil(minSupRelative * database.getSequences().size());
//		this.minSupAbsolute = minSupRelative;
//		if (this.minSupAbsolute == 0) { // protection
//			this.minSupAbsolute = 1;
//		}
////		runSPADE(database);
////           // reset the stats about memory usage 
////           MemoryLogger.getInstance().reset(); 
////           //keeping the starting time 
////           start = System.currentTimeMillis(); 
////           //We run SPADE algorithm 
//		convertVertically(database,(int) this.minSupAbsolute);
//		runSPADE(this.tempDatabase,time,(int) this.minSupAbsolute);
//		List<SpadeTable> tt = new ArrayList<SpadeTable>();
//		for(SpadeGroups sg : this.tempDatabase) {
//			for (SpadeTable t:sg.groups) {
////				t.view();
//				if(t.item.size()>1) {
//					tt.add(t);
//				}
//			}
//		}
//		for(SpadeTable f:tt) {
//			f.view();
//		}
//////           //keeping the ending time 
////           end = System.currentTimeMillis(); 
////           //Search for frequent patterns: Finished 
////           saver.finish(); 
//	}
//
//	public void convertVertically(SequenceDatabase database,int treshold) {
//		Map<Integer, SpadeGroups> tempPattern = new HashMap<Integer, SpadeGroups>();
//		// Map.keys is group category
//		int trIteration = 1;
//		for (Sequence tr : database.getSequences()) {
//			int secIteration = 1;
//			for (Itemset sec : tr.itemsets) {
//				if (!tempPattern.containsKey(sec.items.get(0).getId())) {
//					SpadeTable sG = new SpadeTable(trIteration,new SpadeItem(secIteration, sec.timestamp, sec.items.get(0)));
//					tempPattern.put(sec.items.get(0).getId(), new SpadeGroups(sG));
//				} else {
//					SpadeTable sG = new SpadeTable(trIteration,new SpadeItem(secIteration, sec.timestamp, sec.items.get(0)));
//					tempPattern.get(sec.items.get(0).getId()).groups.add(sG);
//					
//				}
//				secIteration++;
//			}
//			trIteration++;
//		}
//		this.tempDatabase = new ArrayList<SpadeGroups>(tempPattern.values());
//		this.tempDatabase.removeIf(e->(e.groups.size()<treshold));
//	}
//
//	protected void runSPADE(List<SpadeGroups> tempDatabase, int time, int treshold) {
//
//		for (int i = 0; i < 1; i++) {
//			List<SpadeGroups> T = new ArrayList<SpadeGroups>();
////			for(SpadeTable a:tempDatabase.get(i).groups) {
////				a.view();
////				System.out.println();
////			}
//			for (int j = i+1; j < tempDatabase.size(); j++) {
//				List<SpadeTable> R = new ArrayList<SpadeTable>();
//				List<SpadeTable> A = new ArrayList<SpadeTable>(tempDatabase.get(i).groups);
//				List<SpadeTable> B = new ArrayList<SpadeTable>(tempDatabase.get(j).groups);
//				R = mergeArray(A,B,time,treshold);
//				for(SpadeTable t:R) {
////					t.view();
////					if (R.size() >= treshold) {
////						T.add(new SpadeGroups(t));
////					}
//				}
//			}
////			runSPADE(T, time,treshold);
//		}
//	}
//
//	public List<SpadeTable> mergeArray(List<SpadeTable> A, List<SpadeTable> B, int time, int Threshold) {
//		List<SpadeTable> R = new ArrayList<SpadeTable>();
////		List<SpadeTable> tempA = new ArrayList<SpadeTable>();
////		tempA.addAll(A);
//		for (SpadeTable spA : A) {
//			List<SpadeTable> tempB = new ArrayList<SpadeTable>();
//			tempB.addAll(B);
//			tempB.removeIf(e->(e.SID != spA.SID));
//
//			for (SpadeTable spB : tempB) {
//				if (spA.item.get(spA.item.size()-1).EID < spB.item.get(spB.item.size()-1).EID && (spB.item.get(spB.item.size()-1).time - spA.item.get(0).time) <= time) {
//					if(spA.item.get(spA.item.size()-1).item.getId()!=spB.item.get(spB.item.size()-1).item.getId()) {
//						spA.item.add(spB.item.get(spB.item.size()-1));
//						R.add(spA);
//						System.out.println(R);
//					}
//					
//				}
//			}
//			break;
//		}
//		return R;
//	}
//
//	public void view2(Map<Integer, List<SpadeTable>> tempPattern) {
//
//		for (List<SpadeTable> ss : tempPattern.values()) {
//			for (SpadeTable c : ss) {
//				c.view();
//				System.out.println();
//			}
//			System.out.println();
//		}
//
//	}
//	
//	public void view() {
//		int i=0;
//		for (SpadeGroups s:tempDatabase) {
//			System.out.println(i);
//			for (SpadeTable c : s.groups) {
//				c.view();
//				System.out.println();
//			}
//			System.out.println();
//			i++;
//		}
//	}
//	
////	 protected void runSPADE(SequenceDatabase database) { 
////	        //We get the equivalence classes formed by the frequent 1-patterns 
//////	        frequentItems = database.frequentItems(); 
//////	        //We extract their patterns 
//////	        Collection<Pattern> size1sequences = getPatterns(frequentItems); 
////	     
////	        //  NEW-CODE-PFV 2013 
////	        // Map: key: item   value:  another item that followed the first item + support 
////	        // (could be replaced with a triangular matrix...) 
////	        Map<Integer, Map<Integer, Integer>> coocMapAfter = new HashMap<Integer, Map<Integer, Integer>>(1000); 
////	        Map<Integer, Map<Integer, Integer>> coocMapEquals = new HashMap<Integer, Map<Integer, Integer>>(1000); 
////	 
////	        // update COOC map 
//////	        database.view();
////	        for (Sequence seq : database.getSequences()) { 
////	            HashSet<Integer> alreadySeenA = new HashSet<Integer>(); 
////	            Map<Integer, Set<Integer>> alreadySeenB_equals = new HashMap<>(); 
////	 
////	            // for each item 
////	            for (int i = 0; i < seq.getItemsets().size(); i++) { 
////	                Itemset itemsetA = seq.get(i); 
////	              
////	                for (int j = 0; j < itemsetA.size(); j++) { 
////	                    Integer itemA = (Integer) itemsetA.get(j).getId(); 
////	                    boolean alreadyDoneForItemA = false; 
////	                    Set equalSet = alreadySeenB_equals.get(itemA);
////	                    if (equalSet == null) { 
////	                        equalSet = new HashSet(); 
////	                        alreadySeenB_equals.put(itemA, equalSet); 
////	                    } 
////	 
////	                    if (alreadySeenA.contains(itemA)) { 
////	                        alreadyDoneForItemA = true; 
////	                    } 
////	 
////	                    // create the map if not existing already 
////	                    Map<Integer, Integer> mapCoocItemEquals = coocMapEquals.get(itemA); 
////	                    // create the map if not existing already 
////	                    Map<Integer, Integer> mapCoocItemAfter = null; 
////	                    if (!alreadyDoneForItemA) { 
////	                        mapCoocItemAfter = coocMapAfter.get(itemA); 
////	                    } 
////	 
////	                    //For each item after itemA in the same itemset 
////	                    for (int k = j + 1; k < itemsetA.size(); k++) { 
////	                        Integer itemB = (Integer) itemsetA.get(k).getId(); 
////	                        if (!equalSet.contains(itemB)) { 
////	                            if (mapCoocItemEquals == null) { 
////	                                mapCoocItemEquals = new HashMap<Integer, Integer>(); 
////	                                coocMapEquals.put(itemA, mapCoocItemEquals); 
////	                            } 
////	                            Integer frequency = mapCoocItemEquals.get(itemB); 
////	 
////	                            if (frequency == null) { 
////	                                mapCoocItemEquals.put(itemB, 1); 
////	                            } else { 
////	                                mapCoocItemEquals.put(itemB, frequency + 1); 
////	                            } 
////	                             
////	                            equalSet.add(itemB); 
////	                        } 
////	                    } 
////	 
////	                    HashSet<Integer> alreadySeenB_after = new HashSet<Integer>(); 
////	                    // for each item after 
////	                    if (!alreadyDoneForItemA) { 
////	                        for (int k = i + 1; k < seq.getItemsets().size(); k++) { 
////	                            Itemset itemsetB = seq.get(k); 
////	                            for (int m = 0; m < itemsetB.size(); m++) { 
////	                                Integer itemB = (Integer) itemsetB.get(m).getId(); 
////	                                if (alreadySeenB_after.contains(itemB)) { 
////	                                    continue; 
////	                                } 
////	 
////	                                if (mapCoocItemAfter == null) { 
////	                                    mapCoocItemAfter = new HashMap<Integer, Integer>(); 
////	                                    coocMapAfter.put(itemA, mapCoocItemAfter); 
////	                                } 
////	                                Integer frequency = mapCoocItemAfter.get(itemB); 
////	                                if (frequency == null) { 
////	                                    mapCoocItemAfter.put(itemB, 1); 
////	                                } else { 
////	                                    mapCoocItemAfter.put(itemB, frequency + 1); 
////	                                } 
////	                                alreadySeenB_after.add(itemB); 
////	                            } 
////	                        } 
////	                        alreadySeenA.add(itemA); 
////	                    } 
////	                } 
////	            } 
////	        } 
////	        System.out.println(coocMapAfter);
////	        database = null; 
////	 
//////	        //We define the root class 
//////	        EquivalenceClass rootClass = new EquivalenceClass(null); 
//////	        /*And we insert the equivalence classes corresponding to the frequent         1-patterns as its members*/ 
//////	        for (EquivalenceClass atom : frequentItems) { 
//////	            rootClass.addClassMember(atom); 
//////	        } 
//////	 
//////	        //Inizialitation of the class that is in charge of find the frequent patterns 
//////	        FrequentPatternEnumeration frequentPatternEnumeration = new FrequentPatternEnumeration(candidateGenerator, minSupAbsolute, saver); 
//////	        //We set the number of frequent items to the number of frequent items 
//////	        frequentPatternEnumeration.setFrequentPatterns(frequentItems.size()); 
//////	 
//////	        //We execute the search 
//////	        frequentPatternEnumeration.execute(rootClass, dfs, keepPatterns, verbose, coocMapAfter, coocMapEquals); 
//////	 
//////	        /* Once we had finished, we keep the number of frequent patterns that we 
//////	         * finally found 
//////	         */ 
//////	        numberOfFrequentPatterns = frequentPatternEnumeration.getFrequentPatterns(); 
//////	        intersectionCounter = frequentPatternEnumeration.INTERSECTION_COUNTER; 
//////	        // check the memory usage for statistics 
//////	        MemoryLogger.getInstance().checkMemory(); 
////	    } 
//}
