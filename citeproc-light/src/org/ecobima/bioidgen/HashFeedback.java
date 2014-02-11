package org.ecobima.bioidgen;

public interface HashFeedback
{
public boolean shouldCancel();
public void progress(double s);
}