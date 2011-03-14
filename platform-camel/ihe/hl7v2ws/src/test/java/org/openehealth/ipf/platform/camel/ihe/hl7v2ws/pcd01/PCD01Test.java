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

import static org.openehealth.ipf.platform.camel.core.util.Exchanges.resultMessage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openehealth.ipf.platform.camel.ihe.ws.ServletContextTestSupport;
import org.springframework.core.io.ClassPathResource;
/**
 * @author Mitko Kolev
 * 
 */
public class PCD01Test extends ServletContextTestSupport {
    
    private static final String REQUEST = loadClasspathResource("pcd01/pcd01-request.hl7v2.xml");
    private static final String RESPONSE = loadClasspathResource("pcd01/pcd01-response.hl7v2.xml");

    public PCD01Test() {
        startServerCXF();
    }

   
    @Test
    public void testHappyCase() {
        String url = "pcd-pcd01://localhost:" + getPort()
                + "/devicedata";
        System.out.println(REQUEST);
        
        Object response = template.requestBody(url, REQUEST);
        System.out.println(response);
        
        assertNotNull(response);
    }

    
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("pcd-pcd01:devicedata").onException(Exception.class)
                        .maximumRedeliveries(0).end()
                        
                        .process(setOutBody(RESPONSE));
            }

        };
    }

    private Processor setOutBody(final String body) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                resultMessage(exchange).setBody(body);
            }
        };
    }

    private static String loadClasspathResource(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        InputStream stream = null;
        try {
            stream = resource.getInputStream();
            return IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
