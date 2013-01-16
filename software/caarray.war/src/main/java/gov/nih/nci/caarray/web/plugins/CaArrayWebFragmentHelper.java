//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.velocity.exception.VelocityException;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import com.atlassian.velocity.VelocityManager;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;

/**
 * Implementation of Atlassian interfaces to support web fragment plugins.
 * 
 * @author dkokotov
 */
public class CaArrayWebFragmentHelper implements WebFragmentHelper, I18nResolver {
    private final VelocityManager velocityManager;

    /**
     * Constructor.
     * 
     * @param velocityManager VelocityManager for rendering templates
     */
    public CaArrayWebFragmentHelper(VelocityManager velocityManager) {
        this.velocityManager = velocityManager;
    }

    private final TextProvider textProvider = new TextProviderFactory().createInstance(getClass(),
            new LocaleProvider() {
                @Override
                public Locale getLocale() {
                    return ServletActionContext.getContext().getLocale();
                }
            });

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderVelocityFragment(String fragment, Map<String, Object> context) {
        try {
            return this.velocityManager.getEncodedBodyForContent(fragment, "http://foo", context);
        } catch (final VelocityException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextProvider loadContextProvider(String className, Plugin plugin) throws ConditionLoadingException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Condition loadCondition(String className, Plugin plugin) throws ConditionLoadingException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getI18nValue(String key, List<?> arguments, Map<String, Object> context) {
        return this.textProvider.getText(key, (List<Object>) arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message createMessage(String key, Serializable... arguments) {
        return new MessageImpl(key, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageCollection createMessageCollection() {
        return new MessageCollectionImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getAllTranslationsForPrefix(String prefix) {
        return new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale) {
        return new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(Message message) {
        return getText(message.getKey(), message.getArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(String key) {
        return this.textProvider.getText(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(String key, Serializable... arguments) {
        return this.textProvider.getText(key, new ArrayList<Object>(Arrays.asList(arguments)));
    }

    /**
     * Implementation of message.
     */
    private class MessageImpl implements Message {
        private static final long serialVersionUID = 1L;

        private final String key;
        private final Serializable[] arguments;

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Serializable[] getArguments() {
            return this.arguments;
        }

        public MessageImpl(String key, Serializable[] arguments) {
            this.key = key;
            this.arguments = arguments;
        }
    }

    /**
     * Implementation of message collection.
     */
    private class MessageCollectionImpl implements MessageCollection {
        private final List<Message> messages = new ArrayList<Message>();

        public MessageCollectionImpl() {
            // nop-op
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAll(List<Message> msgs) {
            this.messages.addAll(msgs);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addMessage(Message message) {
            this.messages.add(message);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addMessage(String key, Serializable... arguments) {
            this.messages.add(new MessageImpl(key, arguments));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Message> getMessages() {
            return this.messages;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty() {
            return this.messages.isEmpty();
        }
    }
}
