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

import java.util.Map
import ca.uhn.hl7v2.validation.ValidationContext

/**
 * RuleBuilder together with its subclasses allows the create validation
 * rules using a domain specific language (DSL).
 * 
 * @author Christian Ohr
 */
public class RuleBuilder {
	
     ValidationContext context
	
	RuleBuilder(ValidationContext context) {
		this.context = context
	}
	
	/**
	 * @return a builder that allows to formulate validation rules for the 
	 * given message version.
	 */
	VersionBuilder forVersion(String version) {
		new VersionBuilder(version, context)
	}
	
	/**
	 * @return a builder that allows to formulate validation rules for the 
	 * given Primitive type and message version.
	 */    
	PrimitiveRuleBuilder forVersionAndType(String version, String typeName) {
		new VersionBuilder(version, context).type(typeName)
	}
	
	/**
	 * @return a builder that allows to formulate validation rules for the 
	 * message versions that are yet to be defined.
	 */
	VersionBuilder forVersion() {
		new VersionBuilder(context)
	}
	
	/**
	 * @return a builder that allows to formulate validation rules for all 
	 * message versions
	 */
	VersionBuilder forAllVersions() {
		new VersionBuilder('*', context)
	}
}
