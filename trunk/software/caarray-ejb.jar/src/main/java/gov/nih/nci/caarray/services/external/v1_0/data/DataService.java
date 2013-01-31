//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.FileStreamableContents;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import javax.ejb.Remote;

/**
 * Remote service for retrieving file and parsed data. Used by the grid service, and can also be used directly by 
 * EJB clients.
 * 
 * @author dkokotov
 */
@Remote
public interface DataService {
    /**
     * The JNDI name to look up this Remote EJB under.
     */
    String JNDI_NAME = "caarray/external/v1_0/DataServiceBean";
        
    /**
     * Retrieves the parsed data set identified by the given request. The DataSet will contain a HybridizationData for
     * each Hybridization referenced by the request, and/or for each Hybridization linked to a data File referenced by
     * the request. Each HybridizationData will contain one DataColumn for each QuantitationType referenced by the
     * request.
     * 
     * @param dataSetRequest a DataSetRequest instance identifying the parsed data to be retrieved. The request must
     *            specify at least one hybridization or one file, and at least one quantitation type.
     * @throws InvalidReferenceException if any of the hybridization, file or quantitation references in the
     *             dataSetRequest are not valid
     * @throws InvalidInputException if no quantitation type is specified in the request, or if at least one file OR one
     *             hybridization is not specified in the request.
     * @throws InconsistentDataSetsException if the data sets for the hybridizations and/or files in the request are not
     *             consistent, e.g. do not correspond to the same design element list.
     * @return the DataSet with the requested parsed data, as specified above.
     */
    DataSet getDataSet(DataSetRequest dataSetRequest) throws InvalidReferenceException, InvalidInputException,
            InconsistentDataSetsException;

    /**
     * Returns a RemoteInputStream through which the client can retrieve the data for the file identified by the given
     * reference. The client must take care to ensure the RemoteInputStream is closed when the contents is read, even
     * when exceptions occur.
     * 
     * @param fileRef the reference identifying the file to retrieve.
     * @param compressed if true, then the RemoteInputStream will return the contents of the file compressed using Gzip
     * @return the remote input stream (using the rmiio library) from which the file contents can be read.
     * @throws InvalidReferenceException if the fileRef is not a valid file reference.
     * @throws DataTransferException if there is an error streaming the data.
     */
    FileStreamableContents streamFileContents(CaArrayEntityReference fileRef, boolean compressed)
            throws InvalidReferenceException, DataTransferException;

    /**
     * Retrieves a set of files containing the MAGE-TAB IDF and SDRF for the experiment identified by the given
     * reference. The IDF and SDRF are generated dynamically. The file set also contains references to the data files
     * referenced by the SDRF.
     * 
     * @param experimentRef reference identifying the experiment
     * @return a MageTabFileSet consisting of the generated IDF and SDRF for this experiment, and references to the 
     * data files linked to hybridizations in the experiment
     * @throws InvalidReferenceException if the experimentRef is not a valid experiment reference
     * @throws DataTransferException if there is an error generating the mage-tab file data
     */    
    MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException;
}
