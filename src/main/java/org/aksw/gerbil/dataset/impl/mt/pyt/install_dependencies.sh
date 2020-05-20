# INSTALL PYTHON DEPENDENCIES
pip3 install -r src/main/java/org/aksw/gerbil/dataset/impl/mt/pyt/requirements.txt

# INSTALL METEOR
wget https://www.cs.cmu.edu/~alavie/METEOR/download/meteor-1.5.tar.gz
tar -xvf meteor-1.5.tar.gz
mv meteor-1.5 metrics
rm meteor-1.5.tar.gz