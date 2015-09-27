import java.io.*;
import java.util.*;

public class learnLikeSyl {
	
	static File rawDict;
	static File phones;
	static File trainingSet;
	static File learn;
	static final int NUM_SYLLABLES = 38;
	static String rawDictFileName = "cmudict.txt";
	static String phonesFileName = "phones.txt";
	static String learnFileName = "learn.txt";
	
	//num_syllables + 10 : To maintain load factor of .75
	static LinkedHashMap<String, String> sylMap = new LinkedHashMap<String, String>(NUM_SYLLABLES + 10);
	
	static Dictionary dict;
	
	//	One of the major problems with our initial SadGab attempts was that SadGab was too rigid
	//	in its attempts to match syllables in the pre-gab and post-gab strings.
	//
	//	What I'm hoping to do here is establish relationships between syllables by feeding in
	//	actual MadGab combinations, and having LearnLikeSyl try and find where some syllables may
	//	be substituted for others.
	
	static String trainingSetName = "trainingSet1.txt";
	
	public static void main(String[] args) {
		
		if(args.length < 2)
		{
			System.out.println("Usage: learnLikeSyl [command] [file]");
			System.exit(1);
		}
		
		System.out.println("Running learnLikeSyl with args "+args[0]+" "+args[1]);
		
		if(args[0].equals("init"))
		{
			phones = new File(args[1]);
			learn = new File(learnFileName);
			initialize();
		}
		
		if(args[0].equals("learn"))
		{
			trainingSet = new File(args[1]);
			learn = new File(learnFileName);
			rawDict = new File(rawDictFileName);
			learn();
		}
		
		System.exit(0);

	}
	
	//	Takes input syllables file, takes only the syllables from it
	//	
	//	learn.txt will be organized as follows:
	//
	//	syl1 syl1
	//	syl2 syl2 syl3 syl5
	//	syl3 syl3 syl2 syl5
	//	syl4 syl4
	//	syl5 syl5 syl3 syl2
	//
	//	where the first syl in a row is marked as similar to syls
	//	in the same row
	
	//	we initialize learn.txt with rows of pairs of syllables
	private static void initialize()
	{
		try {
			Scanner phonesInput = new Scanner(phones);
			PrintWriter learnOutput = new PrintWriter(learn);
			String workingString;
			
			while(phonesInput.hasNextLine())
			{	
				workingString = phonesInput.nextLine();
				workingString = workingString.substring(0, workingString.indexOf('\t'));
				learnOutput.println(workingString+" "+workingString);
				System.out.println("Wrote ["+workingString + " " + workingString+"] to "+learnFileName);
			}
			
			phonesInput.close();
			learnOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private static void learn()
	{
		Scanner learnInput = null;
		Scanner trainingSetInput = null;
		String workingString;
		
		//First check to see if the files we'll be working with exist
		try {
			if(learn.createNewFile())
			{
				System.out.println("ERROR: "+learnFileName+" not found! Please run init!");
				System.exit(1);
			}
			learnInput = new Scanner(learn);
			
			if(rawDict.createNewFile())
			{
				System.out.println("ERROR: "+rawDictFileName+" not found!");
				System.exit(1);
			}
			dict = new Dictionary(rawDict);
			
			if(trainingSet.createNewFile())
			{
				System.out.println("ERROR: "+trainingSet.getName()+" not found!");
				System.exit(1);
			}
			trainingSetInput = new Scanner(trainingSet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//initialize sylMap
		//key = syllable
		//value = like syllables
		while(learnInput.hasNextLine())
		{
			workingString = learnInput.nextLine();
			sylMap.put(workingString.substring(0, workingString.indexOf(' ')), workingString.substring(workingString.indexOf(' ')+1));
		}
		learnInput.close();
		
		analyze(trainingSetInput);
				
	}
	
	private static String[] combineStringArrays(String[] array1, String[] array2)
	{
		int length1;
		int length2;
		if(array1 == null)
			length1 = 0;
		else
			length1 = array1.length;
		if(array2 == null)
			length2 = 0;
		else
			length2 = array2.length;
		
		int length3 = length1 + length2;
		
		String[] array3 = new String[length3];
		
		for(int i = 0; i<length1; i++)
			array3[i]=array1[i];
		for(int j = 0; j<length2; j++)
			array3[j + length1]=array2[j];
		
		return array3;
	}
	
	private static void analyze(Scanner trainingSetInput)
	{
		//String[] preGabWords = null;
		String[] preGabSyls = null;
		String[] preGabWords = null;
		
		//String[] postGabWords = null;
		String[] postGabSyls = null;
		String[] postGabWords = null;
		
		String workingString = null;
		
		//	trainingSet should have the following format:
		//	pre gab word\tpost gab words
		//	pre gab word	post gab words
		//	pregab and postgab separated by a tab.
		
		while(trainingSetInput.hasNextLine())
		{
			workingString = trainingSetInput.nextLine().toUpperCase();
			//System.out.println("Processing: "+workingString);
			preGabWords = workingString.substring(0, workingString.indexOf('\t')).split(" ");
			postGabWords = workingString.substring(workingString.indexOf('\t')+1).split(" ");
			
			preGabSyls = null;
			postGabSyls = null;
			
			for(int i = 0; i < preGabWords.length; i++)
			{
				//System.out.print(preGabSyls[i]+" = ");
				workingString = dict.getSyllable(preGabWords[i]);
				//System.out.println(workingString);
						
				if(workingString != null)
				preGabSyls = combineStringArrays(preGabSyls, workingString.split(" "));
				//System.out.println(preGabSyls[i]);
			}
			
			for(int i = 0; i < postGabWords.length; i++)
			{
				//System.out.print(postGabSyls[i]+" = ");
				workingString = dict.getSyllable(postGabWords[i]);
				if(workingString != null)
				postGabSyls = combineStringArrays(postGabSyls, workingString.split(" "));
			
				//System.out.println(postGabSyls[i]);
			}
			
			
			
			if(preGabSyls == null || postGabSyls == null || preGabSyls.length != postGabSyls.length)
			{
				//System.out.println("WARNING: DIFFERENT SYLLABLE LENGTHS");
				//System.out.println("pre length: "+preGabSyls.length+" post length: "+postGabSyls.length);
			}
			else
			{
				//	Initially assuming a One-To-One syllable conversion,
				//	hoping this establishes similar syllables
				for(int i = 0; i < preGabSyls.length; i++)
				{
					
					if(!sylMap.containsKey(preGabSyls[i]))
						System.out.println("ERROR: sylMap does not contain ["+preGabSyls[i]+"]");
					else
					{
						//System.out.println("Comparing pre: ["+preGabSyls[i]+"] and post: ["+postGabSyls[i]+"]");
						if(sylMap.get(preGabSyls[i]).indexOf(postGabSyls[i]) == -1)
						{
							System.out.println("Adding ["+postGabSyls[i]+"] to row "+preGabSyls[i]);
							sylMap.put(preGabSyls[i], sylMap.get(preGabSyls[i])+" "+postGabSyls[i]);
						
						}
					}
					
				}
			}
		}
		
		try {
			PrintWriter learnOutput = new PrintWriter(new FileOutputStream(learnFileName, false));
			for(String key : sylMap.keySet())
			{
				learnOutput.println(key + " " + sylMap.get(key));
			}
			System.out.println("Wrote to "+learnFileName);
			learnOutput.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
