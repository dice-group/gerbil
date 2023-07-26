package org.aksw.gerbil.dataset.impl.masakha;

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
public class MasakhaNERDatasetTest extends AbstractGenericCoNLLDatasetTest {

    private boolean isAmharic = false;

    public MasakhaNERDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId,
            int markingId, boolean isAmharic) {
        super(fileContent, text, expectedMarking, documentId, markingId);
        this.isAmharic = isAmharic;
    }

    @Override
    public InitializableDataset createDataset(File file) {
        return new MasakhaNERDataset(file.getAbsolutePath(), isAmharic);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // Amharic language
        testConfigs.add(new Object[] {
                "የጀርመን B-LOC\nየምርጫ O\nዘመቻን O\nአስመልክቶ O\nከባልደረባችን O\nማንተጋፍቶት B-PER\nስለሺ I-PERO\nጋር O\nቃለ O\nምልልስ O\nአድርገናል O\n፡፡",
                "የጀርመን ፡ የምርጫ ፡ ዘመቻን ፡ አስመልክቶ ፡ ከባልደረባችን ፡ ማንተጋፍቶት ፡ ስለሺ ፡ ጋር ፡ ቃለ ፡ ምልልስ ፡ አድርገናል፡፡",
                new TypedSpanImpl(36, 12, "http://dbpedia.org/ontology/Person"), 0, 0, true });
        // Hausa language
        testConfigs.add(new Object[] {
                "A O\nsaurari O\ncikakken O\nrahoton O\nwakilin O\nMuryar B-ORG\nAmurka I-ORG\nIbrahim B-PER\nAbdul'aziz I-PER",
                "A saurari cikakken rahoton wakilin Muryar Amurka Ibrahim Abdul'aziz",
                new TypedSpanImpl(35, 13, "http://dbpedia.org/ontology/Organisation"), 0, 0, false });
        // Igbo language
        testConfigs.add(new Object[] {
                "Ike O\nịda O\njụụ O\notụ B-DATE\nnkeji I-DATE\nbanyere O\noke O\nogbugbu O\nna O\n- O\neme O\nn'ala O\nNaijiria B-LOC\nagwụla O\nEkweremmadụ B-PER",
                "Ike ịda jụụ otụ nkeji banyere oke ogbugbu na- eme n'ala Naijiria agwụla Ekweremmadụ",
                new TypedSpanImpl(12, 9, "http://dbpedia.org/ontology/Unknown"), 0, 0, false });
        // Kinyarwanda language
        testConfigs.add(new Object[] {
                "Ambasaderi O\nwa O\nEU B-ORG\nmu O\nRwanda B-LOC\nNicola B-PER\nBellomo I-PER\nyagize O\nati O\nInkunga O\nyacu O\nni O\nimwe O\nmu O\nnkunga O\nyagutse O\nyiswe O\n# O\nTeamEurope O\n. O",
                "Ambasaderi wa EU mu Rwanda Nicola Bellomo yagize ati Inkunga yacu ni imwe mu nkunga yagutse yiswe# TeamEurope.",
                new TypedSpanImpl(14, 2, "http://dbpedia.org/ontology/Organisation"), 0, 0, false });
        // Luganda language
        testConfigs.add(new Object[] {
                "Empaka O\nzaakubeera O\nmu O\nkibuga O\nLiverpool B-LOC\ne O\nBungereza B-LOC\nokutandika O\nnga O\nJuly B-DATE\n12 I-DATE\n. O",
                "Empaka zaakubeera mu kibuga Liverpool e Bungereza okutandika nga July 12.",
                new TypedSpanImpl(28, 9, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Luo language
        testConfigs.add(new Object[] {
                "Migosi O\nRaila B-PER\nne O\nowuoyo O\ne O\nvideo O\nmane O\nogol O\nkod O\nnyare O\nmatin O\nWinnie B-PER\nOdinga I-PER",
                "Migosi Raila ne owuoyo e video mane ogol kod nyare matin Winnie Odinga",
                new TypedSpanImpl(7, 5, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Nigerian Pidgin language
        testConfigs.add(new Object[] {
                "Mixed B-ORG\nMartial I-ORG\nArts I-ORG\njoinbodi O\nUltimate B-ORG\nFighting I-ORG\nChampionship I-ORG\nUFC B-ORG\ndon O\ndecide O\nsay O\ndem O\ngo O\nenta O\nback O\ndi O\noctagon O\non O\nSaturday B-DATE\n9 I-DATE\nMay I-DATE\nfor O\nJacksonville B-LOC\nO",
                "Mixed Martial Arts joinbodi Ultimate Fighting Championship UFC don decide say dem go enta back di octagon on Saturday 9 May for Jacksonville O",
                new TypedSpanImpl(0, 18, "http://dbpedia.org/ontology/Organisation"), 0, 0, false });
        // Swahili language
        testConfigs.add(new Object[] {
                "Hii O\nni O\nbaada O\nya O\nrais O\nYoweri B-PER\nMuseveni I-PER\nkuongeza O\nmda O\nwa O\namri O\nkaribu O\n36 O\nalizotoa O\nkatika O\njuhudi O\nza O\nkukabiliana O\nna O\nmaambukizi O\nya O\nCorona O\nnchini O\nhumo O\nkwa O\nwiki B-DATE\ntatu I-DATE\nzaidi O\nkuanzia O\nleo O\njumanne B-DATE\n.",
                "Hii ni baada ya rais Yoweri Museveni kuongeza mda wa amri karibu 36 alizotoa katika juhudi za kukabiliana na maambukizi ya Corona nchini humo kwa wiki tatu zaidi kuanzia leo jumanne.",
                new TypedSpanImpl(21, 15, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Wolof language
        testConfigs.add(new Object[] {
                "Tënub O\nLéwopóol B-PER\nII I-PER\nba O\nnekk O\nca O\ndéngaleereb O\nngàngunaay O\nbu O\nBurusel B-LOC\nla O\nñu O\ndaax O\ncuub O\nbu O\nxonq O\nci O\ntallata O\njee O\nci O\nngoon O\n.",
                "Tënub Léwopóol II ba nekk ca déngaleereb ngàngunaay bu Burusel la ñu daax cuub bu xonq ci tallata jee ci ngoon.",
                new TypedSpanImpl(6, 11, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Yoruba language
        testConfigs.add(new Object[] {
                "A O\nrán O\nWa B-PER\nLone I-PER\nàti O\nKyaw B-PER\nSoe I-PER\nOo I-PER\nsí O\nẹ̀wọ̀n O\nọdún O\nméje O\nfún O\nrírú O\nòfin O\nÌkọ̀kọ̀ O\nsáà O\n- O\nakónilẹ́rú O\n.",
                "A rán Wa Lone àti Kyaw Soe Oo sí ẹ̀wọ̀n ọdún méje fún rírú òfin Ìkọ̀kọ̀ sáà- akónilẹ́rú.",
                new TypedSpanImpl(6, 7, "http://dbpedia.org/ontology/Person"), 0, 0, false }); 
        // Bambara language
        testConfigs.add(new Object[] {
                "Damakasisɛbɛn O\nladonna O\njumadon B-DATE\nmɛkalo I-DATE\ntile I-DATE\n28 I-DATE\n, O\nKati B-LOC\nkiritikɛso O\nla O\n.",
                "Damakasisɛbɛn ladonna jumadon mɛkalo tile 28, Kati kiritikɛso la.",
                new TypedSpanImpl(22, 22, "http://dbpedia.org/ontology/Unknown"), 0, 0, false });
        // Ghomala language
        testConfigs.add(new Object[] {
                "Brɛ́ndá B-PER\nBiya I-PER\nmú O\nyə O\nmjwǐ O\nFo O\ngúŋ O\nLəpʉə O\nKaməlûm B-LOC\n.",
                "Brɛ́ndá Biya mú yə mjwǐ Fo gúŋ Ləpʉə Kaməlûm.",
                new TypedSpanImpl(0, 13, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Ewe language
        testConfigs.add(new Object[] {
                "Tsitretsitsi O\nɖe O\naʋawɔwɔ O\nŋu O\nle O\nBurkina B-LOC\nFaso I-LOC\n: O\ndziɖuɖua O\nɖe O\ngbeƒã O\name O\naɖe O\nƒe O\nlele O\n. O",
                "Tsitretsitsi ɖe aʋawɔwɔ ŋu le Burkina Faso: dziɖuɖua ɖe gbeƒã ame aɖe ƒe lele.",
                new TypedSpanImpl(30, 12, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Fon language
        testConfigs.add(new Object[] {
                "Atinkɛn O\nɛ́ O\nè O\nè O\nbló O\nɖò O\nBenɛ B-LOC\nɔ́ O\nè O\ngbɛ́ O\nɖɔ O\nè O\nkún O\nná O\nzán O\né O\nlɔ́ɔ O\nmɔ̌ O\nó O\n. O",
                "Atinkɛn ɛ́ è è bló ɖò Benɛ ɔ́ è gbɛ́ ɖɔ è kún ná zán é lɔ́ɔ mɔ̌ ó.",
                new TypedSpanImpl(22, 4, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Mossi language
        testConfigs.add(new Object[] {
                "Naam O\nyell O\nGenon B-LOC\nsoogã O\n: O\ntalgdbã O\n39 O\nwã O\nbe O\nbʋ O\n- O\nkaoodb O\ntaoore O\n. O",
                "Naam yell Genon soogã: talgdbã 39 wã be bʋ- kaoodb taoore.",
                new TypedSpanImpl(10, 5, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Chichewa language
        testConfigs.add(new Object[] {
                "Ukwati O\nndiye O\nadamanga O\npa O\n4 B-DATE\nOctober I-DATE\n2015 I-DATE\n, O\nku O\nFeed B-ORG\nthe I-ORG\nChildren I-ORG\nku O\nNyambadwe B-LOC\nmumzindawu O\n. O",
                "Ukwati ndiye adamanga pa 4 October 2015, ku Feed the Children ku Nyambadwe mumzindawu.",
                new TypedSpanImpl(25, 14, "http://dbpedia.org/ontology/Unknown"), 0, 0, false });
        // Setswana language
        testConfigs.add(new Object[] {
                "Zuma B-PER\no O\nipolela O\na O\nse O\nmolato O\n. O", 
                "Zuma o ipolela a se molato.",
                new TypedSpanImpl(0, 4, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Twi (Akan/Twi) language
        testConfigs.add(new Object[] { 
                "Paul B-PER\nresusu O\nsika O\ndodow O\na O\nohia O\nna O\nɔde O\nawie O\nfie O\nno O\n. O",
                "Paul resusu sika dodow a ohia na ɔde awie fie no.",
                new TypedSpanImpl(0, 4, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // chiShona language
        testConfigs.add(new Object[] { 
                "Messi B-PER\nndiye O\nakarova O\npenalty O\nyekutanga O\nakatadza O\n. O",
                "Messi ndiye akarova penalty yekutanga akatadza.",
                new TypedSpanImpl(0, 5, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // isiXhosa language
        testConfigs.add(new Object[] {
                "Ngempazamo O\nnje O\nenye O\n, O\niye O\nyohlwaywa O\nkabuhlungu O\nnayo O\niRussia B-ORG\nizolo B-DATE\n.",
                "Ngempazamo nje enye, iye yohlwaywa kabuhlungu nayo iRussia izolo.",
                new TypedSpanImpl(51, 7, "http://dbpedia.org/ontology/Organisation"), 0, 0, false });
        // isiZulu language
        testConfigs.add(new Object[] { 
                "IMeya O\nyeTheku B-LOC\ningenelela O\nenkingeni O\nyombhikisho O",
                "IMeya yeTheku ingenelela enkingeni yombhikisho",
                new TypedSpanImpl(6, 7, "http://dbpedia.org/ontology/Place"), 0, 0, false });

        return testConfigs;
    }

}
