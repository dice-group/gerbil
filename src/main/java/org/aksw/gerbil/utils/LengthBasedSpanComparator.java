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
package org.aksw.gerbil.utils;

import java.util.Comparator;

import org.aksw.gerbil.transfer.nif.Span;

/**
 * Comparator that does the comparison based on the length between Spans (i.e
 * Named Entities)
 * 
 * @author Nikit
 *
 */
public class LengthBasedSpanComparator implements Comparator<Span> {
	@Override
	public int compare(Span s1, Span s2) {
		// sort them based on their length
		int diff = s1.getLength() - s2.getLength();
		if (diff == 0) {
			return 0;
		} else if (diff < 0) {
			return -1;
		} else {
			return 1;
		}
	}
}
