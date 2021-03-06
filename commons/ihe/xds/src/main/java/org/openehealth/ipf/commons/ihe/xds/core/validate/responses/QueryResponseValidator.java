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
package org.openehealth.ipf.commons.ihe.xds.core.validate.responses;

import static org.apache.commons.lang.Validate.notNull;
import org.openehealth.ipf.commons.core.modules.api.Validator;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.*;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.ObjectReference;
import static org.openehealth.ipf.commons.ihe.xds.core.metadata.Vocabulary.*;
import static org.openehealth.ipf.commons.ihe.xds.core.validate.ValidationMessage.*;
import org.openehealth.ipf.commons.ihe.xds.core.validate.ValidationProfile;
import static org.openehealth.ipf.commons.ihe.xds.core.validate.ValidatorAssertions.metaDataAssert;
import org.openehealth.ipf.commons.ihe.xds.core.validate.XDSMetaDataException;
import org.openehealth.ipf.commons.ihe.xds.core.validate.requests.ObjectContainerValidator;

import java.util.List;

/**
 * Validate a {@link EbXMLQueryResponse}.
 * @author Jens Riemschneider
 */
public class QueryResponseValidator implements Validator<EbXMLQueryResponse, ValidationProfile> {
    private final RegistryResponseValidator regResponseValidator = new RegistryResponseValidator();
    private final ObjectContainerValidator objectContainerValidator = new ObjectContainerValidator();

    @Override
    public void validate(EbXMLQueryResponse response, ValidationProfile profile) {
        notNull(response, "response cannot be null");
        
        ValidationProfile queryProfile = new ValidationProfile(profile);
        queryProfile.setQuery(true);
        
        regResponseValidator.validate(response, queryProfile);        
        objectContainerValidator.validate(response, queryProfile);       

        List<ObjectReference> references = response.getReferences();
        for (ObjectReference objRef : references) {
            metaDataAssert(objRef.getId() != null, MISSING_OBJ_REF);
        }

        validatePatientIdsAreIdentical(response);
    }

    private void validatePatientIdsAreIdentical(EbXMLObjectContainer container) throws XDSMetaDataException {
        String patientId = checkForMultiplePatientIds(null, SUBMISSION_SET_PATIENT_ID_EXTERNAL_ID,
                container.getRegistryPackages(SUBMISSION_SET_CLASS_NODE));

        patientId = checkForMultiplePatientIds(patientId, DOC_ENTRY_PATIENT_ID_EXTERNAL_ID,
                container.getExtrinsicObjects(DOC_ENTRY_CLASS_NODE));

        checkForMultiplePatientIds(patientId, FOLDER_PATIENT_ID_EXTERNAL_ID,
                container.getRegistryPackages(FOLDER_CLASS_NODE));
    }

    private String checkForMultiplePatientIds(String patientId, String id, List<? extends EbXMLRegistryObject> entries) {
        for (EbXMLRegistryObject entry : entries) {
            String patientIdEntry = entry.getExternalIdentifierValue(id);
            patientId = patientId == null ? patientIdEntry : patientId;
            metaDataAssert(patientId.equals(patientIdEntry), RESULT_NOT_SINGLE_PATIENT);
        }
        return patientId;
    }

}
