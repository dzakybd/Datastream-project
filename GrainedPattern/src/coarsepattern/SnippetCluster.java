package coarsepattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Database.Group;
import Database.Places;


public class SnippetCluster {
	int mSnippetLength; //snippetLength
	List<Snippet> mSnippets; //all the snippets must have the same length
	List<Integer> mGroupSequence; //a sequence of group ID, the coarse pattern
	
	public SnippetCluster(int snippetLength) {
		mSnippetLength = snippetLength;
		mSnippets = new ArrayList<Snippet>();
		mGroupSequence = new ArrayList<Integer>();
	}
	

	public void addGroupID(Integer groupId) {
		mGroupSequence.add( groupId );
	}
	
	public List<Integer> getPlaceSequence() {
		return mGroupSequence;
	}
	
	public void addSnippet(Snippet snippet) {
		mSnippets.add( snippet );
	}
	
	public List<Snippet> getSnippets() {
		return mSnippets;
	}
	
	public int getSnippetLength() {
		return mSnippetLength;
	}
	
	//transform a snippet cluster into a group sequence
	public GroupSequence toGroupSequence() {
		GroupSequence gs = new GroupSequence();
		for(int i=0; i<mSnippetLength; i++) {
			Group g = constructGroup( i );
			gs.addGroup( g );
		}
//		gs.setSupport( computeSuppport() );
		return gs;
	}
	
	private Group constructGroup(int position) {
		Group group = new Group(); //group id: -1, a pseudo group
		for(Snippet snippet: mSnippets) 
			group.addPlace( snippet.getPlace(position) );
		return group;
	}
	
	public void view(){
//		System.out.println("Snipptet ");
//		for(Snippet s: mSnippets) {
//			s.view();
//		}
		System.out.print("MGroub ");
		for(Integer i:mGroupSequence) {
			System.out.print(i + " - ");
		}
//		System.out.println();
//		System.out.println();
	}
	
	public String toString(){
		StringBuffer r = new StringBuffer(200);
		r.append("mGroupSequence: ");
		for (Integer place : mGroupSequence){
			r.append(place);
			r.append(" ");
		}
		r.append("\n");
		for (Snippet mySnippet : mSnippets){
			r.append("Place Sequence: ");
			for (Integer myplace : mySnippet.getPlaceSequence()){
				r.append(myplace);
				r.append(" ");
			}
			r.append("\n");
			r.append("Sequence ID: ");
			for (Integer myID : mySnippet.getVisitors()){
				r.append(myID);
				r.append(" ");
			}
			r.append("\n");
		}
		r.append("\n");
		return r.toString();
	}
	
	////////////////////////////////////////////////////
	//support of snippets
		public int computeSuppport() {
			Set<Integer> visitors = new HashSet<Integer>();
			for(Snippet snippet: mSnippets) 
				visitors.addAll( snippet.getVisitors() );
			return visitors.size();
		}
		
		//average group variance
		public double computeVaraince(Places places) {
			double variance = 0;
			for(int i=0; i<mSnippetLength; i++) {
				Group group = constructGroup( i );
				variance += group.getVariance( places );
//				break;
			}
			return variance / mSnippetLength;
		}
		
//		private Group constructGroup(int position) {
//			Group group = new Group(); //group id: -1, a pseudo group
//			for(Snippet snippet: mSnippets) 
//				group.addPlace( snippet.getPlace(position) );
//			return group;
//		}
}
