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
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.Inject;

/**
 * Ensures that retrieved entities are ready for transport, including correct
 * 'cutting' of object graphs.
 *
 * @see EntityPruner#makeChildrenLeaves(Object)
 */
public class EntityConfiguringInterceptor {

    private static final Logger LOG = Logger.getLogger(EntityConfiguringInterceptor.class);
    @Inject private static CaArrayHibernateHelper hibernateHelper; 

    /**
     * Ensures that any object returned and its direct associated entities are loaded.
     *
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "unchecked", "ucd" }) 
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        // make the call to the underlying method.  This method (prepareReturnValue) wraps the intended method.
        Object returnValue = invContext.proceed();

        // flush any changes made (ie, DataSet population) to this point.  We're going to modify
        // hibernate objects in ways we /don't/ want flushed in prepareEntity

        // FIXME: we cannot flush any changes here, because the SecurityPolicyPostLoadEventListener makes
        // changes to objects (like Project) that hibernate sees as modifications.  The anonymous user
        // does not have permission to modify the objects, so we get an exception at this point.
        // Commenting this call out for now, because the only time we'd need to flush changes is for
        // parse-on-demand, which we don't currently do.
//        hibernateHelper.getCurrentSession().flush();

        if (returnValue instanceof Collection) {
            prepareEntities((Collection<Object>) returnValue);
        } else {
            prepareEntity(returnValue);
        }

        // keep hibernate from performing write behind of all the cutting we just did
        hibernateHelper.getCurrentSession().clear();

        return returnValue;
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")  // Lots of explanatory comments
    private void prepareEntities(Collection<Object> collection) {
        // Create a temporary collection for the cut objects.  We will replace the passed-in
        // collection elements with the cut elements in here.
        Collection<Object> tmpCollection = new ArrayList<Object>(collection.size());
        EntityPruner pruner = new EntityPruner();
        // A note on this loop: We refresh each element first because the session is cleared
        // each iteration, which would potentially mean that the entities un-initialized
        // collection properties would be unavailable.
        //
        // We call clear because the hibernate session could be potentially HUGE after getting
        // certain collections on objects (like ArrayDesignDetails.designElements, probes, etc.)
        //
        // Together, these calls keep the hibernate session small, and ensure that we won't get
        // LazyInitializationExceptions
        Iterator<Object> it = collection.iterator();
        while (it.hasNext()) {
            Object toCut = it.next();
            if (toCut instanceof PersistentObject) {
                // some test code has non-persistentObject entities, which can't work here
                toCut = hibernateHelper.getCurrentSession().get(toCut.getClass(),
                                                              ((PersistentObject) toCut).getId());
            }
            prepareEntity(toCut, pruner);
            hibernateHelper.getCurrentSession().clear();
            tmpCollection.add(toCut);
            try {
                it.remove();
            } catch (UnsupportedOperationException uoe) {
                // Not fatal, or even so bad, but log anyways.
                LOG.debug("Could not remove from iterator: " + uoe, uoe);
            }
        }
        // Replace collection's elements with the cut elements.  Cutting in place will not work!
        collection.clear();
        collection.addAll(tmpCollection);
    }

    private void prepareEntity(Object entity) {
        prepareEntity(entity, new EntityPruner());
    }

    private void prepareEntity(Object entity, EntityPruner pruner) {
        pruner.makeChildrenLeaves(entity);
    }
}
