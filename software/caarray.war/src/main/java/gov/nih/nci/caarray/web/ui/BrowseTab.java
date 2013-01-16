//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.ui;


/**
 * @author Winston Cheng
 *
 */
public class BrowseTab implements Comparable<BrowseTab> {
    private final String name;
    private final Number id;
    private final int count;

    /**
     * Construct a browse tab.
     * @param name name of the tab
     * @param count count of results
     * @param id id
     */
    public BrowseTab(String name, Number id, int count) {
        this.name = name;
        this.id = id;
        this.count = count;
    }
    /**
     * Sorts the tabs by name.
     * {@inheritDoc}
     */
    public int compareTo(BrowseTab tab) {
        return name.compareToIgnoreCase(tab.getName());
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the id
     */
    public Number getId() {
        return id;
    }
    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
}
