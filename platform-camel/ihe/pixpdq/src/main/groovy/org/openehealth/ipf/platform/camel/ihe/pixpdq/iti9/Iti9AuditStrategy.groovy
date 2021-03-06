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
package org.openehealth.ipf.platform.camel.ihe.pixpdq.iti9

import org.apache.camel.Exchange;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditDataset;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditStrategy;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.AuditUtils;
import org.openehealth.ipf.modules.hl7.message.MessageUtils;

/**
 * Generic audit strategy for ITI-9 (PIX Query).
 * @author Dmytro Rud
 */
abstract class Iti9AuditStrategy implements MllpAuditStrategy {
    
    String[] getNecessaryFields(String messageType) {
        ['Payload', 'PatientIds'] as String[]
    }

    
    void enrichAuditDatasetFromRequest(MllpAuditDataset auditDataset, MessageAdapter msg, Exchange exchange) {
        if(msg.QPD?.value) {
            def patientId = MessageUtils.pipeEncode(msg.QPD[3].target)
            if(patientId) { 
                auditDataset.patientIds = [patientId]
            }
        }

        // request message as String
        auditDataset.payload = AuditUtils.getRequestString(exchange, msg)
    }

    
    void enrichAuditDatasetFromResponse(MllpAuditDataset auditDataset, MessageAdapter msg) {
        if((msg.MSH[9][1].value == 'RSP') && 
           (msg.MSH[9][2].value == 'K23') && 
           (msg.QUERY_RESPONSE?.PID?.value)) 
        {
            def patientIds = AuditUtils.pidList(msg.QUERY_RESPONSE.PID[3])
            if(( ! auditDataset.patientIds) || patientIds.contains(auditDataset.patientIds[0])) {
                auditDataset.patientIds = patientIds
            } else {
                auditDataset.patientIds = patientIds + auditDataset.patientIds[0]
            }
        }
    }
}
