package org.ecobima.citeproclight.record;

/**
 * Generic value
 * 
 * @author Johan Henriksson
 *
 */
public class CitationValueString implements CitationValue
	{
	public CitationValueString(String string)
		{
		this.s=string;
		}

	public String s;
	
	public String toString()
		{
		return s;
		}
	}
