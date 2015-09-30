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
package org.aksw.gerbil.transfer.nif;

import java.util.Arrays;

import org.aksw.gerbil.io.nif.NIFWriter;

/**
 * This class is a simple Wrapper of a {@link NIFWriter} instance offering the
 * methods of the {@link NIFDocumentCreator} interface and might be removed in
 * future releases.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public abstract class AbstractNIFDocumentCreator implements NIFDocumentCreator {

    private NIFWriter nifWriter;

    public AbstractNIFDocumentCreator(NIFWriter nifWriter) {
        this.nifWriter = nifWriter;
    }

    @Override
    public String getDocumentAsNIFString(Document document) {
        return nifWriter.writeNIF(Arrays.asList(document));
    }

    @Override
    public String getHttpContentType() {
        return nifWriter.getHttpContentType();
    }
}
