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

import org.bdgp.apps.dagedit.dataadapter.DEDataAdapterI;
import org.bdgp.apps.dagedit.dataadapter.GOBOAdapter;
import org.bdgp.apps.dagedit.datamodel.DEEditHistory;
import org.bdgp.apps.dagedit.datamodel.Term;
import org.bdgp.io.DataAdapterException;

import java.util.*;
import java.io.IOException;

/**
 * Provides cached map of terms for OBO-formatted ontologies. For example:
 * <pre>
 *  [Term]
 *  id: GO:0000001
 *  name: mitochondrion inheritance
 *  namespace: biological_process
 *  def: "The distribution of mitochondria\, including the mitochondrial genome\, into daughter cells after mitosis or meiosis\, mediated by interactions between mitochondria and the cytoskeleton." [PMID:11389764, PMID:10873824, SGD:mcc]
 *  is_a: GO:0048308 ! organelle inheritance
 *  is_a: GO:0048311 ! mitochondrion distribution
 * </pre>
 *
 * @author  Antony Quinn
 * @version $Id: OboOntology.java,v 1.2 2005/06/21 14:49:11 aquinn Exp $
 * @since   1.0
 * @see     <a href="http://www.geneontology.org/ontology/gene_ontology.obo">Example</a>
 */
public final class OboOntology extends AbstractOntology implements Ontology {

    /**
     * Register class with OntologyManager
     *
     * @see OntologyManager#registerOntology(String, OntologyFactory)
     */
    static  {
        OntologyManager.registerOntology("uk.ac.ebi.hawthorn.OboOntology", new OboOntology.OboOntologyFactory());
    }

    static class OboOntologyFactory implements OntologyFactory {
        public Ontology getInstance(String prefix, String uri, String userName, String password,
                                    int refreshInterval, boolean tolerateRefreshException, 
                                    InputStreamListener inputStreamListener)
               throws IOException   {
            return new OboOntology(prefix, uri, userName, password, refreshInterval, tolerateRefreshException, inputStreamListener);
        }
    }

    /**
     * Loads terms from OBO-formatted ontology.
     *
     * @param   prefix
     * @param   uri
     * @param   userName
     * @param   password
     * @param   refreshInterval
     * @param   tolerateRefreshException
     * @throws  IOException
     * @see     uk.ac.ebi.hawthorn.AbstractOntology#AbstractOntology(String, String, String, String, int, boolean, InputStreamListener)
     */
    protected OboOntology(String prefix, String uri, String userName, String password,
                          int refreshInterval, boolean tolerateRefreshException, 
                          InputStreamListener inputStreamListener)
              throws IOException    {
        super(prefix, uri, userName, password, refreshInterval, tolerateRefreshException, inputStreamListener);
    }

    public Map getTerms() throws IOException {
        return getTerms(getAdapter());
    }

    /**
     * Returns map of terms using ID as key and term as value.
     *
     * @param   adapter     DAG-Edit data adapter
     * @return  map of terms using ID as key and term as value.
     * @throws  IOException if could not load terms
     */
    static Map getTerms(DEDataAdapterI adapter) throws IOException {
        try {
            Map terms = new HashMap();
            DEEditHistory history = adapter.getRoot();
            Map termsHash = history.getAllTermsHash();
            for (Iterator t = termsHash.entrySet().iterator(); t.hasNext();)  {
                Map.Entry entry = (Map.Entry) t.next();
                Term term = (Term) entry.getValue();
                terms.put(entry.getKey(), term.getTerm());
            }
            return Collections.unmodifiableMap(terms);
        }
        catch (DataAdapterException e)  {
            String message = "Could not load terms from " + adapter.getName();
            try {
                message += "\nHistories: " + adapter.getHistories().toString();
            }
            catch (DataAdapterException de) {
                message += " Histories exception: " + de.toString();
            }
            message += "\n" + e.toString();
            throw new IOException(message);
        }
    }

    private DEDataAdapterI getAdapter()    {
        GOBOAdapter adaptor = new GOBOAdapter();
        GOBOAdapter.IOProfile profile = new GOBOAdapter.IOProfile("");
        Vector v = new Vector();
        v.add(getUri());
        profile.setFiles(v);
        adaptor.setIOProfile(profile);
        return adaptor;
    }

 }
