package NMeanshift;

import java.util.ArrayList;
import java.util.List;


public class NGroupSequence {
	List<NGroup> mGroups = new ArrayList<NGroup>();
	int mSupport; //support: number of visitors
	
	public NGroupSequence() {
	}
	
	public void addGroup(NGroup g) {
		mGroups.add( g );
	}
	
	public void setSupport(int support) {
		mSupport = support;
	}
	
	public void view() {
		for (NGroup p:mGroups) {
			p.view();
		}
	}
}
