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
package org.openehealth.ipf.commons.ihe.xcpd.iti56.asyncresponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.Action;

/**
 * SEI for asynchronous ITI-56 response receiver. 
 * @author Dmytro Rud
 */
@WebService(targetNamespace = "urn:ihe:iti:xcpd:2009", name = "RespondingGateway_Response_PortType")
@SOAPBinding(style = Style.DOCUMENT, parameterStyle = ParameterStyle.BARE)
public interface Iti56AsyncResponsePortType {

    //@Oneway
    @Action(input = "urn:ihe:iti:2009:PatientLocationQueryResponse")
    @WebMethod(operationName = "RespondingGateway_PatientLocationQuery")
    public Object respondingGatewayPatientLocationQuery(
        @WebParam(partName = "Body", targetNamespace = "urn:ihe:iti:xcpd:2009")
        String response
    );
}
