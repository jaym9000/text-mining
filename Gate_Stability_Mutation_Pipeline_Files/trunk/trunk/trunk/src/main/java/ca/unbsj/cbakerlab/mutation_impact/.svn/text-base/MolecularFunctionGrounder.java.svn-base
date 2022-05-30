package ca.unbsj.cbakerlab.mutation_impact;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.tartarus.snowball.SnowballStemmer;

public class MolecularFunctionGrounder {
	
	private static final Logger log = Logger.getLogger(MolecularFunctionGrounder.class);

	
	public void ground(Document doc){
		try {  //log.trace("AAAA: "+ doc.getFeatures());
			String pacs = doc.getFeatures().get("pacs").toString();
			//log.trace("pacs: "+pacs);
			
			// Parse pacs.
			List<String> pacList = new ArrayList<String>();
			
			//List<String[]> pacsAndOffsets = new ArrayList<String[]>();
			
			String[] split = pacs.split("__");
			for(String s : split){
				String[] split2 = s.split("_");
				pacList.add(split2[0]);
				//pacsAndOffsets.add(split2);
			}
			log.trace("pacList: "+pacList);
			/*
			String pac = "";
			for(int i = 0; i < pacs.length(); i++) {
				if(i < 5) {
					pac+=pacs.charAt(i);
				}
				else if(i == 5) {
					pac+=pacs.charAt(i);
					pacList.add(new String(pac));
					pac="";	
				}
				else if(i == 6) {
					pacs = pacs.substring(7);
					i=-1;
				}
			}*/
			
			/**/

			Properties pro = new Properties();
			pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
			//String DATABASE_URL = pro.getProperty("DATABASE_URL");
			//String USERNAME = pro.getProperty("DATABASE_USERNAME");
			//String PASSWORD = pro.getProperty("DATABASE_PASSWORD");

			String DATABASE_URL = "jdbc:mysql://138.119.12.33:3306/swissprot";
			String USERNAME = "swissprot";
			String PASSWORD = "password";

			Class.forName("com.mysql.jdbc.Driver");		
			Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			
			String goidQuery = "SELECT * FROM swissprotgeneontology WHERE ";
			for(int i = 0; i < pacList.size(); i++) {
				if(i > 0) {
					goidQuery += "OR pac = '"+pacList.get(i)+"' ";
				} else {
					goidQuery += "pac = '"+pacList.get(i)+"' ";
				}
			}
			log.debug("goidQuery: "+goidQuery);

			Statement goidSt = con.createStatement();
			ResultSet goidRs = goidSt.executeQuery(goidQuery);
			
			List<String> goids = new ArrayList<String>();
			while(goidRs.next()) {
				goids.add(goidRs.getString("goid"));
				log.debug("result: "+goidRs.getString("goid"));
			}
			/**/

			Map<String, Set<String>> goidSynsets = new HashMap<String, Set<String>>();
			GOParser gop = new GOParser();
			
			for(String goid : goids) {
				log.debug("goid relevant to document: "+goid);
				Set<String> parentGoids = new HashSet<String>(gop.retrievePath(goid));
				Set<String> synset = new HashSet<String>();
				for(String parentGoid : parentGoids) {
					synset.addAll(gop.synMap.get(parentGoid));
				}
				synset.addAll(gop.synMap.get(goid));
				goidSynsets.put(goid, synset);
			}
			
			TextPreparator tp = new TextPreparator();
			
			Map<String, Set<Set<String>>> preparedGoidSynsets = new HashMap<String, Set<Set<String>>>();
			Map<String, String> goidKind = new HashMap<String,String>();
			int numberOfEnzymaticGoids = 0;
			String enzymaticGoid = "";
			
			for(String goid : goidSynsets.keySet()) {
				boolean binding = false;
				Set<Set<String>> preparedSynset = new HashSet<Set<String>>();
				for(String synonym : goidSynsets.get(goid)) {
					if(synonym.equals("binding")) {
						binding = true;
					}
					///System.out.println(synonym);
					preparedSynset.add(new HashSet<String>(tp.stemWords(tp.removeStopWords(tp.stringToWords(synonym)))));
				}
				if(binding) {
					goidKind.put(goid,"binding");
				}
				else {
					goidKind.put(goid,"activity");
					numberOfEnzymaticGoids++;
					enzymaticGoid = goid;
				}
				preparedGoidSynsets.put(goid,preparedSynset);
			}
			
			//try to ground all mentions of molecular functions
			AnnotationSet mfAS = doc.getAnnotations().get("MolecularFunction");
			for(Annotation mfA : mfAS) {
				Map<String,Double> scores = new HashMap<String,Double>();
				String mfText = doc.getContent().getContent(mfA.getStartNode().getOffset(), mfA.getEndNode().getOffset()).toString();
				Set<String> mfWordSet = new HashSet<String>(tp.stemWords(tp.removeStopWords(tp.stringToWords(mfText))));
				
				//check kind of mf
				String kind = "";
				if(mfText.matches(".*activity.*")) {
					kind = "activity";
				} 
				else {
					kind = "binding";
				}
				
				//calculate grounding score (and choose highest)
				for(String goid : preparedGoidSynsets.keySet()) {
					if(mfWordSet.isEmpty() && goidKind.get(goid).equals(kind)) {
						scores.put(goid,0.0);
					}
					else if(!mfWordSet.isEmpty() && goidKind.get(goid).equals(kind)) {
						double score = 0.0;
						for(Set<String> synonym : preparedGoidSynsets.get(goid)) {
							if(!synonym.isEmpty()) {
								Set<String> intersection = new HashSet<String>(mfWordSet);
								intersection.retainAll(synonym);
								double tempScore = ((double)(intersection.size()*intersection.size()))/((double)(synonym.size()*mfWordSet.size()));
								if(tempScore > score) {
									score = tempScore;
								}
							} 
						}
						scores.put(goid,score);
					}
				}
				
				double highscore = 0.0;
				String bestMatch = "";
				for(String goid : scores.keySet()) {
					if(scores.get(goid)>highscore) {
						highscore = scores.get(goid);
						bestMatch = goid;
					}
					else if(scores.get(goid)==highscore) {
						bestMatch = "noMatchPossible";
					}
				}
				
				if(!bestMatch.equals("noMatchPossible") && !bestMatch.equals("")) {
					log.debug("match: "+bestMatch);
					//mfA is grounded to bestMatch with score highscore.
					FeatureMap features = mfA.getFeatures();
					features.put("isGroundedTo",bestMatch);
					features.put("groundingScore",highscore);
					features.put("hasNormalizedName",gop.synMap.get(bestMatch).get(0));
					doc.getAnnotations().add(mfA.getStartNode().getOffset(),mfA.getEndNode().getOffset(),"GroundedMolecularFunction",features);
				}else{
					log.debug("no match: "+bestMatch);
					FeatureMap features = mfA.getFeatures();
					features.put("isGroundedTo","P11111_0");
					features.put("groundingScore",0.0);
					features.put("hasNormalizedName","mfNameNormalized");
					doc.getAnnotations().add(mfA.getStartNode().getOffset(),mfA.getEndNode().getOffset(),"GroundedMolecularFunction",features);
				}
			}
			
			//ground kinetic variables if it is possible, ie. there is only one enzymatic molecular function.
			if(numberOfEnzymaticGoids == 1){
				AnnotationSet kVars = doc.getAnnotations().get("KineticVariable");
				for(Annotation kVar : kVars) {
					FeatureMap features = kVar.getFeatures();
					features.put("measuredFor",enzymaticGoid);
					features.put("groundingScore",1.0);
					doc.getAnnotations().add(kVar.getStartNode().getOffset(),kVar.getEndNode().getOffset(),"KineticVariableMention",features);
				}
			}
			
			//remove old annotationsets..
			//inputAS.removeAll(inputAS.get("KineticVariable"));
			//inputAS.removeAll(inputAS.get("kVar"));
			//inputAS.removeAll(inputAS.get("KineticVariable"));
			//inputAS.removeAll(inputAS.get("MolecularFunction"));
			//inputAS.removeAll(inputAS.get("PossibleMF"));
			//inputAS.removeAll(inputAS.get("NP"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class GOParser {

	public  HashMap<String,List<String>> isaMap = new HashMap<String,List<String>>();
	public  HashMap<String,List<String>> synMap = new HashMap<String,List<String>>();
	private URI p;
	
	public GOParser() {
		final Logger log = Logger.getLogger(GOParser.class);

		try {
		Properties pro = new Properties();
		pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
		
		String geneOntologyUrl = pro.getProperty("GENE_ONTOLOGY_URL");
		log.info("geneOntologyUrl: "+geneOntologyUrl);
		
		//p = this.getClass().getClassLoader().getResource(geneOntologyUrl).toURI();
		
       // pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));

		//p = this
		//		.getClass()
		//		.getClassLoader()
		//		.getResource(geneOntologyUrl)
		//		.toURI();
		/*System.out.println("p: "+this
				.getClass()
				.getClassLoader()
				.getResource(geneOntologyUrl)
				.toURI());

		//open file
		//FileInputStream fstream = new FileInputStream(new File(p));
		
		FileInputStream fstream = new FileInputStream(new File(this
				.getClass()
				.getClassLoader()
				.getResource(geneOntologyUrl)
				.toURI()));
		*/
		
		
		InputStream is = this.getClass().getResourceAsStream("/"+geneOntologyUrl);
		
		//Get the object of DataInputStream
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = new String();
		boolean firstTime = true;
		Pattern  synPattern = Pattern.compile("\"[^\"]+");
		Matcher matcher;
		String id = new String();
		List<String> synset = new ArrayList<String>();
		boolean namespaceIsMF = false;
		List<String> parents = new ArrayList<String>();

		while ((strLine = br.readLine()) != null)   {
			if(strLine.length()>3) {
				if(strLine.substring(0,3).equals("id:")) {
					id = new String(strLine.substring(4));
					firstTime = false;
				}
				else if(strLine.length() > 5) {
				   if(strLine.substring(0,6).equals("[Term]") && !firstTime) {
					   //was previous mf?
					   if(namespaceIsMF) {
						   isaMap.put(new String(id), new ArrayList<String>(parents));
						   synMap.put(new String(id), new ArrayList<String>(synset));
						   namespaceIsMF = false;
						   id = "";
						   synset.clear();
						   parents.clear();
					   }
					   else {
						   id = "";
						   synset.clear();
						   parents.clear();
					   }
				   }
				   else if(strLine.substring(0,5).equals("name:")) {
					   synset.add(new String(strLine.substring(6)));
				   }
				   else if(strLine.substring(0,5).equals("is_a:")) {
					   parents.add(new String(strLine.substring(6,16)));
				   }
				   else if(strLine.length() > 7) {
					   if(strLine.substring(0,8).equals("synonym:")) {
						   matcher = synPattern.matcher(strLine.substring(9));
						   if(matcher.find()) {
							   synset.add(new String(matcher.group().substring(1)));
						   }
					   }
					   else if(strLine.length() > 9) {
						   if(strLine.substring(0,10).equals("namespace:")) {
							   if(strLine.substring(11).equals("molecular_function")) {
								   namespaceIsMF = true;
							   }
						   }
					   }
				   }
				}
			}
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	public List<String> retrievePath(String goid) {
		List<String> result = new ArrayList<String>();
		result.add(goid);
		if(isaMap.containsKey(goid)) {     
		   for(String nextGoid : isaMap.get(goid)) {
			   result.addAll(retrievePath(nextGoid));
		   }
		}
		return result;
	}
}

class TextPreparator {
	private String[] STOP_WORDS_ARRAY;
	private List<String> STOP_WORDS_LIST;
	
	public TextPreparator() {
		STOP_WORDS_ARRAY = new String[] { "a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "computer", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "i", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thick", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves" };
		STOP_WORDS_LIST = Arrays.asList(STOP_WORDS_ARRAY);
	}
	
	public List<String> stringToWords(String in) {
		List<String> result = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(in);
		while(st.hasMoreTokens()) {
			result.add(st.nextToken().toLowerCase());
		}
		return result;
	}

	public List<String> removeStopWords(List<String> in) {
		List<String> result = (ArrayList<String>)((ArrayList<String>) in).clone();
		int i = 0;
		for(String word : in) {
			if(STOP_WORDS_LIST.contains(word)) {
				result.remove(i);
				i--;
			}
			i++;
		}
		return result;
	}
	
	
	public List<String> stemWords(List<String> in) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<String> result = (ArrayList<String>)((ArrayList<String>) in).clone();
		final Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
		SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
		int i = 0;
		for(String word : in) {
			stemmer.setCurrent(word);
			stemmer.stem();
			result.remove(i);
			result.add(i,stemmer.getCurrent());
			i++;
		}
		return result;
	}
}
