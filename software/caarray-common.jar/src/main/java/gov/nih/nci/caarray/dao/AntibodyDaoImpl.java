package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UnfilteredCallback;
import gov.nih.nci.carpla.domain.Antibody;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

public class AntibodyDaoImpl extends AbstractCaArrayDaoImpl implements AntibodyDao {

	public Antibody getAntibody ( long id) {
		 return (Antibody) getCurrentSession().get(Antibody.class, id);
	}

	@Override
	Logger getLog () {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Antibody> getAntibodies () {
		// TODO Auto-generated method stub
		return null;
	}
	
//	   private static final Logger LOG = Logger.getLogger(ArrayDaoImpl.class);
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public ArrayDesign getArrayDesign(long id) {
//	        return (ArrayDesign) getCurrentSession().get(ArrayDesign.class, id);
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    @SuppressWarnings("unchecked")
//	    public List<ArrayDesign> getArrayDesigns() {
//	        return getCurrentSession().createCriteria(ArrayDesign.class).list();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    @SuppressWarnings("unchecked")
//	    public List<Organization> getArrayDesignProviders() {
//	        String query = "select distinct ad.provider from " + ArrayDesign.class.getName() + " ad "
//	                + " where ad.provider is not null order by ad.provider.name asc";
//	        return getCurrentSession().createQuery(query).list();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    @SuppressWarnings("unchecked")
//	    public List<ArrayDesign> getArrayDesignsForProvider(Organization provider, boolean importedOnly) {
//	        StringBuilder queryStr = new StringBuilder("from ").append(ArrayDesign.class.getName()).append(
//	                " ad where ad.provider = :provider ");
//	        if (importedOnly) {
//	            queryStr.append(" and (ad.designFile.status = :status1 or ad.designFile.status = :status2) ");
//	        }
//	        queryStr.append("order by name asc");
//	        Query query = getCurrentSession().createQuery(queryStr.toString());
//	        query.setEntity("provider", provider);
//	        if (importedOnly) {
//	            query.setString("status1", FileStatus.IMPORTED.name());
//	            query.setString("status2", FileStatus.IMPORTED_NOT_PARSED.name());
//	        }
//	        return query.list();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    @SuppressWarnings("unchecked")
//	    public List<ArrayDesign> getArrayDesigns(Organization provider, AssayType assayType, boolean importedOnly) {
//	        StringBuilder queryStr = new StringBuilder("from ").append(ArrayDesign.class.getName()).append(
//	                " ad where ad.provider = :provider and ad.assayType = :assayType ");
//	        if (importedOnly) {
//	            queryStr.append(" and (ad.designFile.status = :status1 or ad.designFile.status = :status2) ");
//	        }
//	        queryStr.append("order by name asc");
//	        Query query = getCurrentSession().createQuery(queryStr.toString());
//	        query.setEntity("provider", provider);
//	        query.setString("assayType", assayType.getValue());
//	        if (importedOnly) {
//	            query.setString("status1", FileStatus.IMPORTED.name());
//	            query.setString("status2", FileStatus.IMPORTED_NOT_PARSED.name());
//	        }
//	        return query.list();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public RawArrayData getRawArrayData(CaArrayFile file) {
//	        Session session = HibernateUtil.getCurrentSession();
//	        session.flush();
//	        Query query = session.createQuery("from " + RawArrayData.class.getName()
//	                + " arrayData where arrayData.dataFile = :file");
//	        query.setEntity("file", file);
//	        return (RawArrayData) query.uniqueResult();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public DerivedArrayData getDerivedArrayData(CaArrayFile file) {
//	        Session session = HibernateUtil.getCurrentSession();
//	        Query query = session.createQuery("from " + DerivedArrayData.class.getName()
//	                + " arrayData where arrayData.dataFile = :file");
//	        query.setEntity("file", file);
//	        return (DerivedArrayData) query.uniqueResult();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public AbstractArrayData getArrayData(long id) {
//	        Query q =
//	                HibernateUtil.getCurrentSession().createQuery(
//	                        "from " + AbstractArrayData.class.getName() + " where id = :id");
//	        q.setLong("id", id);
//	        return (AbstractArrayData) q.uniqueResult();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public Hybridization getHybridization(Long id) {
//	        Query q =
//	                HibernateUtil.getCurrentSession().createQuery(
//	                        "from " + Hybridization.class.getName() + " where id = :id");
//	        q.setLong("id", id);
//	        return (Hybridization) q.uniqueResult();
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
//	        if (descriptor == null) {
//	            return null;
//	        }
//	        ArrayDataType example = new ArrayDataType();
//	        example.setName(descriptor.getName());
//	        example.setVersion(descriptor.getVersion());
//	        List<ArrayDataType> matches = queryEntityByExample(example);
//	        if (matches.isEmpty()) {
//	            return null;
//	        } else if (matches.size() == 1) {
//	            return matches.get(0);
//	        } else {
//	            throw new IllegalStateException("Duplicate registration of ArrayDataType " + descriptor);
//	        }
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
//	        if (descriptor == null) {
//	            return null;
//	        }
//	        QuantitationType example = new QuantitationType();
//	        example.setName(descriptor.getName());
//	        List<QuantitationType> matches = queryEntityByExample(example);
//	        if (matches.isEmpty()) {
//	            return null;
//	        } else if (matches.size() == 1) {
//	            return matches.get(0);
//	        } else {
//	            throw new IllegalStateException("Duplicate registration of ArrayDataType " + descriptor);
//	        }
//	    }
//
//	    @Override
//	    Logger getLog() {
//	        return LOG;
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
//	        return (DesignElementList) getEntityByLsid(DesignElementList.class, lsidAuthority, lsidNamespace, lsidObjectId);
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
//	        return (ArrayDesign) getEntityByLsid(ArrayDesign.class, lsidAuthority, lsidNamespace, lsidObjectId);
//	    }
//
//	    /**
//	     * {@inheritDoc}
//	     */
//	    public boolean isArrayDesignLocked(final Long id) {
//	        UnfilteredCallback u = new UnfilteredCallback() {
//	            public Object doUnfiltered(Session s) {
//	                BrowseCategory cat = BrowseCategory.ARRAY_DESIGNS;
//	                StringBuffer sb = new StringBuffer();
//	                sb.append("SELECT COUNT(DISTINCT p) FROM ")
//	                  .append(Project.class.getName()).append(" p JOIN ").append(cat.getJoin())
//	                  .append(" WHERE ").append(cat.getField()).append(".id = :id");
//	                Query q = s.createQuery(sb.toString());
//	                q.setParameter("id", id);
//	                return q.uniqueResult();
//	            }
//	        };
//	        Number count = (Number) HibernateUtil.doUnfiltered(u);
//	        return count.intValue() > 0;
//	    }
	

}
