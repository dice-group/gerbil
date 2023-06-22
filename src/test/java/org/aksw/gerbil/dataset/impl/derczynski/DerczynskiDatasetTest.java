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
package org.aksw.gerbil.dataset.impl.derczynski;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.conll.AbstractGenericCoNLLDatasetTest;
import org.aksw.gerbil.dataset.impl.derczysnki.DerczynskiDataset;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DerczynskiDatasetTest extends AbstractGenericCoNLLDatasetTest {

    public DerczynskiDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId,
            int markingId) {
        super(fileContent, text, expectedMarking, documentId, markingId);
    }

    @Override
    public InitializableDataset createDataset(File file) {
        return new DerczynskiDataset(file.getAbsolutePath());
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] {
                "#Astros	http://dbpedia.org/resource/Houston_Astros	B-sportsteam	HT\nlineup		O	NN\nfor		O	IN\ntonight		O	NN\n.		O	0\nKeppinger	http://dbpedia.org/resource/Jeff_Keppinger	B-person	NNP\nsits		O	VBZ\n,		O	,\nDowns	http://dbpedia.org/resource/Brodie_Downs	B-person	NNP\nplays		O	VBZ\n2B		O	NN\n,		O	,\nCJ	NIL	B-person	NNP\nbats		O	VBZ\n5th		O	JJ\n.		O	0\n@alysonfooter		O	USR\nhttp://bit.ly/bHvgCS		O	URL",
                "#Astros lineup for tonight . Keppinger sits , Downs plays 2B , CJ bats 5th . @alysonfooter http://bit.ly/bHvgCS ",
                new TypedNamedEntity(0, 7, "http://dbpedia.org/resource/Houston_Astros",
                        new HashSet<>(Arrays.asList("http://dbpedia.org/ontology/SportsTeam"))),
                0, 0 });
        testConfigs.add(new Object[] {
                "#Astros	http://dbpedia.org/resource/Houston_Astros	B-sportsteam	HT\nlineup		O	NN\nfor		O	IN\ntonight		O	NN\n.		O	0\nKeppinger	http://dbpedia.org/resource/Jeff_Keppinger	B-person	NNP\nsits		O	VBZ\n,		O	,\nDowns	http://dbpedia.org/resource/Brodie_Downs	B-person	NNP\nplays		O	VBZ\n2B		O	NN\n,		O	,\nCJ	NIL	B-person	NNP\nbats		O	VBZ\n5th		O	JJ\n.		O	0\n@alysonfooter		O	USR\nhttp://bit.ly/bHvgCS		O	URL",
                "#Astros lineup for tonight . Keppinger sits , Downs plays 2B , CJ bats 5th . @alysonfooter http://bit.ly/bHvgCS ",
                new TypedNamedEntity(29, 9, "http://dbpedia.org/resource/Jeff_Keppinger",
                        new HashSet<>(Arrays.asList("http://dbpedia.org/ontology/Person"))),
                0, 1 });
        testConfigs.add(new Object[] {
                "#Astros	O	B-sportsteam	HT\nlineup	O	I-sportsteam	NN\nfor		O	IN\ntonight		O	NN\n.		O	0\nJeff	http://dbpedia.org/resource/Jeff_Keppinger	B-person	NNP\nKeppinger	http://dbpedia.org/resource/Jeff_Keppinger	I-person	NNP\nsits		O	VBZ\n,		O	,\nDowns	http://dbpedia.org/resource/Brodie_Downs	B-person	NNP\nplays		O	VBZ\n2B		O	NN\n,		O	,\nCJ	NIL	B-person	NNP\nbats		O	VBZ\n5th		O	JJ\n.		O	0\n@alysonfooter		O	USR\nhttp://bit.ly/bHvgCS		O	URL",
                "#Astros lineup for tonight . Jeff Keppinger sits , Downs plays 2B , CJ bats 5th . @alysonfooter http://bit.ly/bHvgCS ",
                new TypedSpanImpl(0, 14, new HashSet<>(Arrays.asList("http://dbpedia.org/ontology/SportsTeam"))),
                0, 0 });
        return testConfigs;
    }

}
