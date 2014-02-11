package org.ecobima.citeproclight.record;

/**
 * Citation date value
 * 
 * @author Johan Henriksson
 *
 */
public class CitationValueDate implements CitationValue
	{
	public Integer year, month, day;
	
	@Override
	public String toString()
		{
		return ""+year+"-"+month+"-"+day;
		}

	public String getYear()
		{
		return ""+year;
		}

	public String getMonth()
		{
		return ""+month;
		}

	public String getDay()
		{
		return ""+day;
		}
	
	
	}
