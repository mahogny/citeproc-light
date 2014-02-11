package org.ecobima.citeproclight.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.ecobima.citeproclight.LabnoteUtil;
import org.ecobima.citeproclight.record.CitationRecord;

/**
 * Connection to the Mendeley Desktop client. 
 * 
 * Calls from here:
 * https://github.com/Mendeley/openoffice-plugin/blob/master/src/MendeleyHttpClient.py
 * 
 * @author Johan Henriksson
 *
 */
public class MendeleyDesktopClient
	{
	private static String host = "http://127.0.0.1:50002";

	/**
	 * Get current user account
	 */
	public static String getUserAccount() throws IOException
		{
		try
			{
			URL url = new URL(host+"/userAccount");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	
			int status = conn.getResponseCode();
			if (status==HttpURLConnection.HTTP_OK)
				{
				InputStream is = conn.getInputStream();
				String s = LabnoteUtil.readStreamToString(is);
				is.close();
				return s;
				}
			else
				return null;
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}
		}

	
	/**
	 * Get information about the client
	 */
	public static String getClientInfo() throws IOException
		{
		try
			{
			URL url = new URL(host+"/mendeleyDesktopInfo");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			int status = conn.getResponseCode();
			if (status==HttpURLConnection.HTTP_OK)
				{
				InputStream is = conn.getInputStream();
				String s = LabnoteUtil.readStreamToString(is);
				is.close();
				return s;
				}
			else
				{
				System.err.println("No info");
				return null;
				}
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}
		}

	
	/**
	 * Open and get citation interactively
	 */
	public static LinkedList<CitationRecord> openCitationChooser() throws IOException
		{
		try
			{
			URL url = new URL(host+"/citation/choose/interactive");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			// "citationStyleUrl": citationStyleUrl,
			// "citationClusters": citationClusters

			int status = conn.getResponseCode();
			if (status==HttpURLConnection.HTTP_OK)
				{
				InputStream is = conn.getInputStream();
				String s = LabnoteUtil.readStreamToString(is);
				is.close();
				return parse(s);
				}
			return null;
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}
		}

	
	/**
	 * Parse JSON citation list
	 */
	public static LinkedList<CitationRecord> parse(String s) throws IOException
		{
		try
			{
			JSONObject fileRoot = (JSONObject) JSONValue.parseWithException(s);
			return MendeleyDesktopClient.parse(fileRoot);
			}
		catch (Throwable e)
			{
			throw new IOException(e);
			}
		}

	
	/**
	 * Parse JSON citation list
	 */
	private static LinkedList<CitationRecord> parse(JSONObject fileRoot) throws IOException
		{
		LinkedList<CitationRecord> out=new LinkedList<CitationRecord>();
		JSONObject clob=(JSONObject)fileRoot.get("citationCluster");
		JSONArray clarr=(JSONArray)clob.get("citationItems");
		for(Object o:clarr)
			{
			JSONObject item=(JSONObject)o;
			JSONObject itemdata=(JSONObject)item.get("itemData");
			out.add(JSONCSLReader.parse(itemdata));
			}
		return out;
		}

	
	public static void openCitation(CitationRecord rec)
		{
		
		//how to get this ID? seems to only exist for our own documents
		
		// "mendeley://library/document/4865903371"
		
//		http://www.mendeley.com/research-papers/search/?query=henriksson+hench
		
		
		
		}
	
	
	}
