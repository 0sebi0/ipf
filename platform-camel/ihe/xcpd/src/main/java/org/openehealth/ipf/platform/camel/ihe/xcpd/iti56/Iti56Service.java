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
package org.openehealth.ipf.platform.camel.ihe.xcpd.iti56;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.cxf.interceptor.Fault;
import org.openehealth.ipf.commons.ihe.xcpd.iti56.Iti56PortType;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

/**
 * Service implementation for the IHE ITI-56 transaction (XCPD).
 * @author Dmytro Rud
 */
public class Iti56Service extends DefaultItiWebService implements Iti56PortType {

    @Override
    public String respondingGatewayPatientLocationQuery(String request) {
        Exchange result = process(request);
        if(result.getException() != null) {
            throw new Fault(result.getException());
        }
        
        Message resultMessage = Exchanges.resultMessage(result);
        return resultMessage.getBody(String.class);
    }

}
