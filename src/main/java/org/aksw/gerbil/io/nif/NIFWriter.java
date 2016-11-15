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

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;

/**
 * This interface defines a NIFWriter - a class that should write NIF data from
 * a given list of {@link Document} instances.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public interface NIFWriter {

    /**
     * Writes the given lost of {@link Document} instances as NIF data to a
     * String.
     * 
     * @param document
     *            the list of {@link Document} instances that should be written
     *            as NIF data
     * @return the String containing the NIF data
     */
    public String writeNIF(List<Document> document);

    /**
     * Writes the given lost of {@link Document} instances as NIF data to the
     * given {@link Writer}.
     * 
     * <p>
     * <b>Note</b> that following the recommendations of the Jena framework we
     * highly recommend to use {@link #writeNIF(List, OutputStream)} instead of
     * using a {@link Writer} since the {@link OutputStream} is not relying on a
     * specific encoding.
     * </p>
     * 
     * @param document
     *            the list of {@link Document} instances that should be written
     *            as NIF data
     * @param writer
     *            the {@link Writer} to which the NIF data should be written
     */
    public void writeNIF(List<Document> document, Writer writer);

    /**
     * Writes the given lost of {@link Document} instances as NIF data to the
     * given {@link OutputStream}.
     * 
     * @param document
     *            the list of {@link Document} instances that should be written
     *            as NIF data
     * @param os
     *            the {@link OutputStream} to which the NIF data should be
     *            written
     */
    public void writeNIF(List<Document> document, OutputStream os);

    /**
     * This method should return the HTTP content type string for the data that
     * is created by this writer.
     * 
     * @return the HTTP content type string
     */
    public String getHttpContentType();

    /**
     * Returns the {@link DocumentListWriter} used by this parser.
     * 
     * @return the {@link DocumentListWriter} used by this parser
     */
    public DocumentListWriter getDocumentListWriter();

    /**
     * Sets the {@link DocumentListWriter} used by this parser.
     * 
     * @param listParser
     *            the {@link DocumentListWriter} used by this parser
     */
    public void setDocumentListWriter(DocumentListWriter listWriter);

}
