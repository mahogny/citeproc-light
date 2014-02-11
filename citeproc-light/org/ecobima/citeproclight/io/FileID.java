package org.ecobima.citeproclight.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.minidev.json.JSONObject;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueHash;
import org.ecobima.citeproclight.record.CitationValueString;


/**
 * 
 * Common functions for getting the CSLID of a file, or generating it
 * 
 * The extension could be .fileid (?) as it is vendor neutral
 * 
 * Should connect to and request IDs from several vendors.
 * Should also support checking what IDs are available, and requesting more from others. e.g. getting a DOI
 * 
 * EPIC - http://www.pidconsortium.eu/
 * identifiers.org  - currently requires manual registration http://sourceforge.net/p/identifiers-org/new-collection/
 * 
 * 
 * 
 * epic id: 10.1594/ PANGAEA.667386 (first part is some kind of server link :S )
 * http://tools.ietf.org/html/rfc3651  for the Handle server spec
 * 
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class FileID
	{
	public static String fileEnding=".fileid";
	
	private File localFile;
	private CitationRecord rec;
	
	
	/**
	 * Get which file the IDFILE is for
	 */
	public File getLocalFile()
		{
		return localFile;
		}
	
	/**
	 * Get the associated citation record object
	 */
	public CitationRecord getCitationRecord()
		{
		return rec;
		}

	
	/**
	 * Compute the filename of the IDFILE for a given file
	 */
	private static File getIDfileFor(File f)
		{
		return new File(f.getParentFile(), f.getName()+fileEnding);
		}
	
	
	/**
	 * Attempt to open the IDFILE for a given file. If it is the IDFILE rather, it will try to find the corresponding file
	 * (but will not warn if it does not exist)
	 */
	public static FileID getDataForFile(File f) throws IOException
		{
		if(f.exists())
			{
			//Figure out the name of the FIELID file
			if(!f.getName().endsWith(fileEnding))
				f=getIDfileFor(f);

			//FILEID file exists
			if(f.exists())
				{
				FileID n=new FileID();
				
				InputStream is=new FileInputStream(f);
				n.rec=JSONCSLReader.parse(is);
				n.localFile=new File(f.getParentFile(), f.getName().substring(0,f.getName().length()-fileEnding.length()));
				
				return n;
				}
			}
		return null;
		}
	
	
	/**
	 * Create an empty IDFILE for a given file. Does not write anything to disk
	 */
	public static FileID createEmptyIdFor(File f)
		{
		FileID n=new FileID();
		n.localFile=f;
		n.rec=new CitationRecord();
		return n;
		}

	
	/**
	 * Write an IDFILE for the given file
	 */
	public void saveIdFile() throws IOException
		{
		File f=getIDfileFor(localFile);
		
		JSONObject ob=JSONCSLWriter.write(rec);
		
		FileWriter w=new FileWriter(f);
		ob.writeJSONString(w);
		w.close();
		}
	
	
	/**
	 * Put a DOI into the metadata
	 */
	public void assignDOI(String doi)
		{
		rec.put(CitationRecord.VAR_DOI, new CitationValueString(doi));
		}
	
	
	/**
	 * Put a MIRIAM ID into the metadata
	 */
	public void assignMiriam(String id)
		{
		rec.put(CitationRecord.VAR_MIRIAM, new CitationValueString(id));
		}
	
	/**
	 * Put an EPIC ID into the metadata
	 */
	public void assignEpicID(String id)
		{
		rec.put(CitationRecord.VAR_EPICID, new CitationValueString(id));
		}


	/**
	 * Store a computed hash value
	 */
	public void addHash(CitationValueHash hash)
		{
		rec.addHash(hash);
		}

	
	public List<CitationValueHash> getHashes()
		{
		return rec.getHashes();
		}
	
	
	}
