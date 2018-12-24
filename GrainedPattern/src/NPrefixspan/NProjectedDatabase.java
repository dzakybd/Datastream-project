package NPrefixspan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Database.Sequence;
import NDatabase.NDatabase;
import NDatabase.NPlace;
import NDatabase.NSequence;
import NDatabase.NTrajectory;

public class NProjectedDatabase {
//	private List<NProjectedSequence> pseudoSequences = new ArrayList<NProjectedSequence>();
	public List<NTrajectory> trajectories = new ArrayList<NTrajectory>();

	public Map<NPlace, List<Integer>> projection(NDatabase d, NSequence item) {
		Map<NPlace, List<Integer>> test = new HashMap<NPlace, List<Integer>>();
		for (NTrajectory t : d.trajectories) {
			for (int in = 0; in < t.trajectory.size(); in++) {
				if (t.trajectory.get(in).equals(item)) {
					NTrajectory Temp = new NTrajectory();
					Temp.trajectory = t.trajectory.subList(in + 1, t.trajectory.size());
					Temp.trajectoryId = t.trajectoryId;
					Temp.baseline.add(t.trajectory.get(in));
					trajectories.add(Temp);

					if (!test.keySet().contains(t.trajectory.get(in).place)) {
						test.put(t.trajectory.get(in).place, new ArrayList<Integer>());
					}
					test.get(t.trajectory.get(in).place).add(t.trajectoryId);
				}
			}
		}
		return test;
	}
	

	public Map<List<NPlace>, List<Integer>> pseudoProjection(NProjectedDatabase d, NSequence item, int time) {
		Map<List<NPlace>, List<Integer>> test = new HashMap<List<NPlace>, List<Integer>>();
		for (NTrajectory t : d.trajectories) {
			for (int in = 0; in < t.trajectory.size(); in++) {
				if (t.trajectory.get(in).equals(item)) {
					if (t.trajectory.get(in).time - t.baseline.get(0).time <= time) {
						NTrajectory Temp = new NTrajectory();
						List<NSequence> F = t.trajectory.subList(in + 1, t.trajectory.size());
						Temp.trajectory.addAll(F);
						Temp.trajectoryId = t.trajectoryId;
						Temp.baseline.addAll(t.baseline);
						Temp.baseline.add(t.trajectory.get(in));
						trajectories.add(Temp);
						
						List<NPlace> temp = new ArrayList<NPlace>();
						for (NSequence c : Temp.baseline) {
							temp.add(c.place);
						}
						if (!test.keySet().contains(temp)) {
							test.put(temp, new ArrayList<Integer>());
						}
						test.get(temp).add(t.trajectoryId);
					}
				}
			}
		}
		return test;
	}

	public void view() {
		for (NTrajectory t : trajectories) {
			System.out.print(t.trajectoryId + " : ");
			for (NSequence sc : t.baseline) {
				sc.view();
			}
			System.out.print(" ==> ");
			for (NSequence sc : t.trajectory) {
				sc.view();
			}
//			t.view();
			System.out.println();
		}
	}
}
