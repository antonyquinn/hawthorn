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

import org.biojava.utils.io.InputStreamMonitor;
import org.biojava.utils.net.URLConnectionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

/**
 * Provides a cached map of ontology terms.
 *
 * @author  Antony Quinn
 * @version $Id: AbstractOntology.java,v 1.2 2005/06/21 14:49:11 aquinn Exp $
 * @since   1.0
 */
public abstract class AbstractOntology implements Ontology {

    private final String prefix;
    private final String uri;
    private final String userName;
    private final String password;
    private final int    refreshInterval;
    private final boolean tolerateRefreshException;

    private final InputStreamMonitor  inputStreamMonitor;
    private final InputStreamListener inputStreamListener;
    private final Map                 terms;
    private final Log                 log;

    /**
     * Loads ontology terms from <code>url</code>.
     *
     * @param   prefix Ontology prefix
     * @param   uri
     * @param   userName
     * @param   password
     * @param   refreshInterval How often in seconds to check for updates.
     * @param   tolerateRefreshException Allow refresh exceptions to be logged or thrown
     * @param   inputStreamListener Listens for calls to obtain input streams
     * @throws  IOException if could not load terms
     */
    protected AbstractOntology(String prefix,
                               String uri,
                               String userName,
                               String password,
                               int refreshInterval,
                               boolean tolerateRefreshException,
                               InputStreamListener inputStreamListener)
              throws IOException   {
        this.log                = LogFactory.getLog(this.getClass());
        this.prefix             = prefix;
        this.uri                = uri;
        this.userName           = userName;
        this.password           = password;
        this.refreshInterval    = refreshInterval;
        this.tolerateRefreshException = tolerateRefreshException;
        this.inputStreamMonitor = new InputStreamMonitor(getRefreshInterval());
        this.inputStreamListener = inputStreamListener;
        this.terms              = new HashMap();
        terms.putAll(getTerms());
    }

    public final String getPrefix()   {
         return prefix;
    }

    public final int getRefreshInterval()  {
        return refreshInterval;
    }

    public final String getPassword()   {
         return password;
    }

    public final String getUserName()   {
         return userName;
    }

    public final String getUri() {
        return uri;
    }

    public final String getTerm(String id) throws IOException, NoSuchElementException   {
        refresh();
        if (terms.containsKey(id))
            return (String) terms.get(id);
        else
            throw new NoSuchElementException("Could not find term for ontology ID: " + id);
    }

    public boolean isTolerateRefreshExceptions() {
        return tolerateRefreshException;
    }

    /**
     * Returns prefix, type, URL, user name, password, refresh interval, term count.
     *
     * @return prefix, type, URL, user name, password, refresh interval, term count.
     */
    public final String toString()    {
        StringBuffer buf = new StringBuffer();
        buf.append("Prefix:\t"           + getPrefix()          + "\n");
        buf.append("Type:\t"             + getClass().getName() + "\n");
        buf.append("URI:\t"              + getUri()             + "\n");
        buf.append("User name:\t"        + getUserName()        + "\n");
        buf.append("Password:\t"         + getPassword()        + "\n");
        buf.append("Refresh interval:\t" + getRefreshInterval() + "\n");
        buf.append("Tolerate refresh exceptions:\t" + isTolerateRefreshExceptions() + "\n");
        buf.append("Term count:\t"       + Integer.toString(terms.size()) + "\n");
        return (buf.toString());
    }

    /**
     * Returns input stream for URL.
     *
     * @return  input stream for URL
     * @throws  IOException if could not open connection or get input stream
     * @see     #getUri
     */
    protected final InputStream getInputStream() throws IOException {
        String uri = getUri();
        try {
            InputStream inputStream = null;
            // First try event listeners
            if (inputStreamListener != null)    {
                inputStream = inputStreamListener.getInputStream(uri);
            }
            // Now try class loader
            if (inputStream == null)    {
                inputStream = getClass().getResourceAsStream(uri);
            }
            if (inputStream == null)    {
                // Next try file system
                File file = new File(uri);
                if (file.exists())  {
                    inputStream = new FileInputStream(file);
                }
                else    {
                    // Finally assume it's a URL
                    inputStream = URLConnectionHelper.getInputStream(new URL(uri), getUserName(), getPassword());
                }
            }
            return inputStream;
        }
        catch (IOException e)    {
            String message = "Could not get input stream for " + uri;
            message += "\n" + e.toString();
            throw new IOException(message);
        }
    }

    /**
     * Reloads terms if ontology has been updated.
     *
     * @throws IOException if could not get input stream for ontology
     */
    private void refresh() throws IOException  {
        Map map = null;
        try {
            if (inputStreamMonitor.isModified(getInputStream()))   {
                map = getTerms();
            }
        }
        catch (IOException e)   {
            if (isTolerateRefreshExceptions())    {
                log.warn("Could not refresh ontology '" + getPrefix() + "'", e);
            }
            else    {
                throw e;
            }
        }
        if (map != null)    {
            terms.clear();
            terms.putAll(map);
        }
    }

}