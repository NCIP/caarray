package com.fiveamsolutions.caarrayclient;

import java.util.List;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.application.CaArraySearchService;
import gov.nih.nci.caarray.query.*;

public class CaArrayRemoteClient {
    public static void main(String[] args) {
        Context context;
        Category exampleCategory = new Category();
        exampleCategory.setName("DummyTestCategory4");
        //String hqlString = "FROM Category c WHERE c.name LIKE 'DummyTest%'";
        String hqlString = "FROM Protocol p WHERE p.type.value LIKE 'feature_extraction'";
        try {
            context = new InitialContext();
            CaArraySearchService searchBean = (CaArraySearchService) context.lookup(CaArraySearchService.JNDI_NAME);
            System.out.println("SEARCH BY EXAMPLE...");
            List returnedList = searchBean.search(exampleCategory);
            //List returnedList = searchBean.search((Category)null);
            if (returnedList != null) {
                Iterator i = returnedList.iterator();
                while (i.hasNext()) {
                    Category c = (Category) i.next();
                    System.out.println("Retrieved category: " + c.getName());
                }
            }
            System.out.println("SEARCH BY HQL...");
            returnedList = searchBean.search(hqlString);
            if (returnedList != null) {
                Iterator i = returnedList.iterator();
                while (i.hasNext()) {
                    Protocol p = (Protocol) i.next();
                    System.out.println("Retrieved protocol: " + p.getName());
		    Category c = p.getType().getCategory();
                    System.out.println("    Its category is: " + c.getName());
                }
            }
            System.out.println("SEARCH BY CQL...");
            CQLQuery cqlQuery = new CQLQuery();
            CQLObject target = new CQLObject();
            target.setName("gov.nih.nci.caarray.domain.vocabulary.Category");
            CQLAttribute attribute = new CQLAttribute();
            attribute.setName("name");
            attribute.setValue("%Protocol%");
            attribute.setPredicate(CQLPredicate.LIKE);
            target.setAttribute(attribute);
            cqlQuery.setTarget(target);
            returnedList = searchBean.search(cqlQuery);
            if (returnedList != null) {
                Iterator i = returnedList.iterator();
                while (i.hasNext()) {
                    Category c = (Category) i.next();
                    System.out.println("Retrieved category: " + c.getName());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
