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
package org.openehealth.ipf.platform.camel.ihe.hl7v2ws.pcd01;

import org.openehealth.ipf.commons.ihe.hl7v2ws.pcd01.Pcd01PortType;
import org.openehealth.ipf.platform.camel.ihe.hl7v2ws.core.AbstractHl7v2WebService;
import org.openehealth.ipf.platform.camel.ihe.hl7v2ws.core.Hl7v2wsTransactionConfiguration;

/**
 * Service implementation for the IHE PCD-01 transaction.
 * @author Dmytro Rud
 */
public class Pcd01Service extends AbstractHl7v2WebService implements Pcd01PortType {
    // TODO fill parameters
    private static final Hl7v2wsTransactionConfiguration CONFIG = new Hl7v2wsTransactionConfiguration(
    );


    public Pcd01Service() {
        super(CONFIG);
    }


    @Override
    public String communicate(String requestString) {
        return doProcess(requestString);
    }
}
