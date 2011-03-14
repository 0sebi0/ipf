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

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openehealth.ipf.platform.camel.ihe.hl7v2ws.core.Hl7v2wsTransactionConfiguration;
import org.openehealth.ipf.platform.camel.ihe.hl7v2ws.pcd01.Pcd01Service;

/**
 * @author Mitko Kolev
 *
 */
public class Pcd01ServiceConfigurationTest {

    private Hl7v2wsTransactionConfiguration configuration = new Pcd01Service()
            .getConfiguration();

    @Test
    public void testAudit() {
        String[] requestTypes = configuration.getAllowedRequestMessageTypes();
        // the service does not use auditing
        for (String type : requestTypes) {
            assertFalse(configuration.isAuditable(type));
        }
    }

    @Test
    public void testResponseContinuations() {
        String[] requestTypes = configuration.getAllowedResponseMessageTypes();
        // the transaction does not use continuations
        for (String type : requestTypes) {
            assertFalse(configuration.isContinuable(type));
        }
    }
}
