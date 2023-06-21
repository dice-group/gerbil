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
package org.aksw.gerbil.dataset.impl.umbc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.conll.AbstractGenericCoNLLDatasetTest;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class UMBCDatasetTest extends AbstractGenericCoNLLDatasetTest {

    public UMBCDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId, int markingId) {
        super(fileContent, text, expectedMarking, documentId, markingId);
    }

    @Override
    public InitializableDataset createDataset(File file) {
        return new UMBCDataset(file.getAbsolutePath());
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // Simple example with a single document and a single entity
        testConfigs.add(new Object[] {
                "Texans	O\nurged	O\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-LOC\ncoast	I-LOC\n,	O\na	O\nURL	O",
                "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ",
                new TypedSpanImpl(89, 11, "http://dbpedia.org/ontology/Place"), 0, 0 });
        // Example with 2 documents. We check the first entity in the first document.
        testConfigs.add(new Object[] {
                "I'm	O\nbored	O\nhere	O\nin	O\nSydney	B-LOC\n.	O\nI	O\nwant	O\nto	O\ndo	O\nsomething	O\nanyone	O\nwant	O\nto	O\nwatch	O\nmovies	O\n?	O\n\nI	O\ncan	O\nfeel	O\nthe	O\nMobile	B-ORG\nWorld	I-ORG\nCongress	I-ORG\nvibe	O\non	O\nTwitter	B-ORG\nSee	O\nyou	O\nguys	O\nin	O\nBarcelona	B-LOC\nnext	O\nweek	O\n.	O",
                "I'm bored here in Sydney . I want to do something anyone want to watch movies ? ",
                new TypedSpanImpl(18, 6, "http://dbpedia.org/ontology/Place"), 0, 0 });
        // Example with 2 documents. We check the first entity in the second document.
        testConfigs.add(new Object[] {
                "I'm	O\nbored	O\nhere	O\nin	O\nSydney	B-LOC\n.	O\nI	O\nwant	O\nto	O\ndo	O\nsomething	O\nanyone	O\nwant	O\nto	O\nwatch	O\nmovies	O\n?	O\n\nI	O\ncan	O\nfeel	O\nthe	O\nMobile	B-ORG\nWorld	I-ORG\nCongress	I-ORG\nvibe	O\non	O\nTwitter	B-ORG\nSee	O\nyou	O\nguys	O\nin	O\nBarcelona	B-LOC\nnext	O\nweek	O\n.	O",
                "I can feel the Mobile World Congress vibe on Twitter See you guys in Barcelona next week . ",
                new TypedSpanImpl(15, 21, "http://dbpedia.org/ontology/Organisation"), 1, 0 });
        // Example with 2 documents. We check the third entity in the second document.
        testConfigs.add(new Object[] {
                "I'm	O\nbored	O\nhere	O\nin	O\nSydney	B-LOC\n.	O\nI	O\nwant	O\nto	O\ndo	O\nsomething	O\nanyone	O\nwant	O\nto	O\nwatch	O\nmovies	O\n?	O\n\nI	O\ncan	O\nfeel	O\nthe	O\nMobile	B-ORG\nWorld	I-ORG\nCongress	I-ORG\nvibe	O\non	O\nTwitter	B-ORG\nSee	O\nyou	O\nguys	O\nin	O\nBarcelona	B-LOC\nnext	O\nweek	O\n.	O",
                "I can feel the Mobile World Congress vibe on Twitter See you guys in Barcelona next week . ",
                new TypedSpanImpl(69, 9, "http://dbpedia.org/ontology/Place"), 1, 2 });
        return testConfigs;
    }

}
