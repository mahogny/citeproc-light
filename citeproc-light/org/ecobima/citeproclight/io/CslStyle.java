package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.labnote.client.LabnoteUtil;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

/**
 * One CSL style
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class CslStyle
	{
	private Namespace ns=Namespace.getNamespace("http://purl.org/net/xbiblio/csl");
	private HashMap<String, Element> mapMacros=new HashMap<String, Element>();
	private Element eLayoutIntext;
	private Element eLayoutBibliography;
	
	private CitationValueName curName;
	private CitationValueDate curDate;

	
	public void parse(InputStream is) throws IOException
		{
		try
			{
			Document doc=LabnoteUtil.readXML(is);

			Element root=doc.getRootElement();
			
			for(Element e:root.getChildren())
				{
				if(e.getName().equals("macro"))
					{
					mapMacros.put(e.getAttributeValue("name"), e);
					}
				else if(e.getName().equals("citation"))
					{
					eLayoutIntext=e.getChild("layout",ns);
					}
				else if(e.getName().equals("bibliography"))
					{
					eLayoutBibliography=e.getChild("layout",ns);
					}
				
				
				}
			
			if(eLayoutIntext==null)
				throw new IOException("no layout");
			}
		catch (JDOMException e)
			{
			throw new IOException(e);
			}
		}
	

	// http://citationstyles.org/documentation/
	
	

	
	/**
	 * MACRO
	 */
	private void buildMacro(StringBuilder sb, CitationRecord rec, Element root)
		{
		String macroName=root.getAttributeValue("macro");
		Element eMacro=mapMacros.get(macroName);
		buildGroup(sb, rec, eMacro);
		}

	
	/**
	 * Get attribute value (handle null)
	 */
	private static String getAttributeValue(Element r, String value)
		{
		Attribute a=r.getAttribute(value);
		if(a==null)
			return null;
		else
			return a.getValue();
		}
	private static String getAttributeValue(Element r, String value, String def)
		{
		String v=getAttributeValue(r, value);
		if(v==null)
			return def;
		else
			return v;
		}

	
	
	/**
	 * Build the start layout
	 */
	private void buildStart(StringBuilder sb, CitationRecord rec, Element root)
		{
		String prefix=getAttributeValue(root, "prefix");
		if(prefix!=null)
			appendWithSpace(sb, prefix);
		}
	/**
	 * Build the end layout
	 */
	private void buildEnd(StringBuilder sb, CitationRecord rec, Element root)
		{
		String suffix=getAttributeValue(root, "suffix");
		if(suffix!=null)
			sb.append(suffix);
		}

	
	/**
	 * Build group-like element
	 * @return 
	 */
	private boolean buildGroup(StringBuilder sb, CitationRecord rec, Element root)
		{
		buildStart(sb, rec, root);
		String delim=getAttributeValue(root, "delimiter","");
		
		boolean produced=false;
		boolean needDelim=false;
		for(Element e:root.getChildren())
			{
			if(needDelim)
				{
				sb.append(delim);
				needDelim=false;
				}
			
			if(e.getName().equals("group"))
				needDelim=buildGroup(sb, rec, e);
			else if(e.getName().equals("text"))
				needDelim=buildText(sb, rec, e);
			else if(e.getName().equals("names"))
				needDelim=buildNames(sb, rec, e);
			else if(e.getName().equals("name"))
				needDelim=buildName(sb,rec,e);
			else if(e.getName().equals("label"))
				needDelim=buildLabel(sb, rec, e);
			else if(e.getName().equals("date"))
				needDelim=buildDate(sb, rec, e);
			else if(e.getName().equals("date-part"))
				needDelim=buildDatePart(sb, rec, e);
			else if(e.getName().equals("choose"))
				needDelim=buildChoose(sb, rec, e);
			else
				System.err.println("unhandled: "+e.getName());
			produced|=needDelim;
			}
		
		buildEnd(sb, rec, root);
		return produced;
		}

	
	/**
	 * NAME
	 */
	private boolean buildName(StringBuilder sb, CitationRecord rec, Element e)
		{
		String form=getAttributeValue(e, "form", "");

		
		//Much to do. May contain name-part
		
		if(form.equals("short"))
			appendWithSpace(sb, curName.surname);
		else
			appendWithSpace(sb, curName.toString());
		return true;
		}


	/**
	 * TEXT
	 */
	private boolean buildText(StringBuilder sb, CitationRecord rec, Element e)
		{
		boolean produced=false;
		//TODO text-case="title"
		
		String macroName=getAttributeValue(e, "macro");
		String variableName=getAttributeValue(e, "variable");
		if(macroName!=null)
			{
			buildMacro(sb, rec, e);
			produced=true;
			}
		else if(variableName!=null)
			{
			String value=rec.getString(variableName);
			if(value==null)
				{
				System.err.println("warning: missing var "+variableName);
				//appendWithSpace(sb, "NULL");
				}
			else
				{
				appendWithSpace(sb, value);
				produced=true;
				}
			}
		return produced;
		}

	private static void appendWithSpace(StringBuilder sb, String s)
		{
		//Evil!
		String foo=sb.toString();
		if(!foo.isEmpty() && "( ".indexOf(foo.charAt(foo.length()-1))==-1)
			sb.append(' ');
		sb.append(s);
		}

	/**
	 * DATE
	 * http://citationstyles.org/downloads/specification.html#date
	 * @return 
	 */
	private boolean buildDate(StringBuilder sb, CitationRecord rec, Element root)
		{
		buildStart(sb, rec, root);
		String variableName=getAttributeValue(root, "variable");

		curDate=rec.getDate(variableName);
			
		buildGroup(sb, rec, root); //Need one "group" without the ()
		
		buildEnd(sb, rec, root);
		return true;
		}

	
	
	
	/**
	 * Any kind of date
	 * @return 
	 */
	private boolean buildDatePart(StringBuilder sb, CitationRecord rec, Element root)
		{
		if(curDate==null)
			appendWithSpace(sb, "NO DATE");
		else
			{
			String name=getAttributeValue(root, "name");

			if(name.equals("year"))
				{
				appendWithSpace(sb, curDate.getYear());
				}
			else
				{
				appendWithSpace(sb, "UNHANDLED DATEPART "+name);
				}
			}
		return true;
		}


	
	/**
	 * CHOOSE with IF, ELSE-IF and ELSE
	 * @return 
	 */
	private boolean buildChoose(StringBuilder sb, CitationRecord rec, Element root)
		{
		for(Element e:root.getChildren())
			{
			if(e.getName().equals("if") || e.getName().equals("else-if"))
				{
				///IF based on type of document
				String type=getAttributeValue(e, "type");
				if(type!=null)
					{
					StringTokenizer stok=new StringTokenizer(type," ");
					HashSet<String> set=new HashSet<String>();
					while(stok.hasMoreElements())
						set.add(stok.nextToken());
					if(set.contains(rec.getType()))
						{
						buildGroup(sb, rec, e);
						break;
						}
					}
				
				}
			else if(e.getName().equals("else"))
				{
				System.err.println("warn: else");
				buildGroup(sb, rec, e);
				}
			}
		
		return true;
		}

	
	
	
	/**
	 * LABEL
	 */
	private boolean buildLabel(StringBuilder sb, CitationRecord rec, Element root)
		{
		boolean produced=false;
		
		String variableName=getAttributeValue(root, "variable");
		if(variableName!=null)
			{
			buildStart(sb, rec, root);
			appendWithSpace(sb, rec.getVariable(variableName).toString());
			//LABEL: may also carry affixes, formatting, text-case and strip-periods attributes.
			buildEnd(sb, rec, root);
			produced=true;
			}
		return produced;
		}
	
	/**
	 * NAMES
	 * @return 
	 */
	private boolean buildNames(StringBuilder sb, CitationRecord rec, Element root)
		{
		buildStart(sb, rec, root);
		String variableName=getAttributeValue(root, "variable");
		String delim=getAttributeValue(root, "delim", ", ");
		
		boolean first=true;
		Collection<CitationValueName> listnames=rec.getNames(variableName);
		for(CitationValueName name:listnames)
			{
			if(!first)
				sb.append(delim);
			curName=name;
			
			
//			cs:names has four child elements (discussed below): cs:name,	cs:et-al, cs:substitute and cs:label. 
			
			buildGroup(sb, rec, root);
			first=false;
			}
		buildEnd(sb, rec, root);
		return true;
		}
	
	
	
	
	
	

	
	/**
	 * Format one citation for in-text display
	 */
	public String formatIntext(CitationRecord rec)
		{
		StringBuilder sb=new StringBuilder();
		
		buildGroup(sb, rec, eLayoutIntext);

		return sb.toString();
		}
	
	
	/**
	 * Format one citation for bibliography
	 */
	public String formatBibl(CitationRecord rec)
		{
		StringBuilder sb=new StringBuilder();
		
		buildGroup(sb, rec, eLayoutBibliography);

		return sb.toString();
		}
	
	
	
	
	}
