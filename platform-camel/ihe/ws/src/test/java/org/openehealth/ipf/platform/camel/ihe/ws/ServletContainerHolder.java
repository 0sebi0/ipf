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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehealth.ipf.commons.ihe.ws.server.JettyServer;
import org.openehealth.ipf.commons.ihe.ws.server.ServletServer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Mitko Kolev
 * 
 */
public class ServletContainerHolder {

    private static final Log LOG = LogFactory
            .getLog(ServletContainerHolder.class);
    private ServletServer servletServer;
    protected ApplicationContext appContext;
    private AtomicBoolean serverStarted = new AtomicBoolean(false);

    public void startServer(Servlet servlet, String appContextName, int port,
            boolean secure) {
        ClassPathResource contextResource = new ClassPathResource(
                appContextName);
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
        serverStarted.set(true);
    }

    public void stopServer() {
        if (servletServer != null) {
            servletServer.stop();
            LOG.info("Server stopped");
        }
        serverStarted.set(false);
    }

    public boolean isServerStarted() {
        return serverStarted.get();
    }

}
