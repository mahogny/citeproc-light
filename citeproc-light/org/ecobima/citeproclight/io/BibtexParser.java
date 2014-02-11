package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import org.ecobima.citeproclight.record.CitationRecord;
import org.ecobima.citeproclight.record.CitationValueDate;
import org.ecobima.citeproclight.record.CitationValueName;
import org.ecobima.citeproclight.record.CitationValueString;
import org.ecobima.labnote.client.LabnoteUtil;

/**
 * Parser for BibTeX entries
 * 
 * @author Johan Henriksson
 *
 */
public class BibtexParser
	{
	
	

	private static class Parser
		{
		int curpos=0;
		String s;
		
		public boolean hasMore()
			{
			return curpos<s.length();
			}
		
		public char c()
			{
			return s.charAt(curpos);
			}

		public char next()
			{
			char c=c();
			curpos++;
			return c;
			}

		public void skipWhitespace()
			{
			while(hasMore() && Character.isWhitespace(c()))
				next();
			}
		
		}

	
	public static LinkedList<CitationRecord> parse(InputStream is) throws IOException
		{
		HashMap<String, String> strings=new HashMap<String, String>();

		Parser p=new Parser();
		p.s=LabnoteUtil.readStreamToString(is);

		LinkedList<CitationRecord> listrecs=new LinkedList<CitationRecord>();
		
		while(p.hasMore())
			{
			if(Character.isWhitespace(p.c()))
				p.next(); //Ignore
			else if(p.c()=='@')
				{
				p.next();

				StringBuilder sbName=new StringBuilder();
				while(p.c()!='{')
					{
					sbName.append(p.c());
					p.next();
					}
				p.next();
				String sectionType=sbName.toString().trim();
				

				HashMap<String, String> mapKeyValue=new HashMap<String, String>();
				//String sectionName=null;
				
				sectionloop: for(;;)
					{
					p.skipWhitespace();
					if(p.c()=='}')
						{
						p.next();
						break sectionloop;
						}
					else
						{
						//Variable name
						StringBuilder sbfoo=new StringBuilder();
						while(", \r\n\t=".indexOf(p.c())==-1)
							sbfoo.append(p.next());
						String variable=sbfoo.toString().toLowerCase();
						String value=null;
						p.skipWhitespace();

						if(p.c()==',')
							{
							//sectionName=variable;
							p.next();
							}
						else
							{
							p.next(); //Skip =
							p.skipWhitespace();
							////Some kind of value!
							
							
							StringBuilder sbvalue=new StringBuilder();
							while(p.c()!=',' && p.c()!='}')
								{
								
								if(p.c()=='\"')
									{
									p.next();
									while(p.c()!='\"')
										sbvalue.append(p.next());
									p.next();
									}
								else if(p.c()=='#')
									{
									//Combining symbol. Semi-properly handled
									p.next();
									}
								else
									{
									//Get symbol
									StringBuilder sbthis=new StringBuilder();
									if(!Character.isWhitespace(p.c()))
										sbthis.append(p.c());
									p.next();
									
									//Check if it is a string substitution
									String subst=sbthis.toString();
									if(subst!=null)
										sbvalue.append(subst);
									else
										sbvalue.append(sbthis.toString());
									}
								}
							value=sbvalue.toString().trim();
							if(p.c()==',')
								p.next();
							mapKeyValue.put(variable, value);
							}
						}

					}
				
				
				if(sectionType.equals("string"))
					{
					//////////////////// Substitution //////////////////////
					String from=mapKeyValue.keySet().iterator().next();
					String to=mapKeyValue.get(from);
					strings.put(from,to);
					}
				else if(!sectionType.equals("comment") && !sectionType.equals("preamble"))
					{
					//////////////////// Map bibtex record to standard format //////////////////////
					CitationRecord rec=new CitationRecord();
					listrecs.add(rec);

					
					if(sectionType.equals("article"))
						rec.setType(CitationRecord.TYPE_ARTICLE);
					else if(sectionType.equals("book"))
						rec.setType(CitationRecord.TYPE_BOOK);
					
					

					putMaybe(rec, mapKeyValue, "title", CitationRecord.VAR_TITLE);
					putMaybe(rec, mapKeyValue, "publisher", CitationRecord.VAR_PUBLISHER);
					putMaybe(rec, mapKeyValue, "address", CitationRecord.VAR_PUBLISHER);

					putMaybe(rec, mapKeyValue, "journal", CitationRecord.VAR_CONTAINERTITLE);

					putMaybe(rec, mapKeyValue, "volume", CitationRecord.VAR_VOLUME);
					putMaybe(rec, mapKeyValue, "number", CitationRecord.VAR_ISSUE);
					putMaybe(rec, mapKeyValue, "pages", CitationRecord.VAR_PAGES);
					putMaybe(rec, mapKeyValue, "url", CitationRecord.VAR_URL);
					putMaybe(rec, mapKeyValue, "issn", CitationRecord.VAR_ISSN);
					putMaybe(rec, mapKeyValue, "isbn", CitationRecord.VAR_ISBN);

					//DOI
					String doi=mapKeyValue.get("doi");
					if(doi!=null)
						{
						if(doi.startsWith("http://dx.doi.org/"))
							doi=doi.substring("http://dx.doi.org/".length());
						rec.put(CitationRecord.VAR_DOI, new CitationValueString(doi));
						}
					
					//Author
					String author=mapKeyValue.get("author");
					if(author!=null)
						{
						//and-separation works for sciencedirect. later likely also need ,-separation
						String[] toks=author.split(" and ");
						for(String onea:toks)
							{
							CitationValueName vname=new CitationValueName();
							
							if(onea.indexOf(' ')!=-1)
								{
								int ind=onea.indexOf(' ');
								vname.forename=onea.substring(0, ind).trim();
								vname.surname=onea.substring(ind).trim();
								}
							else
								//TODO: what about inverse order of names?
								vname.forename=onea;

							
							rec.addArray(CitationRecord.VAR_AUTHOR, vname);
							}
						
						}

					
					String syear=mapKeyValue.get("year");
					if(syear!=null)
						{
						CitationValueDate d=new CitationValueDate();
						d.year=Integer.parseInt(syear);
						rec.put(CitationRecord.VAR_ISSUED, d);
						}
						
					}
				}
			
			
			}
		
		
		
		
		
		
		
		
		return listrecs;
		}

	
	private static void putMaybe(CitationRecord rec, HashMap<String, String> map, String from, String to)
		{
		String val=map.get(from);
		if(val!=null)
			rec.put(to, new CitationValueString(val));
		}
	
	}
