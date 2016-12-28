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

import java.util.*;
import java.io.*;
import java.net.MalformedURLException;

/**
 * Provides a map of ontologies. Key is ontology name, value is <code>Ontology</code>.
 * <p>
 * Ontology settings should be specified in a properties file of the format:
 * </p>
 * <pre>
 * &lt;ontology-prefix&gt;.url=&lt;URL&gt;
 * &lt;ontology-prefix&gt;.username=&lt;user name&gt;
 * &lt;ontology-prefix&gt;.password=&lt;password&gt;
 * &lt;ontology-prefix&gt;.refresh-interval=&lt;refresh interval&gt;
 * &lt;ontology-prefix&gt;.class=&lt;class implementing Ontology&gt;
 *
 * </pre>
 * For example:
 * <pre>
 *  GO.url=http://www.geneontology.org/doc/GO.terms_ids_obs
 *  GO.username=
 *  GO.password=
 *  GO.refresh-interval=600
 *  GO.class=uk.ac.ebi.hawthorn.TabOntology
 * </pre>
 *
 * @author  Antony Quinn
 * @version $Id: OntologyMap.java,v 1.3 2005/11/23 15:32:57 aquinn Exp $
 * @since   1.0
 * @see     Ontology
 */
public class OntologyMap    {

    private static final String PREFIX_SEPARATOR            = ":";

    private static final String PROPERTIES_FILE             = "ontologies.properties";
    private static final String PROPERTY_SEP                = ".";
    private static final String PROPERTY_URI                = "uri";
    private static final String PROPERTY_USER_NAME          = "username";
    private static final String PROPERTY_PASSWORD           = "password";
    private static final String PROPERTY_REFRESH_INTERVAL   = "refresh-interval";
    private static final String PROPERTY_TOLERATE_REFRESH_EXCEPTION   = "tolerate-refresh-exception";
    private static final String PROPERTY_CLASS              = "class";

    private static final String DEFAULT_USER_NAME           = "";
    private static final String DEFAULT_PASSWORD            = "";
    private static final String DEFAULT_REFRESH_INTERVAL    = "60";
    private static final String DEFAULT_CLASS               = "uk.ac.ebi.hawthorn.GoOntology";
    private static final boolean DEFAULT_TOLERATE_REFRESH_EXCEPTION = false;

    private final Map map;
    private final InputStreamListener inputStreamListener;

    /**
     * Loads ontology map using default properties file
     * (<code>ontologies.properties</code> in working directory).
     *
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file
     * @see     #OntologyMap(InputStream)
     */
    public OntologyMap() throws ClassNotFoundException, FileNotFoundException, IOException   {
        this(new FileInputStream(PROPERTIES_FILE), null);
    }

    /**
     * Loads ontology map using default properties file
     * (<code>ontologies.properties</code> in working directory) and input stream listener.
     *
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file
     * @see     #OntologyMap(InputStream)
     */
    public OntologyMap(InputStreamListener inputStreamListener) throws ClassNotFoundException, FileNotFoundException, IOException   {
        this(new FileInputStream(PROPERTIES_FILE), inputStreamListener);
    }

    /**
     * Loads ontology map using <code>propertiesInputStream</code>
     *
     * @param   propertiesInputStream    Properties file
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file
     */
    public OntologyMap(InputStream propertiesInputStream)
           throws ClassNotFoundException, FileNotFoundException, IOException   {
        this(propertiesInputStream, null);
    }

    /**
     * Loads ontology map using <code>propertiesInputStream</code>  and obtains input streams
     * using  <code>inputStreamListener</code>
     *
     * @param   propertiesInputStream    Properties file
     * @param   inputStreamListener      Event listener
     * @throws  ClassNotFoundException   if class is unrecognised
     * @throws  FileNotFoundException    if could not find properties file
     * @throws  IOException              if could not load properties file
     */
    public OntologyMap(InputStream propertiesInputStream, InputStreamListener inputStreamListener)
           throws ClassNotFoundException, FileNotFoundException, IOException   {
        this.inputStreamListener = inputStreamListener;
        this.map = new HashMap();
        Properties p = new Properties();
        p.load(propertiesInputStream);
        // Get prefixes
        Set prefixes = new HashSet();
        for (Iterator i=p.keySet().iterator(); i.hasNext(); )    {
            String key = (String) i.next();
            int sep = key.indexOf(PROPERTY_SEP);
            if (sep > -1)   {
                String prefix = key.substring(0, sep);
                if (!prefixes.contains(prefix))   {
                    prefixes.add(prefix);
                }
            }
        }
        // Get ontologies
        for (Iterator i=prefixes.iterator(); i.hasNext(); )    {
            String name     = (String) i.next();
            Ontology ontology = getOntology(name, p);
            this.map.put(name, ontology);
        }
    }

    /**
     * Returns ontology map.
     *
     * @return  ontology map
     */
    public Map getMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns ontology term from map.
     *
     * @param   id  Ontology ID
     * @return  ontology term
     * @throws  IllegalArgumentException if id is not a recognised ontology identifier
     * @throws  NoSuchElementException   if id is unrecognised
     * @throws  IOException              if ontology could not be loaded
     */
    public String getTerm(String id)
                  throws IllegalArgumentException, NoSuchElementException, IOException  {
        if (isValidID(id)) {
            int sep = id.indexOf(PREFIX_SEPARATOR);
            final String prefix = id.substring(0, sep);
            if (map.containsKey(prefix))   {
                final Ontology ontology = (Ontology) map.get(prefix);
                return ontology.getTerm(id);
            }
            else    {
                throw new NoSuchElementException("Unrecogised prefix: " + prefix);
            }
        }
        else    {
            String message = "ID (" + id + ") does not contain the prefix separator '" + PREFIX_SEPARATOR + "'";
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * Returns true if <code>id</code> contains a valid prefix separator.
     * For example, "GO:0001" is a valid ontology ID, "GO-0001" is not a valid ontology ID.
     *
     * @param   id  Ontology identifier
     * @return  true if <code>id</code> contains a valid prefix separator
     */
    public boolean isValidID(String id)  {
        if (id == null) {
            return false;
        }
        return (id.indexOf(PREFIX_SEPARATOR) > -1);
    }

    /**
     * Returns ontology name and details by calling <code>Ontology.toString</code>
     *
     * @return  ontology name and details by calling <code>Ontology.toString</code>
     * @see     Ontology#toString
     */
    public String toString()   {
        StringBuffer buffer = new StringBuffer("OntologyMap:\n");
        for (Iterator i=map.keySet().iterator(); i.hasNext();)  {
            Map.Entry entry = (Map.Entry) i.next();
            String name = (String) entry.getKey();
            Ontology ontology = (Ontology) entry.getValue();
            buffer.append(name + "\n");
            buffer.append(ontology.toString());
        }
        return buffer.toString();
    }

    /**
     * Intialises and returns ontology based on setting in properties file.
     *
     * @param   prefix  Ontology prefix
     * @param   p       Properties file
     * @return
     * @throws  ClassNotFoundException  if class is unrecognised
     * @throws  IOException             if could not load ontology terms
     */
    private Ontology getOntology(String prefix, Properties p)
            throws ClassNotFoundException, IOException  {
        // Get properties
        String s   = prefix + PROPERTY_SEP;
        String userName = p.getProperty(s + PROPERTY_USER_NAME, DEFAULT_USER_NAME);
        String password = p.getProperty(s + PROPERTY_PASSWORD, DEFAULT_PASSWORD);
        String className = p.getProperty(s + PROPERTY_CLASS, DEFAULT_CLASS);
        boolean tolerate = getBooleanProperty(p, s + PROPERTY_TOLERATE_REFRESH_EXCEPTION, DEFAULT_TOLERATE_REFRESH_EXCEPTION);
        String uri       = p.getProperty(s + PROPERTY_URI);
        // Can override URI in file with system property
        uri = System.getProperty(s + PROPERTY_URI, uri);
        String refInt = p.getProperty(s + PROPERTY_REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL);
        int refreshInterval = Integer.parseInt(refInt);
        // Return class
        Class.forName(className);
        return OntologyManager.getOntology(className, prefix, uri, userName, password, refreshInterval, tolerate, new InputStreamListenerImpl());
    }

    private boolean getBooleanProperty(Properties p, String key, boolean defaultValue)    {
        String value = p.getProperty(key, Boolean.toString(defaultValue));
        return Boolean.valueOf(value).booleanValue();
    }

    private class InputStreamListenerImpl implements InputStreamListener    {
        public InputStream getInputStream(String uri) throws FileNotFoundException, IOException, MalformedURLException {
            if (inputStreamListener == null)    {
                return null;
            }
            else    {
                return inputStreamListener.getInputStream(uri);
            }
        }
    }

}
