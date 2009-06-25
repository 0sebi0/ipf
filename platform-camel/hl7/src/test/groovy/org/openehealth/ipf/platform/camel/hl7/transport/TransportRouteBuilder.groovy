/*
 * Copyright 2008 the original author or authors.
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
package org.openehealth.ipf.platform.camel.hl7.transport

import org.apache.camel.spring.SpringRouteBuilder
/**
 * @author Martin Krasser
 */
class TransportRouteBuilder extends SpringRouteBuilder {
    
    void configure() {

        from('mina:tcp://127.0.0.1:8888?sync=true&codec=hl7Codec')
            .unmarshal().ghl7()
            .transmogrify {it.OBXNTE.OBX[5][5]}
            .to('mock:output')
            .transmogrify {''}

        from('mina:tcp://127.0.0.1:8889?sync=true&codec=hl7Codec')
            .unmarshal().ghl7()
            .to('mock:output')
            .transmogrify {''}

    }
    
}