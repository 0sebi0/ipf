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
package ccdbuilders

import groovytools.builder.*

// The Continuity of Care document

continuityOfCareDocument(schema:'clinicalDocument') {
   properties {
      code(schema:'loincCode', def: {
         getMetaBuilder().build {
            loincCode(code:'34133-9',
	                   displayName:'Summarization of episode note')
         }
	   })
      component(schema:'ccd_component')
	}

	collections {
	   templateIds(collection:'templateId', def: {
	       getMetaBuilder().buildList {
	         ii(root:'2.16.840.1.113883.3.27.1776')
	      }
	   })
	   {
	      templateId(schema:'ii')
	   }
	}
}


ccd_component(schema:'component') {
    properties {
       structuredBody(schema:'ccd_structuredBody')
    }
 }

//  Add CCD sections as properties

 ccd_structuredBody(schema:'structuredBody') {
    properties {
       purpose(schema:'ccd_purpose')
       payers(schema:'ccd_payers')
       // ...
    }
 }

