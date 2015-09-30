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

/**
 * A class implementing this interface contains a URI that points to the meaning
 * of this object as well as a confidence score for this linkage.
 * 
 * @deprecated This interface will be removed in the future since it is only a
 *             combination of the {@link Meaning} and the {@link ScoredMarking}
 *             interfaces.
 * 
 * @author Michael Röder
 * 
 */
@Deprecated
public interface ScoredMeaning extends Meaning, ScoredMarking {

}
