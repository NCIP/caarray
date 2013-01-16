//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public interface CaArraySvc_v1_0I {

  /**
   * Search for experiments matching given criteria
   *
   * @param criteria
   * @param limitOffset
   * @throws UnsupportedCategoryFault
   *	
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault ;

  /**
   * return list of all persons who are principal investigators for some experiment
   *
   */
  public gov.nih.nci.caarray.external.v1_0.experiment.Person[] getAllPrincipalInvestigators() throws RemoteException ;

  /**
   * Begin an enumeration of experiments matching given criteria
   *
   * @param experimentSearchCriteria
   */
  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria experimentSearchCriteria) throws RemoteException ;

  /**
   * Returns a grid transfer reference for retrieving the given reference
   *
   * @param fileRef
   * @param compress
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   * @throws DataStagingFault
   *	
   */
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference fileRef,boolean compress) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault ;

  /**
   * Retrieves a parsed data set
   *
   * @param dataSetRequest
   *	a DataSetRequest identifying the parsed data to be retrieved. The request must  specify at least one hybridization or one file, and at least one quantitation type
   * @return the data set
   * @throws InconsistentDataSetsFault
   *	if the data sets for the hybridizations and/or files in the request are not consistent, e.g. do not correspond to the same design element list.
   * @throws NoEntityMatchingReferenceFault
   *	if any of the hybridization, file or quantitation references in the dataSetRequest are not valid
   * @throws IncorrectEntityTypeFault
   *	if any of the hybridization, file or quantitation references in the dataSetRequest are not valid
   */
  public gov.nih.nci.caarray.external.v1_0.data.DataSet getDataSet(gov.nih.nci.caarray.external.v1_0.query.DataSetRequest dataSetRequest) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InconsistentDataSetsFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault ;

  /**
   * search for biomaterials matching criteria
   *
   * @param criteria
   * @param limitOffset
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   * @throws UnsupportedCategoryFault
   *	
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault ;

  /**
   * search for hybridizations matching criteria
   *
   * @param criteria
   * @param limitOffset
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault ;

  /**
   * Searches for experiments using a keyword search across fields
   *
   * @param criteria
   * @param limitOffset
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException ;

  /**
   * Search for files matching given criteria
   *
   * @param criteria
   * @param limitOffset
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault ;

  /**
   * Search for biomaterials matching given keyword
   *
   * @param criteria
   * @param limitOffset
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException ;

  /**
   * Returns an export of an experiment as a set of mage-tab IDF and SDRF, along with metadata for associated data files
   *
   * @param experimentRef
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   * @throws DataStagingFault
   *	
   */
  public gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet getMageTabExport(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault ;

  /**
   * Search for quantitation types matching criteria
   *
   * @param criteria
   * @throws IncorrectEntityTypeFault
   *	
   * @throws NoEntityMatchingReferenceFault
   *	
   */
  public gov.nih.nci.caarray.external.v1_0.data.QuantitationType[] searchForQuantitationTypes(gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria criteria) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault ;

  /**
   * Search using an entity as example
   *
   * @param criteria
   * @param limitOffset
   */
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException ;

  public gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet getAnnotationSet(gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest request) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault ;

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Term[] getTermsForCategory(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference categoryRef,java.lang.String valuePrefix) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault ;

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Category[] getAllCharacteristicCategories(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria) throws RemoteException ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria) throws RemoteException ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria) throws RemoteException ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria) throws RemoteException ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria) throws RemoteException ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria) throws RemoteException ;

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException ;

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException ;

}

