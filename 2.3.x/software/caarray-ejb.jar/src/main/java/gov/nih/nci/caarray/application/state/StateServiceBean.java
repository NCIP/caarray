//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.state;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.StateDao;
import gov.nih.nci.caarray.domain.state.State;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

/**
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
@Local(StateService.class)
@Stateless
public class StateServiceBean implements StateService {

    private static final Logger LOG = Logger.getLogger(StateServiceBean.class);

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    private StateDao getStateDao() {
        return this.daoFactory.getStateDao();
    }

    /**
     * {@inheritDoc}
     */
    public List<State> getStates() {
        LogUtil.logSubsystemEntry(LOG);
        List<State> result = getStateDao().getStates();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }
}
