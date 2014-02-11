package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueArray;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueHash;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Reader for CSL JSON data
 * 
 * https://github.com/citation-style-language/schema/raw/master/csl-citation.json
 * 
 * @author Johan Henriksson
 */
public class JSONCSLReader
	{
	//Mapping JSON -> CSL
	static HashMap<String,String> mapJsonCSL=new HashMap<String,String>(); 
	static HashMap<String,String> mapCSLJson=new HashMap<String,String>(); 
	
	private static void putMap(String json, String csl)
		{
		mapJsonCSL.put(json, csl);
		mapCSLJson.put(csl, json);
		}
	
	static
		{
		putMap("shortTitle",CitationRecord.VAR_TITLE_SHORT);
		}
	
	
	
	public static CitationRecord parse(InputStream is) throws IOException
		{
		if (is==null)
			throw new IOException("Input is null");
		try
			{
			JSONObject fileRoot = (JSONObject) JSONValue.parseWithException(is);
			return parse(fileRoot);
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}
		}	
	
	
	public static CitationRecord parse(String is) throws IOException
		{
		try
			{
			JSONObject fileRoot = (JSONObject) JSONValue.parseWithException(is);
			return parse(fileRoot);
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}
		}	


	
	/**
	 * Parse a record
	 */
	public static CitationRecord parse(JSONObject fileRoot) throws IOException
		{
		CitationRecord rec=new CitationRecord();
		try
			{
			//Things we know we can ignore safely (vendor specific)
			HashSet<String> ignoredKeys=new HashSet<String>();
			ignoredKeys.add("id");
			ignoredKeys.add("uris");


			
			//Go through the file
			for(String key:fileRoot.keySet())
				{
				if(ignoredKeys.contains(key))
					; //Ignore
				else if(key.equals("author"))
					{
					parseAuthors(rec, (JSONArray)fileRoot.get(key));
					}
				else if(key.equals(CitationRecord.VAR_HASH))
					{
					parseHash(rec, (JSONArray)fileRoot.get(key));
					}
				else if(key.equals("issued"))
					{
					parseDate(rec, (JSONObject)fileRoot.get(key));
					}
				else if(key.equals("type"))
					{
					String vtype=(String)fileRoot.get(key);
					rec.setType(vtype);
					}
				else if(mapJsonCSL.containsKey(key))
					{
					//Remapped variables
					rec.put(mapJsonCSL.get(key), new CitationValueString((String)fileRoot.get(key)));
					}
				else
					rec.put(key, new CitationValueString((String)fileRoot.get(key)));
				}
			
			return rec;
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}		
		}
	
	
	private static void parseHash(CitationRecord rec, JSONArray jsonArray)
		{
		CitationValueArray arr=new CitationValueArray();
		for(Object ob:jsonArray)
			{
			JSONObject js=(JSONObject)ob;

			CitationValueHash n=new CitationValueHash(
					(String)js.get("algo"),
					(String)js.get("summary"),
					(String)js.get("value")
					);
			
			arr.add(n);
			}
		rec.put(CitationRecord.VAR_HASH, arr);

		}


	private static void parseDate(CitationRecord rec, JSONObject root)
		{
		JSONArray arr=(JSONArray)root.get("date-parts");

		for(Object o:arr)
			{
			JSONArray darr=(JSONArray)o;
			
			CitationValueDate d=new CitationValueDate();
			if(darr.size()>0 && !((String)darr.get(0)).equals(""))
				d.year=Integer.parseInt((String)darr.get(0));
			if(darr.size()>1 && !((String)darr.get(1)).equals(""))
				d.month=Integer.parseInt((String)darr.get(1));
			if(darr.size()>2 && !((String)darr.get(2)).equals(""))
				d.day=Integer.parseInt((String)darr.get(2));
			
			rec.put(CitationRecord.VAR_ISSUED, d);
			}
		
		
//		"date-parts" : [ [ "2013", "6" ] ]
		}

	/**
	 * Handle authors[...]
	 */
	private static void parseAuthors(CitationRecord rec, JSONArray jsonArray)
		{
		CitationValueArray arr=new CitationValueArray();
		for(Object ob:jsonArray)
			{
			CitationValueName n=new CitationValueName();
			JSONObject js=(JSONObject)ob;
			n.forename=(String)js.get("given");
			n.surname=(String)js.get("family");
			n.dropping_particle=(String)js.get("dropping-particle");
			n.non_dropping_particle=(String)js.get("non-dropping-particle");
			n.suffix=(String)js.get("suffix");
			
			arr.add(n);
			}
		rec.put("author", arr);
		}

	}
