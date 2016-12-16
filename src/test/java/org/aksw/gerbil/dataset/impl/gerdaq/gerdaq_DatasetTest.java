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
package org.aksw.gerbil.dataset.impl.gerdaq;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aksw.gerbil.datatypes.ErrorTypes;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

import org.apache.commons.lang3.StringEscapeUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

public class gerdaq_DatasetTest {
    
    private static final String WIKIPEDIA_URI = "http://en.wikipedia.org/wiki/";
    private static final String GERDAQ_DATASET_DEVEL = "gerbil_data/datasets/gerdaq_1.0/";

    private static List<List<Document>> EXPECTED_DOCUMENTS;
    private static List<List<Document>> LOADED_DOCUMENTS;
    
    private static List<String> DOCUMENT_URI;
    
    @BeforeClass
    public static void prepareResourcesToTest() throws GerbilException {
        
        /**
         * 0 = devel
         * 1 = test
         * 2 = trainingA
         * 3 = trainingB
         */
        
        DOCUMENT_URI = new ArrayList();
        
        DOCUMENT_URI.add("http://Gerdaq-Test/gerdaq_devel.xml");
        DOCUMENT_URI.add("http://Gerdaq-Test/gerdaq_test.xml");
        DOCUMENT_URI.add("http://Gerdaq-Test/gerdaq_trainingA.xml");
        DOCUMENT_URI.add("http://Gerdaq-Test/gerdaq_trainingB.xml");
        
        loadExpectedSet();
        
        loadDatasets();
        
//        generateTerminalOutputForLoadedDatasets();
        
    }

    @Test
    public void checkLoadDatasets() throws GerbilException {
        
        assertThat(LOADED_DOCUMENTS.size(), is(4));
        
        int countdocuments = 0;
        int countmarkings = 0;
        for (int i = 0; i < LOADED_DOCUMENTS.size(); i++){
            countdocuments += LOADED_DOCUMENTS.get(i).size();
            for (int j = 0; j < LOADED_DOCUMENTS.get(i).size(); j++){
                countmarkings += LOADED_DOCUMENTS.get(i).get(j).getMarkings().size();
            }
        }
        
        assertThat(countdocuments, is(887));
        assertThat(countmarkings, is(1815));

    }
    
    @Test
    public void checkExpectedDataset() {
        
        assertThat(EXPECTED_DOCUMENTS.size(), is(4));
        
        int countdocuments = 0;
        int countmarkings = 0;
        for (int i = 0; i < EXPECTED_DOCUMENTS.size(); i++){
            countdocuments += EXPECTED_DOCUMENTS.get(i).size();
            for (int j = 0; j < EXPECTED_DOCUMENTS.get(i).size(); j++){
                countmarkings += EXPECTED_DOCUMENTS.get(i).get(j).getMarkings().size();
            }
        }
        
        assertThat(countdocuments, is(40));
        assertThat(countmarkings, is(77));
    }
    
    @Test
    public void checkExpectedDatasetIsSubsetOfLoadedDataset() throws GerbilException {

        for (int i = 0; i < EXPECTED_DOCUMENTS.size(); i++){
            for (int j = 0; j < EXPECTED_DOCUMENTS.get(i).size(); j++){
                for (int k = 0; k < EXPECTED_DOCUMENTS.get(i).get(j).getMarkings().size(); k++){
                
                    String ld_mark = LOADED_DOCUMENTS.get(i).get(j).getMarkings().get(k).toString();
                    ld_mark = ld_mark.substring(1, ld_mark.length()-1);
                    String[] ld_parts = ld_mark.split(" ");

                    assertThat(ld_parts.length, is(3));

                    String ld_start = ld_parts[0].substring(0, ld_parts[0].length()-1);
                    String ld_length = ld_parts[1].substring(0, ld_parts[1].length()-1);
                    String ld_uri = ld_parts[2].substring(1 + WIKIPEDIA_URI.length(), ld_parts[2].length()-1);
                    ld_uri = ld_uri.replace('_', ' ');

                    String ex_mark = EXPECTED_DOCUMENTS.get(i).get(j).getMarkings().get(k).toString();
                    ex_mark = ex_mark.substring(1, ex_mark.length()-1);
                    String[] ex_parts = ex_mark.split(" ");

                    assertThat(ex_parts.length, is(3));

                    String ex_start = ex_parts[0].substring(0, ex_parts[0].length()-1);
                    String ex_length = ex_parts[1].substring(0, ex_parts[1].length()-1);
                    String ex_uri = ex_parts[2].substring(1 + WIKIPEDIA_URI.length(), ex_parts[2].length()-1);
                    ex_uri = ex_uri.replace('_', ' ');

                    assertEquals(ld_start, ex_start);
                    assertEquals(ld_length, ex_length);
                    assertEquals(ld_uri, ex_uri);
                
                }
            }
        }
        
    }
    
    @Test
    public void checkLoadedDatasetFindInDatasetFiles() throws GerbilException {

        for (int i = 0; i < LOADED_DOCUMENTS.size(); i++){
            int curP = 0;
            int oldP = 0;
            for (int j = 0; j < LOADED_DOCUMENTS.get(i).size(); j++){
                for (int k = 0; k < LOADED_DOCUMENTS.get(i).get(j).getMarkings().size(); k++){
                
                    String mark = LOADED_DOCUMENTS.get(i).get(j).getMarkings().get(k).toString();
                    mark = mark.substring(1, mark.length()-1);
                    String[] parts = mark.split(" ");

                    assertThat(parts.length, is(3));

                    String start = parts[0].substring(0, parts[0].length()-1);
                    String uri = parts[2].substring(1 + WIKIPEDIA_URI.length(), parts[2].length()-1);
                    uri = uri.replace('_', ' ');

                    String filePath = GERDAQ_DATASET_DEVEL + DOCUMENT_URI.get(i).substring(19);

                    try {
                        uri = new String(uri.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new GerbilException("The given string >" + uri + "< is not in UTF-8 format.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
                    }

                    if (curP != Integer.valueOf(start)) {
                        oldP = curP;
                        curP = Integer.valueOf(start);
                    }

                    int pos = checkStringInFileRange(filePath, curP, oldP, uri);

                    assertThat((pos>0), is(true));
                
                }
            }
        }
        
    }
    
    private static void loadDatasets() throws GerbilException {

        assertThat(LOADED_DOCUMENTS, is(nullValue()));
        
        File[] fileArr = new File(GERDAQ_DATASET_DEVEL).listFiles();
        LOADED_DOCUMENTS = new ArrayList();
        for (File file : fileArr) {
            gerdaq_Dataset dataset = new gerdaq_Dataset(file.getAbsolutePath());
            dataset.setName("Gerdaq-Test");
            dataset.init();
            LOADED_DOCUMENTS.add(dataset.getInstances());
        }
        
        assertThat(LOADED_DOCUMENTS, is(notNullValue()));
        assertThat(LOADED_DOCUMENTS.size(), is(fileArr.length));
        
        for (List<Document> docList : LOADED_DOCUMENTS) {
            assertThat(docList.size(), is(not(0)));
        }
        
    }
    
    private static void loadExpectedSet() {
        
        assertThat(EXPECTED_DOCUMENTS, is(nullValue()));
        
        EXPECTED_DOCUMENTS = new ArrayList();
        
        List<String> text = new ArrayList();
        List<List<Marking>>[] markings = new ArrayList[4];
        
        for (int i = 0; i < markings.length; i++){
            markings[i] = new ArrayList();
        }
        
        text.add("<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<dataset><instance>loris <annotation rank_0_id=\"44017\" rank_0_score=\"0.925555555556\" rank_0_title=\"Candle\">candle</annotation> sampler</instance><instance><annotation rank_0_id=\"230699\" rank_0_score=\"0.666666666667\" rank_0_title=\"Conveyancing\">buying land</annotation> and <annotation rank_0_id=\"21883824\" rank_0_score=\"1.0\" rank_0_title=\"Arizona\">arizona</annotation></instance><instance>hip gry pl</instance><instance><annotation rank_0_id=\"42925\" rank_0_score=\"0.741111111111\" rank_0_title=\"Pine (email client)\">webpine email</annotation></instance><instance><annotation rank_0_id=\"917917\" rank_0_score=\"0.852222222222\" rank_0_title=\"Fundraising\">fundraisers</annotation> <annotation rank_0_id=\"128149\" rank_0_score=\"0.888888888889\" rank_0_title=\"Ramseur, North Carolina\">ramseur</annotation></instance><instance>sergio cardinali</instance><instance><annotation rank_0_id=\"298049\" rank_0_score=\"0.63\" rank_0_title=\"Case Closed\">thamtulungdanh conan</annotation></instance><instance><annotation rank_0_id=\"33978\" rank_0_score=\"0.814444444444\" rank_0_title=\"Weather\">weather</annotation> <annotation rank_0_id=\"107803\" rank_0_score=\"0.777777777778\" rank_0_title=\"Truckee, California\">truckee tahoe</annotation></instance><instance><annotation rank_0_id=\"329180\" rank_0_score=\"0.888888888889\" rank_0_title=\"Girl Scout Cookies\">girl scout cookie</annotation> <annotation rank_0_id=\"155698\" rank_0_score=\"0.963333333333\" rank_0_title=\"Sales\">sales</annotation> <annotation rank_0_id=\"7123\" rank_0_score=\"0.777777777778\" rank_0_title=\"Calendar date\">dates</annotation></instance><instance><annotation rank_0_id=\"244113\" rank_0_score=\"0.925555555556\" rank_0_title=\"Lyme disease\">lyme disease</annotation> in <annotation rank_0_id=\"48830\" rank_0_score=\"0.814444444444\" rank_0_title=\"Georgia (U.S. state)\">georgia</annotation></instance><instance><annotation rank_0_id=\"167334\" rank_0_score=\"0.972222222222\" rank_0_title=\"Adult\">adult</annotation> <annotation rank_0_id=\"330541\" rank_0_score=\"0.972222222222\" rank_0_title=\"Yahoo! Groups\">yahho groups</annotation></instance></dataset>");
        text.add("<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<dataset><instance><annotation rank_0_id=\"41118\" rank_0_score=\"1.0\" rank_0_title=\"Error\">error</annotation> in <annotation rank_0_id=\"18831\" rank_0_score=\"1.0\" rank_0_title=\"Mathematics\">mathematics</annotation> <annotation rank_0_id=\"5176\" rank_0_score=\"0.925555555556\" rank_0_title=\"Calculus\">calculas</annotation></instance><instance>luther champ</instance><instance>saint <annotation rank_0_id=\"19673\" rank_0_score=\"1.0\" rank_0_title=\"MP3\">mp3</annotation> <annotation rank_0_id=\"349732\" rank_0_score=\"0.888888888889\" rank_0_title=\"Download\" rank_1_id=\"797714\" rank_1_score=\"0.666666666667\" rank_1_title=\"Music download\">download</annotation> <annotation rank_0_id=\"5052197\" rank_0_score=\"1.0\" rank_0_title=\"Elton John\">elton john</annotation></instance><instance>i'll be your <annotation rank_0_id=\"44785\" rank_0_score=\"1.0\" rank_0_title=\"Dream\">dream</annotation> u be my <annotation rank_0_id=\"23534170\" rank_0_score=\"0.963333333333\" rank_0_title=\"Fantasy\">fantasy</annotation></instance><instance>cheap <annotation rank_0_id=\"14276\" rank_0_score=\"1.0\" rank_0_title=\"Hotel\">hotels</annotation> in <annotation rank_0_id=\"961193\" rank_0_score=\"0.925555555556\" rank_0_title=\"Downtown Disney (Walt Disney World)\" rank_1_id=\"961200\" rank_1_score=\"0.925555555556\" rank_1_title=\"Downtown Disney (Disneyland Resort)\" rank_2_id=\"7977452\" rank_2_score=\"0.888888888889\" rank_2_title=\"Downtown Disney Resort Area\">downtown disney</annotation></instance><instance>down seek <annotation rank_0_id=\"18723138\" rank_0_score=\"0.972222222222\" rank_0_title=\"Game\">games</annotation></instance><instance><annotation rank_0_id=\"254496\" rank_0_score=\"0.63\" rank_0_title=\"Power Mac G5\">power mac g5</annotation> <annotation rank_0_id=\"179683\" rank_0_score=\"0.925555555556\" rank_0_title=\"Software developer\">developer</annotation> note <annotation rank_0_id=\"24077\" rank_0_score=\"0.963333333333\" rank_0_title=\"Portable Document Format\">pdf</annotation></instance><instance><annotation rank_0_id=\"397986\" rank_0_score=\"0.888888888889\" rank_0_title=\"Nail (anatomy)\">nail</annotation> <annotation rank_0_id=\"80381\" rank_0_score=\"1.0\" rank_0_title=\"Health\">healht</annotation></instance></dataset>");
        text.add("<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<dataset><instance><annotation rank_0_id=\"364646\" rank_0_score=\"0.833333333333\" rank_0_title=\"Sidney Lumet\">sidney lumet</annotation> familt</instance><instance><annotation rank_0_id=\"88771\" rank_0_score=\"0.638888888889\" rank_0_title=\"Metronome\">metronome</annotation> setting of <annotation rank_0_id=\"30967\" rank_0_score=\"0.666666666667\" rank_0_title=\"Tempo\">allegro</annotation></instance><instance><annotation rank_0_id=\"2321001\" rank_0_score=\"0.777777777778\" rank_0_title=\"South Street (Philadelphia)\">south st</annotation> <annotation rank_0_id=\"50585\" rank_0_score=\"1.0\" rank_0_title=\"Philadelphia\">philly</annotation> <annotation rank_0_id=\"183515\" rank_0_score=\"0.777777777778\" rank_0_title=\"Retail\">stores</annotation></instance><instance><annotation rank_0_id=\"287769\" rank_0_score=\"0.888888888889\" rank_0_title=\"Aloha Airlines\">aloha airlines</annotation> pbase</instance><instance><annotation rank_0_id=\"6021\" rank_0_score=\"0.694444444444\" rank_0_title=\"C (programming language)\">c book</annotation> <annotation rank_0_id=\"957316\" rank_0_score=\"0.822222222222\" rank_0_title=\"Default (computer science)\">default</annotation></instance><instance><annotation rank_0_id=\"29112639\" rank_0_score=\"0.63\" rank_0_title=\"Antique Motorcycle Club of America\">anteques motorcycles</annotation></instance><instance><annotation rank_0_id=\"73367\" rank_0_score=\"0.852222222222\" rank_0_title=\"Big business\">large company</annotation> payroll <annotation rank_0_id=\"1613082\" rank_0_score=\"0.888888888889\" rank_0_title=\"Service provider\">service providers</annotation></instance><instance>medica elga</instance><instance>mendia</instance><instance><annotation rank_0_id=\"8772245\" rank_0_score=\"0.777777777778\" rank_0_title=\"Coleman Army Airfield\">colemans army surplus</annotation></instance><instance>national <annotation rank_0_id=\"32977\" rank_0_score=\"0.777777777778\" rank_0_title=\"Writing\">write</annotation> <annotation bad_0_id=\"19468510\" bad_0_score=\"0.407777777778\" bad_0_title=\"United States House of Representatives\" rank_0_id=\"223225\" rank_0_score=\"0.741111111111\" rank_0_title=\"Member of Congress\">congressman</annotation> <annotation rank_0_id=\"574821\" rank_0_score=\"0.777777777778\" rank_0_title=\"Legitimacy (law)\">legit</annotation> <annotation rank_0_id=\"39206\" rank_0_score=\"0.861111111111\" rank_0_title=\"Business\">business</annotation></instance></dataset>");
        text.add("<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<dataset><instance><annotation rank_0_id=\"80903\" rank_0_score=\"0.972222222222\" rank_0_title=\"Billie Joe Armstrong\">billie joe armstrong</annotation> <annotation rank_0_id=\"643521\" rank_0_score=\"0.963333333333\" rank_0_title=\"Fansite\">fan sites</annotation></instance><instance>bmv <annotation rank_0_id=\"4868451\" rank_0_score=\"0.888888888889\" rank_0_title=\"Debt management plan\">debt management</annotation></instance><instance>malenuces</instance><instance><annotation rank_0_id=\"3743931\" rank_0_score=\"0.814444444444\" rank_0_title=\"Rent-to-own\">rental stores\"</annotation> \"<annotation rank_0_id=\"63503\" rank_0_score=\"0.925555555556\" rank_0_title=\"Duluth, Minnesota\">duluth mn</annotation></instance><instance><annotation rank_0_id=\"2249807\" rank_0_score=\"1.0\" rank_0_title=\"Houston Police Department\">houston police</annotation> car auctin</instance><instance><annotation rank_0_id=\"28191\" rank_0_score=\"0.777777777778\" rank_0_title=\"Snow\">snow</annotation> <annotation rank_0_id=\"4400\" rank_0_score=\"0.852222222222\" rank_0_title=\"Bear\">bear</annotation> <annotation rank_0_id=\"14276\" rank_0_score=\"0.963333333333\" rank_0_title=\"Hotel\">hotel</annotation></instance><instance><annotation bad_0_id=\"1100889\" bad_0_score=\"0.63\" bad_0_title=\"Fernand Point\" rank_0_id=\"19280445\" rank_0_score=\"0.777777777778\" rank_0_title=\"Fernand\">fernand</annotation> <annotation bad_0_id=\"19094931\" bad_0_score=\"0.147777777778\" bad_0_title=\"Alain Chapel\" rank_0_id=\"27340118\" rank_0_score=\"0.852222222222\" rank_0_title=\"Alain (surname)\">alain</annotation></instance><instance><annotation bad_0_id=\"14485161\" bad_0_score=\"0.185555555556\" bad_0_title=\"Jason Colwell\" rank_0_id=\"954975\" rank_0_score=\"0.741111111111\" rank_0_title=\"Colwell\">colweel</annotation></instance><instance>harry and henry <annotation rank_0_id=\"730219\" rank_0_score=\"0.963333333333\" rank_0_title=\"Bathing\">bath</annotation> <annotation rank_0_id=\"240410\" rank_0_score=\"0.814444444444\" rank_0_title=\"Product (business)\">products</annotation></instance><instance><annotation rank_0_id=\"3679088\" rank_0_score=\"0.741111111111\" rank_0_title=\"Marriott Hotels &amp; Resorts\">amariot hotels</annotation></instance><instance><annotation rank_0_id=\"33127\" rank_0_score=\"0.916666666667\" rank_0_title=\"Wisconsin\">wisconsin</annotation> <annotation rank_0_id=\"406786\" rank_0_score=\"0.888888888889\" rank_0_title=\"Probation\">probation</annotation> <annotation rank_0_id=\"1782724\" rank_0_score=\"0.63\" rank_0_title=\"Probation officer\">parole office</annotation></instance></dataset>");

        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(146, 6, "http://en.wikipedia.org/wiki/Candle")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(283, 11, "http://en.wikipedia.org/wiki/Conveyancing"),
            (Marking) new NamedEntity(387, 7, "http://en.wikipedia.org/wiki/Arizona")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(554, 13, "http://en.wikipedia.org/wiki/Pine_(email_client)")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(689, 11, "http://en.wikipedia.org/wiki/Fundraising"),
            (Marking) new NamedEntity(814, 7, "http://en.wikipedia.org/wiki/Ramseur,_North_Carolina")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(970, 20, "http://en.wikipedia.org/wiki/Case_Closed")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(1107, 7, "http://en.wikipedia.org/wiki/Weather"),
            (Marking) new NamedEntity(1224, 13, "http://en.wikipedia.org/wiki/Truckee,_California")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(1366, 17, "http://en.wikipedia.org/wiki/Girl_Scout_Cookies"),
            (Marking) new NamedEntity(1479, 5, "http://en.wikipedia.org/wiki/Sales"),
            (Marking) new NamedEntity(1586, 5, "http://en.wikipedia.org/wiki/Calendar_date")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(1714, 12, "http://en.wikipedia.org/wiki/Lyme_disease"),
            (Marking) new NamedEntity(1839, 7, "http://en.wikipedia.org/wiki/Georgia_(U.S._state)")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(1962, 5, "http://en.wikipedia.org/wiki/Adult"),
            (Marking) new NamedEntity(2071, 12, "http://en.wikipedia.org/wiki/Yahoo!_Groups")
        ));
        markings[0].add(Arrays.asList(
            (Marking) new NamedEntity(2226, 25, "http://en.wikipedia.org/wiki/Pine_Mountain_State_Resort_Park")
        ));
        
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(128, 5, "http://en.wikipedia.org/wiki/Error"),
            (Marking) new NamedEntity(226, 11, "http://en.wikipedia.org/wiki/Mathematics"),
            (Marking) new NamedEntity(334, 8, "http://en.wikipedia.org/wiki/Calculus")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(483, 3, "http://en.wikipedia.org/wiki/MP3"),
            (Marking) new NamedEntity(664, 8, "http://en.wikipedia.org/wiki/Download"),
            (Marking) new NamedEntity(664, 8, "http://en.wikipedia.org/wiki/Music_download"),
            (Marking) new NamedEntity(763, 10, "http://en.wikipedia.org/wiki/Elton_John")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(890, 5, "http://en.wikipedia.org/wiki/Dream"),
            (Marking) new NamedEntity(1003, 7, "http://en.wikipedia.org/wiki/Fantasy")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(1120, 6, "http://en.wikipedia.org/wiki/Hotel"),
            (Marking) new NamedEntity(1448, 15, "http://en.wikipedia.org/wiki/Downtown_Disney_(Walt_Disney_World)"),
            (Marking) new NamedEntity(1448, 15, "http://en.wikipedia.org/wiki/Downtown_Disney_(Disneyland_Resort)"),
            (Marking) new NamedEntity(1448, 15, "http://en.wikipedia.org/wiki/Downtown_Disney_Resort_Area")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(1590, 5, "http://en.wikipedia.org/wiki/Game")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(1708, 12, "http://en.wikipedia.org/wiki/Power_Mac_G5"),
            (Marking) new NamedEntity(1829, 9, "http://en.wikipedia.org/wiki/Software_developer"),
            (Marking) new NamedEntity(1957, 3, "http://en.wikipedia.org/wiki/Portable_Document_Format")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(2085, 4, "http://en.wikipedia.org/wiki/Nail_(anatomy)"),
            (Marking) new NamedEntity(2174, 6, "http://en.wikipedia.org/wiki/Health")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(2295, 4, "http://en.wikipedia.org/wiki/Beta")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(2433, 5, "http://en.wikipedia.org/wiki/Diet_(nutrition)"),
            (Marking) new NamedEntity(2531, 11, "http://en.wikipedia.org/wiki/Cholesterol")
        ));
        markings[1].add(Arrays.asList(
            (Marking) new NamedEntity(2705, 13, "http://en.wikipedia.org/wiki/Furniture")
        ));
        
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(147, 12, "http://en.wikipedia.org/wiki/Sidney_Lumet")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(285, 9, "http://en.wikipedia.org/wiki/Metronome"),
            (Marking) new NamedEntity(400, 7, "http://en.wikipedia.org/wiki/Tempo")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(546, 8, "http://en.wikipedia.org/wiki/South_Street_(Philadelphia)"),
            (Marking) new NamedEntity(645, 6, "http://en.wikipedia.org/wiki/Philadelphia"),
            (Marking) new NamedEntity(748, 6, "http://en.wikipedia.org/wiki/Retail")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(879, 14, "http://en.wikipedia.org/wiki/Aloha_Airlines")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(1032, 6, "http://en.wikipedia.org/wiki/C_(programming_language)"),
            (Marking) new NamedEntity(1155, 7, "http://en.wikipedia.org/wiki/Default_(computer_science)")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(1299, 20, "http://en.wikipedia.org/wiki/Antique_Motorcycle_Club_of_America")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(1441, 13, "http://en.wikipedia.org/wiki/Big_business"),
            (Marking) new NamedEntity(1570, 17, "http://en.wikipedia.org/wiki/Service_provider")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(1779, 21, "http://en.wikipedia.org/wiki/Coleman_Army_Airfield")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(1926, 5, "http://en.wikipedia.org/wiki/Writing"),
            (Marking) new NamedEntity(2142, 11, "http://en.wikipedia.org/wiki/Member_of_Congress"),
            (Marking) new NamedEntity(2260, 5, "http://en.wikipedia.org/wiki/Legitimacy_(law)"),
            (Marking) new NamedEntity(2363, 8, "http://en.wikipedia.org/wiki/Business")
        ));
        markings[2].add(Arrays.asList(
            (Marking) new NamedEntity(2497, 14, "http://en.wikipedia.org/wiki/Cooking_school"),
            (Marking) new NamedEntity(2620, 15, "http://en.wikipedia.org/wiki/Mont_Saint-Michel")
        ));
        
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(154, 20, "http://en.wikipedia.org/wiki/Billie_Joe_Armstrong"),
            (Marking) new NamedEntity(272, 9, "http://en.wikipedia.org/wiki/Fansite")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(417, 15, "http://en.wikipedia.org/wiki/Debt_management_plan")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(585, 14, "http://en.wikipedia.org/wiki/Rent-to-own"),
            (Marking) new NamedEntity(707, 9, "http://en.wikipedia.org/wiki/Duluth,_Minnesota")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(842, 14, "http://en.wikipedia.org/wiki/Houston_Police_Department")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(981, 4, "http://en.wikipedia.org/wiki/Snow"),
            (Marking) new NamedEntity(1078, 4, "http://en.wikipedia.org/wiki/Bear"),
            (Marking) new NamedEntity(1177, 5, "http://en.wikipedia.org/wiki/Hotel")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(1368, 7, "http://en.wikipedia.org/wiki/Fernand"),
            (Marking) new NamedEntity(1559, 5, "http://en.wikipedia.org/wiki/Alain_(surname)")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(1759, 7, "http://en.wikipedia.org/wiki/Colwell")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(1900, 4, "http://en.wikipedia.org/wiki/Bathing"),
            (Marking) new NamedEntity(2013, 8, "http://en.wikipedia.org/wiki/Product_(business)")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(2162, 14, "http://en.wikipedia.org/wiki/Marriott_Hotels_&_Resorts")
        ));
        markings[3].add(Arrays.asList(
            (Marking) new NamedEntity(2295, 9, "http://en.wikipedia.org/wiki/Wisconsin"),
            (Marking) new NamedEntity(2404, 9, "http://en.wikipedia.org/wiki/Probation"),
            (Marking) new NamedEntity(2512, 13, "http://en.wikipedia.org/wiki/Probation_officer")
        ));
        
        for (int j = 0; j < markings.length; j++){
            List<Document> newDocument = new ArrayList();
            for (int i = 0; i < markings[j].size(); i++) {
                newDocument.add(new DocumentImpl(text.get(j), DOCUMENT_URI.get(j), markings[j].get(i)));
            }
            EXPECTED_DOCUMENTS.add(newDocument);
        }
        
        assertThat(EXPECTED_DOCUMENTS, is(notNullValue()));
        assertThat(EXPECTED_DOCUMENTS.size(), is(4));
        
    }
    
    private int checkStringInFileRange(String filePath, int position, int lastPosition, String match) throws GerbilException{ 
        
        RandomAccessFile raf;
        int pos = -1;
        try {
            File file = new File(filePath);
            byte[] search = new byte[position - lastPosition];
            raf = new RandomAccessFile(file, "r");
            raf.seek(lastPosition);
            raf.readFully(search);
            String tmp = new String(search);
            pos = tmp.indexOf(match);
            raf.close();
            if (pos < 0){
                pos = tmp.indexOf(StringEscapeUtils.escapeHtml4(match));
            }
        } catch (IOException e) {
            throw new GerbilException("The given file " + filePath + " could not load.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        
        return pos;
        
    }
    
    private static void generateTerminalOutputForLoadedDatasets() throws GerbilException {
        
        System.out.println("=========================================================");
        System.out.println("===================== File-Documents [" + LOADED_DOCUMENTS.size() + "] =====================");
        for (List<Document> docList : LOADED_DOCUMENTS){
            System.out.println("===================== Documents [" + docList.size() + "] =====================");
            for (Document doc : docList){
                System.out.println("=========================================================");
                System.out.println("Document-URI: " + doc.getDocumentURI());
                System.out.println("==================== Markings [" + doc.getMarkings().size() + "] ====================");
                for (Marking mark : doc.getMarkings()){
                    System.out.println(mark.toString());
                }
            }
        }
        System.out.println("=========================================================");
        
    }
    
}
