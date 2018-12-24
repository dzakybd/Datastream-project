package NPrefixspan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import NDatabase.NDatabase;
import NMeanshift.NGroup;
import NMeanshift.NGroupSequence;

public class NSnippetCluster {
	int mSnippetLength; 
	List<NSnippet> mSnippets; 
	public List<Integer> mGroupSequence; 

	public NSnippetCluster() {
		mSnippets = new ArrayList<NSnippet>();
		mGroupSequence = new ArrayList<Integer>();
	}

	public int getSnippetLength() {
		return mGroupSequence.size();
	}

	public List<NSnippet> getSnippet() {
		return mSnippets;
	}

	public void view() {
		for (NSnippet s : mSnippets) {
			s.view();
		}
		System.out.print("MGroub ");
		for (Integer i : mGroupSequence) {
			System.out.print(i + " - ");
		}
		System.out.println();
	}
	

	//////////////////////Splitter//////////////////////////////

	// transform a snippet cluster into a group sequence
	public NGroupSequence toGroupSequence() {
		NGroupSequence gs = new NGroupSequence();
		for (int i = 0; i <mSnippetLength; i++) {
			NGroup g = constructGroup(i);
			gs.addGroup(g);
		}
		gs.setSupport( computeSuppport() );
		return gs;
	}

	private NGroup constructGroup(int position) {
		NGroup group = new NGroup(); // group id: -1, a pseudo group
		for (NSnippet snippet : mSnippets)
			group.placeIds.add(snippet.mPlaceSequence.get(position).placeId);
		return group;
	}

	public void addSnippet(NSnippet snippet) {
		mSnippets.add(snippet);
	}

	// support of snippets
	public int computeSuppport() {
		Set<Integer> visitors = new HashSet<Integer>();
		for (NSnippet snippet : mSnippets)
			visitors.addAll(snippet.mVisitors);
		return visitors.size();
	}

	// average group variance
	public double computeVariance(NDatabase d) {
		double variance = 0;
		for (int i = 0; i <mSnippetLength ; i++) {
				NGroup group = constructGroup( i );
				variance += group.getVariance(d);
		}
		return variance / mSnippetLength;
	}
	
	public void setSnippetLength(int snip) {
		this.mSnippetLength = snip;
	}
	
//		

}
