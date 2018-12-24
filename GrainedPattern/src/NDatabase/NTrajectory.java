package NDatabase;

import java.util.ArrayList;
import java.util.List;


public class NTrajectory {
	public int trajectoryId;
	//for prefixspan
	public List<NSequence> baseline = new ArrayList<NSequence>();
	public List<NSequence> trajectory = new ArrayList<NSequence>();

	
	public NTrajectory() {
	}


	public List<NSequence> getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(List<NSequence> trajectory) {
		this.trajectory = trajectory;
	}
	
	public void view() {
		System.out.print("ID "+ this.trajectoryId +" : ");
		for (NSequence s: trajectory) {
			s.view();
		}
	}
	
	public void viewB() {
		System.out.print("ID "+ this.trajectoryId +" : ");
		for (NSequence s: baseline) {
			s.view();
		}
	}
}
