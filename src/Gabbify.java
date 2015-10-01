import java.io.*;
import java.util.*;


//	Implementation of recursive "gabbifying" algorithm

public class Gabbify {

	static String[] inSyllables;
	static String[] inStringSplit;
	static String inString;
	static Dictionary dict;
	public static void main(String[] args) {
		
		//	Initialize Dictionary
		File rawDict = new File("cmudict.txt");
		
		dict = new Dictionary(rawDict);
		
		//	Ask for Input
		Scanner input = new Scanner(System.in);
		
		System.out.println("Welcome to SadGab");
		System.out.println("Please input words: ");
		inString = input.nextLine().toUpperCase();
		
		
		inStringSplit = inString.split(" ");
		
		//	Transform Input into ARPAbet syllables
		StringBuilder inputSyll = new StringBuilder();
		
		for(String s : inStringSplit)
		{
			if(dict.getSyllable(s) == null)
			{
				System.out.println("ERROR: \""+s+"\" not defined in dictionary");
				System.exit(1);
			}
			inputSyll.append(dict.getSyllable(s)).append(" ");
		}
		
		inSyllables = inputSyll.toString().split(" ");
		
		GladGab gladGab = new GladGab(inSyllables);
		
		boolean done = false;
		String output = null;
		while(!done)
		{
			if(inSyllables == null)
			{
				done = true;
				System.out.println("ERROR: Exhausted GladGab combinations");
			}
			else
			{
				output = convert(0);
				if(output != null)
				{
					System.out.println(output + "\nFind another? (Y/N)");
					if(input.nextLine().toUpperCase().equals("N"))
						done = true;				
				}
				inSyllables = gladGab.nextSylArray();
			}
			
		}
		//System.out.println(output);
		
		input.close();
		

	}
	/*	convert() implements our gabbifying algorithm.
	 *	
	 * 	We index each syllable in the input string, and starting
	 * 	from startIndex (which is initially 0), we check to see if
	 * 	the syllables from startIndex to endIndex (which starts
	 * 	at startIndex and is incremented)
	 * 
	 * 	If it does not find a word that can be formed with the syllables
	 * 	from startIndex to endIndex, it will increase endIndex by 1 
	 * 	(adding more syllables) until it reaches lastIndex (the last
	 * 	possible syllable in the input string). If it does that, and still
	 * 	does not find a word, it returns a null, indicating a failure.
	 * 
	 * 	If it does form a word, and that word is not contained in the 
	 * 	input string, it will do one of two things:
	 * 		(A) if endIndex (the syllable end of the word we created) is
	 * 		 equal to lastIndex (the last overall syllable in input),
	 * 		 we return the word.
	 * 		(B) otherwise, we call convert with parameters (endIndex + 1)
	 * 		 effectively repeating the process with the rest of the 
	 * 		 input syllables.
	 * 	
	 * 	If convert(endIndex + 1) returns non-null, we return our word plus the 
	 * 	return value of that convert.
	 * 	If it does return null, it indicates a failure, and convert will
	 * 	continue incrementing endIndex.
	 */
	public static String convert(Integer startIndex)
	{
		//	Starts with endIndex = startIndex
		//	effectively checking if there is a 1
		//	syllable word.
		int endIndex = startIndex;
		int lastIndex = inSyllables.length - 1;
		
		while(endIndex <= lastIndex) {
			//	checks dictionary for word formed with syllables from
			//	startIndex to endIndex
			String found = dict.getWord(createString(startIndex, endIndex));
			if(found != null && !duplicateWord(found)) {
				if(endIndex == lastIndex) {
					// Success! Found a word that uses all remaining syllables!
					return found;
				}
				else {
					//	Recursive call
					String subsequent = convert(endIndex + 1);
					if(subsequent != null) {
						//	Success! Recursive call found word(s) that uses
						//	all the remaining syllables!
						return found +" "+ subsequent;
					}
				}
			}
			//	Add more syllables to our search
			endIndex++;
		}
		return null;
	}
	
	//	Concatenates a string of syllables given a startIndex and lastIndex
	public static String createString(int startIndex, int lastIndex) {
		StringBuilder sb = new StringBuilder();
		for(int i = startIndex; i <= lastIndex; i++) {
			sb.append(inSyllables[i]).append(" ");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
	
	//	Checks to see if our new string has a word in common with our input
	//	To-Do: Add white-listed words (Like "A", "THE", etc)
	public static boolean duplicateWord(String word)
	{
		return (inString.indexOf(word.toUpperCase()) != -1);
	}
	
}
