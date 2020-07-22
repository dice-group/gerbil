# INSTALL PYTHON DEPENDENCIES
pip3 install -r src/main/java/org/aksw/gerbil/python/mt/requirements.txt

# INSTALL METEOR
wget https://www.cs.cmu.edu/~alavie/METEOR/download/meteor-1.5.tar.gz
tar -xvf meteor-1.5.tar.gz
mv meteor-1.5 src/main/java/org/aksw/gerbil/python/mt/metrics
rm meteor-1.5.tar.gz