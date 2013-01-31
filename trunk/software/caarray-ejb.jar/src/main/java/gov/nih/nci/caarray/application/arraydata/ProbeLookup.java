//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.array.AbstractProbe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides lookup of array design <code>AbstractProbe</code> by location information.
 */
final class ProbeLookup {

    private final Map<String, AbstractProbe> probeMap;

    ProbeLookup(Collection<? extends AbstractProbe> probes) {
        probeMap = new HashMap<String, AbstractProbe>(probes.size());
        for (AbstractProbe probe : probes) {
            addProbe(probe);
        }
    }

    private void addProbe(AbstractProbe probe) {
        probeMap.put(probe.getName(), probe);
    }

    AbstractProbe getProbe(String probeName) {
        return probeMap.get(probeName);
    }

}
