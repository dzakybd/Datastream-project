package Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequenceDatabase {
	// List of sequences
	private final List<Sequence> sequences = new ArrayList<Sequence>();

	public List<Sequence> getSequences() {
		return sequences;
	}
	
	public void setItemIds(Places places) {
		for(Sequence s: sequences) {
			s.setItemIds(places);
		}
	}

	public void loadFile(String path) throws IOException {

		String thisLine;
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			String line = br.readLine();
			while (line != null) {
				// note that sequence file is separated by empty string
				processSequence(line.split(" "));
				line = br.readLine();
			}
		} finally {
			br.close();
		}
	}

	/**
	 * Process a line from the input file, splitted into tokens.
	 * 
	 * @param tokens a list of tokens (String).
	 */
	void processSequence(String[] tokens) { //
		// create a new Sequence
		Sequence sequence = new Sequence(sequences.size());
		// Create an itemset that will be used to store items
		// from the first itemset and eventually the next itemsets.
		Itemset itemset = new Itemset();
		// for each tokens
		for (String integer : tokens) {
			// if this token is a timestamp
			if (integer.codePointAt(0) == '<') {
				// we extract the timestamp and set it as the timestamp
				// of the current itemset
				String value = integer.substring(1, integer.length() - 1);
//				System.out.println(value);
				itemset.timestamp = Long.parseLong(value);
			} else if (integer.equals("-1")) {
				// If -1, this indicate the end of the current itemset,
				// so we add the itemset to the sequence, and
				// create a new itemset.
				sequence.itemsets.add(itemset);
				itemset = new Itemset();
			} else if (integer.equals("-2")) {
				// If -2, it indicates the end of a sequence
				// If the last itemset is not empty, it means
				// that a -1 was missing.
				if (itemset.items.size() > 0) {
					// in this case we add the current itemset to the sequence
					// because it is not empty
					sequence.itemsets.add(itemset);
					itemset = new Itemset();
				}
				// finally, we add the sequence to the sequence database
				sequences.add(sequence);
			} else {
				// The item ID is extracted
				Item item = new Item(Integer.parseInt(integer));
				// If the item is not already in this itemset
				if (!itemset.items.contains(item)) {
					// we add it to the itemset.
					itemset.addItem(item);
				}
			}
		}
	}
	
	public void view(){
		for(Sequence s:sequences) {
			s.view();
			System.out.println();
		}
	}
}
