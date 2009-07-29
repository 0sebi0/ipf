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
package builders.content.section

import org.openhealthtools.ihe.common.cdar2.*

// 3.8.2.4.1.1 Reaction observation

// CONF-280: An alert observation MAY contain one or more reaction observations 
//           (templateId 2.16.840.1.113883.10.20.1.54), each of which MAY contain
//           exactly one severity observation (templateId 2.16.840.1.113883.10.20.1.55) 
//           AND/OR one or more reaction interventions.
// CONF-282: A reaction observation (templateId 2.16.840.1.113883.10.20.1.54) SHALL 
//           be represented with Observation.
// CONF-283: The value for �Observation / @classCode� in a reaction observation SHALL 
//           be �OBS� 2.16.840.1.113883.5.6 ActClass STATIC.
ccd_reactionObservation(schema:'observation'){
  properties{
      // CONF-284: The value for �Observation / @moodCode� in a reaction observation
      //           SHALL be �EVN� 2.16.840.1.113883.5.1001 ActMood STATIC.
      moodCode(def:XActMoodDocumentObservation.EVN_LITERAL)
      // CONF-285: A reaction observation SHALL include exactly one Observation / statusCode.
      // CONF-286: The value for �Observation / statusCode� in a reaction observation
      //           SHALL be �completed� 2.16.840.1.113883.5.14 ActStatus STATIC.
      statusCode(req:true, def: {
          getMetaBuilder().build {
              cs('completed')
          }
      })
      severityObservation(schema:'ccd_severityObservation')
      // CONF-297: A reaction intervention SHALL be represented as
      //           a procedure activity (templateId 2.16.840.1.113883.10.20.1.29),
      //           a medication activity (templateId 2.16.840.1.113883.10.20.1.24),
      //           or some other clinical statement.
      //TODO capability to add procedure and medication activity needed
  }
  collections{
      templateIds(collection:'templateId', def: {
          getMetaBuilder().buildList {
              ii(root:'2.16.840.1.113883.10.20.1.54')
          }
      })
  }
}


// CONF-287: A severity observation (templateId 2.16.840.1.113883.10.20.1.55) SHALL be
//           represented with Observation.
// CONF-289: The value for �Observation / @classCode� in a severity observation
//           SHALL be �OBS� 2.16.840.1.113883.5.6 ActClass STATIC.
ccd_severityObservation(schema:'observation'){
    properties{
        // CONF-290: The value for �Observation / @moodCode� in a severity observation 
        //           SHALL be �EVN� 2.16.840.1.113883.5.1001 ActMood STATIC.
        moodCode(def:XActMoodDocumentObservation.EVN_LITERAL)
        // CONF-291: A severity observation SHALL include exactly one Observation / statusCode.
        // CONF-292: The value for �Observation / statusCode� in a severity observation 
        //           SHALL be �completed� 2.16.840.1.113883.5.14 ActStatus STATIC.
        statusCode(req:true, def: {
            getMetaBuilder().build {
                cs('completed')
            }
        })
        // CONF-293: A severity observation SHALL contain exactly one Observation / code.
        // CONF-294: The value for �Observation / code� in a severity observation 
        //           SHALL be �SEV� �Severity observation� 2.16.840.1.113883.5.4 ActCode STATIC.
        code(def: {
            getMetaBuilder().build{
                actCode(code:'SEV',
                      displayName:'Severity observation')
            }
        })
    }
    collections{
        templateIds(collection:'templateId', def: {
            getMetaBuilder().buildList {
                ii(root:'2.16.840.1.113883.10.20.1.55')
            }
        })
        // CONF-295: A severity observation SHALL contain exactly one Observation / value.
        values(collection:'value', min:1, max:1)
    }
}

