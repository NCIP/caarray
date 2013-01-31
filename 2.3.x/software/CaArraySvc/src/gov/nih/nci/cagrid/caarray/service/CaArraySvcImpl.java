//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.service;

import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.file.FileRetrievalService;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;

/**
 * Primary service side implementation of the remote caArray API.  This implementation
 * delegates to the various remote EJB services.
 *
 * @see ArrayDesignDetailsService
 * @see DataRetrievalService
 * @see FileRetrievalService
 */
public class CaArraySvcImpl extends CaArraySvcImplBase {

    private static final Log LOG = LogFactory.getLog(CaArraySvcImpl.class);

    final ArrayDesignDetailsService arrayDesignDetailsService;
    final DataRetrievalService dataRetrievalService;
    final FileRetrievalService fileRetrievalService;

    /**
     * Constructs the service implementation.
     *
     * @throws RemoteException if unable to connect to the remote EJBs
     */
    public CaArraySvcImpl() throws RemoteException {
        try {
            final Properties jndiProp = new Properties();
            jndiProp.load(CaArraySvcImpl.class.getResourceAsStream("/gov/nih/nci/cagrid/caarray/jndi.properties"));

            if (jndiProp.getProperty("java.naming.factory.initial") == null
                    || jndiProp.getProperty("java.naming.factory.url.pkgs") == null
                    || jndiProp.getProperty("java.naming.provider.url") == null) {
                throw new IllegalArgumentException("Unable to find all required properties in jndi.properties file.");
            }

            final Context context = new InitialContext(jndiProp);
            arrayDesignDetailsService = (ArrayDesignDetailsService) context.lookup(ArrayDesignDetailsService.JNDI_NAME);
            dataRetrievalService = (DataRetrievalService) context.lookup(DataRetrievalService.JNDI_NAME);
            fileRetrievalService = (FileRetrievalService) context.lookup(FileRetrievalService.JNDI_NAME);
        } catch (final Exception e) {
            throw new RemoteException("Problems establishing jndi connection to server", e);
        }
    }

    /**
     * Returns complete details of all design elements and relationships for the
     * requested design.
     *
     * @param design get details for this design
     * @return the design details.
     * @see ArrayDesignDetailsService
     */
  public gov.nih.nci.caarray.domain.array.ArrayDesignDetails getDesignDetails(gov.nih.nci.caarray.domain.array.ArrayDesign arrayDesign) throws RemoteException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDesignDetails(" + arrayDesign + ")");
        }
        return arrayDesignDetailsService.getDesignDetails(arrayDesign);
    }

    /**
     * Returns the bytes of the requested file's contents.
     *
     * @param file the caArray file to retrieve
     * @return file contents
     * @see FileRetrievalService
     */
  public byte[] readFile(gov.nih.nci.caarray.domain.file.CaArrayFile caArrayFile) throws RemoteException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("readFile(" + caArrayFile + ")");
        }

        return fileRetrievalService.readFile(caArrayFile);
    }

  public gov.nih.nci.caarray.domain.data.DataSet getDataSet(gov.nih.nci.caarray.domain.data.DataRetrievalRequest dataRetrievalRequest) throws RemoteException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDataSetByDataRetrievalRequest(" + dataRetrievalRequest + ")");
        }
        return dataRetrievalService.getDataSet(dataRetrievalRequest);
    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createFileTransfer(gov.nih.nci.caarray.domain.file.CaArrayFile caArrayFile) throws RemoteException {
      if (LOG.isDebugEnabled()) {
          LOG.debug("createFileTransfer(" + caArrayFile + ")");
      }

      // do the easy thing first - grab the bytes and then let transfer service do its thing
      // a smarter implementation would try to use a shared filesystem or some such
      byte[] fileData = fileRetrievalService.readFile(caArrayFile);
      return TransferServiceHelper.createTransferContext(fileData, null);
  }
}

