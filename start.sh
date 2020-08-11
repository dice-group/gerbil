#!/bin/bash

# This script is part of the GERBIL project.
# It reuses functions from Mitch Frazier (http://www.linuxjournal.com/content/asking-yesno-question-bash-script)

#####################################################################
# Print warning message.

function warning()
{
    echo "$*" >&2
}

#####################################################################
# Print error message and exit.

function error()
{
    echo "$*" >&2
    exit 1
}


#####################################################################
# Ask yesno question.
#
# Usage: yesno OPTIONS QUESTION
#
#   Options:
#     --timeout N    Timeout if no input seen in N seconds.
#     --default ANS  Use ANS as the default answer on timeout or
#                    if an empty answer is provided.
#
# Exit status is the answer.

function yesno()
{
    local ans
    local ok=0
    local timeout=0
    local default
    local t

    while [[ "$1" ]]
    do
        case "$1" in
        --default)
            shift
            default=$1
            if [[ ! "$default" ]]; then error "Missing default value"; fi
            t=$(tr '[:upper:]' '[:lower:]' <<<$default)

            if [[ "$t" != 'y'  &&  "$t" != 'yes'  &&  "$t" != 'n'  &&  "$t" != 'no' ]]; then
                error "Illegal default answer: $default"
            fi
            default=$t
            shift
            ;;

        --timeout)
            shift
            timeout=$1
            if [[ ! "$timeout" ]]; then error "Missing timeout value"; fi
            if [[ ! "$timeout" =~ ^[0-9][0-9]*$ ]]; then error "Illegal timeout value: $timeout"; fi
            shift
            ;;

        -*)
            error "Unrecognized option: $1"
            ;;

        *)
            break
            ;;
        esac
    done

    if [[ $timeout -ne 0  &&  ! "$default" ]]; then
        error "Non-zero timeout requires a default answer"
    fi

    if [[ ! "$*" ]]; then error "Missing question"; fi

    while [[ $ok -eq 0 ]]
    do
        if [[ $timeout -ne 0 ]]; then
            if ! read -t $timeout -p "$*" ans; then
                ans=$default
            else
                # Turn off timeout if answer entered.
                timeout=0
                if [[ ! "$ans" ]]; then ans=$default; fi
            fi
        else
            read -p "$*" ans
            if [[ ! "$ans" ]]; then
                ans=$default
            else
                ans=$(tr '[:upper:]' '[:lower:]' <<<$ans)
            fi 
        fi

        if [[ "$ans" == 'y'  ||  "$ans" == 'yes'  ||  "$ans" == 'n'  ||  "$ans" == 'no' ]]; then
            ok=1
        fi

        if [[ $ok -eq 0 ]]; then warning "Valid answers are: yes y no n"; fi
    done
    [[ "$ans" = "y" || "$ans" == "yes" ]]
}

#####################################################################
# Check for dependencies
echo "Checking dependencies..."
file="gerbil_data/"

if [ ! -d "gerbil_data" ]; then
    mkdir -p "gerbil_data" || error "Could not create gerbil_data directory"
    if [ ! -f "$file" ]; then
        echo "Downloading dependencies ... ($url)"
        curl --retry 4 -L -o "$file" "$url"

        if [ ! -f "$file" ]; then
            error "Couldn't downloading dependency data: $file"
        else
            echo "Extracting dependencies ... "
            unzip "$file"
       fi
   fi
fi

#####################################################################
# Check for property file
echo "Checking properties files..."
dir="src/main/properties"
file="$dir/gerbil_keys.properties"

if [ ! -f "$file" ]; then
	echo "Creating empty $file file"
	mkdir -p "$dir" || error "Could not create $dir directory"
	echo "##############################################################################"  > $file
	echo "# This is the properties file contains our keys for several annotator web    #"  >> $file
	echo "# services.                                                                  #"  >> $file
	echo "#                      IT SHOULD NOT BE DISTRIBUTED!!!                       #"  >> $file
	echo "##############################################################################"  >> $file
fi

#####################################################################
# Check for dbpedia sameAs index
#echo "Checking dbpedia sameAs index..."
#if [ ! -d "indexes/dbpedia" ]; then
#	echo "Couldn't find a dbpedia sameAs index"
#	if yesno "Should the index be downloaded (~1GB zipped, ~2GB extracted)? (yes/no): "; then
#		mkdir -p "indexes/dbpedia" || error "Could not create indexes/dbpedia directory"
#		file="indexes/dbpedia/dbpedia_index.zip"
#		url="http://139.18.2.164/mroeder/gerbil/dbpedia_index.zip"
#		echo "Downloading index ... ($url)"
#		curl --retry 4 -L -o "$file" "$url"
#
#		if [ ! -f "$file" ]; then
#			echo "Couldn't downloading index file: $file"
#		else
#			echo "Extracting index ... "
#			unzip "$file" -d "indexes/dbpedia"
#		fi
#	fi
#fi

#####################################################################
# Check for dbpedia entity check index
#echo "Checking dbpedia entity check index..."
#if [ ! -d "indexes/dbpedia_check" ]; then
#	echo "Couldn't find a dbpedia entity check index"
#	if yesno "Should the index be downloaded (~0.3GB zipped, ~0.7GB extracted)? (yes/no): "; then
#		mkdir -p "indexes/dbpedia_check" || error "Could not create indexes/dbpedia_check directory"
#		file="indexes/dbpedia_check/dbpedia_check_index.zip"
#		url="http://139.18.2.164/mroeder/gerbil/dbpedia_check_index.zip"
#		echo "Downloading index ... ($url)"
#		curl --retry 4 -L -o "$file" "$url"
#
#		if [ ! -f "$file" ]; then
#			echo "Couldn't downloading index file: $file"
#		else
#			echo "Extracting index ... "
#			unzip "$file" -d "indexes/dbpedia_check"
#		fi
#	fi
#fi

# INSTALL PYTHON DEPENDENCIES
pip3 install -r src/main/java/org/aksw/gerbil/python/mt/requirements.txt

# INSTALL METEOR
if [ ! -d "src/main/java/org/aksw/gerbil/python/mt/metrics/meteor-1.5" ]; then
  wget https://www.cs.cmu.edu/~alavie/METEOR/download/meteor-1.5.tar.gz
  tar -xvf meteor-1.5.tar.gz
  mv meteor-1.5 src/main/java/org/aksw/gerbil/python/mt/metrics
  rm meteor-1.5.tar.gz
fi

echo "Building and starting GERBIL..."
mvn clean org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Dmaven.tomcat.port=1234