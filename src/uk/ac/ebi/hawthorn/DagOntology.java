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

import org.bdgp.apps.dagedit.dataadapter.GOFlatFileAdapter;
import java.io.IOException;
import java.util.Map;

/**
 * Provides cached map of terms for DAG-formatted ontologies. For example:
 * <pre>
 *  $Gene_Ontology ; GO:0003673
 *  &lt;molecular_function ; GO:0003674
 *  %antioxidant activity ; GO:0016209
 * </pre>
 *
 * @author  Antony Quinn
 * @version $Id: DagOntology.java,v 1.2 2005/06/21 14:49:11 aquinn Exp $
 * @since   1.0
 * @see     <a href="http://www.geneontology.org/ontology/function.ontology">Example</>
 */
public final class DagOntology extends AbstractOntology implements Ontology {

    /**
     * Register class with OntologyManager
     * @see OntologyManager#registerOntology(String, OntologyFactory)
     */
    static  {
        OntologyManager.registerOntology("uk.ac.ebi.hawthorn.DagOntology", new DagOntologyFactory());
    }

    static class DagOntologyFactory implements OntologyFactory {
        public Ontology getInstance(String prefix, String uri, String userName,
                                    String password, int refreshInterval, 
                                    boolean tolerateRefreshException, 
                                    InputStreamListener inputStreamListener)
               throws IOException   {
            return new DagOntology(prefix, uri, userName, password, refreshInterval, tolerateRefreshException, inputStreamListener);
        }
    }

    /**
     * Loads terms from GO-formatted ontology.
     *
     * @param   prefix
     * @param   uri
     * @param   userName
     * @param   password
     * @param   refreshInterval
     * @param   tolerateRefreshException
     * @param   inputStreamListener     Listens for calls to obtain input streams
     * @throws  IOException if could not load terms
     * @see     AbstractOntology#AbstractOntology(String, String, String, String, int, boolean, InputStreamListener)
     */
    protected DagOntology(String prefix, String uri, String userName, String password,
                          int refreshInterval, boolean tolerateRefreshException,
                          InputStreamListener inputStreamListener)
              throws IOException   {
        super(prefix, uri, userName, password, refreshInterval, tolerateRefreshException, inputStreamListener);
    }

    public Map getTerms() throws IOException {
        GOFlatFileAdapter adapter = new GOFlatFileAdapter();
        adapter.setPath(getUri());
        return OboOntology.getTerms(adapter);
    }
}
