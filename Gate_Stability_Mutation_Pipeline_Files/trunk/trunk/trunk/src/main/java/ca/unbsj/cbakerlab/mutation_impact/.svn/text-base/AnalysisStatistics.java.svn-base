package ca.unbsj.cbakerlab.mutation_impact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ca.unbsj.cbakerlab.mutation.GroundedPointMutation;

public class AnalysisStatistics {

	private static final Logger log = Logger.getLogger(AnalysisStatistics.class);

	
	List<String> inPool;
	List<String> notInPool;
	List<String> inForGroundingSet;
	List<String> notInForGroundingSet;
	List<String> notInAfterGrounding;
	List<String> inAfterGrounding;
	List<String> inFilteredSet;
	List<String> notInFilteredSet;
	List<String> inTopOfTable;
	List<String> notInTopOfTable;
	List<String> inTopOfOneOfCluster;
	List<String> notInTopOfOneOfCluster;
	
	Map<String,Set<String>> map;
	
	public AnalysisStatistics(){
		if(inPool == null) inPool = new ArrayList<String>();
		if(notInPool == null) notInPool = new ArrayList<String>();
		if(inForGroundingSet == null) inForGroundingSet = new ArrayList<String>();
		if(notInForGroundingSet == null) notInForGroundingSet = new ArrayList<String>();
		if(notInAfterGrounding == null) notInAfterGrounding = new ArrayList<String>();
		if(inAfterGrounding == null) inAfterGrounding = new ArrayList<String>();
		if(inFilteredSet == null) inFilteredSet = new ArrayList<String>();
		if(notInFilteredSet == null) notInFilteredSet = new ArrayList<String>();
		if(inTopOfTable == null) inTopOfTable = new ArrayList<String>();
		if(notInTopOfTable == null) notInTopOfTable = new ArrayList<String>();
		if(inTopOfOneOfCluster == null) inTopOfOneOfCluster = new ArrayList<String>();
		if(notInTopOfOneOfCluster == null) notInTopOfOneOfCluster = new ArrayList<String>();
		
	}
	
    Map<String,Set<String>> getMap(){
    	return map;
    }
    
	public void loadMap(File evalFile) throws FileNotFoundException, IOException, URISyntaxException{
		map = new TreeMap<String,Set<String>>();
		//File evalFile = new File("/home/artjomk/evaluation.csv");
		Corpus corpus = new Corpus("corpus");
		corpus.loadCsv(evalFile);
		//Map<String,DocumentInformation> evalMap = loadCsvFile(evalFile);
		int countGPM0 = 0;
		for(Entry<String,DocumentInformation> e : corpus.getDocInfos().entrySet()){
			countGPM0 = countGPM0 + e.getValue().getAllGroundedPointMutations().size();
			for(GroundedPointMutation gpm : e.getValue().getAllGroundedPointMutations()){
				
				String swissProtId = gpm.getSwissProtId().keySet().iterator().next();

				if(map.containsKey(e.getKey())){
					map.get(e.getKey()).add(swissProtId);
				}
				else {
					Set<String> l = new HashSet();
					l.add(swissProtId);
					map.put(e.getKey(), l);
				}
				
			}		
		}
		log.info("number of diff uniprot_ids loaded: "+countGPM0);
		
		int countGPM = 0;
		for(Entry<String,Set<String>> e : map.entrySet()){
			countGPM = countGPM + e.getValue().size();
		}
		log.info("number of documents loaded: "+countGPM);
	}
	
	
	public String asString(){
		StringBuilder sb = new StringBuilder();
				
		sb.append("\n ========================= POOL =========================");
		
		sb.append("\nNotInPool: " +notInPool.size());
		for(String s : notInPool)	sb.append(s);
		sb.append("\nInPool: "+inPool.size());
		for(String s : inPool)	sb.append(s);

		sb.append("\n\nNotInForGroundingSet: " + notInForGroundingSet.size());
		for(String s : notInForGroundingSet)	sb.append("\n"+s);
		sb.append("\nInForGroundingSet: " + inForGroundingSet.size());
		for(String s : inForGroundingSet)	sb.append("\n"+s);

		sb.append("\n\nNotInAfterGrounding: " +  notInAfterGrounding.size());
		for(String s : notInAfterGrounding)	sb.append("\n"+s);
		sb.append("\nInAfterGrounding: " + inAfterGrounding.size());
		for(String s : inAfterGrounding)	sb.append("\n"+s);

		sb.append("\n\nNotInFilteredSet: " + notInFilteredSet.size());
		for(String s : notInFilteredSet)	sb.append("\n"+s);
		sb.append("\nInFilteredSet: " + inFilteredSet.size());
		for(String s : inFilteredSet)	sb.append("\n"+s);

		sb.append("\n\nNotInTopOfTable: " + notInTopOfTable.size());
		for(String s : notInTopOfTable)	sb.append("\n"+s);
		sb.append("\n\nInTopOfTable: " + inTopOfTable.size());
		for(String s : inTopOfTable)	sb.append("\n"+s);

		sb.append("\n\nNotInTopOfOneOfCluster: " + notInTopOfOneOfCluster.size());
		for(String s : notInTopOfOneOfCluster)	sb.append("\n"+s);
		sb.append("\n\nInTopOfOneOfCluster: " + inTopOfOneOfCluster.size());
		for(String s : inTopOfOneOfCluster)	sb.append("\n"+s);


		
		sb.append("\n=========================\n");
		return sb.toString();
	}	
	
}
