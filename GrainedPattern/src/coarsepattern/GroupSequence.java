package coarsepattern;

import java.util.ArrayList;
import java.util.List;

import Database.Group;

public class GroupSequence {
	List<Group> mGroups = new ArrayList<Group>();
	int mSupport; //support: number of visitors
	
	public GroupSequence() {
	}
	
	public void addGroup(Group g) {
		mGroups.add( g );
	}
	
	public void setSupport(int support) {
		mSupport = support;
	}
	
	public void view() {
		for (Group p:mGroups) {
			p.view();
		}
	}
}
