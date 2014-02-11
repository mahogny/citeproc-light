package org.ecobima.citeproclight.test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import org.ecobima.citeproclight.io.BibtexParser;
import org.ecobima.citeproclight.io.CslStyle;
import org.ecobima.citeproclight.io.JSONCSLWriter;
import org.ecobima.citeproclight.io.MedlineReader;
import org.ecobima.citeproclight.io.MendeleyCitationReaderWEB;
import org.ecobima.citeproclight.io.MendeleyDesktopClient;
import org.ecobima.citeproclight.io.RisCitationReader;
import org.ecobima.citeproclight.record.CitationRecord;

/**
 * Testing of citation system
 * 
 * @author Johan Henriksson
 *
 */
public class Test
	{

	
	
	
	public static void mainMendeley(String[] args)
		{

		try
			{
			CslStyle f=new CslStyle();
			f.parse(Test.class.getResourceAsStream("cell.csl"));

			System.out.println(MendeleyDesktopClient.getClientInfo());
			
			System.out.println("user: "+MendeleyDesktopClient.getUserAccount());
			
			Collection<CitationRecord> recs=MendeleyDesktopClient.openCitationChooser();

			for(CitationRecord r:recs)
				{
				r.remove(CitationRecord.VAR_ABSTRACT);
				System.out.println(r);
				System.out.println(f.formatIntext(r));
				System.out.println(f.formatBibl(r));
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	public static void main(String[] args)
		{
		
		try
			{
			CslStyle f=new CslStyle();
			f.parse(Test.class.getResourceAsStream("cell.csl"));
			System.out.println();
			System.out.println();
			CitationRecord rec;
			
			rec=MendeleyCitationReaderWEB.parse(Test.class.getResourceAsStream("mendeleyresponse.txt"));
			System.out.println(f.formatIntext(rec));
			System.out.println(f.formatBibl(rec));

			System.out.println();
			System.out.println();

			rec=MedlineReader.read(Test.class.getResourceAsStream("medlineformat.txt"));
			System.out.println(f.formatIntext(rec));
			System.out.println(f.formatBibl(rec));
		
			System.out.println();
			System.out.println();

			rec=RisCitationReader.read(Test.class.getResourceAsStream("sciencedirect.ris"));
			System.out.println(f.formatIntext(rec));
			System.out.println(f.formatBibl(rec));

			System.out.println();
			System.out.println();

			StringWriter sw=new StringWriter();
			JSONCSLWriter.write(rec).writeJSONString(sw);
			System.out.println("---- "+sw.getBuffer().toString());
			
			System.out.println();
			System.out.println();
			
			

			for(CitationRecord r:BibtexParser.parse(Test.class.getResourceAsStream("nature.bib")))
				{
				System.out.println(f.formatIntext(r));
				System.out.println(f.formatBibl(r));
				}

			}
		catch (Throwable e)
			{
			e.printStackTrace();
			}
		
		
		}
	
	
	
	public static void main2(String[] args)
		{
		
		try
			{
			CslStyle f=new CslStyle();
			f.parse(f.getClass().getResourceAsStream("cell.csl"));
			
			CitationRecord rec=new CitationRecord();
			
			System.out.println(f.formatIntext(rec));
			
			
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		
		
		
		}

	}
