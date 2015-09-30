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

import java.util.Set;

public class MeaningEqualityChecker {

    public static final boolean overlaps(Meaning m1, Meaning m2) {
        return overlaps(m1.getUris(), m2.getUris());
    }

    public static final boolean overlaps(Set<String> uris1, Set<String> uris2) {
        Set<String> smaller, larger;
        if (uris1.size() > uris2.size()) {
            smaller = uris2;
            larger = uris1;
        } else {
            smaller = uris1;
            larger = uris2;
        }
        for (String uri : smaller) {
            if (larger.contains(uri)) {
                return true;
            }
        }
        return false;
    }

}
