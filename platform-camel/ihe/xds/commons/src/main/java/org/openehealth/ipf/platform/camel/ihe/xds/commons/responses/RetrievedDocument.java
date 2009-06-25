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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.responses;

import javax.activation.DataHandler;

import org.openehealth.ipf.platform.camel.ihe.xds.commons.requests.RetrieveDocument;

/**
 * A single document retrieved from the repository.
 * @author Jens Riemschneider
 */
public class RetrievedDocument {
    private DataHandler dataHandler;
    private RetrieveDocument requestData;
    
    public DataHandler getDataHandler() {
        return dataHandler;
    }
    
    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
    
    public RetrieveDocument getRequestData() {
        return requestData;
    }

    public void setRequestData(RetrieveDocument requestData) {
        this.requestData = requestData;
    }
}