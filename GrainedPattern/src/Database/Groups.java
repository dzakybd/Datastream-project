package Database;

import java.util.ArrayList;
import java.util.List;

public class Groups {
	List<Group> groups = null;

	public Groups() {
		this.groups = new ArrayList<Group>();
	}

	public Groups(List<Group> groups) {
		this.groups = groups;
	}

	public void addGroup(Group p) {
		groups.add(p);
	}

	public List<Group> getGroups() {
		return groups;
	}
	
	// current level when drilling down the category tree
	public void splitByCategory(Places places, int level) {
		List<Group> newGroups = new ArrayList<Group>();
		int groupNum = groups.size(); // original number of groups.
		for (int i = 0; i < groupNum; i++) {
			Group group = groups.get(i);
			List<Group> subgroups = group.splitByCategory(places, level);
			newGroups.addAll(subgroups);
		}
		this.groups = newGroups;

	}
	
	public void view() {
		for(Group g:this.groups) {
			g.view();
		}
	}
}
