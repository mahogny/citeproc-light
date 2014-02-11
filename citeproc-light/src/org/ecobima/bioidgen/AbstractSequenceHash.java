package org.ecobima.bioidgen;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.ecobima.citeproclight.record.CitationValueHash;

/**
 * 
 * Hashing of biological sequence data - DNA, RNA, Protein etc
 * 
 * Call in order: name, letters, [phred], repeat for the next sequence
 * 
 * @author Johan Henriksson
 *
 */
public class AbstractSequenceHash 
	{
	public static final String summarymethod="sequence";
	
	private String hashalgo;
	private MessageDigest digest;
	
	public void init(String hashalgo)
		{		
		this.hashalgo=hashalgo;
		try
			{
			digest = MessageDigest.getInstance(hashalgo);
			}
		catch(Exception e)
			{
			throw new RuntimeException(e);
			}
		}

	
	private void addString(String s)
		{
		try
			{
			byte[] arr=s.getBytes("UTF8");
			digest.update(arr,0,arr.length);
			}
		catch (UnsupportedEncodingException e)
			{
			throw new RuntimeException(e);
			}
		}

	
	public void addSequenceName(String name)
		{
		addString(">");
		addString(name);
		}
	
	/**
	 * Add a part of a sequence.
	 * Should one add constraints on the use of N, * etc?
	 */
	public void addSequenceLetters(String name)
		{
		if(name.contains("\n"))
			name=name.replace("\n", "");
		if(name.contains("\r"))
			name=name.replace("\r", "");
		name=name.toUpperCase();
		addString(name);
		}

	
	/**
	 * Add PHRED score. For sanger, ASCII - 33.
	 * 
	 * Should add phred score after each sequence
	 */
	public void addPhred(byte[] score)
		{
		
		}
	

	
	public CitationValueHash getHash()
		{
		return new CitationValueHash(hashalgo, summarymethod, digest.digest());
		}


	public String getSummaryName()
		{
		return summarymethod;
		}

	}