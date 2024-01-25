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
package org.aksw.gerbil.dataset.impl.ritter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.conll.AbstractGenericCoNLLDatasetTest;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RitterDatasetTest extends AbstractGenericCoNLLDatasetTest {

    public RitterDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId, int markingId) {
        super(fileContent, text, expectedMarking, documentId, markingId);
    }

    @Override
    public InitializableDataset createDataset(File file) {
        return new RitterDataset(file.getAbsolutePath());
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // TODO: The problem of leading white spaces in front of apostrophes needs a
        // solution (e.g. "I 'm")
        testConfigs.add(new Object[] {
                "@paulwalk	O\nIt	O\n's	O\nthe	O\nview	O\nfrom	O\nwhere	O\nI	O\n'm	O\nliving	O\nfor	O\ntwo	O\nweeks	O\n.	O\nEmpire	B-facility\nState	I-facility\nBuilding	I-facility\n=	O\nESB	B-facility\n.	O\nPretty	O\nbad	O\nstorm	O\nhere	O\nlast	O\nevening	O\n.	O\n	\nFrom	O\nGreen	O\nNewsfeed	O\n:	O\nAHFA	B-other\nextends	O\ndeadline	O\nfor	O\nSage	B-other\nAward	I-other\nto	O\nNov	O\n.	O\n5	O\nhttp://tinyurl.com/24agj38	O",
                "@paulwalk It 's the view from where I 'm living for two weeks. Empire State Building= ESB. Pretty bad storm here last evening.",
                new TypedSpanImpl(63, 21, new HashSet<>(Arrays.asList("http://dbpedia.org/ontology/Place"))), 0, 0 });
        testConfigs.add(new Object[] {
                "@paulwalk	O\nIt	O\n's	O\nthe	O\nview	O\nfrom	O\nwhere	O\nI	O\n'm	O\nliving	O\nfor	O\ntwo	O\nweeks	O\n.	O\nEmpire	B-facility\nState	I-facility\nBuilding	I-facility\n=	O\nESB	B-facility\n.	O\nPretty	O\nbad	O\nstorm	O\nhere	O\nlast	O\nevening	O\n.	O\n	\nFrom	O\nGreen	O\nNewsfeed	O\n:	O\nAHFA	B-other\nextends	O\ndeadline	O\nfor	O\nSage	B-other\nAward	I-other\nto	O\nNov	O\n.	O\n5	O\nhttp://tinyurl.com/24agj38	O",
                "@paulwalk It 's the view from where I 'm living for two weeks. Empire State Building= ESB. Pretty bad storm here last evening.",
                new TypedSpanImpl(86, 3, new HashSet<>(Arrays.asList("http://dbpedia.org/ontology/Place"))), 0, 1 });
        testConfigs.add(new Object[] {
                "@paulwalk	O\nIt	O\n's	O\nthe	O\nview	O\nfrom	O\nwhere	O\nI	O\n'm	O\nliving	O\nfor	O\ntwo	O\nweeks	O\n.	O\nEmpire	B-facility\nState	I-facility\nBuilding	I-facility\n=	O\nESB	B-facility\n.	O\nPretty	O\nbad	O\nstorm	O\nhere	O\nlast	O\nevening	O\n.	O\n	\nFrom	O\nGreen	O\nNewsfeed	O\n:	O\nAHFA	B-other\nextends	O\ndeadline	O\nfor	O\nSage	B-other\nAward	I-other\nto	O\nNov	O\n.	O\n5	O\nhttp://tinyurl.com/24agj38	O",
                "From Green Newsfeed: AHFA extends deadline for Sage Award to Nov. 5 http://tinyurl.com/24agj38",
                new TypedSpanImpl(21, 4, new HashSet<>(Arrays.asList("http://dbpedia.org/ontology/Unknown"))), 1, 0 });
        return testConfigs;
    }

}
