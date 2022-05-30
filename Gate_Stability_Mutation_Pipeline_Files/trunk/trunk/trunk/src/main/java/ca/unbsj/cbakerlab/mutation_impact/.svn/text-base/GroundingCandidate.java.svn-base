package ca.unbsj.cbakerlab.mutation_impact;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO
 * GroundingCandidate
 *
 */
public class GroundingCandidate{

	private FeatureDescriptor dataFeatures;
	private FeatureDescriptor scoringFeatures;
	private float score;

	private static List<String> dataFeatureNames = new ArrayList<String>(Arrays.asList(
			"id",
			"pac",
			"mutation",
			"proteinNamesInText", // Set<String>
			"proteinNamesInDb", // Set<String>
			"organismNames", // Set<String>
			"mutations", // Set<String>
			"offset", // int
			"geneNames", // String[]
			"sequence"
			));
	
	private static List<String> scoringFeatureNames = new ArrayList<String>(Arrays.asList(
			"sdProteinName",
			"sdOrganismName",
			"sdPOISS",
			"sdPMISS",
			"sdOMISS",
			"sdProteinSysnonym",
			"sdExactProteinName",
			"absolutePosition",
			"swissProt",
			"sdNumberOfGroundedMutation"
			));

	// ######### COMPARATORS ################	
	static GroundingCandidateComparator sortByPac = new GroundingCandidateComparator("pac");

	GroundingCandidate(){
		dataFeatures = new FeatureDescriptor();
		scoringFeatures = new FeatureDescriptor();
		score = 0;
	}
	
	public FeatureDescriptor getData(){
		return this.dataFeatures;
	}
	
	public FeatureDescriptor getFeatures(){
		return this.scoringFeatures;
	}
	
 	public float getScore(){
		return this.score;
	}

	public void setScore(float score){
		this.score = score;
	}

	
	public String asString(){
		StringBuilder sb = new StringBuilder();
		sb.append("---------------/\n");
		Enumeration names = dataFeatures.attributeNames();
		while(names.hasMoreElements()){
			String attr = (String)names.nextElement();
			sb.append(attr+": "+dataFeatures.getValue(attr)+" ");
		}
		sb.append("/---------------\n");
		return sb.toString();
	}
	
}


class GroundingCandidateComparator implements Comparator<GroundingCandidate> {
	private String feature;
	GroundingCandidateComparator(String feature){
		this.feature = feature;
	}
	public int compare(GroundingCandidate ref1, GroundingCandidate ref2) {
		if(((String)ref1.getData().getValue(feature))
				.compareTo((String)ref2.getData().getValue(feature))>0){
			return 1;
		}
		return 0;		
	}
}

/**
 * CLASS GroundingClusterComparatorByOffset
 * @author artjomk
 *
 */


class GroundingCandidateTable{
	List<GroundingCandidate> candidates;
	private Map<String,List<GroundingCandidate>> indexByPac = new TreeMap<String,List<GroundingCandidate>>();
	
	GroundingCandidateTable(){
		candidates = new ArrayList<GroundingCandidate>();
	}
	
	public List<GroundingCandidate> getCandidates() {		
		return candidates;
	}
	
	void setCandidates(List<GroundingCandidate> candidates) {
		this.candidates = new ArrayList<GroundingCandidate>();
		this.candidates.addAll(candidates);
	}
	
	List<GroundingCandidate> getCandidatesByPac(String pac){
		return indexByPac.get(pac);
	}
	
	void addEntry(GroundingCandidate gv){
		candidates.add(gv);
		if(indexByPac.containsKey(gv.getFeatures().getValue("pac"))){
			indexByPac.get(gv.getFeatures().getValue("pac")).add(gv);
		}else{
			List l = new ArrayList();
			l.add(gv);
			indexByPac.put((String)gv.getFeatures().getValue("pac"), l);
		}
	}
	
	void removeEntry(GroundingCandidate sce){
		indexByPac.get(sce.getFeatures().getValue("pac")).remove(sce);
		candidates.remove(sce);
		/*
		if(scoreTableIndexByPac.containsKey(sce.getPac())){
			scoreTableIndexByPac.get(sce.getPac()).add(sce);
		}else{
			List l = new ArrayList();
			l.add(sce);
			scoreTableIndexByPac.put(sce.getPac(), l);
		}*/
	}
	
	boolean containsPac(String pac){
		if(indexByPac.containsKey(pac))return true;
		return false;
	}
	
	
	String asString(String feature){
		GroundingCandidateComparator comparator = new GroundingCandidateComparator(feature);
		Collections.sort(candidates, comparator);
		
		StringBuilder sb = new StringBuilder();		
		for(GroundingCandidate gc : candidates){
			sb.append(gc.asString()+"\n");
		}
		return sb.toString();
	}	
	
}
