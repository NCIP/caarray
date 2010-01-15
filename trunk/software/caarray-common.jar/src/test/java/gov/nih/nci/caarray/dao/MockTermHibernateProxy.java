package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class MockTermHibernateProxy extends Term implements HibernateProxy {

    private static final long serialVersionUID = 1L;
    public Object implementation = new Object();

    public MockTermHibernateProxy(Term impl) {
        this.implementation = impl;
        copyValues(impl);

    }

    public final void copyValues(Term impl) {
        this.implementation = impl;
        super.setValue(impl.getValue());
        super.setSource(impl.getSource());
        super.setAccession(impl.getAccession());
    }

    public LazyInitializer getHibernateLazyInitializer() {
        return new LazyInitializer() {

            public String getEntityName() {
                return implementation.getClass().getName();
            }

            public Serializable getIdentifier() {
                return null;
            }

            public Object getImplementation() {
                return implementation;
            }

            public Object getImplementation(SessionImplementor s)
                    throws HibernateException {
                return null;
            }

            public Class getPersistentClass() {
                return null;
            }

            public SessionImplementor getSession() {
                return null;
            }

            public void initialize() throws HibernateException {

            }

            public boolean isUninitialized() {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean isUnwrap() {
                // TODO Auto-generated method stub
                return false;
            }

            public void setIdentifier(Serializable id) {
                // TODO Auto-generated method stub

            }

            public void setImplementation(Object target) {
                // TODO Auto-generated method stub

            }

            public void setSession(SessionImplementor s)
                    throws HibernateException {
                // TODO Auto-generated method stub

            }

            public void setUnwrap(boolean unwrap) {
                // TODO Auto-generated method stub

            }

        };
    }

    public Object writeReplace() {
        // TODO Auto-generated method stub
        return null;
    }
}
