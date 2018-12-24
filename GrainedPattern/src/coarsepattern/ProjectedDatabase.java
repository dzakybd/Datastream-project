package coarsepattern;

import java.util.ArrayList;
import java.util.List;

public class ProjectedDatabase {
	private List<ProjectedSequence> pseudoSequences = new ArrayList<ProjectedSequence>();

	
	public List<ProjectedSequence> getPseudoSequences(){
		return pseudoSequences;
	}
	
	
	public void addSequence(ProjectedSequence newSequence) {
		pseudoSequences.add(newSequence);
		
	}
	
	public void view() {
		for (ProjectedSequence r:pseudoSequences) {
			r.view();
			System.out.println();
		}
	}
}
