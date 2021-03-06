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
package org.openehealth.ipf.commons.ihe.ws.cxf.asyncaudit;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.DocLiteralInInterceptor;
import org.apache.cxf.phase.Phase;
import org.openehealth.ipf.commons.ihe.ws.ItiServiceInfo;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.AuditInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditDataset;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditStrategy;

/**
 * CXF interceptor for ATNA auditing in WS-based IHE transactions with
 * WSA asynchrony support.  Handles <b>incoming</b> requests
 * on <b>service</b> (consumer) side.
 *
 * @author Dmytro Rud
 */
public class AsyncAuditInRequestInterceptor extends AuditInterceptor {
    private final ItiServiceInfo serviceInfo;

    /**
     * Constructor.
     */
    public AsyncAuditInRequestInterceptor(WsAuditStrategy auditStrategy, ItiServiceInfo serviceInfo) {
        super(Phase.UNMARSHAL, auditStrategy);
        addAfter(DocLiteralInInterceptor.class.getName());
        this.serviceInfo = serviceInfo;
    }

    
    @Override
    protected void process(SoapMessage message) throws Exception {
        WsAuditDataset auditDataset = getAuditDataset(message);
        extractAddressesFromServletRequest(message, auditDataset);
        
        if (serviceInfo.isAuditRequestPayload()) {
            auditDataset.setRequestPayload(message.getContent(String.class));
        }

        getAuditStrategy().enrichDatasetFromRequest(extractPojo(message), auditDataset);
    }

}
