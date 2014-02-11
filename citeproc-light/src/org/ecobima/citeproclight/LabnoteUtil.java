package org.ecobima.citeproclight;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


/**
 * Utility functions
 * 
 * @author Johan Henriksson
 *
 */
public class LabnoteUtil
	{
	
	
  /**
   * Copy file from one location to another
   */
	public static void copy(File src, File dst) throws IOException
		{
		FileInputStream is = new FileInputStream(src);
		FileOutputStream os = new FileOutputStream(dst);
		FileChannel inChannel = is.getChannel();
		FileChannel outChannel = os.getChannel();
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inChannel.close();
		outChannel.close();
		is.close();
		os.close();
		if(src.length()!=dst.length())
			throw new IOException("File copy length mismatch");
		}

  /**
   * Read file into memory
   */
	public static byte[] readFileToArray(File src) throws IOException
		{
		FileInputStream is = new FileInputStream(src);
		byte[] arr=readStreamToArray(is);
		is.close();
		return arr;
		}

	/**
	 * Read file into string
	 */
	public static String readFileToString(File src) throws IOException
		{
		InputStream is=new FileInputStream(src);
		String s=readStreamToString(is);
		is.close();
		return s;
		}

	/**
	 * Read stream into an array
	 */
	public static byte[] readStreamToArray(InputStream is) throws IOException
		{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		byte[] buf = new byte[16384];
	  int len;
	  while ((len = is.read(buf)) > 0)
	  	os.write(buf, 0, len);
	  os.close();

	  return os.toByteArray();
		}


  
	/**
	 * Read the XML content from a file
	 */
  public static Document readXML(File filename) throws IOException, JDOMException
	  {
	  SAXBuilder saxBuilder = new SAXBuilder();
	  Document document = saxBuilder.build(filename);
	  return document;
	  }

	public static Document readXML(InputStream is) throws IOException, JDOMException
		{
	  SAXBuilder saxBuilder = new SAXBuilder();
	  Document document = saxBuilder.build(is);
	  return document;
		}

  
  /**
   * Write XML-document to disk
   */
  public static void writeXmlData(Document doc, File file) throws IOException
    {
    FileOutputStream writer=new FileOutputStream(file);
    writeXmlData(doc, writer);
    writer.close();
    }

  /**
   * Write XML-document to stream
   */
  public static void writeXmlData(Document doc, OutputStream os) throws IOException
    {
    Format format=Format.getPrettyFormat();
    XMLOutputter outputter = new XMLOutputter(format);
    outputter.output(doc, os);
    }

  /**
   * Delete directory or file recursively
   */
  public static void deleteRecursive(File f) throws IOException
    {
		if (f.isDirectory())
			for (File c : f.listFiles())
				deleteRecursive(c);
		f.delete();
    }



	/**
	 * E-mail regexp 
	 */
	private static final Pattern rfc2822 = Pattern.compile(
      "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
			);
	
	/**
	 * Check if an email address is valid
	 */
	public static boolean isEmailValid(String email)
		{
		return rfc2822.matcher(email).matches();
		}


	/**
	 * Figure out file extension. Return as .foo or as "" if none
	 */
	public static String getFileExt(File f)
		{
		String ext=f.getName();
		int index=ext.indexOf(".");
		if(index==-1)
			return "";
		else
			return ext.substring(index);
		}


	/**
	 * Put value within <name></name> and return XML element
	 */
	/*
	public static Element elementWithValue(String name, String value)
		{
		Element e=new Element(name);
		e.addContent(value);
		return e;
		}
*/
	/**
	 * Store content of input stream to a file. The stream is closed afterwards
	 */
	public static void streamToFile(InputStream is, File file) throws IOException
		{
		FileOutputStream fos=new FileOutputStream(file);
		streamToStream(is, fos);
		is.close();
		fos.close();
		}

	/**
	 * Transfer the content from one stream to another
	 */
	public static void streamToStream(InputStream is, OutputStream os) throws IOException
		{
		byte[] buf = new byte[16384];
	  int len;
	  while ((len = is.read(buf)) > 0)
	  	os.write(buf, 0, len);
		}

	
	public static void streamToStream(InputStream is, OutputStream os, long tot) throws IOException
		{
		byte[] buf = new byte[16384];
	  int len;
	  while ((len = is.read(buf,0,(int)Math.min(tot,buf.length))) > 0)
	  	{
	  	os.write(buf, 0, len);
	  	tot-=len;
	  	}
		
		}

	

	/**
	 * Write string into file
	 */
	public static void writeStringToFile(File f, String s) throws IOException
		{
		FileWriter w=new FileWriter(f);
		w.write(s);
		w.close();
		}


	
	/**
	 * Read UTF8 string from stream (assuming to read all of the stream)
	 */
	public static String readStreamToString(InputStream is) throws IOException
		{
		InputStreamReader reader=new InputStreamReader(is, "UTF-8");
		StringBuilder sbdis = new StringBuilder();
		char buf[]=new char[16386];
		for(;;)
			{
			int len=reader.read(buf);
			if(len==-1)
				break;
			sbdis.append(buf, 0, len);
			}
		reader.close();
		return sbdis.toString();
		}

	public static boolean equalsNull(Object value, Object value2)
		{
		if(value==null)
			return value2==null;
		else
			return value.equals(value2);
		}


	public static int compareInt(int a, int b)
		{
		if(a<b)
			return -1;
		else if(a>b)
			return 1;
		else
			return 0;
		}

	}
