package Spade;

import java.util.ArrayList;
import java.util.List;

public class SpadeTable {
	public int SID;
	public List<SpadeItem> item = new ArrayList<SpadeItem>();
	
	
	public SpadeTable(int sID, SpadeItem item) {
		super();
		SID = sID;
		this.item.add(item);
	}





	public void view() {
		System.out.print("SID : "+SID+" : ");
		for (SpadeItem si:item) {
			System.out.print(si.item.getRawId()+"-");
		}
		System.out.println();
	}	
}
