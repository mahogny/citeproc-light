package org.ecobima.bioidgen;

import java.io.File;
import java.io.IOException;

import org.ecobima.citeproclight.record.CitationValueHash;

public interface BioHashAlgorithm
	{
	public void init(String hashalgo) throws IOException;
	public CitationValueHash getHash();
	
	public boolean computeForFile(File f, HashFeedback feedback);
	public String getSummaryName();
	}
