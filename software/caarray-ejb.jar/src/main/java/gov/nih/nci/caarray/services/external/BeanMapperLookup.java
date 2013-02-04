//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.MapperIF;

/**
 * Utility class maintaining a cache of dozer mappers for each API version.
 * 
 * @author dkokotov
 */
public final class BeanMapperLookup {
    /**
     * Version 1.0 of API.
     */
    public static final String VERSION_1_0 = "v1_0";
    
    private static final Map<String, String> CONFIG_FILES = new HashMap<String, String>();
    static {
        CONFIG_FILES.put(VERSION_1_0, "dozerBeanMapping_v1_0.xml");
    }

    private static final Map<String, MapperIF> MAPPERS = new HashMap<String, MapperIF>();

    private BeanMapperLookup() {
        // NOOP
    }
    
    /**
     * Return mapper for given API version.
     * @param apiVersion api version to get mapper for
     * @return the mapper
     */
    public static synchronized MapperIF getMapper(String apiVersion) {
        MapperIF mapper = MAPPERS.get(apiVersion);
        if (mapper == null) {
            String configFile = CONFIG_FILES.get(apiVersion);
            mapper = new DozerBeanMapper(Collections.singletonList(configFile));
            MAPPERS.put(apiVersion, mapper);
        }
        return mapper;
    }
    
    /**
     * Adds a new api -> mapper association.  For use by unit tests.
     * 
     * @param apiVersion version of api
     * @param mapper associated mapper for that api version
     */
    public static void addMapper(String apiVersion, MapperIF mapper) {
        MAPPERS.put(apiVersion, mapper);
    }
}
