//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Class encapsulating the set of columns in an SDRF.
 * 
 * @author dkokotov
 */
public class SdrfColumns {
    private final List<SdrfColumn> columns;

    /**
     * Create an SdrfColumns with an empty column list.
     */
    public SdrfColumns() {
        columns = new ArrayList<SdrfColumn>();
    }

    /**
     * Create an SdrfColumns with the given columns.
     * @param columns the columns in the SDRF
     */
    public SdrfColumns(List<SdrfColumn> columns) {
        this.columns = columns;
    }

    /**
     * @return the columns
     */
    public List<SdrfColumn> getColumns() {
        return columns;
    }

    /**
     * Return the column immediately following the given column.
     * 
     * @param column the column for which to find the next one
     * @return the next column, or null if this is the last column.
     */
    public SdrfColumn getNextColumn(SdrfColumn column) {
        int nextColumnIndex = columns.indexOf(column) + 1;
        if (nextColumnIndex < columns.size()) {
            return columns.get(nextColumnIndex);
        } else {
            return null;
        }
    }

    /**
     * Return the column immediately preceding the given column.
     * 
     * @param column the column for which to find the previous one one
     * @return the previous column, or null if this is the first column.
     */    
    public SdrfColumn getPreviousColumn(SdrfColumn column) {
        int previousColumnIndex = columns.indexOf(column) - 1;
        if (previousColumnIndex > -1) {
            return columns.get(previousColumnIndex);
        } else {
            return null;
        }
    }

    /**
     * Return the column most closely preceding the given column that is a node column.
     * 
     * @param column the column for which to find the preceding node column
     * @return the preceding node column, or null if there is none
     */    
    public SdrfColumn getPreviousNodeColumn(SdrfColumn column) {
        int columnIndex = columns.indexOf(column);
        if (columnIndex < 1) {
            return null;
        }
        
        try {
            return Iterables.find(Iterables.reverse(columns.subList(0, columnIndex)),
                    new Predicate<SdrfColumn>() {
                        public boolean apply(SdrfColumn c) {
                            return c.getType().isNode();
                        }
                    });
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
