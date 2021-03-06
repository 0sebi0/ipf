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
package org.openehealth.ipf.modules.hl7.validation.builder

import ca.uhn.hl7v2.validation.Rule
import ca.uhn.hl7v2.validation.ValidationContext

/**
 * @author Christian Ohr
 */
public class VersionBuilder extends RuleBuilder{
	
	String version
	Rule rule
	static def versions = [ '2.1', '2.2', '2.3', '2.3.1', '2.4', '2.5', '2.5.1', '2.6']
	
	VersionBuilder(ValidationContext context) {
		super(context)	    
	}
	
	VersionBuilder(String version, ValidationContext context) {
	    this(context)
	    if (!version) {
	        throw IllegalArgumentException("Version must not be empty")
	    } else if (version == '*')
		    this.version = versions.join(' ')
		else
		    this.version = version
	}
	
    /**
     * @param version the oldest version the rules apply to
     * @return restricts the rules to message versions starting with the passed version
     */
	VersionBuilder asOf(String version) {
	    def matchingVersions = versions.findAll { version <= it }
	    this.version = matchingVersions.join(' ')
	    this
	}
	
    /**
     * @param version the oldest version the rules do not apply
     * @return restricts the rules to message versions older than the passed version
     */
	VersionBuilder before(String version) {
	    def matchingVersions = versions.findAll { version > it }
	    this.version = matchingVersions.join(' ')	
	    this
	}

     /**
      * @param version the only version the rules do not apply
      * @return restricts the rules to all message versions but the passed version
      */
	VersionBuilder except(String version) {
	    def matchingVersions = versions.findAll { version != it }
	    this.version = matchingVersions.join(' ')
	    this
	}
	
      /**
       * @param typeName name of the Primitive type
       * @return builder that allows to add a Primitive validation rule
       */
	PrimitiveRuleBuilder type(String typeName) {
		new PrimitiveRuleBuilder(version, context, typeName)
	}	
	
    /**
     * @param messageType
     * @param triggerEvent
     * @return builder that allows to add a Message validation rule
     */
	MessageRuleBuilder message(String messageType, triggerEvent) {
		new MessageRuleBuilder(version, context, messageType, triggerEvent)
	}
	
    /**
     * @param encoding
     * @return builder that allows to add a Encoding validation rule
     */
	EncodingRuleBuilder encoding(String encoding) {
		new EncodingRuleBuilder(version, context, encoding)
	}
	
    /**
     * Adds a description to a rule
     */
	RuleBuilder withDescription(String description) {
		rule?.description = description
		this
	}

    /**
     * Adds a HL7 section reference to a rule
     */
	RuleBuilder withReference(String sectionReference) {
		rule?.sectionReference = sectionReference
		this
	}

	
}
