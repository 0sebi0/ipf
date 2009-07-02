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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml;

import java.util.List;

/**
 * Provides functionality for containers of various ebXML objects.
 * @author Jens Riemschneider
 */
public interface EbXMLObjectContainer {
    void addExtrinsicObject(ExtrinsicObject extrinsic);
    List<ExtrinsicObject> getExtrinsicObjects(String objectType);
    List<ExtrinsicObject> getExtrinsicObjects();

    void addRegistryPackage(RegistryPackage ebXML);
    List<RegistryPackage> getRegistryPackages(String classificationNode);
    List<RegistryPackage> getRegistryPackages();

    void addAssociation(EbXMLAssociation ebXML);
    List<EbXMLAssociation> getAssociations();

    void addClassification(Classification classification);
}