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

import groovy.util.XmlSlurper;

import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.openehealth.ipf.platform.camel.ihe.ws.ServletContextTestSupport;
/**
 * 
 * @author Mitko Kolev
 * 
 */
public class PCD01Test extends ServletContextTestSupport {
    
    private static final String SPEC_REQUEST = load("pcd01/pcd01-request.hl7v2");
    private static final String SPEC_RESPONSE = load("pcd01/pcd01-response.hl7v2");
    
      
    @Test
    public void testHappyCase() throws Exception {
        String url = "pcd-pcd01://localhost:" + getServerPort()
                + "/devicedata";
        Object response = template.requestBody(url, SPEC_REQUEST);
        assertEquals(SPEC_RESPONSE, response);
    }
    
    @Test
    public void testApplicationError() throws Exception {
        String url = "pcd-pcd01://localhost:" + getServerPort()
                + "/exception";
        String response = template.requestBody(url, SPEC_REQUEST, String.class);
        assertTrue(response.startsWith("MSH|^~\\&|"));
        assertTrue(response.contains("java.lang.RuntimeException"));
    }
    
    @Test
    public void testParseError() throws Exception {
        String url = "pcd-pcd01://localhost:" + getServerPort()
                + "/devicedata";
        String response = template.requestBody(url, SPEC_REQUEST.replace("MSH", "ERROR"), String.class);
        assertTrue(response.startsWith("MSH|^~\\&|"));
        assertTrue(response.contains("Application internal error"));
    }

    @Test
    public void testParserSupportsCarriageReturn() throws Exception {
        XmlSlurper s = new XmlSlurper(false,false);
        s.parseText("<t>" + SPEC_REQUEST.replaceAll("&", "&amp;") + "</t>");
        
    }
    
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("pcd-pcd01:devicedata")
                    .onException(Exception.class).maximumRedeliveries(0).end()
                    .process(setOutBody(SPEC_RESPONSE));
                
                from("pcd-pcd01:exception")
                    .throwException(new RuntimeException())
                    .process(setOutBody(SPEC_RESPONSE));
            }

        };
    }

}
