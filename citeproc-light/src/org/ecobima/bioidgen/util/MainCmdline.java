package org.ecobima.bioidgen.util;

import java.io.File;
import java.io.IOException;

import org.ecobima.bioidgen.BioHashRegistry;
import org.ecobima.citeproclight.io.FileID;
import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValue;
import org.ecobima.citeproclight.record.CitationValueHash;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;

/**
 * Command-line utility for viewing and creating FileID
 * 
 * @author Johan Henriksson
 *
 */
public class MainCmdline
	{

	
	/**
	 * Entry point
	 */
	public static void main(String[] args)
		{
		try
			{
			if(args.length<1)
				{
				System.out.println("Usage: fileid FILE [options]");
				System.out.println("Options:");
				System.out.println("          -create");
				System.out.println("          -set KEY VALUE");
				System.exit(1);
				}
			else
				{
				boolean toSave=false;
				
				//Load FileID data
				File f=new File(args[0]);
				FileID fid=FileID.getDataForFile(f);
				
				//Check what to do
				if(args.length>1)
					{
					
					for(int cmdi=1;cmdi<args.length;cmdi++)
						{
						String cmd=args[cmdi];
						if(cmd.equals("-set"))
							{
							if(fid==null)
								System.out.println("No FileID");
							else
								{
								String key=args[cmdi+1];
								String value=args[cmdi+2];
								fid.getCitationRecord().put(key, new CitationValueString(value));
								toSave=true;
								}
							cmdi+=2;
							}
						else if(cmd.equals("-create"))
							{
							if(fid==null)
								{
								toSave=true;
								fid=create(f);
								}
							else
								{
								System.out.println("Already created");
								}
							}
						}
					if(toSave)
						fid.saveIdFile();
					}
				else
					{
					//Just display the data
					if(fid==null)
						System.out.println("No FileID exists");
					else
						printRecord(fid);
					}
				}
			}
		catch (IOException e)
			{
			System.err.println(e.getMessage());
			e.printStackTrace();
			}
		}

	
	/**
	 * Create a new fileid
	 */
	private static FileID create(File f) throws IOException
		{
		FileID fid=FileID.createEmptyIdFor(f);
		
		for(CitationValueHash h:BioHashRegistry.computeHash(fid.getLocalFile()))
			fid.addHash(h);

		return fid;
		}

	
	/**
	 * Print a fileid
	 */
	private static void printRecord(FileID fid)
		{
		CitationRecord rec=fid.getCitationRecord();

		System.out.println("== Authors ==");
		for(CitationValueName n:rec.getNames(CitationRecord.VAR_AUTHOR))
			System.out.println("  "+n.toString());

		
		System.out.println("== Key - Values (Strings) ==");
		for(String key:rec.getKeys())
			{
			CitationValue val=rec.getVariable(key);
			if(val instanceof CitationValueString)
				System.out.println("  "+key+" = "+val.toString());
			}
		
		System.out.println("== Hashes ==");
		for(CitationValueHash h:rec.getHashes())
			System.out.println(
					"  (algo="+h.getHashAlgo()+"   summarymethod="+h.getSummaryMethod()+
					"   value="+h.getHashValueString()+")");
		}
	
	
	}
