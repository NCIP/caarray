//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.util.CaArrayHibernateHelperUnsupportedOperationImpl;
import gov.nih.nci.caarray.util.NamingStrategy;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Iterator;

import org.apache.commons.lang.UnhandledException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * Provides a HibernateHelper which uses only a single, given connection to the database.
 * Implements only those functions needed to locate, load and save ArrayDesigns.
 * 
 * @author jscott
 *
 */
class SingleConnectionHibernateHelper extends CaArrayHibernateHelperUnsupportedOperationImpl {

    private AnnotationConfiguration configuration;
    private SessionFactory sessionFactory;

    /**
     * @param connection
     */
    public void initialize(Connection connection) {
        HibernateSingleConnectionProvider.setConnection(connection);

        InputStream configurationStream
        = FixIlluminaGenotypingCsvDesignProbeNamesMigrator.class.getResourceAsStream("/hibernate.cfg.xml");
        SAXReader reader = new SAXReader();
        reader.setEntityResolver(new org.hibernate.util.DTDEntityResolver());
        Document configurationDocument = null;
        try {
            configurationDocument = reader.read(configurationStream);
        } catch (DocumentException e) {
            throw new UnhandledException(e);
        }
        Node sessionFactoryNode = configurationDocument
            .selectSingleNode("/hibernate-configuration/session-factory");

        Iterator<?> iter = ((Branch) sessionFactoryNode).nodeIterator();
        while (iter.hasNext()) {
            Node currentNode = (Node) iter.next();
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && !currentNode.getName().equals("mapping")) {
                iter.remove();
            }
        }

        DOMWriter domWriter = new DOMWriter();
        org.w3c.dom.Document document = null;
        try {
            document = domWriter.write(configurationDocument);
        } catch (DocumentException e) {
            throw new UnhandledException(e);
        }


        configuration = new AnnotationConfiguration();
        configuration.setProperty(Environment.CONNECTION_PROVIDER
                , "gov.nih.nci.caarray.upgrade.HibernateSingleConnectionProvider");

        configuration.configure(document);

        configuration.setProperty(Environment.TRANSACTION_STRATEGY
                , "org.hibernate.transaction.JDBCTransactionFactory");
        configuration.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        configuration.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
        configuration.getProperties().remove(Environment.TRANSACTION_MANAGER_STRATEGY); 
        configuration.setNamingStrategy(new NamingStrategy());

        sessionFactory = configuration.buildSessionFactory();
    }

    @Override
    public Transaction beginTransaction() {
        return sessionFactory.getCurrentSession().beginTransaction();
    }

    @Override
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override   
    public Configuration getConfiguration() {
        return configuration;
    }

}
