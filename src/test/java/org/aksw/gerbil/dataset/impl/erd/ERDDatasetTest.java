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
package org.aksw.gerbil.dataset.impl.erd;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

@Deprecated
public class ERDDatasetTest {
    
    private static final String FREEBASE_URI = "https://www.googleapis.com/freebase";
    private static final String ERD_DATASET_PATH = "gerbil_data/datasets/erd2014/";
    private static final String TEXT_FILE = "Trec_beta.query.txt";
    private static final String ANNOTATION_FILE = "Trec_beta.annotation.txt";

    private static List<Document> EXPECTED_DOCUMENTS;
    private static List<Document> LOADED_DOCUMENTS;
    
    private static List<String> DOCUMENT_URI;
    
    @BeforeClass
    public static void prepareResourcesToTest() throws GerbilException {
        
        DOCUMENT_URI = new ArrayList<String>();
        
        DOCUMENT_URI.add("http://ERD-Test/Trec_beta.query.txt");
        
        loadExpectedSet();
        
        loadDatasets();
        
//        generateTerminalOutputForLoadedErdDatasets();
                
    }
    
    @Test
    public void checkTrecData() {
        
        int min = 5;
        int max = 10;
        List<ERDTrec> treclist = new ArrayList<>();
        List<Integer> linelist = new ArrayList<>();
        List<String> textlist = new ArrayList<>();
        List<String> second_phrase_text = new ArrayList<>();
        
        assertThat(treclist.size(), is(0));
        
        int lineColumnCount = 0;
        ERDTrec dtrec = null;
        int randomtrecs = (int)(Math.random() * max) + min;
        for (int i = 0; i < randomtrecs; i++) {
            
            String id = "TREC-" + i;
            int randomTextpart = (int)(Math.random() * 5) + 2;
            String text = "";
            for (int j = 0; j < randomTextpart; j++){
                String randomText = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(max) + min);
                text = text + randomText + " ";
                if (j==1) second_phrase_text.add(randomText);
            }
            text = id + "\t" + text.substring(0,text.length()-1);
            textlist.add(text);
            
            dtrec = new ERDTrec(text, dtrec);
            treclist.add(dtrec);
            
            lineColumnCount = lineColumnCount + text.length() + 1;
            linelist.add(lineColumnCount - 1);

        }
        
        for (ERDTrec trec : treclist) assertThat(trec, is(notNullValue()));
        
        for (int i = 0; i < treclist.size(); i++) {

            assertThat(treclist.get(i).getColumnCount(), is(linelist.get(i)));
            assertThat(treclist.get(i).getLineNumber(), is(i));
            assertThat(treclist.get(i).getLine().equals(textlist.get(i)), is(true));
            assertThat((treclist.get(i).getTextPosition(second_phrase_text.get(i)) > 0), is(true));
            
        }
        
    }
    
    @Test
    public void checkLoadDatasets() throws GerbilException {
        
        assertThat(LOADED_DOCUMENTS.size(), is(1));
        
        int countmarkings = 0;
        for (Document tmp : LOADED_DOCUMENTS){
            countmarkings += tmp.getMarkings().size();
        }
        
        assertThat(countmarkings, is(59));

    }
    
    @Test
    public void checkExpectedDataset() {
        
        assertThat(EXPECTED_DOCUMENTS.size(), is(1));
        
        int countmarkings = 0;
        for (Document tmp : EXPECTED_DOCUMENTS){
            countmarkings += tmp.getMarkings().size();
        }
        
        assertThat(countmarkings, is(16));
        
    }
    
    @Test
    public void checkExpectedDatasetIsSubsetOfLoadedDataset() throws GerbilException {

        for (int i = 0; i < EXPECTED_DOCUMENTS.size(); i++){
            for (int j = 0; j < EXPECTED_DOCUMENTS.get(i).getMarkings().size(); j++){
                
                String ld_mark = LOADED_DOCUMENTS.get(i).getMarkings().get(j).toString();
                ld_mark = ld_mark.substring(1, ld_mark.length()-1);
                String[] ld_parts = ld_mark.split(" ");
                
                assertThat(ld_parts.length, is(3));
                
                String ld_start = ld_parts[0].substring(0, ld_parts[0].length()-1);
                String ld_length = ld_parts[1].substring(0, ld_parts[1].length()-1);
                String ld_uri = ld_parts[2].substring(1 + FREEBASE_URI.length(), ld_parts[2].length()-1);
                ld_uri = ld_uri.replaceAll("_", " ");
                
                String ex_mark = EXPECTED_DOCUMENTS.get(i).getMarkings().get(j).toString();
                ex_mark = ex_mark.substring(1, ex_mark.length()-1);
                String[] ex_parts = ex_mark.split(" ");
                
                assertThat(ex_parts.length, is(3));
                
                String ex_start = ex_parts[0].substring(0, ex_parts[0].length()-1);
                String ex_length = ex_parts[1].substring(0, ex_parts[1].length()-1);
                String ex_uri = ex_parts[2].substring(1 + FREEBASE_URI.length(), ex_parts[2].length()-1);
                ex_uri = ex_uri.replaceAll("_", " ");
                
                assertEquals(ld_start, ex_start);
                assertEquals(ld_length, ex_length);
                assertEquals(ld_uri, ex_uri);
                
            }
        }
        
    }
    
    @Test
    public void checkLoadedDatasetFindInDatasetFiles() throws GerbilException {
        
        String text = getString(ERD_DATASET_PATH + ANNOTATION_FILE);
        
        for (int i = 0; i < LOADED_DOCUMENTS.size(); i++){
            for (int j = 0; j < LOADED_DOCUMENTS.get(i).getMarkings().size(); j++){
                
                String mark = LOADED_DOCUMENTS.get(i).getMarkings().get(j).toString();
                mark = mark.substring(1, mark.length()-1);
                String[] parts = mark.split(" ");
                
                assertThat(parts.length, is(3));
                
                String start = parts[0].substring(0, parts[0].length()-1);
                String length = parts[1].substring(0, parts[1].length()-1);
                String uri = parts[2].substring(1 + FREEBASE_URI.length(), parts[2].length()-1);
                                
                List<String> searchString = new ArrayList<>();
                int pos = -1;
                while ((pos = text.indexOf(uri, pos + 1)) != -1) {
                    int point = pos + uri.length() + 1;
                    searchString.add(text.substring(point, point+Integer.valueOf(length)));
                }

                String match = returnStringPositionInFile(ERD_DATASET_PATH + TEXT_FILE, Integer.valueOf(start), Integer.valueOf(length));

                assertThat((searchString.contains(match)), is(true));
            }
        }
        
    }
    
    @SuppressWarnings("resource")
    private static void loadDatasets() throws GerbilException {
        
        assertThat(LOADED_DOCUMENTS, is(nullValue()));
        
        LOADED_DOCUMENTS = new ArrayList<>();
        
        assertThat(LOADED_DOCUMENTS, is(notNullValue()));
        assertThat(LOADED_DOCUMENTS.size(), is(0));
        
        ERDDataset dataset = new ERDDataset(ERD_DATASET_PATH + TEXT_FILE, ERD_DATASET_PATH + ANNOTATION_FILE);
        dataset.setName("Erd-Test");
        dataset.init();
        LOADED_DOCUMENTS.addAll(dataset.getInstances());
        
    }
    
    private static void loadExpectedSet() {
        
        assertThat(EXPECTED_DOCUMENTS, is(nullValue()));
        
        EXPECTED_DOCUMENTS = new ArrayList<>();
        
        assertThat(EXPECTED_DOCUMENTS, is(notNullValue()));
        assertThat(EXPECTED_DOCUMENTS.size(), is(0));
        
        List<String> text = new ArrayList<>();
        List<List<Marking>> markings = new ArrayList<>();
        
        text.add("..TREC-1.adobe indian houses..TREC-2.atypical squamous cells..TREC-3.battles in the civil war..TREC-4.becoming a paralegal..TREC-5.best long term care insurance..TREC-6.blue throated hummingbird..TREC-7.bowflex power pro..TREC-8.brooks brothers clearance..TREC-9.butter and margarine..TREC-10.california franchise tax board..TREC-11.cass county missouri..TREC-12.civil right movement..TREC-13.condos in florida..TREC-14.culpeper national cemetery..TREC-15.dangers of asbestos..TREC-16.designer dog breeds..TREC-17.discovery channel store..TREC-18.dog clean up bags..TREC-19.dogs for adoption..TREC-20.dutchess county tourism..TREC-21.earn money at home..TREC-22.east ridge high school..TREC-23.electronic skeet shoot..TREC-24.equal opportunity employer..TREC-25.er tv show..TREC-26.fact on uranus..TREC-27.fickle creek farm..TREC-28.french lick resort and casino..TREC-29.furniture for small spaces..TREC-30.gmat prep classes..TREC-31.gs pay rate..TREC-32.how to build a fence..TREC-33.hp mini 2140..TREC-34.illinois state tax..TREC-35.income tax return online..TREC-36.indiana child support..");
        
        markings.add(Arrays.asList(
            (Marking) new NamedEntity(203, 7, "https://www.googleapis.com/freebase/m/04cnvy"),
            (Marking) new NamedEntity(229, 15, "https://www.googleapis.com/freebase/m/03d452"),
            (Marking) new NamedEntity(333, 20, "https://www.googleapis.com/freebase/m/0nfgq"),
            (Marking) new NamedEntity(393, 5, "https://www.googleapis.com/freebase/m/020ys5"),
            (Marking) new NamedEntity(403, 7, "https://www.googleapis.com/freebase/m/02xry"),
            (Marking) new NamedEntity(420, 26, "https://www.googleapis.com/freebase/m/0c4tkd"),
            (Marking) new NamedEntity(601, 15, "https://www.googleapis.com/freebase/m/0dc3_"),
            (Marking) new NamedEntity(662, 22, "https://www.googleapis.com/freebase/m/03ck4lv"),
            (Marking) new NamedEntity(662, 22, "https://www.googleapis.com/freebase/m/027311j"),
            (Marking) new NamedEntity(662, 22, "https://www.googleapis.com/freebase/m/0bs8gsb"),
            (Marking) new NamedEntity(762, 2, "https://www.googleapis.com/freebase/m/0180mw"),
            (Marking) new NamedEntity(833, 29, "https://www.googleapis.com/freebase/m/02761b3"),
            (Marking) new NamedEntity(872, 9, "https://www.googleapis.com/freebase/m/0c_jw"),
            (Marking) new NamedEntity(913, 4, "https://www.googleapis.com/freebase/m/065y10k"),
            (Marking) new NamedEntity(1008, 14, "https://www.googleapis.com/freebase/m/03v0t"),
            (Marking) new NamedEntity(1070, 7, "https://www.googleapis.com/freebase/m/03v1s")
        ));
        
        EXPECTED_DOCUMENTS = new ArrayList<>();
        
        for (int i = 0; i < 1; i++){
            EXPECTED_DOCUMENTS.add(new DocumentImpl(text.get(i), DOCUMENT_URI.get(i), markings.get(i)));
        }
        
    }
    
    private String getString(String filePath) throws GerbilException {
        
        RandomAccessFile raf;
        String out = "";
        try {
            File file = new File(filePath);
            byte[] filedata = new byte[(int) file.length()];
            raf = new RandomAccessFile(file, "r");
            raf.readFully(filedata);
            out = new String(filedata);
            raf.close();
        } catch (IOException e) {
            throw new GerbilException("Exception while reading annotation file of dataset.", e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        
        return out;
        
    }
    
    private String returnStringPositionInFile(String filePath, int position, int length) throws GerbilException { 
        
        RandomAccessFile raf;
        String out = "";
        try {
            File file = new File(filePath);
            byte[] search = new byte[length];
            raf = new RandomAccessFile(file, "r");
            raf.seek(position);
            raf.readFully(search);
            raf.close();
            out = new String(search);          
        } catch (IOException e) {
            throw new GerbilException("Exception while reading text file of dataset.", e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        
        return out;
        
    }
    
//    private static void generateTerminalOutputForLoadedErdDatasets() throws GerbilException {
//        
//        System.out.println("=========================================================");
//        System.out.println("===================== Documents [" + LOADED_DOCUMENTS.size() + "] =====================");
//        for (int i = 0; i < LOADED_DOCUMENTS.size(); i++){
//            Document doc = LOADED_DOCUMENTS.get(i);
//            System.out.println("=========================================================");
//            System.out.println("Document-URI: " + doc.getDocumentURI());
//            System.out.println("==================== Markings [" + doc.getMarkings().size() + "] ====================");
//            for (Marking mark : doc.getMarkings()){
//                System.out.println(mark.toString());
//            }
//        }
//        System.out.println("=========================================================");
//        
//    }
    
}
