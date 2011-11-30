package gov.nih.nci.logging.webapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import gov.nih.nci.logging.api.applicationservice.Query;
import gov.nih.nci.logging.api.applicationservice.QueryImpl;
import gov.nih.nci.logging.api.applicationservice.exception.QuerySpecificationException;
import gov.nih.nci.logging.webapp.viewobjects.ServerObject;

public class SystemManager
{

	private static Collection serverNameCollection = null;
	private static SortedMap logLevelCollection = null;

	/**
	 * @return
	 * @throws Exception 
	 */
	public static Collection getServerNameCollection() throws Exception
	{
		
			Collection serverNameCollection =	serverNameCollection= new ArrayList();
			serverNameCollection.add(new ServerObject("ALL"));
			try
			{
				Query query = new QueryImpl();
				Collection col = query.retrieveServer();
				Iterator iter = col.iterator();
				while(iter.hasNext()){
					serverNameCollection.add(new ServerObject((String)iter.next()));
				}
			}
			catch (QuerySpecificationException e) { 
				throw new Exception("Unable to retrieve list of Servers.");
			}
		
		return serverNameCollection;
	}
	

	/**
	 * @return
	 */
	public static SortedMap getLogLevelMap()
	{
		if (logLevelCollection == null)
		{
			logLevelCollection = new TreeMap();
			logLevelCollection.put("ALL","All Levels");
			logLevelCollection.put("DEBUG","DEBUG");
			logLevelCollection.put("INFO","INFO");
			logLevelCollection.put("WARN","WARN");
			logLevelCollection.put("ERROR","ERROR");
			logLevelCollection.put("FATAL","FATAL");
			
		}	
		return  logLevelCollection;
	}

	
}
