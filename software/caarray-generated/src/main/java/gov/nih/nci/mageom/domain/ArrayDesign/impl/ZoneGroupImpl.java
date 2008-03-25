package gov.nih.nci.mageom.domain.ArrayDesign.impl;

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
 
  /**
   * Specifies a repeating area on an array. This is useful for printing when the same pattern is repeated 
   * in a regular fashion. 
   * 
   */

public class ZoneGroupImpl 
  extends gov.nih.nci.mageom.domain.impl.ExtendableImpl
  implements gov.nih.nci.mageom.domain.ArrayDesign.ZoneGroup, java.io.Serializable {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    /**
     * The id java.lang.Long.
     */
    private java.lang.Long id;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public java.lang.Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param idVal the id
     */
    public void setId(final java.lang.Long idVal) {
        this.id = idVal;
    }
    /**
     * The spacingsBetweenZonesX java.lang.Float.
     */
    private java.lang.Float spacingsBetweenZonesX;

    /**
     * Gets the spacingsBetweenZonesX.
     *
     * @return the spacingsBetweenZonesX
     */
    public java.lang.Float getSpacingsBetweenZonesX() {
        return spacingsBetweenZonesX;
    }

    /**
     * Sets the spacingsBetweenZonesX.
     *
     * @param spacingsBetweenZonesXVal the spacingsBetweenZonesX
     */
    public void setSpacingsBetweenZonesX(final java.lang.Float spacingsBetweenZonesXVal) {
        this.spacingsBetweenZonesX = spacingsBetweenZonesXVal;
    }
    /**
     * The spacingsBetweenZonesY java.lang.Float.
     */
    private java.lang.Float spacingsBetweenZonesY;

    /**
     * Gets the spacingsBetweenZonesY.
     *
     * @return the spacingsBetweenZonesY
     */
    public java.lang.Float getSpacingsBetweenZonesY() {
        return spacingsBetweenZonesY;
    }

    /**
     * Sets the spacingsBetweenZonesY.
     *
     * @param spacingsBetweenZonesYVal the spacingsBetweenZonesY
     */
    public void setSpacingsBetweenZonesY(final java.lang.Float spacingsBetweenZonesYVal) {
        this.spacingsBetweenZonesY = spacingsBetweenZonesYVal;
    }
    /**
     * The zonesPerX java.lang.Integer.
     */
    private java.lang.Integer zonesPerX;

    /**
     * Gets the zonesPerX.
     *
     * @return the zonesPerX
     */
    public java.lang.Integer getZonesPerX() {
        return zonesPerX;
    }

    /**
     * Sets the zonesPerX.
     *
     * @param zonesPerXVal the zonesPerX
     */
    public void setZonesPerX(final java.lang.Integer zonesPerXVal) {
        this.zonesPerX = zonesPerXVal;
    }
    /**
     * The zonesPerY java.lang.Integer.
     */
    private java.lang.Integer zonesPerY;

    /**
     * Gets the zonesPerY.
     *
     * @return the zonesPerY
     */
    public java.lang.Integer getZonesPerY() {
        return zonesPerY;
    }

    /**
     * Sets the zonesPerY.
     *
     * @param zonesPerYVal the zonesPerY
     */
    public void setZonesPerY(final java.lang.Integer zonesPerYVal) {
        this.zonesPerY = zonesPerYVal;
    }

    /**
     * The distanceUnit gov.nih.nci.mageom.domain.Measurement.DistanceUnit.
     */
    private gov.nih.nci.mageom.domain.Measurement.DistanceUnit distanceUnit;

    /**
     * Gets the distanceUnit.
     *
     * @return the distanceUnit
     */
    public gov.nih.nci.mageom.domain.Measurement.DistanceUnit getDistanceUnit() {
        return distanceUnit;    
    }

    /**
     * Sets the distanceUnit.
     *
     * @param distanceUnitVal the distanceUnit
     */
    public void setDistanceUnit(final 
      gov.nih.nci.mageom.domain.Measurement.DistanceUnit distanceUnitVal) {
        this.distanceUnit = distanceUnitVal;
    }

    /**
     * The zoneLocations set.
     */
    private java.util.Collection zoneLocations = new java.util.HashSet();

    /**
     * Gets the zoneLocations.
     *
     * @return the zoneLocations
     */
    public java.util.Collection getZoneLocations() {
        return zoneLocations;
    }

    /**
     * Sets the zoneLocations.
     *
     * @param zoneLocationsVal the zoneLocations
     */
    public void setZoneLocations(final java.util.Collection zoneLocationsVal) {
        this.zoneLocations = zoneLocationsVal;
    }    

    /**
     * The zoneLayout gov.nih.nci.mageom.domain.ArrayDesign.ZoneLayout.
     */
    private gov.nih.nci.mageom.domain.ArrayDesign.ZoneLayout zoneLayout;

    /**
     * Gets the zoneLayout.
     *
     * @return the zoneLayout
     */
    public gov.nih.nci.mageom.domain.ArrayDesign.ZoneLayout getZoneLayout() {
        return zoneLayout;    
    }

    /**
     * Sets the zoneLayout.
     *
     * @param zoneLayoutVal the zoneLayout
     */
    public void setZoneLayout(final 
      gov.nih.nci.mageom.domain.ArrayDesign.ZoneLayout zoneLayoutVal) {
        this.zoneLayout = zoneLayoutVal;
    }

    /**
     * Checks if given object is equal to this object.
     *
     * @param obj the object to compare to this object
     * @return true if they are equal, false if they are not
     */
    public boolean equals(final Object obj) {
        boolean theyAreEqual = false;
        if (obj instanceof gov.nih.nci.mageom.domain.ArrayDesign.ZoneGroup) {
            final gov.nih.nci.mageom.domain.ArrayDesign.ZoneGroup castObject =
                (gov.nih.nci.mageom.domain.ArrayDesign.ZoneGroup) obj;                  
            java.lang.Long thisId = getId();        
            if (thisId != null && thisId.equals(castObject.getId())) {
                theyAreEqual = true;
            }
            }
            return theyAreEqual;
        }

    /**
     * Returns the hashcode for the object.
     *
     * @return the int hashcode
     */
    public int hashCode() {
        int theHashCode = 0;
        if (getId() != null) {
            theHashCode += getId().hashCode();
        }
        return theHashCode;
    }
}
