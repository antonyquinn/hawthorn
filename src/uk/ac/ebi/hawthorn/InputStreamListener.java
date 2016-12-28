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

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Allows a calling class to provide input streams to ontology files.
 *
 * @author  Antony Quinn
 * @version $Id: InputStreamListener.java,v 1.1 2005/06/21 14:49:11 aquinn Exp $
 * @since   1.0
 */
public interface InputStreamListener {

    /**
     * Returns input stream for URI
     *
     * @param   uri                     Resource, file or URL
     * @return  input stream
     * @throws  FileNotFoundException   if could not find file
     * @throws  IOException             if could not open input stream
     * @throws  MalformedURLException   if URL is incorrect
     */
    public InputStream getInputStream(String uri) throws FileNotFoundException, IOException, MalformedURLException;

}
