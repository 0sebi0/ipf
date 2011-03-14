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
package org.openehealth.ipf.platform.camel.ihe.hl7v2ws.pcd01;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultWsComponent;

/**
 * The Camel component for the IHE PCD-01 transaction.
 */
public class Pcd01Component extends DefaultWsComponent {
    @SuppressWarnings("unchecked") // Required because of base class
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        return new Pcd01Endpoint(uri, remaining, this, getCustomInterceptors(parameters));
    }
}
