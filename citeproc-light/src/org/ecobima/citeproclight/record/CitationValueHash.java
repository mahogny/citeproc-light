package org.ecobima.citeproclight.record;

import org.ecobima.citeproclight.io.Base64;


/**
 * Hash value of biological data
 * 
 * @author Johan Henriksson
 * 
 */
public class CitationValueHash implements CitationValue
	{
	private String hashalgo;
	private String summarymethod;
	private byte[] hashvalue;
	
	public CitationValueHash(String hashalgo, String summarymethod, byte[] hashvalue)
		{
		this.hashalgo = hashalgo;
		this.summarymethod = summarymethod;
		this.hashvalue = hashvalue;
		}
	
	
	public CitationValueHash(String hashalgo, String summarymethod, String hashvalue)
		{
		this.hashalgo = hashalgo;
		this.summarymethod = summarymethod;
		this.hashvalue = Base64.decode(hashvalue);
		}


	public String getHashValueString()
		{
		return new String(Base64.encodeToChar(hashvalue,false));
		}

	public String getSummaryMethod()
		{
		return summarymethod;
		}
	
	public String getHashAlgo()
		{
		return hashalgo;
		}
	
	}
