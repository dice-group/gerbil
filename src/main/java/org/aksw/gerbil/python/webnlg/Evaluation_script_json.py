# import warnings filter
from warnings import simplefilter
from sklearn.exceptions import UndefinedMetricWarning
# ignore all UndefinedMetricWarning warnings
simplefilter(action='ignore', category=UndefinedMetricWarning)
from bs4 import BeautifulSoup
import os
import regex as re
import itertools
import statistics
import sys
from nervaluate import Evaluator
import nltk
from nltk.util import ngrams
import string
from sklearn.metrics import precision_score, recall_score, f1_score
from sklearn import preprocessing
import json

currentpath = os.getcwd()

def getRefs(filepath):
    with open(filepath, encoding='utf-8') as fp:
        refssoup = BeautifulSoup(fp, 'lxml')

    refsentries = refssoup.find('benchmark').find('entries').find_all('entry')

    allreftriples = []

    for entry in refsentries:
        entryreftriples = []
        modtriplesref = entry.find('modifiedtripleset').find_all('mtriple')
        for modtriple in modtriplesref:
            entryreftriples.append(modtriple.text)
        allreftriples.append(entryreftriples)

    newreflist = []

    for entry in allreftriples:
        newtriples = []
        for triple in entry:
            newtriple = re.sub(r"([a-z])([A-Z])", "\g<1> \g<2>", triple).lower()
            newtriple = re.sub(r'_', ' ', newtriple).lower()
            newtriple = re.sub(r'\s+', ' ', newtriple).lower()
            newtriples.append(newtriple)
        newreflist.append(newtriples)

    return allreftriples, newreflist

def getCands(filepath):
    with open(filepath, encoding='utf-8') as fp:
        candssoup = BeautifulSoup(fp, 'lxml')

    candssentries = candssoup.find('benchmark').find('entries').find_all('entry')

    allcandtriples = []

    for entry in candssentries:
        entrycandtriples = []
        modtriplescand = entry.find('generatedtripleset').find_all('gtriple')
        for modtriple in modtriplescand:
            entrycandtriples.append(modtriple.text)
        allcandtriples.append(entrycandtriples)

    newcandlist = []

    for entry in allcandtriples:
        newtriples = []
        for triple in entry:
            newtriple = re.sub(r"([a-z])([A-Z])", "\g<1> \g<2>", triple).lower()
            newtriple = re.sub(r'_', ' ', newtriple).lower()
            newtriple = re.sub(r'\s+', ' ', newtriple).lower()
            newtriples.append(newtriple)
        newcandlist.append(newtriples)

    return allcandtriples, newcandlist

def find_sub_list(sl,l):
    sll=len(sl)
    for ind in (i for i,e in enumerate(l) if e==sl[0]):
        if l[ind:ind+sll]==sl:
            return ind,ind+sll-1

#We are going to try to find matches with the reference, starting with the highest chunk possible (all the words in the reference).
#If we don't find that, we are going to search for all n-grams -1 the number of words in the reference; than -2; than -3; etc.
def nonrefwords(newreflist, newcandlist, foundnum, ngramlength):
    while ngramlength > 0:
        #Get a list of all the ngrams of that size
        ngramlist = list(ngrams(newcandlist, ngramlength))
        for ngram in ngramlist:
            #If we find this ngram (in the same order) in the reference
            if find_sub_list(list(ngram), newreflist) is not None:
                #We're getting the start and end index of the ngram in the reference
                findnewref = find_sub_list(list(ngram), newreflist)
                #And all the numbers in between
                newrefindex = list(range(findnewref[0], findnewref[1] + 1))
                #Change the matched words to FOUNDREF-[FOUNDNUMBER]-[FOUNDINDEX]
                for idx in newrefindex:
                    newreflist[idx] = 'FOUNDREF-' + str(foundnum) + '-' + str(idx)

                #Now find the start and end index of the ngram in the candidate as well
                findnewcand = find_sub_list(list(ngram), newcandlist)
                #And all the indices in between
                newcandindex = list(range(findnewcand[0], findnewcand[1]+1))
                # Change the matched words to FOUNDCAND-[FOUNDNUMBER]-[REFERENCE-FOUNDINDEX]
                for idx, val in enumerate(newcandindex):
                    newcandlist[val] = 'FOUNDCAND-' + str(foundnum) + '-' + str(newrefindex[idx])
                foundnum += 1
                #And try to find new matches again
                nonrefwords(newreflist, newcandlist, foundnum, ngramlength)
        #If no match is found, try to find matches for ngrams 1 smaller
        ngramlength -= 1
    #Return the new lists if all possible ngrams have been searched
    return newreflist, newcandlist

def getrefdict(newreflist, newcandlist, tripletyperef, tripletypecand, baseidx):
    try:
        #If some match is found with the reference
        firstfoundidx = newcandlist.index([i for i in newcandlist if re.findall(r'^FOUNDCAND', i)][0])
        candidatefound = 'y'
    except IndexError:
        candidatefound = 'n'

    if candidatefound == 'y':
        unlinkedlist = []
        beforelist = []
        afterlist = []

        #If the first found candidate match is also the first word in the reference
        if newcandlist[firstfoundidx].endswith('-0'):
            #Flag that some words can appear before the first match, and they are linked with the first candidate match
            beforelinked = 'y'
            firstcand = re.search(r'^(FOUNDCAND-\d+)-', newcandlist[firstfoundidx]).group(1)
        else:
            beforelinked = 'n'

        lastfoundidx = None
        afterlinked = None
        #If there's more words after the last reference, link those to the last reference as well
        #If the last reference word is linked, but the last candidate word is not, one criterion of linking the last words is met
        if (newreflist[-1].startswith('FOUNDREF')) and (not newcandlist[-1].startswith('FOUNDCAND')):
            #If the last linked reference word is the last linked candidate word, the other criterion is also met.
            lastfound = [i for i in newcandlist if re.findall(r'^FOUNDCAND', i)][-1]
            candversion = newreflist[-1].replace('FOUNDREF', 'FOUNDCAND')
            if lastfound == candversion:
                lastfoundidx = newcandlist.index([i for i in newcandlist if re.findall(r'^FOUNDCAND', i)][-1])
                afterlinked = 'y'
                lastcand = re.search(r'^(FOUNDCAND-\d+)-', lastfound).group(1)


        #Ensure that all the not-found blocks are separated by giving them different unlinknumbers
        unlinknumber = 1
        for idx, can in enumerate(newcandlist):
            if not can.startswith('FOUNDCAND'):
                if (idx < firstfoundidx) and (beforelinked == 'y'):
                    newcandlist[idx] = firstcand + '-LINKED'
                    beforelist.append(firstcand + '-LINKED')
                elif (lastfoundidx != None) and (afterlinked != None) and (idx > lastfoundidx) and (afterlinked == 'y'):
                    newcandlist[idx] = lastcand + '-LINKED'
                    afterlist.append(lastcand + '-LINKED')
                else:
                    unlinkedlist.append('NOTFOUND-' + str(unlinknumber))
            else:
                unlinknumber += 1

        totallist = beforelist + newreflist + afterlist + unlinkedlist

        refstart = len(beforelist)
        refend = (len(beforelist) + len(newreflist)) - 1

        refdictlist = [{'label': tripletyperef, 'start': baseidx + refstart, 'end': baseidx + refend}]

        totallist2 = [x.replace('FOUNDREF', 'FOUNDCAND') for x in totallist]

        canddictlist = []
        currentcandidate = ''
        beginidx = ''
        endidx = ''
        collecting = 'n'
        for idx, candidate in enumerate(totallist2):
            if (candidate.startswith('FOUNDCAND')) or (candidate.startswith('NOTFOUND')):
                collecting = 'y'
                curcan = re.search(r'^((.*?)-\d+)', candidate).group(1)
                if curcan != currentcandidate:
                    if currentcandidate != '':
                        endidx = idx-1
                        canddictlist.append({'label': tripletypecand, 'start': baseidx + beginidx, 'end': baseidx + endidx})
                    currentcandidate = curcan
                    beginidx = idx

                if idx == len(totallist2)-1:
                    endidx = idx
                    canddictlist.append({'label': tripletypecand, 'start': baseidx + beginidx, 'end': baseidx + endidx})
            else:
                if collecting == 'y':
                    endidx = idx-1
                    canddictlist.append({'label': tripletypecand, 'start': baseidx + beginidx, 'end': baseidx + endidx})

    else:
        if len(newreflist) == 0:
            refdictlist = []
            canddictlist = [{'label': tripletypecand, 'start': baseidx, 'end': baseidx + (len(newcandlist) - 1)}]
            totallist = newcandlist
        elif len(newcandlist) == 0:
            canddictlist = []
            refdictlist = [{'label': tripletyperef, 'start': baseidx, 'end': baseidx + (len(newreflist) - 1)}]
            totallist = refdictlist
        else:
            totallist = newreflist + newcandlist
            refdictlist = [{'label': tripletyperef, 'start': baseidx, 'end': baseidx + (len(newreflist) - 1)}]
            canddictlist = [{'label': tripletypecand, 'start': baseidx + len(newreflist), 'end': baseidx + (len(totallist) - 1)}]


    return candidatefound, refdictlist, canddictlist, totallist

def evaluaterefcand(reference, candidate):
    newreference = reference.split(' | ')
    newcandidate = candidate.split(' | ')

    #Make sure that reference or candidate aren't '' values originally.
    if (len(newreference) > 1) and (len(newcandidate) > 1):
        indextriple = newreference
    elif (len(newreference) == 1) :
        indextriple = newcandidate
        newreference = ['', '', '']
    else:
        indextriple = newreference
        newcandidate = ['', '', '']

    subjectreflist = None
    subjectcandlist = None
    subjecttotallist = None
    predicatereflist = None
    predicatecandlist = None
    predicatetotallist = None
    objectreflist = None
    objectcandlist = None
    objecttotallist = None
    subjectfound = ''
    predicatefound = ''
    objectfound = ''

    for idx, attrib in enumerate(indextriple):
        #Let's go over each attribute of the triple one by one
        refsub = newreference[idx]
        candsub = newcandidate[idx]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'^[' + re.escape(string.punctuation) + r']+$', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'^[' + re.escape(string.punctuation) + r']$', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        #Start with an ngram the full number of words in the reference
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)
        if idx == 0:
            candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'SUB', 'SUB', 0)
            subjectfound = candidatefound
            subjectreflist = refdictlist.copy()
            subjectcandlist = canddictlist.copy()
            subjecttotallist = totallist.copy()
        elif idx == 1:
            candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'PRED', 'PRED', len(subjecttotallist))
            predicatefound = candidatefound
            predicatereflist = refdictlist.copy()
            predicatecandlist = canddictlist.copy()
            predicatetotallist = totallist.copy()
        else:
            candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'OBJ', 'OBJ', len(subjecttotallist) + len(predicatetotallist))
            objectfound = candidatefound
            objectreflist = refdictlist.copy()
            objectcandlist = canddictlist.copy()
            objecttotallist = totallist.copy()

    switchmatchfound = 'n'
    #If no matches were found for two or more attributes, we are going to try and compare different attributes to each other.
    #First let's try to match the candidate subject and reference object (and vice versa)
    if (subjectfound == 'n') and (objectfound == 'n'):
        refsub = newreference[0]
        candsub = newcandidate[2]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        # Start with an ngram the full number of words in the candidate
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)

        candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'SUB', 'OBJ', 0)

        refsub = newreference[2]
        candsub = newcandidate[0]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        # Start with an ngram the full number of words in the candidate
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)
        candidatefound2, refdictlist2, canddictlist2, totallist2 = getrefdict(newreflist, newcandlist, 'OBJ', 'SUB', len(totallist) + len(predicatetotallist))

        if (candidatefound == 'y') or (candidatefound2 == 'y'):
            subjectfound = candidatefound
            subjectreflist = refdictlist.copy()
            subjectcandlist = canddictlist.copy()
            subjecttotallist = totallist.copy()
            objectfound = candidatefound2
            objectreflist = refdictlist2.copy()
            objectcandlist = canddictlist2.copy()
            objecttotallist = totallist2.copy()

            candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'PRED', 'PRED', len(subjecttotallist))
            predicatefound = candidatefound
            predicatereflist = refdictlist.copy()
            predicatecandlist = canddictlist.copy()
            predicatetotallist = totallist.copy()

            switchmatchfound = 'y'
        else:
            switchmatchfound = 'n'

    # Then, let's try to switch subject and predicate
    if ((subjectfound == 'n') and (predicatefound == 'n')) and (switchmatchfound == 'n'):
        refsub = newreference[0]
        candsub = newcandidate[1]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        # Start with an ngram the full number of words in the candidate
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)

        candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'SUB', 'PRED', 0)

        refsub = newreference[1]
        candsub = newcandidate[0]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        # Start with an ngram the full number of words in the candidate
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)

        candidatefound2, refdictlist2, canddictlist2, totallist2 = getrefdict(newreflist, newcandlist, 'PRED', 'SUB', len(totallist))

        if (candidatefound == 'y') or (candidatefound2 == 'y'):
            subjectfound = candidatefound
            subjectreflist = refdictlist.copy()
            subjectcandlist = canddictlist.copy()
            subjecttotallist = totallist.copy()
            predicatefound = candidatefound2
            predicatereflist = refdictlist2.copy()
            predicatecandlist = canddictlist2.copy()
            predicatetotallist = totallist2.copy()
            switchmatchfound = 'y'
        else:
            switchmatchfound = 'n'

    # Finally, let's try to switch predicate and object
    if ((predicatefound == 'n') and (objectfound == 'n')) and (switchmatchfound == 'n'):
        refsub = newreference[1]
        candsub = newcandidate[2]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        # Start with an ngram the full number of words in the candidate
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)

        candidatefound, refdictlist, canddictlist, totallist = getrefdict(newreflist, newcandlist, 'PRED', 'OBJ', len(subjecttotallist))

        refsub = newreference[2]
        candsub = newcandidate[1]

        reflist = nltk.word_tokenize(refsub)
        candlist = nltk.word_tokenize(candsub)

        reflist = [x.lower() for x in reflist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]
        candlist = [x.lower() for x in candlist if re.search(r'[' + re.escape(string.punctuation) + r']', x) == None]

        newreflist = reflist.copy()
        newcandlist = candlist.copy()
        # Start with an ngram the full number of words in the candidate
        ngramlength = len(newcandlist)
        newreflist, newcandlist = nonrefwords(newreflist, newcandlist, 1, ngramlength)

        candidatefound2, refdictlist2, canddictlist2, totallist2 = getrefdict(newreflist, newcandlist, 'OBJ', 'PRED', len(subjecttotallist) + len(totallist))

        if (candidatefound == 'y') or (candidatefound2 == 'y'):
            predicatefound = candidatefound
            predicatereflist = refdictlist.copy()
            predicatecandlist = canddictlist.copy()
            predicatetotallist = totallist.copy()
            objectfound = candidatefound2
            objectreflist = refdictlist2.copy()
            objectcandlist = canddictlist2.copy()
            objecttotallist = totallist2.copy()
            switchmatchfound = 'y'
        else:
            switchmatchfound = 'n'


    allrefdict = subjectreflist + predicatereflist + objectreflist
    allcanddict = subjectcandlist + predicatecandlist + objectcandlist
    alltotallist = subjecttotallist + predicatetotallist + objecttotallist

    evaluator = Evaluator([allrefdict], [allcanddict], tags=['SUB', 'PRED', 'OBJ'])

    # Returns overall metrics and metrics for each tag

    results, results_per_tag = evaluator.evaluate()

    return results, results_per_tag

def calculateAllScores(newreflist, newcandlist):
    totalsemevallist = []
    totalsemevallistpertag = []

    for idx, candidate in enumerate(newcandlist):
        if len(newcandlist[idx]) != len(newreflist[idx]):
            differencebetween = abs(len(newcandlist[idx]) - len(newreflist[idx]))
            differencelist = [''] * differencebetween
            if len(newcandlist[idx]) < len(newreflist[idx]):
                newcandlist[idx] = newcandlist[idx] + differencelist
            else:
                newreflist[idx] = newreflist[idx] + differencelist

    for idx, candidate in enumerate(newcandlist):
        candidatesemeval = []
        candidatesemevalpertag = []
        for triple in candidate:
            triplesemeval = []
            triplesemevalpertag = []
            for reference in newreflist[idx]:
                results, results_per_tag = evaluaterefcand(reference, triple)
                triplesemeval.append(results)
                triplesemevalpertag.append(results_per_tag)

            candidatesemeval.append(triplesemeval)
            candidatesemevalpertag.append(triplesemevalpertag)

        totalsemevallist.append(candidatesemeval)
        totalsemevallistpertag.append(candidatesemevalpertag)

    return totalsemevallist, totalsemevallistpertag

def calculateSystemScore(totalsemevallist, totalsemevallistpertag, newreflist, newcandlist):
    selectedsemevallist = []
    selectedsemevallistpertag = []
    alldicts = []

    # Get all the permutations of the number of scores given per candidate, so if there's 4 candidates, but 3 references, this part ensures that one of
    # The four will not be scored
    for idx, candidate in enumerate(newcandlist):
        if len(newcandlist[idx]) > len(newreflist[idx]):
            # Get all permutations
            choosecands = list(itertools.permutations([x[0] for x in enumerate(allf1[idx])], len(allf1[idx][0])))
            # The permutations in different orders are not necessary: we only need one order without the number of candidates we're looking at
            choosecands = set([tuple(sorted(i)) for i in choosecands])  # Sort inner list and then use set
            choosecands = list(map(list, choosecands))  # Converting back to list
        else:
            # Otherwise, we're just going to score all candidates
            choosecands = [list(range(len(newcandlist[idx])))]

        # Get all permutations in which the scores can be combined
        if len(newcandlist[idx]) > len(newreflist[idx]):
            choosescore = list(itertools.permutations([x[0] for x in enumerate(totalsemevallist[idx][0])], len(newreflist[idx])))
            choosescore = [list(x) for x in choosescore]
        else:
            choosescore = list(itertools.permutations([x[0] for x in enumerate(totalsemevallist[idx][0])], len(newcandlist[idx])))
            choosescore = [list(x) for x in choosescore]

        # Get all possible combinations between the candidates and the scores
        combilist = list(itertools.product(choosecands, choosescore))

        totaldict = {'totalscore': 0}

        for combination in combilist:
            combiscore = 0
            # Take the combination between the candidate and the score
            zipcombi = list(zip(combination[0], combination[1]))
            collectedsemeval = []
            collectedsemevalpertag = []

            for zc in zipcombi:
                collectedscores = totalsemevallist[idx][zc[0]][zc[1]]
                f1score = statistics.mean([collectedscores['ent_type']['f1'], collectedscores['partial']['f1'], collectedscores['strict']['f1'], collectedscores['exact']['f1']])
                combiscore += f1score

                collectedsemeval.append(collectedscores)
                collectedsemevalpertag.append(totalsemevallistpertag[idx][zc[0]][zc[1]])


            # If the combination is the highest score thus far, or the first score, make it the totaldict
            if (combiscore > totaldict['totalscore']) or (len(totaldict) == 1):
                totaldict = {'totalscore': combiscore, 'combination': combination, 'semevallist': collectedsemeval,
                             'semevalpertaglist': collectedsemevalpertag}

        selectedsemevallist = selectedsemevallist + totaldict['semevallist']
        selectedsemevallistpertag = selectedsemevallistpertag + totaldict['semevalpertaglist']

    alldict = {}
    alldict.update({'Total_scores': {}})

    enttypecorrect = sum([x['ent_type']['correct'] for x in selectedsemevallist])
    enttypeincorrect = sum([x['ent_type']['incorrect'] for x in selectedsemevallist])
    enttypepartial = sum([x['ent_type']['partial'] for x in selectedsemevallist])
    enttypemissed = sum([x['ent_type']['missed'] for x in selectedsemevallist])
    enttypespurious = sum([x['ent_type']['spurious'] for x in selectedsemevallist])
    enttypepossible = sum([x['ent_type']['possible'] for x in selectedsemevallist])
    enttypeactual = sum([x['ent_type']['actual'] for x in selectedsemevallist])
    enttypeprecision = statistics.mean([x['ent_type']['precision'] for x in selectedsemevallist])
    enttyperecall = statistics.mean([x['ent_type']['recall'] for x in selectedsemevallist])
    enttypef1 = statistics.mean([x['ent_type']['f1'] for x in selectedsemevallist])

    enttypedict = {'Ent_type': {'Precision': enttypeprecision,
                                'Recall': enttyperecall, 'F1': enttypef1}}

    alldict['Total_scores'].update(enttypedict)

    partialcorrect = sum([x['partial']['correct'] for x in selectedsemevallist])
    partialincorrect = sum([x['partial']['incorrect'] for x in selectedsemevallist])
    partialpartial = sum([x['partial']['partial'] for x in selectedsemevallist])
    partialmissed = sum([x['partial']['missed'] for x in selectedsemevallist])
    partialspurious = sum([x['partial']['spurious'] for x in selectedsemevallist])
    partialpossible = sum([x['partial']['possible'] for x in selectedsemevallist])
    partialactual = sum([x['partial']['actual'] for x in selectedsemevallist])
    partialprecision = statistics.mean([x['partial']['precision'] for x in selectedsemevallist])
    partialrecall = statistics.mean([x['partial']['recall'] for x in selectedsemevallist])
    partialf1 = statistics.mean([x['partial']['f1'] for x in selectedsemevallist])

    partialdict = {'Partial': {'Precision': partialprecision,
                               'Recall': partialrecall, 'F1': partialf1}}
    alldict['Total_scores'].update(partialdict)

    strictcorrect = sum([x['strict']['correct'] for x in selectedsemevallist])
    strictincorrect = sum([x['strict']['incorrect'] for x in selectedsemevallist])
    strictpartial = sum([x['strict']['partial'] for x in selectedsemevallist])
    strictmissed = sum([x['strict']['missed'] for x in selectedsemevallist])
    strictspurious = sum([x['strict']['spurious'] for x in selectedsemevallist])
    strictpossible = sum([x['strict']['possible'] for x in selectedsemevallist])
    strictactual = sum([x['strict']['actual'] for x in selectedsemevallist])
    strictprecision = statistics.mean([x['strict']['precision'] for x in selectedsemevallist])
    strictrecall = statistics.mean([x['strict']['recall'] for x in selectedsemevallist])
    strictf1 = statistics.mean([x['strict']['f1'] for x in selectedsemevallist])

    strictdict = {'Strict': {'Precision': strictprecision,
                             'Recall': strictrecall, 'F1': strictf1}}
    alldict['Total_scores'].update(strictdict)

    exactcorrect = sum([x['exact']['correct'] for x in selectedsemevallist])
    exactincorrect = sum([x['exact']['incorrect'] for x in selectedsemevallist])
    exactpartial = sum([x['exact']['partial'] for x in selectedsemevallist])
    exactmissed = sum([x['exact']['missed'] for x in selectedsemevallist])
    exactspurious = sum([x['exact']['spurious'] for x in selectedsemevallist])
    exactpossible = sum([x['exact']['possible'] for x in selectedsemevallist])
    exactactual = sum([x['exact']['actual'] for x in selectedsemevallist])
    exactprecision = statistics.mean([x['exact']['precision'] for x in selectedsemevallist])
    exactrecall = statistics.mean([x['exact']['recall'] for x in selectedsemevallist])
    exactf1 = statistics.mean([x['exact']['f1'] for x in selectedsemevallist])

    exactdict = {'Exact': {'Precision': exactprecision,
                           'Recall': exactrecall, 'F1': exactf1}}
    alldict['Total_scores'].update(exactdict)

    alldict.update({'Scores_per_tag': {}})

    alldict['Scores_per_tag'].update({'Subjects': {}})

    subenttypecorrect = sum([x['SUB']['ent_type']['correct'] for x in selectedsemevallistpertag])
    subenttypeincorrect = sum([x['SUB']['ent_type']['incorrect'] for x in selectedsemevallistpertag])
    subenttypepartial = sum([x['SUB']['ent_type']['partial'] for x in selectedsemevallistpertag])
    subenttypemissed = sum([x['SUB']['ent_type']['missed'] for x in selectedsemevallistpertag])
    subenttypespurious = sum([x['SUB']['ent_type']['spurious'] for x in selectedsemevallistpertag])
    subenttypepossible = sum([x['SUB']['ent_type']['possible'] for x in selectedsemevallistpertag])
    subenttypeactual = sum([x['SUB']['ent_type']['actual'] for x in selectedsemevallistpertag])
    subenttypeprecision = statistics.mean([x['SUB']['ent_type']['precision'] for x in selectedsemevallistpertag])
    subenttyperecall = statistics.mean([x['SUB']['ent_type']['recall'] for x in selectedsemevallistpertag])
    subenttypef1 = statistics.mean([x['SUB']['ent_type']['f1'] for x in selectedsemevallistpertag])

    subenttypedict = {'Ent_type': {'Precision': subenttypeprecision,
                                   'Recall': subenttyperecall, 'F1': subenttypef1}}
    alldict['Scores_per_tag']['Subjects'].update(subenttypedict)

    subpartialcorrect = sum([x['SUB']['partial']['correct'] for x in selectedsemevallistpertag])
    subpartialincorrect = sum([x['SUB']['partial']['incorrect'] for x in selectedsemevallistpertag])
    subpartialpartial = sum([x['SUB']['partial']['partial'] for x in selectedsemevallistpertag])
    subpartialmissed = sum([x['SUB']['partial']['missed'] for x in selectedsemevallistpertag])
    subpartialspurious = sum([x['SUB']['partial']['spurious'] for x in selectedsemevallistpertag])
    subpartialpossible = sum([x['SUB']['partial']['possible'] for x in selectedsemevallistpertag])
    subpartialactual = sum([x['SUB']['partial']['actual'] for x in selectedsemevallistpertag])
    subpartialprecision = statistics.mean([x['SUB']['partial']['precision'] for x in selectedsemevallistpertag])
    subpartialrecall = statistics.mean([x['SUB']['partial']['recall'] for x in selectedsemevallistpertag])
    subpartialf1 = statistics.mean([x['SUB']['partial']['f1'] for x in selectedsemevallistpertag])

    subpartialdict = {'Partial': {'Precision': subpartialprecision,
                                  'Recall': subpartialrecall, 'F1': subpartialf1}}
    alldict['Scores_per_tag']['Subjects'].update(subpartialdict)

    substrictcorrect = sum([x['SUB']['strict']['correct'] for x in selectedsemevallistpertag])
    substrictincorrect = sum([x['SUB']['strict']['incorrect'] for x in selectedsemevallistpertag])
    substrictpartial = sum([x['SUB']['strict']['partial'] for x in selectedsemevallistpertag])
    substrictmissed = sum([x['SUB']['strict']['missed'] for x in selectedsemevallistpertag])
    substrictspurious = sum([x['SUB']['strict']['spurious'] for x in selectedsemevallistpertag])
    substrictpossible = sum([x['SUB']['strict']['possible'] for x in selectedsemevallistpertag])
    substrictactual = sum([x['SUB']['strict']['actual'] for x in selectedsemevallistpertag])
    substrictprecision = statistics.mean([x['SUB']['strict']['precision'] for x in selectedsemevallistpertag])
    substrictrecall = statistics.mean([x['SUB']['strict']['recall'] for x in selectedsemevallistpertag])
    substrictf1 = statistics.mean([x['SUB']['strict']['f1'] for x in selectedsemevallistpertag])

    substrictdict = {'Strict': {'Precision': substrictprecision,
                                'Recall': substrictrecall, 'F1': substrictf1}}
    alldict['Scores_per_tag']['Subjects'].update(substrictdict)

    subexactcorrect = sum([x['SUB']['exact']['correct'] for x in selectedsemevallistpertag])
    subexactincorrect = sum([x['SUB']['exact']['incorrect'] for x in selectedsemevallistpertag])
    subexactpartial = sum([x['SUB']['exact']['partial'] for x in selectedsemevallistpertag])
    subexactmissed = sum([x['SUB']['exact']['missed'] for x in selectedsemevallistpertag])
    subexactspurious = sum([x['SUB']['exact']['spurious'] for x in selectedsemevallistpertag])
    subexactpossible = sum([x['SUB']['exact']['possible'] for x in selectedsemevallistpertag])
    subexactactual = sum([x['SUB']['exact']['actual'] for x in selectedsemevallistpertag])
    subexactprecision = statistics.mean([x['SUB']['exact']['precision'] for x in selectedsemevallistpertag])
    subexactrecall = statistics.mean([x['SUB']['exact']['recall'] for x in selectedsemevallistpertag])
    subexactf1 = statistics.mean([x['SUB']['exact']['f1'] for x in selectedsemevallistpertag])

    subexactdict = {'Exact': {'Precision': subexactprecision,
                              'Recall': subexactrecall, 'F1': subexactf1}}
    alldict['Scores_per_tag']['Subjects'].update(subexactdict)

    alldict['Scores_per_tag'].update({'Predicates': {}})

    predenttypecorrect = sum([x['PRED']['ent_type']['correct'] for x in selectedsemevallistpertag])
    predenttypeincorrect = sum([x['PRED']['ent_type']['incorrect'] for x in selectedsemevallistpertag])
    predenttypepartial = sum([x['PRED']['ent_type']['partial'] for x in selectedsemevallistpertag])
    predenttypemissed = sum([x['PRED']['ent_type']['missed'] for x in selectedsemevallistpertag])
    predenttypespurious = sum([x['PRED']['ent_type']['spurious'] for x in selectedsemevallistpertag])
    predenttypepossible = sum([x['PRED']['ent_type']['possible'] for x in selectedsemevallistpertag])
    predenttypeactual = sum([x['PRED']['ent_type']['actual'] for x in selectedsemevallistpertag])
    predenttypeprecision = statistics.mean([x['PRED']['ent_type']['precision'] for x in selectedsemevallistpertag])
    predenttyperecall = statistics.mean([x['PRED']['ent_type']['recall'] for x in selectedsemevallistpertag])
    predenttypef1 = statistics.mean([x['PRED']['ent_type']['f1'] for x in selectedsemevallistpertag])

    predenttypedict = {
        'Ent_type': {'Precision': predenttypeprecision,
                     'Recall': predenttyperecall, 'F1': predenttypef1}}
    alldict['Scores_per_tag']['Predicates'].update(predenttypedict)

    predpartialcorrect = sum([x['PRED']['partial']['correct'] for x in selectedsemevallistpertag])
    predpartialincorrect = sum([x['PRED']['partial']['incorrect'] for x in selectedsemevallistpertag])
    predpartialpartial = sum([x['PRED']['partial']['partial'] for x in selectedsemevallistpertag])
    predpartialmissed = sum([x['PRED']['partial']['missed'] for x in selectedsemevallistpertag])
    predpartialspurious = sum([x['PRED']['partial']['spurious'] for x in selectedsemevallistpertag])
    predpartialpossible = sum([x['PRED']['partial']['possible'] for x in selectedsemevallistpertag])
    predpartialactual = sum([x['PRED']['partial']['actual'] for x in selectedsemevallistpertag])
    predpartialprecision = statistics.mean([x['PRED']['partial']['precision'] for x in selectedsemevallistpertag])
    predpartialrecall = statistics.mean([x['PRED']['partial']['recall'] for x in selectedsemevallistpertag])
    predpartialf1 = statistics.mean([x['PRED']['partial']['f1'] for x in selectedsemevallistpertag])

    predpartialdict = {
        'Partial': {'Precision': predpartialprecision,
                    'Recall': predpartialrecall, 'F1': predpartialf1}}
    alldict['Scores_per_tag']['Predicates'].update(predpartialdict)

    predstrictcorrect = sum([x['PRED']['strict']['correct'] for x in selectedsemevallistpertag])
    predstrictincorrect = sum([x['PRED']['strict']['incorrect'] for x in selectedsemevallistpertag])
    predstrictpartial = sum([x['PRED']['strict']['partial'] for x in selectedsemevallistpertag])
    predstrictmissed = sum([x['PRED']['strict']['missed'] for x in selectedsemevallistpertag])
    predstrictspurious = sum([x['PRED']['strict']['spurious'] for x in selectedsemevallistpertag])
    predstrictpossible = sum([x['PRED']['strict']['possible'] for x in selectedsemevallistpertag])
    predstrictactual = sum([x['PRED']['strict']['actual'] for x in selectedsemevallistpertag])
    predstrictprecision = statistics.mean([x['PRED']['strict']['precision'] for x in selectedsemevallistpertag])
    predstrictrecall = statistics.mean([x['PRED']['strict']['recall'] for x in selectedsemevallistpertag])
    predstrictf1 = statistics.mean([x['PRED']['strict']['f1'] for x in selectedsemevallistpertag])

    predstrictdict = {'Strict': {'Precision': predstrictprecision,
                                 'Recall': predstrictrecall, 'F1': predstrictf1}}
    alldict['Scores_per_tag']['Predicates'].update(predstrictdict)

    predexactcorrect = sum([x['PRED']['exact']['correct'] for x in selectedsemevallistpertag])
    predexactincorrect = sum([x['PRED']['exact']['incorrect'] for x in selectedsemevallistpertag])
    predexactpartial = sum([x['PRED']['exact']['partial'] for x in selectedsemevallistpertag])
    predexactmissed = sum([x['PRED']['exact']['missed'] for x in selectedsemevallistpertag])
    predexactspurious = sum([x['PRED']['exact']['spurious'] for x in selectedsemevallistpertag])
    predexactpossible = sum([x['PRED']['exact']['possible'] for x in selectedsemevallistpertag])
    predexactactual = sum([x['PRED']['exact']['actual'] for x in selectedsemevallistpertag])
    predexactprecision = statistics.mean([x['PRED']['exact']['precision'] for x in selectedsemevallistpertag])
    predexactrecall = statistics.mean([x['PRED']['exact']['recall'] for x in selectedsemevallistpertag])
    predexactf1 = statistics.mean([x['PRED']['exact']['f1'] for x in selectedsemevallistpertag])

    predexactdict = {'Exact': {'Precision': predexactprecision,
                               'Recall': predexactrecall, 'F1': predexactf1}}
    alldict['Scores_per_tag']['Predicates'].update(predexactdict)

    alldict['Scores_per_tag'].update({'Objects': {}})

    objenttypecorrect = sum([x['OBJ']['ent_type']['correct'] for x in selectedsemevallistpertag])
    objenttypeincorrect = sum([x['OBJ']['ent_type']['incorrect'] for x in selectedsemevallistpertag])
    objenttypepartial = sum([x['OBJ']['ent_type']['partial'] for x in selectedsemevallistpertag])
    objenttypemissed = sum([x['OBJ']['ent_type']['missed'] for x in selectedsemevallistpertag])
    objenttypespurious = sum([x['OBJ']['ent_type']['spurious'] for x in selectedsemevallistpertag])
    objenttypepossible = sum([x['OBJ']['ent_type']['possible'] for x in selectedsemevallistpertag])
    objenttypeactual = sum([x['OBJ']['ent_type']['actual'] for x in selectedsemevallistpertag])
    objenttypeprecision = statistics.mean([x['OBJ']['ent_type']['precision'] for x in selectedsemevallistpertag])
    objenttyperecall = statistics.mean([x['OBJ']['ent_type']['recall'] for x in selectedsemevallistpertag])
    objenttypef1 = statistics.mean([x['OBJ']['ent_type']['f1'] for x in selectedsemevallistpertag])

    objenttypedict = {
        'Ent_type': {'Precision': objenttypeprecision,
                     'Recall': objenttyperecall, 'F1': objenttypef1}}
    alldict['Scores_per_tag']['Objects'].update(objenttypedict)

    objpartialcorrect = sum([x['OBJ']['partial']['correct'] for x in selectedsemevallistpertag])
    objpartialincorrect = sum([x['OBJ']['partial']['incorrect'] for x in selectedsemevallistpertag])
    objpartialpartial = sum([x['OBJ']['partial']['partial'] for x in selectedsemevallistpertag])
    objpartialmissed = sum([x['OBJ']['partial']['missed'] for x in selectedsemevallistpertag])
    objpartialspurious = sum([x['OBJ']['partial']['spurious'] for x in selectedsemevallistpertag])
    objpartialpossible = sum([x['OBJ']['partial']['possible'] for x in selectedsemevallistpertag])
    objpartialactual = sum([x['OBJ']['partial']['actual'] for x in selectedsemevallistpertag])
    objpartialprecision = statistics.mean([x['OBJ']['partial']['precision'] for x in selectedsemevallistpertag])
    objpartialrecall = statistics.mean([x['OBJ']['partial']['recall'] for x in selectedsemevallistpertag])
    objpartialf1 = statistics.mean([x['OBJ']['partial']['f1'] for x in selectedsemevallistpertag])

    objpartialdict = {
        'Partial': {'Precision': objpartialprecision,
                    'Recall': objpartialrecall, 'F1': objpartialf1}}
    alldict['Scores_per_tag']['Objects'].update(objpartialdict)

    objstrictcorrect = sum([x['OBJ']['strict']['correct'] for x in selectedsemevallistpertag])
    objstrictincorrect = sum([x['OBJ']['strict']['incorrect'] for x in selectedsemevallistpertag])
    objstrictpartial = sum([x['OBJ']['strict']['partial'] for x in selectedsemevallistpertag])
    objstrictmissed = sum([x['OBJ']['strict']['missed'] for x in selectedsemevallistpertag])
    objstrictspurious = sum([x['OBJ']['strict']['spurious'] for x in selectedsemevallistpertag])
    objstrictpossible = sum([x['OBJ']['strict']['possible'] for x in selectedsemevallistpertag])
    objstrictactual = sum([x['OBJ']['strict']['actual'] for x in selectedsemevallistpertag])
    objstrictprecision = statistics.mean([x['OBJ']['strict']['precision'] for x in selectedsemevallistpertag])
    objstrictrecall = statistics.mean([x['OBJ']['strict']['recall'] for x in selectedsemevallistpertag])
    objstrictf1 = statistics.mean([x['OBJ']['strict']['f1'] for x in selectedsemevallistpertag])

    objstrictdict = {
        'Strict': {'Precision': objstrictprecision,
                   'Recall': objstrictrecall, 'F1': objstrictf1}}
    alldict['Scores_per_tag']['Objects'].update(objstrictdict)

    objexactcorrect = sum([x['OBJ']['exact']['correct'] for x in selectedsemevallistpertag])
    objexactincorrect = sum([x['OBJ']['exact']['incorrect'] for x in selectedsemevallistpertag])
    objexactpartial = sum([x['OBJ']['exact']['partial'] for x in selectedsemevallistpertag])
    objexactmissed = sum([x['OBJ']['exact']['missed'] for x in selectedsemevallistpertag])
    objexactspurious = sum([x['OBJ']['exact']['spurious'] for x in selectedsemevallistpertag])
    objexactpossible = sum([x['OBJ']['exact']['possible'] for x in selectedsemevallistpertag])
    objexactactual = sum([x['OBJ']['exact']['actual'] for x in selectedsemevallistpertag])
    objexactprecision = statistics.mean([x['OBJ']['exact']['precision'] for x in selectedsemevallistpertag])
    objexactrecall = statistics.mean([x['OBJ']['exact']['recall'] for x in selectedsemevallistpertag])
    objexactf1 = statistics.mean([x['OBJ']['exact']['f1'] for x in selectedsemevallistpertag])

    objexactdict = {'Exact': {'Precision': objexactprecision,
                              'Recall': objexactrecall, 'F1': objexactf1}}
    alldict['Scores_per_tag']['Objects'].update(objexactdict)

    return alldict

def calculateExactTripleScore(reflist, candlist, alldict):
    newreflist = [[string.lower() for string in sublist] for sublist in reflist]
    newcandlist = [[string.lower() for string in sublist] for sublist in candlist]
    #First get all the classes by combining the triples in the candidatelist and referencelist
    allclasses = newcandlist + newreflist
    allclasses = [item for items in allclasses for item in items]
    allclasses = list(set(allclasses))

    lb = preprocessing.MultiLabelBinarizer(classes=allclasses)
    mcbin = lb.fit_transform(newcandlist)
    mrbin = lb.fit_transform(newreflist)

    precision = precision_score(mrbin, mcbin, average='macro')
    recall = recall_score(mrbin, mcbin, average='macro')
    f1 = f1_score(mrbin, mcbin, average='macro')

    alldict.update({'Exact_match': {'Precision': precision, 'Recall': recall, 'F1': f1}})

    return alldict

def main(reffile, candfile, outputfile):
    reflist, newreflist = getRefs(reffile)
    candlist, newcandlist = getCands(candfile)
    totalsemevallist, totalsemevallistpertag = calculateAllScores(newreflist, newcandlist)
    alldict = calculateSystemScore(totalsemevallist, totalsemevallistpertag, newreflist, newcandlist)
    alldict2 = calculateExactTripleScore(reflist, candlist, alldict)
    with open(outputfile, 'w') as outfile:
        json.dump(alldict2, outfile)

#main(currentpath + '/Refs.xml', currentpath + '/Cands2.xml', currentpath + '/Results.json')
if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2], sys.argv[3])