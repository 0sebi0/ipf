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

import org.apache.camel.ExchangePattern;
import org.openehealth.ipf.commons.ihe.xcpd.iti56.asyncresponse.Iti56AsyncResponsePortType;
import org.openehealth.ipf.platform.camel.ihe.ws.AsynchronousResponseItiWebService;

/**
 * Service implementation for the IHE ITI-56 (XCPD) async response.
 * @author Dmytro Rud
 */
public class Iti56AsyncResponseService extends AsynchronousResponseItiWebService implements Iti56AsyncResponsePortType {

    @Override
    public Object respondingGatewayPatientLocationQuery(String response) {
        process(response, null, ExchangePattern.InOnly);
        return null;
    }
}
