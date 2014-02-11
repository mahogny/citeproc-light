package org.ecobima.bioidgen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ecobima.citeproclight.record.CitationValueHash;


/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class BioHashRegistry
	{
	private static HashMap<String, Class<? extends BioHashAlgorithm>> methods=new HashMap<String, Class<? extends BioHashAlgorithm>>();

	static
		{
		register(RawFileHash.summarymethod, RawFileHash.class);
		register(FastaHash.summarymethod, FastaHash.class);
		}
	
	
	public static void register(String method, Class<? extends BioHashAlgorithm> cl)
		{
		methods.put(method,cl);
		}

	
	public static Set<String> getMethods()
		{
		return methods.keySet();
		}


	public static BioHashAlgorithm createInstance(String method)
		{
		try
			{
			return methods.get(method).newInstance();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return null;
			}
		}
	
	
	
	

	public static List<CitationValueHash> computeHash(File f)
		{
		return computeHash(f, "SHA-512");
		}
	
	public static List<CitationValueHash> computeHash(File f, String hashalgo)
		{
		LinkedList<CitationValueHash> hashes=new LinkedList<CitationValueHash>();

		
		for(String m:BioHashRegistry.getMethods())
			{
			BioHashAlgorithm algo=BioHashRegistry.createInstance(m);
			try
				{
				algo.init(hashalgo);
				if(algo.computeForFile(f))
					hashes.add(algo.getHash());
				}
			catch (IOException e)
				{
				e.printStackTrace();
				}
			}

		
		return hashes;
		}
	

	}
