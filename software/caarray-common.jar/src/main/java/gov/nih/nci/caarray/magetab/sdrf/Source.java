//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>Source</code> is the original source material before any treatment events.
 * It is also a top node of the directed acyclic graph generated by treatments.
 */
public final class Source extends AbstractBioMaterial {

    private static final long serialVersionUID = -1832555606775950470L;

    private final List<Provider> providers = new ArrayList<Provider>();

    /**
     * @return the providers
     */
    public List<Provider> getProviders() {
        return providers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.SOURCE;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllSources().add(this);
    }

}
