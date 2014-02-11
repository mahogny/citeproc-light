package org.ecobima.bioidgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class FastaHash extends AbstractSequenceHash implements BioHashAlgorithm
	{
	public boolean computeForFile(File f)
		{
		String name=f.getName().toLowerCase();
		if(name.endsWith(".fa") || name.endsWith(".fasta"))
			{
			
			try
				{
				BufferedReader input = new BufferedReader( new FileReader(f) );
				String line = null;
				while (( line = input.readLine()) != null)
					{
					if(line.startsWith(">"))
						addSequenceName(line.substring(1));
					else
						addSequenceLetters(line);
					}
				input.close();
				return true;
				}
			catch (IOException e)
				{
				e.printStackTrace();
				}
			}
		else
			System.out.println("Not a FASTA file");
		return false;
		}

	
	
	
	
	}
