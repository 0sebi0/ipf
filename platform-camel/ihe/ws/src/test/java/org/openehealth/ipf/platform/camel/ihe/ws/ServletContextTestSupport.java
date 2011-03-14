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
package org.openehealth.ipf.platform.camel.ihe.ws;

import static org.openehealth.ipf.commons.ihe.ws.server.ServletServer.getFreePort;
import static org.openehealth.ipf.platform.camel.core.util.Exchanges.resultMessage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.AfterClass;
import org.springframework.core.io.ClassPathResource;

/**
 * Base class for tests that are run within an embedded web container.
 * 
 * @see ContextTestSupport
 * @author Mitko Kolev
 */
public abstract class ServletContextTestSupport extends ContextTestSupport {

    private static ServletContainerHolder container = new ServletContainerHolder();

    public ServletContextTestSupport() {
        super();
    }

    private static int PORT = getFreePort();

    @Override
    protected void setUp() throws Exception {
        //do the server start here to be able to override the config in the test class
        if (!container.isServerStarted()) {
            container.startServer(new CXFServlet(), getAppContext(),
                    getServerPort(), isServerSecure());
        }
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Override this method to use a custom server port.
     * 
     * @return a random free port.
     */
    protected int getServerPort() {
        return PORT;
    }

    /**
     * Override this method to use a secure server.
     * 
     * @return <code>false</code> by default.
     */
    public boolean isServerSecure() {
        return false;
    }

    /**
     * Override this method to use custom application context.
     * 
     * @return the default application context;
     */
    public String getAppContext() {
        return "cxf-only-context.xml";
    }

    /**
     * Helper DSL method to set the out body.
     * 
     * @param body
     *            an Object
     * @return a processor that sets the output body.
     */
    protected <T> Processor setOutBody(final T body) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                resultMessage(exchange).setBody(body);
            }
        };
    }

    /**
     * Load the content of a classpath resource. The resource is read with UTF-8
     * encoding.
     * 
     * @param path
     *            a path to a classpath resource
     * @return a String representation of the resource.
     */
    public static String load(String path) {
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

    @AfterClass
    public static void stopServer() {
        if (container.isServerStarted()) {
            container.stopServer();
        }
    }
}
