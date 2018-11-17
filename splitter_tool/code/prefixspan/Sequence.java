package prefixspan;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import split.Places;
import prefixspan.Itemset;


/**
 * Implementation of a sequence.
 * A sequence is a list of itemsets.
 * @author Philippe Fournier-Viger 
 **/
public class Sequence{
	public long shift = 0;
	
	private final List<Itemset> itemsets = new ArrayList<Itemset>();
	private int id; // id de la sequence
	
	// List of IDS of all patterns that contains this one.
	private Set<Integer> sequencesID = null;
	
	public Sequence(int id){
		this.id = id;
	}
	
	public String getRelativeSupportFormated(int transactionCount) {
		double frequence = ((double)sequencesID.size()) / ((double) transactionCount);
		// pretty formating :
		DecimalFormat format = new DecimalFormat();
		format.setMinimumFractionDigits(0); 
		format.setMaximumFractionDigits(2); 
		return format.format(frequence);
	}
	
	public int getAbsoluteSupport(){
		return sequencesID.size();
	}

	public void addItemset(Itemset itemset) {
		itemsets.add(itemset);
	}
	
	public Sequence cloneSequence(){
		Sequence sequence = new Sequence(getId());
		for(Itemset itemset : itemsets){
			sequence.addItemset(itemset.cloneItemSet());
		}
		return sequence;
	}
	
	public void setItemIds(Places places) {
		for(Itemset itemset: itemsets) {
			itemset.setItemIds(places);
		}
	}

	public void print() {
		System.out.print(toString());
	}
	
	public String toString() {
		// create string buffer
		StringBuffer r = new StringBuffer("");
		// for each itemset
		for(Itemset itemset : itemsets){
			// append each item from this itemset
			for(Item item : itemset.getItems()){
				String string = item.toString();
				r.append(string);
				r.append(' ');
			}
		}
		//  print the list of IDs  of sequences that contains this pattern.
/*		if(getSequencesID() != null){
			r.append("  Sequence ID: ");
			for(Integer id : getSequencesID()){
				r.append(id);
				r.append(' ');
			}
		}
		*/
		// return the string
		return r.append("    ").toString();
	}	
	
	public List<List<Integer>> toStatistics() {
		List<List<Integer>> seq = new ArrayList<List<Integer>>();
		List<Integer> itset;
		for(Itemset itemset : itemsets){
			itset = new ArrayList<Integer>();
			for(Item item : itemset.getItems()){
					itset.add(item.getId());
			}
			System.out.println("add seq :" + itset.toString());
			seq.add(itset);
		}
		return seq;		
	}
		
	/**
	 * Return an abbreviated string representation of this sequence.
	 */
	public String toStringShort() {			// Not used
		// create string buffer
		StringBuffer r = new StringBuffer("");
		// for each itemset
		for(Itemset itemset : itemsets){
			// appennd its timestamp
			r.append("{t=");
			r.append(itemset.getTimestamp());
			r.append(", ");
			// append all items in that itemset
			for(Item item : itemset.getItems()){
				String string = item.toString();
				r.append(string);
				r.append(' ');
			}
			r.append('}');
		}
		// return the string
		return r.append("    ").toString();
	}
		//  print the list of Pattern IDs that contains this pattern.
		/*
		if(getSequencesID() != null){
			r.append("  Sequence ID: ");
			for(Integer id : getSequencesID()){
				r.append(id);
				r.append(' ');
			}
		}*/

	
	public String itemsetsToString() {		// Not used
//		StringBuffer r = new StringBuffer("");
//		for(Itemset itemset : itemsets){
//			for(Item item : itemset.getItems()){
//				String string = item.toString();
//				r.append(string);
//				r.append(' ');
//			}
//			r.append('}');
//		}
//		return r.append("    ").toString();
		
		// create a stringbuffer
		StringBuffer r = new StringBuffer("");
		// for each itemset in that sequence
		for(Itemset itemset : itemsets){
			// append timestamp
			r.append("{t=");
			r.append(itemset.getTimestamp());
			r.append(", ");
			// append each item
			for(Item item : itemset.getItems()){
				String string = item.toString();
				r.append(string);
				r.append(' ');
			}
			r.append('}');
		}
		// return the string
		return r.append("    ").toString();
	}
	
	public int getId() {
		return id;
	}

	public List<Itemset> getItemsets() {
		return itemsets;
	}
	
	public Itemset get(int index) {
		return itemsets.get(index);
	}
	
	// new : nov. 2009.
	public Item getIthItem(int i) { 
		for(int j=0; j< itemsets.size(); j++){
			if(i < itemsets.get(j).size()){
				return itemsets.get(j).get(i);
			}
			i = i- itemsets.get(j).size();
		}
		return null;
	}
	
	public int size(){
		return itemsets.size();
	}

	public Set<Integer> getSequencesID() {
		return sequencesID;
	}

	public void setSequencesID(Set<Integer> sequencesID) {
		this.sequencesID = sequencesID;
	}
	
	/**
	 * Return the sum of the size of all itemsets of this sequence.
	 */
	public int getItemOccurencesTotalCount(){
		int count =0;
		for(Itemset itemset : itemsets){
			count += itemset.size();
		}
		return count;
	}


	public Sequence cloneSequenceMinusItems(Map<Item, Set<Integer>> mapSequenceID, double relativeMinSup) {
		Sequence sequence = new Sequence(getId());
		for(Itemset itemset : itemsets){
			Itemset newItemset = itemset.cloneItemSetMinusItems(mapSequenceID, relativeMinSup);
			if(newItemset.size() !=0){ 
				sequence.addItemset(newItemset);
			}
		}
		return sequence;
	}

	public void setID(int id2) {
		id = id2;
	}

}
