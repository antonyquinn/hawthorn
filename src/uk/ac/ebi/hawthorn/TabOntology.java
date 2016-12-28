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

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Provides cached map of terms for tab-delimited ontologies.
 * Format should be ID&lt;tab&gt;Term. Lines beginning with ! are ignored.
 * For example:
 * <pre>
 *   GO:0000001	mitochondrion inheritance
 *   GO:0000002	mitochondrial genome maintenance
 *   GO:0000003	reproduction
 * </pre>
 *
 * @author  Antony Quinn
 * @version $Id: TabOntology.java,v 1.2 2005/06/21 14:49:11 aquinn Exp $
 * @since   1.0
 * @see     <a href="http://www.geneontology.org/doc/GO.terms_ids_obs">Example</a>
 */
public final class TabOntology extends AbstractOntology implements Ontology {

    private final static String COMMENT    = "!";
    private final static String SEPARATOR  = "\t";

    private final static int COLUMN_ID   = 0;
    private final static int COLUMN_TERM = COLUMN_ID + 1;
    private final static int MIN_COLUMNS = COLUMN_TERM + 1;
    
    /**
     * Register class with OntologyManager
     * @see OntologyManager#registerOntology(String, OntologyFactory)
     */
    static  {
        OntologyManager.registerOntology("uk.ac.ebi.hawthorn.TabOntology", new TabOntology.TabOntologyFactory());
    }    

    static class TabOntologyFactory implements OntologyFactory {
        public Ontology getInstance(String prefix, String uri, String userName, String password,
                                    int refreshInterval, boolean tolerateRefreshException, 
                                    InputStreamListener inputStreamListener)
               throws IOException   {
            return new TabOntology(prefix, uri, userName, password, refreshInterval, tolerateRefreshException, inputStreamListener);
        }
    }

    /**
     * Loads terms from tab-delimited ontology.
     *
     * @param   name
     * @param   uri
     * @param   userName
     * @param   password
     * @param   refreshInterval
     * @param   tolerateRefreshException
     * @throws  IOException
     * @see     uk.ac.ebi.hawthorn.AbstractOntology#AbstractOntology(String, String, String, String, int, boolean, InputStreamListener)
     */
    protected TabOntology(String name, String uri, String userName, String password,
                          int refreshInterval, boolean tolerateRefreshException, 
                          InputStreamListener inputStreamListener)
              throws IOException   {
        super(name, uri, userName, password, refreshInterval, tolerateRefreshException, inputStreamListener);
    }

    public Map getTerms() throws IOException, IndexOutOfBoundsException {
        Map terms = new HashMap();
        InputStream inputStream = getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = reader.readLine()) != null)    {
            if (!line.startsWith(COMMENT))   {
                String[] columns = line.split(SEPARATOR);
                if (columns.length < MIN_COLUMNS) {
                    String message = "Too few columns for line " + line;
                    message += " [expected=" + Integer.toString(MIN_COLUMNS);
                    message += " found=" + Integer.toString(columns.length) + "]";
                    throw new IndexOutOfBoundsException(message);
                }
                String id = columns[COLUMN_ID];
                String term = columns[COLUMN_TERM];
                terms.put(id, term);
            }
        }
        return Collections.unmodifiableMap(terms);
    }

}
