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
package org.aksw.gerbil.io.nif.impl;

import org.aksw.gerbil.io.nif.AbstractNIFWriter;

public class TurtleNIFWriter extends AbstractNIFWriter {

    private static final String HTTP_CONTENT_TYPE = "application/x-turtle";
    private static final String LANGUAGE = "TTL";

    public TurtleNIFWriter() {
        super(HTTP_CONTENT_TYPE, LANGUAGE);
    }

}
