import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.lucene.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class luceneIndexing {
public static int count=0;
public static int c=0;
public static LinkedList<Integer> intermediate = new LinkedList();
public static LinkedList<Integer> inter = new LinkedList();


// GetPostings() For printing the postings list
static void GetPostings(LinkedList<Integer> postingsList,Writer out) throws IOException{
	out.write("Postings list: ");
	for(int i=0;i<postingsList.size();i++)
	{
		out.write(postingsList.get(i)+" ");
	}
	out.write("\r\n");

}


//TAATAND() performs Term-at-a-time AND query
	static luceneIndexing TAATAND(LinkedList<Integer> postingsList, LinkedList<Integer> postingList,luceneIndexing ob) {
	 ob.intermediate = new LinkedList();
		int i = 0, j = 0;
		//postings list of two terms are checked at a time 
		while ((i < postingsList.size()) && (j < postingList.size())) { //till any of the list is not exhausted,we check for intersection
			
			 
		    if (postingsList.get(i) < postingList.get(j)) { //the pointer of smaller docId is incremented
				
				i++;
				ob.count++;
			}  
			else if (postingsList.get(i) > postingList.get(j)) {
				
				j++;
				ob.count++;
				
			}
			else {	// if both docIds are equal increment both the pointers
				
					ob.intermediate.add(postingsList.get(i));
					i++;
					j++;
					ob.count++;
				} 

		}

		
		 return ob; // class object returns intermediate list and counter that checks for no of comparisons
		
	}
	
	
//TAATOR performs Term-at-a-time OR query
	static luceneIndexing TAATOR(LinkedList<Integer> postingsList, LinkedList<Integer> postingList,luceneIndexing obj) {
		obj.inter=new LinkedList();
		int i = 0, j = 0;
		while (i < postingsList.size() && j < postingList.size()) { //the one  with smaller docId  out of the two postings list is added to our OR list and pointer is incremented
			if (postingsList.get(i)> postingList.get(j)) {
				obj.inter.add(postingList.get(j));
				j++;
				obj.c++;
				
				
			} else if (postingsList.get(i) < postingList.get(j)) {
				obj.inter.add(postingsList.get(i));
				i++;
				obj.c++;
			} else  {
				obj.inter.add(postingsList.get(i)); //if docIDs are equal add docId and increment pointers for both the list
				i++;
				j++;
				obj.c++;
				
			}
		}
		while (i < postingsList.size()) {      //if postingList is not exhausted add remaining docIds to the OR list
			obj.inter.add(postingsList.get(i));
			i++;
		}
		while (j < postingList.size()) {    //if postingList is not exhausted add remaining docIds to the OR list
			obj.inter.add(postingList.get(j));
			j++;
		}
		
		return obj;     //class object returns intermediate list and counter that checks for no of comparisons

	}
	
	
	
	//DAATAND performs Document-at-a-time AND query
	static void DAATAND(LinkedList<Integer> []v,Writer out) throws IOException{
		LinkedList<Integer> intermediate = new LinkedList();
		
		int count=0;
		int i=0;
		int [] a=new int [v.length];
		while(i<v[0].size()){  //while loop runs till the postinglist of first term is not exhausted 
			
			int term=1;
			while(term<v.length){ //it checks for all terms of the input query 
			x: while(v[term].get(a[term])<v[0].get(i)){	 
				if(a[term]<v[term].size()-1){       //it finds the minimum from the first docId of postinglist of all terms and increments its pointer 
					count++;
				a[term]++;
				}
				else{ 
					break x;
					}
			}
			 if(v[term].get(a[term]) < v[0].get(i)|| v[term].get(a[term])>v[0].get(i)){       
				
				
				 if( v[term].get(a[term])>v[0].get(i)){
					count++;}
				 i++;
					break;
						
			}else{  
				term++;
				count++;
			}
			}
			if(term==v.length){       //if postinglist of all terms have same docId than add the docId in AND list and increment pointer for postinglist of all terms
				intermediate.add(v[0].get(i));
				
				i++;
			}
		}
		
		
		//Prints PostingList for DAAT AND query
		
		out.write("Results: ");
		for(int x=0;x<intermediate.size();x++){
			out.write(intermediate.get(x)+ " ");
		}
		if(intermediate.size()==0){
			out.write("empty");
		}
		out.write("\r\n"+"Number of documents in results: "+intermediate.size()+"\r\n");
		out.write("Number of comparisons: "+count+" "+"\r\n");
		
	
	}
	
	
	
	
	//DAATOr performs Document-at-a-time OR query
	
	static void DAATOR(LinkedList<Integer> []v,Writer out) throws IOException{
		LinkedList<Integer> result = new LinkedList();
	int [] a=new int [v.length];
	int min_term=0;
	Boolean if_all_terms_exhausted=true;
	int countor=0;
	while(true){   //We check for minimum from first element of postinglist of all terms till the postinglist of all terms does not get exhausted
			if_all_terms_exhausted=true;
			for(int i=0;i<v.length;i++){    
				if(a[i]<v[i].size())
				{
					if_all_terms_exhausted=false;
				}
			}
			if(if_all_terms_exhausted==true)
			{
				break;
			}
		int min=Integer.MAX_VALUE;
		for(int i=0;i<v.length;i++){
			if(a[i]<v[i].size()){
			if(v[i].get(a[i])<min){   //the pointer of the postinglist with minimum docId is incremented and docId is added to OR list 
				min=v[i].get(a[i]);
				min_term=i;
				countor++;
			}else if(v[i].get(a[i])==min){  //if docIds of postingList are same it adds to the OR List only once and pointers of all the terms are incremented
				a[i]++;
				countor++;
			}
		}
		}
		result.add(min);
		a[min_term]++;
	}
	
	
	//Prints PostingList for DAAT OR query
		out.write("Results: ");
		for(int x=0;x<result.size();x++){
			out.write(result.get(x)+ " ");
		}
		if(result.size()==0){
			out.write("empty");
		}
		out.write("\r\n"+"Number of documents in results: "+result.size()+"\r\n");
		out.write("Number of comparisons: "+countor+" "+"\r\n");
		
	}

	
	
	
	
	

	public static void main(String[] args) throws IOException {
		String path = args[0];
		
		FileSystem fs = FileSystems.getDefault();
		Path path1 = fs.getPath(args[0]);
		Writer out =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]),"UTF-8"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[2]),"UTF-8"));
		String sCurrentLine;
		IndexReader reader = DirectoryReader.open(FSDirectory.open(path1));

		int num = reader.maxDoc();
		
		int total = 0;
		
		Fields fields = MultiFields.getFields(reader);
		HashMap<String, LinkedList> m = new HashMap<String, LinkedList>();

		for (String field : fields) {     //creates hashmap for terms of all fields i.e nl, text_fr, text_de, text_ja, text_ru, text_pt, text_es,text_es, text_it, text_da, text_no, and text_sv.
											//key stores the termms and value stores its corresponding postinglist as LinkedList
			if (!field.equals("_version_") && !field.equals("id")) {
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator();

				int count = 0;
				BytesRef text;

				while ((text = termsEnum.next()) != null) {
					LinkedList<Integer> l1 = new LinkedList();

					String s = text.utf8ToString();

					PostingsEnum postenum = MultiFields.getTermDocsEnum(reader, field, text);

					while ((postenum.nextDoc() != PostingsEnum.NO_MORE_DOCS)) {

						l1.add(postenum.docID());

					}

					m.put(s, l1);
					total++;
					count++;

				}
			}

		}
	/*	for (HashMap.Entry<String, LinkedList> pair : m.entrySet()) {

			out.write(pair.getKey() + " " + pair.getValue());
			out.write("\r\n");
}*/
		
		luceneIndexing taatand=new luceneIndexing();
		
		luceneIndexing taator=new luceneIndexing();
		
		while ((sCurrentLine = br.readLine()) != null) {    //each line of the input line reads as seperate query
			luceneIndexing ob = new luceneIndexing();
			luceneIndexing obj = new luceneIndexing();
			
			ob.count=0;
			obj.c=0;
			String[] information = sCurrentLine.split(" ");    //words in each line are taken as terms

			
			
			int x = information.length;
			String t0 = information[0];
			
			String t1 = information[1];
			
			LinkedList<Integer> postingsList = (LinkedList<Integer>) m.get(t0).clone();
			LinkedList<Integer> postingList = (LinkedList<Integer>) m.get(t1).clone();
			
			out.write("GetPostings"+"\r\n");
			out.write(t0+"\r\n");
			GetPostings(postingsList,out);
			out.write("GetPostings"+"\r\n");
			out.write(t1+"\r\n");
			GetPostings(postingList,out);
			
			taatand = TAATAND(postingsList, postingList,ob); //function call to TAATAND function 
			taator = TAATOR(postingsList, postingList,obj);
			LinkedList<Integer>[] v = new LinkedList[x]; // LinkedList array v stores the postinglist of all terms in an array
			v[0] = (LinkedList<Integer>) m.get(t0).clone();
				v[1] = (LinkedList<Integer>) m.get(t1).clone();
				
				
			for (int k = 2; k < information.length; k++) {
				String t2 = information[k];
				v[k] = (LinkedList<Integer>) m.get(information[k]).clone();	// .clone() copies the value of hashmap to LinkedList array
				out.write("GetPostings"+"\r\n");
				out.write(information[k]+"\r\n");
				
				postingList = (LinkedList<Integer>) m.get(t2).clone();
				GetPostings(postingList,out);
				
				taatand = TAATAND(taatand.intermediate, postingList,ob);
			
				taator = TAATOR(taator.inter, postingList,obj);
				
			}
			
			//prints TAAT AND results
			
			out.write("TaatAnd"+"\r\n");
			for(int i=0; i<information.length;i++){
				out.write(information[i]+" ");
			}

			out.write("\r\n"+"Results: ");
			for(int k=0;x<intermediate.size();k++){
				out.write(intermediate.get(k)+ " ");
			}
			if(intermediate.size()==0){
				out.write("empty");
			}
			out.write("\r\n"+"Number of documents in results: "+taatand.intermediate.size()+"\r\n");
			out.write("Number of comparisons: "+taatand.count+" "+"\r\n");
			
			
			//prints TAAT OR results
			
			out.write("TaatOr"+"\r\n");
			
			for(int i=0; i<information.length;i++){
				out.write(information[i]+ " ");
			}
			out.write("\r\n"+"Results: ");
			for(int k=0;k<inter.size();k++){
				out.write(inter.get(k)+ " ");
			}
			if(inter.size()==0){
				out.write("empty");
			}
			out.write("\r\n"+"Number of documents in results: "+taator.inter.size()+"\r\n");
			out.write("Number of comparisons: "+taator.c+" "+"\r\n");

			out.write("DaatAnd"+"\r\n");
			for(int i=0; i<information.length;i++){
				out.write(information[i]+" ");
			}
			out.write("\r\n");
			
			//function call to DAATAND
			
			DAATAND(v,out);
			
			out.write("DaatOr"+"\r\n");
			for(int i=0; i<information.length;i++){
				out.write(information[i]+" ");
			}
			out.write("\r\n");
			
			//function call to DAATOR
		 DAATOR(v,out);
			
			
			
		}
		reader.close();
		out.close();
	}
	
}
