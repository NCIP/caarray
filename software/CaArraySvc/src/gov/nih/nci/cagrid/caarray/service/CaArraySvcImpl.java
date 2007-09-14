package gov.nih.nci.cagrid.caarray.service;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Primary service side implementation of the remote caArray API.  This implementation
 * delegates to the various remote EJB services.
 *
 * @see ArrayDesignDetailsService
 * @see DataRetrievalService
 * @see FileRetrievalService
 * @see CaArraySearchService
 */
public class CaArraySvcImpl extends CaArraySvcImplBase {

    private static final Log LOG = LogFactory.getLog(CaArraySvcImpl.class);

    final ArrayDesignDetailsService arrayDesignDetailsService;
    final DataRetrievalService dataRetrievalService;
    final FileRetrievalService fileRetrievalService;
    //final CaArraySearchService searchService;

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
            //searchService = (CaArraySearchService) context.lookup(CaArraySearchService.JNDI_NAME);
        } catch (final Exception e) {
            throw new RemoteException("Problems establishing jndi connection to server", e);
        }
    }

    /**
     * Test method to verify that this service is up and running for clients.
     *
     * @param string input string
     * @return the input string
     */
  public java.lang.String echo(final java.lang.String string) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("echo(" + string + ")");
        }
        return string;
      }

    /**
     * Returns complete details of all design elements and relationships for the
     * requested design.
     *
     * @param design get details for this design
     * @return the design details.
     * @see ArrayDesignDetailsService
     */
    public ArrayDesignDetails getDesignDetails(final ArrayDesign arrayDesign) {
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
     * @throws RemoteException on IOException or other remote failure
     * @see FileRetrievalService
     */
    public byte[] readFile(final gov.nih.nci.caarray.domain.file.CaArrayFile caArrayFile) throws RemoteException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("readFile(" + caArrayFile + ")");
        }

        final InputStream is = fileRetrievalService.readFile(caArrayFile);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] bytes = new byte[4096];
        int size = 0;
        try {
            while ((size = is.read(bytes)) != -1) {
                baos.write(bytes, 0, size);
            }
        } catch (final IOException ioe) {
            throw new RemoteException("Unable to read file.", ioe);
        } finally {
            try {
                is.close();
            } catch (final IOException ioe) {
                LOG.warn("IOException closing inputstream.", ioe);
            }
        }
        return baos.toByteArray();
    }

    /**
     * Returns the data associated with the given <code>AbstractArrayData</code> object.
     *
     * @param arrayData retrieve data contents for this set of data.
     * @return the corresponding data values.
     */
    public DataSet getDataSetForDerived(final DerivedArrayData arrayData) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDataSetForDerived(" + arrayData + ")");
        }
        return dataRetrievalService.getDataSet(arrayData);
    }

    /**
     * Returns the data associated with the given <code>AbstractArrayData</code> object.
     *
     * @param arrayData retrieve data contents for this set of data.
     * @return the corresponding data values.
     */
    public DataSet getDataSetForRaw(final RawArrayData arrayData) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDataSetForRow(" + arrayData + ")");
        }
        return dataRetrievalService.getDataSet(arrayData);
    }
}
