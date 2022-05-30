package ca.unbsj.cbakerlab.mutation_impact;

import gate.Annotation;
import gate.Document;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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






public class MutationGrounder30 {
	 	
	private static final Logger log = Logger.getLogger(MutationGrounder30.class);

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
	public MutationGrounder30(){
		debugging = true;	
	}	

		
			
	 
	
	private static Map sortMentionTable(Map<String,List<Long[]>>  unsortMap) {        
        Map<String,Integer> dict = new HashMap<String,Integer>();
		for(Entry<String,List<Long[]>> e : unsortMap.entrySet()){
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
	
	
	private List<MutationInfo> alignMutationsOld(GroundingVariantTable scoreTable, List<Mutation> normalizedMutations){
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
	
	
	private Map<String,List<Long[]>> getMentionTable(gate.DocumentContent doc, String annotationType){
		Map<String,List<Long[]>> mentionTable = new HashMap<String,List<Long[]>>();
		
		
		
		return mentionTable;
	}
	
	
	private Map<String,List<Long[]>> getProteinMentionTable(gate.Document doc) throws InvalidOffsetException{
		 Map<String,List<Long[]>> proteinMentionTable = new HashMap<String,List<Long[]>>();
	        for(Annotation proteinMention : doc.getAnnotations().get("ProperProteinName")){        
				Long s = proteinMention.getStartNode().getOffset();
				Long e = proteinMention.getEndNode().getOffset();
				String name = doc.getContent().getContent(s, e).toString().toLowerCase();
				if (name.length() > 2 && name.matches(".*[A-Za-z]+.*") && !proteinNameExceptionList.contains(name)) {
	        		if(proteinMentionTable.containsKey(name)){
	        			proteinMentionTable.get(name).add(new Long[] {s,e});
	            	}else{
	            		List<Long[]> l = new ArrayList<Long[]>();
	            		l.add(new Long[] {s,e});
	            		proteinMentionTable.put(name, l);
	            	}
	        	}        	
	        }	
		return proteinMentionTable;
	}
	
	private Map<String,List<Long[]>> getOrganismMentionTable(gate.Document doc) throws InvalidOffsetException{
		Map<String,List<Long[]>> organismMentionTable = new HashMap<String,List<Long[]>>();
        for(Annotation organismMention : doc.getAnnotations().get("OrganismName")){        
			Long s = organismMention.getStartNode().getOffset();
			Long e = organismMention.getEndNode().getOffset();
			String name = doc.getContent().getContent(s, e).toString().toLowerCase();
			name = getOrganismNameAsInDB(name);
        	if (name.length() > 2 && name.matches(".*[A-Za-z]+.*") && !organismNameExceptionList.contains(name)) {
        		if(organismMentionTable.containsKey(name)){
        			organismMentionTable.get(name).add(new Long[] {s,e});
            	}else{
            		List<Long[]> l = new ArrayList<Long[]>();
            		l.add(new Long[] {s,e});     		
            		organismMentionTable.put(name, l);
            	}
        	}        	
        }    
		return organismMentionTable;
	}
	
	private Map<String,List<Long[]>> getMutationMentionTable(gate.Document doc) throws InvalidOffsetException{		
		Map<String, List<Long[]>> mutationMentionTable = new HashMap<String, List<Long[]>>();
		for (Annotation mutationMention : doc.getAnnotations().get("Mutation")) {
			Long s = mutationMention.getStartNode().getOffset();
			Long e = mutationMention.getEndNode().getOffset();
			String normalizedMutationString = (String) mutationMention.getFeatures().get("wNm");
			if (mutationMentionTable.containsKey(normalizedMutationString)) {
				mutationMentionTable.get(normalizedMutationString).add(new Long[] {s,e});
			} else {
				List<Long[]> l = new ArrayList<Long[]>();
				l.add(new Long[] { s,e});
				mutationMentionTable.put(normalizedMutationString, l);
			}

		}
		return mutationMentionTable;
	}
	
	
	
	
	private GroundingCandidateTable retrieveCandidates(
			Map<String, List<Long[]>> proteinMentionTable, 
			Map<String, List<Long[]>> organismMentionTable) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException {
		
		GroundingCandidateTable scoreTable = new GroundingCandidateTable();
		
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

		Class.forName("com.mysql.jdbc.Driver");		
		con = DriverManager.getConnection("jdbc:mysql://"+DATABASE_URL, USERNAME, PASSWORD);
				
						
		
		/*
		 * Build substring for organisms.			
		 */
		StringBuilder organismSubString = new StringBuilder();	
		for(String o : organismMentionTable.keySet()) {
			log.debug(o);
			if(o.length()<2)continue; // TODO may be < 3 (test it)
			organismSubString.append("organismname3.name='"+o+"' OR ");				
		}
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

		
			
			finalQuerySb.append(organismSubString.toString());
			finalQuerySb.append(") AND (proteinname.id=pac.id) AND (organismname3.id=pac.id) AND (sequence.id=pac.id) AND (genenames2.id=pac.id);");
			log.debug("FINAL QUERY: " + finalQuerySb.toString());

			
			// If query in cache, get results from cache. Else send query to DB.
			Set<String> queryResult = null;
			if(CacheQuerying.cache != null && CacheQuerying.cache.containsKey(finalQuerySb.toString())){
				queryResult = CacheQuerying.cache.get(finalQuerySb.toString());
			}else{
				queryResult = new HashSet<String>();
				Statement primaryAccSt = con.createStatement();
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
				
			/**	if(scoreTable.containsPac(pac)){
					Map m = ((Map)scoreTable.getCandidatesByPac(pac).get(0).getFeatures().getValue("proteinNamesInText"));
					if(m.containsKey(mentionStr)){
						((Set)m.get(mentionStr)).add(proteinNameInDb);
						((Set)scoreTable.getCandidatesByPac(pac).get(0).getFeatures().getValue("proteinNamesInText")).add(proteinNameInDb);
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
				}
				
				**/
				
				
				// if scoreTable contains PAC, add protein name from DB
				/*/ TODO here score possible: frequency of retrieved from DB
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
				
				*/
			}
			log.debug("queryResult: " + pacsCandidates);
		}						
		CacheQuerying.makeCachePersistent(cacheFileName);
		return scoreTable;
	}
	
	
	private List<MutationInfo> alignMutations(
			GroundingVariantTable scoreTable, 
			List<Mutation> normalizedMutations, 
			Map<String, List<Mutation>> sameWTMap){
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
		
		
		Collections.sort(mutationInfoList);
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
		
		return mutationInfoList;
	}
	
	
	private GroundingCandidateTable generateTableForRanking(
			GroundingCandidateTable gct, 
			List<MutationInfo> mutationInfoList) {

		return null;
	}
	
	
	private void rankTable(
			Document doc, 
			GroundingCandidateTable gctForRanking, 
			Map<String, List<Long[]>> proteinMentionTable,
			Map<String, List<Long[]>> organismMentionTable, 
			Map<String, List<Long[]>> mutationMentionTable) {
		
		
	}


	
	/**
	 * =========================================================================================
	 * GROUND
	 * =========================================================================================
	 * @param doc
	 * @param docName
	 * @throws InvalidOffsetException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws FileNotFoundException 
	 */
	public void ground(Document doc) throws InvalidOffsetException, FileNotFoundException, SQLException, ClassNotFoundException, IOException, URISyntaxException{
		//
		// Populate protein mention table.
		//
		 Map<String,List<Long[]>> proteinMentionTable = getProteinMentionTable(doc);
        log.debug(Utils.mapAsPrettyString(sortMentionTable(proteinMentionTable)));
        
        //
		// Populate organism mention table.
		//
        if(doc.getAnnotations().get("OrganismName").size()==0){
        	log.warn("No organisms in document: "+doc.getName());
        	return;
        }
        Map<String,List<Long[]>> organismMentionTable = getOrganismMentionTable(doc);         
        log.debug(Utils.mapAsPrettyString(sortMentionTable(organismMentionTable)));
		
		//
		// Populate mutation mention table.
		//	
		Map<String, List<Long[]>> mutationMentionTable = getMutationMentionTable(doc);		
        log.debug(Utils.mapAsPrettyString(sortMentionTable(mutationMentionTable)));
        
        
        //
        // ???
        //
		Set<Mutation> normalizedMutationSet = new HashSet<Mutation>();
		for(String s : mutationMentionTable.keySet()){
			String[] parsedMutation = Utils.parseWNMFormat(s);
			normalizedMutationSet.add(new Mutation(parsedMutation[0], Integer.parseInt(parsedMutation[1]), parsedMutation[2]));
		}		
		//make a list of the set
		List<Mutation> normalizedMutations = new ArrayList<Mutation>(normalizedMutationSet);		
		//sort the mutations, can't really remember why anymore.
		Collections.sort(normalizedMutations);		
		
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
		//remove "dublicates"
		for(int i = normalizedMutations.size()-1; i > -1; i--) {
			if(listedPositions.contains(i)) {
				normalizedMutations.remove(i);System.err.println();
			}
		}
		// @AK.Debug
	    for(Mutation mutation : normalizedMutations){
		    log.debug("normalizedMutations.mutation: "+mutation.wildtype+mutation.position+mutation.mutation);
		 }
		
	    
	    String docName = doc.getName().split("\\.")[0];
		doc.getAnnotations().removeAll((gate.AnnotationSet)doc.getAnnotations().get("GroundedPointMutation"));
	    GroundingCandidateTable gct = retrieveCandidates(proteinMentionTable,organismMentionTable);

	    
		/*
		 * GROUNDING START
		 */
        GroundingVariantTable scoreTable = new GroundingVariantTable();
		if(cacheForGroundingFileName != null) CacheAlignment.loadCache(cacheForGroundingFileName);
		List<MutationInfo> mutationInfoList = alignMutations(scoreTable,normalizedMutations,sameWTMap);
		if(cacheForGroundingFileName != null) CacheAlignment.makeCachePersistent(cacheForGroundingFileName);
		
		GroundingCandidateTable gctForRanking = generateTableForRanking(gct, mutationInfoList);
		
		rankTable( doc, gctForRanking, proteinMentionTable, organismMentionTable, mutationMentionTable);
        
        
        
	}
	
	
}


