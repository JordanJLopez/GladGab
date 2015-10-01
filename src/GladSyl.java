
public class GladSyl {
	private String originalSyl;
	private String[] similarSyl;
	private Boolean exhausted = false;
	private int currentIndex = 0;
	private int numSimilarSyl;
	
	public GladSyl(String originalSyl, String[] similarSyl)
	{
		this.originalSyl = originalSyl;
		this.similarSyl = similarSyl;
		this.numSimilarSyl = similarSyl.length;
	}
	
	public Boolean hasNextSyl()
	{
		if(currentIndex + 1 < similarSyl.length)
			return true;
		else
			return false;
	}
	
	public String getOriginalSyl()
	{
		return originalSyl;
	}
	
	public String getCurrentSyl()
	{
		return similarSyl[currentIndex];
	}
	
	public Boolean checkExhausted()
	{
		return exhausted;
	}
	
	public String nextSyl()
	{
		
		if(currentIndex==similarSyl.length - 1)
		{
			currentIndex = 0;
			exhausted = true;
			return similarSyl[similarSyl.length - 1];
		}
		return similarSyl[currentIndex++];
	}
	
	public void refresh()
	{
		exhausted = false;
	}
	
	public void revert()
	{
		currentIndex = 0;
	}
	
	public int getCurrentIndex()
	{
		return currentIndex;
	}
	
	public int getNumSimilarSyl()
	{
		return numSimilarSyl;
	}
	
	public String getSylAt(int index)
	{
		return similarSyl[index];
	}
}
