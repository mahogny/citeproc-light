package org.ecobima.citeproclight.record;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.ecobima.citeproclight.LabnoteUtil;


/**
 * 
 * 
 * @author mahogny
 *
 */
public class CitationRecord
	{
	public static final String VAR_AUTHOR = "author";
	public static final String VAR_PMCID = "PMCID"; //?
	public static final String VAR_NIHMSID = "NIHMSID"; //?
	public static final String VAR_ISSN = "ISSN";
	public static final String VAR_TITLE = "title";
	public static final String VAR_ISSUE= "issue";
	public static final String VAR_PAGES = "page";
	public static final String VAR_VOLUME = "volume";
	public static final String VAR_CONTAINERTITLE = "container-title";
	public static final String VAR_CONTAINERTITLE_SHORT = "container-title-short";
	public static final String VAR_ISSUED = "issued";
	public static final String VAR_URL = "URL";
	public static final String VAR_ABSTRACT = "abstract";
	public static final String VAR_ISBN = "ISBN";
	public static final String VAR_PUBLISHER = "publisher";
	public static final String VAR_PUBLISHER_PLACE = "publisher-place";
	public static final String VAR_TITLE_SHORT = "title-short";
	public static final String VAR_PMID = "PMID";
	//etc

	public static final String VAR_DOI = "DOI";

	
	public static final String TYPE_ARTICLE = "article";
	public static final String TYPE_BOOK = "book";
	public static final String TYPE_CHAPTER = "chapter";
	public static final String TYPE_DATASET = "dataset";
	//etc

	
	///// These are our proposed extensions ///////////////////////////////
	public static final String VAR_MIRIAM = "MIRIAMID";
	public static final String VAR_EPICID = "EPICID";
	public static final String VAR_HASH = "datahash";
	///////////////////////////////////////////////////////////////////////
	
	
	private String type;
	private HashMap<String, CitationValue> variables=new HashMap<String, CitationValue>();

	
	public void put(String s, CitationValue val)
		{
		variables.put(s,val);
		}
	/**
	 * 
	 */
	public CitationValue getVariable(String variableName)
		{
		return variables.get(variableName);
		}

	
	/**
	 * 
	 */
	public Collection<CitationValueName> getNames(String variableName)
		{
		LinkedList<CitationValueName> list=new LinkedList<CitationValueName>();
		CitationValueArray arr=(CitationValueArray)getVariable(variableName);
		if(arr!=null)
			for(CitationValue v:arr.list)
				list.add((CitationValueName)v);
		return list;
		}

	public CitationValueDate getDate(String variableName)
		{
		return (CitationValueDate)getVariable(variableName);
		}



	public String getString(String variableName)
		{
		CitationValueString v=(CitationValueString)getVariable(variableName);
		if(v!=null)
			return ((CitationValueString)v).s;
		else
			return null;
		}
	
	
	@Override
	public String toString()
		{
		return "citationtype:"+type+", "+variables.toString();
		}
	
	/**
	 * Add variable to array. Create array if needed
	 */
	public void addArray(String var, CitationValue val)
		{
		CitationValueArray arr=(CitationValueArray)getVariable(var);
		if(arr==null)
			{
			arr=new CitationValueArray();
			put(var, arr);
			}
		arr.list.add(val);
		}
	

	public void setType(String s)
		{
		type=s;
		}
	
	public String getType()
		{
		return type;
		}
	
	
	@Override
	public int hashCode()
		{
		return 0; //TODO
		}
	
	@Override
	public boolean equals(Object obj)
		{
		if(obj instanceof CitationRecord)
			{
			CitationRecord o=(CitationRecord)obj;
			return 
					LabnoteUtil.equalsNull(type, o.type) &&
					variables.equals(o.variables);  //TODO!
			}
		else
			return false;
		}
	
	
	public void remove(String key)
		{
		variables.remove(key);
		}
	
	

	
	/**
	 * Try to figure out a website where this document can be viewed
	 */
	public Map<String, URL> getViewingURL()
		{
		TreeMap<String, URL> urls=new TreeMap<String, URL>();
		try
			{
			String doi=getString(CitationRecord.VAR_DOI);
			if(doi!=null)
				{
				if(!doi.startsWith("http://dx.doi.org/"))
					doi="http://dx.doi.org/"+doi;
				urls.put("DOI",new URL(doi));
				}
			}
		catch (MalformedURLException e)
			{
			e.printStackTrace();
			}

		
		try
			{
			String url=getString(CitationRecord.VAR_URL);
			if(url!=null)
				{
				if(!url.contains("://"))
					url="http://"+url;
				urls.put("URL",new URL(url));
				}
			}
		catch (MalformedURLException e)
			{
			e.printStackTrace();
			}

		
		try
			{
			String pmid=getString(CitationRecord.VAR_PMID);
			String title=getString(VAR_TITLE);
			if(pmid!=null)
				urls.put("Pubmed",new URL("http://www.ncbi.nlm.nih.gov/pubmed/"+pmid));
			else if(title!=null)
				urls.put("Pubmed",new URL("http://www.ncbi.nlm.nih.gov/pubmed/?term="+title));
			}
		catch (MalformedURLException e)
			{
			System.out.println(e);
			e.printStackTrace();
			}
		
		
		try
			{
			String title=getString(VAR_TITLE);
			if(title!=null)
				urls.put("Google Scholar",new URL("http://scholar.google.se/scholar?q="+title));
			}
		catch (MalformedURLException e)
			{
			System.out.println(e);
			e.printStackTrace();
			}
				
		
		
		return urls;
		}
	
	
	public Set<String> getKeys()
		{
		return variables.keySet();
		}
	
	
	public CitationValueArray getArray(String name)
		{
		return (CitationValueArray)variables.get(name);
		}
	
	
	public List<CitationValueHash> getHashes()
		{
		LinkedList<CitationValueHash> hashes=new LinkedList<CitationValueHash>();
		
		CitationValueArray arr=getArray(CitationRecord.VAR_HASH);
		if(arr!=null)
			for(CitationValue ac:arr.list)
				{
				CitationValueHash r=(CitationValueHash)ac;
				hashes.add(r);
				}
		
		return hashes;
		}
	
	
	public void addHash(CitationValueHash hash)
		{
		addArray(CitationRecord.VAR_HASH, hash);
		}
	
	public void addAuthor(CitationValueName val)
		{
		addArray(CitationRecord.VAR_AUTHOR, val);
		}
	
	}
