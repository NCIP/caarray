/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
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
package gov.nih.nci.caarray.application.fileaccess;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Provides a scope
 * 
 * @author dkokotov
 */
public class ExplicitRequestScope implements Scope {
    /**
     * A threadlocal scope map for non-http request scopes. The {@link #REQUEST} scope falls back to this scope map if
     * no http request is available, and requires {@link #scopeRequest} to be called as an alertnative.
     */
    private static final ThreadLocal<Map<String, Object>> requestScopeContext = new ThreadLocal<Map<String, Object>>();

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        final String name = key.toString();
        return new Provider<T>() {
            @Override
            public T get() {
                final Map<String, Object> scopeMap = requestScopeContext.get();
                if (null != scopeMap) {
                    @SuppressWarnings("unchecked")
                    T t = (T) scopeMap.get(name);

                    // Accounts for @Nullable providers.
                    if (NullObject.INSTANCE == t) {
                        return null;
                    }

                    if (t == null) {
                        t = unscoped.get();
                        // Store a sentinel for provider-given null values.
                        scopeMap.put(name, t != null ? t : NullObject.INSTANCE);
                    }

                    return t;
                }
                throw new OutOfScopeException("No TempCacheScope Active");
            }

            @Override
            public String toString() {
                return String.format("%s[%s]", unscoped, this);
            }
        };
    }

    /**
     * Scopes the given callable inside a request scope. This is not the same as the HTTP request scope, but is used if
     * no HTTP request scope is in progress. In this way, keys can be scoped as @RequestScoped and exist in non-HTTP
     * requests (for example: RPC requests) as well as in HTTP request threads.
     * 
     * @param callable code to be executed which depends on the request scope. Typically in another thread, but not
     *            necessarily so.
     * @param seedMap the initial set of scoped instances for Guice to seed the request scope with. To seed a key with
     *            null, use {@code null} as the value.
     * @return a callable that when called will run inside the a request scope that exposes the instances in the
     *         {@code seedMap} as scoped keys.
     * @since 3.0
     */
    public static <T> Callable<T> scope(final Callable<T> callable, Map<Key<?>, Object> seedMap) {
        Preconditions.checkArgument(null != seedMap,
                "Seed map cannot be null, try passing in Collections.emptyMap() instead.");

        // Copy the seed values into our local scope map.
        final Map<String, Object> scopeMap = Maps.newHashMap();
        for (final Map.Entry<Key<?>, Object> entry : seedMap.entrySet()) {
            final Object value = validateAndCanonicalizeValue(entry.getKey(), entry.getValue());
            scopeMap.put(entry.getKey().toString(), value);
        }

        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                Preconditions.checkState(null == requestScopeContext.get(),
                        "A request scope is already in progress, cannot scope a new request in this thread.");

                requestScopeContext.set(scopeMap);

                try {
                    return callable.call();
                } finally {
                    requestScopeContext.remove();
                }
            }
        };
    }

    /**
     * Validates the key and object, ensuring the value matches the key type, and canonicalizing null objects to the
     * null sentinel.
     */
    private static Object validateAndCanonicalizeValue(Key<?> key, Object object) {
        if (object == null || object == NullObject.INSTANCE) {
            return NullObject.INSTANCE;
        }

        if (!key.getTypeLiteral().getRawType().isInstance(object)) {
            throw new IllegalArgumentException("Value[" + object + "] of type[" + object.getClass().getName()
                    + "] is not compatible with key[" + key + "]");
        }

        return object;
    }

    @Override
    public String toString() {
        return "caArrayScopes.TEMPORARY_FILE_CACHE";
    }

    /** A sentinel attribute value representing null. */
    enum NullObject {
        INSTANCE
    }
}
