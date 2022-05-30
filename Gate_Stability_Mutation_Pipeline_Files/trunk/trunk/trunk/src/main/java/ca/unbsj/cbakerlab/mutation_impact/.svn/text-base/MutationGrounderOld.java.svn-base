package ca.unbsj.cbakerlab.mutation_impact;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;




/**
 * CLASS ValueThenKeyComparator
 *
class ValueThenKeyComparator<K extends Comparable<? super K>, V extends Comparable<? super V>> implements Comparator<Map.Entry<K, V>> {

	public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b) {
		int cmp1 = b.getValue().compareTo(a.getValue());
		if (cmp1 != 0) {
			return cmp1;
		} else {
			return a.getKey().compareTo(b.getKey());
		}
	}

}*/

public class MutationGrounderOld {
	 	
	private static final Logger log = Logger.getLogger(MutationGrounderOld.class);

	private AnalysisStatistics statistics;
	private Connection con = null;
	private Connection con2 = null;	
	Map<String,String> PASequenceMapping = new HashMap<String, String>();	


	// map: doc name to list of grounded variant for packs
	static Map<String,List<GroundingVariant>> goldPacsForDebugging = new TreeMap<String,List<GroundingVariant>>();
	
	private static boolean debugging = false;	
	static long corpusQueryTime = 0;
	
	public static String cacheFileName = null;
	public static String cacheForGroundingFileName = null;
	
	StringBuilder debugingResultsSb = new StringBuilder();
	StringBuilder debugingResultsSb2 = new StringBuilder();

	static final Set<String> proteinNameExceptionList = new HashSet<String>(Arrays.asList(
			"new", "has", "was", "all", "gene","unknown","for","similar to","time","similar","type",
			"had","but","not","large","all","out","such","how","end","see", "known protein","gene,","gene, ","black",
			"far","small","can","fragment","iii","this protein","receptor protein","not","blue","brown","run","top", "this family",
			"tag","his","catalytic activity","did","similar","similar to","ret protein","coding sequence","cdna sequence",
			"dna","slow","family","old","not known","are","a protein","rna","age","2 protein","dna sequence","membrane",
			"dna-binding domain","none","black","protein","por protein","putative","binding","amino acid","enzyme","yellow",
			"basic","beta 1","beta-1","unknown protein","period","genome","variable region","white","tail","lambda",
			"signal","pubmed","alpha","period","surface","conserved","truncated","core","similarity to","pre","post","alpha","beta","gamma"));

	static final Set<String> organismNameExceptionList = new HashSet<String>(Arrays.asList(
			"green","bio","adv","new york","athens","a-i","mixed","c18","australia","h10","gag","grey","brown"));
	// this, major, cancer, beta, alpha, gamma
	/**
	 * Used in pipeline (in Jape rule).
	 */
	public MutationGrounderOld(){
		debugging = true;	
	}	

		
	public void groundOld1(Document doc){
		boolean debugging = false;
		try {	
			//System.out.print("Ground mutation mentions...");
						
			//Directory indexDirectory = FSDirectory.open(new File("index"));
			//NGramSearcher searcher = new NGramSearcher(indexDirectory);  
			
			
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/swissprot";
			String DATABASE_URL = "jdbc:mysql://localhost:3306/uniprot";
			//String DATABASE_URL2 = "jdbc:mysql://localhost:3306/trembl2";
			String USERNAME = "swissprot";
			String PASSWORD = "password";

			Class.forName("com.mysql.jdbc.Driver");		
			con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			//con2 = DriverManager.getConnection(DATABASE_URL2, USERNAME, PASSWORD);
			//System.err.println("CONN ESTABLISHED");
			
			String docName = doc.getName().split("\\.")[0];
			log.debug("docName: "+docName);
			doc.getAnnotations().removeAll((gate.AnnotationSet)doc.getAnnotations().get("GroundedPointMutation"));
						
			//MutationNormalizer mutationNormalizer = new MutationNormalizer("C:\\JARS\\MutationFinder\\");
			
			/**
			 * Initialize a table for protein mentions.
			 */
			// Lexicon index.
			Map<String,Integer> dictProperProteinName = new HashMap();
			
			
			Map<int[],List<String>> mentionTable = new HashMap<int[],List<String>>();
			Set<String> mentionSet = new HashSet<String>();
			
			List<gate.Annotation> proteinMentions = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("ProperProteinName"));
			for(gate.Annotation proteinMention : proteinMentions) {
				Long s = proteinMention.getStartNode().getOffset();
				Long e = proteinMention.getEndNode().getOffset();
				String value = doc.getContent().getContent(s, e).toString();
				String start = Long.toString(s);
				String end = Long.toString(e);
				
				//only consider mentions with length > 3
				if (value.length() > 3) {
					mentionTable.put(new int[] {Integer.parseInt(start),Integer.parseInt(end)}, (new ArrayList<String>(Arrays.asList((new String[]{value.toLowerCase()})))));
					mentionSet.add(value.toLowerCase());
				}
			}

			// debug.
			for(int[] positions : mentionTable.keySet()) {
				log.info(positions[0]+":"+positions[1]+":"+mentionTable.get(positions));
			}


			for(String m : mentionSet) {
				Set<String> queryResult = new HashSet<String>();
				String primaryAccQuery = "SELECT DISTINCT pac FROM fullname WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt = con.createStatement();
				ResultSet primaryAccRs = primaryAccSt.executeQuery(primaryAccQuery);
				while(primaryAccRs.next()) {
					queryResult.add(primaryAccRs.getString("pac"));
				}
				String primaryAccQuery2 = "SELECT DISTINCT pac FROM genename WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt2 = con.createStatement();
				ResultSet primaryAccRs2 = primaryAccSt2.executeQuery(primaryAccQuery2);
				while(primaryAccRs2.next()) {
					queryResult.add(primaryAccRs2.getString("pac"));
				}
				String primaryAccQuery3 = "SELECT DISTINCT pac FROM shortname WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt3 = con.createStatement();
				ResultSet primaryAccRs3 = primaryAccSt3.executeQuery(primaryAccQuery3);
				while(primaryAccRs3.next()) {
					queryResult.add(primaryAccRs3.getString("pac"));
				}
				//add them all to the proteinMentionTable
				List<String> queryResultAsList = new ArrayList<String>(queryResult);
				queryResultAsList.add(0, m);
				log.info("query result: "+queryResultAsList);
				for(int[] ia : mentionTable.keySet()) {
					if (mentionTable.get(ia).get(0).equals(m)) {
						mentionTable.put(ia, queryResultAsList);
					}
				}
			}
	        		
			
	        
	        
			// DEBUGGING.
			Set<String> removed = new TreeSet<String>();
			
			// DEBUGGING.
			Set<String> set = new TreeSet<String>();
			
			//log.debug("\n\nmentionTable populated with uniprot_ids from DB:");
			for(int[] positions : mentionTable.keySet()) {
				//log.debug(positions[0]+":"+positions[1]+":"+mentionTable.get(positions));
				set.addAll(mentionTable.get(positions));
			}

			log.debug("(1) All PACs: "+set);
			
			//System.err.println(docName);
			//System.err.println(stat.getMap());
			
			if(debugging){
				for(String swissProt : 	statistics.getMap().get(docName)){
					if(!set.contains(swissProt)){
						log.debug(swissProt+" not pool in set");
						statistics.notInPool.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_");	
						removed.add(swissProt);
					}else{
						log.debug(swissProt+" IS pool in set");
						statistics.inPool.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_");

					}
				}
			}
			
			
			
			
			
			
			
			/**
			 * Score the accession numbers depending on occurrence.
			 */
			Map<String,Integer> scoreboard = new HashMap<String, Integer>();
			for(int[] ia : mentionTable.keySet()) {
				boolean m = true;
				for(String pac : mentionTable.get(ia)) {
					if(!m) {
						if(scoreboard.get(pac) == null) {
							scoreboard.put(pac, 1);
						}
						else {
							scoreboard.put(pac, scoreboard.get(pac)+1);
						}
					}
					else {
						m = false;
					}
				}
			}
			
			Set<String> topPACs = new HashSet<String>();
			int numberOfMentionings = 0;
			for(String pac : scoreboard.keySet()) {
				if(scoreboard.get(pac)>numberOfMentionings) {
					topPACs.clear();
					topPACs.add(pac);
					numberOfMentionings = scoreboard.get(pac);
				}
				else if(scoreboard.get(pac)==numberOfMentionings) {
					topPACs.add(pac);
				}
			}
			Set<String> allPAs = topPACs;
			
			/*/ @AK for test (remove after real fix!!!!!!!!!!!!!!!!!!!)
			if(allPAs.contains("Q9GLJ8")){log.info("remove Q9GLJ8");
			    allPAs.remove("Q9GLJ8");
			}else log.info("no Q9GLJ8");
			*/
			
			//Set<String> allPAs = new HashSet<String>();allPAs.add("P32245");
			
			/*/ DEBUGGING.
			if(debugging){
				log.debug("\n\n(2) All TOP PACs: "+allPAs);
				for(String swissProt : stat
						.getMap()
						.get(docName)){
					if(removed.contains(swissProt))continue;
					if(!allPAs.contains(swissProt)){
						log.debug(swissProt+" not in top set");
						removed.add(swissProt);
					}else{
						log.debug(swissProt+" IS in top set");
					}
				}
			}*/
			
			
			/** 
			 * Filter with use of organism mentions
			*
			*/
			//retrieve all organism mentions
			Set<String> organisms = new HashSet<String>();
			
			List<gate.Annotation> organismMentions = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("OrganismName"));
			for(gate.Annotation organismMention : organismMentions) {
				Long s = organismMention.getStartNode().getOffset();
				Long e = organismMention.getEndNode().getOffset();
				organisms.add(doc.getContent().getContent(s, e).toString());
			}
			
			//transform all mentions into scientific names.
			Set<String> organismsSci = new HashSet<String>();
			for(String organism : organisms) {
				String organismSciQuery = "SELECT sciname FROM organismscieng WHERE engname = '"+organism+"'";
				Statement organismSciSt = con.createStatement();
				ResultSet organismSciRs = organismSciSt.executeQuery(organismSciQuery);
				if(organismSciRs.next()) {
					organismsSci.add(organismSciRs.getString(1));
				}
				else {
					organismsSci.add(organism);
				}
			}
			
			//create organism sub query
			String organismSubQuery = "(";
			for(String organismsci : organismsSci) {
				organismSubQuery += "name ='"+organismsci+"' OR ";
			}
			if(organismSubQuery.length() > 1) {
				organismSubQuery = organismSubQuery.substring(0, organismSubQuery.length()-4) + ")";
			}
			
			Set<String> tempAllPAs = new HashSet<String>();
			if (organismsSci.size() > 0) {
				for(String pac : allPAs) {
					String checkOrganismPACQuery = "SELECT * FROM organismname WHERE pac = '"+pac+"' AND "+organismSubQuery;
					//log.debug("checkOrganismPACQuery: "+checkOrganismPACQuery);
					Statement checkOrganismPACSt = con.createStatement();
					ResultSet checkOrganismPACRs = checkOrganismPACSt.executeQuery(checkOrganismPACQuery);
					if(checkOrganismPACRs.next()) {
						tempAllPAs.add(pac);
					}
				}
			}
			//if the filtering ended up with accession numbers left, accept the changes.
			///log.info("\n\nallPAs: "+allPAs);
			///log.info("\n\ntempAllPAs: "+tempAllPAs);
			
			if(tempAllPAs.size() != 0) {
				allPAs = tempAllPAs;
			}
					
			
			// DEBUGGING.
			if(debugging){
				log.debug("\n\n(3) All PACs after organism filter: "+allPAs);
				for(String swissProt : statistics.getMap().get(docName)){
					if(removed.contains(swissProt))continue;
					if(!allPAs.contains(swissProt)){
						log.debug(swissProt+" not for grounding in set");
						statistics.notInForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_");
						removed.add(swissProt);
					}else{
						log.debug(swissProt+" IS for grounding in set");
						statistics.inForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_");
					}
				}
			}
			
			
			
					
			/* 
			 * End of organism filter.
			*/
			
			
			
			
			
			/**
			 * Retrieve all mutation mentions.
			 */
			List<gate.Annotation> mutationAnnotations = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("Mutation"));
			int numberOfMutationMentions = mutationAnnotations.size();
			log.debug("number of mutation mentions: "+numberOfMutationMentions);
			String[][] mutationTable = new String[numberOfMutationMentions][6];
			Set<String> mutationMentions = new HashSet<String>();
			int mutationIndex = 0;
			
			Set<Mutation> normalizedMutationSet = new HashSet<Mutation>();
			
			for(gate.Annotation mutationAnnotation : mutationAnnotations) {
				Long s = mutationAnnotation.getStartNode().getOffset();
				Long e = mutationAnnotation.getEndNode().getOffset();
				
				mutationTable[mutationIndex][0] = Long.toString(s);
				mutationTable[mutationIndex][1] = Long.toString(e);
				mutationTable[mutationIndex][2] = doc.getContent().getContent(s, e).toString();

				String normalizedMutationString = (String)mutationAnnotation.getFeatures().get("wNm");			
				mutationTable[mutationIndex][3] = normalizedMutationString;
				
				//extract the different parts and add to a set of normalized mutations.
				String wildtype = normalizedMutationString.substring(0,1);
				String mutant = normalizedMutationString.substring(normalizedMutationString.length()-1);
				int position = Integer.parseInt(normalizedMutationString.substring(1,normalizedMutationString.length()-1));
				normalizedMutationSet.add(new Mutation(wildtype, position, mutant));
				
				mutationMentions.add(doc.getContent().getContent(s, e).toString());
				mutationIndex++;
			}
			
						
			//make a list of the set
			List<Mutation> normalizedMutations = new ArrayList<Mutation>(normalizedMutationSet);
			
			//sort the mutations, can't really remember why anymore.
			Collections.sort(normalizedMutations);
			
			//Make sure mutations with the same WT residue and the same position are treated as one single mutation.
			//When the grounding and the filtering is done, they are again treated as different mutations
			Map<Mutation,List<Mutation>> sameWTMap = new HashMap<Mutation, List<Mutation>>();
			List<Integer> listedPositions = new ArrayList<Integer>();
			for(int i = 0; i < normalizedMutations.size(); i++) {
				if(!listedPositions.contains(i)) {
					List<Mutation> mList = new ArrayList<Mutation>();
					for(int j = i+1; j < normalizedMutations.size(); j++) {
						//two mutations are considered equal if they have the same WT residue and the same position
						if(normalizedMutations.get(i).equals(normalizedMutations.get(j))) {
							mList.add(new Mutation(normalizedMutations.get(j).wildtype, normalizedMutations.get(j).position, normalizedMutations.get(j).mutation));
							listedPositions.add(j);
						}
					}
					if (mList.size() > 0) {
						sameWTMap.put(normalizedMutations.get(i), mList);
					}
				}
			}

			// @AK.Debug
			//for(Mutation mutation : normalizedMutations){
			    //log.info("\n\nnormalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
			 //}

			//remove "dublicates"
			for(int i = normalizedMutations.size()-1; i > -1; i--) {
				if(listedPositions.contains(i)) {
					normalizedMutations.remove(i);
				}
			}

			// @AK.Debug
			for(Mutation mutation : normalizedMutations){
			    log.debug("normalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
			 }

			
			
			//Initialize a List<PAMOMap> 
			List<MutationInfo> mutationInfoList = new ArrayList<MutationInfo>();
				//for each PA in allPAs
			for(String PA : allPAs) {
				log.info("\n\nGROUNDING ALGORITHM for : "+ PA);
				MutationInfo mi;
				
				/* GROUNDING ALGORITHM START
				*/
				//private MutationInfo ground(List<Mutation> normalizedMutations, String PA,Connection con) throws IOException, SQLException {
				try {
					//retrieve the amino acid sequence
					String sequence = PASequenceMapping.get(PA);
					if(sequence == null) {
						String sequenceQuery = "SELECT sequence FROM sequence WHERE pac='"+PA+"'";
						Statement sequenceSt = con.createStatement();
						ResultSet sequenceRs = sequenceSt.executeQuery(sequenceQuery);
						if(sequenceRs.next()) {
							sequence = sequenceRs.getString("sequence");
						}
						else {
							mi = null;
						}
						PASequenceMapping.put(PA, sequence);
						log.info("PA: "+PA + " sequence: "+sequence);
					}
					
					//discard mutations that can never fit the sequence (position>sequence length +50)
					for(int i = normalizedMutations.size()-1; i > -1 ; i--) {
						if(normalizedMutations.get(i).position > sequence.length() + 50) {
							normalizedMutations.remove(i);
						}
					}

					// @AK.Debug
					for(Mutation mutation : normalizedMutations){
					    ///log.info("normalizedMutations3.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
					}

					List<Mutation> G = new ArrayList<Mutation>();
					boolean groundingFound = false;
					String regex = "";
					Pattern pattern;
					Matcher matcher;
					
					List<MutationInfo> GList = new ArrayList<MutationInfo>();
					//first, look for smallest possible groundings (2 point mutations)
					for(int i = 0; i < normalizedMutations.size(); i++) {
						for(int j = i+1; j < normalizedMutations.size(); j++) {
							regex = normalizedMutations.get(i).wildtype;
							for(int k = 0; k < (normalizedMutations.get(j).position - normalizedMutations.get(i).position -1); k++) {
								regex += ".";
							}
							regex += normalizedMutations.get(j).wildtype;
							
							pattern = null;
							pattern = Pattern.compile(regex);
							matcher = pattern.matcher(sequence);
							
							//if the smallest possible is found, see if it can be made larger
							while(matcher.find()) {
								groundingFound = true;
								G.add(normalizedMutations.get(i));
								G.add(normalizedMutations.get(j));
								
								int firstMutationsPosition = G.get(0).position;
								int offset = firstMutationsPosition - (matcher.start()+1);
								
								//add all other mutations that fit.
								for(int k = j + 1; k < normalizedMutations.size(); k++) {
									int positionToLookAt = normalizedMutations.get(k).position-(offset+1);
									if(sequence.length() > positionToLookAt) {
										if (normalizedMutations.get(k).wildtype.equals(Character.toString(sequence.charAt(positionToLookAt)))) {
											G.add(normalizedMutations.get(k));
										}	
									}
								}
								GList.add(new MutationInfo(PA,new LinkedList<Mutation>(G),offset));
								G.clear();
							}
						}
					}
					
					Collections.sort(GList);
					int listSize = GList.size();

					 // @AK debug
					log.info("GList.size(): "+GList.size());

					for(MutationInfo mi2 : GList){
					  ///log.info("GList.mi.PA: "+mi2.PA);
					  ///log.info("GList.mi.Offset: "+mi2.Offset);
					  for(Mutation mutation2 : mi2.Mutations){
					    ///log.info("GList.mi.mutation: "+mutation2.wildtype+mutation2.position+mutation2.mutation);
					  }
					}

					for(int i = listSize - 1 ; i > -1 ; i--) {
						boolean remove = false;
						boolean removej = false;
						int j2 = listSize;
						for(int j = i - 1 ; j > -1 ; j-- ) {
							//get the intersection of the two mutation lists
							Set<Mutation> intersection = new HashSet<Mutation>(GList.get(i).Mutations);
							intersection.retainAll(GList.get(j).Mutations);
							if(intersection.size() > 0) {
								if(GList.get(j).Mutations.size() > GList.get(i).Mutations.size()) {
									remove = true;
									break;
								}
								else {
									if(Math.abs(GList.get(j).Offset) < Math.abs(GList.get(i).Offset)) {
										remove = true;
										break;
									}
									else {
										remove = true;
										removej = true;
										j2 = j;
										break;
									}
								}
							}
						}
						if(remove) {
							if(removej) {
								GList.remove(j2);
							}
							else {
								GList.remove(i);
							}
						}
					}

					 /// @AK debug
					log.info("GList2.size(): "+GList.size());
					/*for(MutationInfo mi : GList){
					  log.info("GList2.mi.PA: "+mi.PA);
					  log.info("GList2.mi.Offset: "+mi.Offset);
					  for(Mutation mutation : mi.Mutations){
					    log.info("GList2.mi.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
					  }
					}*/

					//if there still are more than one remaining, just choose the top one for crying out loud..
					if (GList.size()>0) {
						mi = GList.get(0);
					}
					//if there are no matches, create an empty list with mutations (inside a mutationInfo object)
					else {
						mi = new MutationInfo(PA,new LinkedList<Mutation>(),189819);
					}
				}	catch(StackOverflowError e) {
						log.info("KLEPPP!");
						log.info("PA:"+PA);
						for(Mutation m : normalizedMutations) {
							log.info(m.wildtype+m.position+m.mutation);
						}
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						mi = null;
				}

				/* GROUNDING ALGORITHM END
				*/
				

				if(mi!=null) {
					mutationInfoList.add(mi);
					//log.info("grounding against "+PA+" is done.");
				}
				else {
					log.info("grounding against "+PA+" failed due to missing sequence (or perhaps something else, who knows?)");
				}
			}
			Collections.sort(mutationInfoList);

			log.info("mutationInfoList.size(): "+mutationInfoList.size());
			for(MutationInfo mi : mutationInfoList){
			  ///log.info("mi.PA: "+mi.PA);
			  ///log.info("mi.Offset: "+mi.Offset);
			  for(Mutation mutation : mi.Mutations){
			    ///log.info("mi.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
			  }
			}

			//filter
			for(int i = mutationInfoList.size()-1; i > -1; i--) {
				if(mutationInfoList.get(i).Mutations.size() == 0) {
					mutationInfoList.remove(i);
				}
				else {
					boolean remove = false;
					boolean removej = false;
					int outerJ = 0;
					for (int j = i-1; j > -1; j--) {
						List<Mutation> intersect = new ArrayList<Mutation>(mutationInfoList.get(j).Mutations);
						intersect.retainAll(mutationInfoList.get(i).Mutations);
						if(intersect.size() > 0) {
							if (mutationInfoList.get(j).Mutations.size() == mutationInfoList.get(i).Mutations.size()) {
								if(Math.abs(mutationInfoList.get(j).Offset) <= Math.abs(mutationInfoList.get(i).Offset)) {
									remove = true;
								}
								else {
									remove = true;
									removej = true;
									outerJ = j;
								}
							}
							else {
								remove = true;
							}
						}
					}
					if(remove) {
						if(removej) {
							mutationInfoList.remove(outerJ);
						}
						else {
							mutationInfoList.remove(i);
						}
					}
				}
			}
			//when filtering and extension is done, add the mutations previously removed, if their
			//wildtype residue and position is equal to any of the remaining mutations.
			for(MutationInfo mi : mutationInfoList) {
				for(Mutation m : mi.Mutations) {
					List<String> nm = new ArrayList<String>();
					nm.add(m.wildtype+Integer.toString(m.position)+m.mutation);
					try { 
						for(Mutation sameWT : sameWTMap.get(m)) {
							nm.add(sameWT.wildtype+Integer.toString(sameWT.position)+sameWT.mutation);
						}
					} catch(NullPointerException e) {
						
					}
					for(int i = 0; i < numberOfMutationMentions; i++) {
						if(nm.contains(mutationTable[i][3])) {
							// TEMPORARY
							//mutationTable[i][4] = mi.PA;
							mutationTable[i][4] = mi.PA+"_"+mi.Offset;

							mutationTable[i][5] = Integer.toString(mi.Offset);
						}
					}
				}
			}
			
			
			Set<String> chosenPacs = new HashSet<String>();

			
			/* Add PAC:s as features to protein and mutation mentions
			 */
			//log.info("Pmid\tStart\tEnd\tValue\t\t\tNormal\tGround\tOffset");
			for(int i = 0; i < numberOfMutationMentions; i++) {
				//log.info(doc.getName()+"\t"+mutationTable[i][0]+"\t"+mutationTable[i][1]+"\t"+mutationTable[i][2]+"\t"+mutationTable[i][3]+"\t\t\t"+mutationTable[i][4]+"\t"+mutationTable[i][5]);
				
				if(mutationTable[i][4] != null) {
					AnnotationSet as = doc.getAnnotations().get(Long.parseLong(mutationTable[i][0]),Long.parseLong(mutationTable[i][1]));
					Annotation a = (gate.Annotation)as.get("Mutation").iterator().next();
					FeatureMap features = Factory.newFeatureMap();  
					 
					features.put("hasMentionedPosition",Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1)));
					features.put("hasCorrectPosition", Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-Integer.parseInt(mutationTable[i][5]));
					List<String[]> pacsAndOffsets = new ArrayList<String[]>();
					String[] s = new String[2];
					s[0] = mutationTable[i][4];
					s[1] = mutationTable[i][5];
					pacsAndOffsets.add(s);
					features.put("isGroundedTo", mutationTable[i][4]);//pacsAndOffsets);
					features.put("hasWildtypeResidue", mutationTable[i][3].substring(0,1));
					features.put("hasMutantResidue", Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1)));
					// outputAS					
					doc.getAnnotations().add(Long.parseLong(mutationTable[i][0]), Long.parseLong(mutationTable[i][1]), "GroundedPointMutation",features);
			
					
					String mutStr = mutationTable[i][3].substring(0,1)+Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))+Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1));
					//System.out.println(mutStr+" is grounded to "+mutationTable[i][4]);
					
					
					//export result to db for future evaluation
					/**
					Statement insertSt = con.createStatement();
						try {
							insertSt.executeUpdate("INSERT INTO groundedmutations (pmid,start,end,normal,pa,offset) VALUES('"+doc.getName()+"',"+mutationTable[i][0]+","+mutationTable[i][1]+",'"+mutationTable[i][3]+"','"+mutationTable[i][4]+"',"+mutationTable[i][5]+")");
						} catch(MySQLIntegrityConstraintViolationException err) {
							
						}
					/**/
					//remember the accession numbers for later use (the protein annotation step).
					chosenPacs.add(mutationTable[i][4]);
				}
			}
			
			for(int[] ia : mentionTable.keySet()) {
				for(String ac : chosenPacs) {
					if (mentionTable.get(ia).contains(ac)) {
						AnnotationSet as = doc.getAnnotations().get(Long.parseLong(Integer.toString(ia[0])),Long.parseLong(Integer.toString(ia[1])));
						Annotation a = (gate.Annotation)as.get("ProteinName").iterator().next();
						FeatureMap features = Factory.newFeatureMap();
						features.put("mentionsProtein",ac);
						// ouputAS
						doc.getAnnotations().add(Long.parseLong(Integer.toString(ia[0])),Long.parseLong(Integer.toString(ia[1])), "ProteinMention", features);
					}
				}
			}
			
			//add chosenPacs as a feature to the document
			String pacFeature = new String();
			int nop = 0;
			for(String ac : chosenPacs) {
				if(nop == 0) {
					pacFeature=ac;
				} else {
					pacFeature+=","+ac;
				}
				nop++;
			}
			doc.getFeatures().put("pacs",pacFeature);
			doc.getFeatures().put(pacFeature,PASequenceMapping.get(pacFeature));

			//inputAS.removeAll(inputAS.get("MutationTemp"));
			//inputAS.removeAll(inputAS.get("ProteinName"));
			//inputAS.removeAll(inputAS.get("ProperProteinName"));
			//inputAS.removeAll(inputAS.get("Lookup"));

			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public void groundOld2(Document doc) throws InvalidOffsetException{
				
		
		try {	
			//System.out.print("Ground mutation mentions...");
			
			//Properties pro = new Properties();
            //pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
            //String DATABASE_URL = pro.getProperty("DATABASE_URL");

			
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/swissprot";
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/uniprot";
			String DATABASE_URL = "jdbc:mysql://localhost:3306/uniprot";
			String DATABASE2_URL = "jdbc:mysql://localhost:3306/trembl2";
			//String DATABASE2_URL = "jdbc:mysql://sadi2:3306/trembl2";

			//String USERNAME = "swissprot";
			//String PASSWORD = "password";
			String USERNAME = "root"; 
			String PASSWORD = "deduction";

			Class.forName("com.mysql.jdbc.Driver");		
			con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			con2 = DriverManager.getConnection(DATABASE2_URL, USERNAME, PASSWORD);
			//System.err.println("CONN ESTABLISHED");
			
			
			//Statement initStatement = con.createStatement();
			//initStatement.execute("GRANT ALL ON trembl2 TO swissprot@'localhost' IDENTIFIED BY 'password'");
			
			//initStatement.execute("SET GLOBAL query_cache_size = 0;");
			//initStatement.execute("RESET QUERY CACHE;");
			//initStatement.execute("FLUSH QUERY CACHE;");
			//initStatement.execute("SHOW VARIABLES LIKE 'query_cache_size';");
			

			
			String docName = doc.getName().split("\\.")[0];
			log.debug("docName: "+docName);
			doc.getAnnotations().removeAll((gate.AnnotationSet)doc.getAnnotations().get("GroundedPointMutation"));
						
			//MutationNormalizer mutationNormalizer = new MutationNormalizer("C:\\JARS\\MutationFinder\\");
			
			/**
			 * Initialize a table for protein mentions.
			 */
			// Lexicon index.
			Map<String,Integer> dictProperProteinName = new HashMap();
			
			
			Map<int[],List<String>> mentionTable = new HashMap<int[],List<String>>();
			Set<String> mentionSet = new HashSet<String>();
			
			//List<gate.Annotation> proteinMentions = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("ProperProteinName"));
			/*for(gate.Annotation proteinMention : proteinMentions) {
				Long s = proteinMention.getStartNode().getOffset();
				Long e = proteinMention.getEndNode().getOffset();
				String value = doc.getContent().getContent(s, e).toString();
				String start = Long.toString(s);
				String end = Long.toString(e);
				
				//only consider mentions with length > 3
				if (value.length() > 3 && value.matches(".*[A-Za-z]+.*") && !proteinNameExceptionList.contains(value)) {
				//if (value.length() > 2 && value.matches(".*[A-Za-z]+.*") && !exceptionList.contains(value)) {

					mentionTable.put(new int[] {Integer.parseInt(start),Integer.parseInt(end)}, (new ArrayList<String>(Arrays.asList((new String[]{value.toLowerCase()})))));
					mentionSet.add(value.toLowerCase());
				}
			}*/

			// debug.
			for(int[] positions : mentionTable.keySet()) {
				//log.info(positions[0]+":"+positions[1]+":"+mentionTable.get(positions));
			}

			long queryTime = System.currentTimeMillis();
			for(String m : mentionSet) {
				
				Set<String> queryResult = new HashSet<String>();
				
				m=m.replaceAll("'", "");
				
				
/*
				String primaryAccQuery = "select distinct pac from proteinname where match(name) against ('" + m +
					"') and name like '" + m + "%'";
				Statement primaryAccSt = con.createStatement();
				ResultSet primaryAccRs = primaryAccSt.executeQuery(primaryAccQuery);
				while(primaryAccRs.next()) {
					queryResult.add(primaryAccRs.getString("pac"));
				}*/
				
				
				String primaryAccQuery = "SELECT pac FROM fullname WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt = con.createStatement();
				ResultSet primaryAccRs = primaryAccSt.executeQuery(primaryAccQuery);
				while(primaryAccRs.next()) {
					queryResult.add(primaryAccRs.getString("pac"));
				}
				
				
				String primaryAccQuery2 = "SELECT DISTINCT pac FROM genename WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt2 = con.createStatement();
				ResultSet primaryAccRs2 = primaryAccSt2.executeQuery(primaryAccQuery2);
				while(primaryAccRs2.next()) {
					queryResult.add(primaryAccRs2.getString("pac"));
				}
				String primaryAccQuery3 = "SELECT DISTINCT pac FROM shortname WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt3 = con.createStatement();
				ResultSet primaryAccRs3 = primaryAccSt3.executeQuery(primaryAccQuery3);
				while(primaryAccRs3.next()) {
					queryResult.add(primaryAccRs3.getString("pac"));
				}
				
				
				String primaryAccQuery4 = "SELECT pac FROM fullname2 WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt4 = con2.createStatement();
				ResultSet primaryAccRs4 = primaryAccSt4.executeQuery(primaryAccQuery4);
				while(primaryAccRs4.next()) {
					queryResult.add(primaryAccRs4.getString("pac"));
				}
				
				
				String primaryAccQuery5 = "SELECT DISTINCT pac FROM genename WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt5 = con2.createStatement();
				ResultSet primaryAccRs5 = primaryAccSt5.executeQuery(primaryAccQuery5);
				while(primaryAccRs5.next()) {
					queryResult.add(primaryAccRs5.getString("pac"));
				}
				String primaryAccQuery6 = "SELECT DISTINCT pac FROM shortname WHERE name LIKE '"+m+"%'";
				Statement primaryAccSt6 = con2.createStatement();
				ResultSet primaryAccRs6 = primaryAccSt6.executeQuery(primaryAccQuery6);
				while(primaryAccRs6.next()) {
					queryResult.add(primaryAccRs6.getString("pac"));
				}
				
				
				//add them all to the proteinMentionTable
				List<String> queryResultAsList = new ArrayList<String>(queryResult);
				queryResultAsList.add(0, m);
				//log.info("query result: "+queryResultAsList);
				log.info("query result: "+queryResultAsList.get(0));
				for(int[] ia : mentionTable.keySet()) {
					if (mentionTable.get(ia).get(0).equals(m)) {
						mentionTable.put(ia, queryResultAsList);
					}
				}
			}
			corpusQueryTime = corpusQueryTime + (System.currentTimeMillis() - queryTime);
	
			
	        
	        
			// DEBUGGING.
			Set<String> removed = new TreeSet<String>();
			
			// DEBUGGING.
			Set<String> set = new TreeSet<String>();
			
			//log.debug("\n\nmentionTable populated with uniprot_ids from DB:");
			for(int[] positions : mentionTable.keySet()) {
				//log.debug(positions[0]+":"+positions[1]+":"+mentionTable.get(positions));
				set.addAll(mentionTable.get(positions));
			}

			//log.debug("(1) All PACs: "+set);
			log.debug("(1) Size of initial pool: "+set.size());
			//System.err.println(docName);
			//System.err.println(stat.getMap());
			
			//Main.notInPool.add(doc.getName()+"_");
			
			if(debugging){
				for(String swissProt : 	statistics.getMap().get(docName)){
					if(!set.contains(swissProt)){
						log.debug(swissProt+" not in set");
						statistics.notInForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_"+mentionSet);
						removed.add(swissProt);
					}else{	
						log.debug(swissProt+" IS in set");						
					}
				}
			}
			
			
			
			
			
			
			
			/**
			 * Score the accession numbers depending on occurrence.
			 **/
			Map<String,Integer> scoreboard = new HashMap<String, Integer>();
			for(int[] ia : mentionTable.keySet()) {
				boolean m = true;
				for(String pac : mentionTable.get(ia)) {
					if(!m) {
						if(scoreboard.get(pac) == null) {
							scoreboard.put(pac, 1);
						}
						else {
							scoreboard.put(pac, scoreboard.get(pac)+1);
						}
					}
					else {
						m = false;
					}
				}
			}
			
			Set<String> topPACs = new HashSet<String>();
			int numberOfMentionings = 0;
			for(String pac : scoreboard.keySet()) {
				if(scoreboard.get(pac)>numberOfMentionings) {
					topPACs.clear();
					topPACs.add(pac);
					numberOfMentionings = scoreboard.get(pac);
				}
				else if(scoreboard.get(pac)==numberOfMentionings) {
					topPACs.add(pac);
				}
			}
			Set<String> allPAs = topPACs;
			
			
			
			/*/ @AK for test (remove after real fix!!!!!!!!!!!!!!!!!!!)
			if(allPAs.contains("Q9GLJ8")){log.info("remove Q9GLJ8");
			    allPAs.remove("Q9GLJ8");
			}else log.info("no Q9GLJ8");
			*/
			
			//Set<String> allPAs = new HashSet<String>();allPAs.add("P32245");
			
			// DEBUGGING.
			if(debugging){
				//log.debug("\n\n(2) All TOP PACs: "+allPAs);
				for(String swissProt : statistics
						.getMap()
						.get(docName)){
					if(removed.contains(swissProt))continue;
					if(!allPAs.contains(swissProt)){
						log.debug(swissProt+" not in top set");
						statistics.notInAfterGrounding.add(doc.getName()+"_"+swissProt);
						removed.add(swissProt);
					}else{
						log.debug(swissProt+" IS in top set");
					}
				}
			}
			
			
			/** 
			 * Filter with use of organism mentions
			*
			*/
			//retrieve all organism mentions
			Set<String> organisms = new HashSet<String>();
			
			List<gate.Annotation> organismMentions = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("OrganismName"));
			for(gate.Annotation organismMention : organismMentions) {
				Long s = organismMention.getStartNode().getOffset();
				Long e = organismMention.getEndNode().getOffset();
				organisms.add(doc.getContent().getContent(s, e).toString());
			}
			
			//transform all mentions into scientific names.
			Set<String> organismsSci = new HashSet<String>();
			for(String organism : organisms) {
				String organismSciQuery = "SELECT sciname FROM organismscieng WHERE engname = '"+organism+"'";
				
				log.debug("organismSciQuery: "+organismSciQuery);
				
				Statement organismSciSt = con.createStatement();
				ResultSet organismSciRs = organismSciSt.executeQuery(organismSciQuery);
				if(organismSciRs.next()) {
					organismsSci.add(organismSciRs.getString(1));
				}
				else {
					organismsSci.add(organism);
				}
				/*
				Statement organismSciSt2 = con2.createStatement();
				ResultSet organismSciRs2 = organismSciSt2.executeQuery(organismSciQuery);
				if(organismSciRs2.next()) {
					organismsSci.add(organismSciRs2.getString(1));
				}
				else {
					organismsSci.add(organism);
				}
				*/
			}
			
			//create organism sub query
			String organismSubQuery = "(";
			for(String organismsci : organismsSci) {
				organismSubQuery += "name ='"+organismsci+"' OR ";
			}
			if(organismSubQuery.length() > 1) {
				organismSubQuery = organismSubQuery.substring(0, organismSubQuery.length()-4) + ")";
			}
			
			Set<String> tempAllPAs = new HashSet<String>();
			if (organismsSci.size() > 0) {
				for(String pac : allPAs) {
					String checkOrganismPACQuery = "SELECT * FROM organismname WHERE pac = '"+pac+"' AND "+organismSubQuery;
					
					log.debug("checkOrganismPACQuery: "+checkOrganismPACQuery);
					Statement checkOrganismPACSt = con.createStatement();
					ResultSet checkOrganismPACRs = checkOrganismPACSt.executeQuery(checkOrganismPACQuery);
					if(checkOrganismPACRs.next()) {
						tempAllPAs.add(pac);
					}
					
					/*
					Statement checkOrganismPACSt2 = con2.createStatement();
					ResultSet checkOrganismPACRs2 = checkOrganismPACSt2.executeQuery(checkOrganismPACQuery);
					if(checkOrganismPACRs2.next()) {
						tempAllPAs.add(pac);
					}
					*/
				}
			}
			//if the filtering ended up with accession numbers left, accept the changes.
			///log.info("\n\nallPAs: "+allPAs);
			///log.info("\n\ntempAllPAs: "+tempAllPAs);
			
			if(tempAllPAs.size() != 0) {
				allPAs = tempAllPAs;
			}
					
			
			// DEBUGGING.
			if(debugging){
				log.debug("\n\n(3) All PACs after organism filter: "+allPAs);
				for(String swissProt : statistics.getMap().get(docName)){
					if(removed.contains(swissProt))continue;
					if(!allPAs.contains(swissProt)){
						log.debug(swissProt+" not in filtered set");
						statistics.inAfterGrounding.add(doc.getName()+"_"+swissProt);
						removed.add(swissProt);
					}else{
						log.debug(swissProt+" IS in filtered set");
						statistics.inFilteredSet.add(doc.getName()+"_"+swissProt);
					}
				}
			}
			
			
			
					
			/* 
			 * End of organism filter.
			*/
			
			
			
			
			
			/**
			 * Retrieve all mutation mentions.
			 */
			List<gate.Annotation> mutationAnnotations = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("Mutation"));
			int numberOfMutationMentions = mutationAnnotations.size();
			log.debug("number of mutation mentions: "+numberOfMutationMentions);
			String[][] mutationTable = new String[numberOfMutationMentions][6];
			Set<String> mutationMentions = new HashSet<String>();
			int mutationIndex = 0;
			
			Set<Mutation> normalizedMutationSet = new HashSet<Mutation>();
			
			for(gate.Annotation mutationAnnotation : mutationAnnotations) {
				Long s = mutationAnnotation.getStartNode().getOffset();
				Long e = mutationAnnotation.getEndNode().getOffset();
				
				mutationTable[mutationIndex][0] = Long.toString(s);
				mutationTable[mutationIndex][1] = Long.toString(e);
				mutationTable[mutationIndex][2] = doc.getContent().getContent(s, e).toString();

				String normalizedMutationString = (String)mutationAnnotation.getFeatures().get("wNm");			
				mutationTable[mutationIndex][3] = normalizedMutationString;
				
				//extract the different parts and add to a set of normalized mutations.
				String wildtype = normalizedMutationString.substring(0,1);
				String mutant = normalizedMutationString.substring(normalizedMutationString.length()-1);
				int position = Integer.parseInt(normalizedMutationString.substring(1,normalizedMutationString.length()-1));
				normalizedMutationSet.add(new Mutation(wildtype, position, mutant));
				
				mutationMentions.add(doc.getContent().getContent(s, e).toString());
				mutationIndex++;
			}
			
						
			//make a list of the set
			List<Mutation> normalizedMutations = new ArrayList<Mutation>(normalizedMutationSet);
			
			//sort the mutations, can't really remember why anymore.
			Collections.sort(normalizedMutations);
			
			//Make sure mutations with the same WT residue and the same position are treated as one single mutation.
			//When the grounding and the filtering is done, they are again treated as different mutations
			Map<Mutation,List<Mutation>> sameWTMap = new HashMap<Mutation, List<Mutation>>();
			List<Integer> listedPositions = new ArrayList<Integer>();
			for(int i = 0; i < normalizedMutations.size(); i++) {
				if(!listedPositions.contains(i)) {
					List<Mutation> mList = new ArrayList<Mutation>();
					for(int j = i+1; j < normalizedMutations.size(); j++) {
						//two mutations are considered equal if they have the same WT residue and the same position
						if(normalizedMutations.get(i).equals(normalizedMutations.get(j))) {
							mList.add(new Mutation(normalizedMutations.get(j).wildtype, normalizedMutations.get(j).position, normalizedMutations.get(j).mutation));
							listedPositions.add(j);
						}
					}
					if (mList.size() > 0) {
						sameWTMap.put(normalizedMutations.get(i), mList);
					}
				}
			}

			// @AK.Debug
			//for(Mutation mutation : normalizedMutations){
			    //log.info("\n\nnormalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
			 //}

			//remove "dublicates"
			for(int i = normalizedMutations.size()-1; i > -1; i--) {
				if(listedPositions.contains(i)) {
					normalizedMutations.remove(i);
				}
			}

			// @AK.Debug
			for(Mutation mutation : normalizedMutations){
			    log.debug("normalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
			 }

			
			
			//Initialize a List<PAMOMap> 
			List<MutationInfo> mutationInfoList = new ArrayList<MutationInfo>();
				//for each PA in allPAs
			for(String PA : allPAs) {
				log.info("\n\nGROUNDING ALGORITHM for : "+ PA);
				MutationInfo mi;
				
				/**
				 *  GROUNDING ALGORITHM START
				 */
				//private MutationInfo ground(List<Mutation> normalizedMutations, String PA,Connection con) throws IOException, SQLException {
				try {
					//retrieve the amino acid sequence
					String sequence = PASequenceMapping.get(PA);
					if(sequence == null) {
						String sequenceQuery = "SELECT sequence FROM sequence WHERE pac='"+PA+"'";
						Statement sequenceSt = con.createStatement();
						ResultSet sequenceRs = sequenceSt.executeQuery(sequenceQuery);
						if(sequenceRs.next()) {
							sequence = sequenceRs.getString("sequence");
						}
						else {
							mi = null;
						}
						PASequenceMapping.put(PA, sequence);
						log.info("PA: "+PA + " sequence: "+sequence);
					}
					
					//discard mutations that can never fit the sequence (position>sequence length +50)
					for(int i = normalizedMutations.size()-1; i > -1 ; i--) {
						if(normalizedMutations.get(i).position > sequence.length() + 50) {
							normalizedMutations.remove(i);
						}
					}

					// @AK.Debug
					for(Mutation mutation : normalizedMutations){
					    ///log.info("normalizedMutations3.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
					}

					List<Mutation> G = new ArrayList<Mutation>();
					boolean groundingFound = false;
					String regex = "";
					Pattern pattern;
					Matcher matcher;
					
					List<MutationInfo> GList = new ArrayList<MutationInfo>();
					//first, look for smallest possible groundings (2 point mutations)
					for(int i = 0; i < normalizedMutations.size(); i++) {
						for(int j = i+1; j < normalizedMutations.size(); j++) {
							regex = normalizedMutations.get(i).wildtype;
							for(int k = 0; k < (normalizedMutations.get(j).position - normalizedMutations.get(i).position -1); k++) {
								regex += ".";
							}
							regex += normalizedMutations.get(j).wildtype;
							
							pattern = null;
							pattern = Pattern.compile(regex);
							matcher = pattern.matcher(sequence);
							
							//if the smallest possible is found, see if it can be made larger
							while(matcher.find()) {
								groundingFound = true;
								G.add(normalizedMutations.get(i));
								G.add(normalizedMutations.get(j));
								
								int firstMutationsPosition = G.get(0).position;
								int offset = firstMutationsPosition - (matcher.start()+1);
								
								//add all other mutations that fit.
								for(int k = j + 1; k < normalizedMutations.size(); k++) {
									int positionToLookAt = normalizedMutations.get(k).position-(offset+1);
									if(sequence.length() > positionToLookAt) {
										if (normalizedMutations.get(k).wildtype.equals(Character.toString(sequence.charAt(positionToLookAt)))) {
											G.add(normalizedMutations.get(k));
										}	
									}
								}
								GList.add(new MutationInfo(PA,new LinkedList<Mutation>(G),offset));
								G.clear();
							}
						}
					}
					
					Collections.sort(GList);
					int listSize = GList.size();

					 // @AK debug
					log.info("GList.size(): "+GList.size());

					for(MutationInfo mi2 : GList){
					  ///log.info("GList.mi.PA: "+mi2.PA);
					  ///log.info("GList.mi.Offset: "+mi2.Offset);
					  for(Mutation mutation2 : mi2.Mutations){
					    ///log.info("GList.mi.mutation: "+mutation2.wildtype+mutation2.position+mutation2.mutation);
					  }
					}

					for(int i = listSize - 1 ; i > -1 ; i--) {
						boolean remove = false;
						boolean removej = false;
						int j2 = listSize;
						for(int j = i - 1 ; j > -1 ; j-- ) {
							//get the intersection of the two mutation lists
							Set<Mutation> intersection = new HashSet<Mutation>(GList.get(i).Mutations);
							intersection.retainAll(GList.get(j).Mutations);
							if(intersection.size() > 0) {
								if(GList.get(j).Mutations.size() > GList.get(i).Mutations.size()) {
									remove = true;
									break;
								}
								else {
									if(Math.abs(GList.get(j).Offset) < Math.abs(GList.get(i).Offset)) {
										remove = true;
										break;
									}
									else {
										remove = true;
										removej = true;
										j2 = j;
										break;
									}
								}
							}
						}
						if(remove) {
							if(removej) {
								GList.remove(j2);
							}
							else {
								GList.remove(i);
							}
						}
					}

					 /*// @AK debug
					log.info("GList2.size(): "+GList.size());
					for(MutationInfo mi : GList){
					  log.info("GList2.mi.PA: "+mi.PA);
					  log.info("GList2.mi.Offset: "+mi.Offset);
					  for(Mutation mutation : mi.Mutations){
					    log.info("GList2.mi.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
					  }
					}*/

					//if there still are more than one remaining, just choose the top one for crying out loud..
					if (GList.size()>0) {
						mi = GList.get(0);
					}
					//if there are no matches, create an empty list with mutations (inside a mutationInfo object)
					else {
						mi = new MutationInfo(PA,new LinkedList<Mutation>(),189819);
					}
				}	catch(StackOverflowError e) {
						log.info("KLEPPP!");
						log.info("PA:"+PA);
						for(Mutation m : normalizedMutations) {
							log.info(m.wildtype+m.position+m.mutation);
						}
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						mi = null;
				}

				/**
				 *  GROUNDING ALGORITHM END
				 */
				

				if(mi!=null) {
					mutationInfoList.add(mi);
					//log.info("grounding against "+PA+" is done.");
				}
				else {
					log.info("grounding against "+PA+" failed due to missing sequence (or perhaps something else, who knows?)");
				}
			}
			Collections.sort(mutationInfoList);

			// DEBUGGING
			log.info("mutationInfoList.size(): "+mutationInfoList.size());
			for(MutationInfo mi : mutationInfoList){
			  ///log.info("mi.PA: "+mi.PA);
			  ///log.info("mi.Offset: "+mi.Offset);
			  for(Mutation mutation : mi.Mutations){
			    ///log.info("mi.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
			  }
			}

			/**
			 * Filter.
			 */
			for(int i = mutationInfoList.size()-1; i > -1; i--) {
				if(mutationInfoList.get(i).Mutations.size() == 0) {
					mutationInfoList.remove(i);
				}
				else {
					boolean remove = false;
					boolean removej = false;
					int outerJ = 0;
					for (int j = i-1; j > -1; j--) {
						List<Mutation> intersect = new ArrayList<Mutation>(mutationInfoList.get(j).Mutations);
						intersect.retainAll(mutationInfoList.get(i).Mutations);
						if(intersect.size() > 0) {
							if (mutationInfoList.get(j).Mutations.size() == mutationInfoList.get(i).Mutations.size()) {
								if(Math.abs(mutationInfoList.get(j).Offset) <= Math.abs(mutationInfoList.get(i).Offset)) {
									remove = true;
								}
								else {
									remove = true;
									removej = true;
									outerJ = j;
								}
							}
							else {
								remove = true;
							}
						}
					}
					if(remove) {
						if(removej) {
							mutationInfoList.remove(outerJ);
						}
						else {
							mutationInfoList.remove(i);
						}
					}
				}
			}
			//when filtering and extension is done, add the mutations previously removed, if their
			//wildtype residue and position is equal to any of the remaining mutations.
			for(MutationInfo mi : mutationInfoList) {
				for(Mutation m : mi.Mutations) {
					List<String> nm = new ArrayList<String>();
					nm.add(m.wildtype+Integer.toString(m.position)+m.mutation);
					try { 
						for(Mutation sameWT : sameWTMap.get(m)) {
							nm.add(sameWT.wildtype+Integer.toString(sameWT.position)+sameWT.mutation);
						}
					} catch(NullPointerException e) {
						
					}
					for(int i = 0; i < numberOfMutationMentions; i++) {
						if(nm.contains(mutationTable[i][3])) {
							mutationTable[i][4] = mi.PA;
							mutationTable[i][5] = Integer.toString(mi.Offset);
						}
					}
				}
			}
			
			
			Set<String> chosenPacs = new HashSet<String>();

			
			/**
			 *  Add PAC:s as features to protein and mutation mentions
			 */
			//log.info("Pmid\tStart\tEnd\tValue\t\t\tNormal\tGround\tOffset");
			for(int i = 0; i < numberOfMutationMentions; i++) {
				//log.info(doc.getName()+"\t"+mutationTable[i][0]+"\t"+mutationTable[i][1]+"\t"+mutationTable[i][2]+"\t"+mutationTable[i][3]+"\t\t\t"+mutationTable[i][4]+"\t"+mutationTable[i][5]);
				
				if(mutationTable[i][4] != null) {
					AnnotationSet as = doc.getAnnotations().get(Long.parseLong(mutationTable[i][0]),Long.parseLong(mutationTable[i][1]));
					Annotation a = (gate.Annotation)as.get("Mutation").iterator().next();
					FeatureMap features = Factory.newFeatureMap();  
					 
					features.put("hasMentionedPosition",Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1)));
					features.put("hasCorrectPosition", Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-Integer.parseInt(mutationTable[i][5]));
					features.put("isGroundedTo", mutationTable[i][4]);
					features.put("hasWildtypeResidue", mutationTable[i][3].substring(0,1));
					features.put("hasMutantResidue", Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1)));
					// outputAS					
					doc.getAnnotations().add(Long.parseLong(mutationTable[i][0]), Long.parseLong(mutationTable[i][1]), "GroundedPointMutation",features);
			
					
					String mutStr = mutationTable[i][3].substring(0,1)+Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))+Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1));
					//System.out.println(mutStr+" is grounded to "+mutationTable[i][4]);
					
					
					//export result to db for future evaluation
					/**
					Statement insertSt = con.createStatement();
						try {
							insertSt.executeUpdate("INSERT INTO groundedmutations (pmid,start,end,normal,pa,offset) VALUES('"+doc.getName()+"',"+mutationTable[i][0]+","+mutationTable[i][1]+",'"+mutationTable[i][3]+"','"+mutationTable[i][4]+"',"+mutationTable[i][5]+")");
						} catch(MySQLIntegrityConstraintViolationException err) {
							
						}
					/**/
					//remember the accession numbers for later use (the protein annotation step).
					chosenPacs.add(mutationTable[i][4]);
				}
			}
			
			/**
			 * Protein mentions.
			 */
			for(int[] ia : mentionTable.keySet()) {
				for(String ac : chosenPacs) {
					if (mentionTable.get(ia).contains(ac)) {
						AnnotationSet as = doc.getAnnotations().get(Long.parseLong(Integer.toString(ia[0])),Long.parseLong(Integer.toString(ia[1])));
						Annotation a = (gate.Annotation)as.get("ProteinName").iterator().next();
						FeatureMap features = Factory.newFeatureMap();
						features.put("mentionsProtein",ac);
						// ouputAS
						doc.getAnnotations().add(Long.parseLong(Integer.toString(ia[0])),Long.parseLong(Integer.toString(ia[1])), "ProteinMention", features);
					}
				}
			}
			
			/**
			 * Add chosenPacs as a feature to the document.
			 */
			String pacFeature = new String();
			int nop = 0;
			for(String ac : chosenPacs) {
				if(nop == 0) {
					pacFeature=ac;
				} else {
					pacFeature+=","+ac;
				}
				nop++;
			}
			doc.getFeatures().put("pacs",pacFeature);
			doc.getFeatures().put(pacFeature,PASequenceMapping.get(pacFeature));

			//inputAS.removeAll(inputAS.get("MutationTemp"));
			//inputAS.removeAll(inputAS.get("ProteinName"));
			//inputAS.removeAll(inputAS.get("ProperProteinName"));
			//inputAS.removeAll(inputAS.get("Lookup"));
			
			if(con!=null)con.close();
			if(con2!=null)con2.close();
			
	        //log.info(poolResultsAsString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
		
	 
	
	private Map sortMentionTable(Map<String,List<int[]>>  unsortMap) {        
        Map<String,Integer> dict = new HashMap<String,Integer>();
		for(Entry<String,List<int[]>> e : unsortMap.entrySet()){
			dict.put(e.getKey(), e.getValue().size());
		} 
        List<Map.Entry<String, Integer>> listtt = new ArrayList<Map.Entry<String, Integer>>(dict.entrySet());
        Collections.sort(listtt, new ValueThenKeyComparator<String, Integer>());
		// put sorted list into map again
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = listtt.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static String getOrganismNameAsInDB(String organismName){
		organismName = organismName.replaceAll("\\.", "");
		String organismNameNormalized = null;
		if(organismName.equalsIgnoreCase("mice")){
			organismNameNormalized = "mouse";
		}
		else if(organismName.equalsIgnoreCase("geese")){
			organismNameNormalized = "goose";
		}
		else if(organismName.equalsIgnoreCase("oxen")){
			organismNameNormalized = "ox";
		}
		else if(organismName.equalsIgnoreCase("calves")){
			organismNameNormalized = "calf";
		}
		else if(organismName.equalsIgnoreCase("cacti")){
			organismNameNormalized = "cactus";
		}
		else if(organismName.equalsIgnoreCase("pigeon")){
			organismNameNormalized = "domestic pigeon";
		}
		else if(organismName.equalsIgnoreCase("patient")){
			organismNameNormalized = "human";
		}
		else if(organismName.equalsIgnoreCase("patients")){
			organismNameNormalized = "human";
		}
		else if(organismName.equalsIgnoreCase("homo sapiens")){
			organismNameNormalized = "human";
		}
		else if(organismName.equalsIgnoreCase("coral")){
			organismNameNormalized = "plexaura homomalla";
		}else if(organismName.equalsIgnoreCase("corals")){
			organismNameNormalized = "plexaura homomalla";
		}
		else if(organismName.equalsIgnoreCase("mycobacteriophages")){
			organismNameNormalized = "mycobacteriophage";
		}
		if(organismNameNormalized != null)return organismNameNormalized;
		else return organismName;
	}
	
	private static String getOrganismNameAsInText(String organismName){
		// Un-normalize organism name (temporary solution)
		String organismNameUnnormalized = null;
		if(organismName.equalsIgnoreCase("mouse")){
			organismNameUnnormalized = "mice";
		}
		else if(organismName.equalsIgnoreCase("goose")){
			organismNameUnnormalized = "geese";
		}
		else if(organismName.equalsIgnoreCase("ox")){
			organismNameUnnormalized = "oxen";
		}
		else if(organismName.equalsIgnoreCase("calf")){
			organismNameUnnormalized = "calves";
		}
		else if(organismName.equalsIgnoreCase("cactus")){
			organismNameUnnormalized = "cacti";
		}
		else if(organismName.equalsIgnoreCase("domestic pigeon")){
			organismNameUnnormalized = "pigeon";
		}
		else if(organismName.equalsIgnoreCase("human")){
			organismNameUnnormalized = "patient";
		}
		else if(organismName.equalsIgnoreCase("human")){
			organismNameUnnormalized = "patients";
		}
		else if(organismName.equalsIgnoreCase("human")){
			organismNameUnnormalized = "homo sapiens";
		}
		if(organismNameUnnormalized != null)return organismNameUnnormalized;
		else return organismName;
	}

	// write proteinNameInDb in low case (!)
	private static String getProteinNameAsInDB(String proteinNameInText){
		String proteinNameInDb = null;
		if(proteinNameInText.equalsIgnoreCase("APA")){
			proteinNameInDb = "ap-a";
		} else if(proteinNameInText.equalsIgnoreCase("1,3-1,4-β-glucanase")){
			proteinNameInDb = "beta-1,3-1,4-glucanase";
		} else if(proteinNameInText.equalsIgnoreCase("saporin 5")){
			proteinNameInDb = "sap5";
		} else if(proteinNameInText.equalsIgnoreCase("saporin 6")){
			proteinNameInDb = "sap6";
		} else if(proteinNameInText.equalsIgnoreCase("cystatin b")){
			proteinNameInDb = "cystatin-b";
		} else if(proteinNameInText.equalsIgnoreCase("ENaC")){
			proteinNameInDb = "amiloride-sensitive sodium channel subunit gamma";
		} else if(proteinNameInText.equalsIgnoreCase("amiloride-sensitive sodium channel")){
			proteinNameInDb = "amiloride-sensitive sodium channel subunit gamma";
		} else if(proteinNameInText.equalsIgnoreCase("hOGG1")){
			proteinNameInDb = "ogg1";
		}

	
		if(proteinNameInDb != null)return proteinNameInDb;
		else return proteinNameInText;
	}
	
	
	private List<MutationInfo> alignMutations(GroundingVariantTable scoreTable, List<Mutation> normalizedMutations){
		//for(Mutation m : normalizedMutations)System.out.println(m.asString());
		Collections.sort(normalizedMutations);
		//for(Mutation m : normalizedMutations)System.out.println(m.asString());

		List<MutationInfo> mutationInfoList = new ArrayList<MutationInfo>();
		/*
		StringBuilder sb = new StringBuilder();
		for(String PA : scoreTable.getIndexByPack().keySet()){
			sb.append(PA);
		}
		for(int i = 0; i < normalizedMutations.size(); i++) {
			Mutation m = normalizedMutations.get(i);
			sb.append(m.asString());
		}
		
		System.err.println(sb.toString());
		
		
		
		System.err.println("\n\n\n"+CacheForGrounding.cache);
			*/	
		
		
		for(String PA : scoreTable.getIndexByPack().keySet()) {
			
			
			//
			// CACHE
			//
			StringBuilder sb = new StringBuilder();			
			sb.append(PA);			
			for(int i = 0; i < normalizedMutations.size(); i++) {
				Mutation m = normalizedMutations.get(i);
				sb.append(m.asString());
			}
			
			//System.err.println(sb.toString());
			
			if(CacheAlignment.cache!=null && CacheAlignment.cache.containsKey(sb.toString())){
				//System.err.println("==> "+sb.toString());
				List<MutationInfo> list = new ArrayList<MutationInfo>();
				for(String mi : CacheAlignment.cache.get(sb.toString())){
					//String[] parsed = Util.parseWNMFormat(mi);
					MutationInfo newMi = new MutationInfo(mi);
					list.add(newMi);
				}
				mutationInfoList.addAll(list);
				continue;
			}
			
			
			
			//System.err.println("==========================>");
			
			
					
			
			List<Mutation> normalizedMutationsClone = new ArrayList<Mutation>(normalizedMutations);

			log.trace("\n\nGROUNDING ALGORITHM for : "+ PA);
			MutationInfo mi;
			
			
					
			
			
			
			
			
			
			try {					
				
				String sequence = scoreTable.getIndexByPack().get(PA).get(0).getSequence();
				
				if(sequence==null){
					log.warn("!!!!!!!!! NO SEQUENCE FOR "+PA);
					continue;
				}
				//discard mutations that can never fit the sequence (position>sequence length +50)
				for(int i = normalizedMutationsClone.size()-1; i > -1 ; i--) {
					if(normalizedMutationsClone.get(i).position > sequence.length()) {
						//System.err.println("removed: "+normalizedMutationsClone.get(i).asString() +" "+PA);
						normalizedMutationsClone.remove(i); //commented by artjom
						i--;
					}
				}

				// @AK.Debug
				//for(Mutation mutation : normalizedMutations){
				    ///log.info("normalizedMutations3.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
				//}

				List<Mutation> G = new ArrayList<Mutation>();
				boolean groundingFound = false;
				String regex = "";
				Pattern pattern;
				Matcher matcher;
				
				List<MutationInfo> GList = new ArrayList<MutationInfo>();
				//first, look for smallest possible groundings (2 point mutations)
				for(int i = 0; i < normalizedMutationsClone.size(); i++) {						
					for(int j = i+1; j < normalizedMutationsClone.size(); j++) {
						log.trace(normalizedMutationsClone.get(i).asString()+" "+normalizedMutationsClone.get(j).asString());	
						if(normalizedMutationsClone.get(i).position == normalizedMutationsClone.get(j).position)continue;
						regex = normalizedMutationsClone.get(i).wildtype;
						for(int k = 0; k < (normalizedMutationsClone.get(j).position - normalizedMutationsClone.get(i).position -1); k++) {
							regex += ".";
						}
						regex += normalizedMutationsClone.get(j).wildtype;
						
						regex = "(?=("+regex+"))";

						
						log.trace("regex: "+regex);
						log.trace("sequence: "+sequence);
						
						pattern = null;
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(sequence);
						
						//if the smallest possible is found, see if it can be made larger
						while(matcher.find()) {
							log.trace("Found: "+normalizedMutationsClone.get(i).asString()+" "+normalizedMutationsClone.get(j).asString()+"\n");
							groundingFound = true;
							G.add(normalizedMutationsClone.get(i));
							G.add(normalizedMutationsClone.get(j));
							
							int firstMutationsPosition = G.get(0).position;
							int offset = firstMutationsPosition - (matcher.start()+1);
							
							//add all other mutations that fit.
							for(int k = j + 1; k < normalizedMutationsClone.size(); k++) {
								int positionToLookAt = normalizedMutationsClone.get(k).position-(offset+1);
								if(sequence.length() > positionToLookAt) {
									if (normalizedMutationsClone.get(k).wildtype.equals(Character.toString(sequence.charAt(positionToLookAt)))) {
										G.add(normalizedMutationsClone.get(k));
									}	
								}
							}
							GList.add(new MutationInfo(PA,new LinkedList<Mutation>(G),offset));
							G.clear();
						}
					}
				}			
				
				/*/ Ground singleton mutations.
				for(int i = 0; i < normalizedMutations.size(); i++) {
					Mutation m = normalizedMutations.get(i);
					//if(PA.equals("Q0P6M6"))log.debug(m.position+" "+sequence);
					if(m.position <= sequence.length()){						
						if (m.wildtype.charAt(0) == sequence.charAt(m.position-1)) {
							List<Mutation> list = new ArrayList<Mutation>();
							list.add(m);
							GList.add(new MutationInfo(PA,new LinkedList<Mutation>(list),0));
						}
					}
					
				}*/
				
				Collections.sort(GList);
				int listSize = GList.size();

				/*/ DEBUG
				log.info("GList.size(): "+GList.size());
				for(MutationInfo mi2 : GList){
					log.info(mi2.asString());
				}*/

				
				
				for(int i = listSize - 1 ; i > -1 ; i--) {
					boolean remove = false;
					boolean removej = false;
					int j2 = listSize;
					for(int j = i - 1 ; j > -1 ; j-- ) {
						//get the intersection of the two mutation lists
						Set<Mutation> intersection = new HashSet<Mutation>(GList.get(i).Mutations);
						intersection.retainAll(GList.get(j).Mutations);
						if(intersection.size() > 0) {
							if(GList.get(j).Mutations.size() > GList.get(i).Mutations.size()) {
								remove = true;
								break;
							}
							else {
								if(Math.abs(GList.get(j).Offset) < Math.abs(GList.get(i).Offset)) {
									remove = true;
									break;
								}
								else {
									remove = true;
									removej = true;
									j2 = j;
									break;
								}
							}
						}
					}
					if(remove) {
						if(removej) {
							GList.remove(j2);//System.out.println("REMOVE==============================");
						}
						else {
							GList.remove(i);//System.out.println("REMOVE==============================");
						}
					}
				}

				// @Artjom
				// Ground singleton mutations.
				for(int i = 0; i < normalizedMutations.size(); i++) {
					Mutation m = normalizedMutations.get(i);
					//if(PA.equals("Q0P6M6"))log.debug(m.position+" "+sequence);
					if(m.position <= sequence.length()){					
						
						if (m.wildtype.charAt(0) == sequence.charAt(m.position-1)) {
							List<Mutation> list = new ArrayList<Mutation>();
							list.add(m);
							GList.add(new MutationInfo(PA,new LinkedList<Mutation>(list),0));
						}
					}
					
				}
				
				 // DEBUG
				//log.info("GList2.size(): "+GList.size());
				//for(MutationInfo mii : GList){
				 // log.info("GList2.mi.PA: "+mii.PA);
				 // log.info("GList2.mi.Offset: "+mi.Offset);
				 // for(Mutation mutation : mii.Mutations){
				   // log.info("GList2.mi.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
				  //}
				//}

				//if there still are more than one remaining, just choose the top one for crying out loud..
				if (GList.size()>0) {
					mutationInfoList.addAll(GList);
					//mi = GList.get(0);
				}
				//if there are no matches, create an empty list with mutations (inside a mutationInfo object)
				else {
					//mi = new MutationInfo(PA,new ArrayList<Mutation>(),189819);
				}
				
				Set<String> seeeet = new HashSet<String>();
				for(MutationInfo mi2 : GList){
					seeeet.add(mi2.serialize());
				}
				if(CacheAlignment.cache!=null) CacheAlignment.cache.put(sb.toString(), seeeet);
				
			}	catch(StackOverflowError e) {
					log.warn("KLEPPP!");
					log.warn("PA:"+PA);
					for(Mutation m : normalizedMutations) {
						log.warn(m.wildtype+m.position+m.mutation);
					}
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mi = null;
			}
			/**
			 *  GROUNDING ALGORITHM END
			 */
			//if(mi!=null) {
				//mutationInfoList.add(mi);
				//log.info("grounding against "+PA+" is done.");
			//}
			//else {
			//	log.info("grounding against "+PA+" failed due to missing sequence (or perhaps something else, who knows?)");
			//}
		}
		//Set<String> seeeet = new HashSet<String>();
		//for(MutationInfo mi : mutationInfoList){
		//	seeeet.add(mi.serialize());
		//}
		//CacheForGrounding.cache.put(sb.toString(), seeeet);
		return mutationInfoList;
	}
	
	
	private GroundingVariantTable rankTable(
			gate.Document doc,
			GroundingVariantTable newScoreTable, 
			Map<String,List<int[]>> proteinMentionTable, 
			Map<String,List<int[]>> organismMentionTable,
			Map<String, List<int[]>> mutationMentionTable) throws InvalidOffsetException{
		
		
///////////////////////////////////////////////////////////////////////////////////
		// Prepare statistics to calculating statistical distributions for features of GroundingVariants in newScoreTable. 
		//
		int numberOfProteinMentionsInDoc = 0;
		int numberOfOrganismMentionsInDoc = 0;
		int numberOfPOMentionsInDoc = 0;
		int numberOfPMMentionsInDoc = 0;
		int numberOfOMMentionsInDoc = 0;
		int numberOfPOMMentionsInDoc = 0;
		int maxNumberOfSynonymsInTable = 0;
		int numberOfMutationsInDoc = 0;


		for(Entry<String,List<int[]>> e : proteinMentionTable.entrySet()){
        	numberOfProteinMentionsInDoc = numberOfProteinMentionsInDoc + e.getValue().size();
		}			 
		for(Entry<String,List<int[]>> e : organismMentionTable.entrySet()){
			numberOfOrganismMentionsInDoc = numberOfOrganismMentionsInDoc + e.getValue().size();
		}
		for(Entry<String,List<int[]>> e : mutationMentionTable.entrySet()){
			numberOfMutationsInDoc = numberOfMutationsInDoc + e.getValue().size();
		}

        
        Map<String,Integer> poFrequencyMap = new HashMap<String,Integer>();
        Map<String,Integer> pmFrequencyMap = new HashMap<String,Integer>();
        Map<String,Integer> omFrequencyMap = new HashMap<String,Integer>();
        Map<String,Integer> pomFrequencyMap = new HashMap<String,Integer>();
        Iterator mapIter13 = proteinMentionTable.keySet().iterator();
        while ( mapIter13.hasNext() ){
        	String proteinNameInText = (String)mapIter13.next();
			for(int[] startAndEnd : proteinMentionTable.get(proteinNameInText)){
				Annotation sentence = (Annotation)doc.getAnnotations().get("Sentence",new Long(startAndEnd[0]),new Long(startAndEnd[1])).iterator().next();
				//System.err.println("s: "+doc.getContent().getContent(sentence.getStartNode().getOffset(), sentence.getEndNode().getOffset()).toString());
				
				boolean POISS = false;
				boolean PMISS = false;

				AnnotationSet organismNames = doc.getAnnotations().get("OrganismName",sentence.getStartNode().getOffset(),sentence.getEndNode().getOffset());
				for(Annotation ann : organismNames){
					Long s = ann.getStartNode().getOffset();
					Long e = ann.getEndNode().getOffset();
					String organismNameInText = doc.getContent().getContent(s, e).toString().toLowerCase();						
					String poPair = proteinNameInText+"_"+getOrganismNameAsInDB(organismNameInText);						
					if(poFrequencyMap.containsKey(poPair)){
						int temp = poFrequencyMap.get(poPair) + 1;
						poFrequencyMap.put(poPair, temp);
					}else{
						poFrequencyMap.put(poPair, 1);
					}	
					POISS = true;
				}
				
				AnnotationSet mutationTemps = doc.getAnnotations().get("Mutation",sentence.getStartNode().getOffset(),sentence.getEndNode().getOffset());
				for(Annotation ann : mutationTemps){
					String normalizedMutationString = (String)ann.getFeatures().get("wNm");	
					//System.err.println("normalizedMutationString: "+normalizedMutationString);						
					String pmPair = proteinNameInText+"_"+normalizedMutationString;						
					if(pmFrequencyMap.containsKey(pmPair)){
						int temp = pmFrequencyMap.get(pmPair) + 1;
						pmFrequencyMap.put(pmPair, temp);
					}else{
						pmFrequencyMap.put(pmPair, 1);
					}
					PMISS = true;
				}
				
				
				for(Annotation ann : organismNames){
					Long s = ann.getStartNode().getOffset();
					Long e = ann.getEndNode().getOffset();
					String organismNameInText = doc.getContent().getContent(s, e).toString().toLowerCase();						
					for(Annotation ann2 : mutationTemps){
						String normalizedMutationString = (String)ann2.getFeatures().get("wNm");	
						//System.err.println("normalizedMutationString: "+normalizedMutationString);						
						String omPair = getOrganismNameAsInDB(organismNameInText)+"_"+normalizedMutationString;	
						numberOfOMMentionsInDoc++;
						if(omFrequencyMap.containsKey(omPair)){
							int temp = omFrequencyMap.get(omPair) + 1;
							omFrequencyMap.put(omPair, temp);
						}else{
							omFrequencyMap.put(omPair, 1);
						}
						//OMISS = true;
					}
				}
				
				if(POISS && PMISS){
					numberOfPOMMentionsInDoc = numberOfPOMMentionsInDoc + 1;
				}
			}
        } 
        
		for(Entry<String,Integer> e : poFrequencyMap.entrySet()){
			numberOfPOMentionsInDoc = numberOfPOMentionsInDoc + e.getValue();
		}
		for(Entry<String,Integer> e : pmFrequencyMap.entrySet()){
			numberOfPMMentionsInDoc = numberOfPMMentionsInDoc + e.getValue();
		}
        
		// SYNONYMS			
		for(Iterator<GroundingVariant> iter = newScoreTable.iterator();iter.hasNext();){
			GroundingVariant gv = iter.next();
			if(gv.getProteinNameInText().size() > maxNumberOfSynonymsInTable){
				maxNumberOfSynonymsInTable = gv.getProteinNameInText().size();
			}
		}
		
///////////////////////////////////////////////////////////////////////////////////
		// Get Features for GroundingVariants in newScoreTable. And calculate the score. NEW!!!!!
		//
		float sdProteinNameMax = 0;
		float sdOrganismNameMax = 0;
		float sdPOISSMax = 0;
		float sdPMISSMax = 0;
		float sdOMISSMax = 0;
		float sdProteinSysnonymMax = 0;
		float sdExactProteinNameMax = 0;
		float absolutePositionMax = 0;
		float swissProtMax = 0;
		float sdNumberOfGroundedMutationMax = 0;

		for(GroundingVariant gv : newScoreTable.getEntries()){
			gv.setFeatureDescriptor();
			
			//--- statistical distribution protein names ---
			int numberOfProteinMentionsInGv = 0;
			for(String s : gv.getProteinNameInText().keySet()){
				//System.err.println(s);
				numberOfProteinMentionsInGv = numberOfProteinMentionsInGv + proteinMentionTable
				.get(s)
				.size();
			}
			float f1 = (float)numberOfProteinMentionsInGv/(float)numberOfProteinMentionsInDoc;
			gv.getFeatureDescriptor().setValue("sdProteinName",f1); 
			if(f1 > sdProteinNameMax) sdProteinNameMax = f1;
			
			// statistical distribution organism names
			//System.out.println(gv.getOrganismName() + ": "+gv.getOrganismName());
			//System.out.println(organismMentionTable.keySet());
			
			int numberOfOrganismMentionsInGv = 0;
			for(String organismName : gv.getOrganismName()){
				numberOfOrganismMentionsInGv = numberOfOrganismMentionsInGv + organismMentionTable.get(organismName).size();
			}
			float f2 = (float)numberOfOrganismMentionsInGv/(float)numberOfOrganismMentionsInDoc;
			gv.getFeatureDescriptor().setValue("sdOrganismName",f2); 
			if(f2 > sdOrganismNameMax) sdOrganismNameMax = f2;
			
			//--- statistical distribution PO pairs ---
			int numberOfPOMentionsInGv = 0;
			for(String proteinNameInText : gv.getProteinNameInText().keySet()){
				for(String organismName : gv.getOrganismName()){
					String proteinNameInTextOrganism = proteinNameInText + "_" + organismName;					
					if(poFrequencyMap.containsKey(proteinNameInTextOrganism)){
						numberOfPOMentionsInGv = numberOfPOMentionsInGv + poFrequencyMap.get(proteinNameInTextOrganism);
					}
				}
									
			}
			float f3 = (float)numberOfPOMentionsInGv/(float)numberOfPOMentionsInDoc;
			gv.getFeatureDescriptor().setValue("sdPOISS",f3); 
			if(f3 > sdPOISSMax) sdPOISSMax = f3;

			//--- statistical distribution PM pairs ---
			int numberOfPMMentionsInGv = 0;
			for(String proteinNameInText : gv.getProteinNameInText().keySet()){
				for(String mutation : gv.getMutations()){
					String proteinNameInTextMutation = proteinNameInText + "_" + mutation;					
					if(pmFrequencyMap.containsKey(proteinNameInTextMutation)){
						numberOfPMMentionsInGv = numberOfPMMentionsInGv + pmFrequencyMap.get(proteinNameInTextMutation);
					}	
				}									
			}
			//log.debug("@1: "+numberOfPMMentionsInGv);
			//log.debug("@2: "+numberOfPMMentionsInDoc);
			float f4 = (float)numberOfPMMentionsInGv/(float)numberOfPMMentionsInDoc;
			gv.getFeatureDescriptor().setValue("sdPMISS",f4); 
			if(f4 > sdPMISSMax) sdPMISSMax = f4;
			//log.debug("OLAAAAAA"); 
			// statistical distribution PM pairs
			int numberOfOMMentionsInGv = 0;				
			for(String mutation : gv.getMutations()){
				for(String organismMutation : gv.getOrganismName()){
					String organismNameInTextMutation = organismMutation + "_" + mutation;					
					if(omFrequencyMap.containsKey(organismNameInTextMutation)){
						numberOfOMMentionsInGv = numberOfOMMentionsInGv + omFrequencyMap.get(organismNameInTextMutation);
					}
				}
					
			}			
			float f4_2 = (float)numberOfOMMentionsInGv/(float)numberOfOMMentionsInDoc;
			gv.getFeatureDescriptor().setValue("sdOMISS",f4_2); 
			if(f4_2 > sdOMISSMax) sdOMISSMax = f4_2;
			
			//--- statistical distribution protein synonyms ---
			float f5 = (float)gv.getProteinNameInText().size()/(float)maxNumberOfSynonymsInTable;
			gv.getFeatureDescriptor().setValue("sdProteinSysnonym",f5); 
			if(f5 > sdProteinSysnonymMax) sdProteinSysnonymMax = f5;

			//--- statistical distribution exact name of protein ---
			int numberOfExactProteinNamesInGv = 0;
			for(String proteinNameInText : gv.getProteinNameInText().keySet()){
				for(String proteinNameInDb : gv.getProteinNameInText().get(proteinNameInText)){
					if(proteinNameInText.equalsIgnoreCase(proteinNameInDb)){
						numberOfExactProteinNamesInGv = numberOfExactProteinNamesInGv + 1;
						break;
					}
				}
			}
			float f6 = (float)numberOfExactProteinNamesInGv/(float)gv.getProteinNameInText().size();				
			gv.getFeatureDescriptor().setValue("sdExactProteinName",f6); 
			if(f6 > sdExactProteinNameMax) sdExactProteinNameMax = f6;
			
			float f7 = 0;
			if(gv.getOffset() == 0) f7 = 1;
			gv.getFeatureDescriptor().setValue("absolutePosition",f7); 
			if(f7 > absolutePositionMax) absolutePositionMax = f7;

			float f8 = 0;
			if(Integer.parseInt(gv.getId()) < 532793) f8 = 1;
			gv.getFeatureDescriptor().setValue("swissProt",f8); 
			if(f8 > swissProtMax) swissProtMax = f8;

			float f9 = 0;
			f9 = (float)gv.getMutations().size()/(float)numberOfMutationsInDoc;
			gv.getFeatureDescriptor().setValue("sdNumberOfGroundedMutation",f9); 
			if(f9 > sdNumberOfGroundedMutationMax) sdNumberOfGroundedMutationMax = f9;

			gv.originalScore = (float) (
					0.1*(Float)gv.getFeatureDescriptor().getValue("sdProteinName")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdOrganismName")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdPOISS")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdPMISS")
					//+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdOMISS")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdProteinSysnonym")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdExactProteinName")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("absolutePosition")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("swissProt")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdNumberOfGroundedMutation")						
					);
			
			
			
		}
			
		Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByOriginalScore);
		
		//////////////////////////////////
		///// Filter GVs with the same PAC: keep with the highest score
		///// This used to increase Precision
		/*if(filterOne){
			GroundingVariant bestInTable = newScoreTable.getEntries().get(0);
			if((Float)bestInTable.getFeatureDescriptor().getValue("sdPOISS") == 0.0
						&& (Float)bestInTable.getFeatureDescriptor().getValue("sdProteinName") < 0.15
						&& (Float)bestInTable.getFeatureDescriptor().getValue("sdNumberOfGroundedMutation") > 0.015
						){
				log.debug("MutationGrounding is stopped here: "+bestInTable);
				return;
			}
		}*/
		
		
		
		try{
			debugingResultsSb.append(
					doc.getName()+"_"+
					newScoreTable
					.getEntries()
					.get(0)+"\n");
		} catch(Exception e){
			e.printStackTrace();
		}
		
		Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByScore);
		
		//log.debug("\n"+newScoreTable.asStringTable());

		/*
		 * 
		 * Normalization. Recalculate scores.
		 * FORMULA
		 * 	
		 */
		Map<String,Float> maxPAcScore = new HashMap<String,Float>();
		float defaultt= 0;
		for(GroundingVariant gv : newScoreTable.getEntries()){
			if(sdProteinNameMax!=0.0)	
				gv.getFeatureDescriptor().setValue("sdProteinName",(Float)gv.getFeatureDescriptor().getValue("sdProteinName")*(1/sdProteinNameMax));
			if(sdOrganismNameMax!=0.0)	
				gv.getFeatureDescriptor().setValue("sdOrganismName",(Float)gv.getFeatureDescriptor().getValue("sdOrganismName")*(1/sdOrganismNameMax));
			
			if(sdPOISSMax!=0.0)	gv.getFeatureDescriptor().setValue("sdPOISS",(Float)gv.getFeatureDescriptor().getValue("sdPOISS")*(1/sdPOISSMax));
			else gv.getFeatureDescriptor().setValue("sdPOISS",defaultt);

			if(sdPMISSMax!=0.0)	gv.getFeatureDescriptor().setValue("sdPMISS",(Float)gv.getFeatureDescriptor().getValue("sdPMISS")*(1/sdPMISSMax));
			else gv.getFeatureDescriptor().setValue("sdPMISS",defaultt);
			
			if(sdProteinSysnonymMax!=0.0) gv.getFeatureDescriptor().setValue("sdProteinSysnonym",(Float)gv.getFeatureDescriptor().getValue("sdProteinSysnonym")*(1/sdProteinSysnonymMax));
			else gv.getFeatureDescriptor().setValue("sdProteinSysnonym",defaultt);

			if(sdExactProteinNameMax!=0.0) gv.getFeatureDescriptor().setValue("sdExactProteinName",(Float)gv.getFeatureDescriptor().getValue("sdExactProteinName")*(1/sdExactProteinNameMax));
			else gv.getFeatureDescriptor().setValue("sdExactProteinName",defaultt);

			if(absolutePositionMax!=0.0)gv.getFeatureDescriptor().setValue("absolutePosition",(Float)gv.getFeatureDescriptor().getValue("absolutePosition")*(1/absolutePositionMax));
			else gv.getFeatureDescriptor().setValue("absolutePosition",defaultt);

			if(swissProtMax!=0.0) gv.getFeatureDescriptor().setValue("swissProt",(Float)gv.getFeatureDescriptor().getValue("swissProt")*(1/swissProtMax));
			else gv.getFeatureDescriptor().setValue("swissProt",defaultt);

			if(sdNumberOfGroundedMutationMax!=0.0) gv.getFeatureDescriptor().setValue("sdNumberOfGroundedMutation",(Float)gv.getFeatureDescriptor().getValue("sdNumberOfGroundedMutation")*(1/sdNumberOfGroundedMutationMax));
			else gv.getFeatureDescriptor().setValue("sdNumberOfGroundedMutation",defaultt);

			if(sdOMISSMax!=0.0) gv.getFeatureDescriptor().setValue("sdOMISS",(Float)gv.getFeatureDescriptor().getValue("sdOMISS")*(1/sdOMISSMax));
			else gv.getFeatureDescriptor().setValue("sdOMISS",defaultt);

							
			gv.score = (float) (
					0.1*(Float)gv.getFeatureDescriptor().getValue("sdProteinName")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdOrganismName")*1.5
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdPOISS")
					+ (float)0.1*(Float)gv.getFeatureDescriptor().getValue("sdPMISS")
					//+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdOMISS")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdProteinSysnonym")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdExactProteinName")*1.6
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("absolutePosition")*1.5
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("swissProt")*1.9
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdNumberOfGroundedMutation")*9			
					);
			
			
			
			/*
			 * gv.score = (float) (
					0.1*(Float)gv.getFeatureDescriptor().getValue("sdProteinName")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdOrganismName")*1.5
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdPOISS")*1.5
					+ (float)0.1*(Float)gv.getFeatureDescriptor().getValue("sdPMISS")*2
					//+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdOMISS")
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdProteinSysnonym")*2
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdExactProteinName")*1.5
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("absolutePosition")*3.3
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("swissProt")*4
					+ 0.1*(Float)gv.getFeatureDescriptor().getValue("sdNumberOfGroundedMutation")*9					
					);
			 */
			if((Float)gv.getFeatureDescriptor().getValue("sdPMISS") > 0.9){
				gv.score = gv.score + (float)0.1;
			}
			
			if(maxPAcScore.containsKey(gv.getPac()+"_"+gv.getOffset())){
				if(maxPAcScore.get(gv.getPac()+"_"+gv.getOffset()) < gv.score) maxPAcScore.put(gv.getPac()+"_"+gv.getOffset(), gv.score);
			}else{
				maxPAcScore.put(gv.getPac()+"_"+gv.getOffset(), gv.score);
			}
			
		}
		
		return newScoreTable;
	}
	
	/**
	 * =========================================================================================
	 * GROUND
	 * =========================================================================================
	 * @param doc
	 * @param docName
	 * @throws InvalidOffsetException 
	 */
	public void groundSuperNew(Document doc) throws InvalidOffsetException{
		
		/*
		 * For Testing gate gazetteers.
		 *
		List<gate.Annotation> proteinMentions = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("ProperProteinName"));
		for(gate.Annotation proteinMention : proteinMentions) {
			Long s = proteinMention.getStartNode().getOffset();
			Long e = proteinMention.getEndNode().getOffset();
			String value = doc.getContent().getContent(s, e).toString();
			
			if(proteinNames.containsKey(doc.getName())){
				proteinNames.get(doc.getName()).add(value);
			}else{
				Set<String> l = new HashSet<String>();
				proteinNames.put(doc.getName(), l);
			}
		}*/
			
		for(Annotation proteinMention : doc.getAnnotations().get("ProperProteinName")){        
			Long s = proteinMention.getStartNode().getOffset();
			Long e = proteinMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			String start = Long.toString(s);
			String end = Long.toString(e);	
			if(doc.getAnnotations().get("NP",s,e).size() >0){
				Annotation np = (Annotation)doc.getAnnotations()
				.get("NP",s,e)
				.iterator()
				.next();
				
				AnnotationSet otherProteins = doc.getAnnotations().get("ProperProteinName",np.getStartNode().getOffset(),np.getEndNode().getOffset());

				if(otherProteins.size()>2){
					doc.getAnnotations().removeAll(otherProteins);				
				}
			}
			
			
			
		}

		
		/*
		 * Populate protein mention table.
		 */
        Map<String,List<int[]>> proteinMentionTable = new HashMap<String,List<int[]>>();
        for(Annotation proteinMention : doc.getAnnotations().get("ProperProteinName")){        
			Long s = proteinMention.getStartNode().getOffset();
			Long e = proteinMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			String start = Long.toString(s);
			String end = Long.toString(e);	
			
			// Synthetic test
			////name = getProteinNameAsInDB(name);
        	
			if (name.length() > 2 && name.matches(".*[A-Za-z]+.*") && !proteinNameExceptionList.contains(name)) {
        		if(proteinMentionTable.containsKey(name)){
        			proteinMentionTable.get(name).add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});
            	}else{
            		List<int[]> l = new ArrayList<int[]>();
            		l.add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});
            		proteinMentionTable.put(name, l);
            	}
        	}        	
        }       
        
		/*
		 * Filter by number of mentions.
		 */
        Iterator mapIter = proteinMentionTable.keySet().iterator();
        while ( mapIter.hasNext() ){
        	String key = (String)mapIter.next();
        	if(proteinMentionTable.get(key).size()<2 && key.length() < 4){
        		mapIter.remove();
        	}
        }       
        log.debug(Utils.mapAsPrettyString(sortMentionTable(proteinMentionTable)));
        
		/*
		 * Populate organism mention table.
		 */
        if(doc.getAnnotations().get("OrganismName").size()==0){
        	log.warn("No organisms in document: "+doc.getName());
        	return;
        }
        Map<String,List<int[]>> organismMentionTable = new HashMap<String,List<int[]>>();
        for(Annotation organismMention : doc.getAnnotations().get("OrganismName")){        
			Long s = organismMention.getStartNode().getOffset();
			Long e = organismMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			String start = Long.toString(s);
			String end = Long.toString(e);	
			name = getOrganismNameAsInDB(name);
        	if (name.length() > 2 && name.matches(".*[A-Za-z]+.*")) {
        		if(organismMentionTable.containsKey(name)){
        			organismMentionTable.get(name).add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});
            	}else{
            		List<int[]> l = new ArrayList<int[]>();
            		l.add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});     		
            		organismMentionTable.put(name, l);
            	}
        	}        	
        }        
        log.debug(Utils.mapAsPrettyString(sortMentionTable(organismMentionTable)));


		
		/*
		 * Populate mutation mention table.
		 */
		Set<String> normalizedMutationsSet = new TreeSet<String>();
		Map<String, List<int[]>> mutationMentionTable = new HashMap<String, List<int[]>>();
		for (Annotation mutationMention : doc.getAnnotations().get("Mutation")) {
			Long s = mutationMention.getStartNode().getOffset();
			Long e = mutationMention.getEndNode().getOffset();
			String start = Long.toString(s);
			String end = Long.toString(e);
			String normalizedMutationString = (String) mutationMention.getFeatures().get("wNm");
			normalizedMutationsSet.add(normalizedMutationString);
			if (mutationMentionTable.containsKey(normalizedMutationString)) {
				mutationMentionTable.get(normalizedMutationString).add(new int[] { Integer.parseInt(start), Integer.parseInt(end) });
			} else {
				List<int[]> l = new ArrayList<int[]>();
				l.add(new int[] { Integer.parseInt(start), Integer.parseInt(end) });
				mutationMentionTable.put(normalizedMutationString, l);
			}

		}
        log.debug(Utils.mapAsPrettyString(sortMentionTable(mutationMentionTable)));
        
        
		
        /*
		 * Jonas mutation table.
		 */
		
		List<gate.Annotation> mutationAnnotations = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("Mutation"));
		int numberOfMutationMentions = mutationAnnotations.size();
		log.debug("number of mutation mentions: "+numberOfMutationMentions);
		String[][] mutationTable = new String[numberOfMutationMentions][6];
		Set<String> mutationMentions = new HashSet<String>();
		Set<String> mutationsWNMFormat = new TreeSet<String>();
		
		int mutationIndex = 0;
		
		Set<Mutation> normalizedMutationSet = new HashSet<Mutation>();
		
		for(gate.Annotation mutationAnnotation : mutationAnnotations) {
			Long s = mutationAnnotation.getStartNode().getOffset();
			Long e = mutationAnnotation.getEndNode().getOffset();
			
			mutationTable[mutationIndex][0] = Long.toString(s);
			mutationTable[mutationIndex][1] = Long.toString(e);
			mutationTable[mutationIndex][2] = doc.getContent().getContent(s, e).toString();

			String normalizedMutationString = (String)mutationAnnotation.getFeatures().get("wNm");			
			mutationTable[mutationIndex][3] = normalizedMutationString;
			
			//extract the different parts and add to a set of normalized mutations.
			String wildtype = normalizedMutationString.substring(0,1);
			String mutant = normalizedMutationString.substring(normalizedMutationString.length()-1);
			int position = Integer.parseInt(normalizedMutationString.substring(1,normalizedMutationString.length()-1));
			normalizedMutationSet.add(new Mutation(wildtype, position, mutant));
			
			mutationMentions.add(doc.getContent().getContent(s, e).toString());
			mutationsWNMFormat.add(normalizedMutationString);
			mutationIndex++;
		}
		
					
		//make a list of the set
		List<Mutation> normalizedMutations = new ArrayList<Mutation>(normalizedMutationSet);		
		//sort the mutations, can't really remember why anymore.
		Collections.sort(normalizedMutations);
		
		/*/ save statistics about by MutationFinder found mutations
        try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("document-mutations"), true));
			bw.write(doc.getName()+": "+mutationsWNMFormat);			
			bw.write("\n");
			bw.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}*/

		
		
		//Make sure mutations with the same WT residue and the same position are treated as one single mutation.
		//When the grounding and the filtering is done, they are again treated as different mutations
		Map<String,List<Mutation>> sameWTMap = new HashMap<String, List<Mutation>>();
		List<Integer> listedPositions = new ArrayList<Integer>();
		for(int i = 0; i < normalizedMutations.size(); i++) {
			if(!listedPositions.contains(i)) {
				List<Mutation> mList = new ArrayList<Mutation>();
				for(int j = i+1; j < normalizedMutations.size(); j++) {
					//two mutations are considered equal if they have the same WT residue and the same position
					if(normalizedMutations.get(i).equals(normalizedMutations.get(j))) {
						mList.add(new Mutation(normalizedMutations.get(j).wildtype, normalizedMutations.get(j).position, normalizedMutations.get(j).mutation));
						listedPositions.add(j);
					}
				}
				if (mList.size() > 0) {
					sameWTMap.put(normalizedMutations.get(i).asString(), mList);
				}
			}
		}

		
		
		
		// @AK.Debug
		//for(Mutation mutation : normalizedMutations){
		    //log.info("\n\nnormalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
		 //}

		//remove "dublicates"
		for(int i = normalizedMutations.size()-1; i > -1; i--) {
			if(listedPositions.contains(i)) {
				normalizedMutations.remove(i);
			}
		}

		// @AK.Debug
	    for(Mutation mutation : normalizedMutations){
		    log.debug("normalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
		 }

		
		///log.debug(stringTableAsString(mutationTable));
        
        
		/*
		 * 
		 * 
		 * 
		 * 
		 * ScoreTable AND Grounding.
		 * 
		 * 
		 * 
		 * 
		 */
        GroundingVariantTable scoreTable = new GroundingVariantTable();
        
		try {			
			Properties pro = new Properties();
            pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
            boolean onlySwissprot = false;
            if(pro.getProperty("ONLY_SWISSPROT").equals("true")){
            	onlySwissprot = true;
            }
            log.debug("ONLY_SWISSPROT: "+onlySwissprot);
            boolean filterOne = false;
            if(pro.getProperty("FILTER_ONE").equals("true")){
            	filterOne = true;
            }
            log.debug("FILTER_ONE: "+filterOne);
            
            
            String DATABASE_URL = pro.getProperty("DATABASE_URL");
            String USERNAME = pro.getProperty("DATABASE_USERNAME");; 
			String PASSWORD = pro.getProperty("DATABASE_PASSWORD");;
            
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/swissprot";
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/uniprot";
			//String DATABASE_URL = "jdbc:mysql://localhost:3306/uniprot";
			//String DATABASE2_URL = "jdbc:mysql://localhost:3306/trembl2";
			//String DATABASE2_URL = "jdbc:mysql://sadi2:3306/uniprot3";

			//String USERNAME = "root"; 
			//String PASSWORD = "deduction";

			Class.forName("com.mysql.jdbc.Driver");		
			con2 = DriverManager.getConnection("jdbc:mysql://"+DATABASE_URL, USERNAME, PASSWORD);
			//con2 = DriverManager.getConnection(DATABASE2_URL, USERNAME, PASSWORD);
			
			String docName = doc.getName().split("\\.")[0];
			log.debug("docName: "+docName);
			doc.getAnnotations().removeAll((gate.AnnotationSet)doc.getAnnotations().get("GroundedPointMutation"));
							
			
			/*
			 * Build substring for organisms.			
			 */
			StringBuilder organismSubString = new StringBuilder();	
			for(String o : organismMentionTable.keySet()) {
				log.debug(o);
				if(o.length()<2)continue; // TODO may be < 3 (test it)
				organismSubString.append("organismname3.name='"+o+"' OR ");				
			}
			//System.out.println("organismSubString: "+organismSubString);
			//if(organismSubString.length()>4)
				organismSubString.delete(organismSubString.length()-4, organismSubString.length());

			
			Set<String> set = new TreeSet<String>();	
			
			
			/*
			 * Receive PAC candidates from DB.
			 */
			CacheQuerying.loadCache(cacheFileName);
			long queryTime = System.currentTimeMillis();
			for (String proteinName : proteinMentionTable.keySet()) {

				
				StringBuilder finalQuerySb = new StringBuilder();
				finalQuerySb.append("SELECT DISTINCT *  " +
						"FROM proteinname, organismname3, pac, sequence, genenames2  " +
						"WHERE (match(proteinname.name) against (");

				String mentionStr = proteinName.toLowerCase();
				String mentionStrForSqlQuery = mentionStr.replaceAll("'", "''");
				finalQuerySb.append("'" + mentionStrForSqlQuery + "'");
				
				//
				// EXACT MATCH OF PROTEIN NAMES
				//
				finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStrForSqlQuery + "%') AND (");		
				//finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStr + "') AND (");					

				/*
				 * WRONG WAY
				if(mentionStr.length()>5){
					finalQuerySb.append("'" + mentionStr + "'");
					finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStr + "%') AND (");					
				}else {
					finalQuerySb.append("'" + mentionStr + "'");
					finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStr + "') AND (");					
				}
				Number of documents: 38
				numberOfAllMutations_Eval: 178
				numberOfAllMutations_Grounded: 156
				numberOfAllMutations_GroundedCorrect: 97
				Precision: 0.6217949
				Recall: 0.5449438
				GroundingRate: 0.8764045
				*/
				
				finalQuerySb.append(organismSubString.toString());
				finalQuerySb.append(") AND (proteinname.id=pac.id) AND (organismname3.id=pac.id) AND (sequence.id=pac.id) AND (genenames2.id=pac.id);");
				log.debug("FINAL QUERY: " + finalQuerySb.toString());

				
				// If query in cache, get results from cache. Else send query to DB.
				Set<String> queryResult = null;
				if(CacheQuerying.cache.containsKey(finalQuerySb.toString())){
					queryResult = CacheQuerying.cache.get(finalQuerySb.toString());
				}else{
					queryResult = new HashSet<String>();
					Statement primaryAccSt = con2.createStatement();
					ResultSet primaryAccRs = primaryAccSt.executeQuery(finalQuerySb.toString());
					while (primaryAccRs.next()) {
						String geneNames = "XXX";
						if(!primaryAccRs.getString("genenames2.name").equals(""))geneNames=primaryAccRs.getString("genenames2.name");
						String s = primaryAccRs.getString("pac.pac") + "##" 
							+ primaryAccRs.getString("proteinname.name") + "##"
							+ primaryAccRs.getString("organismname3.name") + "##"
							+ primaryAccRs.getString("sequence.sequence") + "##"
							+ primaryAccRs.getString("pac.id") + "##"
							+ geneNames;						
						queryResult.add(s);		//System.err.println(s);									
					}
					CacheQuerying.cache.put(finalQuerySb.toString(), queryResult);						
					corpusQueryTime = corpusQueryTime + (System.currentTimeMillis() - queryTime);
				}	
					
				//
				// Populate score table.
				//
				Set<String> pacsCandidates = new HashSet<String>();

				for(String s : queryResult){
					String[] split = s.split("##");
					String pac = split[0]; pacsCandidates.add(pac);	
					String proteinNameInDb = split[1];

					
					// Activate to ground only to SWISS-PROT
					if(onlySwissprot){
						if(Integer.parseInt(split[4]) > 532792) continue;
					}
					if(Integer.parseInt(split[4]) > 532792 && statistics.getMap().get(doc.getName()).contains(pac)) System.err.println("from trembl: "+doc.getName()+"_"+pac);
					
					// if scoreTable contains PAC, add protein name from DB
					// TODO here score possible: frequency of retrieved from DB
					if(scoreTable.getIndexByPack().containsKey(pac)){						
						if(scoreTable.getIndexByPack().get(pac).get(0).getProteinNameInText().containsKey(mentionStr)){
							scoreTable.getIndexByPack().get(pac).get(0).getProteinNameInText().get(mentionStr).add(proteinNameInDb);
							scoreTable.getIndexByPack().get(pac).get(0).getProteinName().add(proteinNameInDb);
						}else{
							Set<String> newSet = new HashSet<String>();
							newSet.add(proteinNameInDb);
							scoreTable.getIndexByPack().get(pac).get(0).getProteinNameInText().put(mentionStr, newSet);
							scoreTable.getIndexByPack().get(pac).get(0).getProteinName().add(proteinNameInDb);
						}
						String organismName = split[2].toLowerCase();
						//if(!scoreTable.getIndexByPack().get(pac).get(0).getOrganismName().contains(organismName)){
							scoreTable.getIndexByPack().get(pac).get(0).getOrganismName().add(organismName);
						//}
					}else{
						Map<String,Set<String>> proteinNameInTextSet = new LinkedHashMap<String,Set<String>>();
						Set<String> namesInDb = new HashSet<String>();
						namesInDb.add(proteinNameInDb);
						proteinNameInTextSet.put(mentionStr,namesInDb);
						Set<String> proteinNameSet = new LinkedHashSet<String>();
						proteinNameSet.add(proteinNameInDb);
						
						Set<String> organismNameSet = new LinkedHashSet<String>();
						organismNameSet.add(split[2].toLowerCase());
						
						GroundingVariant gv = new GroundingVariant(
								pac,
								proteinNameInTextSet,
								proteinNameSet,
								organismNameSet, 		// organism names
								split[3], 		 		// sequence
								split[4],				// id
								split[5].split("__") 	// gene names
								);
						scoreTable.addEntry(gv);
						set.add(pac);
					}
				}
				log.debug("queryResult: " + pacsCandidates);
			}						
			CacheQuerying.makeCachePersistent(cacheFileName);
			
	        
			//scoreTable.display(GroundingVariant.sortByPac);



			// DEBUGGING.
			Set<String> removed = new TreeSet<String>();			
			for(String swissProt : 	statistics
					.getMap()
					.get(
							docName)){
				if(!scoreTable.containsPac(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not in set");
					statistics.notInForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_"+proteinMentionTable.keySet());	
					removed.add(swissProt);
				}else{	
					statistics.inForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_"+proteinMentionTable.keySet());
					log.debug(doc.getName()+"_"+swissProt+" IS in set");						
				}
			}

			
			/*
			 * GROUNDING START
			 */
			CacheAlignment.loadCache(cacheForGroundingFileName);
			List<MutationInfo> mutationInfoList = alignMutations(scoreTable,normalizedMutations);//new ArrayList<MutationInfo>();
			//List<MutationInfo> mutationInfoList = new ArrayList<MutationInfo>();

			CacheAlignment.makeCachePersistent(cacheForGroundingFileName);
			/*
			Set<Integer> offsets = new HashSet();
			for(MutationInfo mi : mutationInfoList){
				offsets.add(mi.Offset);
			}
			
			// Ground singleton mutations
			for(String PA : scoreTable.getIndexByPack().keySet()) {
				String sequence = scoreTable.getIndexByPack().get(PA).get(0).getSequence();
				for(int i = 0; i < normalizedMutations.size(); i++) {
					Mutation m = normalizedMutations.get(i);
					//if(PA.equals("Q0P6M6"))log.debug(m.position+" "+sequence);
					for(Integer offset : offsets){
						//System.err.println("offset: "+offset+" "+m.position);
						if(m.position+offset <= sequence.length() && (m.position+offset)>0){						
							if (m.wildtype.charAt(0) == sequence.charAt(m.position+offset-1)) {
								List<Mutation> list = new ArrayList<Mutation>();
								list.add(m);
								mutationInfoList.add(new MutationInfo(PA,new LinkedList<Mutation>(list),0));
							}
						}
					}
					
					
				}
			}
			*/
			
			
			/*
			 *  GROUNDING END
			 */			
			Collections.sort(mutationInfoList);

			
			//log.debug("\n=============================\n");
			//for(MutationInfo mi : mutationInfoList)log.debug(mi.asString());
			//log.debug("\n/=============================\n");
			

		
			
			/* when filtering and extension is done, add the mutations previously removed, if their */
			/* wildtype residue and position is equal to any of the remaining mutations. */
			for(MutationInfo mi : mutationInfoList) {
				List<Mutation> mutationSameWT = new ArrayList<Mutation>();
				for(Mutation m : mi.Mutations) {
					try { 
						Set<String> uniqMutationsWNM = new HashSet<String>();
						uniqMutationsWNM.add(m.asString());
						if(sameWTMap!=null && m != null && sameWTMap.get(m.asString()) != null){
							for(Mutation sameWT : sameWTMap.get(m.asString())) {
								//System.out.println("sameWT: "+sameWT.asString());
								//System.out.println("m: "+m.asString());
								if(!uniqMutationsWNM.contains(sameWT.asString())){
									//System.out.println("AGA: "+m.asString());
									mutationSameWT.add(sameWT);
									uniqMutationsWNM.add(sameWT.asString());
								}						
							}
						}
						
					} catch(NullPointerException e) {
						e.printStackTrace();
					}
				}
				mi.Mutations.addAll(mutationSameWT);
			}			
			
			//log.debug("\n=============================\n");
			//for(MutationInfo mi : mutationInfoList)log.debug(mi.asString());
			//log.debug("\n/=============================\n");
			//log.debug("\n"+scoreTable.asStringTable());
			
			
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////
			// New score table.
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////

			// TODO possible to reduce the usage of memory by avoiding the cloning
			GroundingVariantTable newScoreTable = new GroundingVariantTable();
			for(MutationInfo mi : mutationInfoList){
				for(Iterator<GroundingVariant> iter = scoreTable.getIndexByPack().get(mi.PA).iterator();iter.hasNext();){
					GroundingVariant gv = iter.next();
					for(Mutation m : mi.Mutations){
						GroundingVariant gvClone = gv.clone();
						
						List<String> mutationList = new ArrayList<String>();
						for(Mutation mu : mi.Mutations){
							mutationList.add(mu.asString());
						}						
						gvClone.getMutations().addAll(mutationList);
						gvClone.setOffset(mi.Offset);
						gvClone.setMutation(m.asString());
						newScoreTable.addEntry(gvClone);
					}
				}
			}			
			
			//newScoreTable.display(GroundingVariant.sortByPac);
			
			//log.debug("\n\n\n\n!!!!!@@@@@@@@@@@@@@@@@@@@@@@@Table:");
			//log.debug("\n"+newScoreTable.asStringTable());
			
			newScoreTable = rankTable( doc, newScoreTable, proteinMentionTable, organismMentionTable, mutationMentionTable);
			
			Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByScore);
			log.debug("Table:");
			log.debug("\n"+newScoreTable.asStringTable());
			
			
			Set<GroundingVariant> newFinalGv = new HashSet<GroundingVariant>();
			Set<String> groundedMutations = new HashSet<String>();
			//for(String m : normalizedMutationsSet){
				for(GroundingVariant gv : newScoreTable.getEntries()){
					String mut = gv.getMutation();
					log.debug(mut);
					if(!groundedMutations.contains(mut)){
						log.debug(gv);
						newFinalGv.add(gv);
						groundedMutations.add(mut);
					}
					if(groundedMutations.size() == normalizedMutationsSet.size())break;
				}
			//}
			
				
				
				///////////////////////////////////////////////
				// pick GVs which have score as a max candidate
				List<GroundingVariant> gvsWithFromFinal = new ArrayList<GroundingVariant>();
				List<GroundingVariant> gvsNotFromFinal = new ArrayList<GroundingVariant>();		
				for(GroundingVariant gv : newScoreTable.getEntries()){
					if(newFinalGv.contains(gv))gvsWithFromFinal.add(gv);
					else gvsNotFromFinal.add(gv);
				}
				
				List<GroundingVariant> finalGvsss = new ArrayList<GroundingVariant>(gvsWithFromFinal);
				
///////////////////////////////////////
				// pick GVs which have "identical" pacs
				for(GroundingVariant gvWithMaxScore : gvsWithFromFinal){
					String geneNamesOfBestGv = "__"+StringUtils.arrayToDelimitedString(gvWithMaxScore.getGeneNames(),"__")+"__";
					for(GroundingVariant gv : gvsNotFromFinal){
						boolean sameGene= false;
						for(String s : gv.getGeneNames()){
							if(geneNamesOfBestGv.contains("__"+s+"__")){
								sameGene = true;
							}
						}
						
						
						boolean sameProtein= false;					
						//System.err.println(gv.getProteinName());
						//System.err.println(gvWithMaxScore.getProteinName());					
						Set<String> intersection = new HashSet<String>(gv.getProteinName());
						intersection.retainAll(gvWithMaxScore.getProteinName());
						//System.err.println("intersection: "+intersection);
						if(intersection.size() > 0) {
							sameProtein = true;
						}	
						
						
						boolean sameOrganism= false;
						Set<String> intersection2 = new HashSet<String>(gv.getOrganismName());
						intersection2.retainAll(gvWithMaxScore.getOrganismName());
						//System.err.println(gv.getPac());
						//System.err.println("intersection1: "+intersection2);
						//System.err.println("intersection2: "+gv.getOrganismName());
						//System.err.println("intersection3: "+gvWithMaxScore.getOrganismName());
						if(intersection2.size() > 0) {
							sameOrganism = true;
						}	
						//System.err.println(sameOrganism);
						// if conditions are satisfied, add gv to results
						if(gvWithMaxScore.getSequence().equals(gv.getSequence())
								&& (sameGene || sameProtein)
								&& sameOrganism
								//&& gvWithMaxScore.getOffset() == gv.getOffset()
								){//System.err.println(gv);
							
							//if(!gv.getPac().equals(gvWithMaxScore.getPac())){
								finalGvsss.add(gv);
							//}
						}
						//System.err.println();
					}
				}
				
			try {
				debugingResultsSb2.append(doc.getName() + "_" + newScoreTable.getEntries().get(0) + "\n");

			} catch (Exception e) {
				e.printStackTrace();
			}
		
	


			// DEBUG 2
			//for(Mutation m : normalizedMutations)log.debug(m.asString());

			for(String swissProt : 	statistics.getMap().get(docName)){
				if(removed.contains(swissProt))continue;				
				if(!newScoreTable.containsPac(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not AfterGrounding in set");
					statistics.notInAfterGrounding.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());	
					removed.add(swissProt);
				}else{	
					statistics.inAfterGrounding.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());
					log.debug(doc.getName()+"_"+swissProt+" IS AfterGrounding in set");						
				}
			}	
			
			// DEBUGGING in top of GV TABLE or not.
			newScoreTable.calcualteMaxScore();
			log.debug("scoreTable.MAXSCORE: "+newScoreTable.getMaxScore());
			for(String swissProt : 	statistics.getMap().get(docName)){
				if(!newScoreTable.pacInTop(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not in top");
					statistics.notInTopOfTable.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));					
				}else{	
					statistics.inTopOfTable.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));
					log.debug(doc.getName()+"_"+swissProt+" IS in top");						
				}
			}
			
			for(String swissProt : 	statistics.getMap().get(docName)){
				if(newScoreTable.containsPac(swissProt)){
					if(goldPacsForDebugging.containsKey(docName)){
						goldPacsForDebugging.get(docName).addAll(newScoreTable.getIndexByPack().get(swissProt));
					}else{
						List<GroundingVariant> list = new ArrayList<GroundingVariant>();
						list.addAll(newScoreTable.getIndexByPack().get(swissProt));
						goldPacsForDebugging.put(docName, list);
					}						
				}				
			}
			

			
			/*
			 * 
			 * 
			 * Clustering.
			 * 
			 * 
			 *
			GroundingClusterTable gcTable = clustering(newScoreTable, normalizedMutationsSet);
			for(String swissProt : 	stat.getMap().get(docName)){
				if(!gcTable.pacInTopOfOneOfClusters(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not in cluster top");
					notInTopOfOneOfCluster.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));					
				}else{	
					inTopOfOneOfCluster.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));
					log.debug(doc.getName()+"_"+swissProt+" IS in cluster top");						
				}
			}*
			
			
				
			// DEBUGGING
			for(String swissProt : 	stat.getMap().get(docName)){
				if(removed.contains(swissProt))continue;				
				if(!gcTable.containsPac(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not Filtered in set");
					notInFilteredSet.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());	
					removed.add(swissProt);
				}else{	
					inFilteredSet.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());
					log.debug(doc.getName()+"_"+swissProt+" IS Filtered in set");						
				}
			}*/
			
			
					
					
			
			/*
			 * Filter on scoreTable: Offset 0
			 *
			for(Iterator<GroundingVariant> iter = newScoreTable.iterator();iter.hasNext();){
				GroundingVariant sce = iter.next();
				if(sce.getOffset() != 0){
					iter.remove();
				}
			}*/
			
			
			/*
			 * Filter on scoreTable: NumberOfGroundedMutations
			 *			
			Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByNumberOfGroundedMutations);
			int maxNumberOfMutation = newScoreTable.getEntries().get(0).getMutations().size();
			for(Iterator<GroundingVariant> iter = newScoreTable.iterator();iter.hasNext();){
				GroundingVariant sce = iter.next();				
				if(sce.getMutations().size() != maxNumberOfMutation){
					iter.remove();
				}
			}*/
				
			
			/*AAA
			Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByScore);
			
			
			
				
			
			///////////////////////////////////////////////
			// pick GVs which have score as a max candidate
			List<GroundingVariant> gvsWithMaxScore = new ArrayList<GroundingVariant>();
			List<GroundingVariant> gvsNotWithMaxScore = new ArrayList<GroundingVariant>();

			float maxScore = newScoreTable.getEntries().get(0).score;			
			for(GroundingVariant gv : newScoreTable.getEntries()){
				if(gv.score == maxScore)gvsWithMaxScore.add(gv);
				else gvsNotWithMaxScore.add(gv);
			}
			
			List<GroundingVariant> finalGv = new ArrayList<GroundingVariant>(gvsWithMaxScore);
			
			*/
			
			

			/////////////////////////////////////
			// Check for more grounded mutations
			// newScoreTable is pre-sorted by score !!!
			/*/ Variant 1: unique P-M
			Set<String> remainMutations = new TreeSet<String>(normalizedMutationsSet);			
			remainMutations.removeAll(newScoreTable.getEntries().get(0).getMutations()) ;			
			//System.out.println("normalizedMutationsSet: "+normalizedMutationsSet);
			//System.out.println("gv.getMutations(): "+newScoreTable.getEntries().get(0).getMutations());			
			int finalLoop = 0;
			while(remainMutations.size() > 0){
				finalLoop = remainMutations.size();
				//System.err.println(remainMutations);
				boolean break2 = false;
				for(int k = remainMutations.size(); k > 0; k--){
					//System.err.println("k: "+k);

					for(GroundingVariant gv : newScoreTable.getEntries()){						
						if(gv.getMutations().size()==k){
							if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
								if(remainMutations.containsAll(gv.getMutations())){
									finalGv.add(gv);
									//System.err.println("remainMutations: "+remainMutations);
									//System.err.println("gv.getMutations(): "+gv.getMutations());
									remainMutations.removeAll(gv.getMutations());
									//System.err.println("remainMutations2: "+remainMutations);
									break2 = true;
								}
								
							}
						}
						if(break2)break;
					}
					if(break2)break;
					
				}
				if(finalLoop == remainMutations.size())break;				
			}*/
					
			
			//
			// second run with diff offset
			//
			/*
			while(remainMutations.size() > 0){
				finalLoop = remainMutations.size();
				System.err.println(remainMutations);
				boolean break2 = false;
				for(int k = remainMutations.size(); k > 0; k--){
					System.err.println("k: "+k);

					for(GroundingVariant gv : newScoreTable.getEntries()){						
						if(gv.getMutations().size()==k){
							//if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
								if(remainMutations.containsAll(gv.getMutations())){
									finalGv.add(gv);
									System.err.println("remainMutations: "+remainMutations);
									System.err.println("gv.getMutations(): "+gv.getMutations());
									remainMutations.removeAll(gv.getMutations());
									System.err.println("remainMutations2: "+remainMutations);
									break2 = true;
								}
								
							//}
						}
						if(break2)break;
					}
					if(break2)break;
					
				}
				if(finalLoop == remainMutations.size())break;				
			}
 			*/
			
 			/*// Second try to ground remain mutations
 			if(remainMutations.size() > 0){
 				for(GroundingVariant gv : newScoreTable.getEntries()){
 					//if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
 						Set<String> intersection = new TreeSet<String>(gv.getMutations());
 						intersection.removeAll(remainMutations);
 						if(intersection.size() == 0) {
 							finalGv.add(gv);	
 							remainMutations.removeAll(gv.getMutations());
 						}
 					//}
 				}
 			}*/
 			
			
 			
 			
 			
 			 /////////////////////////////////////
			// Check for more grounded mutations
			// newScoreTable is pre-sorted by score !!!
			/*AAA/ Variant 1: unique P-M
			Set<String> remainMutations = new TreeSet<String>(normalizedMutationsSet);
			remainMutations.removeAll(newScoreTable.getEntries().get(0).getMutations()) ;
 			for(GroundingVariant gv : newScoreTable.getEntries()){
				if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
					Set<String> intersection = new TreeSet<String>(gv.getMutations());
					intersection.removeAll(remainMutations);
					if(intersection.size() == 0) {
						finalGv.add(gv);	
						remainMutations.removeAll(gv.getMutations());
					}
				}
			}
				
 			
 			// Second try to ground remain mutations
 			if(remainMutations.size() > 0){
 				for(GroundingVariant gv : newScoreTable.getEntries()){
 					//if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
 						Set<String> intersection = new TreeSet<String>(gv.getMutations());
 						intersection.removeAll(remainMutations);
 						if(intersection.size() == 0) {
 							finalGv.add(gv);	
 							remainMutations.removeAll(gv.getMutations());
 						}
 					//}
 				}
 			}
 			
 			 
 			
			///////////////////////////////////////
			// pick GVs which have "identical" pacs
			for(GroundingVariant gvWithMaxScore : gvsWithMaxScore){
				String geneNamesOfBestGv = "__"+StringUtils.arrayToDelimitedString(gvWithMaxScore.getGeneNames(),"__")+"__";
				for(GroundingVariant gv : gvsNotWithMaxScore){
					boolean sameGene= false;
					for(String s : gv.getGeneNames()){
						if(geneNamesOfBestGv.contains("__"+s+"__")){
							sameGene = true;
						}
					}
					
					
					boolean sameProtein= false;					
					//System.err.println(gv.getProteinName());
					//System.err.println(gvWithMaxScore.getProteinName());					
					Set<String> intersection = new HashSet<String>(gv.getProteinName());
					intersection.retainAll(gvWithMaxScore.getProteinName());
					//System.err.println("intersection: "+intersection);
					if(intersection.size() > 0) {
						sameProtein = true;
					}	
					
					
					boolean sameOrganism= false;
					Set<String> intersection2 = new HashSet<String>(gv.getOrganismName());
					intersection2.retainAll(gvWithMaxScore.getOrganismName());
					//System.err.println(gv.getPac());
					//System.err.println("intersection1: "+intersection2);
					//System.err.println("intersection2: "+gv.getOrganismName());
					//System.err.println("intersection3: "+gvWithMaxScore.getOrganismName());
					if(intersection2.size() > 0) {
						sameOrganism = true;
					}	
					//System.err.println(sameOrganism);
					// if conditions are satisfied, add gv to results
					if(gvWithMaxScore.getSequence().equals(gv.getSequence())
							&& (sameGene || sameProtein)
							&& sameOrganism
							//&& gvWithMaxScore.getOffset() == gv.getOffset()
							){//System.err.println(gv);
						
						//if(!gv.getPac().equals(gvWithMaxScore.getPac())){
							finalGv.add(gv);
						//}
					}
					//System.err.println();
				}
			}
			
			
			*/
			
 			
 			
 			//--- Populate mutationTable with results (TODO Eliminate this step in the future) --- 
			for(GroundingVariant gv : finalGvsss) {
				for(String m : gv.getMutations()) {
					List<String> nm = new ArrayList<String>();
					nm.add(m);
			
					
					for(int i = 0; i < numberOfMutationMentions; i++) {
						if(nm.contains(mutationTable[i][3])) {
							if(mutationTable[i][4] != null){
								mutationTable[i][4] = mutationTable[i][4]+"__"+gv.getPac()+"_"+gv.getOffset();
							}else{
								mutationTable[i][4] = gv.getPac()+"_"+gv.getOffset();
							}
							
							mutationTable[i][5] = Integer.toString(gv.getOffset());
						}
					}
				}
			}
			
			
			

			
			
			
			
			
			
			
			/*
			 *  Add PAC:s as features to protein and mutation mentions
			 */
			Set<String> chosenPacs = new HashSet<String>();
			//log.info("Pmid\tStart\tEnd\tValue\t\t\tNormal\tGround\tOffset");
			for(int i = 0; i < numberOfMutationMentions; i++) {
				//log.info(doc.getName()+"\t"+mutationTable[i][0]+"\t"+mutationTable[i][1]+"\t"+mutationTable[i][2]+"\t"+mutationTable[i][3]+"\t\t\t"+mutationTable[i][4]+"\t"+mutationTable[i][5]);
				
				if(mutationTable[i][4] != null) {
					AnnotationSet as = doc.getAnnotations().get(Long.parseLong(mutationTable[i][0]),Long.parseLong(mutationTable[i][1]));
					Annotation a = (gate.Annotation)as.get("Mutation").iterator().next();
					FeatureMap features = Factory.newFeatureMap();  
					 
					//features.put("hasMentionedPosition",Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1)));
					features.put("hasCorrectPosition", Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-Integer.parseInt(mutationTable[i][5]));
					
					features.put("hasMentionedPosition",Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1)));
					//features.put("hasCorrectPosition", Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-0);
					
					
					List<String[]> pacsAndOffsets = new ArrayList<String[]>();
					String[] split = mutationTable[i][4].split("__");
					for(String s : split){
						String[] split2 = s.split("_");
						pacsAndOffsets.add(split2);
					}
					features.put("isGroundedTo", mutationTable[i][4]);
					features.put("hasWildtypeResidue", mutationTable[i][3].substring(0,1));
					features.put("hasMutantResidue", Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1)));
					// outputAS					
					doc.getAnnotations().add(Long.parseLong(mutationTable[i][0]), Long.parseLong(mutationTable[i][1]), "GroundedPointMutation",features);
			
					
					String mutStr = mutationTable[i][3].substring(0,1)+Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))+Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1));
					//System.out.println(mutStr+" is grounded to "+mutationTable[i][4]);
						
					//remember the accession numbers for later use (the protein annotation step).
					chosenPacs.add(mutationTable[i][4]);
				}
			}
					
			/*
			 * Add chosenPacs as a feature to the document.
			 */
			String pacFeature = new String();
			int nop = 0;
			for(String ac : chosenPacs) {
				if(nop == 0) {
					pacFeature=ac;
				} else {
					pacFeature+=","+ac;
				}
				nop++;
			}
			doc.getFeatures().put("pacs",pacFeature);
			doc.getFeatures().put(pacFeature,PASequenceMapping.get(pacFeature));

			//inputAS.removeAll(inputAS.get("MutationTemp"));
			//inputAS.removeAll(inputAS.get("ProteinName"));
			//inputAS.removeAll(inputAS.get("ProperProteinName"));
			//inputAS.removeAll(inputAS.get("Lookup"));
			
			if(con!=null)con.close();
			if(con2!=null)con2.close();		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

//	
	/**
	 * =========================================================================================
	 * GROUND
	 * =========================================================================================
	 * @param doc
	 * @param docName
	 * @throws InvalidOffsetException 
	 */
	public void ground(Document doc) throws InvalidOffsetException{
		if(doc.getFeatures().get("statistics")!=null){
			statistics = (AnalysisStatistics)doc.getFeatures().get("statistics");
		}
	
		for(Annotation proteinMention : doc.getAnnotations().get("ProperProteinName")){        
			Long s = proteinMention.getStartNode().getOffset();
			Long e = proteinMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			String start = Long.toString(s);
			String end = Long.toString(e);	
			if(doc.getAnnotations().get("NP",s,e).size() >0){
				Annotation np = (Annotation)doc.getAnnotations()
				.get("NP",s,e)
				.iterator()
				.next();
				
				AnnotationSet otherProteins = doc.getAnnotations().get("ProperProteinName",np.getStartNode().getOffset(),np.getEndNode().getOffset());

				if(otherProteins.size()>2){
					doc.getAnnotations().removeAll(otherProteins);				
				}
			}
			
			
			
		}

		
		/*
		 * Populate protein mention table.
		 */
        Map<String,List<int[]>> proteinMentionTable = new HashMap<String,List<int[]>>();
        for(Annotation proteinMention : doc.getAnnotations().get("ProperProteinName")){        
			Long s = proteinMention.getStartNode().getOffset();
			Long e = proteinMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			String start = Long.toString(s);
			String end = Long.toString(e);	
			
			// Synthetic test
			////name = getProteinNameAsInDB(name);
        	
			if (name.length() > 2 && name.matches(".*[A-Za-z]+.*") && !proteinNameExceptionList.contains(name)) {
        		if(proteinMentionTable.containsKey(name)){
        			proteinMentionTable.get(name).add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});
            	}else{
            		List<int[]> l = new ArrayList<int[]>();
            		l.add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});
            		proteinMentionTable.put(name, l);
            	}
        	}        	
        }       
        
		/*
		 * Filter by number of mentions.
		 */
        Iterator mapIter = proteinMentionTable.keySet().iterator();
        while ( mapIter.hasNext() ){
        	String key = (String)mapIter.next();
        	if(proteinMentionTable.get(key).size()<2 && key.length() < 4){
        		mapIter.remove();
        	}
        }       
        log.debug(Utils.mapAsPrettyString(sortMentionTable(proteinMentionTable)));
        
		/*
		 * Populate organism mention table.
		 */
        if(doc.getAnnotations().get("OrganismName").size()==0){
        	log.warn("No organisms in document: "+doc.getName());
        	return;
        }
        Map<String,List<int[]>> organismMentionTable = new HashMap<String,List<int[]>>();
        for(Annotation organismMention : doc.getAnnotations().get("OrganismName")){        
			Long s = organismMention.getStartNode().getOffset();
			Long e = organismMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			String start = Long.toString(s);
			String end = Long.toString(e);	
			name = getOrganismNameAsInDB(name);
        	if (name.length() > 2 && name.matches(".*[A-Za-z]+.*") && !organismNameExceptionList.contains(name)) {
        		if(organismMentionTable.containsKey(name)){
        			organismMentionTable.get(name).add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});
            	}else{
            		List<int[]> l = new ArrayList<int[]>();
            		l.add(new int[] {Integer.parseInt(start),Integer.parseInt(end)});     		
            		organismMentionTable.put(name, l);
            	}
        	}        	
        }        
        log.debug(Utils.mapAsPrettyString(sortMentionTable(organismMentionTable)));


		
		/*
		 * Populate mutation mention table.
		 */
		Set<String> normalizedMutationsSet = new TreeSet<String>();
		Map<String, List<int[]>> mutationMentionTable = new HashMap<String, List<int[]>>();
		for (Annotation mutationMention : doc.getAnnotations().get("Mutation")) {
			Long s = mutationMention.getStartNode().getOffset();
			Long e = mutationMention.getEndNode().getOffset();
			String start = Long.toString(s);
			String end = Long.toString(e);
			String normalizedMutationString = (String) mutationMention.getFeatures().get("wNm");
			normalizedMutationsSet.add(normalizedMutationString);
			if (mutationMentionTable.containsKey(normalizedMutationString)) {
				mutationMentionTable.get(normalizedMutationString).add(new int[] { Integer.parseInt(start), Integer.parseInt(end) });
			} else {
				List<int[]> l = new ArrayList<int[]>();
				l.add(new int[] { Integer.parseInt(start), Integer.parseInt(end) });
				mutationMentionTable.put(normalizedMutationString, l);
			}

		}
        log.debug(Utils.mapAsPrettyString(sortMentionTable(mutationMentionTable)));
        
        
		
        /*
		 * Jonas mutation table.
		 */
		
		List<gate.Annotation> mutationAnnotations = new ArrayList<gate.Annotation>((gate.AnnotationSet)doc.getAnnotations().get("Mutation"));
		int numberOfMutationMentions = mutationAnnotations.size();
		log.debug("number of mutation mentions: "+numberOfMutationMentions);
		String[][] mutationTable = new String[numberOfMutationMentions][7];
		Set<String> mutationMentions = new HashSet<String>();
		Set<String> mutationsWNMFormat = new TreeSet<String>();
		
		int mutationIndex = 0;
		
		Set<Mutation> normalizedMutationSet = new HashSet<Mutation>();
		
		for(gate.Annotation mutationAnnotation : mutationAnnotations) {
			Long s = mutationAnnotation.getStartNode().getOffset();
			Long e = mutationAnnotation.getEndNode().getOffset();
			
			mutationTable[mutationIndex][0] = Long.toString(s);
			mutationTable[mutationIndex][1] = Long.toString(e);
			mutationTable[mutationIndex][2] = doc.getContent().getContent(s, e).toString();

			String normalizedMutationString = (String)mutationAnnotation.getFeatures().get("wNm");			
			mutationTable[mutationIndex][3] = normalizedMutationString;
			
			//extract the different parts and add to a set of normalized mutations.
			String wildtype = normalizedMutationString.substring(0,1);
			String mutant = normalizedMutationString.substring(normalizedMutationString.length()-1);
			int position = Integer.parseInt(normalizedMutationString.substring(1,normalizedMutationString.length()-1));
			normalizedMutationSet.add(new Mutation(wildtype, position, mutant));
			
			mutationMentions.add(doc.getContent().getContent(s, e).toString());
			mutationsWNMFormat.add(normalizedMutationString);
			mutationIndex++;
		}
		
					
		//make a list of the set
		List<Mutation> normalizedMutations = new ArrayList<Mutation>(normalizedMutationSet);		
		//sort the mutations, can't really remember why anymore.
		Collections.sort(normalizedMutations);
		
		/*/ save statistics about by MutationFinder found mutations
        try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("document-mutations"), true));
			bw.write(doc.getName()+": "+mutationsWNMFormat);			
			bw.write("\n");
			bw.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}*/

		
		
		//Make sure mutations with the same WT residue and the same position are treated as one single mutation.
		//When the grounding and the filtering is done, they are again treated as different mutations
		Map<String,List<Mutation>> sameWTMap = new HashMap<String, List<Mutation>>();
		List<Integer> listedPositions = new ArrayList<Integer>();
		for(int i = 0; i < normalizedMutations.size(); i++) {
			if(!listedPositions.contains(i)) {
				List<Mutation> mList = new ArrayList<Mutation>();
				for(int j = i+1; j < normalizedMutations.size(); j++) {
					//two mutations are considered equal if they have the same WT residue and the same position
					if(normalizedMutations.get(i).equals(normalizedMutations.get(j))) {
						mList.add(new Mutation(normalizedMutations.get(j).wildtype, normalizedMutations.get(j).position, normalizedMutations.get(j).mutation));
						listedPositions.add(j);
					}
				}
				if (mList.size() > 0) {
					sameWTMap.put(normalizedMutations.get(i).asString(), mList);
				}
			}
		}

		
		
		
		// @AK.Debug
		//for(Mutation mutation : normalizedMutations){
		    //log.info("\n\nnormalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
		 //}

		//remove "dublicates"
		for(int i = normalizedMutations.size()-1; i > -1; i--) {
			if(listedPositions.contains(i)) {
				normalizedMutations.remove(i);
			}
		}

		// @AK.Debug
	    for(Mutation mutation : normalizedMutations){
		    log.debug("normalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
		 }

		
		///log.debug(stringTableAsString(mutationTable));
        
        
		/*
		 * 
		 * 
		 * 
		 * 
		 * ScoreTable AND Grounding.
		 * 
		 * 
		 * 
		 * 
		 */
        GroundingVariantTable scoreTable = new GroundingVariantTable();
        
		try {			
			Properties pro = new Properties();
            pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
            boolean onlySwissprot = false;
            if(pro.getProperty("ONLY_SWISSPROT").equals("true")){
            	onlySwissprot = true;
            }
            log.debug("ONLY_SWISSPROT: "+onlySwissprot);
            boolean filterOne = false;
            if(pro.getProperty("FILTER_ONE").equals("true")){
            	filterOne = true;
            }
            log.debug("FILTER_ONE: "+filterOne);
            
            
            String DATABASE_URL = pro.getProperty("DATABASE_URL");
            String USERNAME = pro.getProperty("DATABASE_USERNAME");; 
			String PASSWORD = pro.getProperty("DATABASE_PASSWORD");;
            
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/swissprot";
			//String DATABASE_URL = "jdbc:mysql://sadi2:3306/uniprot";
			//String DATABASE_URL = "jdbc:mysql://localhost:3306/uniprot";
			//String DATABASE2_URL = "jdbc:mysql://localhost:3306/trembl2";
			//String DATABASE2_URL = "jdbc:mysql://sadi2:3306/uniprot3";

			//String USERNAME = "root"; 
			//String PASSWORD = "deduction";

			Class.forName("com.mysql.jdbc.Driver");		
			con2 = DriverManager.getConnection("jdbc:mysql://"+DATABASE_URL, USERNAME, PASSWORD);
			//con2 = DriverManager.getConnection(DATABASE2_URL, USERNAME, PASSWORD);
			
			String docName = doc.getName().split("\\.")[0];
			log.debug("docName: "+docName);
			doc.getAnnotations().removeAll((gate.AnnotationSet)doc.getAnnotations().get("GroundedPointMutation"));
							
			
			/*
			 * Build substring for organisms.			
			 */
			StringBuilder organismSubString = new StringBuilder();	
			for(String o : organismMentionTable.keySet()) {
				log.debug(o);
				if(o.length()<2)continue; // TODO may be < 3 (test it)
				organismSubString.append("organismname3.name='"+o+"' OR ");				
			}
			//System.out.println("organismSubString: "+organismSubString);
			//if(organismSubString.length()>4)
				organismSubString.delete(organismSubString.length()-4, organismSubString.length());

			
			Set<String> set = new TreeSet<String>();	
		
			
			/*
			 * Receive PAC candidates from DB.
			 */
			if(cacheFileName!=null)	CacheQuerying.loadCache(cacheFileName);
			long queryTime = System.currentTimeMillis();
			for (String proteinName : proteinMentionTable.keySet()) {

				StringBuilder finalQuerySb = new StringBuilder();
				finalQuerySb.append("SELECT DISTINCT *  " +
						"FROM proteinname, organismname3, pac, sequence, genenames2  " +
						"WHERE (match(proteinname.name) against (");

				String mentionStr = proteinName.toLowerCase();
				String mentionStrForSqlQuery = mentionStr.replaceAll("'", "''");
				finalQuerySb.append("'" + mentionStrForSqlQuery + "'");
				
				//
				// EXACT MATCH OF PROTEIN NAMES
				//
				finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStrForSqlQuery + "%') AND (");		
				//finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStr + "') AND (");					

				/*
				 * WRONG WAY
				if(mentionStr.length()>5){
					finalQuerySb.append("'" + mentionStr + "'");
					finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStr + "%') AND (");					
				}else {
					finalQuerySb.append("'" + mentionStr + "'");
					finalQuerySb.append(")  and proteinname.name LIKE '" + mentionStr + "') AND (");					
				}
				Number of documents: 38
				numberOfAllMutations_Eval: 178
				numberOfAllMutations_Grounded: 156
				numberOfAllMutations_GroundedCorrect: 97
				Precision: 0.6217949
				Recall: 0.5449438
				GroundingRate: 0.8764045
				*/
				
				finalQuerySb.append(organismSubString.toString());
				finalQuerySb.append(") AND (proteinname.id=pac.id) AND (organismname3.id=pac.id) AND (sequence.id=pac.id) AND (genenames2.id=pac.id);");
				log.debug("FINAL QUERY: " + finalQuerySb.toString());

				
				// If query in cache, get results from cache. Else send query to DB.
				Set<String> queryResult = null;
				if(CacheQuerying.cache != null && CacheQuerying.cache.containsKey(finalQuerySb.toString())){
					queryResult = CacheQuerying.cache.get(finalQuerySb.toString());
				}else{
					queryResult = new HashSet<String>();
					Statement primaryAccSt = con2.createStatement();
					ResultSet primaryAccRs = primaryAccSt.executeQuery(finalQuerySb.toString());
					while (primaryAccRs.next()) {
						String geneNames = "XXX";
						if(!primaryAccRs.getString("genenames2.name").equals(""))geneNames=primaryAccRs.getString("genenames2.name");
						String s = primaryAccRs.getString("pac.pac") + "##" 
							+ primaryAccRs.getString("proteinname.name") + "##"
							+ primaryAccRs.getString("organismname3.name") + "##"
							+ primaryAccRs.getString("sequence.sequence") + "##"
							+ primaryAccRs.getString("pac.id") + "##"
							+ geneNames;						
						queryResult.add(s);		//System.err.println(s);									
					}
					if(CacheQuerying.cache != null)	CacheQuerying.cache.put(finalQuerySb.toString(), queryResult);						
					corpusQueryTime = corpusQueryTime + (System.currentTimeMillis() - queryTime);
				}	
					
				//
				// Populate score table.
				//
				Set<String> pacsCandidates = new HashSet<String>();

				for(String s : queryResult){
					String[] split = s.split("##");
					String pac = split[0]; pacsCandidates.add(pac);	
					String proteinNameInDb = split[1];

					
					// Activate to ground only to SWISS-PROT
					if(onlySwissprot){
						if(Integer.parseInt(split[4]) > 532792) continue;
					}
					
					// development debugging
					if(statistics != null){
						if(Integer.parseInt(split[4]) > 532792
								&& statistics
								.getMap()
								.get(
										doc
										.getName())
										.contains(pac)) {
							log.debug("from trembl: "+doc.getName()+"_"+pac);
						}
					}
					
					
					// if scoreTable contains PAC, add protein name from DB
					// TODO here score possible: frequency of retrieved from DB
					if(scoreTable.getIndexByPack().containsKey(pac)){						
						if(scoreTable.getIndexByPack().get(pac).get(0).getProteinNameInText().containsKey(mentionStr)){
							scoreTable.getIndexByPack().get(pac).get(0).getProteinNameInText().get(mentionStr).add(proteinNameInDb);
							scoreTable.getIndexByPack().get(pac).get(0).getProteinName().add(proteinNameInDb);
						}else{
							Set<String> newSet = new HashSet<String>();
							newSet.add(proteinNameInDb);
							scoreTable.getIndexByPack().get(pac).get(0).getProteinNameInText().put(mentionStr, newSet);
							scoreTable.getIndexByPack().get(pac).get(0).getProteinName().add(proteinNameInDb);
						}
						String organismName = split[2].toLowerCase();
						//if(!scoreTable.getIndexByPack().get(pac).get(0).getOrganismName().contains(organismName)){
							scoreTable.getIndexByPack().get(pac).get(0).getOrganismName().add(organismName);
						//}
					}else{
						Map<String,Set<String>> proteinNameInTextSet = new LinkedHashMap<String,Set<String>>();
						Set<String> namesInDb = new HashSet<String>();
						namesInDb.add(proteinNameInDb);
						proteinNameInTextSet.put(mentionStr,namesInDb);
						Set<String> proteinNameSet = new LinkedHashSet<String>();
						proteinNameSet.add(proteinNameInDb);
						
						Set<String> organismNameSet = new LinkedHashSet<String>();
						organismNameSet.add(split[2].toLowerCase());
						
						GroundingVariant gv = new GroundingVariant(
								pac,
								proteinNameInTextSet,
								proteinNameSet,
								organismNameSet, 		// organism names
								split[3], 		 		// sequence
								split[4],				// id
								split[5].split("__") 	// gene names
								);
						scoreTable.addEntry(gv);
						set.add(pac);
					}
				}
				log.debug("queryResult: " + pacsCandidates);
			}						
			CacheQuerying.makeCachePersistent(cacheFileName);
			
	        
			//scoreTable.display(GroundingVariant.sortByPac);



			// DEBUGGING.
			Set<String> removed = new TreeSet<String>();	
			if(statistics != null){
				for(String swissProt : 	statistics
						.getMap()
						.get(
								docName)){
					if(!scoreTable.containsPac(swissProt)){
						log.debug(doc.getName()+"_"+swissProt+" not in set");
						statistics.notInForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_"+proteinMentionTable.keySet());	
						removed.add(swissProt);
					}else{	
						statistics.inForGroundingSet.add(doc.getName()+"_"+swissProt+"_"+set.size()+"_"+proteinMentionTable.keySet());
						log.debug(doc.getName()+"_"+swissProt+" IS in set");						
					}
				}
			}
			

			
			/*
			 * GROUNDING START
			 */
			if(cacheForGroundingFileName != null) CacheAlignment.loadCache(cacheForGroundingFileName);
			List<MutationInfo> mutationInfoList = alignMutations(scoreTable,normalizedMutations);//new ArrayList<MutationInfo>();
			//List<MutationInfo> mutationInfoList = new ArrayList<MutationInfo>();

			if(cacheForGroundingFileName != null) CacheAlignment.makeCachePersistent(cacheForGroundingFileName);
			/*
			Set<Integer> offsets = new HashSet();
			for(MutationInfo mi : mutationInfoList){
				offsets.add(mi.Offset);
			}
			
			// Ground singleton mutations
			for(String PA : scoreTable.getIndexByPack().keySet()) {
				String sequence = scoreTable.getIndexByPack().get(PA).get(0).getSequence();
				for(int i = 0; i < normalizedMutations.size(); i++) {
					Mutation m = normalizedMutations.get(i);
					//if(PA.equals("Q0P6M6"))log.debug(m.position+" "+sequence);
					for(Integer offset : offsets){
						//System.err.println("offset: "+offset+" "+m.position);
						if(m.position+offset <= sequence.length() && (m.position+offset)>0){						
							if (m.wildtype.charAt(0) == sequence.charAt(m.position+offset-1)) {
								List<Mutation> list = new ArrayList<Mutation>();
								list.add(m);
								mutationInfoList.add(new MutationInfo(PA,new LinkedList<Mutation>(list),0));
							}
						}
					}
					
					
				}
			}
			*/
			
			
			/*
			 *  GROUNDING END
			 */			
			Collections.sort(mutationInfoList);

			
			//log.debug("\n=============================\n");
			//for(MutationInfo mi : mutationInfoList)log.debug(mi.asString());
			//log.debug("\n/=============================\n");
			

		
			
			/* when filtering and extension is done, add the mutations previously removed, if their */
			/* wildtype residue and position is equal to any of the remaining mutations. */
			for(MutationInfo mi : mutationInfoList) {
				List<Mutation> mutationSameWT = new ArrayList<Mutation>();
				for(Mutation m : mi.Mutations) {
					try { 
						Set<String> uniqMutationsWNM = new HashSet<String>();
						uniqMutationsWNM.add(m.asString());
						if(sameWTMap!=null && m != null && sameWTMap.get(m.asString()) != null){
							for(Mutation sameWT : sameWTMap.get(m.asString())) {
								//System.out.println("sameWT: "+sameWT.asString());
								//System.out.println("m: "+m.asString());
								if(!uniqMutationsWNM.contains(sameWT.asString())){
									//System.out.println("AGA: "+m.asString());
									mutationSameWT.add(sameWT);
									uniqMutationsWNM.add(sameWT.asString());
								}						
							}
						}
						
					} catch(NullPointerException e) {
						e.printStackTrace();
					}
				}
				mi.Mutations.addAll(mutationSameWT);
			}			
			
			//log.debug("\n=============================\n");
			//for(MutationInfo mi : mutationInfoList)log.debug(mi.asString());
			//log.debug("\n/=============================\n");
			//log.debug("\n"+scoreTable.asStringTable());
			
			
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////
			// New score table.
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////

			// TODO possible to reduce the usage of memory by avoiding the cloning
			GroundingVariantTable newScoreTable = new GroundingVariantTable();
			for(MutationInfo mi : mutationInfoList){
				for(Iterator<GroundingVariant> iter = scoreTable.getIndexByPack().get(mi.PA).iterator();iter.hasNext();){
					GroundingVariant gv = iter.next();
					GroundingVariant gvClone = gv.clone();
					List<String> mutationList = new ArrayList<String>();
					for(Mutation m : mi.Mutations){
						mutationList.add(m.asString());
					}
					gvClone.getMutations().addAll(mutationList);
					gvClone.setOffset(mi.Offset);
					newScoreTable.addEntry(gvClone);
				}
			}			
			//newScoreTable.display(GroundingVariant.sortByPac);
			
			newScoreTable = rankTable( doc, newScoreTable, proteinMentionTable, organismMentionTable, mutationMentionTable);
			
			
			
			try{
				debugingResultsSb2.append(doc.getName()+"_"+newScoreTable.getEntries().get(0)+"\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			
			log.debug("Table:");
			log.debug("\n"+newScoreTable.asStringTable());


			// DEBUG 2
			for(Mutation m : normalizedMutations)log.debug(m.asString());
			if(statistics != null){
				for(String swissProt : 	statistics.getMap().get(docName)){
					if(removed.contains(swissProt))continue;				
					if(!newScoreTable.containsPac(swissProt)){
						log.debug(doc.getName()+"_"+swissProt+" not AfterGrounding in set");
						statistics.notInAfterGrounding.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());	
						removed.add(swissProt);
					}else{	
						statistics.inAfterGrounding.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());
						log.debug(doc.getName()+"_"+swissProt+" IS AfterGrounding in set");						
					}
				}	
				
				// DEBUGGING in top of GV TABLE or not.
				newScoreTable.calcualteMaxScore();
				log.debug("scoreTable.MAXSCORE: "+newScoreTable.getMaxScore());
				for(String swissProt : 	statistics.getMap().get(docName)){
					if(!newScoreTable.pacInTop(swissProt)){
						log.debug(doc.getName()+"_"+swissProt+" not in top");
						statistics.notInTopOfTable.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));					
					}else{	
						statistics.inTopOfTable.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));
						log.debug(doc.getName()+"_"+swissProt+" IS in top");						
					}
				}
				
				for(String swissProt : 	statistics.getMap().get(docName)){
					if(newScoreTable.containsPac(swissProt)){
						if(goldPacsForDebugging.containsKey(docName)){
							goldPacsForDebugging.get(docName).addAll(newScoreTable.getIndexByPack().get(swissProt));
						}else{
							List<GroundingVariant> list = new ArrayList<GroundingVariant>();
							list.addAll(newScoreTable.getIndexByPack().get(swissProt));
							goldPacsForDebugging.put(docName, list);
						}						
					}				
				}
			}
			
			

			
			/*
			 * 
			 * 
			 * Clustering.
			 * 
			 * 
			 *
			GroundingClusterTable gcTable = clustering(newScoreTable, normalizedMutationsSet);
			for(String swissProt : 	stat.getMap().get(docName)){
				if(!gcTable.pacInTopOfOneOfClusters(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not in cluster top");
					notInTopOfOneOfCluster.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));					
				}else{	
					inTopOfOneOfCluster.add(doc.getName()+"_"+swissProt+"_"+set.size()+newScoreTable.getIndexByPack().get(swissProt));
					log.debug(doc.getName()+"_"+swissProt+" IS in cluster top");						
				}
			}*
			
			
				
			// DEBUGGING
			for(String swissProt : 	stat.getMap().get(docName)){
				if(removed.contains(swissProt))continue;				
				if(!gcTable.containsPac(swissProt)){
					log.debug(doc.getName()+"_"+swissProt+" not Filtered in set");
					notInFilteredSet.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());	
					removed.add(swissProt);
				}else{	
					inFilteredSet.add(doc.getName()+"_"+swissProt+"_"+newScoreTable.getEntries().size()+"_"+proteinMentionTable.keySet());
					log.debug(doc.getName()+"_"+swissProt+" IS Filtered in set");						
				}
			}*/
			
			
					
					
			
			/*
			 * Filter on scoreTable: Offset 0
			 *
			for(Iterator<GroundingVariant> iter = newScoreTable.iterator();iter.hasNext();){
				GroundingVariant sce = iter.next();
				if(sce.getOffset() != 0){
					iter.remove();
				}
			}*/
			
			
			/*
			 * Filter on scoreTable: NumberOfGroundedMutations
			 *			
			Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByNumberOfGroundedMutations);
			int maxNumberOfMutation = newScoreTable.getEntries().get(0).getMutations().size();
			for(Iterator<GroundingVariant> iter = newScoreTable.iterator();iter.hasNext();){
				GroundingVariant sce = iter.next();				
				if(sce.getMutations().size() != maxNumberOfMutation){
					iter.remove();
				}
			}*/
						
			Collections.sort(newScoreTable.getEntries(), GroundingVariant.sortByScore);
			
			
			
				
			
			///////////////////////////////////////////////
			// pick GVs which have score as a max candidate
			List<GroundingVariant> gvsWithMaxScore = new ArrayList<GroundingVariant>();
			List<GroundingVariant> gvsNotWithMaxScore = new ArrayList<GroundingVariant>();

			float maxScore = newScoreTable.getEntries().get(0).score;			
			for(GroundingVariant gv : newScoreTable.getEntries()){
				if(gv.score == maxScore)gvsWithMaxScore.add(gv);
				else gvsNotWithMaxScore.add(gv);
			}
			
			List<GroundingVariant> finalGv = new ArrayList<GroundingVariant>(gvsWithMaxScore);
			
			
			
			

			/////////////////////////////////////
			// Check for more grounded mutations
			// newScoreTable is pre-sorted by score !!!
			// Variant 1: unique P-M
			Set<String> remainMutations = new TreeSet<String>(normalizedMutationsSet);			
			remainMutations.removeAll(newScoreTable.getEntries().get(0).getMutations()) ;			
			//System.out.println("normalizedMutationsSet: "+normalizedMutationsSet);
			//System.out.println("gv.getMutations(): "+newScoreTable.getEntries().get(0).getMutations());			
			int finalLoop = 0;
			while(remainMutations.size() > 0){
				finalLoop = remainMutations.size();
				//System.err.println(remainMutations);
				boolean break2 = false;
				for(int k = remainMutations.size(); k > 0; k--){
					//System.err.println("k: "+k);

					for(GroundingVariant gv : newScoreTable.getEntries()){						
						if(gv.getMutations().size()==k){
							if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
								if(remainMutations.containsAll(gv.getMutations())){
									finalGv.add(gv);
									//System.err.println("remainMutations: "+remainMutations);
									//System.err.println("gv.getMutations(): "+gv.getMutations());
									remainMutations.removeAll(gv.getMutations());
									//System.err.println("remainMutations2: "+remainMutations);
									break2 = true;
								}
								
							}
						}
						if(break2)break;
					}
					if(break2)break;
					
				}
				if(finalLoop == remainMutations.size())break;				
			}
				 			
			// Second try to ground remain mutations
 			if(remainMutations.size() > 0){
 				for(GroundingVariant gv : newScoreTable.getEntries()){
 					//if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
 						Set<String> intersection = new TreeSet<String>(gv.getMutations());
 						intersection.removeAll(remainMutations);
 						if(intersection.size() == 0) {
 							finalGv.add(gv);	
 							remainMutations.removeAll(gv.getMutations());
 						}
 					//}
 				}
 			}
 			
			
			//
			// second run with diff offset
			//
			/*
			while(remainMutations.size() > 0){
				finalLoop = remainMutations.size();
				System.err.println(remainMutations);
				boolean break2 = false;
				for(int k = remainMutations.size(); k > 0; k--){
					System.err.println("k: "+k);

					for(GroundingVariant gv : newScoreTable.getEntries()){						
						if(gv.getMutations().size()==k){
							//if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
								if(remainMutations.containsAll(gv.getMutations())){
									finalGv.add(gv);
									System.err.println("remainMutations: "+remainMutations);
									System.err.println("gv.getMutations(): "+gv.getMutations());
									remainMutations.removeAll(gv.getMutations());
									System.err.println("remainMutations2: "+remainMutations);
									break2 = true;
								}
								
							//}
						}
						if(break2)break;
					}
					if(break2)break;
					
				}
				if(finalLoop == remainMutations.size())break;				
			}
 			*/
			

 			
			
 			
 			
 			
 			 /////////////////////////////////////
			// Check for more grounded mutations
			// newScoreTable is pre-sorted by score !!!
			/*/ Variant 1: unique P-M
			Set<String> remainMutations = new TreeSet<String>(normalizedMutationsSet);
			remainMutations.removeAll(newScoreTable.getEntries().get(0).getMutations()) ;
 			for(GroundingVariant gv : newScoreTable.getEntries()){
				if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
					Set<String> intersection = new TreeSet<String>(gv.getMutations());
					intersection.removeAll(remainMutations);
					if(intersection.size() == 0) {
						finalGv.add(gv);	
						remainMutations.removeAll(gv.getMutations());
					}
				}
			}
				
 			
 			// Second try to ground remain mutations
 			if(remainMutations.size() > 0){
 				for(GroundingVariant gv : newScoreTable.getEntries()){
 					//if(gv.getOffset() == newScoreTable.getEntries().get(0).getOffset()){
 						Set<String> intersection = new TreeSet<String>(gv.getMutations());
 						intersection.removeAll(remainMutations);
 						if(intersection.size() == 0) {
 							finalGv.add(gv);	
 							remainMutations.removeAll(gv.getMutations());
 						}
 					//}
 				}
 			}
 			*/
 			 
 			
			///////////////////////////////////////
			// pick GVs which have "identical" pacs
			for(GroundingVariant gvWithMaxScore : gvsWithMaxScore){
				String geneNamesOfBestGv = "__"+StringUtils.arrayToDelimitedString(gvWithMaxScore.getGeneNames(),"__")+"__";
				for(GroundingVariant gv : gvsNotWithMaxScore){
					boolean sameGene= false;
					for(String s : gv.getGeneNames()){
						if(geneNamesOfBestGv.contains("__"+s+"__")){
							sameGene = true;
						}
					}
					
					
					boolean sameProtein= false;					
					//System.err.println(gv.getProteinName());
					//System.err.println(gvWithMaxScore.getProteinName());					
					Set<String> intersection = new HashSet<String>(gv.getProteinName());
					intersection.retainAll(gvWithMaxScore.getProteinName());
					//System.err.println("intersection: "+intersection);
					if(intersection.size() > 0) {
						sameProtein = true;
					}	
					
					
					boolean sameOrganism= false;
					Set<String> intersection2 = new HashSet<String>(gv.getOrganismName());
					intersection2.retainAll(gvWithMaxScore.getOrganismName());
					//System.err.println(gv.getPac());
					//System.err.println("intersection1: "+intersection2);
					//System.err.println("intersection2: "+gv.getOrganismName());
					//System.err.println("intersection3: "+gvWithMaxScore.getOrganismName());
					if(intersection2.size() > 0) {
						sameOrganism = true;
					}	
					//System.err.println(sameOrganism);
					// if conditions are satisfied, add gv to results
					if(gvWithMaxScore.getSequence().equals(gv.getSequence())
							&& (sameGene || sameProtein)
							&& sameOrganism
							//&& gvWithMaxScore.getOffset() == gv.getOffset()
							){//System.err.println(gv);
						
						//if(!gv.getPac().equals(gvWithMaxScore.getPac())){
						
						boolean containAlready = false;
						for(GroundingVariant gvAlready: finalGv){
							if(gvAlready.getPac().equals(gv.getPac())){
								containAlready = true;
								break;
							}
						}
						if(!containAlready)
							finalGv.add(gv);
						//}
					}
					//System.err.println();
				}
			}
			
			
			
			
 			
 			
 			//--- Populate mutationTable with results (TODO Eliminate this step in the future) --- 
			for(GroundingVariant gv : finalGv) {
				for(String m : gv.getMutations()) {
					List<String> nm = new ArrayList<String>();
					nm.add(m);
			
					
					for(int i = 0; i < numberOfMutationMentions; i++) {
						if(nm.contains(mutationTable[i][3])) {
							if(mutationTable[i][4] != null){
								mutationTable[i][4] = mutationTable[i][4]+"__"+gv.getPac()+"_"+gv.getOffset();
							}else{
								mutationTable[i][4] = gv.getPac()+"_"+gv.getOffset();
							}
							
							mutationTable[i][5] = Integer.toString(gv.getOffset());
							mutationTable[i][6] = gv.getSequence();

						}
					}
				}
			}
			
			
			//System.err.println(Utils.mapAsPrettyString(proteinMentionTable));


			for(GroundingVariant gv : finalGv) {
				//System.err.println(gv.asString());
				for(String protNameInText : gv.getProteinNameInText().keySet()){
					//System.err.println("protNameInText: "+protNameInText);
					if(proteinMentionTable.containsKey(protNameInText)){
						for(int[] location : proteinMentionTable.get(protNameInText)){
							//System.err.println("location: "+location[0]+location[1]);

							FeatureMap featuresForMutatedProtein = Factory.newFeatureMap();  
							featuresForMutatedProtein.put("pac", gv.getPac());
							featuresForMutatedProtein.put("sequence", gv.getSequence());
							//System.err.println("MutatedProtein: ");
							doc.getAnnotations().add((long)location[0], (long)location[1], "MutatedProtein",featuresForMutatedProtein);
						}
					}
				}
				
			}
			
			
			
			
			
			
			/*
			 *  Add PAC:s as features to protein and mutation mentions
			 */
			Set<String> chosenPacs = new HashSet<String>();
			//log.info("Pmid\tStart\tEnd\tValue\t\t\tNormal\tGround\tOffset");
			for(int i = 0; i < numberOfMutationMentions; i++) {
				//log.info(doc.getName()+"\t"+mutationTable[i][0]+"\t"+mutationTable[i][1]+"\t"+mutationTable[i][2]+"\t"+mutationTable[i][3]+"\t\t\t"+mutationTable[i][4]+"\t"+mutationTable[i][5]);
				
				if(mutationTable[i][4] != null) {
					AnnotationSet as = doc.getAnnotations().get(Long.parseLong(mutationTable[i][0]),Long.parseLong(mutationTable[i][1]));
					Annotation a = (gate.Annotation)as.get("Mutation").iterator().next();
					FeatureMap features = Factory.newFeatureMap();  
					 
					//features.put("hasMentionedPosition",Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1)));
					features.put("hasCorrectPosition", Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-Integer.parseInt(mutationTable[i][5]));
					
					features.put("hasMentionedPosition",Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1)));
					//features.put("hasCorrectPosition", Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-0);
					
					/*
					List<String[]> pacsAndOffsets = new ArrayList<String[]>();
					String[] split = mutationTable[i][4].split("__");
					for(String s : split){
						String[] split2 = s.split("_");
						pacsAndOffsets.add(split2);
					}*/
					
					/*
					List<String> pacsAndOffsets = new ArrayList<String>();
					String[] split = mutationTable[i][4].split("__");
					for(String s : split){
						pacsAndOffsets.add(s);
					}*/
					
					features.put("isGroundedTo", mutationTable[i][4]);
					features.put("hasWildtypeResidue", mutationTable[i][3].substring(0,1));
					features.put("hasMutantResidue", Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1)));
					features.put("wtSequence", mutationTable[i][6]);
					
					/*
					features.put("zmSequence", 
							replaceCharAt(
									mutationTable[i][6],
									(Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))-Integer.parseInt(mutationTable[i][5])),
									mutationTable[i][3].charAt(mutationTable[i][3].length()-1)
							));
					*/
					
					features.put("zmSequence", replaceCharAt(
							(String)features.get("wtSequence"),
							((Integer)features.get("hasCorrectPosition")-1),
							((String)features.get("hasMutantResidue")).charAt(0)));
							
					System.out.println(features.get("hasWildtypeResidue"));
					System.out.println(features.get("hasMutantResidue"));
					System.out.println(features.get("hasCorrectPosition"));
					System.out.println(features.get("wtSequence"));
					System.out.println(features.get("zmSequence"));
					// outputAS					
					doc.getAnnotations().add(Long.parseLong(mutationTable[i][0]), Long.parseLong(mutationTable[i][1]), "GroundedPointMutation",features);
			
					
					String mutStr = mutationTable[i][3].substring(0,1)+Integer.parseInt(mutationTable[i][3].substring(1,mutationTable[i][3].length()-1))+Character.toString(mutationTable[i][3].charAt(mutationTable[i][3].length()-1));
					//System.out.println(mutStr+" is grounded to "+mutationTable[i][4]);

					
					
					//remember the accession numbers for later use (the protein annotation step).
					chosenPacs.add(mutationTable[i][4]);
				}
			}
					
			
			for(String proteinName : proteinMentionTable.keySet()) {
				for(int[] ia :proteinMentionTable.get(proteinName)){
					for(String ac : chosenPacs) {
						if (proteinMentionTable.get(proteinName).contains(ac)) {
							AnnotationSet as = doc.getAnnotations().get(Long.parseLong(Integer.toString(ia[0])),Long.parseLong(Integer.toString(ia[1])));
							//Annotation a = (gate.Annotation)as.get("ProteinName").iterator().next();
							FeatureMap features = Factory.newFeatureMap();
							features.put("mentionsProtein",ac);
							doc.getAnnotations().add(Long.parseLong(Integer.toString(ia[0])),Long.parseLong(Integer.toString(ia[1])), "ProteinMention", features);
						}
					}
				}
				
			}
			
			/*
			 * Add chosenPacs as a feature to the document.
			 */
			String pacFeature = new String();
			int nop = 0;
			for(String ac : chosenPacs) {
				if(nop == 0) {
					pacFeature=ac;
				} else {
					pacFeature+=","+ac;
				}
				nop++;
			}
			doc.getFeatures().put("pacs",pacFeature);
			doc.getFeatures().put(pacFeature,PASequenceMapping.get(pacFeature));

			//inputAS.removeAll(inputAS.get("MutationTemp"));
			//inputAS.removeAll(inputAS.get("ProteinName"));
			//inputAS.removeAll(inputAS.get("ProperProteinName"));
			//inputAS.removeAll(inputAS.get("Lookup"));
			
			if(con!=null)con.close();
			if(con2!=null)con2.close();		
			
			if(doc.getFeatures().get("statistics")!=null){
				doc.getFeatures().put("statistics",statistics);
			}
			//log.info(poolResultsAsString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	 public static String replaceCharAt(String s, int pos, char c) {
		    return s.substring(0, pos) + c + s.substring(pos + 1);
		  }
	
}


