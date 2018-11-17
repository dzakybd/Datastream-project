package split;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import split.Group;

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
	
	public void write(String groupFile, String patternFile) throws Exception {
		BufferedWriter bwGroup = new BufferedWriter(new FileWriter(groupFile, true)); // append
		for(Group g: mGroups)
			bwGroup.append( g.toString() + "\n" );
		bwGroup.close();

		BufferedWriter bwPattern = new BufferedWriter(new FileWriter(patternFile, true));
		for(Group g: mGroups)
			bwPattern.append( g.getId() + " ");
		bwPattern.append( "Support:" + mSupport + "\n");
		bwPattern.close();
	}
}
