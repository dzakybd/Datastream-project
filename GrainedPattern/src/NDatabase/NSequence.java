package NDatabase;

import java.util.ArrayList;
import java.util.List;

import Database.Item;

public class NSequence {
	public int time;
	public NPlace place;
	public List<NPlace> pattern = new ArrayList<NPlace>();
	

	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public NPlace getPlace() {
		return place;
	}
	public void setPlace(NPlace place) {
		this.place = place;
	}
	
	public void viewPattern() {
		for(NPlace c:pattern) {
			c.view();
			
		}
		System.out.println();
	}
	
	public void view() {
		System.out.print("(Tid:" + this.time +",");
		this.place.view();
		System.out.print(")");
	}
	
	@Override 
	public boolean equals(Object object){
		NSequence item = (NSequence) object;
		if((item.place.category.categoryId == place.category.categoryId)){
			return true;
		}
		return false;
	}
}
