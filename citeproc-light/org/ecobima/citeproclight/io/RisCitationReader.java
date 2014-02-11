package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;
import org.ecobima.labnote.client.LabnoteUtil;

/**
 * RIS citation format. Originally for Endnote, Refman etc
 * 
 * @author Johan Henriksson
 *
 */
public class RisCitationReader
	{

	public static final String mimeType="application/x-research-info-systems";
	
	/**
	 * Parse a name
	 * Format: forename, surname, suffix
	 */
	private static CitationValueName parseAuthor(String s)
		{
		CitationValueName n=new CitationValueName();

		StringTokenizer stok=new StringTokenizer(s,",");
		
		n.forename=stok.nextToken();
		if(stok.hasMoreElements())
			n.surname=stok.nextToken().trim();
		//Suffix ignored
		return n;
		}

	/**
	 * Parse date
	 * Format: yyyy/mm/dd/note
	 */
	private static CitationValueDate parseDate(String therest)
		{
		CitationValueDate v=new CitationValueDate();

		StringTokenizer stok=new StringTokenizer(therest,"/");
		if(stok.hasMoreElements())
			{
			//Optional year
			String syear=stok.nextToken();
			if(!syear.isEmpty())
				v.year=Integer.parseInt(syear);
			
			//Optional month
			if(stok.hasMoreElements())
				{
				String smonth=stok.nextToken();
				if(!smonth.isEmpty())
					v.month=Integer.parseInt(smonth);
				
				//Optional day
				if(stok.hasMoreElements())
					{
					String sday=stok.nextToken();
					if(!sday.isEmpty())
						v.day=Integer.parseInt(sday);
					}
				}
			}
		return v;
		}

	/**
	 * Parse journal article citation
	 */
	private static void parseJournalArticle(CitationRecord rec, LinkedList<String> lines) throws IOException
		{
		while(!lines.isEmpty())
			{
			String line=lines.removeFirst();
			String linetype=line.substring(0, 2);
			
			if(!line.substring(2,2+4).equals("  - "))
				throw new IOException("Bad separator - not a RIS file");
			String therest=line.substring(2+4);
			
			if(linetype.equals("ER"))
				break;
			else if(linetype.equals("AU"))
				{
				rec.addArray(CitationRecord.VAR_AUTHOR, parseAuthor(therest));
				}
			else if(linetype.equals("C1"))
				{
				//Legal note
				}
			else if(linetype.equals("C2"))
				{
				rec.put(CitationRecord.VAR_PMCID, new CitationValueString(therest));
				}
			else if(linetype.equals("C6"))
				{
				rec.put(CitationRecord.VAR_NIHMSID, new CitationValueString(therest));
				}
			else if(linetype.equals("C7"))
				{
				//Article number
				}
			else if(linetype.equals("CA"))
				{
				//Caption
				}
			else if(linetype.equals("CN"))
				{
				//Call number
				}
			else if(linetype.equals("DA"))
				{
				//Date
				}
			else if(linetype.equals("DO"))
				{
				//DOI
				String url=therest;
				if(url.startsWith("http://dx.doi.org/"))
					url=url.substring("http://dx.doi.org/".length());
				rec.put(CitationRecord.VAR_DOI, new CitationValueString(url));
				}
			else if(linetype.equals("ET"))
				{
				//Epub date
				}
			else if(linetype.equals("JO"))
				{
				//Journal
				rec.put(CitationRecord.VAR_CONTAINERTITLE, new CitationValueString(therest));
				}
			else if(linetype.equals("J2"))
				{
				//Alternative journal
				}
			else if(linetype.equals("KW"))
				{
				//Keywords
				}
			else if(linetype.equals("L1"))
				{
				//file attachments
				}
			else if(linetype.equals("L4"))
				{
				//Figure
				}
			else if(linetype.equals("LA"))
				{
				//Language
				}
			else if(linetype.equals("LB"))
				{
				//Label
				}
			else if(linetype.equals("IS"))
				{
				rec.put(CitationRecord.VAR_ISSUE, new CitationValueString(therest));
				}
			else if(linetype.equals("M2"))
				{
				//Start page
				}
			else if(linetype.equals("M3"))
				{
				//Type of article
				}
			else if(linetype.equals("N1"))
				{
				//Notes
				}
			else if(linetype.equals("OP"))
				{
				//Original publication
				}
			else if(linetype.equals("PY"))
				rec.put(CitationRecord.VAR_ISSUED, parseDate(therest));
			else if(linetype.equals("SN"))
				rec.put(CitationRecord.VAR_ISSN, new CitationValueString(therest));
			else if(linetype.equals("SP"))
				rec.put(CitationRecord.VAR_PAGES, new CitationValueString(therest));
			else if(linetype.equals("ST"))
				{
				//Short title
				}
			else if(linetype.equals("T2"))
				{
				//Journal
				}
			else if(linetype.equals("TA"))
				{
				//Translated author
				}
			else if(linetype.equals("T1"))
				rec.put(CitationRecord.VAR_TITLE, new CitationValueString(therest));
			else if(linetype.equals("TT"))
				{
				//Translated title
				}
			else if(linetype.equals("UR"))
				rec.put(CitationRecord.VAR_URL, new CitationValueString(therest));
			else if(linetype.equals("VL"))
				rec.put(CitationRecord.VAR_VOLUME, new CitationValueString(therest));
			else if(linetype.equals("Y2"))
				{
				//Access date
				}

			//EP  todo
			
			}
		}
	

	public static CitationRecord read(InputStream is) throws IOException
		{
		CitationRecord rec=new CitationRecord();
		String file=LabnoteUtil.readStreamToString(is);
		
		String[] lines=file.split("\\r\\n");
		LinkedList<String> nlines=new LinkedList<String>();
		for(String s:lines)
			nlines.add(s);
		
		while(!nlines.isEmpty())
			{
			String line=nlines.removeFirst();
			String linetype=line.substring(0, 2);
			
			if(!line.substring(2,2+4).equals("  - "))
				throw new IOException("Bad separator - not a RIS file");
			String therest=line.substring(2+4);
			

			if(linetype.equals("ER"))
				break;
			else if(linetype.equals("TY"))
				{
				if(therest.equals("ABST") || therest.equals("INPR") || therest.equals("JFULL") || therest.equals("JOUR"))
					{
					parseJournalArticle(rec, nlines);
					//Set type
					rec.setType(CitationRecord.TYPE_ARTICLE);
					}
				}
				
			
			
			}
		
		
		return rec;
		}
	}
