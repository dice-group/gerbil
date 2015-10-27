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
package org.aksw.gerbil.annotators;


import it.unipi.di.acube.batframework.problems.TopicSystem;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.bat.annotator.FOXAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;

public class FOXAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private WikipediaApiInterface wikiApi;
    // don't cache me in the final version
    private static boolean        cache = false;

    public FOXAnnotatorConfig(WikipediaApiInterface wikiApi) {
        super(FOXAnnotator.NAME, cache, ExperimentType.Sa2KB);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        return new FOXAnnotator(wikiApi);
    }
}
