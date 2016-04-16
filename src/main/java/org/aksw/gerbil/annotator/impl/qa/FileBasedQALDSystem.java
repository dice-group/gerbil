package org.aksw.gerbil.annotator.impl.qa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.QALD_Loader;
import org.apache.commons.io.IOUtils;

public class FileBasedQALDSystem extends InstanceListBasedAnnotator implements QASystem {

    protected static List<Document> loadInstances(List<String> qaldFiles, List<String> questionUriPrefixes)
            throws GerbilException {
        List<Document> instances = new ArrayList<Document>();
        for (int i = 0; i < qaldFiles.size(); ++i) {
            loadInstances(qaldFiles.get(i), questionUriPrefixes.get(i), instances);
        }
        return instances;
    }

    protected static void loadInstances(String qaldFile, String questionUriPrefix, List<Document> instances)
            throws GerbilException {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(qaldFile);
            List<IQuestion> questions = QALD_Loader.loadJSON(fin);
            for (IQuestion question : questions) {
                instances.add(QAUtils.translateQuestion(question, questionUriPrefix + question.getId()));
            }
        } catch (FileNotFoundException e) {
            throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

    /**
     * Constructor.
     * 
     * @param qaldFiles
     *            the QALD files that contain the responses of this annotator
     * @param questionUriPrefixes
     *            the URI prefixes that are used for the single QALD files
     * @throws GerbilException
     *             if one of the given files can not be loaded correctly
     */
    public FileBasedQALDSystem(List<String> qaldFiles, List<String> questionUriPrefixes) throws GerbilException {
        this(null, qaldFiles, questionUriPrefixes);
    }

    /**
     * Constructor.
     * 
     * @param name
     *            the name of the annotator
     * @param qaldFiles
     *            the QALD files that contain the responses of this annotator
     * @param questionUriPrefixes
     *            the URI prefixes that are used for the single QALD files
     * @throws GerbilException
     *             if one of the given files can not be loaded correctly
     */
    public FileBasedQALDSystem(String name, List<String> qaldFiles, List<String> questionUriPrefixes)
            throws GerbilException {
        super(name, loadInstances(qaldFiles, questionUriPrefixes));
    }

}
