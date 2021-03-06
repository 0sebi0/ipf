/*
 * Copyright 2010 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.hl7v3;

import org.apache.cxf.helpers.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openehealth.ipf.commons.core.modules.api.ValidationException;

public class HL7v3ValidatorTest {

	@Test
	public void testValidateOk() throws Exception {
		String message = IOUtils.readStringFromStream(getClass()
				.getResourceAsStream("/xsd/prpa-valid.xml"));
        Hl7v3Validator validator = new Hl7v3Validator();
        validator.validate(message, Hl7v3ValidationProfiles.getREQUEST_TYPES().get("iti-44"));
	}
	
	@Test
	public void testValidateError() throws Exception {
		String message = IOUtils.readStringFromStream(getClass()
				.getResourceAsStream("/xsd/prpa-invalid.xml"));
		Hl7v3Validator validator = new Hl7v3Validator();
        boolean failed = false;
        try {
			validator.validate(message, Hl7v3ValidationProfiles.getREQUEST_TYPES().get("iti-44"));
		} catch (ValidationException e) {
		    failed = true;
		}
		Assert.assertTrue(failed);
	}

}
