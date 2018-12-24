package coarsepattern;

import java.util.ArrayList;
import java.util.List;

import Database.Item;
import Database.Itemset;
import Database.Sequence;

public class ProjectedSequence {
	private Sequence sequence;
	private int firstItemset;
	private int firstItem;
	private int lastItemset;
	private int lastItem;
	private double prevtimestamp;
	private List<Itemset> previtemset = new ArrayList<Itemset>();
	
	public void view() {
		sequence.view();
		System.out.print(firstItemset+","+firstItem+","+lastItemset+","+lastItem+","+prevtimestamp);
	}
	
	public  ProjectedSequence(long decalageTemps, Sequence sequence, int indexItemset, int indexItem, double prevtimestamp){
		this.sequence = sequence;
		this.firstItemset = indexItemset;
		this.firstItem = indexItem;
		// Last itemset and item  (by default, this is the last item & itemset of the sequence.
		this.lastItemset = sequence.itemsets.size()-1;
		this.lastItem = sequence.getItemsets().get(lastItemset).items.size()-1;
		this.prevtimestamp = prevtimestamp;
		this.previtemset = new ArrayList<Itemset>();
	}
	
	
	public ProjectedSequence(ProjectedSequence sequence, int indexItemset, int indexItem, double prevtimestamp){

		this.sequence = sequence.sequence;
		this.firstItemset = indexItemset + sequence.firstItemset;
		if(this.firstItemset == sequence.firstItemset){
			this.firstItem = indexItem + sequence.firstItem;
		}else{
			this.firstItem = indexItem; // 
		}
		this.lastItemset = sequence.lastItemset;
		this.lastItem = sequence.lastItem;
		this.prevtimestamp = prevtimestamp;
		this.previtemset = new ArrayList<Itemset>();
		for (Itemset copy : sequence.previtemset){
			this.previtemset.add(copy);
		}
	}
	
	public int size(){
		int size = sequence.size() - firstItemset - ((sequence.size()-1) - lastItemset);
		if(size == 1 && sequence.getItemsets().get(firstItemset).size() == 0){
			return 0;
		}
		return size;
	}
	
	public int indexOf(int indexItemset, int idItem) {
		for(int i=0; i < getSizeOfItemsetAt(indexItemset); i++){
			if(getItemAtInItemsetAt(i, indexItemset).getId() == idItem){
				return i;
			}
		}
		return -1;
	}
	
	public List<Itemset> getPrevitemset() {
		return previtemset;
	}
	public int getId() {
		return sequence.getId();
	}
	public boolean isFirstItemset(int index){
		return index == 0;
	}
	public Itemset getItemset(int index){
		return sequence.get(index+firstItemset);
	}
	
	public Item getItemAtInItemsetAt(int indexItem, int indexItemset){
		if(isFirstItemset(indexItemset)){
			return getItemset(indexItemset).get(indexItem + firstItem);
		}else{
			return getItemset(indexItemset).get(indexItem);
		}
	}
	public boolean isLastItemset(int index){
		return (index + firstItemset) == lastItemset;
	}
	
	public int getSizeOfItemsetAt(int index){
		int size = sequence.getItemsets().get(index + firstItemset).size();
		if(isLastItemset(index)){
			size -= ((size -1) - lastItem);
		}
		if(isFirstItemset(index)){
			size -=  firstItem;
		}
		return size;
	}
	

	public void addprevitemset(Itemset itemset) {
		previtemset.add(itemset);
	}
	
	public double getPrevtimestamp() {
		return prevtimestamp;
	}
	//	return true if this itemset is cut at its left.
	public boolean isPostfix(int indexItemset){
		return indexItemset == 0  && firstItem !=0;
	}
}
