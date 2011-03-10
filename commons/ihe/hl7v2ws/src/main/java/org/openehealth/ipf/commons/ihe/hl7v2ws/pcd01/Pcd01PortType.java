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
package org.openehealth.ipf.commons.ihe.hl7v2ws.pcd01;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;

@WebService(targetNamespace = "urn:ihe:pcd:dec:2010", name = "DeviceObservationConsumer_PortType")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface Pcd01PortType {

    @WebResult(name = "CommunicatePCDDataResponse", targetNamespace = "urn:ihe:pcd:dec:2010", partName = "Body")
    @Action(input = "urn:ihe:pcd:2010:CommunicatePCDData", output = "urn:ihe:pcd:2010:CommunicatePCDDataResponse")
    @WebMethod(operationName = "CommunicatePCDData", action = "urn:ihe:pcd:2010:CommunicatePCDData")
    public String communicate(
        @WebParam(partName = "Body", name = "CommunicatePCDData", targetNamespace = "urn:ihe:pcd:dec:2010")
        String request
    );
}
