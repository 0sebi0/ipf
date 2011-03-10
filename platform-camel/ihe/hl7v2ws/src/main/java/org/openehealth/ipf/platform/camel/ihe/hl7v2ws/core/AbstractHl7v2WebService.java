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

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import org.apache.camel.Exchange;
import org.openehealth.ipf.modules.hl7.parser.PipeParser;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

/**
 * Generic implementation of an HL7v2-based Web Service.
 * @author Dmytro Rud
 */
public class AbstractHl7v2WebService extends DefaultItiWebService {
    private static final Parser PARSER = new PipeParser();

    private final Hl7v2wsTransactionConfiguration config;


    public AbstractHl7v2WebService(Hl7v2wsTransactionConfiguration config) {
        super();
        this.config = config;
    }


    protected String doProcess(String requestString) {
        Exception exception = null;
        Message requestMessage, responseMessage;

        try {
            requestMessage = PARSER.parse(requestString);
        } catch (EncodingNotSupportedException e) {
            // TODO: reuse org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.consumer.ConsumerMarshalInterceptor.processUnmarshallingException()
            Message nak = createNak(e);
            return createXmlResponse(nak);
        } catch (HL7Exception e) {
            // TODO: reuse org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.consumer.ConsumerMarshalInterceptor.processUnmarshallingException()
            Message nak = createNak(e);
            return createXmlResponse(nak);
        }

        Exchange exchange = super.process(requestMessage);
        Object o = Exchanges.resultMessage(exchange).getBody();

        // TODO: reuse org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpMarshalUtils.extractMessageAdapter()
        if(exchange.getException() != null) {
            exception = exchange.getException();
        }
        else if (o instanceof Message) {
            responseMessage = (Message) o;
        }
        else if (o instanceof MessageAdapter) {
            responseMessage = (Message) ((MessageAdapter) o).getTarget();
        }
        else {
            throw new RuntimeException("cannot process " +
                    ((o == null) ? "null" : o.getClass().getName()));
        }

        return createXmlResponse(responseMessage);
    }



    private MessageAdapter extractMessageAdapter(String xmlPayload) {
        // TODO
    }


    private String createXmlResponse(Message message) {
        return new StringBuilder()
                .append('<')
                .append(config.getRequestRootElementName())
                .append(" xmlns=\"")
                .append(config.nsUri)
                .append("\">\n")
                .append(renderHl7(message))
                .append("\n</")
                .append(config.getRequestRootElementName())
                .append('>')
                .toString();
    }


    private String renderHl7(Message message) {
        try {
            return PARSER.encode(message);
            // TODO: post-process, quote special symbols
        } catch (HL7Exception e) {
            throw new RuntimeException(e);
        }
    }

}
