package main;

import java.util.Map;

public class Main {

	public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            runWithConfig(args[0]);
		} else {
			run();
		}
	}

	public static void run() throws Exception {
		// default parameters
		String dataFile = "./data/sequences.txt";
		String placeFile = "./data/places.csv";
		String groupFile = "./data/groups.txt";
		String patternFile = "./data/patterns.txt";
		String statFile = "./data/stats.txt";
        int minSup = 100;
		double varThreshold = 0.0002;
		double tau = 0.8;
		int maxInterval = 120;
		double initialBandwidth = 0.02;
		// run
		Assesser assesser = new Assesser(groupFile, patternFile);
		assesser.loadDatabase(dataFile);
		assesser.loadPlaces(placeFile);
		double sup = (double) minSup / assesser.getDataSize();
		assesser.runSplitter(sup, maxInterval, varThreshold, tau, initialBandwidth);
		assesser.writeStats(statFile);
	}

	public static void runWithConfig(String configFile) throws Exception {
		// load parameters
		Map config = new Config().load(configFile);
		String inputDir = (String) config.get("inputDir");
		String outputDir = (String) config.get("outputDir");
		String dataFile = inputDir + "sequences.txt";
		String placeFile = inputDir + "places.csv";
		String groupFile = outputDir + "groups.txt";
		String patternFile = outputDir + "patterns.txt";
		String statFile = outputDir + "stats.txt";
		int minSup = (int) config.get("minSup");
		double varThreshold = (Double) config.get("varThreshold");
		int maxInterval = (Integer) config.get("maxInterval");
		double tau = (Double) config.get("tau");
		double initialBandwidth = (Double) config.get("bandwidth");
        // run
		Assesser assesser = new Assesser(groupFile, patternFile);
		assesser.loadDatabase(dataFile);
		assesser.loadPlaces(placeFile);
		double sup = (double) minSup / assesser.getDataSize();
		assesser.runSplitter(sup, maxInterval, varThreshold, tau, initialBandwidth);
		assesser.writeStats(statFile);
	}

}
