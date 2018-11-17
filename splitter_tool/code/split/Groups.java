package split;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import prefixspan.Sequence;
import prefixspan.SequenceDatabase;

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

	public void splitByGrid(Places places, double minDensity, double gridSize) {
		final int rownum = (int) ((41.0 - 40.4) / gridSize) + 1;
		final int colnum = (int) ((-73.7 + 74.3) / gridSize) + 1;
		int groupNum = groups.size(); // original number of groups.
		List<Group> newGroups = new ArrayList<Group>();
		for (int i = 0; i < groupNum; i++) {
			Group group = groups.get(i);
			double density[][] = new double[rownum][colnum];
			int used[][] = new int[rownum][colnum];
			for (int j = 0; j < rownum; j++) {
				for (int k = 0; k < colnum; k++) {
					density[j][k] = 0.0;
					used[j][k] = -1;
				}
			}
			for (Integer placeId : group.placeIds) {
				Place place = places.getPlace(placeId);
				int row = (int) ((place.getLat() - 40.4) / gridSize);
				int col = (int) ((place.getLng() + 74.3) / gridSize);
				density[row][col] = density[row][col] + 1;
			}
			ArrayList<GridDensity> mydensitylist = new ArrayList<GridDensity>();
			for (int j = 0; j < rownum; j++) {
				for (int k = 0; k < colnum; k++) {
					GridDensity mydensity = new GridDensity(j, k, density[j][k]);
					mydensitylist.add(mydensity);
				}
			}
			Collections.sort(mydensitylist, new Comparator<GridDensity>() {
				public int compare(GridDensity u1, GridDensity u2) {
					if (u2.getDensity() - u1.getDensity() > 0)
						return 1;
					else if (u2.getDensity() - u1.getDensity() == 0)
						return 0;
					else
						return -1;
				}
			});
			int iter = 0;
			int subgroupid = 0;
			while (mydensitylist.get(iter).getDensity() > minDensity) {
				int row = mydensitylist.get(iter).getRow();
				int col = mydensitylist.get(iter).getCol();
				if (used[row][col] == -1) {
					used[row][col] = subgroupid;
					double total_density = density[row][col];
					int boarder[] = new int[4];
					int dir_boarder[] = new int[4];
					double sum_density[] = new double[4];
					double avg_density[] = new double[4];
					boarder[0] = row;
					boarder[1] = col;
					boarder[2] = row;
					boarder[3] = col;
					while (true) {
						// ///////////////////////// Up
						if (boarder[0] > 0) {
							dir_boarder[0] = boarder[0] - 1;
							dir_boarder[1] = boarder[1];
							dir_boarder[2] = boarder[0] - 1;
							dir_boarder[3] = boarder[3];
							boolean haslarger = false;
							boolean notvisited = true;
							sum_density[0] = 0;
							for (int j = dir_boarder[1]; j <= dir_boarder[3]; j++) {
								if (density[dir_boarder[0]][j] > minDensity)
									haslarger = true;
								if (used[dir_boarder[0]][j] != -1)
									notvisited = false;
								sum_density[0] = sum_density[0]
										+ density[dir_boarder[0]][j];
							}
							avg_density[0] = (sum_density[0] + total_density)
									/ ((boarder[3] - boarder[1] + 1)
											* (boarder[2] - boarder[0] + 1) + (dir_boarder[3]
											- dir_boarder[1] + 1)
											* (dir_boarder[2] - dir_boarder[0] + 1));
							if (avg_density[0] < minDensity
									|| haslarger == false
									|| notvisited == false) {
								avg_density[0] = -1;
							}
						} else {
							avg_density[0] = -1;
						}
						// ///////////////////////// Right
						if (boarder[3] < colnum - 1) {
							dir_boarder[0] = boarder[0];
							dir_boarder[1] = boarder[3] + 1;
							dir_boarder[2] = boarder[2];
							dir_boarder[3] = boarder[3] + 1;
							boolean haslarger = false;
							boolean notvisited = true;
							sum_density[1] = 0;
							for (int j = dir_boarder[0]; j <= dir_boarder[2]; j++) {
								if (density[j][dir_boarder[1]] > minDensity)
									haslarger = true;
								if (used[j][dir_boarder[1]] != -1)
									notvisited = false;
								sum_density[1] = sum_density[1]
										+ density[j][dir_boarder[1]];
							}
							avg_density[1] = (sum_density[1] + total_density)
									/ ((boarder[3] - boarder[1] + 1)
											* (boarder[2] - boarder[0] + 1) + (dir_boarder[3]
											- dir_boarder[1] + 1)
											* (dir_boarder[2] - dir_boarder[0] + 1));
							if (avg_density[1] < minDensity
									|| haslarger == false
									|| notvisited == false) {
								avg_density[1] = -1;
							}
						} else {
							avg_density[1] = -1;
						}
						// ///////////////////////// Down
						if (boarder[2] < colnum - 1) {
							dir_boarder[0] = boarder[2] + 1;
							dir_boarder[1] = boarder[1];
							dir_boarder[2] = boarder[2] + 1;
							dir_boarder[3] = boarder[3];
							boolean haslarger = false;
							boolean notvisited = true;
							sum_density[2] = 0;
							for (int j = dir_boarder[1]; j <= dir_boarder[3]; j++) {
								if (density[dir_boarder[0]][j] > minDensity)
									haslarger = true;
								if (used[dir_boarder[0]][j] != -1)
									notvisited = false;
								sum_density[2] = sum_density[2]
										+ density[dir_boarder[0]][j];
							}
							avg_density[2] = (sum_density[2] + total_density)
									/ ((boarder[3] - boarder[1] + 1)
											* (boarder[2] - boarder[0] + 1) + (dir_boarder[3]
											- dir_boarder[1] + 1)
											* (dir_boarder[2] - dir_boarder[0] + 1));
							if (avg_density[2] < minDensity
									|| haslarger == false
									|| notvisited == false) {
								avg_density[2] = -1;
							}
						} else {
							avg_density[2] = -1;
						}
						// ///////////////////////// Left
						if (boarder[1] > 0) {
							dir_boarder[0] = boarder[0];
							dir_boarder[1] = boarder[1] - 1;
							dir_boarder[2] = boarder[2];
							dir_boarder[3] = boarder[1] - 1;
							boolean haslarger = false;
							boolean notvisited = true;
							sum_density[3] = 0;
							for (int j = dir_boarder[0]; j <= dir_boarder[2]; j++) {
								if (density[j][dir_boarder[1]] > minDensity)
									haslarger = true;
								if (used[j][dir_boarder[1]] != -1)
									notvisited = false;
								sum_density[3] = sum_density[3]
										+ density[j][dir_boarder[1]];
							}
							avg_density[3] = (sum_density[3] + total_density)
									/ ((boarder[3] - boarder[1] + 1)
											* (boarder[2] - boarder[0] + 1) + (dir_boarder[3]
											- dir_boarder[1] + 1)
											* (dir_boarder[2] - dir_boarder[0] + 1));
							if (avg_density[3] < minDensity
									|| haslarger == false
									|| notvisited == false) {
								avg_density[3] = -1;
							}
						} else {
							avg_density[3] = -1;
						}
						// /////////////////////////
						int maxindex = 0;
						double maxvalue = avg_density[maxindex];
						for (int j = 1; j < 4; j++) {
							if (avg_density[j] > maxvalue) {
								maxvalue = avg_density[j];
								maxindex = j;
							}
						}
						if (maxvalue == -1)
							break;
						total_density = total_density + sum_density[maxindex];
						if (maxindex == 0) {
							boarder[0] = boarder[0] - 1;
							for (int j = boarder[1]; j <= boarder[3]; j++) {
								used[boarder[0]][j] = subgroupid;
							}
						}
						if (maxindex == 1) {
							boarder[3] = boarder[3] + 1;
							for (int j = boarder[0]; j <= boarder[2]; j++) {
								used[j][boarder[3]] = subgroupid;
							}
						}
						if (maxindex == 2) {
							boarder[2] = boarder[2] + 1;
							for (int j = boarder[1]; j <= boarder[3]; j++) {
								used[boarder[2]][j] = subgroupid;
							}
						}
						if (maxindex == 3) {
							boarder[1] = boarder[1] - 1;
							for (int j = boarder[0]; j <= boarder[2]; j++) {
								used[j][boarder[1]] = subgroupid;
							}
						}
					}
					subgroupid = subgroupid + 1;
				}
				iter = iter + 1;
			}
			
//			StringBuffer r = new StringBuffer(200);
//			for (int row=0;row <rownum;row++){
//				for (int col=0;col<colnum;col++){
//					r.append(used[row][col]);
//					r.append(" ");
//				}
//				r.append("\n");
//			}
//			r.append("\n\n\n\n\n\n\n");
//			BufferedWriter bwPattern;
//			try {
//				bwPattern = new BufferedWriter(new FileWriter("C:\\Users\\jefferson\\workspace\\grid\\data\\out.txt", true));
//				bwPattern.append(r.toString());
//				bwPattern.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			List<Group> subgroups = new ArrayList<Group>();
//			System.out.println(subgroupid);
			for (int j=0;j<subgroupid;j++){
				Group newsubgroup = new Group();
				subgroups.add(newsubgroup);
			}
			for (Integer placeId : group.placeIds) {
				Place place = places.getPlace(placeId);
				int row = (int) ((place.getLat() - 40.4) / gridSize);
				int col = (int) ((place.getLng() + 74.3) / gridSize);
				if (used[row][col]!=-1)
					subgroups.get(used[row][col]).addPlace(place.getId());
			}
			newGroups.addAll(subgroups);
		}
		this.groups = newGroups;
	}

	public void splitByLocation(Clusterer clusterer, double varThreshold,
			Places places) {
		int groupNum = groups.size(); // original number of groups.
		List<Group> newGroups = new ArrayList<Group>();
		for (int i = 0; i < groupNum; i++) {
			Group group = groups.get(i);
			 
			if(group.getVariance(places) <= varThreshold) {
				newGroups.add(group);
				continue;
			}
			List<Group> subgroups = Arrays.asList(clusterer.cluster(group));
			newGroups.addAll(subgroups);
		}
		this.groups = newGroups;
	}

	public int getGroupNum() {
		return groups.size();
	}

	public Group getIthGroup(int i) {
		return groups.get(i);
	}

	public int getPlaceNum() {
		int sum = 0;
		for (Group group : groups) {
			sum += group.size();
		}
		return sum;
	}

	// public void trim(SequenceDatabase database) {
	// Set<Integer> placeIds = database.getPlaceIdSet();
	// this.trim( placeIds );
	// }

	private void trim(Set<Integer> patternedPlaces) {
		List<Group> trimmedGroups = new ArrayList<Group>();
		for (Group g : groups) {
			Group trimmedGroup = g.trim(patternedPlaces);
			if (trimmedGroup.size() > 0)
				trimmedGroups.add(trimmedGroup);
		}
		this.groups = trimmedGroups;
	}

	public String toString() {
		StringBuffer r = new StringBuffer("");
		if (groups != null) {
			for (Group group : groups) {
				r.append(group.toString());
				r.append("\n");
			}
		}
		// return the string
		return r.toString();
	}
}
