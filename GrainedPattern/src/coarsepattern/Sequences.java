package coarsepattern;

import java.util.ArrayList;
import java.util.List;

import Database.Sequence;

public class Sequences {
	public final List<List<Sequence>> levels = new ArrayList<List<Sequence>>();  // itemset classified by size
	public int sequenceCount=0;
	
	private final String name;
	
	public void addSequence(Sequence sequence, int k){
		while(levels.size() <= k){
			levels.add(new ArrayList<Sequence>());
		}
		levels.get(k).add(sequence);
		sequenceCount++;
	}
	
	public Sequences(String name){
		this.name = name;
		levels.add(new ArrayList<Sequence>()); // we created zero empty by default.
	}
	
	public void view() {
		for(int i=0;i<levels.size();i++) {
			for(int j=0;j<levels.get(i).size();j++) {
				levels.get(i).get(j).view();
				System.out.println();
			}
			System.out.println();
		}
	}
	
	
}
