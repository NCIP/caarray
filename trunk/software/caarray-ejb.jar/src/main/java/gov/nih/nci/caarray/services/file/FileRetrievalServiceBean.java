//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.file;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.EntityConfiguringInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

/**
 * Implementation of the remote API file retrieval subsystem.
 */
@Stateless
@Remote(FileRetrievalService.class)
@PermitAll
@Interceptors({ AuthorizationInterceptor.class, HibernateSessionInterceptor.class, EntityConfiguringInterceptor.class })
public class FileRetrievalServiceBean implements FileRetrievalService {

    private static final Logger LOG = Logger.getLogger(FileRetrievalServiceBean.class);
    private static final int CHUNK_SIZE = 4096;
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public byte[] readFile(final CaArrayFile caArrayFileArg) {
        // Look up the fully-populated CaArray object since the one passed in by remote clients will have contents set
        // to null (not serializable).
        CaArrayFile caArrayFile = getSearchDao().query(caArrayFileArg).get(0);
        InputStream is = null;
        try {
            File file = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caArrayFile);
            is = new FileInputStream(file.getPath());
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
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException ioe) {
                LOG.warn("IOException closing inputstream.", ioe);
            }
            TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
        }
    }

    private SearchDao getSearchDao() {
        return getDaoFactory().getSearchDao();
    }

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
