package org.aksw.gerbil.dataset.impl.indq;

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

@RunWith(Parameterized.class)
public class IndQNERDatasetTest extends AbstractGenericCoNLLDatasetTest {

    public IndQNERDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId, int markingId) {
        super(fileContent, text, expectedMarking, documentId, markingId);
    }

    @Override
    public InitializableDataset createDataset(File file) {
        return new IndQNERDataset(file.getAbsolutePath());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<>();
        // test configurations
        testConfigs.add(new Object[] {
                "Dia	O\nmenarik	O\ntangannya	O\n,	O\ntiba-tiba	O\nia	O\n(	O\ntangan	O\nitu	O\n)	O\nmenjadi	O\nputih	B-Color\n(	O\nbercahaya	O\n)	O\nbagi	O\norang-orang	O\nyang	O\nmelihat	O\n(	O\n-nya	O\n)	O\n.	O\n\nPara	O\npemuka	O\nkaum	O\nFir‘aun	B-Person\nberkata	O\n,	O\n“	O\nSesungguhnya	O\norang	O\nini	O\nbenar-benar	O\npenyihir	O\nyang	O\nsangat	O\npandai	O\n.	O\n\nDia	O\nhendak	O\nmengusir	O\nkamu	O\ndari	O\nnegerimu	O\n.	O",
                "Dia menarik tangannya , tiba-tiba ia ( tangan itu ) menjadi putih ( bercahaya ) bagi orang-orang yang melihat ( -nya ) . ",
                new TypedSpanImpl(60, 5, "https://corpus.quran.com/concept.jsp?id=color"), 0, 0 });
        testConfigs.add(new Object[] {
                "Dia	O\nmenarik	O\ntangannya	O\n,	O\ntiba-tiba	O\nia	O\n(	O\ntangan	O\nitu	O\n)	O\nmenjadi	O\nputih	B-Color\n(	O\nbercahaya	O\n)	O\nbagi	O\norang-orang	O\nyang	O\nmelihat	O\n(	O\n-nya	O\n)	O\n.	O\n\nPara	O\npemuka	O\nkaum	O\nFir‘aun	B-Person\nberkata	O\n,	O\n“	O\nSesungguhnya	O\norang	O\nini	O\nbenar-benar	O\npenyihir	O\nyang	O\nsangat	O\npandai	O\n.	O\n\nDia	O\nhendak	O\nmengusir	O\nkamu	O\ndari	O\nnegerimu	O\n.	O",
                "Para pemuka kaum Fir‘aun berkata , “ Sesungguhnya orang ini benar-benar penyihir yang sangat pandai . ",
                new TypedSpanImpl(17, 7, "http://dbpedia.org/ontology/Person"), 1, 0 });

        return testConfigs;
    }

}
