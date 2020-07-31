package org.aksw.gerbil.evaluate.impl.NLG;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;

import java.io.File;
import java.util.List;

public class NLGDataset extends AbstractDataset implements InitializableDataset {
    protected List<Document> documents;
    protected String refDirectory;
    NLGEvaluator nlgEvaluator = new NLGEvaluator();

    public NLGDataset(String textsDirectory){
        this.refDirectory = textsDirectory;

    }


    @Override
    public int size() {
        return documents.size();
    }

    @Override
    public List<Document> getInstances() {
        return documents;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(refDirectory));
    }


        protected List<Document> loadDocuments(File textDir) throws GerbilException {

            if ((!textDir.exists()) || (!textDir.isDirectory()))
            {
                throw new GerbilException(
                        "The given text directory (" + textDir.getAbsolutePath() + ") is not existing or not a directory.",
                        ErrorTypes.DATASET_LOADING_ERROR);
            }
            textDir.list((dir, name) -> name.matches("reference[0-7]+"));
            return documents;
        }

   /* public  File folderSize(File dir) {
        File direc = null;
        for (File f : dir.listFiles()) {
            if (f.isFile())
            direc=f;
            nlgEvaluator.f(dir.listFiles());

        }
        System.out.println("direc: "+dir);
        return direc;
    }

    */
}
