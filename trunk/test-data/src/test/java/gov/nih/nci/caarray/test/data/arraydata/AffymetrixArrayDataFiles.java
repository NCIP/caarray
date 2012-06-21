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
package gov.nih.nci.caarray.test.data.arraydata;

import static gov.nih.nci.caarray.test.data.ResourceFiles.getResourceFile;

import java.io.File;

public class AffymetrixArrayDataFiles {

    public static final File TEST3_CEL = getResourceFile("/arraydata/affymetrix/Test3-1-121502.CEL");
    public static final File TEST3_CHP = getResourceFile("/arraydata/affymetrix/Test3-1-121502.CHP");
    public static final File TEST3_CALVIN_CEL = getResourceFile("/arraydata/affymetrix/Test3-1-121502.calvin.CEL");
    public static final File HG_FOCUS_CEL = getResourceFile("/arraydata/affymetrix/HG-Focus-1-121502.cel");
    public static final File HG_FOCUS_CALVIN_CEL = getResourceFile("/arraydata/affymetrix/HG-Focus-1-121502.calvin.cel");
    public static final File HG_FOCUS_CHP = getResourceFile("/arraydata/affymetrix/HG-Focus-1-121502.CHP");
    public static final File HG_FOCUS_CALVIN_CHP = getResourceFile("/arraydata/affymetrix/HG-Focus-1-121502.calvin.CHP");
    public static final File TEN_K_1_CHP = getResourceFile("/arraydata/affymetrix/10k_1.CHP");
    public static final File TEN_K_1_CALVIN_CHP = getResourceFile("/arraydata/affymetrix/10k_1-calvin.CHP");
    public static final File TEST_HG_U133_PLUS_2_CEL = getResourceFile("/arraydata/affymetrix/Test_HG-U133_Plus_2.CEL");
    public static final File TEST3_CALVIN_CHP = getResourceFile("/arraydata/affymetrix/Test3-1-121502.calvin.CHP");
    public static final File TEST3_INVALID_HEADER_CEL = getResourceFile("/arraydata/affymetrix/Test3_invalid_header.CEL");
    public static final File TEST3_INVALID_DATA_CEL = getResourceFile("/arraydata/affymetrix/Test3_invalid_data.CEL");
    public static final File TEST3_SPECIFICATION_ZIP = getResourceFile("/magetab/specification/specification.zip");    
    public static final File GSM428956_CEL =  getResourceFile("/arraydata/affymetrix/GSM428956.CEL");
    public static final File U133_Plus_Ivt_Breast_A_CEL = getResourceFile("/arraydata/affymetrix/u1332plus_ivt_breast_A.CEL");
    public static final File BIRDSEED_SNP_TEST_CHP =  getResourceFile("/arraydata/affymetrix/BirdseedSNPTest.chp");
    public static final File BRLMM_SNP_TEST_CHP =  getResourceFile("/arraydata/affymetrix/BrlmmSNPTest001.chp");
    public static final File AXIOMGT_SNP_TEST_CHP =  getResourceFile("/arraydata/affymetrix/AxiomgtSNPTest1.chp");
    public static final File COPY_NUMBER_4_CHP = getResourceFile("/arraydata/affymetrix/test.cn4.cnchp");
    public static final File DABG_CHP = getResourceFile("/arraydata/affymetrix/1_1T.rma-exon-core-dabg.chp");
 }
