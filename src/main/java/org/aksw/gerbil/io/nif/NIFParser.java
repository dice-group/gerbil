/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * This interface defines a NIFParser - a class that should parse NIF data from
 * a given source and create a list of {@link Document} instances from this
 * data.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public interface NIFParser {

    /**
     * Parses the NIF data inside the given String and returns the list of
     * documents that could be found.
     * 
     * @param nifString
     *            a String containing the NIF data
     * @return a list of {@link Document} instances found in the given NIF
     */
    public List<Document> parseNIF(String nifString);

    /**
     * Parses the NIF data read from the given Reader and returns the list of
     * documents that could be found.
     * 
     * @param reader
     *            a {@link Reader} from which the NIF data is read
     * @return a list of {@link Document} instances found in the given NIF
     */
    public List<Document> parseNIF(Reader reader);

    /**
     * Parses the NIF data read from the given InputStream and returns the list
     * of documents that could be found.
     * 
     * @param is
     *            an {@link InputStream} from which the NIF data is read
     * @return a list of {@link Document} instances found in the given NIF
     */
    public List<Document> parseNIF(InputStream is);

    /**
     * Like {@link #parseNIF(String)}, this method parses the NIF data inside
     * the given String and returns the list of documents that could be found.
     * But it uses the given model to store this information.
     * 
     * <p>
     * Note that the parser will use and change the data that is already inside
     * the given model.
     * </p>
     * 
     * <p>
     * The aim of this method is to take a reference to a model (that might be
     * empty) and store all additional information that has been read but could
     * not be added to the created {@link Document} instances.
     * </p>
     * 
     * @param nifString
     *            a String containing the NIF data
     * @param model
     *            a model which is used to store the NIF data
     * @return a list of {@link Document} instances found in the given NIF
     */
    public List<Document> parseNIF(String nifString, Model model);

    /**
     * Like {@link #parseNIF(Reader)}, this method parses the NIF data inside
     * the given String and returns the list of documents that could be found.
     * But it uses the given model to store this information.
     * 
     * <p>
     * Note that the parser will use and change the data that is already inside
     * the given model.
     * </p>
     * 
     * <p>
     * The aim of this method is to take a reference to a model (that might be
     * empty) and store all additional information that has been read but could
     * not be added to the created {@link Document} instances.
     * </p>
     * 
     * @param reader
     *            a {@link Reader} from which the NIF data is read
     * @param model
     *            a model which is used to store the NIF data
     * @return a list of {@link Document} instances found in the given NIF
     */
    public List<Document> parseNIF(Reader reader, Model model);

    /**
     * Like {@link #parseNIF(InputStream)}, this method parses the NIF data
     * inside the given String and returns the list of documents that could be
     * found. But it uses the given model to store this information.
     * 
     * <p>
     * Note that the parser will use and change the data that is already inside
     * the given model.
     * </p>
     * 
     * <p>
     * The aim of this method is to take a reference to a model (that might be
     * empty) and store all additional information that has been read but could
     * not be added to the created {@link Document} instances.
     * </p>
     * 
     * @param is
     *            an {@link InputStream} from which the NIF data is read
     * @param model
     *            a model which is used to store the NIF data
     * @return a list of {@link Document} instances found in the given NIF
     */
    public List<Document> parseNIF(InputStream is, Model model);

    public String getHttpContentType();
}
