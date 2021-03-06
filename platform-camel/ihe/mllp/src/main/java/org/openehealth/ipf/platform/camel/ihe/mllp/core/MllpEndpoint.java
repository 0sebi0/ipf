/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.mllp.core;

import ca.uhn.hl7v2.parser.Parser;
import org.apache.camel.*;
import org.apache.camel.component.mina.MinaConfiguration;
import org.apache.camel.component.mina.MinaEndpoint;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.commons.lang.Validate;
import org.apache.mina.common.*;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.CustomInterceptorWrapper;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.MllpCustomInterceptor;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.consumer.*;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.producer.*;

import javax.net.ssl.SSLContext;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;


/**
 * A wrapper for standard camel-mina endpoint 
 * which provides support for IHE PIX/PDQ-related extensions.
 * @author Dmytro Rud
 */
public class MllpEndpoint extends DefaultEndpoint {

    private final MinaEndpoint wrappedEndpoint;
    private final boolean audit;
    private final boolean allowIncompleteAudit;
    private final MllpAuditStrategy serverStrategy;
    private final MllpAuditStrategy clientStrategy;
    private final MllpTransactionConfiguration transactionConfiguration;
    private final Parser parser;
    
    private final SSLContext sslContext;
    private final List<MllpCustomInterceptor> customInterceptors;
    private final boolean mutualTLS;
    private final String[] sslProtocols;
    private final String[] sslCiphers;
    
    private final boolean supportInteractiveContinuation;
    private final boolean supportUnsolicitedFragmentation;
    private final boolean supportSegmentFragmentation;
    private final int interactiveContinuationDefaultThreshold;
    private final int unsolicitedFragmentationThreshold;
    private final int segmentFragmentationThreshold;
    private final InteractiveContinuationStorage interactiveContinuationStorage;
    private final UnsolicitedFragmentationStorage unsolicitedFragmentationStorage;
    private final boolean autoCancel;

    private final NakFactory nakFactory;

    /**
     * Constructor.
     * @param wrappedEndpoint
     *      The original camel-mina endpoint instance.
     * @param audit
     *      Whether ATNA auditing should be performed.
     * @param allowIncompleteAudit
     *      Whether incomplete ATNA auditing are allowed as well.
     * @param serverStrategy
     *      Server-side audit strategy.
     * @param clientStrategy
     *      Client-side audit strategy.
     * @param transactionConfiguration
     *      Configuration of the transaction exposed by this endpoint.
     * @param parser
     *      HL7 v2 parser instance for using in (un-)marshalling.      
     * @param sslContext
     *      the SSL context to use; {@code null} if secure communication is not used.
     * @param mutualTLS
     *      {@code true} when client authentication for mutual TLS is required.
     * @param customInterceptors
     *      the interceptors defined in the endpoint URI.
     * @param sslProtocols
     *      the protocols defined in the endpoint URI or {@code null} if none were specified.
     * @param sslCiphers
     *      the ciphers defined in the endpoint URI or {@code null} if none were specified.
     * @param supportInteractiveContinuation
     *      {@code true} when this endpoint should support interactive message continuation.
     * @param supportUnsolicitedFragmentation
     *      {@code true} when this endpoint should support segment fragmentation.
     * @param supportSegmentFragmentation
     *      {@code true} when this endpoint should support segment fragmentation.
     * @param interactiveContinuationDefaultThreshold
     *      default consumer-side threshold for interactive response continuation. 
     * @param unsolicitedFragmentationThreshold
     *      producer-side threshold for unsolicited message fragmentation. 
     * @param segmentFragmentationThreshold
     *      threshold for segment fragmentation.
     * @param interactiveContinuationStorage
     *      consumer-side storage for interactive message continuation.
     * @param unsolicitedFragmentationStorage
     *      consumer-side storage for unsolicited message fragmentation.
     * @param autoCancel
     *      whether the producer should automatically send a cancel message
     *      after it has collected all inetractive continuation pieces.
     * @param nakFactory
     *      HL7v2 NAK factory.
     */
    public MllpEndpoint(
            MinaEndpoint wrappedEndpoint, 
            boolean audit, 
            boolean allowIncompleteAudit, 
            MllpAuditStrategy serverStrategy, 
            MllpAuditStrategy clientStrategy, 
            MllpTransactionConfiguration transactionConfiguration, 
            Parser parser, 
            SSLContext sslContext, 
            boolean mutualTLS, 
            List<MllpCustomInterceptor> customInterceptors, 
            String[] sslProtocols, 
            String[] sslCiphers,
            boolean supportInteractiveContinuation,
            boolean supportUnsolicitedFragmentation,
            boolean supportSegmentFragmentation,
            int interactiveContinuationDefaultThreshold,
            int unsolicitedFragmentationThreshold,
            int segmentFragmentationThreshold,
            InteractiveContinuationStorage interactiveContinuationStorage,
            UnsolicitedFragmentationStorage unsolicitedFragmentationStorage,
            boolean autoCancel,
            NakFactory nakFactory)
    {
        Validate.notNull(wrappedEndpoint);
        Validate.notNull(serverStrategy);
        Validate.notNull(clientStrategy);
        Validate.notNull(transactionConfiguration);
        Validate.notNull(parser);
        Validate.noNullElements(customInterceptors);
        Validate.notNull(nakFactory);

        this.wrappedEndpoint = wrappedEndpoint;
        this.audit = audit;
        this.allowIncompleteAudit = allowIncompleteAudit;
        this.serverStrategy = serverStrategy;
        this.clientStrategy = clientStrategy;
        this.transactionConfiguration = transactionConfiguration;
        this.parser = parser;
        this.sslContext = sslContext;
        this.mutualTLS = mutualTLS;
        this.customInterceptors = customInterceptors;
        this.sslProtocols = sslProtocols;
        this.sslCiphers = sslCiphers;
        
        this.supportInteractiveContinuation = supportInteractiveContinuation;
        this.supportUnsolicitedFragmentation = supportUnsolicitedFragmentation;
        this.supportSegmentFragmentation = supportSegmentFragmentation;
        this.interactiveContinuationDefaultThreshold = interactiveContinuationDefaultThreshold;
        this.unsolicitedFragmentationThreshold = unsolicitedFragmentationThreshold;
        this.segmentFragmentationThreshold = segmentFragmentationThreshold;
        this.interactiveContinuationStorage = interactiveContinuationStorage;
        this.unsolicitedFragmentationStorage = unsolicitedFragmentationStorage;
        this.autoCancel = autoCancel;

        this.nakFactory = nakFactory;
    }


    /**
     * Wraps the original starting point of the consumer route 
     * into a set of PIX/PDQ-specific interceptors.
     * @param processor
     *      The original consumer processor.  
     */
    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        if (sslContext != null) {
            DefaultIoFilterChainBuilder filterChain = wrappedEndpoint.getAcceptorConfig().getFilterChain();
            if (!filterChain.contains("ssl")) {
                HandshakeCallbackSSLFilter filter = new HandshakeCallbackSSLFilter(sslContext);
                filter.setNeedClientAuth(mutualTLS);
                filter.setHandshakeExceptionCallback(new HandshakeFailureCallback());
                filter.setEnabledProtocols(sslProtocols);
                filter.setEnabledCipherSuites(sslCiphers);
                filterChain.addFirst("ssl", filter);
            }
        }

        Processor x = processor;
        for (MllpCustomInterceptor interceptor : customInterceptors) {
            x = new CustomInterceptorWrapper(interceptor, this, x);
        }
        if (isAudit()) {
            x = new ConsumerAuthenticationFailureInterceptor(this, x);
        }
        x = new ConsumerAdaptingInterceptor(this, x);
        x = new ConsumerOutputAcceptanceInterceptor(this, x);
        if (isAudit()) {
            x = new ConsumerAuditInterceptor(this, x);
        }
        if (isSupportInteractiveContinuation()) {
            x = new ConsumerInteractiveResponseSenderInterceptor(this, x);
        }
        x = new ConsumerInputAcceptanceInterceptor(this, x);
        x = new ConsumerMarshalInterceptor(this, x);
        if (isSupportUnsolicitedFragmentation()) {
            x = new ConsumerRequestDefragmenterInterceptor(this, x);
        }
        x = new ConsumerStringProcessorInterceptor(this, x);
        return wrappedEndpoint.createConsumer(x);
    }


    /**
     * Wraps the original camel-mina producer  
     * into a set of PIX/PDQ-specific ones.
     */
    @Override
    public Producer createProducer() throws Exception {
        if (sslContext != null) {
            DefaultIoFilterChainBuilder filterChain = wrappedEndpoint.getConnectorConfig().getFilterChain();
            if (!filterChain.contains("ssl")) {
                HandshakeCallbackSSLFilter filter = new HandshakeCallbackSSLFilter(sslContext);
                filter.setUseClientMode(true);
                filter.setHandshakeExceptionCallback(new HandshakeFailureCallback());
                filter.setEnabledProtocols(sslProtocols);
                filter.setEnabledCipherSuites(sslCiphers);
                filterChain.addFirst("ssl", filter);
            }
        }

        Producer x = wrappedEndpoint.createProducer();
        x = new ProducerStringProcessorInterceptor(this, x);
        if (isSupportUnsolicitedFragmentation()) {
            x = new ProducerRequestFragmenterInterceptor(this, x);
        }
        x = isSupportInteractiveContinuation() 
                ? new ProducerMarshalAndInteractiveResponseReceiverInterceptor(this, x)
                : new ProducerMarshalInterceptor(this, x);
        x = new ProducerOutputAcceptanceInterceptor(this, x);
        if (isAudit()) {
            x = new ProducerAuditInterceptor(this, x);
        }
        x = new ProducerInputAcceptanceInterceptor(this, x);
        x = new ProducerAdaptingInterceptor(this, x);
        return x;
    }


    private class HandshakeFailureCallback implements HandshakeCallbackSSLFilter.Callback {
        @Override
        public void run(IoSession session) {
            if (isAudit()) {
                String hostAddress = session.getRemoteAddress().toString();
                getServerAuditStrategy().auditAuthenticationNodeFailure(hostAddress);
            }
        }
    }
    
    
    // ----- getters -----
    
    /**
     * Returns <tt>true</tt> when ATNA auditing should be performed.
     */
    public boolean isAudit() { 
        return audit;
    }
    
    /**
     * Returns <tt>true</tt> when incomplete ATNA auditing records are allowed as well.
     */
    public boolean isAllowIncompleteAudit() { 
        return allowIncompleteAudit;
    }
    
    /**
     * Returns client-side audit strategy instance.
     */
    public MllpAuditStrategy getClientAuditStrategy() {
        return clientStrategy;
    }

    /**
     * Returns server-side audit strategy instance.
     */
    public MllpAuditStrategy getServerAuditStrategy() {
        return serverStrategy;
    }
    
    /**
     * Returns transaction configuration.
     */
    public MllpTransactionConfiguration getTransactionConfiguration() {
        return transactionConfiguration;
    }

    /**
     * Returns HL7 parser instance.
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * Returns <code>true</code> if this endpoint supports interactive continuation.
     */
    public boolean isSupportInteractiveContinuation() {
        return supportInteractiveContinuation;
    }

    /**
     * Returns <code>true</code> if this endpoint supports unsolicited message fragmentation.
     */
    public boolean isSupportUnsolicitedFragmentation() {
        return supportUnsolicitedFragmentation;
    }

    /**
     * Returns <code>true</code> if this endpoint supports segment fragmentation.
     */
    public boolean isSupportSegmentFragmentation() {
        return supportSegmentFragmentation;
    }

    /**
     * Returns default threshold for interactive continuation 
     * (relevant on consumer side only).
     * <p>
     * This value will be used when interactive continuation is generally supported 
     * by this endpoint and is particularly applicable for the current response message,   
     * and the corresponding request message does not set the records count threshold 
     * explicitly (RCP-2-1==integer, RCP-2-2=='RD').
     */
    public int getInteractiveContinuationDefaultThreshold() {
        return interactiveContinuationDefaultThreshold;
    }

    /**
     * Returns threshold for unsolicited message fragmentation 
     * (relevant on producer side only).
     */
    public int getUnsolicitedFragmentationThreshold() {
        return unsolicitedFragmentationThreshold;
    }

    /**
     * Returns threshold for segment fragmentation. 
     */
    public int getSegmentFragmentationThreshold() {
        return segmentFragmentationThreshold;
    }

    /**
     * Returns the interactive continuation storage bean. 
     */
    public InteractiveContinuationStorage getInteractiveContinuationStorage() {
        return interactiveContinuationStorage;
    }

    /**
     * Returns the unsolicited fragmentation storage bean. 
     */
    public UnsolicitedFragmentationStorage getUnsolicitedFragmentationStorage() {
        return unsolicitedFragmentationStorage;
    }

    /**
     * Returns true, when the producer should automatically send a cancel
     * message after it has colelcted all interactive continuation pieces.
     */
    public boolean isAutoCancel() {
        return autoCancel;
    }

    /**
     * Returns HL7v2 NAK factory for this enspoint.
     */
    public NakFactory getNakFactory() {
        return nakFactory;
    }

    // ----- dumb delegation, nothing interesting below -----

    @SuppressWarnings("unchecked")
    @Override
    public void configureProperties(Map options) {
        wrappedEndpoint.configureProperties(options);
    }

    @Override
    public Exchange createExchange() {
        return wrappedEndpoint.createExchange();
    }

    @Override
    public Exchange createExchange(Exchange exchange) {
        return wrappedEndpoint.createExchange(exchange);
    }

    @Override
    public Exchange createExchange(ExchangePattern pattern) {
        return wrappedEndpoint.createExchange(pattern);
    }

    public Exchange createExchange(IoSession session, Object payload) {
        return wrappedEndpoint.createExchange(session, payload);
    }

    @Override
    public PollingConsumer createPollingConsumer() throws Exception {
        return wrappedEndpoint.createPollingConsumer();
    }

    @Override
    public boolean equals(Object object) {
        return wrappedEndpoint.equals(object);
    }

    public IoAcceptor getAcceptor() {
        return wrappedEndpoint.getAcceptor();
    }

    public IoAcceptorConfig getAcceptorConfig() {
        return wrappedEndpoint.getAcceptorConfig();
    }

    public SocketAddress getAddress() {
        return wrappedEndpoint.getAddress();
    }

    @Override
    public CamelContext getCamelContext() {
        return wrappedEndpoint.getCamelContext();
    }

    @Override
    public Component getComponent() {
        return wrappedEndpoint.getComponent();
    }

    public MinaConfiguration getConfiguration() {
        return wrappedEndpoint.getConfiguration();
    }

    public IoConnector getConnector() {
        return wrappedEndpoint.getConnector();
    }

    public IoConnectorConfig getConnectorConfig() {
        return wrappedEndpoint.getConnectorConfig();
    }

    @Override
    public String getEndpointKey() {
        return wrappedEndpoint.getEndpointKey();
    }

    @Override
    public String getEndpointUri() {
        return wrappedEndpoint.getEndpointUri();
    }

    @Override
    public ExchangePattern getExchangePattern() {
        return wrappedEndpoint.getExchangePattern();
    }

    @Override
    public Class<Exchange> getExchangeType() {
        return wrappedEndpoint.getExchangeType();
    }

    @Override
    public int hashCode() {
        return wrappedEndpoint.hashCode();
    }

    @Override
    public boolean isLenientProperties() {
        return wrappedEndpoint.isLenientProperties();
    }

    @Override
    public boolean isSingleton() {
        return wrappedEndpoint.isSingleton();
    }

    public void setAcceptor(IoAcceptor acceptor) {
        wrappedEndpoint.setAcceptor(acceptor);
    }

    public void setAcceptorConfig(IoAcceptorConfig acceptorConfig) {
        wrappedEndpoint.setAcceptorConfig(acceptorConfig);
    }

    public void setAddress(SocketAddress address) {
        wrappedEndpoint.setAddress(address);
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        wrappedEndpoint.setCamelContext(camelContext);
    }

    public void setConfiguration(MinaConfiguration configuration) {
        wrappedEndpoint.setConfiguration(configuration);
    }

    public void setConnector(IoConnector connector) {
        wrappedEndpoint.setConnector(connector);
    }

    public void setConnectorConfig(IoConnectorConfig connectorConfig) {
        wrappedEndpoint.setConnectorConfig(connectorConfig);
    }

    @Override
    public void setEndpointUriIfNotSpecified(String value) {
        wrappedEndpoint.setEndpointUriIfNotSpecified(value);
    }

    @Override
    public void setExchangePattern(ExchangePattern exchangePattern) {
        wrappedEndpoint.setExchangePattern(exchangePattern);
    }

    @Override
    public String toString() {
        return wrappedEndpoint.toString();
    }
}
