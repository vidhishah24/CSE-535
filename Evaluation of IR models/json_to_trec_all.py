# -*- coding: utf-8 -*-
"""
Thanks to the author Ruhan Sa, who is the TA of IR project 3 in Fall 2015
"""

import json
import urllib.request

count = 1
#langs = {'text_de','text_ru','text_en'}
langs = {'text_ru'}
#cores = {'DFR_core', 'classicCore','booksdemo'}
cores = {'booksdemo'}
#model = {'DFR', 'TFIDF', 'BM25'}
model = {'DFR'}
countRows = 0
for core in cores:
    print(core)
    outf = open('text_ru' + '.txt', 'w+')
    count = 1;
    with open('query_ru.txt', encoding="utf-8") as f:
        for line in f:
            query = line.strip('s\n').replace(':', '')[4:]
            query = urllib.parse.quote(query)
            #print(query)
            countRows = 0
            for lang in langs:
                inurl = 'http://54.191.124.218:8983/solr/' + core + '/select?df=' + lang + '&fl=score,id&indent=on&q=' + query + '&rows=20&wt=json'
                qid = count
                IRModel = core
                #print(inurl)
                data = urllib.request.urlopen(inurl).read()
                docs = json.loads(data.decode('utf-8'))['response']['docs']
                rank = 1
                for doc in docs:
                    countRows += 1
                    if qid > 9:
                        outf.write('0' + str(qid) + ' ' + 'Q' + str(count) + ' ' + str(doc['id']) + ' ' + str(countRows) + ' ' + str(doc['score']) + ' ' + core + '\n')
                    else:
                        outf.write('00' + str(qid) + ' ' + 'Q' + str(count) + ' ' + str(doc['id']) + ' ' + str(countRows) + ' ' + str(doc['score']) + ' ' + core + '\n')
                    rank += 1
            count += 1
    outf.close()
