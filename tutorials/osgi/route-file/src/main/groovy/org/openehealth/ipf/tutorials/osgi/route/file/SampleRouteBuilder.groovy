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
package org.openehealth.ipf.tutorials.osgi.route.file

import org.apache.camel.spring.SpringRouteBuilder
/**
 * @author Martin Krasser
 */
public class SampleRouteBuilder extends SpringRouteBuilder {

     void configure() {

         from('file:workspace/input')
             .initFlow(this.class.package.name).application('osgi-file')
             .unmarshal().ghl7()
             .validate().ghl7()
             .transmogrify('admissionTransmogrifier')
             .dedupeFlow()
             .marshal().ghl7()
             .to('jms:queue:delivery-file')
             
         from('jms:queue:delivery-file')
             .to('file:workspace/output')
             .ackFlow()

     }
    
}
