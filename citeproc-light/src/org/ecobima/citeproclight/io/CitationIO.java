package org.ecobima.citeproclight.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.ecobima.citeproclight.record.CitationRecord;

/**
 * General citation I/O
 * 
 * @author Johan Henriksson
 *
 */
public class CitationIO
	{
	/**
	 * Detect format and read citation and detect format
	 */
	public static List<CitationRecord> readFile(File f) throws IOException
		{
		//Try BibTeX parsing
		try
			{
			FileInputStream fis=new FileInputStream(f);
			List<CitationRecord> list=BibtexParser.parse(fis);
			fis.close();
			return list;
			}
		catch (Exception e)
			{
			}
		
		//Try RIS parsing
		try
			{
			FileInputStream fis=new FileInputStream(f);
			CitationRecord rec=RisCitationReader.read(fis);
			fis.close();
			return Arrays.asList(rec);
			}
		catch (Exception e)
			{
			}

		//Try Medline parsing
		try
			{
			FileInputStream fis=new FileInputStream(f);
			CitationRecord rec=MedlineReader.read(fis);
			fis.close();
			return Arrays.asList(rec);
			}
		catch (Exception e)
			{
			}

		
		
		return null;
		}
	
	}
