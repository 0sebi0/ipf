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
package org.openehealth.ipf.platform.camel.ihe.hl7v2ws.pcd01;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.openehealth.ipf.commons.ihe.hl7v2ws.pcd01.Pcd01PortType;
import org.openehealth.ipf.commons.ihe.ws.ItiClientFactory;
import org.openehealth.ipf.commons.ihe.ws.ItiServiceFactory;
import org.openehealth.ipf.commons.ihe.ws.ItiServiceInfo;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiConsumer;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

import javax.xml.namespace.QName;

/**
 * Camel endpoint for the PCD-01 transaction.
 */
public class Pcd01Endpoint extends DefaultItiEndpoint {
    private static final String NS_URI = "urn:ihe:pcd:dec:2010";
    public static final ItiServiceInfo PCD01 = new ItiServiceInfo(
            new QName(NS_URI, "DeviceObservationConsumer_Service", "ihe"),
            Pcd01PortType.class,
            new QName(NS_URI, "DeviceObservationConsumer_Binding_Soap12", "ihe"),
            false,
            "wsdl/pcd01/pcd01-raw.wsdl",
            true,
            false,
            false,
            true);

    /**
     * Constructs the endpoint.
     * @param endpointUri
     *          the endpoint URI.
     * @param address
     *          the endpoint address from the URI.
     * @param component
     *          the component creating this endpoint.
     */
    public Pcd01Endpoint(
            String endpointUri,
            String address,
            Pcd01Component component,
            InterceptorProvider customInterceptors) 
    {
        super(endpointUri, address, component, customInterceptors);
    }

    @Override
    public Producer createProducer() throws Exception {
        ItiClientFactory clientFactory = new ItiClientFactory(
                PCD01,
                getServiceUrl(), 
                getCustomInterceptors());
        return new Pcd01Producer(this, clientFactory);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        ItiServiceFactory serviceFactory = new ItiServiceFactory(
                PCD01,
                getServiceAddress(),
                getCustomInterceptors());
        ServerFactoryBean serverFactory =
            serviceFactory.createServerFactory(Pcd01Service.class);
        Server server = serverFactory.create();
        DefaultItiWebService service = (DefaultItiWebService) serverFactory.getServiceBean();
        return new DefaultItiConsumer(this, processor, service, server);
    }
}
