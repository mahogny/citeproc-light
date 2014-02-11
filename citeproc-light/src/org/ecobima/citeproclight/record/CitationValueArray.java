package org.ecobima.citeproclight.record;

import java.util.LinkedList;

/**
 * Array of values
 * 
 * @author Johan Henriksson
 *
 */
public class CitationValueArray implements CitationValue
	{
	public LinkedList<CitationValue> list=new LinkedList<CitationValue>();

	public void add(CitationValue n)
		{
		list.add(n);
		}
	
	public String toString()
		{
		return list.toString();
		}
	}
