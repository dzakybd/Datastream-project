package prefixspan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import split.SnippetCluster;
import split.Snippet;

/*** 
 * This is an implementation of the PrefixSpan algorithm by Pei et al. 2001
 * This implementation is part of the SPMF framework.
 * 
 * This implemtation uses pseudo-projection as suggested by Pei et al. 2001
 * @author Philippe Fournier-Viger
 **/

public class AlgoPrefixSpan{
	
	// The sequential patterns that are found
	private Sequences patterns = null;
	private List<SnippetCluster> results = null;
	
	// for statistics
	private long startTime;
	
	// minimum support
	private double minsup;
	
	private double maxInterval;

	//absolute value of minimum support
	private int minsuppRelative;
		
	public AlgoPrefixSpan(double minsup, double maxInterval){
		this.minsup = minsup;
		this.maxInterval = maxInterval;
	}

	public Sequences runAlgorithm(SequenceDatabase database) {
		patterns = new Sequences("FREQUENT SEQUENTIAL PATTERNS");
		results = new ArrayList<SnippetCluster>();
		this.minsuppRelative = (int) Math.ceil(minsup* database.size());
		if(this.minsuppRelative == 0){ // protection
			this.minsuppRelative = 1;
		}
		startTime = System.currentTimeMillis();
		prefixSpan(database);
		return patterns;
	}
	
	/**
	 * @param contexte The initial context.
	 */
	private void prefixSpan(SequenceDatabase database){
		// We have to scan the database to find all frequent patterns of size 1.
		// We note the sequences in which these patterns appear.
		Map<Item, Set<Integer>> mapSequenceID = findSequencesContainingItems(database);
		
		// WE CONVERT THE DATABASE IN A PSEUDO-DATABASE, AND REMOVE
		// THE ITEMS OF SIZE 1 THAT ARE NOT FREQUENT, SO THAT THE ALGORITHM 
		// WILL NOT CONSIDER THEM ANYMORE. (OPTIMIZATION : OCTOBER-08 )
		PseudoSequenceDatabase initialContext = new PseudoSequenceDatabase();
		for(Sequence sequence : database.getSequences()){
			Sequence optimizedSequence = sequence.cloneSequenceMinusItems(mapSequenceID, minsuppRelative);
			if(optimizedSequence.size() != 0){
				initialContext.addSequence(new PseudoSequence(0, optimizedSequence, 0, 0,-1));
			}
		}
		
		// For each item
		for(Entry<Item, Set<Integer>> entry : mapSequenceID.entrySet()){
			if(entry.getValue().size() >= minsuppRelative){ // if the item is frequent
				// build the projected context
				Item item = entry.getKey();
				
				PseudoSequenceDatabase unfilterProjectedContext = buildProjectedContext(item, initialContext,  false);
				createSnippetCluster(unfilterProjectedContext);
				PseudoSequenceDatabase projectedContext = filterProjectedContext(unfilterProjectedContext);
				
				// Create the prefix for the projected context.
				Sequence prefix = new Sequence(0);  
				prefix.addItemset(new Itemset(item));
				prefix.setSequencesID(entry.getValue());

				patterns.addSequence(prefix, 1);  // we found a sequence. Level: 1
				
				// Recursive call !
				recursion(prefix, 2, projectedContext); 		
			}
		}
	}
	
	private void createSnippetCluster(PseudoSequenceDatabase database){
		if (database.getPseudoSequences().size()>0){
			Map<List<Integer>,Set<Integer>> snippetdata = new HashMap<List<Integer>,Set<Integer>>();
			for(PseudoSequence sequence : database.getPseudoSequences()){
				List<Integer> prefixplaces = new ArrayList<Integer>();
				for (Itemset prefixitemset : sequence.getPrevitemset()){
					prefixplaces.add(prefixitemset.get(0).getRawId());
				}
				Set<Integer> prefixvalue = snippetdata.get(prefixplaces);
				if (prefixvalue == null){
					Set<Integer> addprefixvalue = new HashSet<Integer>();
					addprefixvalue.add(sequence.getId());
					snippetdata.put(prefixplaces, addprefixvalue);
				}
				else{
					prefixvalue.add(sequence.getId());
				}
			}
			SnippetCluster newSnippetCluster = new SnippetCluster(database.getPseudoSequences().get(0).getPrevitemset().size());
			for (Itemset prefixitemset : database.getPseudoSequences().get(0).getPrevitemset()){
				newSnippetCluster.addGroupID( prefixitemset.get(0).getId() );
			}
			for(Entry<List<Integer>,Set<Integer>> snippetentry : snippetdata.entrySet()){
				Snippet newSnippet = new Snippet(snippetentry.getKey(),snippetentry.getValue());
				newSnippetCluster.addSnippet(newSnippet);
			}
			results.add(newSnippetCluster);
		}
	}
	
	/**
	 * Find all sequences that contains an item.
	 * @param contexte Le contexte
	 * @return Map of items and Set of sequences that contains each of them.
	 */
	private Map<Item, Set<Integer>> findSequencesContainingItems(SequenceDatabase contexte) {
		Set<Integer> alreadyCounted = new HashSet<Integer>(); // il faut compter un item qu'une fois par s子uence.
		Sequence lastSequence = null;
		Map<Item, Set<Integer>> mapSequenceID = new HashMap<Item, Set<Integer>>(); // pour conserver les ID des s子uences: <Id Item, Set d'id de s子uences>
		for(Sequence sequence : contexte.getSequences()){
			if(lastSequence == null || lastSequence.getId() != sequence.getId()){ // FIX
				alreadyCounted.clear(); 
				lastSequence = sequence;
			}
			for(Itemset itemset : sequence.getItemsets()){
				for(Item item : itemset.getItems()){
					if(!alreadyCounted.contains(item.getId())){
						Set<Integer> sequenceIDs = mapSequenceID.get(item);
						if(sequenceIDs == null){
							sequenceIDs = new HashSet<Integer>();
							mapSequenceID.put(item, sequenceIDs);
						}
						sequenceIDs.add(sequence.getId());
						alreadyCounted.add(item.getId()); 
					}
				}
			}
		}
		return mapSequenceID;
	}
	

	/**
	 * Create a projected database by pseudo-projection
	 * @param item The item to use to make the pseudo-projection
	 * @param context The current database.
	 * @param inSuffix This boolean indicates if the item "item" is part of a suffix or not.
	 * @return the projected database.
	 */
	private PseudoSequenceDatabase buildProjectedContext(Item item, PseudoSequenceDatabase database, boolean inSuffix) {
		// The projected pseudo-database
		PseudoSequenceDatabase sequenceDatabase = new PseudoSequenceDatabase();
		for(PseudoSequence sequence : database.getPseudoSequences()){ // for each sequence
			for(int i =0; i< sequence.size(); i++){  // for each item of the sequence
				
				// if the itemset contains the item
				int index = sequence.indexOf(i, item.getId());
				if(index != -1 && sequence.isPostfix(i) == inSuffix){
					if (sequence.getPrevtimestamp() == -1 || sequence.getItemset(i).getTimestamp()-sequence.getPrevtimestamp() <= maxInterval){
						if(index != sequence.getSizeOfItemsetAt(i)-1){ // if this is not the last item of the itemset
							PseudoSequence newSequence = new PseudoSequence(sequence, i, index+1,sequence.getItemset(i).getTimestamp());
							newSequence.addprevitemset(sequence.getItemset(i));
							if(newSequence.size() >0){
								sequenceDatabase.addSequence(newSequence);
							} 
						}else if ((i != sequence.size()-1)){// if this is not the last itemset of the sequence			 
							PseudoSequence newSequence = new PseudoSequence( sequence, i+1, 0,sequence.getItemset(i).getTimestamp());
							newSequence.addprevitemset(sequence.getItemset(i));
							if(newSequence.size() >0){
								sequenceDatabase.addSequence(newSequence);
							}	
						}
						else{
							PseudoSequence newSequence = new PseudoSequence( sequence, i, 0, -2);
							newSequence.addprevitemset(sequence.getItemset(i));
							if(newSequence.size() >0){
								sequenceDatabase.addSequence(newSequence);
							}	
						}
					}
				}
			}
		}
		return sequenceDatabase;
	}
	
	private PseudoSequenceDatabase filterProjectedContext(PseudoSequenceDatabase database) {
		PseudoSequenceDatabase sequenceDatabase = new PseudoSequenceDatabase();
		for(PseudoSequence sequence : database.getPseudoSequences()){ // for each sequence
			if (sequence.getPrevtimestamp() != -2){
				sequenceDatabase.addSequence(sequence);
			}
		}
		return sequenceDatabase;
	}
	
	private void recursion(Sequence prefix, int k, PseudoSequenceDatabase contexte) {
		// find frequent items of size 1.
		Set<Pair> pairs = findAllFrequentPairs(prefix, contexte.getPseudoSequences());
		
		// For each pair found, 
		for(Pair paire : pairs){
			// if the item is freuqent.
			if(paire.getCount() >= minsuppRelative){
				// create the new postfix
				Sequence newPrefix;
				if(paire.isPostfix()){ // if the item is part of a postfix
					newPrefix = appendItemToPrefixOfSequence(prefix, paire.getItem()); // is =<is, (deltaT,i)>
				}else{ // else
					newPrefix = appendItemToSequence(prefix, paire.getItem());
				}
				
				PseudoSequenceDatabase unfilterProjectedContext = buildProjectedContext(paire.getItem(), contexte, paire.isPostfix());
				createSnippetCluster(unfilterProjectedContext);
				PseudoSequenceDatabase projectedContext = filterProjectedContext(unfilterProjectedContext);
				
				// create new prefix
				Sequence prefix2 = newPrefix.cloneSequence();
				prefix2.setSequencesID(paire.getSequencesID()); 
				
				// It is a projection with recursion by calling the prefix
				patterns.addSequence(prefix2, prefix2.size());  
				recursion(prefix2, k+1, projectedContext); // r残ursion
			}
		}
	}
	
	/**
	 * Method to find all frequent items in a context (database).
	 * This is for k> 1.
	 * @param prefix
	 * @param sequences
	 * @return
	 */
	protected Set<Pair> findAllFrequentPairs(Sequence prefix, List<PseudoSequence> sequences){
		// we will scan the database and store the cumulative support of each pair
		// in a map.
		Map<Pair, Pair> mapPairs = new HashMap<Pair, Pair>();
		
		PseudoSequence lastSequence = null;
		Set<Pair> alreadyCountedForSequenceID = new HashSet<Pair>(); // to count each item only one time for each sequence ID

		for(PseudoSequence sequence : sequences){
			// if the sequence does not have the same id, we clear the map.
			if(sequence != lastSequence){
				alreadyCountedForSequenceID.clear(); 
				lastSequence = sequence;
			}

			for(int i=0; i< sequence.size(); i++){
				if (sequence.getPrevtimestamp() == -1 || sequence.getItemset(i).getTimestamp()-sequence.getPrevtimestamp() <= maxInterval){
					for(int j=0; j < sequence.getSizeOfItemsetAt(i); j++){
						Item item = sequence.getItemAtInItemsetAt(j, i);
						Pair paire = new Pair(false, sequence.isPostfix(i), item);   // false is ok?
						Pair oldPaire = mapPairs.get(paire);
						if(!alreadyCountedForSequenceID.contains(paire)){
							if(oldPaire == null){
								mapPairs.put(paire, paire);
							}else{
								paire = oldPaire;
							}
							alreadyCountedForSequenceID.add(paire);
							// we keep the sequence id
							paire.getSequencesID().add(sequence.getId());
						}
					}
				}
			}
		}
		return mapPairs.keySet();
	}

	// This method takes as parameters : a sequence, an item, and the item support.
	// It creates a copy of the sequence and add the item to the sequence. It sets the 
	// support of the sequence as the support of the item.
	private Sequence appendItemToSequence(Sequence prefix, Item item) {
		Sequence newPrefix = prefix.cloneSequence();  // isSuffix
		newPrefix.addItemset(new Itemset(item));  // cr試 un nouvel itemset   + decalage
		return newPrefix;
	}
	
	// This method takes as parameters : a sequence, an item, and the item support.
	// It creates a copy of the sequence and add the item to the last itemset of the sequence. 
	// It sets the support of the sequence as the support of the item.
	private Sequence appendItemToPrefixOfSequence(Sequence prefix, Item item) {
		Sequence newPrefix = prefix.cloneSequence();
		Itemset itemset = newPrefix.get(newPrefix.size()-1);  // ajoute au dernier itemset
		itemset.addItem(item);   
		return newPrefix;
	}

	public void printStatistics(int size, StringBuilder out) {
		//StringBuffer r = new StringBuffer(200);
		out.append("=============  Algorithm - STATISTICS =============\n Total time ~ ");
		out.append(System.currentTimeMillis() - startTime);
		out.append(" ms\n");
		out.append(" Frequent sequences count : ");
		out.append(patterns.sequenceCount);
		out.append('\n');
		out.append(patterns.toString(size));
		out.append("===================================================\n");
		System.out.println( out );
	}
	
	public HashMap<List<List<Integer>>, Integer> getStatistics(int size) {
		HashMap<List<List<Integer>>, Integer> results = new HashMap<List<List<Integer>>, Integer>();
		results = patterns.toStatistics(size);
		return results;
	}
	
	public double getMinSupp() {
		return minsup;
	}
	
	public void setMinSup(double minSup) {
		this.minsup = minSup;
	}

	public double getMaxInterval() {
		return maxInterval;
	}

	public void setMaxInterval(double maxInterval) {
		this.maxInterval = maxInterval;
	}
	
	public List<SnippetCluster> getSnippetClusters() {
		return results;
	}

	public void setResult(List<SnippetCluster> results) {
		this.results = results;
	}
}
