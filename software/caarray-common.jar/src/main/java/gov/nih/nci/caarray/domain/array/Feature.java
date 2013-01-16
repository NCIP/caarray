//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.vocabulary.Term;

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
    private Integer featureNumber;
    // CHECKSTYLE:OFF
    private Double x_Coordinate;
    private Double y_Coordinate;
    // CHECKSTYLE:ON
    private Term coordinateUnits;

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
     * @return the featureNumber
     */
    public Integer getFeatureNumber() {
        return featureNumber;
    }

    /**
     * @param featureNumber the featureNumber to set
     */
    public void setFeatureNumber(Integer featureNumber) {
        this.featureNumber = featureNumber;
    }

    // CHECKSTYLE:OFF
    // the underscore in the method name is to avoid 2 consecutive capital letters which make hibernate use a diffrent
    // property name computation that the grid/castor
    /**
     * @return the xCoordinate
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    @Column(name = "feature_x_coordinate")
    public Double getX_Coordinate() {
        return x_Coordinate;
    }

    /**
     * @param xCoordinate the x coordinate to set
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    public void setX_Coordinate(Double xCoordinate) {
        this.x_Coordinate = xCoordinate;
    }

    /**
     * @return the y coordinate
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    @Column(name = "feature_y_coordinate")
    public Double getY_Coordinate() {
        return y_Coordinate;
    }

    /**
     * @param yCoordinate the y coordinate to set
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    public void setY_Coordinate(Double yCoordinate) {
        this.y_Coordinate = yCoordinate;
    }
    // CHECKSTYLE:ON
    /**
     * @return the coordinate units
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_coordinate_units")
    @ForeignKey(name = "feature_coordinate_units_fk")
    public Term getCoordinateUnits() {
        return coordinateUnits;
    }

    /**
     * @param coordinateUnits the coordinate units to set
     */
    public void setCoordinateUnits(Term coordinateUnits) {
        this.coordinateUnits = coordinateUnits;
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
}
