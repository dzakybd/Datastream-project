package Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sequence {
	public long shift = 0;
	
	public final List<Itemset> itemsets = new ArrayList<Itemset>();
	public int id; // id de la sequence
	
//	// List of IDS of all patterns that contains this one.
//	public Set<Integer> sequencesID = null;
	// List of IDS of all patterns that contains this one. =====projected
		private Set<Integer> sequencesID = null;
	public Sequence(int id){
		this.id = id;
	}
	
	public void setItemIds(Places places) {
		for(Itemset itemset: itemsets) {
			itemset.setItemIds(places);
		}
	}
	
	public int getId() {
		return id;
	}
	
	public int size(){
		return itemsets.size();
	}
	
	public List<Itemset> getItemsets() {
		return itemsets;
	}
	

	public Sequence cloneSequenceMinusItems(Map<Item, Set<Integer>> mapSequenceID, double relativeMinSup) {
		Sequence sequence = new Sequence(getId());
		for(Itemset itemset : itemsets){
			Itemset newItemset = itemset.cloneItemSetMinusItems(mapSequenceID, relativeMinSup);
			if(newItemset.size() !=0){ 
				sequence.itemsets.add(newItemset);
			}
		}
		return sequence;
	}
	

	public void addItemset(Itemset itemset) {
		itemsets.add(itemset);
	}
	
	
	public void setSequencesID(Set<Integer> sequencesID) {
		this.sequencesID = sequencesID;
	}
	
	public void view() {
		System.out.print("S id: " + id +" = ");
		for(Itemset ts:itemsets) {
			ts.view();
		}
	}
	
	//use by projectedDatabase
	public Itemset get(int index) {
		return itemsets.get(index);
	}

	public Sequence cloneSequence(){
		Sequence sequence = new Sequence(getId());
		for(Itemset itemset : itemsets){
			sequence.addItemset(itemset.cloneItemSet());
		}
		return sequence;
	}
}
