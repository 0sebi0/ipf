/*
 * Copyright 2010 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xcpd.iti56.asyncresponse;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3ServiceInfo;
import org.openehealth.ipf.commons.ihe.ws.ItiServiceFactory;
import org.openehealth.ipf.commons.ihe.ws.ItiServiceInfo;
import org.openehealth.ipf.commons.ihe.xcpd.XcpdAsyncResponseServiceFactory;
import org.openehealth.ipf.commons.ihe.xcpd.iti56.Iti56ClientAuditStrategy;
import org.openehealth.ipf.commons.ihe.xcpd.iti56.asyncresponse.Iti56AsyncResponsePortType;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiConsumer;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

/**
 * The Camel endpoint for the ITI-56 async response.
 */
public class Iti56AsyncResponseEndpoint extends DefaultItiEndpoint {
    private final static String NS_URI = "urn:ihe:iti:xcpd:2009";
    private final static Hl7v3ServiceInfo ITI_56_ASYNC_RESPONSE = new Hl7v3ServiceInfo(
            new QName(NS_URI, "RespondingGateway_Response_Service", "xcpd"),
            Iti56AsyncResponsePortType.class,
            new QName(NS_URI, "RespondingGateway_Response_Binding_Soap12", "xcpd"),
            false,
            "wsdl/iti56/iti56-asyncresponse-raw.wsdl",
            null,
            null,
            null,
            false,
            false);
           
    /**
     * Constructs the endpoint.
     * @param endpointUri
     *          the endpoint URI.
     * @param address
     *          the endpoint address from the URI.
     * @param iti56AsyncResponseComponent
     *          the component creating this endpoint.
     * @throws URISyntaxException
     *          if the endpoint URI was not a valid URI.
     */
    public Iti56AsyncResponseEndpoint(
            String endpointUri, 
            String address, 
            Iti56AsyncResponseComponent iti56AsyncResponseComponent,
            InterceptorProvider customInterceptors) throws URISyntaxException 
    {
        super(endpointUri, address, iti56AsyncResponseComponent, customInterceptors);
    }

    public Producer createProducer() throws Exception {
        throw new IllegalStateException("No producer support for asynchronous response endpoints");
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        ItiServiceFactory serviceFactory = new XcpdAsyncResponseServiceFactory(
                ITI_56_ASYNC_RESPONSE,
                isAudit() ? new Iti56ClientAuditStrategy(isAllowIncompleteAudit()) : null,
                getServiceAddress(),
                getCorrelator(),
                getCustomInterceptors());
        ServerFactoryBean serverFactory = 
            serviceFactory.createServerFactory(Iti56AsyncResponseService.class);
        Server server = serverFactory.create();
        DefaultItiWebService service = (DefaultItiWebService) serverFactory.getServiceBean();
        return new DefaultItiConsumer(this, processor, service, server);
    }
}
