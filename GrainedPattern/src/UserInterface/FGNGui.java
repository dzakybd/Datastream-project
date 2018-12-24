package UserInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import NDatabase.NDatabase;
import NDatabase.NPlace;
import NDatabase.NSequence;
import NDatabase.NTrajectory;
import NPrefixspan.NSnippet;
import NPrefixspan.NSnippetCluster;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PApplet;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.*;
import de.fhpotsdam.unfolding.utils.MapUtils;
import g4p_controls.*;

public class FGNGui extends PApplet {
	UnfoldingMap map;
	NProcessing Nprocess = null;
	NDatabase D;
//	private Map<Integer,List<SimpleLinesMarker>> lineMarker = new HashMap<Integer,List<SimpleLinesMarker>>();
	private List<SimplePointMarker> placingMarker = new ArrayList<SimplePointMarker>();
	private List<SimpleLinesMarker> lineMarker = new ArrayList<SimpleLinesMarker>();
	private List<Integer> linecolor = new ArrayList<Integer>();
	private List<Integer> groupcolor = new ArrayList<Integer>();
	private List<NPlace> currentplace = null;
	private List<NSnippetCluster> currentPattern = null;
	List<GCheckbox> test = new ArrayList<GCheckbox>();
	NPlace zoom = null;
	public List<String> top5 = new ArrayList<String>();
	public String var = Double.toString(0.0002);
	public String sup = Double.toString(100);
	public String time = Integer.toString(120);
	public String bw = Double.toString(0.02);
	public String to = Double.toString(0.8);
	public String patternlength = Integer.toString(2);

	String dataFile = "../Data/sequences - Copy.txt";
	String placeFile = "../Data/places - Copy.csv";
	String groupFile = "../Data/category - Copy.csv";

	public void setup() {
		size(1200, 660, P3D);
		if (frame != null) {
			frame.setResizable(true);
		}
		createGUI();
		customGUI();
		setTextManual();

	}

	public void draw() {
		background(230);
//		
//		fill(255,0,0);
//		rect(20,20,100,100);
		map.draw();
	}

	// Use this method to add additional statements
	// to customise the GUI controls
	public void customGUI() {
		map = new UnfoldingMap(this, 240, 40, 720, 520, new OpenStreetMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
	}

	public void setCurrentPlace() {
		this.currentplace = new ArrayList<NPlace>();
		for (NSnippetCluster ns : this.currentPattern) {
			for (NSnippet s : ns.getSnippet()) {
				for (NPlace plc : s.mPlaceSequence) {
					if (!currentplace.contains(plc)) {
						currentplace.add(plc);
					}
				}

			}
		}
	}

	public void trajectoryOnMap(List<NPlace> places) {
		map = new UnfoldingMap(this, 240, 40, 720, 520, new OpenStreetMapProvider());
		map.zoomAndPanTo(10, new Location(zoom.lat, zoom.getLng()));
		if (this.currentPattern != null && this.checkbox_showline.isSelected()) {
			foldingCoarse();
		}
		placingMarker = new ArrayList<SimplePointMarker>();
		viewPlaceOnly(places);
		showPointMarker();
		MapUtils.createDefaultEventDispatcher(this, map);

	}

	public void viewPlaceOnly(List<NPlace> place) {

		for (NPlace plc : place) {
			Location placeX = new Location(plc.getLat(), plc.getLng());
			SimplePointMarker placeMarker = new SimplePointMarker(placeX);
			placeMarker.setStrokeWeight(1);
			placeMarker.setColor(groupcolor.get((plc.category.categoryId) - 1));
			placingMarker.add(placeMarker);
			map.addMarkers(placeMarker);
		}
	}

	public void foldingCoarse() {
		// Draw place
		for (NSnippetCluster ns : this.currentPattern) {
			for (NSnippet s : ns.getSnippet()) {
				int i = 0;
				for (NPlace plc : s.mPlaceSequence) {
					Location placeX = new Location(plc.getLat(), plc.getLng());

					if (i != 0) {
						Location placePrev = new Location(s.mPlaceSequence.get(i - 1).getLat(),
								s.mPlaceSequence.get(i - 1).getLng());
//							fill(0);
						SimpleLinesMarker connectionMarker = new SimpleLinesMarker(placePrev, placeX);

						connectionMarker.setColor(linecolor.get(s.mVisitors.iterator().next()));
						connectionMarker.setStrokeWeight(1);
						lineMarker.add(connectionMarker);
						map.addMarkers(connectionMarker);
					}
					i++;
				}
			}
		}
//		MapUtils.createDefaultEventDispatcher(this, map);
	}

	public void getSupport() {

		Map<Integer, List<NSnippetCluster>> divider = new HashMap<Integer, List<NSnippetCluster>>();

		for (NSnippetCluster x : this.currentPattern) {
			int temp = x.getSnippet().get(0).mPlaceSequence.size();
			if (!divider.keySet().contains(temp)) {
				List<NSnippetCluster> tempdata = new ArrayList<NSnippetCluster>();
				divider.put(temp, tempdata);
			}
			divider.get(temp).add(x);
		}

		String category = "";
		for (Entry<Integer, List<NSnippetCluster>> entry : divider.entrySet()) {
			category = category + "Pattern Length = " + entry.getKey() + ", Size = " + entry.getValue().size() + "\n";
			Map<Integer, List<NSnippet>> forsort = new HashMap<Integer, List<NSnippet>>();
			for (NSnippetCluster x : entry.getValue()) {
				forsort.put(x.getSnippet().size(), x.getSnippet());
			}

			Map<Integer, String> length = new HashMap<Integer, String>();
			for (NSnippetCluster x : entry.getValue()) {
				String content = "";
				for (NSnippet y : x.getSnippet()) {
//					tempsize = y.mPlaceSequence.size();
					for (NPlace c : y.mPlaceSequence) {
						content = content + c.category.categoryName + " ->";
					}
					break;
				}

				content = "(Support= " + x.getSnippet().size() + ") => " + content;
				length.put(x.getSnippet().size(), content);
			}
//
			List sortedKeys = new ArrayList(forsort.keySet());
			Collections.sort(sortedKeys);

			for (int i = sortedKeys.size() - 1; i >= 0; i--) {
				category = category + length.get(sortedKeys.get(i)) + "\n";
			}
//			category = category + "\n\n";

		}

		textarea_top5.setText(category);
	}

	public void showPointMarker() {
		if (this.checkbox_showPlace.isSelected()) {
			for (SimplePointMarker x : placingMarker) {
				x.setHidden(false);
			}
		} else {
			for (SimplePointMarker x : placingMarker) {
				x.setHidden(true);
			}
		}
	}

	public void showLinesMarker() {
		if (this.checkbox_showline.isSelected()) {
			for (SimpleLinesMarker x : lineMarker) {
				x.setHidden(false);
			}
		} else {
			for (SimpleLinesMarker x : lineMarker) {
				x.setHidden(true);
			}
		}
	}

//	public void viewWithLine(boolean place) {
//		map = new UnfoldingMap(this, 240, 40, 720, 600, new OpenStreetMapProvider());
//		map.zoomAndPanTo(10, new Location(zoom.lat, zoom.getLng()));
//
//		for (NTrajectory tr : D.trajectories) {
//			Random rand = new Random();
//			// Java 'Color' class takes 3 floats, from 0 to 1.
//			float r = rand.nextInt(256);
//			float g = rand.nextInt(256);
//			float b = rand.nextInt(256);
//			int i = 0;
//			for (NSequence ns : tr.trajectory) {
//				Location placeX = new Location(ns.place.getLat(), ns.place.getLng());
//				if (place) {
//					SimplePointMarker placeMarker = new SimplePointMarker(placeX);
//					placeMarker.setStrokeWeight(2);
//					map.addMarkers(placeMarker);
//				}
//				if (i != 0) {
//					Location placePrev = new Location(tr.trajectory.get(i - 1).place.getLat(),
//							tr.trajectory.get(i - 1).place.getLng());
////					fill(0);
//					SimpleLinesMarker connectionMarker = new SimpleLinesMarker(placePrev, placeX);
//
//					connectionMarker.setColor(color(r, g, b));
//					connectionMarker.setStrokeWeight(1);
//					map.addMarkers(connectionMarker);
//				}
//				i++;
//			}
//			linecolor.add(color(r, g, b));
//		}
//		MapUtils.createDefaultEventDispatcher(this, map);
//	}

	public void setGCheck() {
		test.add(checkbox1);
		test.add(checkbox2);
		test.add(checkbox3);
		test.add(checkbox4);
		test.add(checkbox5);
		test.add(checkbox6);
		test.add(checkbox7);
		test.add(checkbox8);
		test.add(checkbox9);
		test.add(checkbox10);
		test.add(checkbox11);
		test.add(checkbox12);
		test.add(checkbox13);
		test.add(checkbox14);
		test.add(checkbox15);
		test.add(checkbox16);
	}

	public List<Integer> getSelectedCheckbox() {
		List<Integer> selected = new ArrayList<Integer>();
		int x = 1;
		for (GCheckbox i : test) {
			if (i.isSelected()) {
				selected.add(x);
			}
			x++;
		}
		return selected;
	}

	public void showChekbox() {
		if (this.checkbox_showPlace.isSelected()) {
			if (this.currentplace != null) {
				List<Integer> groupId = getSelectedCheckbox();
				int i = 0;
				for (SimplePointMarker plc : this.placingMarker) {
					NPlace place = this.currentplace.get(i);
					if (!groupId.contains(place.category.categoryId)) {
						plc.setHidden(true);
					} else {
						plc.setHidden(false);
					}
					i++;
				}

			}

		}

	}

	public void setTextManual() {
		this.category.setText(groupFile);
		this.place.setText(placeFile);
		this.sequence.setText(dataFile);
		this.textfield_sup.setText(this.sup);
		this.textfield_var.setText(this.var);
		this.textfield_time.setText(this.time);
		this.textfield_toa.setText(this.to);
		this.textfield_bandwidth.setText(this.bw);
		this.textfield_patternLength.setText(this.patternlength);
	}

	public void setColor() {
		for (NTrajectory t : this.D.trajectories) {
			Random rand = new Random();
			// Java 'Color' class takes 3 floats, from 0 to 1.
			float r = rand.nextInt(256);
			float g = rand.nextInt(256);
			float b = rand.nextInt(256);
			linecolor.add(color(r, g, b));
		}
	}

	public void setgroupColor() {
		int y = 360;
		for (Integer o : Nprocess.database.getGroup().keySet()) {
			Random rand = new Random();
			// Java 'Color' class takes 3 floats, from 0 to 1.
			float r = rand.nextInt(256);
			float g = rand.nextInt(256);
			float b = rand.nextInt(256);
			test.get(o - 1).setText(Nprocess.database.groupName.get(o - 1));
			groupcolor.add(color(r, g, b));
		}
	}

	/*
	 * ========================================================= ==== WARNING ===
	 * ========================================================= The code in this
	 * tab has been generated from the GUI form designer and care should be taken
	 * when editing this file. Only add/edit code inside the event handlers i.e.
	 * only use lines between the matching comment tags. e.g.
	 * 
	 * void myBtnEvents(GButton button) { //_CODE_:button1:12356: // It is safe to
	 * enter your event code here } //_CODE_:button1:12356:
	 * 
	 * Do not rename this tab!
	 * =========================================================
	 */

	public void textfield1_category(GTextField source, GEvent event) { // _CODE_:category:662454:
		println("category - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:category:662454:

	public void button1_category(GButton source, GEvent event) { // _CODE_:buttonCategory:818042:
		println("buttonCategory - GButton >> GEvent." + event + " @ " + millis());
	} // _CODE_:buttonCategory:818042:

	public void textfield1_place(GTextField source, GEvent event) { // _CODE_:place:292592:
		println("place - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:place:292592:

	public void button1_place(GButton source, GEvent event) { // _CODE_:button_place:687991:
		println("button_place - GButton >> GEvent." + event + " @ " + millis());
	} // _CODE_:button_place:687991:

	public void textfield1_change1(GTextField source, GEvent event) { // _CODE_:sequence:555789:
		println("sequence - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:sequence:555789:

	public void button1_sequence(GButton source, GEvent event) { // _CODE_:button_sequence:376547:
		println("button_sequence - GButton >> GEvent." + event + " @ " + millis());
	} // _CODE_:button_sequence:376547:

	public void button1_createdb(GButton source, GEvent event) { // _CODE_:button_createdb:646818:
//	  println("button_createdb - GButton >> GEvent." + event + " @ " + millis());
		try {
			setGCheck();
			Nprocess = new NProcessing();
			Nprocess.createDatabase(this.category.getText(), this.place.getText(), this.sequence.getText());
			this.D = Nprocess.database;
			zoom = D.trajectories.get(0).trajectory.get(0).getPlace();
			this.setColor();
			this.setgroupColor();
			this.currentplace = new ArrayList<NPlace>();
			this.currentplace.addAll(this.D.getPlace().values());
			this.textfield_databasesize.setText(Integer.toString(this.D.trajectories.size()));
			this.textfield_numberofplace.setText(Integer.toString(this.currentplace.size()));
			this.textfield_categorysize.setText(Integer.toString(this.D.getGroup().size()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.trajectoryOnMap(this.currentplace);
		}
	} // _CODE_:button_createdb:646818:

	public void textfield1_sup(GTextField source, GEvent event) { // _CODE_:textfield_sup:336014:
		println("textfield_sup - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_sup:336014:

	public void textfield1_var(GTextField source, GEvent event) { // _CODE_:textfield_var:387184:
		println("textfield_var - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_var:387184:

	public void textfield1_time(GTextField source, GEvent event) { // _CODE_:textfield_time:984273:
		println("textfield_time - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_time:984273:

	public void textfield1_toa(GTextField source, GEvent event) { // _CODE_:textfield_toa:978198:
		println("textfield_toa - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_toa:978198:

	public void textfield1_bandwidth(GTextField source, GEvent event) { // _CODE_:textfield_bandwidth:546317:
		println("textfield_bandwidth - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_bandwidth:546317:

	public void button1_prefix(GButton source, GEvent event) { // _CODE_:button_prefix:551316:
//	  println("button_prefix - GButton >> GEvent." + event + " @ " + millis());
		Nprocess.setParams(textfield_sup.getText(), textfield_time.getText(), textfield_var.getText(),
				textfield_toa.getText(), textfield_bandwidth.getText(), textfield_patternLength.getText());
		long start = System.currentTimeMillis();
		Nprocess.runSplitter();
		long end = System.currentTimeMillis();
		double timeSplitterCoarse = (end - start) / 1000.0;
		this.textfield_timecoarse.setText(Double.toString(timeSplitterCoarse));
		this.textfield_coarse.setText(Integer.toString(Nprocess.getCoarsePattern().size()));
		this.currentPattern = Nprocess.getCoarsePattern();
		setCurrentPlace();
		trajectoryOnMap(this.currentplace);
		this.textfield_numberofplace.setText(Integer.toString(this.currentplace.size()));

		getSupport();
	} // _CODE_:button_prefix:551316:

	public void button1_Mean(GButton source, GEvent event) { // _CODE_:button_Mean:823129:
		println("button_Mean - GButton >> GEvent." + event + " @ " + millis());
		Nprocess.setParams(textfield_sup.getText(), textfield_time.getText(), textfield_var.getText(),
				textfield_toa.getText(), textfield_bandwidth.getText(), textfield_patternLength.getText());
		long start = System.currentTimeMillis();
		Nprocess.runMeanShift();
		long end = System.currentTimeMillis();
		double timeRefine = (end - start) / 1000.0;
		this.currentPattern = Nprocess.getFineGrained();
		setCurrentPlace();
		trajectoryOnMap(this.currentplace);
		textfield_fine.setText(Integer.toString(Nprocess.getFineGrained().size()));
		textfield_timefine.setText(Double.toString(timeRefine));
		textfield_totaltime.setText(Double.toString(new Double(textfield_timecoarse.getText()) + timeRefine));
		getSupport();
	} // _CODE_:button_Mean:823129:

	public void button1_pm(GButton source, GEvent event) { // _CODE_:button_pm:669719:
		println("button_pm - GButton >> GEvent." + event + " @ " + millis());

		Nprocess.setParams(textfield_sup.getText(), textfield_time.getText(), textfield_var.getText(),
				textfield_toa.getText(), textfield_bandwidth.getText(), textfield_patternLength.getText());
		long start = System.currentTimeMillis();
		Nprocess.runSplitter();
		long end = System.currentTimeMillis();
		double timeSplitterCoarse = (end - start) / 1000.0;
		this.textfield_timecoarse.setText(Double.toString(timeSplitterCoarse));
		this.textfield_coarse.setText(Integer.toString(Nprocess.getCoarsePattern().size()));
		this.currentPattern = Nprocess.getCoarsePattern();
		setCurrentPlace();
		trajectoryOnMap(this.currentplace);
		this.textfield_numberofplace.setText(Integer.toString(this.currentplace.size()));

		/////////////////////////////////////////////
		long start2 = System.currentTimeMillis();
		Nprocess.runMeanShift();
		long end2 = System.currentTimeMillis();
		double timeRefine = (end2 - start2) / 1000.0;
		textfield_fine.setText(Integer.toString(Nprocess.getFineGrained().size()));
		this.currentPattern = Nprocess.getFineGrained();
		setCurrentPlace();
		trajectoryOnMap(this.currentplace);
		Nprocess.viewFineGrained();
		textfield_timefine.setText(Double.toString(timeRefine));
		textfield_totaltime.setText(Double.toString(new Double(textfield_timecoarse.getText()) + timeRefine));
		getSupport();

	} // _CODE_:button_pm:669719:

	public void textfield1_coarse(GTextField source, GEvent event) { // _CODE_:textfield_coarse:601417:
		println("textfield_coarse - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_coarse:601417:

	public void textfield1_timecoarse(GTextField source, GEvent event) { // _CODE_:textfield_timecoarse:205098:
		println("textfield_timecoarse - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_timecoarse:205098:

	public void textfield1_timefine(GTextField source, GEvent event) { // _CODE_:textfield_timefine:228573:
		println("textfield_timefine - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_timefine:228573:

	public void textfield1_totaltime(GTextField source, GEvent event) { // _CODE_:textfield_totaltime:828078:
		println("textfield_totaltime - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_totaltime:828078:

	public void textfield1_fine(GTextField source, GEvent event) { // _CODE_:textfield_fine:516321:
		println("textfield_fine - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_fine:516321:

	public void textfield1_databasesize(GTextField source, GEvent event) { // _CODE_:textfield_databasesize:490266:
		println("textfield_databasesize - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_databasesize:490266:

	public void textfield1_numberofplace(GTextField source, GEvent event) { // _CODE_:textfield_numberofplace:276734:
		println("textfield_numberofplace - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_numberofplace:276734:

	public void checkbox1_showline(GCheckbox source, GEvent event) { // _CODE_:checkbox_showline:289362:
		println("checkbox_showline - GCheckbox >> GEvent." + event + " @ " + millis());
		showLinesMarker();
	} // _CODE_:checkbox_showline:289362:

	public void textfield1_categorisize(GTextField source, GEvent event) { // _CODE_:textfield_categorysize:798894:
		println("textfield_categorysize - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_categorysize:798894:

	public void dropList1_top5(GDropList source, GEvent event) { // _CODE_:dropList_top5:681273:
		println("dropList_top5 - GDropList >> GEvent." + event + " @ " + millis());
	} // _CODE_:dropList_top5:681273:

	public void checkbox1_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox1:999228:
		println("checkbox1 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();

	} // _CODE_:checkbox1:999228:

	public void checkbox2_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox2:867006:
		println("checkbox2 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox2:867006:

	public void checkbox3_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox3:211043:
		println("checkbox3 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox3:211043:

	public void checkbox4_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox4:625602:
		println("checkbox4 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox4:625602:

	public void checkbox5_showPlace(GCheckbox source, GEvent event) { // _CODE_:checkbox_showPlace:246507:
		println("checkbox_showPlace - GCheckbox >> GEvent." + event + " @ " + millis());
		showPointMarker();
	} // _CODE_:checkbox_showPlace:246507:

	public void button1_reset(GButton source, GEvent event) { // _CODE_:button_reset:656389:
		println("button_reset - GButton >> GEvent." + event + " @ " + millis());
		textarea_top5.setText("");
		textfield_bandwidth.setText("");
		textfield_categorysize.setText("");
		textfield_coarse.setText("");
		textfield_databasesize.setText("");
		textfield_fine.setText("");
		textfield_numberofplace.setText("");
		textfield_patternLength.setText("");
		textfield_sup.setText("");
		textfield_time.setText("");
		textfield_var.setText("");
		textfield_totaltime.setText("");
		textfield_toa.setText("");
		textfield_timefine.setText("");
		textfield_timecoarse.setText("");
		category.setText("");
		place.setText("");
		sequence.setText("");
		for (GCheckbox i : test) {
			i.setText("");
		}
		map = new UnfoldingMap(this, 240, 40, 720, 520, new OpenStreetMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);

	} // _CODE_:button_reset:656389:

	public void textfield1_patternLength(GTextField source, GEvent event) { // _CODE_:textfield_patternLength:543036:
		println("textfield_patternLength - GTextField >> GEvent." + event + " @ " + millis());
	} // _CODE_:textfield_patternLength:543036:

	public void checkbox5_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox5:262628:
		println("checkbox5 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox5:262628:

	public void checkbox6_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox6:633783:
		showChekbox();
		println("checkbox6 - GCheckbox >> GEvent." + event + " @ " + millis());
	} // _CODE_:checkbox6:633783:

	public void checkbox7_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox7:800813:
		showChekbox();
		println("checkbox7 - GCheckbox >> GEvent." + event + " @ " + millis());
	} // _CODE_:checkbox7:800813:

	public void checkbox8_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox8:335565:
		println("checkbox8 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox8:335565:

	public void checkbox9_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox9:410476:
		println("checkbox9 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox9:410476:

	public void checkbox10_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox10:603653:
		println("checkbox10 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox10:603653:

	public void checkbox11_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox11:490597:
		println("checkbox11 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox11:490597:

	public void checkbox12_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox12:294186:
		println("checkbox12 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox12:294186:

	public void checkbox13_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox13:821665:
		println("checkbox13 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox13:821665:

	public void checkbox14_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox14:278550:
		println("checkbox14 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox14:278550:

	public void checkbox15_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox15:413675:
		println("checkbox15 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox15:413675:

	public void checkbox16_clicked1(GCheckbox source, GEvent event) { // _CODE_:checkbox16:875575:
		println("checkbox16 - GCheckbox >> GEvent." + event + " @ " + millis());
		showChekbox();
	} // _CODE_:checkbox16:875575:

	public void textarea1_top5(GTextArea source, GEvent event) { // _CODE_:textarea_top5:517038:
		println("textarea_top5 - GTextArea >> GEvent." + event + " @ " + millis());
	} // _CODE_:textarea_top5:517038:

	// Create all the GUI controls.
	// autogenerated do not edit
	public void createGUI() {
		G4P.messagesEnabled(false);
		G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
		G4P.setCursor(ARROW);
//	  surface.setTitle("Sketch Window");
		subtitle = new GLabel(this, 20, 6, 149, 34);
		subtitle.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
		subtitle.setText("Configuration");
		subtitle.setTextBold();
		subtitle.setOpaque(false);
		label1 = new GLabel(this, 9, 49, 115, 33);
		label1.setText("Database Input");
		label1.setTextBold();
		label1.setOpaque(false);
		category = new GTextField(this, 10, 87, 116, 30, G4P.SCROLLBARS_NONE);
		category.setOpaque(true);
		category.addEventHandler(this, "textfield1_category");
		buttonCategory = new GButton(this, 130, 88, 100, 28);
		buttonCategory.setText("Browse Category");
		buttonCategory.addEventHandler(this, "button1_category");
		place = new GTextField(this, 10, 129, 114, 30, G4P.SCROLLBARS_NONE);
		place.setOpaque(true);
		place.addEventHandler(this, "textfield1_place");
		button_place = new GButton(this, 130, 130, 100, 28);
		button_place.setText("Browse Place");
		button_place.addEventHandler(this, "button1_place");
		sequence = new GTextField(this, 10, 170, 114, 29, G4P.SCROLLBARS_NONE);
		sequence.setOpaque(true);
		sequence.addEventHandler(this, "textfield1_change1");
		button_sequence = new GButton(this, 130, 171, 99, 28);
		button_sequence.setText("Browse Sequence");
		button_sequence.addEventHandler(this, "button1_sequence");
		label_params = new GLabel(this, 10, 248, 80, 20);
		label_params.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
		label_params.setText("Parameters");
		label_params.setTextBold();
		label_params.setOpaque(false);
		button_createdb = new GButton(this, 10, 208, 222, 30);
		button_createdb.setText("Create Database");
		button_createdb.addEventHandler(this, "button1_createdb");
		label_sup = new GLabel(this, 10, 278, 112, 20);
		label_sup.setText("Support Threshold");
		label_sup.setOpaque(false);
		label_var = new GLabel(this, 10, 309, 112, 20);
		label_var.setText("Variance Threshold");
		label_var.setOpaque(false);
		label_time = new GLabel(this, 10, 338, 111, 19);
		label_time.setText("Time Limit");
		label_time.setOpaque(false);
		textfield_sup = new GTextField(this, 130, 278, 98, 23, G4P.SCROLLBARS_NONE);
		textfield_sup.setOpaque(true);
		textfield_sup.addEventHandler(this, "textfield1_sup");
		label_toa = new GLabel(this, 10, 371, 109, 20);
		label_toa.setText("Dumpening Factor");
		label_toa.setOpaque(false);
		label_bandwidth = new GLabel(this, 10, 400, 108, 21);
		label_bandwidth.setText("Bandwidth");
		label_bandwidth.setOpaque(false);
		textfield_var = new GTextField(this, 130, 310, 98, 20, G4P.SCROLLBARS_NONE);
		textfield_var.setOpaque(true);
		textfield_var.addEventHandler(this, "textfield1_var");
		textfield_time = new GTextField(this, 130, 339, 97, 20, G4P.SCROLLBARS_NONE);
		textfield_time.setOpaque(true);
		textfield_time.addEventHandler(this, "textfield1_time");
		textfield_toa = new GTextField(this, 130, 371, 97, 19, G4P.SCROLLBARS_NONE);
		textfield_toa.setOpaque(true);
		textfield_toa.addEventHandler(this, "textfield1_toa");
		textfield_bandwidth = new GTextField(this, 130, 401, 99, 20, G4P.SCROLLBARS_NONE);
		textfield_bandwidth.setOpaque(true);
		textfield_bandwidth.addEventHandler(this, "textfield1_bandwidth");
		button_prefix = new GButton(this, 9, 460, 220, 31);
		button_prefix.setText("Prefix Span ");
		button_prefix.addEventHandler(this, "button1_prefix");
		button_Mean = new GButton(this, 10, 499, 219, 33);
		button_Mean.setText("Mean Shift");
		button_Mean.addEventHandler(this, "button1_Mean");
		button_pm = new GButton(this, 10, 540, 220, 31);
		button_pm.setText("Prefix Span & Mean Shift");
		button_pm.addEventHandler(this, "button1_pm");
		label_pattern = new GLabel(this, 959, 119, 122, 21);
		label_pattern.setText("Coarse Pattern");
		label_pattern.setOpaque(false);
		textfield_coarse = new GTextField(this, 1091, 120, 100, 20, G4P.SCROLLBARS_NONE);
		textfield_coarse.setOpaque(true);
		textfield_coarse.addEventHandler(this, "textfield1_coarse");
		label_timecorse = new GLabel(this, 959, 148, 122, 23);
		label_timecorse.setText("Running Time");
		label_timecorse.setOpaque(false);
		textfield_timecoarse = new GTextField(this, 1089, 151, 100, 20, G4P.SCROLLBARS_NONE);
		textfield_timecoarse.setOpaque(true);
		textfield_timecoarse.addEventHandler(this, "textfield1_timecoarse");
		label_refine = new GLabel(this, 958, 178, 123, 22);
		label_refine.setText("Fine Grained Pattern");
		label_refine.setOpaque(false);
		textfield_timefine = new GTextField(this, 1093, 211, 97, 21, G4P.SCROLLBARS_NONE);
		textfield_timefine.setOpaque(true);
		textfield_timefine.addEventHandler(this, "textfield1_timefine");
		label_totaltime = new GLabel(this, 960, 239, 120, 22);
		label_totaltime.setText("Total Running Time");
		label_totaltime.setOpaque(false);
		textfield_totaltime = new GTextField(this, 1090, 241, 100, 21, G4P.SCROLLBARS_NONE);
		textfield_totaltime.setOpaque(true);
		textfield_totaltime.addEventHandler(this, "textfield1_totaltime");
		label_timefine = new GLabel(this, 958, 208, 121, 23);
		label_timefine.setText("Running Time");
		label_timefine.setOpaque(false);
		textfield_fine = new GTextField(this, 1090, 180, 99, 21, G4P.SCROLLBARS_NONE);
		textfield_fine.setOpaque(true);
		textfield_fine.addEventHandler(this, "textfield1_fine");
		label_finegrained = new GLabel(this, 440, 7, 362, 28);
		label_finegrained.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
		label_finegrained.setText("Fine Grained Pattern");
		label_finegrained.setTextBold();
		label_finegrained.setOpaque(false);
		label_databaseSize = new GLabel(this, 958, 60, 124, 21);
		label_databaseSize.setText("Database Size");
		label_databaseSize.setOpaque(false);
		label_result = new GLabel(this, 1000, 6, 149, 34);
		label_result.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
		label_result.setText("Result");
		label_result.setTextBold();
		label_result.setOpaque(false);
		textfield_databasesize = new GTextField(this, 1090, 60, 99, 21, G4P.SCROLLBARS_NONE);
		textfield_databasesize.setOpaque(true);
		textfield_databasesize.addEventHandler(this, "textfield1_databasesize");
		label_numberofnodes = new GLabel(this, 958, 91, 123, 20);
		label_numberofnodes.setText("Number of place");
		label_numberofnodes.setOpaque(false);
		textfield_numberofplace = new GTextField(this, 1089, 89, 99, 21, G4P.SCROLLBARS_NONE);
		textfield_numberofplace.setOpaque(true);
		textfield_numberofplace.addEventHandler(this, "textfield1_numberofplace");
		togGroup1 = new GToggleGroup();
		checkbox_showline = new GCheckbox(this, 240, 50, 120, 20);
		checkbox_showline.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox_showline.setText("Show trajectory");
		checkbox_showline.setOpaque(false);
		checkbox_showline.addEventHandler(this, "checkbox1_showline");
		label_top5 = new GLabel(this, 961, 300, 183, 20);
		label_top5.setText("Visualisation");
		label_top5.setTextBold();
		label_top5.setOpaque(false);
		label_categorysize = new GLabel(this, 950, 360, 132, 21);
		label_categorysize.setText("Number of Category");
		label_categorysize.setOpaque(false);
		textfield_categorysize = new GTextField(this, 1080, 360, 108, 20, G4P.SCROLLBARS_NONE);
		textfield_categorysize.setOpaque(true);
		textfield_categorysize.addEventHandler(this, "textfield1_categorisize");
//		label_visualization = new GLabel(this, 960, 270, 80, 20);
//		label_visualization.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
//		label_visualization.setText("Visualization");
//		label_visualization.setTextBold();
//		label_visualization.setOpaque(false);
//		dropList_top5 = new GDropList(this, 182, 150, 90, 80, 3);
//		dropList_top5.addItem("asdf");
//		dropList_top5.addEventHandler(this, "dropList_top5");
		checkbox1 = new GCheckbox(this, 957, 388, 116, 24);
		checkbox1.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox1.setOpaque(false);
		checkbox1.addEventHandler(this, "checkbox1_clicked1");
		checkbox1.setSelected(true);
		checkbox2 = new GCheckbox(this, 1077, 387, 114, 26);
		checkbox2.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox2.setOpaque(false);
		checkbox2.addEventHandler(this, "checkbox2_clicked1");
		checkbox2.setSelected(true);
		checkbox3 = new GCheckbox(this, 958, 418, 113, 23);
		checkbox3.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox3.setOpaque(false);
		checkbox3.addEventHandler(this, "checkbox3_clicked1");
		checkbox3.setSelected(true);
		checkbox4 = new GCheckbox(this, 1077, 417, 115, 25);
		checkbox4.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox4.setOpaque(false);
		checkbox4.addEventHandler(this, "checkbox4_clicked1");
		checkbox4.setSelected(true);
		checkbox_showPlace = new GCheckbox(this, 240, 79, 120, 20);
		checkbox_showPlace.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox_showPlace.setText("Show place");
		checkbox_showPlace.setOpaque(false);
		checkbox_showPlace.addEventHandler(this, "checkbox5_showPlace");
		checkbox_showPlace.setSelected(false);
		button_reset = new GButton(this, 10, 580, 218, 30);
		button_reset.setText("Reset");
		button_reset.addEventHandler(this, "button1_reset");
		label_patternlength = new GLabel(this, 10, 430, 109, 21);
		label_patternlength.setText("Min Pattern Length");
		label_patternlength.setOpaque(false);
		textfield_patternLength = new GTextField(this, 130, 430, 101, 20, G4P.SCROLLBARS_NONE);
		textfield_patternLength.setOpaque(true);
		textfield_patternLength.addEventHandler(this, "textfield1_patternLength");
		checkbox5 = new GCheckbox(this, 958, 448, 114, 23);
		checkbox5.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox5.setOpaque(false);
		checkbox5.addEventHandler(this, "checkbox5_clicked1");
		checkbox5.setSelected(true);
		checkbox6 = new GCheckbox(this, 1077, 448, 117, 22);
		checkbox6.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox6.setOpaque(false);
		checkbox6.addEventHandler(this, "checkbox6_clicked1");
		checkbox6.setSelected(true);
		checkbox7 = new GCheckbox(this, 958, 478, 114, 23);
		checkbox7.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox7.setOpaque(false);
		checkbox7.addEventHandler(this, "checkbox7_clicked1");
		checkbox7.setSelected(true);
		checkbox8 = new GCheckbox(this, 1078, 478, 116, 22);
		checkbox8.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox8.setOpaque(false);
		checkbox8.addEventHandler(this, "checkbox8_clicked1");
		checkbox8.setSelected(true);
		checkbox9 = new GCheckbox(this, 957, 508, 114, 23);
		checkbox9.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox9.setOpaque(false);
		checkbox9.addEventHandler(this, "checkbox9_clicked1");
		checkbox9.setSelected(true);
		checkbox10 = new GCheckbox(this, 1079, 508, 115, 23);
		checkbox10.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox10.setOpaque(false);
		checkbox10.addEventHandler(this, "checkbox10_clicked1");
		checkbox10.setSelected(true);
		checkbox11 = new GCheckbox(this, 957, 539, 113, 22);
		checkbox11.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox11.setOpaque(false);
		checkbox11.addEventHandler(this, "checkbox11_clicked1");
		checkbox11.setSelected(true);
		checkbox12 = new GCheckbox(this, 1079, 538, 117, 22);
		checkbox12.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox12.setOpaque(false);
		checkbox12.addEventHandler(this, "checkbox12_clicked1");
		checkbox12.setSelected(true);
		checkbox13 = new GCheckbox(this, 959, 569, 111, 21);
		checkbox13.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox13.setOpaque(false);
		checkbox13.addEventHandler(this, "checkbox13_clicked1");
		checkbox13.setSelected(true);
		checkbox14 = new GCheckbox(this, 1078, 569, 116, 22);
		checkbox14.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox14.setOpaque(true);
		checkbox14.addEventHandler(this, "checkbox14_clicked1");
		checkbox14.setSelected(true);
		checkbox15 = new GCheckbox(this, 960, 599, 110, 21);
		checkbox15.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox15.setOpaque(false);
		checkbox15.addEventHandler(this, "checkbox15_clicked1");
		checkbox15.setSelected(true);
		checkbox16 = new GCheckbox(this, 1078, 598, 116, 23);
		checkbox16.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
		checkbox16.setOpaque(false);
		checkbox16.addEventHandler(this, "checkbox16_clicked1");
		checkbox16.setSelected(true);
		textarea_top5 = new GTextArea(this, 240, 540, 720, 110);
//		textarea_top5 = new GTextArea(this, 960, 270, 227, 110);
		textarea_top5.setOpaque(true);
		textarea_top5.addEventHandler(this, "textarea1_top5");
	}

	// Variable declarations
	// autogenerated do not edit
	GLabel subtitle;
	GLabel label1;
	GTextField category;
	GButton buttonCategory;
	GTextField place;
	GButton button_place;
	GTextField sequence;
	GButton button_sequence;
	GLabel label_params;
	GButton button_createdb;
	GLabel label_sup;
	GLabel label_var;
	GLabel label_time;
	GTextField textfield_sup;
	GLabel label_toa;
	GLabel label_bandwidth;
	GTextField textfield_var;
	GTextField textfield_time;
	GTextField textfield_toa;
	GTextField textfield_bandwidth;
	GButton button_prefix;
	GButton button_Mean;
	GButton button_pm;
	GLabel label_pattern;
	GTextField textfield_coarse;
	GLabel label_timecorse;
	GTextField textfield_timecoarse;
	GLabel label_refine;
	GTextField textfield_timefine;
	GLabel label_totaltime;
	GTextField textfield_totaltime;
	GLabel label_timefine;
	GTextField textfield_fine;
	GLabel label_finegrained;
	GLabel label_databaseSize;
	GLabel label_result;
	GTextField textfield_databasesize;
	GLabel label_numberofnodes;
	GTextField textfield_numberofplace;
	GToggleGroup togGroup1;
	GCheckbox checkbox_showline;
	GLabel label_top5;
	GLabel label_categorysize;
	GTextField textfield_categorysize;
//	GLabel label_visualization;
	GDropList dropList_top5;
	GCheckbox checkbox1;
	GCheckbox checkbox2;
	GCheckbox checkbox3;
	GCheckbox checkbox4;
	GCheckbox checkbox_showPlace;
	GButton button_reset;
	GLabel label_patternlength;
	GTextField textfield_patternLength;
	GCheckbox checkbox5;
	GCheckbox checkbox6;
	GCheckbox checkbox7;
	GCheckbox checkbox8;
	GCheckbox checkbox9;
	GCheckbox checkbox10;
	GCheckbox checkbox11;
	GCheckbox checkbox12;
	GCheckbox checkbox13;
	GCheckbox checkbox14;
	GCheckbox checkbox15;
	GCheckbox checkbox16;
	GTextArea textarea_top5;
}
