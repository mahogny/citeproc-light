package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.ecobima.citeproclight.LabnoteUtil;
import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;


/**
 * Reader for Medline/Pubmed citations
 * 
 * The file ends with .nbib sometimes
 * 
 * @author Johan Henriksson
 *
 */
public class MedlineReader
	{
	
	private static void handleRecord(CitationRecord rec, String key, String value)
		{
		HashMap<String, String> mapMedlineCSL=new HashMap<String, String>();
		mapMedlineCSL.put("PMID",CitationRecord.VAR_PMID);
		mapMedlineCSL.put("TI",CitationRecord.VAR_TITLE);
		mapMedlineCSL.put("AB",CitationRecord.VAR_ABSTRACT);
		mapMedlineCSL.put("JT",CitationRecord.VAR_CONTAINERTITLE);  //Journal title
		mapMedlineCSL.put("BTI",CitationRecord.VAR_CONTAINERTITLE); //Book title
		mapMedlineCSL.put("PMC",CitationRecord.VAR_PMCID);  //TODO with our without PMC in the beginning?
		mapMedlineCSL.put("PG",CitationRecord.VAR_PAGES);
		mapMedlineCSL.put("AD",CitationRecord.VAR_PUBLISHER_PLACE);  
		//PL - place of publication. should add?
		
		mapMedlineCSL.put("TA",CitationRecord.VAR_CONTAINERTITLE_SHORT);  
		
		mapMedlineCSL.put("IS",CitationRecord.VAR_ISSN);
		mapMedlineCSL.put("ISBN",CitationRecord.VAR_ISBN);
		mapMedlineCSL.put("VI",CitationRecord.VAR_VOLUME);
		mapMedlineCSL.put("IP",CitationRecord.VAR_ISSUE);
		
		
		HashSet<String> ignore=new HashSet<String>();
		ignore.add("AU");    //Abbreviated author
		ignore.add("OT");    //Keyword
		ignore.add("SO");    //Spelled out reference - redundant
		ignore.add("GS");    //Gene symbol
		ignore.add("JID");   //NLM specific
		ignore.add("STAT");  //Status
		ignore.add("OWN");   //Owner		
		ignore.add("OID");   //Other ID
		ignore.add("EDAT");  //Entrez date
		ignore.add("MHDA");  //MeSH date
		ignore.add("OTO");   //Other term owner

		//DP    date of publication
		//LR    date last received
		//CRDT  created
		
		if(mapMedlineCSL.containsKey(key))
			rec.put(mapMedlineCSL.get(key), new CitationValueString(value));
		else if(ignore.contains(key))
			;
		else if(key.equals("FAU"))
			{
			CitationValueName n=new CitationValueName();
			
			if(value.contains(","))
				{
				int ind=value.indexOf(',');
				n.surname=value.substring(0,ind).trim();
				n.forename=value.substring(ind+1).trim();
				}
			else
				n.surname=value;
			
			rec.addArray(CitationRecord.VAR_AUTHOR, n);
			}
		else if(key.equals("DEP"))
			{
			//Date of electronic publication

			CitationValueDate d=parseDate(value);

			rec.put(CitationRecord.VAR_ISSUED, d);
			}
		else if(key.equals("PHST"))
			{
			//Publishing history
			String type=null;
			if(value.indexOf('[')!=-1)
				{
				int ind=value.indexOf('[');
				type=value.substring(ind).trim();
				value=value.substring(0,ind).trim();
				}

			CitationValueDate d=parseDateSlash(value);

			System.out.println("Ignored medline PHST type "+type);
			rec.put(CitationRecord.VAR_ISSUED, d);
			}
		else if(key.equals("LID") || key.equals("AID"))
			{
			if(value.contains("[doi]"))
				{
				value=value.replace("[doi]", "").trim();
				rec.put(CitationRecord.VAR_DOI, new CitationValueString(value));
				}
			}
		else if(key.equals("PT"))
			{
			if(value.equals("Journal Article"))
				rec.setType(CitationRecord.TYPE_ARTICLE);
			}
		else
			System.err.println("Unhandled medline record "+key);
		}

	/**
	 * Parse date YYYYMMDD
	 */
	private static CitationValueDate parseDate(String value)
		{
		String year=value.substring(0,4);
		String month=value.substring(4,4+2);
		String day=value.substring(6,6+2);
		
		CitationValueDate d=new CitationValueDate();
		d.year=Integer.parseInt(year);
		d.month=Integer.parseInt(month);
		d.day=Integer.parseInt(day);

		return d;
		}

	/**
	 * Parse date YYYY/MM/DD
	 */
	private static CitationValueDate parseDateSlash(String value)
		{
		String year=value.substring(0,4);
		String month=value.substring(5,5+2);
		String day=value.substring(8,8+2);
		
		CitationValueDate d=new CitationValueDate();
		d.year=Integer.parseInt(year);
		d.month=Integer.parseInt(month);
		d.day=Integer.parseInt(day);

		return d;
		}

	
	/**
	 * Read citation
	 */
	public static CitationRecord read(InputStream is) throws IOException
		{
		CitationRecord rec=new CitationRecord();
		
		String thefile=LabnoteUtil.readStreamToString(is);
		StringTokenizer stok=new StringTokenizer(thefile,"\n");
		
		
		String curelem=null,curcontent=null;
		
		while(stok.hasMoreElements())
			{
			String line=stok.nextToken();
			if(!line.trim().equals(""))
				{
				String rectype=line.substring(0,4);
				String content=line.substring(4+2);
				
				if(rectype.trim().equals(""))
					{
					//Continuation of the previous line
					curcontent=curcontent+" "+content;
					}
				else
					{
					if(curelem!=null)
						{
						handleRecord(rec, curelem, curcontent);
						curelem=null;
						}
					
					//New record
					String spacing=line.substring(4,4+2);
					if(!spacing.equals("- "))
						{
						System.err.println(">"+line);
						throw new IOException("Not a medline format");
						}
					
					curelem=rectype.trim();
					curcontent=content;
					}
				}
			}

		//Handle the last entry
		if(curelem!=null)
			handleRecord(rec, curelem, curcontent);
		
		return rec;
		}
	
	}
