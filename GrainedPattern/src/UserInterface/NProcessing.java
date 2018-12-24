package UserInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import NDatabase.NDatabase;
import NDatabase.NPlace;
import NMeanshift.NMeanshiftAlgo;
import NPrefixspan.NPrefixspanAlgo;
import NPrefixspan.NSnippet;
import NPrefixspan.NSnippetCluster;

public class NProcessing {
	public NDatabase database = new NDatabase();
	double minSup;
	int maxInterval;
	double varThreshold;
	double tau;
	double initialBandwidth;
	int patternlength;
	private List<NSnippetCluster> CoarsePattern = null;
	private List<NSnippetCluster> FineGranined = null;
	List<NPlace> places = null;
	
	public void createDatabase(String category, String place, String sequence) throws IOException {
		database.createDatabase(category, place, sequence);
	}

	public void setParams(String minSup, String maxInterval, String varThreshold, String tau, String initialBandwidth, String patternlength) {
		this.minSup = new Double(minSup);
		this.maxInterval = Integer.parseInt(maxInterval);
		this.varThreshold = new Double(varThreshold);
		this.tau = new Double(tau);
		this.initialBandwidth = new Double(initialBandwidth);
		this.patternlength=Integer.parseInt(patternlength);;
	}

	public void runSplitter() {
		NPrefixspanAlgo prefix = new NPrefixspanAlgo(minSup, this.maxInterval,this.patternlength);
		prefix.runAlgorithm(database);
		this.CoarsePattern = prefix.getCoarsePattern();
	}

	public void runMeanShift() {
		NMeanshiftAlgo refine = new NMeanshiftAlgo(minSup, varThreshold, tau, initialBandwidth, database);
//		System.out.println(CoarsePattern.size());
//		System.out.println(minSup);
//		System.out.println(varThreshold);
//		System.out.println(tau);
//		System.out.println(initialBandwidth);
//		System.out.println(database.getPlace().size());
		List<NSnippetCluster> Rpatterns = new ArrayList<NSnippetCluster>(); // fine patterns are represented as group
					
		for (NSnippetCluster sc : CoarsePattern) {
//			System.out.println(sc.getSnippetLength());

			if (sc.getSnippetLength() >= patternlength) {
//				if (sc.mGroupSequence.get(0) == 11 && sc.mGroupSequence.get(1) == 1) {
//					sc.view();
//					System.out.println(sc.getSnippetLength());
//					System.out.println(sc.getSnippet().size());
					refine.minePatterns(sc.getSnippetLength(), sc.getSnippet());
					Rpatterns.addAll(refine.getPatterns());
//				}
			}
		}
		this.FineGranined=Rpatterns;
		
//		for(NSnippetCluster S : Rpatterns) {
//			System.out.println(S.getSnippet().size());
//		}
	}

	public List<NSnippetCluster> getCoarsePattern() {
		return this.CoarsePattern;
	}
	
	public List<NSnippetCluster> getFineGrained() {
		return this.FineGranined;
	}

	public void viewCoarse() {
		for (NSnippetCluster s : this.CoarsePattern) {
			s.view();
			System.out.println();
		}
	}
	
	public void viewFineGrained() {
		for (NSnippetCluster s : this.FineGranined) {
			s.view();
			System.out.println();
		}
	}
	
	
	public void getCoarsePlace(){
		
		for (NSnippetCluster ns : this.CoarsePattern) {
			places = new ArrayList<NPlace>();
			if (ns.mGroupSequence.size() >= 2) {
				for (NSnippet s : ns.getSnippet()) {
					for (NPlace plc : s.mPlaceSequence) {
						if (!places.contains(plc)) {
							places.add(plc);
						}
					}
				}
			}

		}
	}

}
