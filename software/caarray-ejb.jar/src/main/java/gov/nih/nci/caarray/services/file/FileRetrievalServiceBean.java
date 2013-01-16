//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.file;

import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.EntityConfiguringInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.StorageInterceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Implementation of the remote API file retrieval subsystem.
 */
@Stateless
@Remote(FileRetrievalService.class)
@PermitAll
@Interceptors({AuthorizationInterceptor.class, HibernateSessionInterceptor.class, EntityConfiguringInterceptor.class,
        InjectionInterceptor.class, StorageInterceptor.class })
public class FileRetrievalServiceBean implements FileRetrievalService {
    private static final Logger LOG = Logger.getLogger(FileRetrievalServiceBean.class);
    private static final int CHUNK_SIZE = 4096;

    private SearchDao searchDao;
    private DataStorageFacade dataStorageFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readFile(final CaArrayFile caArrayFileArg) {
        // Look up the fully-populated CaArray object since the one passed in by remote clients will have contents set
        // to null (not serializable).
        final CaArrayFile caArrayFile = this.searchDao.query(caArrayFileArg).get(0);
        InputStream is = null;
        try {
            is = this.dataStorageFacade.openInputStream(caArrayFile.getDataHandle(), false);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] bytes = new byte[CHUNK_SIZE];
            int size = 0;
            while ((size = is.read(bytes)) != -1) {
                baos.write(bytes, 0, size);
            }
            return baos.toByteArray();
        } catch (final IOException e) {
            LOG.error("IOException: " + caArrayFile, e);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * @param dataStorageFacade the dataStorageFacade to set
     */
    @Inject
    public void setDataStorageFacade(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }
}
