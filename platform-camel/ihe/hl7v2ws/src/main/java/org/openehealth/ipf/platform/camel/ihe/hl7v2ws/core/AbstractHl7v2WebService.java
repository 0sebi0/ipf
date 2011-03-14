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

import static org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpMarshalUtils.extractMessageAdapter;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehealth.ipf.modules.hl7.AckTypeCode;
import org.openehealth.ipf.modules.hl7.HL7v2Exception;
import org.openehealth.ipf.modules.hl7.message.MessageUtils;
import org.openehealth.ipf.modules.hl7.parser.PipeParser;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpMarshalUtils;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;
import org.xml.sax.SAXException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;

/**
 * Generic implementation of an HL7v2-based Web Service.
 * 
 * @author Dmytro Rud
 * @author Mitko Kolev
 * @author Stefan Ivanov
 */
public class AbstractHl7v2WebService extends DefaultItiWebService {
    private static final Parser PARSER = new PipeParser();
    private static final Log LOG = LogFactory
            .getLog(AbstractHl7v2WebService.class);

    private final Hl7v2wsTransactionConfiguration config;

    public AbstractHl7v2WebService(Hl7v2wsTransactionConfiguration config) {
        super();
        this.config = config;
    }

    protected String doProcess(String requestXmlString){
        try {
            Message hl7v2Request = extractHl7v2Payload(requestXmlString);
            Exchange exchange = super.process(hl7v2Request);

            Exception processingException = exchange.getException();
            if (processingException != null) {
                LOG.info("Creating NAK for exchange exception "  +  processingException.getMessage());
                return createHl7XmlNakResponse(processingException);
            }
            
            MessageAdapter response = extractMessageAdapter(Exchanges.resultMessage(exchange), PARSER);
            //FIXME no need to adapt the message here. a Hl7 message is enough
            return createHl7XmlResponse((Message)response.getTarget());
            
        } catch (Exception formatException) {
            return createHl7XmlNakResponse(formatException);
        }

    }

    /**
     * The HL7 v.2 payload is the text in the root; See section J.2 in (PCD TF-2
     * 
     * @param xmlPayload
     *            the XML in the SOAP body
     * @return the HL7 v.2 payload, which is the text of the root element of the
     *         XML in the SOAP body
     * @throws EncodingNotSupportedException
     * @throws HL7Exception
     */
    public Message extractHl7v2Payload(String xmlPayload)
            throws EncodingNotSupportedException, HL7Exception {
        GPathResult rootNode;
        try {
            rootNode = new XmlSlurper(false, false).parseText(xmlPayload);
            String payload = normalize(rootNode.text());
            return PARSER.parse(payload);
        } catch (IOException e) {
            throw new HL7Exception("Unable to read the XML payload", e);
        } catch (SAXException e) {
            throw new HL7Exception("Unable to parse the XML payload", e);
        } catch (ParserConfigurationException e) {
            throw new HL7Exception("Unable to parse the XML payload", e);
        }

    }

    /**
     * @return the transaction configuration
     */
    public Hl7v2wsTransactionConfiguration getConfiguration() {
        return config;
    }

    private String createHl7XmlResponse(Message message) throws HL7Exception  {

        String msgAsXMLText = toHl7XmlString(message);
        return new StringBuilder()
            .append('<').append(config.getResponseRootElementName())
            .append(" xmlns=\"").append(config.nsUri).append("\">\n")
            .append(msgAsXMLText)
            .append("</").append(config.getResponseRootElementName()).append('>')
            .toString();

    }

    private String normalize(String hl7String) throws SAXException {
        String result = hl7String;
        if (!hl7String.startsWith("MSH")) {
            int headerIndex = hl7String.indexOf("MSH");
            if (headerIndex == -1) {
                throw new SAXException(
                        "The payload does not contain MSH element as expected");
            }
            result = hl7String.substring(headerIndex);
        }
        return result;
    }

    private String toHl7XmlString(Message message) throws HL7Exception {
        String parsed = PARSER.encode(message);
        String specialCharsEscaped = StringEscapeUtils.escapeXml(parsed);
        //replace the \r with its the XML representation, and add a newline for readability 
        return specialCharsEscaped.replaceAll("\r", "&#xD;\n");

    }
    

    private Message createNak(Throwable t) {

        HL7v2Exception hl7e = new HL7v2Exception(
                MllpMarshalUtils.formatErrorMessage(t),
                config.getRequestErrorDefaultErrorCode(), t);

        // FIXME, validate error type and configuration
        Message nak = MessageUtils.defaultNak(hl7e, AckTypeCode.AR,
                config.getHl7Version(), config.getSendingApplication(),
                config.getSendingFacility());
        return nak;
    }

    
    private String createHl7XmlNakResponse(Throwable exception) {
        Message nak = createNak(exception);
        try{
            return toHl7XmlString(nak);
        }catch (HL7Exception e){
            LOG.error("Unable to render the default response NAK message", e);
            return new MessageAdapter(nak).toString();
        }

    }

}
