/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.datatypes.marking;

public enum MarkingClasses {

    IN_KB("InKB"), EE("EE"), GS_IN_KB("GSInKB");
    
    public static final int NUMBER_OF_CLASSES = MarkingClasses.values().length;
    
    private final String label;

    private MarkingClasses(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
