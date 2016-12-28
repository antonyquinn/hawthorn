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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

import uk.ac.ebi.hawthorn.Ontology;
import uk.ac.ebi.hawthorn.OntologyMap;
import org.biojava.utils.net.URLConnectionHelper;

/**
 * OntologyMap Tester.
 *
 * @author  Antony Quinn
 * @version $Id: OntologyMapTest.java,v 1.3 2005/06/21 15:22:17 aquinn Exp $
 * @since   1.0
 */
public class OntologyMapTest extends TestCase   {

    private static final String TEST_FILE = "OntologyMapTest.properties";

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(OntologyMapTest.class);
        return suite;
    }

    /**
     * Stores test name.
     *
     * @param   name    test name
     */
    public OntologyMapTest(String name) {
        super(name);
    }

    /**
     * Compares the ontology terms specified in OntologyMapTest.properties with those
     * read from the default ontology properties file.
     *
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  IllegalArgumentException if id is not a recognised ontology identifier
     * @throws  NoSuchElementException   if id is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file or ontology
     * @see     OntologyMap#getMap
     * @see     Ontology#getTerm
     */
    public void testGetMap()
                throws ClassNotFoundException, IllegalArgumentException,
                       NoSuchElementException, IOException  {
        final Properties properties = getTestProperties();
        final OntologyMap ontologyMap = new OntologyMap();
        final Map map = ontologyMap.getMap();
        for (Iterator i = map.entrySet().iterator(); i.hasNext(); )   {
            Map.Entry entry = (Map.Entry) i.next();
            String prefix = (String) entry.getKey();
            Ontology ontology = (Ontology) entry.getValue();
            assertEquals("Prefix", prefix, ontology.getPrefix());
            assertNotNull("toString", ontology.toString());
            for (Iterator ip = properties.entrySet().iterator(); ip.hasNext(); )   {
                Map.Entry pEntry = (Map.Entry) ip.next();
                String id = (String) pEntry.getKey();
                String term = (String) pEntry.getValue();
                if (id.startsWith(prefix))  {
                    assertEquals(id, term, ontologyMap.getTerm(id));
                }
            }
        }
    }

    /**
     * Checks the same ontology terms as <code>testGetMap</code> by calling <code>OntologyMap.getTerm</code>
     *
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  IllegalArgumentException if id is not a recognised ontology identifier
     * @throws  NoSuchElementException   if id is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file or ontology
     * @see     #testGetMap
     * @see     OntologyMap#getTerm(String)
     */
    public void testGetTerm()
                throws ClassNotFoundException, IllegalArgumentException,
                       NoSuchElementException, IOException  {
        testTerms(getTestProperties(), new OntologyMap());
    }

    /**
     * Checks the same ontology terms as <code>testGetMap</code> by calling <code>OntologyMap.getTerm</code>,
     * with input stream supplied by listener implementation
     *
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  IllegalArgumentException if id is not a recognised ontology identifier
     * @throws  NoSuchElementException   if id is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file or ontology
     * @see     #testGetMap
     * @see     OntologyMap#getTerm(String)
     */
    public void testGetTermWithListener()
                throws ClassNotFoundException, IllegalArgumentException,
                       NoSuchElementException, IOException  {
        OntologyMap ontologyMap = new OntologyMap(new InputStreamListenerImpl());
        testTerms(getTestProperties(), ontologyMap);
    }

    /**
     * Checks the same ontology terms as <code>testGetMap</code> by calling <code>OntologyMap.getTerm</code>
     *
     * @throws  IllegalArgumentException if id is not a recognised ontology identifier
     * @throws  NoSuchElementException   if id is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file or ontology
     */
    private void testTerms(Properties properties, OntologyMap ontologyMap)
                throws IllegalArgumentException, NoSuchElementException, IOException  {
        for (Iterator i = properties.entrySet().iterator(); i.hasNext(); )   {
            Map.Entry entry = (Map.Entry) i.next();
            String id = (String) entry.getKey();
            String term = (String) entry.getValue();
            assertEquals(id, term, ontologyMap.getTerm(id));
        }
    }

    private Properties getTestProperties() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(TEST_FILE));
        return properties;
    }

   /**
     * Returns summary information for each ontology loaded from the default properties file.
     * Writes errors to System.err
     *
     * @return  summary information for each ontology loaded from the default properties file.
     * @see     #toString(PrintStream)
     * @see     System#err
     */
    public String toString()    {
        return toString(System.err);
    }

   /**
     * Returns summary information for each ontology loaded from the default properties file.
     *
     * @param   err Error stream
     * @return  Summary information for each ontology loaded from the default properties file.
     */
    public String toString(PrintStream err)    {
        StringBuffer buffer = new StringBuffer("");
        final String SUFFIX = "\n";
        try {
            OntologyMap ontologyMap = new OntologyMap();
            final Map map = ontologyMap.getMap();
            for (Iterator i = map.entrySet().iterator(); i.hasNext(); )   {
                Map.Entry entry = (Map.Entry) i.next();
                Ontology ontology = (Ontology) entry.getValue();
                buffer.append(ontology.toString() + SUFFIX);
            }
        }
        catch (Exception e) {
            e.printStackTrace(err);
        }
       return buffer.toString();
    }

    private class InputStreamListenerImpl implements InputStreamListener    {
        public InputStream getInputStream(String uri) throws FileNotFoundException, IOException, MalformedURLException  {
            return getClass().getResourceAsStream(uri);
        }
    }

}
