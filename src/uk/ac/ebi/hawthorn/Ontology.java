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

import org.apache.commons.logging.Log;

import java.net.URL;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Map;

/**
 * Provides cached map of ontology terms.
 *
 * @author  Antony Quinn
 * @version $Id: Ontology.java,v 1.1.1.1 2005/04/27 15:31:35 aquinn Exp $
 * @since   1.0
 */
public interface Ontology {

    /**
     * Returns ontology prefix, for example "GO" or "EVOC"
     *
     * @return  ontology name
     */
    public String getPrefix();

    /**
     * Returns how often in seconds the URL is monitored for updates
     *
     * @return refresh interval (in seconds)
     */
    public int getRefreshInterval();

    /**
     * Returns password to access ontology
     *
     * @return  password
     */
    public String getPassword();

    /**
     * Returns user name to access ontology
     *
     * @return user name
     */
    public String getUserName();

    /**
     * Returns URI of ontology (can be relative or absolute file or URL)
     *
     * @return URI of ontology
     */
    public String getUri();

    /**
     * Returns ontology term for <code>id</code> from <code>url</code>
     *
     * @param   id  Ontology ID, for example GO:0000001
     * @return  ontology term
     * @throws  IOException             if could not access URL
     * @throws  NoSuchElementException  if <code>id</code> not recognised
     */
    public String getTerm(String id) throws IOException, NoSuchElementException;

    /**
     * Returns ontology terms with ontology ID as key and ontology term as value
     *
     * @return  ontology terms with ontology ID as key and term as value
     * @throws  IOException if could not access URL
     */
    public Map getTerms() throws IOException;

    /**
     * Returns true if refresh exceptions are tolerated (expceptions are written to log as warnings),
     * otherwise false (exceptions are thrown)
     *
     * @return true if refresh exceptions are tolerated
     */
    public boolean isTolerateRefreshExceptions();

    public String toString();    

}