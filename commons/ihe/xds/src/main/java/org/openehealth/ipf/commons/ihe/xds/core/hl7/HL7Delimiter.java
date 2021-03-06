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
package org.openehealth.ipf.commons.ihe.xds.core.hl7;

/**
 * Contains all separator characters used in HL7.
 * @author Jens Riemschneider
 */
public enum HL7Delimiter {
    /** HL7 field delimiter: | */
    FIELD("|", "\\F\\"),
    /** HL7 component delimiter: ^ */
    COMPONENT("^", "\\S\\"),
    /** HL7 subcomponent delimiter: & */
    SUBCOMPONENT("&", "\\T\\"),
    /** HL7 repetition delimiter: ~ */
    REPETITION("~", "\\R\\"),
    /** HL7 escape character: \ */
    ESCAPE("\\", "\\E\\");
    
    private final String value;
    private final String substitude;
    
    private HL7Delimiter(String value, String substitude) {
        this.value = value;
        this.substitude = substitude;
    }

    /**
     * @return the string representation of the delimiter.
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the substitute used to escape the delimiter's string representation. 
     */
    public String getSubstitute() {
        return substitude;
    }
}
