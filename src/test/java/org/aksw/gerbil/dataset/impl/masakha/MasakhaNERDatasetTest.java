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
                "የጀርመን B-LOC\n የምርጫ O ዘመቻን O አስመልክቶ O ከባልደረባችን O ማንተጋፍቶት B-PER ስለሺ I-PER ጋር O ቃለ O ምልልስ O አድርገናል O ፡፡ O",
                "የጀርመን : የምርጫ : ዘመቻን : አስመልክቶ : ከባልደረባችን : ማንተጋፍቶት : ስለሺ : ጋር : ቃለ : ምልልስ : አድርገናል ፡፡ ",
                new TypedSpanImpl(36, 12, "http://dbpedia.org/ontology/Person"), 0, 0, true });
        // Hausa language
        testConfigs.add(new Object[] {
                "A O\n saurari O\n cikakken O\n rahoton O\n wakilin O\n Muryar B-ORG\n Amurka I-ORG\n Ibrahim B-PER\n Abdul'aziz I-PER",
                "A saurari cikakken rahoton wakilin Muryar Amurka Ibrahim Abdul'aziz",
                new TypedSpanImpl(49, 18, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Igbo language
        testConfigs.add(new Object[] {
                "Ike O\n ịda O\n jụụ O\n otụ B-DATE\n nkeji I-DATE\n banyere O\n oke O\n ogbugbu O\n na O\n - O\n eme O\n n'ala O\n Naijiria B-LOC\n agwụla O\n Ekweremmadụ B-PER",
                "Ike ịda jụụ otụ nkeji banyere oke ogbugbu na - eme n'ala Naijiria agwụla Ekweremmadụ",
                new TypedSpanImpl(57, 8, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Kinyarwanda language
        testConfigs.add(new Object[] {
                "Ambasaderi O\n wa O\n EU B-ORG\n mu O\n Rwanda B-LOC\n O\n Nicola B-PER\n Bellomo I-PER\n yagize O\n ati O\n O\n Inkunga O\n yacu O\n ni O\n imwe O\n mu O\n nkunga O\n yagutse O\nyiswe O\n # O\n TeamEurope O\n . O",
                "Ambasaderi wa EU mu Rwanda Nicola Bellomo yagize ati Inkunga yacu ni imwe mu nkunga yagutse nyiswe # TeamEurope.",
                new TypedSpanImpl(14, 2, "http://dbpedia.org/ontology/Organization"), 0, 0, false });
        // Luganda language
        testConfigs.add(new Object[] {
                "Empaka O\n zaakubeera O\n mu O\n kibuga O\n Liverpool B-LOC\n e O\n Bungereza B-LOC\n O\n okutandika O\n nga O\n July B-DATE\n 12 I-DATE\n . O",
                "Empaka zaakubeera mu kibuga Liverpool e Bungereza okutandika nga July 12.",
                new TypedSpanImpl(28, 9, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Luo language
        testConfigs.add(new Object[] {
                "Migosi O Raila B-PER ne O owuoyo O e O video O mane O ogol O kod O nyare O matin O Winnie B-PER Odinga I-PER",
                "Migosi Raila ne owuoyo e video mane ogol kod nyare matin Winnie Odinga",
                new TypedSpanImpl(57, 13, "http://dbpedia.org/ontology/Location"), 0, 0, false });
        // Nigerian Pidgin language
        testConfigs.add(new Object[] {
                "Mixed B-ORG\n Martial I-ORG\n Arts I-ORG\n joinbodi O\n O\n Ultimate B-ORG\n Fighting I-ORG\n Championship I-ORG\n O\n UFC B-ORG\n don O\n decide O\n say O\n dem O\n go O\n enta O\n back O\n di O\n octagon O\n on O\n Saturday B-DATE\n I-DATE\n 9 I-DATE\n May I-DATE\n O\n for O\n Jacksonville B-LOC\n O",
                "Mixed Martial Arts joinbodi Ultimate Fighting Championship UFC don decide say dem go enta back di octagon on Saturday 9 May for Jacksonville O",
                new TypedSpanImpl(1, 62, "http://dbpedia.org/ontology/Organization"), 0, 0, false });
        // Swahili language
        testConfigs.add(new Object[] {
                "Hii O\n ni O\n baada O\n ya O\n rais O\n Yoweri B-PER\n Museveni I-PER\n kuongeza O\n mda O\n wa O\n amri O\n karibu O\n 36 O\n alizotoa O\n katika O\n juhudi O\n za O\n kukabiliana O\n na O\n maambukizi O\n ya O\n Corona O\n nchini O\n humo O\n kwa O\n wiki B-DATE\n tatu I-DATE\n zaidi O\n kuanzia O\n leo O\n jumanne B-DATE\n . O\n",
                "Hii ni baada ya rais Yoweri Museveni kuongeza mda wa amri karibu 36 alizoto katika juhudi za kukabiliana na maambukizi ya Corona nchini humo kwa wiki tatu zaidi kuanzia leo jumanne.",
                new TypedSpanImpl(21, 15, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Wolof language
        testConfigs.add(new Object[] {
                "Tënub O Léwopóol B-PER II I-PER ba O nekk O ca O déngaleereb O ngàngunaay O bu O Burusel B-LOC la O ñu O daax O cuub O bu O xonq O ci O tallata O jee O ci O ngoon O . O",
                "Tënub Léwopóol II ba nekk ca déngaleereb ngàngunaay bu Burusel la ñu daax cuub bu xonq ci tallata jee ci ngoon.",
                new TypedSpanImpl(1, 17, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Yoruba language
        testConfigs.add(new Object[] {
                "Ní O ibi O ìfẹ̀hónúhàn O ní O Luanda B-LOC , O àmì O náà O sọ O pé O “ O 30 O . O 500 O Kwanzas O kì O í O ṣe O kékeré O O . O ",
                "Ní ibi ìfẹ̀hónúhàn ní Luanda, àmì náà sọ pé “30.500 Kwanzas kì í ṣe kékeré.",
                new TypedSpanImpl(30, 6, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Bambara language
        testConfigs.add(new Object[] {
                "Damakasisɛbɛn O ladonna O jumadon B-DATE mɛkalo I-DATE tile I-DATE 28 I-DATE , O Kati B-LOC kiritikɛso O la O . O",
                "Damakasisɛbɛn ladonna jumadon mɛkalo tile 28, Kati kiritikɛso la.",
                new TypedSpanImpl(46, 4, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Ghomala language
        testConfigs
                .add(new Object[] { "Sɔ́ʼ O m O nə́ O cúʼtə O khəkhə O ntʉ́m O kɔŋsɛ̂ O Valserô B-PER Zenît B-ORG",
                        "Sɔ́ʼ m nə́ cúʼtə khəkhə ntʉ́m kɔŋsɛ̂ Valserô Zenît",
                        new TypedSpanImpl(37, 7, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Ewe language
        testConfigs.add(new Object[] {
                "Tsitretsitsi O ɖe O aʋawɔwɔ O ŋu O le O Burkina B-LOC Faso I-LOC : O dziɖuɖua O ɖe O gbeƒã O ame O aɖe O ƒe O lele O . O",
                "Tsitretsitsi ɖe aʋawɔwɔ ŋu le Burkina Faso : dziɖuɖua ɖe gbeƒã ame aɖe ƒe lele.",
                new TypedSpanImpl(30, 12, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Fon language
        testConfigs.add(new Object[] {
                "Atinkɛn O nɛ́ O è O è O bló O ɖò O Benɛ B-LOC ɔ́ O O è O gbɛ́ O ɖɔ O è O kún O ná O zán O é O lɔ́ɔ O mɔ̌ O ó O . O",
                "Atinkɛn ɛ́ è è bló ɖò Benɛ ɔ́ è gbɛ́ ɖɔ è kún ná zán é lɔ́ɔ mɔ̌ ó.",
                new TypedSpanImpl(22, 4, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Mossi language
        testConfigs.add(new Object[] {
                "Naam O yell O Genon B-LOC soogã O : O talgdbã O 39 O wã O be O bʋ O - O kaoodb O taoore O . O",
                "Naam yell Genon soogã : talgdbã 39 wã be bʋ - kaoodb taoore.",
                new TypedSpanImpl(10, 5, "http://dbpedia.org/ontology/Place"), 0, 0, false });
        // Chichewa language
        testConfigs.add(new Object[] {
                "Ukwati O ndiye O adamanga O pa O 4 B-DATE October I-DATE 2015 I-DATE , O ku O Feed B-ORG the I-ORG Children I-ORG ku O Nyambadwe B-LOC mumzindawu O . O",
                "Ukwati ndiye adamanga pa 4 October 2015, ku Feed the Children ku Nyambadwe mumzindawu.",
                new TypedSpanImpl(44, 17, "http://dbpedia.org/ontology/Organization"), 0, 0, false });
        // Setswana language
        testConfigs.add(new Object[] { "Zuma B-PER o O ipolela O a O se O molato O. O", "Zuma o ipolela a se molato.",
                new TypedSpanImpl(1, 4, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // Twi (Akan/Twi) language
        testConfigs.add(new Object[] { "Paul B-PER resusu O sika O dodow O a O ohia O na O ɔde O awie O fie O no O . O",
                "Paul resusu sika dodow a ohia na ɔde awie fie no.",
                new TypedSpanImpl(1, 4, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // chiShona language
        testConfigs.add(new Object[] { "Messi B-PER ndiye O akarova O penalty O yekutanga O akatadza O . O",
                "Messi ndiye akarova penalty yekutanga akatadza.",
                new TypedSpanImpl(1, 5, "http://dbpedia.org/ontology/Person"), 0, 0, false });
        // isiXhosa language
        testConfigs.add(new Object[] {
                "Ngempazamo O nje O enye O, O iye O yohlwaywa O kabuhlungu O nayo O iRussia B-ORG izolo B-DATE. O",
                "Ngempazamo nje enye, iye yohlwaywa kabuhlungu nayo iRussia izolo.",
                new TypedSpanImpl(51, 7, "http://dbpedia.org/ontology/Organization"), 0, 0, false });
        // isiZulu language
        testConfigs.add(new Object[] { "IMeya O yeTheku B-LOC ingenelela O enkingeni O yombhikisho O",
                "IMeya yeTheku ingenelela enkingeni yombhikisho",
                new TypedSpanImpl(6, 7, "http://dbpedia.org/ontology/Place"), 0, 0, false });

        return testConfigs;
    }

}
