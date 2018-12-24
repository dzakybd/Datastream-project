package NDatabase;

import java.awt.CheckboxGroup;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class NDatabase {

	public List<NTrajectory> trajectories = new ArrayList<NTrajectory>();
	// temporal
	Map<Integer, String> tempCategory = new HashMap<Integer, String>();
	Map<Integer, NPlace> tempPlace = new HashMap<Integer, NPlace>();
	Map<Integer, List<Integer>> group = new HashMap<Integer, List<Integer>>();
	public List<String> groupName = new ArrayList<String>();
	public List<NTrajectory> getTrajectories() {
		return trajectories;
	}

	public void createDatabase(String category, String place, String sequence) throws IOException {
		readCategory(category);
		readPlace(place);
		readSequence(sequence);
//		this.view();
	}

	// ==================================READ
	// CATEGORY=====================================
	// Read from csv file
	public void readCategory(String category) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(category));
		try {
			String line = br.readLine();
			while (true) {
				// note that csv file is separated by comma
				processCategory(line.split(","));
				line = br.readLine();
				if (line == null)
					break;
			}
		} finally {
			br.close();
		}
	}

	// set array value in variable tempCategory
	public void processCategory(String[] category) {
		// read first column as category id;
		int categoryid = Integer.parseInt(category[0]);
		// read second column as category name;
		String categoryName = category[1];
		tempCategory.put(categoryid, categoryName);
	}

	// =======================================READ
	// PLACE================================
	// read csv file
	public void readPlace(String place) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(place));
		try {
			String line = br.readLine();
			while (true) {
				// note that csv file is separated by comma
				processPlace(line.split(","));
				line = br.readLine();
				if (line == null)
					break;
			}
		} finally {
			for (Entry<Integer, List<Integer>> entry : group.entrySet()) {
//				System.out.println(entry);
			}
			br.close();
		}
	}

	// proces fil
	public void processPlace(String[] place) {

		int placeId = Integer.parseInt(place[0]); // place id
		double lat = Double.parseDouble(place[1]); // latitude
		double lng = Double.parseDouble(place[2]); // longitude

		// this function is used to separate each category and create list of category
		// for each place
		String[] integers = place[place.length - 1].split(" ");
		int i = 0;
		
		List<Integer> temp = new ArrayList<Integer>();
		int index=-1;
		for (String chr : integers) {
			int cId = Integer.parseInt(chr);
			if (i > 0) {
				temp.add(cId);
				if(index==-1) {
					index=checkinGroup(cId);
				}
			}
			i++;
		}
		NPlace p = new NPlace(placeId, lat, lng, groupByCategory(index, temp));
		tempPlace.put(placeId, p);
	}
	
	public NCategory groupByCategory(int index, List<Integer> temp) {
		List<Integer> test = new ArrayList<Integer>();
		test.add(temp.get(0));
		NCategory category = null;
		if(index==-1) {
			group.put(group.size()+1, test);
			groupName.add(tempCategory.get(temp.get(0)));
			category = new NCategory(group.size(), tempCategory.get(temp.get(0)));

		}else {
//			temp.addAll(group.get(index));
//			List<Integer> newList = temp.stream() 
//                    .distinct() 
//                    .collect(Collectors.toList()); 
//			group.get(index).clear();
//			group.get(index).addAll(newList);
			category = new NCategory(index, tempCategory.get(temp.get(0)));
		}
		
		return category;
	}
	
	public int checkinGroup(int id) {
		for (Entry<Integer, List<Integer>> entry : group.entrySet()) {
			if(entry.getValue().contains(id)) {
				return entry.getKey();
			}
		}
		
		return -1;
	}
	

	// =========================================READ
	// SEQUENCE=================================
	// read txt file
	public void readSequence(String sequence) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(sequence));
		try {
			String line = br.readLine();
			while (line != null) {
				// txt file is separated by empty string
				processSequence(line.split(" "));
				line = br.readLine();
			}
		} finally {
			br.close();
		}

	}

	// process the sequence
	// @index ==> trajectory ID
	public void processSequence(String[] sequence) {

		// create variable to save time stamp
		String time = "";
		// for each sequence
		NTrajectory t = new NTrajectory();
		for (String chr : sequence) {
			// if this token is a timestamp
			if (chr.codePointAt(0) == '<') {
				time = chr.substring(1, chr.length() - 1);
			} else if (chr.equals("-1")) {
				// -1 indicate next sequence is connected to current sequence
			} else if (chr.equals("-2")) {
				// -2 indicate the end of sequence
			} else {
				int tms = Integer.parseInt(time);
				int placeId = Integer.parseInt(chr);
				NSequence newSequence = new NSequence();
				newSequence.setTime(tms);
				newSequence.setPlace(tempPlace.get(placeId));
				t.trajectoryId = this.trajectories.size();
				t.trajectory.add(newSequence);
			}
		}
		this.trajectories.add(t);
	}

	public void view() {
		for (NTrajectory t : trajectories) {
			t.view();

			System.out.println();
		}
	}

	public Map<Integer, NPlace> getPlace() {
		return tempPlace;
	}
	
	public Map<Integer, List<Integer>> getGroup() {
		return this.group;
	}
}
