package ca.unbsj.cbakerlab.mutation_impact;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.util.StringUtils;



/**
 * 
 * CLASS ScoreTableEntry
 * 
 */
class GroundingVariant implements Cloneable, Comparable{
	
	//
	// Identification.
	//
	final String id;	
	private final String pac;
	private String mutation;
	
	//
	// Text specific.
	//	
	private Map<String,Set<String>> proteinNameInText;
	
	//
	// UniProt description.
	//
	private Set<String> proteinNamesInDB;
	private final Set<String> organismNamesInDB;
	private final String sequenceInDB;
	private final String[] geneNamesInDB;
	
	//
	// Intermediate.
	//
	private Set<String> alignedMutations;
	private int offset;	
	private FeatureDescriptor fd;	
	float score;
	float originalScore;
	
	
	
	
	FeatureDescriptor getFeatureDescriptor(){
		return this.fd;
	}
	void setFeatureDescriptor(){
		if(this.fd==null) this.fd = new FeatureDescriptor();		
	}
	
	
	int benefitPOISS = 0;
	int benefitPMISS = 0;
	int benefitPOMISS = 0;
	int benefitNumberOfProteinSynonyms = 0;
	int exactProteinName = 0;
	int benefitNumberOfProteinMentions = 0;
	int benefitNumberOfOrganismMentions = 0;
	
	// ######### COMPARATORS ################	
	static GroundingVariantComparatorByPac sortByPac = new GroundingVariantComparatorByPac();
	static GroundingVariantComparatorByOffset sortByOffset = new GroundingVariantComparatorByOffset();
	static GroundingVariantComparatorByNumberOfGroundedMutations sortByNumberOfGroundedMutations = new GroundingVariantComparatorByNumberOfGroundedMutations();
	static GroundingVariantComparatorByScore sortByScore = new GroundingVariantComparatorByScore();
	static GroundingVariantComparatorByOriginalScore sortByOriginalScore = new GroundingVariantComparatorByOriginalScore();
	static GroundingVariantComparatorByOrganism sortByOrganism = new GroundingVariantComparatorByOrganism();

	
	//
	// CONSTRUCTOR.
	//
	GroundingVariant(String pac, Map<String,Set<String>> proteinNameInText, Set<String> proteinName, Set<String> organismNames, String sequence, String id, String[] geneNames) {
		this.id = id;
		this.pac = pac;
		this.mutation = null;
		this.proteinNamesInDB = proteinName;		
		this.proteinNameInText = proteinNameInText;		

		this.organismNamesInDB = organismNames;
		this.sequenceInDB = sequence;
		this.score = 0;
		this.originalScore = 0;
		this.alignedMutations = new TreeSet<String>();
		this.geneNamesInDB = geneNames;
	}		
	

	
	protected GroundingVariant clone(){		
		GroundingVariant clone = new GroundingVariant(pac,proteinNameInText,proteinNamesInDB,organismNamesInDB,sequenceInDB,id, geneNamesInDB);
		//clone.id = this.id;
		clone.score = this.score;
		clone.mutation = this.mutation;
		clone.originalScore = this.originalScore;
		clone.offset = this.offset;
		clone.alignedMutations = new TreeSet<String>(this.alignedMutations);
		return clone;
	}
	

	
	public String getId() {
		return this.id;
	}
	public int getOffset(){
		return this.offset;
	}
	public String getPac(){
		return this.pac;
	}
	public String getMutation(){
		return this.mutation;
	}	
	public Map<String,Set<String>> getProteinNameInText(){
		return proteinNameInText;
	}	
	public Set<String> getProteinName(){
		return proteinNamesInDB;
	}
	public Set<String> getOrganismName(){
		return this.organismNamesInDB;
	}

	public Set<String> getMutations(){
		return this.alignedMutations;
	}
	public String getSequence(){
		return this.sequenceInDB;
	}	
	public void setOffset(int offset){
		this.offset = offset;
	}
	public void setMutation(String mutation){
		this.mutation = mutation;
	}
	public String[] getGeneNames(){
		return this.geneNamesInDB;
	}
	public int compareTo(Object anotherScoreTableEntry) throws ClassCastException {
	    if (!(anotherScoreTableEntry instanceof GroundingVariant))
	      throw new ClassCastException("A ScoreTableEntry object expected.");
	    float anotherScoreTableEntryScore = ((GroundingVariant) anotherScoreTableEntry).score;  
	    return (int) (this.score - anotherScoreTableEntryScore);    
	}
	/*
	public boolean equals(Object anotherScoreTableEntry) throws ClassCastException {
		if (!(anotherScoreTableEntry instanceof ScoreTableEntry))
			throw new ClassCastException("A ScoreTableEntry object expected.");
		return ((ScoreTableEntry)anotherScoreTableEntry).score.equals(this.score) && ((ScoreTableEntry)anotherMutationScoreTableEntry).position == this.position;
	}*/	
	
	static List<String> featureNames = new ArrayList<String>(Arrays.asList(
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
	
	public String featureDescriptorAsString(){
		if(fd==null)return "null";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i<featureNames.size(); i++){
			if(fd
					.getValue(
							featureNames
							.get(i)) instanceof Float)
				sb.append(
						Utils.round(
								(Float)fd
								.getValue(
										featureNames
										.get(i))));
			else sb.append(fd.getValue(featureNames.get(i)));
			if(i != featureNames.size()-1)sb.append("|");
		}
		/*
		Enumeration names = fd.attributeNames();
		while(names.hasMoreElements()){
			String attr = (String)names.nextElement();
			if(fd.getValue(attr) instanceof Float)sb.append(attr+": "+df.format(fd.getValue(attr))+" ");
			else sb.append(attr+": "+fd.getValue(attr)+" ");
		}*/
		sb.append("]");
		return sb.toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(pac+" "+Utils.round(score)+" "+Utils.round(originalScore)+" ");//+" "+proteinNameInText+" "+" '"+organismName+"' ");
		/*sb.append(" ["+	benefitPOISS + ","+
				benefitPMISS + ","+
				benefitPOMISS + "|"+
				benefitNumberOfProteinSynonyms + ","+
				exactProteinName + "|"+
				benefitNumberOfProteinMentions + ","+
				benefitNumberOfOrganismMentions+"] ");*/
		//sb.append(pac+" "+score+" "+proteinNameInText+" "+" "+proteinName+" '"+organismName+"' ");
		if (this.fd!=null)sb.append(featureDescriptorAsString()+" ");
		sb.append(offset+" ");
		sb.append(alignedMutations+" ");		
		sb.append(" '"+organismNamesInDB+"' "+proteinNameInText+" "+this.id+" "+	StringUtils.arrayToDelimitedString(geneNamesInDB, "_"));
		//sb.append(sequence);
		
		//sb.append("\n");
		return sb.toString();
	}
	
	
	
	
	public String asString(){
		StringBuilder sb = new StringBuilder();
		sb.append(pac+" "+mutation+" "+Utils.round(score)+" "+proteinNameInText+" "+" '"+organismNamesInDB+"' ");
		//sb.append(pac+" "+score+" "+proteinNameInText+" "+" "+proteinName+" '"+organismName+"' ");
		sb.append(alignedMutations+" ");
		sb.append(offset+" ");
		//sb.append(sequence);
		if (this.fd!=null)sb.append(featureDescriptorAsString()+"\n");
		sb.append(StringUtils.arrayToDelimitedString(geneNamesInDB, "_"));
		sb.append("\n");
		/*sb.append("		["+	benefitPOISS + ","+
		benefitPMISS + ","+
		benefitPOMISS + "|"+
		benefitNumberOfProteinSynonyms + ","+
		exactProteinName + "|"+
		benefitNumberOfProteinMentions + ","+
		benefitNumberOfOrganismMentions+"]");*/
		return sb.toString();
	}
	
	public String[] asArray() {
		//String[] s = {pac,Float.toString(Util.round(score)),Float.toString(Util.round(originalScore)),featureDescriptorAsString(),Integer.toString(offset),mutations.toString(),"'"+organismNames+"'",proteinNameInText.toString(),id} ;
		String[] s = {pac,mutation,Float.toString(Utils.round(score)),featureDescriptorAsString(),Integer.toString(offset),alignedMutations.toString(),"'"+organismNamesInDB+"'",proteinNameInText.toString(),id} ;
		return s;
	}
	
	public String[] asArray2() {
		//String[] s = {pac,Float.toString(Util.round(score)),Float.toString(Util.round(originalScore)),featureDescriptorAsString(),Integer.toString(offset),mutations.toString(),"'"+organismNames+"'",proteinNameInText.toString(),id} ;
		String[] s = {pac,mutation,Float.toString(Utils.round(score)),featureDescriptorAsString(),Integer.toString(offset),alignedMutations.toString(),"'"+organismNamesInDB+"'",proteinNameInText.toString(),id} ;
		return s;
	}
	
	public String toDebugString(){
		StringBuilder sb = new StringBuilder();
		sb.append(pac+" "+Utils.round(score));//+" "+proteinNameInText+" "+" '"+organismName+"' ");
		sb.append(" ["+	benefitPOISS + ","+
				benefitPMISS + ","+
				benefitPOMISS + "|"+
				benefitNumberOfProteinSynonyms + ","+
				exactProteinName + "|"+
				benefitNumberOfProteinMentions + ","+
				benefitNumberOfOrganismMentions+"] ");
		//sb.append(pac+" "+score+" "+proteinNameInText+" "+" "+proteinName+" '"+organismName+"' ");
		sb.append(offset+" ");
		sb.append(alignedMutations+" ");		
		sb.append(" '"+organismNamesInDB+"' "+proteinNameInText+" ");
		//sb.append(sequence);
		if (this.fd!=null)sb.append(featureDescriptorAsString());
		sb.append("\n");
		return sb.toString();
	}


}


class GroundingVariantComparatorByOffset implements Comparator<GroundingVariant> {
	public int compare(GroundingVariant ref1, GroundingVariant ref2) {
		if(ref1.getOffset() > ref2.getOffset()){
			return 1;
		}
		return 0;		
	}
}

class GroundingVariantComparatorByPac implements Comparator<GroundingVariant> {
	public int compare(GroundingVariant ref1, GroundingVariant ref2) {
		if(ref1.getPac().compareTo(ref2.getPac())>0){
			return 1;
		}
		return 0;		
	}
}

class GroundingVariantComparatorByNumberOfGroundedMutations implements Comparator<GroundingVariant> {
	public int compare(GroundingVariant ref1, GroundingVariant ref2) {
		if(ref1.getMutations().size() < ref2.getMutations().size()){
			return 1;
		}
		return 0;		
	}
}

class GroundingVariantComparatorByScore implements Comparator<GroundingVariant> {
	public int compare(GroundingVariant ref1, GroundingVariant ref2) {
		if(ref1.score < ref2.score){
			return 1;
		}
		return 0;		
	}
}

class GroundingVariantComparatorByOriginalScore implements Comparator<GroundingVariant> {
	public int compare(GroundingVariant ref1, GroundingVariant ref2) {
		if(ref1.originalScore < ref2.originalScore){
			return 1;
		}
		return 0;		
	}
}
class GroundingVariantComparatorByOrganism implements Comparator<GroundingVariant> {
	public int compare(GroundingVariant ref1, GroundingVariant ref2) {
		if(ref1.getOrganismName().iterator().next().compareTo(ref2.getOrganismName().iterator().next())>0){
			return 1;
		}
		return 0;		
	}
}

/**
 * 
 * CLASS GroundingVariantTable.
 *
 */
class GroundingVariantTable implements Iterable<GroundingVariant>{
	//private int currentSize;
	private List<GroundingVariant> entries = new ArrayList<GroundingVariant>();
	private Map<String,List<GroundingVariant>> scoreTableIndexByPac = new TreeMap<String,List<GroundingVariant>>();
	private float maxScore = 0;
	
	Map<String,List<GroundingVariant>> getIndexByPack(){
		return scoreTableIndexByPac;
	}
	
	List<GroundingVariant> getEntries(){
		return entries;
	}
	
	void addEntry(GroundingVariant gv){
		entries.add(gv);
		if(scoreTableIndexByPac.containsKey(gv.getPac())){
			scoreTableIndexByPac.get(gv.getPac()).add(gv);
		}else{
			List l = new ArrayList();
			l.add(gv);
			scoreTableIndexByPac.put(gv.getPac(), l);
		}
	}
	
	void removeEntry(GroundingVariant sce){
		scoreTableIndexByPac.get(sce.getPac()).remove(sce);
		entries.remove(sce);
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
		if(scoreTableIndexByPac.containsKey(pac))return true;
		return false;
	}
	
	boolean pacInTop(String pac){
		if(scoreTableIndexByPac.containsKey(pac)){
			for(GroundingVariant gv : scoreTableIndexByPac.get(pac)){
				if(gv.score == maxScore){
					return true;
				}
			}
		}
		return false;
	}
	
	int pacInTopNumber(String pac){
		int result = 0;
		if(scoreTableIndexByPac.containsKey(pac)){
			for(GroundingVariant gv : scoreTableIndexByPac.get(pac)){
				if(gv.score == maxScore){
					result++;
				}
			}
		}
		return result;
	}
	
	float getMaxScore(){
		return this.maxScore;
	}
	
	float getMaxScoreOnFly(){
		calcualteMaxScore();
		return this.maxScore;
	}
	
	void calcualteMaxScore(){
		maxScore = 0;
		for(GroundingVariant gv : entries){
			if(gv.score > maxScore) maxScore = gv.score;
		}
	}
	
	void setEntries(List<GroundingVariant> entries){
		this.entries = entries;
	}
	
	String asString(Comparator c){
		if(c instanceof GroundingVariantComparatorByOffset){
			Collections.sort(entries, GroundingVariant.sortByOffset);
		}else if(c instanceof GroundingVariantComparatorByPac){
			Collections.sort(entries, GroundingVariant.sortByPac);
		}else if(c instanceof GroundingVariantComparatorByScore){
			Collections.sort(entries, GroundingVariant.sortByScore);
		}

		StringBuilder sb = new StringBuilder();	
		sb.append("Table:\n");
		for(GroundingVariant gv : entries){
			sb.append(gv+"\n");
		}
		return sb.toString();
	}
		
	void display(Comparator c){
		if(c instanceof GroundingVariantComparatorByOffset){
			Collections.sort(entries, GroundingVariant.sortByOffset);
		}else if(c instanceof GroundingVariantComparatorByPac){
			Collections.sort(entries, GroundingVariant.sortByPac);
		} else if(c instanceof GroundingVariantComparatorByScore){
			Collections.sort(entries, GroundingVariant.sortByScore);
		}
		
		System.out.print("\n");
		for(GroundingVariant sce : entries){
			System.out.print(sce.asString());
		}		
	}
	
	
	private String[][] createTable(){
		Collections.sort(entries, GroundingVariant.sortByScore);
		String[][] table = new String[entries.size()][9];
		for(int i = 0; i < entries.size(); i++){
			table[i] = entries.get(i).asArray();
		}
		return table;
	}

	String asStringTable() {
		StringBuilder sb = new StringBuilder();
		String[][] table = createTable();
		  // Find out what the maximum number of columns is in any row
		  int maxColumns = 0;
		  for (int i = 0; i < table.length; i++) {
		    maxColumns = Math.max(table[i].length, maxColumns);
		  }

		  // Find the maximum length of a string in each column
		  int[] lengths = new int[maxColumns];
		  for (int i = 0; i < table.length; i++) {
		    for (int j = 0; j < table[i].length; j++) {
		      lengths[j] = Math.max(table[i][j].length(), lengths[j]);
		    }
		  }

		  // Generate a format string for each column
		  String[] formats = new String[lengths.length];
		  for (int i = 0; i < lengths.length; i++) {
		   formats[i] = "%1$-" + lengths[i] + "s" 
		       + (i + 1 == lengths.length ? "\n" : " ");
		 }

		  // Print 'em out
		  for (int i = 0; i < table.length; i++) {
		    for (int j = 0; j < table[i].length; j++) {
		      //System.out.printf(formats[j], table[i][j]);
		    	if(i>500)break;
		      sb.append(String.format(formats[j], table[i][j]));
		    }
		  }
		  return sb.toString();
	}
	
	String scoreTableIndexByPacAsString(){		
		StringBuilder sb = new StringBuilder();
		sb.append("\n============================\nscoreTableIndexByPac:\n");
		for(Entry<String, List<GroundingVariant>> e : scoreTableIndexByPac.entrySet()){
			sb.append(e.getKey()+" [");
			for(GroundingVariant sce : e.getValue()){
				sb.append(sce+" ");
			}
			sb.append("]\n");
		}
		return sb.toString();
	}
	
    @Override
    public Iterator<GroundingVariant> iterator() {
        Iterator<GroundingVariant> it = new Iterator<GroundingVariant>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < entries.size() && entries.get(currentIndex) != null;
            }

            @Override
            public GroundingVariant next() {
                return entries.get(currentIndex++);
            }

            @Override
            public void remove() {
        		int temp = --currentIndex;
        		GroundingVariant current = entries.get(temp);
        		scoreTableIndexByPac.get(current.getPac()).remove(current);
        		if(scoreTableIndexByPac.get(current.getPac()).isEmpty()) scoreTableIndexByPac.remove(current.getPac());
        		entries.remove(current);
            }
        };
        return it;
    }

	
	/**
	 * Create Index for scoreTableByPac.
	 
	for(ScoreTableEntry e : scoreTable){
		if(scoreTableIndexByPac.containsKey(e.getPac())){
			scoreTableIndexByPac.get(e.getPac()).add(e);
		}else{
			List l = new ArrayList();
			l.add(e);
			scoreTableIndexByPac.put(e.getPac(), l);
		}
	}*/
}

