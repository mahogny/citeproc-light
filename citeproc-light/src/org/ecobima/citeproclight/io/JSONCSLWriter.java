package org.ecobima.citeproclight.io;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValue;
import org.ecobima.citeproclight.record.CitationValueArray;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueHash;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;

/**
 * Writer of CSL JSON data
 * 
 * https://github.com/citation-style-language/schema/raw/master/csl-citation.json
 * 
 * @author Johan Henriksson
 */
public class JSONCSLWriter
	{
	public static JSONObject write(CitationRecord rec)
		{
		JSONObject root=new JSONObject();		
		
		//Write all the simple variables
		for(String key:rec.getKeys())
			{
			//Sometimes the json key is not the same as the CSL key (see spec!). Remap these:
			String jsonKey=JSONCSLReader.mapCSLJson.get(key);
			if(jsonKey!=null)
				key=jsonKey;
			
			//For different kind of types:
			CitationValue val=rec.getVariable(key);
			if(val instanceof CitationValueString)
				{
				CitationValueString s=(CitationValueString)val;
				root.put(key, s.s);
				}
			else if(val instanceof CitationValueDate)
				{
				CitationValueDate date=(CitationValueDate)val;
				
				JSONArray arr2=new JSONArray();
				if(date.year!=null)
					arr2.add(date.getYear());
				else
					arr2.add("");
				if(date.month!=null)
					arr2.add(date.getMonth());
				else
					arr2.add("");
				if(date.day!=null)
					arr2.add(date.getDay());
				else
					arr2.add("");
				
				JSONArray arr1=new JSONArray();
				arr1.add(arr2);
				JSONObject ob=new JSONObject();
				ob.put("date-parts", arr1);
				root.put(key, ob);
				}
			else if(val instanceof CitationValueArray)
				{
				CitationValueArray arr=(CitationValueArray)val;
				JSONArray jsonarr=new JSONArray();
				root.put(key,jsonarr);
				
				for(CitationValue o:arr.list)
					{
					if(o instanceof CitationValueName)
						{
						CitationValueName name=(CitationValueName)o;
						JSONObject ob=new JSONObject();
						jsonarr.add(ob);
						if(name.dropping_particle!=null)
							ob.put("dropping-particle", name.dropping_particle);
						if(name.non_dropping_particle!=null)
							ob.put("non-dropping-particle", name.non_dropping_particle);
						if(name.forename!=null)
							ob.put("given", name.forename);
						if(name.surname!=null)
							ob.put("family", name.surname);
						if(name.suffix!=null)
							ob.put("suffix", name.suffix);
						}
					else if(o instanceof CitationValueHash)
						{
						CitationValueHash name=(CitationValueHash)o;
						JSONObject ob=new JSONObject();
						jsonarr.add(ob);
						ob.put("algo", name.getHashAlgo());
						ob.put("summary", name.getSummaryMethod());
						ob.put("value", name.getHashValueString());
						}
					}
				
				}
			}
		
		//Write the type
		if(rec.getType()!=null)
			root.put("type",rec.getType());
		
		return root;
		}
	
	
	
	}
