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
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;

/**
 * Contains hybridization data represented by an <code>AbstractArrayData</code> or as requested by a client.
 */
@Entity
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class DataSet extends AbstractCaArrayObject {

    private static final long serialVersionUID = 4430513886275629776L;

    private AbstractArrayData arrayData;
    private List<HybridizationData> hybridizationDataList = new ArrayList<HybridizationData>();
    private List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();
    private DesignElementList designElementList;

    
    /**
     * @return the arrayData
     */
    @OneToOne(mappedBy = "dataSet")
    public AbstractArrayData getArrayData() {
        return arrayData;
    }

    /**
     * @param arrayData the arrayData to set
     */
    public void setArrayData(AbstractArrayData arrayData) {
        this.arrayData = arrayData;
    }

    /**
     * @return the hybridizationDatas
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_set")
    @IndexColumn(name = "HYBRIDIZATION_INDEX")
    @Cascade(CascadeType.ALL)
    public List<HybridizationData> getHybridizationDataList() {
        return hybridizationDataList;
    }

    /**
     * Creates a new <code>HybridizationData</code> for this <code>DataSet</code>.
     * @param hybridization <code>Hybridization</code> associated with the data
     *
     * @return the new HybridizationData.
     */
    public HybridizationData addHybridizationData(Hybridization hybridization) {
        HybridizationData hybridizationData = new HybridizationData();
        hybridizationData.setHybridization(hybridization);
        getHybridizationDataList().add(hybridizationData);
        hybridizationData.setDataSet(this);
        return hybridizationData;
    }

    /**
     * @param hybridizationDatas the hybridizationDatas to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setHybridizationDataList(List<HybridizationData> hybridizationDatas) {
        this.hybridizationDataList = hybridizationDatas;
    }

    /**
     * @return the quantitationTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "quantitationtype_index")
    @JoinTable(
            name = "datasetquantitationtype",
            joinColumns = { @JoinColumn(name = "dataset_id") },
            inverseJoinColumns = { @JoinColumn(name = "quantitationtype_id") }
    )
    @ForeignKey(name = "dataset_fk", inverseName = "quantitationtype_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public List<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * @param quantitationTypes the quantitationTypes to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setQuantitationTypes(List<QuantitationType> quantitationTypes) {
        this.quantitationTypes = quantitationTypes;
    }

    /**
     * Adds a new type to this <code>DataSet</code>, creating the appropriate columns for all
     * <code>HybridizationDatas</code>.
     *
     * @param type the type to add.
     */
    public void addQuantitationType(QuantitationType type) {
        quantitationTypes.add(type);
        for (HybridizationData hybridizationData : hybridizationDataList) {
            hybridizationData.addColumn(type);
        }
    }

    /**
     * Adds a list of new types to this <code>DataSet</code>, creating the appropriate columns for all
     * <code>HybridizationDatas</code>.
     *
     * @param types the types to add.
     */
    public void addQuantitationTypes(List<QuantitationType> types) {
        for (QuantitationType type : types) {
            addQuantitationType(type);
        }
    }
    /**
     * Initialize the columns of each HybridizationData in this DataSet with empty value sets of given size.
     * 
     * @param types the types of the columns to initialize
     * @param numberOfRows the number of rows each columnn's array of values should have
     */    
    public void prepareColumns(List<QuantitationType> types, int numberOfRows) {
        for (HybridizationData hybridizationData : hybridizationDataList) {
            hybridizationData.prepareColumns(types, numberOfRows);
        }
    }
    
    /**
     * Determine whether all columns for the given quantitation types in each HybridizationData in this DataSet have
     * been loaded with data values.
     * 
     * @param types the quantitation types of interest
     * @return true if for each HybridizationData in this DataSet, each column whose type is in the given set of types
     *         has been loaded, e.g. column.isLoaded is true; false otherwise.
     */
    public boolean datasLoaded(List<QuantitationType> types) {
        for (HybridizationData hybridizationData : hybridizationDataList) {
            if (!hybridizationData.areColumnsLoaded(types)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return the designElementList
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "DATASET_DESIGNELEMENTLIST_FK")
    public DesignElementList getDesignElementList() {
        return designElementList;
    }

    /**
     * @param designElementList the designElementList to set
     */
    public void setDesignElementList(DesignElementList designElementList) {
        this.designElementList = designElementList;
    }

}
