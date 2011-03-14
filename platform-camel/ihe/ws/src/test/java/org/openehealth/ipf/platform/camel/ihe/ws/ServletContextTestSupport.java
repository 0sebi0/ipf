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

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.camel.ContextTestSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.AfterClass;
import org.openehealth.ipf.commons.ihe.ws.server.JettyServer;
import org.openehealth.ipf.commons.ihe.ws.server.ServletServer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Base class for tests that are run within an embedded web container.
 * 
 * @see ContextTestSupport
 * @author Mitko Kolev
 */
public abstract class ServletContextTestSupport extends ContextTestSupport {
    private static final transient Log LOG = LogFactory
            .getLog(ServletContextTestSupport.class);

    private static ServletServer servletServer;

    protected static ApplicationContext appContext;
    private static int port;

    public ServletContextTestSupport(){
        super();
    }
    
    @Override
    protected void setUp() throws Exception { 
        super.setUp();
    }
    
    
    @Override
    protected void tearDown() throws Exception { 
        super.tearDown();
    }
    
    public static int getPort(){
        return port;
    }
    
    public static void startServer(Servlet servlet, String appContextName,
            boolean secure) {
        ClassPathResource contextResource = new ClassPathResource(
                appContextName);

        port = JettyServer.getFreePort();
        LOG.info("Publishing services on port: " + port);
        servletServer = new JettyServer();

        try {
            servletServer.setContextResource(contextResource.getURI()
                    .toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        servletServer.setPort(port);
        servletServer.setContextPath("");
        servletServer.setServletPath("/*");
        servletServer.setServlet(servlet);
        servletServer.setKeystoreFile("keystore");
        servletServer.setKeystorePass("changeit");
        servletServer.setTruststoreFile("keystore");
        servletServer.setTruststorePass("changeit");

        servletServer.start();

        ServletContext servletContext = servlet.getServletConfig()
                .getServletContext();
        appContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext);
    }

    public static void startServerCXF(String appContextName, boolean secure) {
        startServer(new CXFServlet(), appContextName, secure);
    }

    public static void startServerCXF(String appContextName) {
        startServer(new CXFServlet(), appContextName, false);
    }

    public static void startServerCXF() {
        startServer(new CXFServlet(), "cxf-only-context.xml", false);
    }

    public static void startServer(Servlet servlet, String appContextName) {
        startServer(servlet, appContextName, false);
    }

    @AfterClass
    public static void stopServer() {
        if (servletServer != null) {
            servletServer.stop();
        }
    }
}
