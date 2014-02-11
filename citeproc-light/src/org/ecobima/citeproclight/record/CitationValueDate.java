package org.ecobima.citeproclight.record;

import java.util.Calendar;


/**
 * Citation date value
 * 
 * @author Johan Henriksson
 *
 */
public class CitationValueDate implements CitationValue
	{
	public Integer year, month, day;

	public CitationValueDate()
		{
		}
	
	public CitationValueDate(long t)
		{
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(t);

		year=cal.get(Calendar.YEAR);
		month=cal.get(Calendar.MONTH)+1;
		day=cal.get(Calendar.DAY_OF_MONTH);
		
//				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		}

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
