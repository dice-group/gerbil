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
package org.aksw.gerbil.bat.annotator.nif;


import it.unipi.di.acube.batframework.data.Mention;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BAT2NIF_TranslationHelper {

    public static Document createAnnotatedDocument(String text) {
        return createAnnotatedDocument(text, null);
    }

    public static Document createAnnotatedDocument(String text,
            Set<Mention> mentions) {
        List<Marking> markings = new ArrayList<Marking>();
        if (mentions != null) {
            for (Mention mention : mentions) {
                markings.add(translateMention2Annotation(mention));
            }
        }
        return new DocumentImpl(text, markings);
    }

    public static Marking translateMention2Annotation(Mention mention) {
        return new SpanImpl(mention.getPosition(), mention.getLength());
    }
}
