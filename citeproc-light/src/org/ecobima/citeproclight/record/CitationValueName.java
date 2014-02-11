package org.ecobima.citeproclight.record;

/**
 * Author of an article
 * 
 * @author Johan Henriksson
 *
 */
public class CitationValueName implements CitationValue
	{
	public String forename;
	public String surname;
	
	public String suffix;
	public String dropping_particle;
	public String non_dropping_particle;

	public String toString()
		{
		return forename+" "+surname;
		}
	}
