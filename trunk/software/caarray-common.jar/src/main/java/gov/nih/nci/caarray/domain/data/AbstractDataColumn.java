/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.util.URIUserType;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 * Subclasses of <code>AbstractDataColumn</code> contain the actual array data corresponding to a single
 * <code>QuantitationType</code>.
 * 
 * <p><b>Note:</b> AbstractDataColumn is <em>not</em> a normal hibernate object.  The values API (getValuesAsArray,
 * initializeArray, and setValuesFromArray) does not manipulate hibernate-managed information.  Instead,
 * values are managed by the DataStorageFacade.  To properly initialize this class, ParsedDataPersister
 * must be utilized.  NPEs will result from incorrect usage.
 */
@TypeDefs(@TypeDef(name = "uri", typeClass = URIUserType.class))
@Entity
@Table(name = "datacolumn")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@SuppressWarnings("PMD.CyclomaticComplexity")
public abstract class AbstractDataColumn extends AbstractCaArrayObject {
    private static final long serialVersionUID = 1L;

    /** separator to use for encoding an array of values as string, except for StringColumn. */
    protected static final String SEPARATOR = " ";
    
    /** Error message for incorrect usage of uninitialized values array. */
    protected static final String ERROR_NOT_INITIALIZED = 
            "Cannot get uninitialized values array - must be loaded from DataStorage";

    private HybridizationData hybridizationData;
    private QuantitationType quantitationType;
    private URI dataHandle;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    static AbstractDataColumn create(QuantitationType type) {
        AbstractDataColumn column = null;
        if (type.getTypeClass().equals(Boolean.class)) {
            column = new BooleanColumn();
        } else if (type.getTypeClass().equals(Double.class)) {
            column = new DoubleColumn();
        } else if (type.getTypeClass().equals(Float.class)) {
            column = new FloatColumn();
        } else if (type.getTypeClass().equals(Integer.class)) {
            column = new IntegerColumn();
        } else if (type.getTypeClass().equals(Long.class)) {
            column = new LongColumn();
        } else if (type.getTypeClass().equals(Short.class)) {
            column = new ShortColumn();
        } else if (type.getTypeClass().equals(String.class)) {
            column = new StringColumn();
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + type.getType());
        }
        column.setQuantitationType(type);
        return column;
    }

    /**
     * @return the quantitationType
     */
    @ManyToOne
    @ForeignKey(name = "column_quantitationtype_fk")
    public QuantitationType getQuantitationType() {
        return this.quantitationType;
    }

    /**
     * @param quantitationType the quantitationType to set
     */
    public void setQuantitationType(QuantitationType quantitationType) {
        this.quantitationType = quantitationType;
    }

    /**
     * Indicates whether this column is already loaded, meaning its populated with an array of values.
     * 
     * @return true if data has been loaded.
     */
    @Transient
    public abstract boolean isLoaded();

    /**
     * @return the hybridizationData
     */
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    @ForeignKey(name = "column_hybridizationdata_fk")
    @IndexColumn(name = "column_index")
    public HybridizationData getHybridizationData() {
        return this.hybridizationData;
    }

    /**
     * @param hybridizationData the hybridizationData to set
     */
    public void setHybridizationData(HybridizationData hybridizationData) {
        this.hybridizationData = hybridizationData;
    }

    /**
     * @return the dataHandle
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_handle")
    @Type(type = "uri")
    public URI getDataHandle() {
        return this.dataHandle;
    }

    /**
     * @param dataHandle the dataHandle to set
     */
    public void setDataHandle(URI dataHandle) {
        this.dataHandle = dataHandle;
    }

    /**
     * Serialized values, from the DataStorageFacade.
     * 
     * @return the values in this column as an array. Subclasses should return an array of the appropriate primitive
     *         type or String.
     */
    @Transient
    public abstract Serializable getValuesAsArray();

    /**
     * Set the values of this column from a value array.  The incoming values should be coming
     * from either a parsed file, or the DataStorageFacade.
     * 
     * @param array the values for this column. Should be an array of the appropriate primitive or String type.
     */
    public abstract void setValuesFromArray(Serializable array);

    
    // get/setValuesAsString is called via xml-mapping.xml for the remote APIs.  These are the only clients
    // to these methods.  Do not remove them.
    
    /**
     * @return the values of this column, in a space-separated representation, where each value is encoded using the
     *         literal representation of the xs:short type defined in the XML Schema standard.
     */
    @Transient
    public abstract String getValuesAsString();

    /**
     * Set values from a String representation. The string should contain a list of space-separated values, with each
     * value encoded using the literal representation of the xs:boolean type defined in XML Schema.
     * 
     * @param s the string containing the space-separated values
     */
    public abstract void setValuesAsString(String s);

    /**
     * Initializes this column to hold the number of values given.
     * 
     * @param numberOfValues number of values
     */
    public abstract void initializeArray(int numberOfValues);
}
