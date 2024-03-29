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

import java.io.PrintStream;

/**
 * Dumps ontology map to output stream.
 *
 * @author  Antony Quinn
 * @version $Id: OntologyMapDump.java,v 1.1.1.1 2005/04/27 15:31:35 aquinn Exp $
 * @since   1.0
 */
public class OntologyMapDump {

    /**
     * Dumps output to standard out
     *
     * @see #dump
     */
	public static void main (String[] args) {
        try {
            dump(System.out, System.err);
        }
        catch (Exception e)    {
            e.printStackTrace();
        }
	}

    /**
     * Dumps output to <code>out</code>
     *
     * @param   out Output/logging stream
     * @see     OntologyMapTest#toString
     */
	public static void dump (PrintStream out, PrintStream err) {
        OntologyMapTest test = new OntologyMapTest("OntologyMapDump");
        out.println(test.toString(err));
	}

}
