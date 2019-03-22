import java.io.*;
import java.util.*;

class Word{
	//Key: output value
	//Value: Number of occurrences of the word in a given output
	HashMap<String, Integer> output_count;
	HashMap<String, Double> probabilities;

	Word(){
		output_count = new HashMap<String,Integer>();
		probabilities = new HashMap<String, Double>();
	}

	void calculate_probabilities(){
		int sum = 0;
		for(String term: output_count.keySet())
			sum += output_count.get(term);
		
		for(String term: output_count.keySet()){
			probabilities.put(term, (double) (output_count.get(term)+1)/(sum));
		}
	}
}

public class NaiveBayes{
	public static void main(String[] args) {
		/*
		files[0] = Train Input, files[1] = Train Output
		files[2] = Test Input, 	files[3] = Test Output
		*/
		Scanner[] files = get_files(args);

		/*
		file_lines.get(0) = Train Input, file_lines.get(1) = Train output
		file_lines.get(2) = Test Input, file_lines.get(3) = Test Output
		*/
		ArrayList<ArrayList<String>> file_lines = get_lines(files);

		//Number of unique outputs
		int unique_outputs = count_outputs(file_lines.get(1));
		//output_count contains the number of occurrences of output
		HashMap<String, Integer> output_count = get_output_count(file_lines.get(1));
		//output_count uses terms as the key and returns a value that is a Word
		//Each word keeps track of how many occurrences of the word there are in each class
		HashMap<String, Word> term_occurrences = get_term_occurrences(file_lines.get(0),file_lines.get(1));

		//Debug command to see how many occurrences of each word appears in each class
		//print_term_occurrences(term_occurrences);

		set_probabilities(term_occurrences);

		test_data(file_lines.get(2), file_lines.get(3), term_occurrences, output_count);
	}

	public static void test_data(ArrayList<String> test_input, ArrayList<String> test_output, HashMap<String,Word> term_occurrences, HashMap<String, Integer> output_count){
		System.out.println("Predicted output\tCorrect output");
		for(int i = 0; i < test_input.size(); i++){
			double max_prob = 0;
			String pred = "";
			for(String output:output_count.keySet()){
				double prob = 1;
				for(String s: test_input.get(i).split(" ")){
					if(term_occurrences.containsKey(s))
						if(term_occurrences.get(s).probabilities.containsKey(output))
							prob *= term_occurrences.get(s).probabilities.get(output);
				}
				if (prob > max_prob)
					pred = output;
			}
			System.out.println(pred + "\t" + test_output.get(i));
		}
	}

	public static void set_probabilities(HashMap<String, Word> term_occurrences){
		for(String term: term_occurrences.keySet())
			term_occurrences.get(term).calculate_probabilities();
	}

	public static void print_term_occurrences(HashMap<String, Word> term_occurrences){
		for(String key: term_occurrences.keySet()){
			System.out.println("Key is: " + key);
			for(String word_key: term_occurrences.get(key).output_count.keySet()){
				System.out.println("\tWord key is " + word_key);
				System.out.println("\t\t\tValue is: " + term_occurrences.get(key).output_count.get(word_key));
			}
		}
	}

	public static HashMap<String, Integer> get_output_count(ArrayList<String> output){
		HashMap<String, Integer> return_hashmap = new HashMap<String,Integer>();

		for(String word:output){
			if(return_hashmap.containsKey(word)){
				int old = return_hashmap.get(word);
				return_hashmap.put(word, ++old);
			}else{
				return_hashmap.put(word, 1);
			}
		}

		return return_hashmap;
	}

	public static HashMap<String, Word> get_term_occurrences(ArrayList<String> input_text, ArrayList<String> output_text){
		HashMap<String, Word> return_hashmap = new HashMap<String, Word> ();
		
		int i = 0;
		String output_line;
		for(String input_line: input_text){
			output_line = output_text.get(i++);
			for(String word: input_line.split(" ")){
				if(return_hashmap.containsKey(word)){
					if(return_hashmap.get(word).output_count.containsKey(output_line)){
						int old_value = return_hashmap.get(word).output_count.get(output_line);
						return_hashmap.get(word).output_count.put(output_line, ++old_value);
					}else{
						return_hashmap.get(word).output_count.put(output_line, 1);
					}
				}
				else{
					Word new_word = new Word();
					new_word.output_count.put(output_line, 1);
					return_hashmap.put(word, new_word);
				}
			}
		}

		return return_hashmap;
	}

	/*
		Converts an ArrayList to a set and returns the size of the set
	*/
	public static int count_outputs(ArrayList<String> output_list){
		Set<String> set = new HashSet<String>(output_list);
		return set.size();
	}

	public static ArrayList<ArrayList<String>> get_lines(Scanner[] files){
		ArrayList<ArrayList<String>> return_array = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < 4; i++){
			ArrayList<String> new_list = new ArrayList<String>();
			return_array.add(new_list);
			while(files[i].hasNextLine()){
				return_array.get(i).add(files[i].nextLine());
			}
		}
		return return_array;
	}

	public static Scanner[] get_files(String[] args){
		Scanner[] return_array = new Scanner[4];
		try{
			return_array[0] = new Scanner(new File(args[0]));
			return_array[1] = new Scanner(new File(args[1]));
			return_array[2] = new Scanner(new File(args[2]));
			return_array[3] = new Scanner(new File(args[3]));
		}catch(Exception e){
			System.out.println("Some file not found");
			System.exit(-1);
		}
		return return_array;
	}
}