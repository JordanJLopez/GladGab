import java.io.*;
import java.util.*;

	/*
	 *	Our initial gabbifying algorithm was very strict 
	 *	with its use of syllables, and required the exact
	 *	syllables of a prospective word to get a match.
	 *
	 *	We called this implementation "SadGab"
	 * 
	 * 	This is GladGab.
	 * 	The end goal is to use machine learning to teach
	 * 	GladGab to gabbify properly, to not be so picky
	 * 	about syllables.
	 * 
	 * 	The GladGab object uses what it's learned 
	 * 	(its knowledge is stored in learn.txt) to take an
	 * 	array of syllables and make it into a similar one.
	 * 
	 * 	And should that one not be up to snuff, it will further
	 * 	change it until it can no longer do so.
	 * 
	 * 	The GladGab object is essentially a combination engine.
	 */



public class GladGab {
	
	private String[] unalteredSyllables;	
	private String[] workingSyllables;
	private GladSyl[] gladSylArray;
	
	private String learnFileName = "learn.txt";
	private File learn;
	
	final int NUM_SYLLABLES = 38;
	private HashMap<String, String[]> sylMap;
	private HashMap<String[], Boolean>	combMap;
	private Stack<String[]> combinations;

	
	public GladGab(String[] inSyllables) 
	{
		this.unalteredSyllables = inSyllables;
		this.workingSyllables = inSyllables;
		gladSylArray = new GladSyl[inSyllables.length];
		
		init();
		createCombinations();
		
	}
	
	private void init()
	{
		learn = new File(learnFileName);
		Scanner learnInput;
		try {
			if(learn.createNewFile())
			{
				System.out.println("ERROR: "+learnFileName+" not found! Please run learnLikeSyl!");
				System.exit(1);
			}
			learnInput = new Scanner(learn);
			
			sylMap = new HashMap<String, String[]>(NUM_SYLLABLES + 10);
			
			while(learnInput.hasNextLine())
			{
				String workingString = learnInput.nextLine();
				
				//	learn.txt should be organized as the following
				//	syl1 syl1 syl2 syl3
				//	(syllables separated by spaces)
				//
				//	We're assigning the first syllable as a key in sylMap,
				//	and all following (similar) syllables as the value of that key
				
				sylMap.put(workingString.substring(0, workingString.indexOf(' ')), workingString.substring(workingString.indexOf(' ')+1).split(" "));
			}
			
			for(int i = 0; i < gladSylArray.length; i++)
			{
				gladSylArray[i] = new GladSyl(unalteredSyllables[i], sylMap.get(unalteredSyllables[i]));
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void createCombinations()
	{
		
		//	Creates all possible combinations of the input syllables
		//	and stores them in combinations for easy retrieval
		
		combinations = new Stack<String[]>();
		combMap = new HashMap<String[], Boolean>();
		int lastSylIndex = unalteredSyllables.length - 1;
		int modifier = 0;
		
		if(lastSylIndex != 0)
		{
			modifier = 1;
		}
		for(int currentIndex = lastSylIndex - modifier; currentIndex >= 0; currentIndex--)
		{
			workingSyllables = unalteredSyllables;
			for(int variationCurrentIndex = 0; variationCurrentIndex < gladSylArray[currentIndex].getNumSimilarSyl(); variationCurrentIndex++)
			{
				workingSyllables[currentIndex] = gladSylArray[currentIndex].getSylAt(variationCurrentIndex);
				boolean ran = false;
				for(int changeIndex = lastSylIndex; changeIndex > currentIndex; changeIndex--)
				{
					ran = true;
					//if(currentIndex != changeIndex)
						for(int variationChangeIndex = 0; variationChangeIndex < gladSylArray[changeIndex].getNumSimilarSyl(); variationChangeIndex++)
						{
							workingSyllables[changeIndex] = gladSylArray[changeIndex].getSylAt(variationChangeIndex);
							//System.out.println(createString(0, lastSylIndex));
							//if(combinations.empty() || !combinations.peek().equals(workingSyllables))
							//System.out.println("Pushing "+createString(0, lastSylIndex));
								combMap.put(workingSyllables.clone(), true);
						}
				}
				if(!ran)
				{
					//System.out.println("Pushing "+createString(0, lastSylIndex));
					combMap.put(workingSyllables.clone(), true);
				}
					
			}
		}
		for(String[] entry : combMap.keySet())
		{
			combinations.push(entry);
		}
		//System.out.println(combinations.size());
	}
	
	public String[] nextSylArray()
	{
		//System.out.println("NextSylArray called");
		if(!combinations.empty())
		{
			workingSyllables = combinations.pop();
			//System.out.println("Returning "+createString(0, workingSyllables.length - 1));
			return workingSyllables;
		}
		return null;
	}
	
	public String createString(int startIndex, int lastIndex) {
		StringBuilder sb = new StringBuilder();
		for(int i = startIndex; i <= lastIndex; i++) {
			sb.append(workingSyllables[i]).append(" ");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
	
}