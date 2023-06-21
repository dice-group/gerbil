import org.aksw.gerbil.dataset.impl.indq.IndQNERDataset;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class IndQNerDatasetTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<>();
        //test configurations
        testConfigs.add(new Object[]{
                "Tidak adab sesuatu yang lebih kecil dan yang lebih besar daripada itu, " +
                "kecuali semua tercatat dalam kitab yang nyata (Lauh Mahfuz ).",
                "Example tweet 1",
                new String[]{"Example expected token 1"}
        });
        testConfigs.add(new Object[]{
                "Ketahuilah bahwa sesungguhnya ( bagi ) para wali Allah-B-Allah itu " +
                "tidak ada rasa takut yang menimpa mereka dan mereka pun tidak bersedih.",
                "Example tweet 2",
                new String[]{"Example expected token 2"}
        });

        return testConfigs;
    }

    private String text;
    private String tweet;
    private String[] expectedTokens;

    public IndQNerDatasetTest(String text, String tweet, String[] expectedTokens) {
        this.text = text;
        this.tweet = tweet;
        this.expectedTokens = expectedTokens;
    }

    @Test
    public void testIndQNerDataset() throws GerbilException {
        // Create an instance of the IndQNerDataset
        IndQNERDataset indQNerDataset = new IndQNERDataset();

        // Initialize the dataset
        indQNerDataset.init();

        // Get the instances/documents from the dataset
        List<Document> documents = indQNerDataset.getInstances();

        // Ensure that the documents are not null and there is at least one document
        Assert.assertNotNull(documents);
        Assert.assertTrue(documents.size() > 0);

        // Iterate over the markings in the first document
        Document document = documents.get(0);
        List<Marking> markings = document.getMarkings();
        Assert.assertNotNull(markings);
        Assert.assertTrue(markings.size() > 0);

        // Verify that the markings are NamedEntities
        for (Marking marking : markings) {
            Assert.assertTrue(marking instanceof NamedEntity);
            NamedEntity namedEntity = (NamedEntity) marking;

            // Get the mention from the named entity
            String mention = tweet.substring(namedEntity.getStartPosition(), namedEntity.getStartPosition() + namedEntity.getLength());

            // Perform assertions to check if the expected tokens match the extracted mentions
            for (String expectedToken : expectedTokens) {
                if (mention.contains(expectedToken)) {
                    Assert.assertEquals(expectedToken, mention);
                }
            }
        }

        // Clean up and close the dataset
        indQNerDataset.close();
    }
}
