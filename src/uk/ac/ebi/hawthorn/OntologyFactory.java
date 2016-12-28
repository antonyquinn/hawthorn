/*
 * Copyright 2005 European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/

package uk.ac.ebi.hawthorn;

import java.io.IOException;

/**
 * Provides static factory methods to create Ontology instances.
 *
 * @author  Antony Quinn
 * @version $Id: OntologyFactory.java,v 1.2 2005/06/21 14:49:11 aquinn Exp $
 * @since   1.0
 */
public interface OntologyFactory {

    /**
     * Returns new Ontology instance
     *
     * @param   prefix          Ontology prefix, for example "GO" or "MI"
     * @param   uri             URL or relative path to ontology
     * @param   userName        User name to access secured ontology (optional)
     * @param   password        Password to access secured ontology (optional)
     * @param   refreshInterval How often to check ontology for updates
     * @param   tolerateRefreshException Allow refresh exceptions to be logged or thrown
     * @param   inputStreamListener Listens for calls to obtain input streams
     * @return  new Ontology instance
     * @throws  IOException if could not load ontology
     */
    public Ontology getInstance(String prefix,
                                String uri,
                                String userName,
                                String password,
                                int refreshInterval,
                                boolean tolerateRefreshException, 
                                InputStreamListener inputStreamListener)
           throws IOException;

}