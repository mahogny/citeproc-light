package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueArray;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Parser for mendeley JSON data
 * 
 * @author Johan Henriksson
 */
public class MendeleyCitationReaderWEB
	{
	/*
	https://sites.google.com/site/mendeleyapi/home/authentication
*/
	
	/**
	 * Parse a record
	 */
	public static CitationRecord parse(InputStream is) throws IOException
		{
		if(is==null)
			throw new IOException("Input is null");
		CitationRecord rec=new CitationRecord();
		try
			{
			JSONObject fileRoot=(JSONObject)JSONValue.parseWithException(is);

			//Things we know we can ignore safely (vendor specific)
			HashSet<String> ignoredKeys=new HashSet<String>();
			ignoredKeys.add("stats");
			ignoredKeys.add("mendeley_url");
			ignoredKeys.add("uuid");
			ignoredKeys.add("public_file_hash");

			//Mapping mendeley -> CSL
			HashMap<String,String> variableKeys=new HashMap<String,String>();  // mendeley -> CSL
			variableKeys.put("issue",CitationRecord.VAR_ISSUE);
			variableKeys.put("pages",CitationRecord.VAR_PAGES);
			variableKeys.put("abstract",CitationRecord.VAR_ABSTRACT);
			variableKeys.put("volume",CitationRecord.VAR_VOLUME);
			variableKeys.put("title",CitationRecord.VAR_TITLE);
			variableKeys.put("issn",CitationRecord.VAR_ISSN);
			variableKeys.put("isbn",CitationRecord.VAR_ISBN); 
			//pubmed etc?
			//TODO a whole lot other variables

			
			HashMap<String, String> typeMap=new HashMap<String, String>();
			typeMap.put("Journal Article", CitationRecord.TYPE_ARTICLE);
			
			//Go through the file
			Integer year=null;
			for(String key:fileRoot.keySet())
				{
				if(ignoredKeys.contains(key))
					; //Ignore
				else if(key.equals("authors"))
					{
					parseAuthors(rec, (JSONArray)fileRoot.get(key));
					}
				else if(key.equals("year"))
					{
					year=(Integer)fileRoot.get(key);
					}
				else if(key.equals("identifiers"))
					{
					JSONObject obIdent=(JSONObject)fileRoot.get(key);
					for(String identkey:obIdent.keySet())
						if(variableKeys.containsKey(identkey))
							rec.put(identkey, new CitationValueString((String)obIdent.get(identkey)));
						else
							System.err.println("Unhandled ident: "+identkey);
					}
				else if(key.equals("type"))
					{
					String vtype=(String)fileRoot.get(key);
					if(typeMap.containsKey(vtype))
						rec.setType(typeMap.get(vtype));
					else
						System.err.println("Unhandled type: "+vtype);
					}
				else if(variableKeys.containsKey(key))
					{
					rec.put(variableKeys.get(key), new CitationValueString((String)fileRoot.get(key)));
					}
				else
					System.err.println("Unhandled: "+key);
				}
			
			if(year!=null)
				{
				CitationValueDate d=new CitationValueDate();
				d.year=year;
				rec.put("issued", d);
				}
			
			return rec;
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}		
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
			n.forename=(String)js.get("forename");
			n.surname=(String)js.get("surname");
			arr.add(n);
			}
		rec.put("author", arr);
		}

	}
