import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SpellingCorrection {
	/*
	private char[] vowels = new char[] {'a','e','h','i','o','u','w','y'};
	private char[] labials = new char[] {'b','p','f','v'};
	private char[] misc = new char[] {'c','g','j','k','q','s','x','z'};
	private char[] dentals = new char[] {'d', 't'};
	private char[] laterals = new char[] {'l'};
	private char[] nasals = new char[] {'m','n'};
	private char[] rhotic = new char[] {'r'};
	*/
	private static int[] soundex= new int[] {0,1,2,3,0,1,2,0,0,2,2,4,5,5,0,1,2,6,2,3,0,1,0,2,0,2};
	private static int[] editex_letter= new int[] {0,1,2,3,0,7,6,10,0,6,2,4,5,5,0,7,2,4,8,3,0,7,11,8,0,9};
	private static int[] editex_letter_addition= new int[] {0,1,9,3,0,7,6,10,0,6,2,4,5,5,0,7,2,4,9,3,0,7,11,8,0,9};
	
	public SpellingCorrection() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Welcome to the spelling correction system.");
		while(true) {
			System.out.println("\nThe methods are:");
			System.out.println("1.GED 2.LED 3.Soundex 4.N-gram distance 5.Neighbourhood search");
			System.out.println("Options are:");
			System.out.println("1. find the best match(es) for a token");
			System.out.println("2. find the best match(es) for each wiki misspelled token");
			System.out.println("Please input the option number:");
			Scanner keyboard = new Scanner(System.in);
			int option = keyboard.nextInt();

			ArrayList<String> dictionary = new ArrayList<String>();
			dictionary = readFile("dict.txt");
			
			if(option == 1) {
				System.out.println("\nPlease input the token:");
				String token = keyboard.next();
				System.out.println("Please input the choice of methods:");
				int choice = keyboard.nextInt();
				ArrayList<String> returnList = new ArrayList<String>();
				if(choice == 1) {
					returnList = GEDResults(token, dictionary, true, false);
				}
				System.out.println("\nThe correct word may be:");
				for(int i=0; i<returnList.size(); i++) {
					System.out.println(returnList.get(i));
				}
				System.out.println("\n" + dictionary.size() + " words are in the dict.");
				System.out.println(returnList.size() + " words are found to be possibly correct.");
			}
			
			else if (option == 2) {
				ArrayList<String> misspells = new ArrayList<String>();
				misspells = readFile("wiki_misspell.txt");
				ArrayList<String> corrects = new ArrayList<String>();
				corrects = readFile("wiki_correct.txt");
				int predictions = 0;
		        int num = 0;
		        double precision;
		        double recall;
		        int total = misspells.size();
		        System.out.printf("\n%-7s%-18s%-18s%-10s%-20s\n","No.","Misspellings","Corrections","Found","Predictions");

		        for(int i=0; i<total; i++) {
		        		boolean match = false;
		        		String token = misspells.get(i);
					ArrayList<String> returnset = new ArrayList<String>();
					/*
					returnset = GEDResults(token, dictionary, false, false);
					for(int j=0; j<returnset.size(); j++) {
						if(corrects.get(i).equals(returnset.get(j))) {
							match = true;
							num += 1;
						}
					}	
					*/
					
					returnset = GEDResults(token, dictionary, true, false);
					for(int j=0; j<returnset.size(); j++) {
						if(corrects.get(i).equals(returnset.get(j))) {
							match = true;
							num += 1;
						}
					}					
					
					/*
					returnset = GEDResults(token, dictionary, false, true);
					for(int j=0; j<returnset.size(); j++) {
						if(corrects.get(i).equals(returnset.get(j))) {
							match = true;
							num += 1;
						}
					}
					*/
					/*
					if(!match) {
						ArrayList<String> returnset_n = neighbourhoodSearch(token, dictionary);
						for(int j=0; j<returnset_n.size(); j++) {
							if(corrects.get(i).equals(returnset_n.get(j))) {
								match = true;
								num += 1;
							}
							returnset.add(returnset_n.get(j));
						}
					}
					
					if(!match) {
						ArrayList<String> returnset_i = neighbourhoodSearchInsertion(token, dictionary);
						for(int j=0; j<returnset_i.size(); j++) {
							if(corrects.get(i).equals(returnset_i.get(j))) {
								match = true;
								num += 1;
							}
							returnset.add(returnset_i.get(j));
						}
					}
					*/
					predictions += returnset.size();
					System.out.printf("%-7d%-18s%-18s%-10s",(i+1),misspells.get(i),corrects.get(i),match);
					for(int j=0; j<returnset.size(); j++) {
						if(j < returnset.size()-1)
							System.out.printf(returnset.get(j) + " / ");
						else
							System.out.printf(returnset.get(j));
					}
					System.out.printf("\n");
		        }
		        System.out.println("The total number of words: " + total);
		        System.out.println("The total number of predictions: " + predictions);
		        System.out.println("The total number of words correct somewhere: " + num);

		        precision = (float)num / (float)predictions;
		        System.out.printf("The precision: %f\n", precision);
				recall = (float)num / (float)total;
				System.out.printf("The recall: %f\n", recall);
			}
			}
	}

	public static ArrayList<String> readFile(String fileName) {
		ArrayList<String> collection = new ArrayList<String>();
		try {
			InputStreamReader in = null;
			File f = new File(fileName);
			if (f.exists()) {
				in = new InputStreamReader(new FileInputStream(f));
				BufferedReader input = new BufferedReader(in);
				String word = (String)input.readLine();
				collection.add(word);
				try {
					while (word != null) {
						word = (String)input.readLine();
						collection.add(word);
						//System.out.println(word);
					} 
					collection.remove(collection.size()-1);
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(in != null) in.close();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return collection;
	}

	public static ArrayList<String> GEDResults(String token, ArrayList<String> wordList, boolean swap, boolean soundex){
		ArrayList<String> returnList = new ArrayList<String>();
		//int maxDistance = GED(token, wordList.get(0));
		int maxDistance = token.length() * 2;
		int times = 0;
		//returnList.add(wordList.get(0));
		for(int i=0; i<wordList.size(); i++) {
			//System.out.println(wordList.get(i));
			String word = wordList.get(i);
			int distance = 0;
			if(!swap & !soundex) {
				distance = GED(token, word);	
			}else if(!swap & soundex) {
				distance = soundexGED(token,word);
			}else if(swap & !soundex) {
				//System.out.println("SWAP");
				//distance = swapGED(token,word);
				//distance = Damerau_GED(token, word);
				//distance = Damerau_GED2(token, word);
				if(token.charAt(0) == word.charAt(0)) {
					distance = Editex(token,word);
					if(times == 0) {
						maxDistance = distance;
						times += 1;
					}
				}else {
					distance = maxDistance + 1;
				}
			}else {
				distance = 0;
			}
			/*
			if(distance == maxDistance) returnList.add(word);
			else if(maxDistance < distance) {
				maxDistance = distance;
				returnList.clear();
				returnList.add(word);
			}
			*/
			
			if(distance == maxDistance) returnList.add(word);
			else if(maxDistance > distance) {
				maxDistance = distance;
				returnList.clear();
				returnList.add(word);
			}
			
		}
		return returnList;
	}
	
	
	public static int GED(String token, String word) {
		int lf = token.length();
		int lt = word.length();
		int[][] matrix = new int[lt+1][lf+1];
		int i = 2;
		int d = 2;
		
		matrix[0][0] = 0;
		for(int j=1; j<=lt; j++) matrix[j][0] = j * i;
		for(int k=1; k<=lf; k++) matrix[0][k] = k * d;
		
		for(int j=1; j<=lt; j++) {
			for(int k=1; k<=lf; k++) {
				matrix[j][k] = Math.min(Math.min(matrix[j][k-1]+d, matrix[j-1][k] + i), 
						matrix[j-1][k-1] + equal(token.charAt(k-1), word.charAt(j-1)));
			}
		}
		return matrix[lt][lf];
	}
	
	public static int equal(char num1, char num2) {
		int m = 0;
		int r = 1;
		if(num1 == num2) return m;
		else return r;
	}
	
	public static int swapGED(String token, String word) {
		int lf = token.length();
		int lt = word.length();
		int[][] matrix = new int[lt+1][lf+1];
		int i = -1;
		int d = -1;
		
		matrix[0][0] = 0;
		for(int j=1; j<=lt; j++) matrix[j][0] = j * i;
		for(int k=1; k<=lf; k++) matrix[0][k] = k * d;
		
		for(int j=1; j<=lt; j++) {
			for(int k=1; k<=lf; k++) {
				matrix[j][k] = Math.max(Math.max(matrix[j][k-1]+d, matrix[j-1][k] + i), 
						matrix[j-1][k-1] + equal(token.charAt(k-1), word.charAt(j-1)));
				if(matrix[j-1][k] == matrix[j][k-1] && matrix[j-1][k-1] == matrix[j][k] 
						&& matrix[j-2][k-1] == matrix[j-1][k-2]) {
					matrix[j][k] = matrix[j-2][k-2] + 2;
				}
			}
		}
		return matrix[lt][lf];
	}
	
	public static int Damerau_GED(String token, String word) {
		int lf = token.length();
		int lt = word.length();
		int[][] matrix = new int[lt+1][lf+1];
		int i = 1;
		int d = 1;
		
		matrix[0][0] = 0;
		for(int j=1; j<=lt; j++) matrix[j][0] = j * i;
		for(int k=1; k<=lf; k++) matrix[0][k] = k * d;
		
		for(int j=1; j<=lt; j++) {
			for(int k=1; k<=lf; k++) {
				matrix[j][k] = Math.min(Math.min(matrix[j][k-1]+d, matrix[j-1][k] + i), 
						matrix[j-1][k-1] + equal(token.charAt(k-1), word.charAt(j-1)));
				if(j>1 && k>1 && token.charAt(k-2) == word.charAt(j-1) && token.charAt(k-1) == word.charAt(j-2)) {
					matrix[j][k] = Math.min(matrix[j][k], matrix[j-2][k-2] + 1);
				}
			}
		}
		return matrix[lt][lf];
	}
	
	public static int Damerau_GED2(String token, String word) {
		int lf = token.length();
		int lt = word.length();
		int[][] matrix = new int[lt+1][lf+1];
		int i = 1;
		int d = 1;
		
		matrix[0][0] = 0;
		for(int j=1; j<=lt; j++) matrix[j][0] = j * i;
		for(int k=1; k<=lf; k++) matrix[0][k] = k * d;
		
		for(int j=1; j<=lt; j++) {
			for(int k=1; k<=lf; k++) {
				matrix[j][k] = Math.min(Math.min(matrix[j][k-1]+d, matrix[j-1][k] + i), 
						matrix[j-1][k-1] + equal(token.charAt(k-1), word.charAt(j-1)));
				if(j>2 && k>2 && token.charAt(k-1) == word.charAt(j-2) &&
						token.charAt(k-2) == word.charAt(j-3) && token.charAt(k-3) == word.charAt(j-1)) {
						matrix[j][k] = Math.min(matrix[j][k], matrix[j-3][k-3] + 1);
					}
				if(j>1 && k>1 && token.charAt(k-2) == word.charAt(j-1) && token.charAt(k-1) == word.charAt(j-2)) {
					matrix[j][k] = Math.min(matrix[j][k], matrix[j-2][k-2] + 1);
				}
			}
		}
		return matrix[lt][lf];
	}
	
	public static int Editex(String token, String word) {		
		int lf = token.length();
		int lt = word.length();
		int[][] matrix = new int[lt][lf];
		//int i = 1;
		//int d = 1;
		
		matrix[0][0] = 0;
		for(int j=1; j<lt; j++) {
			matrix[j][0] = matrix[j-1][0] + d(word.charAt(j-1), word.charAt(j));
		}
		for(int k=1; k<lf; k++) {
			matrix[0][k] = matrix[0][k-1] + d(token.charAt(k-1), token.charAt(k));
		}
		for(int j=1; j<lt; j++) {
			for(int k=1; k<lf; k++) {
					matrix[j][k] = Math.min(Math.min(matrix[j][k-1] + d(token.charAt(k-1),token.charAt(k)), 
						matrix[j-1][k] + d(word.charAt(j-1),word.charAt(j))), 
						matrix[j-1][k-1] + r(token.charAt(k), word.charAt(j)));
			}
		}
		//System.out.println(matrix[lt-1][lf-1]);
		return matrix[lt-1][lf-1];
	}
	
	public static int d(char char1, char char2) {
		if((char1 == 'h' || char1 == 'w') && char1!= char2) return 1;
		else return r(char1, char2);
	}
	
	public static int r(char char1, char char2) {
		if(char1 == char2) return 0;
		else if(editex_letter[(int)char1-97] == editex_letter[(int)char2-97]) return 1;
		else if(editex_letter[(int)char1-97] == editex_letter_addition[(int)char2-97]) return 1;
		else if(editex_letter_addition[(int)char1-97] == editex_letter[(int)char2-97]) return 1;
		else if(editex_letter_addition[(int)char1-97] == editex_letter_addition[(int)char2-97]) return 1;
		else return 2;
	}
	
	public static int r_s(char char1, char char2) {
		if(char1 == char2) return 0;
		else if(soundex[(int)char1-97] == soundex[(int)char2-97]) return 1;
		else return 2;
	}
	
	//soundex
	public static int soundexGED(String token, String word) {
		int lf = token.length();
		int lt = word.length();
		int[][] matrix = new int[lt][lf];
		//int i = 1;
		//int d = 1;
		
		matrix[0][0] = 0;
		for(int j=1; j<lt; j++) {
			matrix[j][0] = matrix[j-1][0] + r_s(word.charAt(j-1), word.charAt(j));
		}
		for(int k=1; k<lf; k++) {
			matrix[0][k] = matrix[0][k-1] + r_s(token.charAt(k-1), token.charAt(k));
		}
		for(int j=1; j<lt; j++) {
			for(int k=1; k<lf; k++) {
					matrix[j][k] = Math.min(Math.min(matrix[j][k-1] + r_s(token.charAt(k-1),token.charAt(k)), 
						matrix[j-1][k] + r_s(word.charAt(j-1),word.charAt(j))), 
						matrix[j-1][k-1] + r_s(token.charAt(k), word.charAt(j)));
			}
		}
		//System.out.println(matrix[lt-1][lf-1]);
		return matrix[lt-1][lf-1];
	}
	
	//neighbourhood Search
	public static ArrayList<String> neighbourhoodSearch(String token, ArrayList<String> wordList){
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> neighbourhood = neighoursGenerator(token);
		for(int i=0; i<neighbourhood.size(); i++) {
			for(int j=0; j< wordList.size(); j++) {
				if(neighbourhood.get(i).equals(wordList.get(j))) {
					returnList.add(wordList.get(j));
				}
			}
		}
		return returnList;
	}
	
	public static ArrayList<String> neighbourhoodSearchInsertion(String token, ArrayList<String> wordList){
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> neighbourhood = neighoursInsertionGenerator(token);
		for(int i=0; i<neighbourhood.size(); i++) {
			for(int j=0; j< wordList.size(); j++) {
				if(neighbourhood.get(i).equals(wordList.get(j))) {
					returnList.add(wordList.get(j));
				}
			}
		}
		return returnList;
	}
	
	public static ArrayList<String> neighoursGenerator(String token){
		ArrayList<String> neighourhood = new ArrayList<String>();
		String tokenNeighbour_d = null;
		int maxIndex = token.length();
		for(int i=0; i<= maxIndex; i++) {
			if(i == 0) {
				tokenNeighbour_d = token.substring(1, maxIndex);
			}else if(i < maxIndex) {
				tokenNeighbour_d = token.substring(0, i) + token.substring(i+1, maxIndex);
			}
			//System.out.println(tokenNeighbour_d);
			neighourhood.add(tokenNeighbour_d);
		}
		return neighourhood;
	}
	
	public static ArrayList<String> neighoursInsertionGenerator(String token){
		ArrayList<String> neighourhood = new ArrayList<String>();
		String tokenNeighbour_i = null;
		int maxIndex = token.length();
		for(int i=0; i<maxIndex; i++) {
			if(i == 0) {
				tokenNeighbour_i = token.charAt(i) + token.substring(0, maxIndex);
			}else{
				tokenNeighbour_i = token.substring(0, i+1) + token.substring(i, maxIndex);
			}
			//System.out.println(tokenNeighbour_i);
			neighourhood.add(tokenNeighbour_i);
		}
		return neighourhood;
	}
	

}
