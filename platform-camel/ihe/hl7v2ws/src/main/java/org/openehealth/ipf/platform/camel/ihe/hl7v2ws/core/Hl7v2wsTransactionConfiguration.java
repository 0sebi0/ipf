/*
 * Copyright 2011 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.hl7v2ws.core;

import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTransactionConfiguration;

/**
 * @author Dmytro Rud
 * @author Mitko Kolev
 */
public class Hl7v2wsTransactionConfiguration extends MllpTransactionConfiguration {

    // TODO: combine this with org.openehealth.ipf.commons.ihe.ws.ItiServiceInfo,
    // which is used in *Endpoint classes

    final String requestRootElementName;
    final String responseRootElementName;
    final String nsUri;
    
    

    public Hl7v2wsTransactionConfiguration(
            String hl7Version,
            String sendingApplication,
            String sendingFacility,
            int requestErrorDefaultErrorCode,
            int responseErrorDefaultErrorCode,
            String[] allowedRequestMessageTypes,
            String[] allowedRequestTriggerEvents,
            String[] allowedResponseMessageTypes,
            String[] allowedResponseTriggerEvents,
            String incomingRootElementName,
            String outgoingRootElementName,
            String nsUri)
    {
        super(hl7Version,
                sendingApplication,
                sendingFacility,
                requestErrorDefaultErrorCode,
                responseErrorDefaultErrorCode,
                allowedRequestMessageTypes,
                allowedRequestTriggerEvents,
                allowedResponseMessageTypes,
                allowedResponseTriggerEvents,
                NO_AUDITING,
                NO_RESPONSE_CONTINUATIONS);

        this.requestRootElementName = incomingRootElementName;
        this.responseRootElementName = outgoingRootElementName;
        this.nsUri = nsUri;
    }


    public String getRequestRootElementName() {
        return requestRootElementName;
    }

    public String getResponseRootElementName() {
        return responseRootElementName;
    }

    public String getNamespaceUri() {
        return nsUri;
    }
}
