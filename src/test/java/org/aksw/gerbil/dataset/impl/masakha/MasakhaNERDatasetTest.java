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

    public MasakhaNERDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId,
            int markingId) {
        super(fileContent, text, expectedMarking, documentId, markingId);
    }

    @Override
    public InitializableDataset createDataset(File file) {
        return new MasakhaNERDataset(file.getAbsolutePath());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<>();
        // Amharic language
        testConfigs.add(new Object[] {
        		"እንደ O\n ኤለርስ B-LOC\n አብዛኞቹ O\n የአፍሪቃ B-LOC\n ሀገሮች O\n በአብዛኛዉ O\n የሥራ O\n ቦታ O\n ያለዉ O\n እርሻ O\n ላይ O\n ነዉ O\n ። O", 
                new TypedSpanImpl(2, 1, "http://dbpedia.org/resource/Amharic_language"), 0, 0 });
        // Hausa language
        testConfigs.add(new Object[] {
        		"A O\n saurari O\n cikakken O\n rahoton O\n wakilin O\n Muryar B-ORG\n Amurka I-ORG\n Ibrahim B-PER\n Abdul'aziz I-PER",
                new TypedSpanImpl(4, 2, "http://dbpedia.org/resource/Igbo_language"), 1, 0 });
        // Igbo language
        testConfigs.add(new Object[] {
        		"Ike O\n ịda O\n jụụ O\n otụ B-DATE\n nkeji I-DATE\n banyere O\n oke O\n ogbugbu O\n na O\n - O\n eme O\n n'ala O\n Naijiria B-LOC\n agwụla O\n Ekweremmadụ B-PER",
                new TypedSpanImpl(1, 1, "http://dbpedia.org/resource/Kinyarwanda_language"), 2, 0 });
        // Kinyarwanda language
        testConfigs.add(new Object[] {
        		"Ambasaderi O\n wa O\n EU B-ORG\n mu O\n Rwanda B-LOC\n O\n Nicola B-PER\n Bellomo I-PER\n yagize O\n ati O\n O\n Inkunga O\n yacu O\n ni O\n imwe O\n mu O\n nkunga O\n yagutse O\nyiswe O\n # O\n TeamEurope O\n . O",
                new TypedSpanImpl(2, 1, "http://dbpedia.org/resource/Kinyarwanda_language"), 3, 0 });
        // Luganda language
        testConfigs.add(new Object[] {
                "Empaka O\n zaakubeera O\n mu O\n kibuga O\n Liverpool B-LOC\n e O\n Bungereza B-LOC\n O\n okutandika O\n nga O\n July B-DATE\n 12 I-DATE\n . O",
                new TypedSpanImpl(4, 2, "http://dbpedia.org/resource/Luganda_language"), 4, 0 });
        // Luo language
        testConfigs.add(new Object[] {
                "Kwan O\n jii O\n maromo O\n 796 O\n mane O\n oyudi O\n ni O\n nigi O\n Covid O\n - O\n 19 O\n ei O\n kawuononi B-DATE",
                new TypedSpanImpl(2, 2, "http://dbpedia.org/resource/Luo_language"), 5, 0 });
        // Nigerian Pidgin language
        testConfigs.add(new Object[] {
                "Mixed B-ORG\n Martial I-ORG\n Arts I-ORG\n joinbodi O\n O\n Ultimate B-ORG\n Fighting I-ORG\n Championship I-ORG\n O\n UFC B-ORG\n don O\n decide O\n say O\n dem O\n go O\n enta O\n back O\n di O\n octagon O\n on O\n Saturday B-DATE\n I-DATE\n 9 I-DATE\n May I-DATE\n O\n for O\n Jacksonville B-LOC\n", ", O\n", "Florida B-LOC\n", ". O",
                new TypedSpanImpl(1, 1, "http://dbpedia.org/resource/Nigerian_Pidgin"), 6, 0 });
        // Swahili language
        testConfigs.add(new Object[] {
                "Hii O\n ni O\n baada O\n ya O\n rais O\n Yoweri B-PER\n Museveni I-PER\n kuongeza O\n mda O\n wa O\n amri O\n karibu O\n 36 O\n alizotoa O\n katika O\n juhudi O\n za O\n kukabiliana O\n na O\n maambukizi O\n ya O\n Corona O\n nchini O\n humo O\n kwa O\n wiki B-DATE\n tatu I-DATE\n zaidi O\n kuanzia O\n leo O\n jumanne B-DATE\n . O\n",
                new TypedSpanImpl(3, 2, "http://dbpedia.org/resource/Swahili_language"), 7, 0 });
        // Wolof language
        testConfigs.add(new Object[] {
        		"Tënub O Léwopóol B-PER II I-PER ba O nekk O ca O déngaleereb O ngàngunaay O bu O Burusel B-LOC la O ñu O daax O cuub O bu O xonq O ci O tallata O jee O ci O ngoon O . O",
                new TypedSpanImpl(1, 2, "http://dbpedia.org/resource/Wolof_language"), 8, 0 });
        // Yoruba language
        testConfigs.add(new Object[] {
        		"Ní O ibi O ìfẹ̀hónúhàn O ní O Luanda B-LOC , O àmì O náà O sọ O pé O “ O 30 O . O 500 O Kwanzas O kì O í O ṣe O kékeré O O . O ",
                new TypedSpanImpl(2, 2, "http://dbpedia.org/resource/Yoruba_language"), 9, 0 });
        // Bambara language
        testConfigs.add(new Object[] {
        		"Nin O waati O in O na O , O a O ka O gɛlɛn O mɔgɔ O k'i O dantigɛ O a O fatuli O kun O jɔnjɔnw O kan O , O k'a O da O a O kan O a O sababuw O tolen O bɛ O dibi O bɛ O . O", 
        		new TypedSpanImpl(0, 1, "http://dbpedia.org/resource/Bambara_language"), 12, 0 });
        // Ghomala language
        testConfigs.add(new Object[] {
        		"Msaʼnyə̂ O gɔtí O cyətə O nə́ O bǎyá O cyə́ O nəjí O pôʼ O bǎhə́lə́ O",
        		new TypedSpanImpl(1, 1, "http://dbpedia.org/resource/Ghomala_language"), 16, 0 });
        // Ewe language
        testConfigs.add(new Object[] {
        		"Tsitretsitsi O ɖe O aʋawɔwɔ O ŋu O le O Burkina B-LOC Faso I-LOC : O dziɖuɖua O ɖe O gbeƒã O ame O aɖe O ƒe O lele O . O", 
        		new TypedSpanImpl(1, 1, "http://dbpedia.org/resource/Ewe_language"), 13, 0 });
        // Fon language
        testConfigs.add(new Object[] {
        		"È O ká O wɔn O dotóo O lɛ́ɛ O ɖesu O ǎ O . O",
        		new TypedSpanImpl(0, 1, "http://dbpedia.org/resource/Fon_language"), 14, 0 });
        // Mossi language
        testConfigs.add(new Object[] {
        		"Yao O sãan O wa O mikame O tɩ O lamd O n O dɩk O bugm O . O",
        		new TypedSpanImpl(1, 2, "http://dbpedia.org/resource/Mossi_language"), 10, 0 });
        // Chichewa language
        testConfigs.add(new Object[] {
        		"Ukwati O ndiye O adamanga O pa O 4 B-DATE October I-DATE 2015 I-DATE , O ku O Feed B-ORG the I-ORG Children I-ORG ku O Nyambadwe B-LOC mumzindawu O . O",
        		new TypedSpanImpl(3, 1, "http://dbpedia.org/resource/Chichewa_language"), 17, 0 });
        // Setswana language
        testConfigs.add(new Object[] {
        		"E O ne O e O le O motlotli O wa O dikgang O yo O gaisang O . O",
        		new TypedSpanImpl(2, 1, "http://dbpedia.org/resource/Setswana_language"), 18, 0 });
        // Twi (Akan/Twi) language
        testConfigs.add(new Object[] {
        		"Sɛ O yɛwɔ O tema O ma O obi O a O , O yebehu O sɛ O ɛnsono O yɛn O ɛnna O ɛsono O ɔno O . O",
        		new TypedSpanImpl(4, 1, "http://dbpedia.org/resource/Akan_language"), 15, 0 });
        // chiShona language
        testConfigs.add(new Object[] {
        		"Huwandu O uhu O hunotarisirwa O kukwira O . O",
        		new TypedSpanImpl(3, 1, "http://dbpedia.org/resource/chiShona_language"), 19, 0 });
        // isiXhosa language
        testConfigs.add(new Object[] {
        		"Konakala O izinto O emsebenzini O emva O kokoyisakala O bubuthonga O ngenxa O yobude O bendlela O . O", 
        		new TypedSpanImpl(3, 1, "http://dbpedia.org/resource/isiXhosa_language"), 20, 0 });
        // isiZulu language
        testConfigs.add(new Object[] {
        		"IMeya O yeTheku B-LOC ingenelela O enkingeni O yombhikisho O",
        		new TypedSpanImpl(4, 1, "http://dbpedia.org/resource/isiZulu_language"), 21, 0 });
        
        return testConfigs;
    }

}
