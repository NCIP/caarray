//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.domain.array.AbstractProbe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides lookup of array design <code>AbstractProbe</code> by location information.
 */
public final class ProbeLookup {

    private final Map<String, AbstractProbe> probeMap;

    /**
     * @param probes the probes to map by name.
     */
    public ProbeLookup(Collection<? extends AbstractProbe> probes) {
        probeMap = new HashMap<String, AbstractProbe>(probes.size());
        for (AbstractProbe probe : probes) {
            addProbe(probe);
        }
    }

    private void addProbe(AbstractProbe probe) {
        probeMap.put(probe.getName(), probe);
    }

    /**
     * @param probeName name of prob to lookup.
     * @return the probe by that name.
     */
    public AbstractProbe getProbe(String probeName) {
        return probeMap.get(probeName);
    }

}
