mkdir dbpedia_dump
cd dbpedia_dump

wget -r --no-parent -R "*.txt, *.html, *.json" -A "*.nt, *.ttl, *.nt.bz2, *.ttl.bz2" http://downloads.dbpedia.org/2016-04/core-i18n/en/
cd downloads.dbpedia.org/2016-04/core-i18n/en/

wget http://www.l3s.de/~minack/rdf2rdf/downloads/rdf2rdf-1.0.1-2.3.1.jar


rm *.json
rm *.txt
rm index.html

for i in *.bz2; do
	bzip2 -vd $i
done

for i in *.ttl; do
	java -jar rdf2rdf-1.0.1-2.3.1.jar $i .nt
done

rm *.ttl
rm rdf2rdf-1.0.1-2.3.1.jar

cd ../../../../../../

mvn exec:java -Dexec.mainClass="org.aksw.gerbil.tools.InitialIndexTool" -Dexec.args="dbpedia_dump/downloads.dbpedia.org/2016-04/core-i18n/en/"

rm -rf dbpedia_dump/ 
