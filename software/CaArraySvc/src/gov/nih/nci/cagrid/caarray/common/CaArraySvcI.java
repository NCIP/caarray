package gov.nih.nci.cagrid.caarray.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public interface CaArraySvcI {

  /**
   * The standard caGrid Data Service query method.
   *
   * @param cqlQuery
   *	The CQL query to be executed against the data source.
   * @return The result of executing the CQL query against the data source.
   * @throws QueryProcessingException
   *	Thrown when an error occurs in processing a CQL query
   * @throws MalformedQueryException
   *	Thrown when a query is found to be improperly formed
   */
  public gov.nih.nci.cagrid.cqlresultset.CQLQueryResults query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType ;

  /**
   * Returns complete details of all design elements and relationships for the requested design.
   *
   * @param arrayDesign
   */
  public gov.nih.nci.caarray.domain.array.ArrayDesignDetails getDesignDetails(gov.nih.nci.caarray.domain.array.ArrayDesign arrayDesign) throws RemoteException ;

  /**
   * test method that verifies the service is operational
   *
   * @param string
   */
  public java.lang.String echo(java.lang.String string) throws RemoteException ;

  /**
   * Returns the file contents
   *
   * @param caArrayFile
   */
  public byte[] readFile(gov.nih.nci.caarray.domain.file.CaArrayFile caArrayFile) throws RemoteException ;

  /**
   * Returns the data associated with the given derived array data
   *
   * @param derivedArrayData
   */
  public gov.nih.nci.caarray.domain.data.DataSet getDataSetForDerived(gov.nih.nci.caarray.domain.data.DerivedArrayData derivedArrayData) throws RemoteException ;

  /**
   * Returns the data associated with the given raw array data
   *
   * @param rawArrayData
   */
  public gov.nih.nci.caarray.domain.data.DataSet getDataSetForRaw(gov.nih.nci.caarray.domain.data.RawArrayData rawArrayData) throws RemoteException ;

}

