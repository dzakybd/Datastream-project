package Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Itemset {
	public final List<Item> items = new ArrayList<Item>(); // ordered list.

	// a timestamp associated to this itemset
	public double timestamp = 0;
	
	//=========================COnstructor=====================================
	public Itemset(Item item){
		addItem(item);
	}
	
	public Itemset(){
	}
	
	//=========================SeterGetter=====================================

	public void addItem(Item value){
			items.add(value);
	}
	
	public void setItemIds(Places places) {
		for(Item item: items) {
			
			int placeId = item.getRawId();
			item.place = places.getPlace(placeId);
			int groupId = places.getGroupIdForPlace( placeId );
			item.setId( groupId );
		}
	}
	
//	public List<Item> getItems(){
//		return items;
//	}
//	
//	public Item get(int index){
//		return items.get(index);
//	}
	
	public void view() {
		System.out.print("(t:"+timestamp);
		for(Item t:items) {
			t.view();
//			System.out.print();
		}
	}
	
	public List<Item> getItems(){
		return items;
	}
	
	public int size(){
		return items.size();
	}

	 
	//coarse pattern
	public Itemset cloneItemSetMinusItems(Map<Item, Set<Integer>> mapSequenceID, double minsuppRelatif) {
		Itemset itemset = new Itemset();
		itemset.timestamp = timestamp;
		for(Item item : this.items){
//			if(mapSequenceID.get(item)!=null) {
				if(mapSequenceID.get(item).size() >= minsuppRelatif){
					itemset.addItem(item);
				}
//			}

		}
		return itemset;
	}
	
	//use by projected
	public Item get(int index){
		return items.get(index);
	}
	
	public double getTimestamp() {
		return timestamp;
	}
	
	public Itemset cloneItemSet(){
		Itemset itemset = new Itemset();
		itemset.timestamp = timestamp;
		itemset.getItems().addAll(items);
		return itemset;
	}
	
}
