package prefixspan;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import split.Places;
import prefixspan.Item;
import prefixspan.Itemset;
import prefixspan.Sequence;

/**
 * Implementation of a sequence database.
 * Each sequence should have a unique id.
 * See examples in /test/ directory for the format of input files.
 * @author Philippe Fournier-Viger 
 **/
public class SequenceDatabase{

	 /** <p>
	   * Represents the number of customers available in the database
	   * </p>
	   */
	private int nbCustomers;
	
	// List of sequences
	private final List<Sequence> sequences = new ArrayList<Sequence>();
	
	public void loadFile(String path) throws IOException {
/*		BufferedReader br = new BufferedReader( new FileReader(path) );
        while(true)  {
            String line = br.readLine();
            if(line == null)
            	break;
            addSequence(line.split(" "));
        }
        br.close();*/
		
		// we will read line by line
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fin));
			// For each line (sequence) until end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// process this line (sequence) splitted into tokens
				processSequence(thisLine.split(" "));		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// Close the input file
			if(myInput != null){
				myInput.close();
			}
	    }
	}
	
	/**
	 * Process a line from the input file, splitted into tokens.
	 * @param tokens  a list of tokens (String).
	 */
	void processSequence(String[] tokens) {	//
		// create a new Sequence
		Sequence sequence = new Sequence(sequences.size());
		// Create an itemset that will be used to store items
		// from the first itemset and eventually the next itemsets.
		Itemset itemset = new Itemset();
		// for each tokens
		for(String integer:  tokens){
			// if this token is a timestamp
			if(integer.codePointAt(0) == '<'){ 
				// we extract the timestamp and set it as the timestamp
				// of the current itemset
				String value = integer.substring(1, integer.length()-1);
				itemset.setTimestamp(Long.parseLong(value));
			}else if(integer.equals("-1")){ 
				// If -1, this indicate the end of the current itemset,
				// so we add the itemset to the sequence, and
				// create a new itemset.
				sequence.addItemset(itemset);
				itemset = new Itemset();
			}else if(integer.equals("-2")){ 
				// If -2, it indicates the end of a sequence
				// If the last itemset is not empty, it means
				// that a -1 was missing.
				if(itemset.size() >0){
					// in this case we add the current itemset to the sequence
					// because it is not empty
					sequence.addItemset(itemset);
					itemset = new Itemset();
				}
				// finally, we add the sequence to the sequence database
				sequences.add(sequence);
			}else{ 
                                //The item ID is extracted
                                Item item = new Item(Integer.parseInt(integer));
                                // If the item is not already in this itemset
                                if(!itemset.getItems().contains(item)){
                                        // we add it to the itemset.
                                        itemset.addItem(item);
                                }
			}
		}
	}
	
/*	public void addSequence(String[] integers) {	
		//id of the new sequence: sequences.size()
		Sequence sequence = new Sequence(sequences.size()); 
		for(String integer:  integers) {
			Itemset itemset = new Itemset();
			Item item = new Item(Integer.parseInt(integer));
			itemset.addItem(item);
			sequence.addItemset(itemset);		
		}
		sequences.add(sequence);
	}*/

	public void setItemIds(Places places) {
		for(Sequence s: sequences) {
			s.setItemIds(places);
		}
	}
	
	public void addSequence(Sequence sequence){
		sequences.add(sequence);
	}
	
	public void print(){
		System.out.println("============  Context ==========");
		for(Sequence sequence : sequences){ // pour chaque objet
			System.out.print(sequence.getId() + ":  ");
			sequence.print();
			System.out.println("");
		}
	}
	
	public String toString(){
		StringBuffer r = new StringBuffer();
		for(Sequence sequence : sequences){ // for each transaction
			r.append(sequence.getId());
			r.append(":  ");
			r.append(sequence.toString());
			r.append('\n');
		}
		return r.toString();
	}
	
	public int size(){
		return sequences.size();
	}

	public List<Sequence> getSequences() {
		return sequences;
	}

	public Set<Integer> getSequenceIDs() {
		Set<Integer> set = new HashSet<Integer>();
		for(Sequence sequence : getSequences()){
			set.add(sequence.getId());
		}
		return set;
	}

}
