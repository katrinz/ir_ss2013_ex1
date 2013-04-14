188.412 Information Retrieval

Assignment 1 
Text Indexer & Similarity Retrieval
 
Prepared by Group M: 
1129105 - Dmytro Grygorenko 
1129108 - Kateryna Zaslavska 

It was done two parts: Task 1.1, Task 1.2 and additional Task.
In order to pass all requirements of the lab, it was implemented three separate applications:
1. CreateIndexApp - creates inverted index for TF*IDF-like algorithm by defined parameters and saves it in to specified file. 
2. DocSearchApp - Retrieves top 10 relevant documents in the corpus for each document from specified list and saves output results into specified folder.

Using 
Next files are necessary to use IREngine: 
-  Executable irEngine-1.0-jar-with-dependencies.jar
-  File of Properties createindex.properties
-  File of Properties docsearch.properties
-  File of stop words stopList.txt

In the assignment classis TF*IDF algorithm was implemented.
Besides that, based on the answer of Mihai Lupu (https://tuwel.tuwien.ac.at/mod/forum/discuss.php?d=42237),
Okapi BM-like algorithm (http://en.wikipedia.org/wiki/Okapi_BM25) (BM25 was used for evaluations) was implemented as well as compensation for the missing of Test 1. 
 
Therefore the output result contains 120 files, instead of 60 (60 files per each algorithm).

During evaluation next configurations was used for our indexes:
1.	Using of stop words (stop list file exists in the project folder)
2.	Stemming 
3.	Maximal document count in the Term Vector was unlimited
4.	Maximal term size in the Term Vector was unlimited

For small postings list Lower Bound = 0.2, Upper Bound = 0.8
All Term Vectors were normalized in order to standardize all results.
For medium postings list Lower Bound = 0.1, Upper Bound = 0.95
For large postings list there were no bounds. 

===========================================================================================================================================================

File createindex.properties stores next settings: 
- maxDocSize = maximum size of documents which term frequencies are stored in the term vector creator: 0 - no limit 
- maxTermSize = maximun size of terms in each document that are taken into account during computation of the term vector: -1 - no limit
- normalize = sets whether term vectors will be normalized: true, false
- usegzip = sets whether gzip will be used for storing and loading index: true, false
- stemming = sets whether stemming will be used for filtering of documents: true, false
- stopWords = sets whether stop words will be used for filtering of documents: true, false
- lowerBound = lower bound of measurement function output for the term in the index: [0;1], e.g. 0.2
- upperBound = upper bound of measurement function output for the term in the index: [0;1], e.g. 0.8
- irfunction = type of information retrieval function: <bm> - BM25 algorithm is used, otherwise TF*IDF classic algorithm is used
- bmK = parameter k1 if BM25 is used, e.g. 2.0 
- bmB = parameter b if BM25 is used, e.g. 0.75
- documentSetPath = path to the corpus, e.g. D:\\corpus\\
- indexPath = path to the inverted index, e.g. D:\\indices\\index_small.tar.gz

Next command is used to start the CreateIndexApp application: 
java -cp irEngine-1.0-jar-with-dependencies.jar tu.wien.irengine.CreateIndexApp

Files createindex.properties and stopList.txt should be in the same folder.

===========================================================================================================================================================

File docsearch.properties stores next settings: 
- maxDocSize = maximum size of documents which term frequencies are stored in the term vector creator: 0 - no limit 
- maxTermSize = maximun size of terms in each document that are taken into account during computation of the term vector: -1 - no limit
- normalize = sets whether term vectors will be normalized: true, false
- usegzip = sets whether gzip will be used for storing and loading index: true, false
- stemming = sets whether stemming will be used for filtering of documents: true, false
- stopWords = sets whether stop words will be used for filtering of documents: true, false
- lowerBound = lower bound of measurement function output for the term in the index: [0;1], e.g. 0.2
- upperBound = upper bound of measurement function output for the term in the index: [0;1], e.g. 0.8
- documentSetPath = path to the corpus, e.g. D:\\corpus\\
- indexPath = path to the inverted index, e.g. D:\\indices\\index_small.tar.gz
- outDir = path to output directory, e.g. D:\\out\\
- indexSize = string name of the postings list (used just for the name of file and results): (small, large, medium, etc.)
- irfunction = type of information retrieval function: <bm> - BM25 algorithm is used, otherwise TF*IDF classic algorithm is used
- bmK = parameter k1 if BM25 is used, e.g. 2.0 
- bmB = parameter b if BM25 is used, e.g. 0.75
- docs = comma-separated list of all topics, e.g. misc.forsale/76057,talk.religion.misc/83561,talk.politics.mideast/75422

Next command is used to start the DocSearchApp application: 
java -cp irEngine-1.0-jar-with-dependencies.jar tu.wien.irengine.DocSearchApp

Files docsearch.properties and stopList.txt should be in the same folder.

===========================================================================================================================================================