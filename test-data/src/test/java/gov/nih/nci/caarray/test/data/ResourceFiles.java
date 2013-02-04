//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.data;

import java.io.File;
import java.net.URL;

/**
 * Static helper for test file classes which read their data from resource files.
 * 
 * @author jscott
 */
public class ResourceFiles {
    /**
     * @param resourcePath path of the resource to return as a File object
     * @return the resource as a File object
     */
    public static File getResourceFile(String resourcePath) {
        //debug trace to avoid the mysterious silent death syndrome, esp when running junit under ant. 
        System.out.println("resourcePath=" + resourcePath);
        URL resourceUrl= ResourceFiles.class.getResource(resourcePath);
        System.out.println("resourceUrl=" + resourceUrl);  
        String resourceFileName= resourceUrl.getFile();
        System.out.println("resourceFileName=" + resourceFileName);
        
        return new File(ResourceFiles.class.getResource(resourcePath).getFile());
    } 
    
}
