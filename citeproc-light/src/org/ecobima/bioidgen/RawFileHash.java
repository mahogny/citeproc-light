package org.ecobima.bioidgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.ecobima.citeproclight.record.CitationValueHash;


/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class RawFileHash implements BioHashAlgorithm
	{
	public static final String summarymethod="raw";

	private String hashalgo;
	private MessageDigest digest;
	
	public void init(String hashalgo) throws IOException
		{
		this.hashalgo=hashalgo;
		try
			{
			digest = MessageDigest.getInstance(hashalgo);
			if(digest==null)
				throw new IOException("Could not create hash");
			}
		catch(Exception e)
			{
			throw new IOException(e);
			}
		}

	
	public void addData(InputStream is) throws IOException
		{
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer))>0)
			digest.update(buffer, 0, len);
		}


	public CitationValueHash getHash()
		{
		return new CitationValueHash(hashalgo, summarymethod, digest.digest());
		}


	public boolean computeForFile(File f)
		{
		try
			{
			if(!f.exists())
				throw new IOException("No such file");
			FileInputStream fs=new FileInputStream(f);
			addData(fs);
			fs.close();
			return true;
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		return false;
		}


	public String getSummaryName()
		{
		return summarymethod;
		}


	
	}
