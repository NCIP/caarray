//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

/**
 * Represents a physical location on a microarray.
 */
@Entity
@DiscriminatorValue("F")
public class Feature extends AbstractDesignElement {

    private static final long serialVersionUID = -6319120292398781186L;

    private short blockColumn;
    private short blockRow;
    private short column;
    private short row;

    private ArrayDesignDetails arrayDesignDetails;

    /**
     * Creates a new Feature.
     *
     * @param details the array design details this feature is for
     */
    public Feature(ArrayDesignDetails details) {
        super();
        setArrayDesignDetails(details);
    }

    /**
     * @deprecated hibernate & castor only
     */
    @Deprecated
    public Feature() {
        // hibernate-only constructor
    }

    /**
     * @return the blockColumn
     */
    public short getBlockColumn() {
        return blockColumn;
    }

    /**
     * @param blockColumn the blockColumn to set
     */
    public void setBlockColumn(short blockColumn) {
        this.blockColumn = blockColumn;
    }

    /**
     * @return the blockRow
     */
    public short getBlockRow() {
        return blockRow;
    }

    /**
     * @param blockRow the blockRow to set
     */
    public void setBlockRow(short blockRow) {
        this.blockRow = blockRow;
    }

    /**
     * @return the column
     */
    @Column(name = "feature_column")
    public short getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(short column) {
        this.column = column;
    }

    /**
     * @return the row
     */
    @Column(name = "feature_row")
    public short getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(short row) {
        this.row = row;
    }

    /**
     * @return the design details
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, name = "feature_details_id")
    @ForeignKey(name = "feature_details_fk")
    public ArrayDesignDetails getArrayDesignDetails() {
        return arrayDesignDetails;
    }

    private void setArrayDesignDetails(ArrayDesignDetails arrayDesignDetails) {
        this.arrayDesignDetails = arrayDesignDetails;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("blockColumn = ");
        stringBuffer.append(blockColumn);
        stringBuffer.append(", blockRow = ");
        stringBuffer.append(blockRow);
        stringBuffer.append(", column = ");
        stringBuffer.append(column);
        stringBuffer.append(", row = ");
        stringBuffer.append(row);
        return stringBuffer.toString();
    }

}
