package ca.unbsj.cbakerlab.mutation_impact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import ca.unbsj.cbakerlab.mutation.GroundedMutation;
import ca.unbsj.cbakerlab.mutation.GroundedPointMutation;
import ca.unbsj.cbakerlab.mutation.ImpactDirection;
import ca.unbsj.cbakerlab.mutation.MutationImpact;
import ca.unbsj.cbakerlab.mutation.Protein;
import ca.unbsj.cbakerlab.mutation.StabilityDirection;

public class Corpus {
	private static final Logger log = Logger.getLogger(Corpus.class);

	private final String name;
	private Map<String,DocumentInformation> docInfos;


	public Corpus(String name) throws FileNotFoundException, IOException, URISyntaxException{
		this.name = name;
		docInfos = new TreeMap<String,DocumentInformation>();
		
	    Properties pro = new Properties();
	    pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
	}
	
	
	public String getName(){
		return name;
	}
	
	public GroundedMutation getGroundedMutation(String docId,String proteinId, String mutationId){
		DocumentInformation docInfo = docInfos.get(docId);
		while(docInfo.getAllGroundedMutationsIterator().hasNext()){
			GroundedMutation gm = docInfo.getAllGroundedMutationsIterator().next();
			//if(gm.g){
			//	return gm;
			//}
		}
		return null;
	}

	public Map<String,DocumentInformation> getDocInfos(){
		return docInfos;
	}
	
	
	public void loadCsv(File file) throws IOException {		
		log.info("Loading corpus from "+file.getName());
		//Map<String,DocumentInformation> map = new TreeMap<String, DocumentInformation>();
		int countGPM = 0;
        try {		
		    InputStream stream = new FileInputStream(file);		
		    CSVReader csvReader = 	new CSVReader(new InputStreamReader(stream),'\t');
		    List<String[]> lines = csvReader.readAll();
		    
		   // for(String s : lines.get(0))log.trace(s);
		    
		    Integer documentIdColNum = Utils.columnNumber("document_id", lines.get(0));
		    if (documentIdColNum == null) {
			    log.fatal(file+" does not have document_id");
			}

		    Integer proteinIdColNum = 	Utils.columnNumber("uniprot_id", lines.get(0));	       
		    if (proteinIdColNum == null) {
			    log.fatal(file+" does not have uniprot_id");
			}
		    
		    Integer wtResidueColNum = 	Utils.columnNumber("wt_residue", lines.get(0));	       
		    if (wtResidueColNum == null) {
			    log.fatal(file+" does not have wt_residue");
			}
		    
		    Integer mPositionColNum = 	Utils.columnNumber("position", lines.get(0));	       
		    if (mPositionColNum == null) {
			    log.fatal(file+" does not have 'position'");
			}
		    
		    Integer mResidueColNum = 	Utils.columnNumber("m_residue", lines.get(0));	       
		    if (mResidueColNum == null) {
			    log.fatal(file+" does not have mResidue");
			}
		    Integer offsetColNum = 	Utils.columnNumber("coordinate_offset", lines.get(0));	       
		    if (offsetColNum == null) {
			    log.fatal(file+" does not have coordinate_offset");
			}
		    
		    Integer propertyIdColNum = 	Utils.columnNumber("property_id", lines.get(0));	       
		    if (propertyIdColNum == null) {
			    log.warn(file+" does not have 'property_id'");
			}		    
		    
		    Integer directionColNum = 	Utils.columnNumber("direction", lines.get(0));	       
		    if (directionColNum == null) {
			    log.warn(file+" does not have 'direction'");
			}
		    
		    Integer textColNum = 	Utils.columnNumber("text", lines.get(0));	       
		    if (textColNum == null) {
			    log.warn(file+" does not have 'text'");
			}

			Integer stabilityDirectionColNum = 	Utils.columnNumber("stability_direction", lines.get(0));	       
		    if (stabilityDirectionColNum == null) {
			    log.warn(file+" does not have stability_direction");
			}
		    
		    lines.remove(0); // headers
	
		    /**
		     * Initialise map.
		     */
		    //log.error("A: "+lines.get(0)[0]);
		    if(lines.get(0)[0].contains("Files")){
		    	for(String documentId : lines.get(0)){//log.debug("->"+documentId);
		    		if(documentId.equals("Files:"))continue;
		    		//log.error(documentId);
		    		DocumentInformation docInfo = new DocumentInformation(documentId);
		    		docInfos.put(documentId, docInfo);		    		
		    	}		    	
		    	lines.remove(0);
		    }
		    
		    
		    Set<String> uniqueGPMs = new HashSet<String>();
		    Set<String> uniqueMies = new HashSet<String>();

		    Map<String,GroundedMutation> uniqueGMs = new HashMap<String,GroundedMutation>();
		    
		    for (String[] line : lines){
			    if (line == null || line.length<=1){
					log.debug("Empty line in "+file.getName());
					continue;
				}
			    else {//log.debug("LINE: +"+line[0]);
				    String documentId = null;
				    if(line[documentIdColNum] != null && !line[documentIdColNum].equals("")  && !line[documentIdColNum].equals(" ")){
					    documentId = line[documentIdColNum].trim();
				    }else continue;
				    	
				    String proteinIds = null;
				    if(line[proteinIdColNum] != null && !line[proteinIdColNum].equals("")  && !line[proteinIdColNum].equals(" ")){
					    proteinIds = line[proteinIdColNum].trim();
				    }else continue;
				    
				   // log.info("---"+documentId+" "+proteinIds);
				    
				    char wtResidue = 0;
				    if(line[wtResidueColNum] != null && !line[wtResidueColNum].equals("")  && !line[wtResidueColNum].equals(" ")){
				    	wtResidue = line[wtResidueColNum].trim().toCharArray()[0];
				    }else continue;
				    
				    int position = 0;
				    if(line[mPositionColNum] != null && !line[mPositionColNum].equals("")  && !line[mPositionColNum].equals(" ") ){
					    position = Integer.parseInt(line[mPositionColNum].trim());
				    }else continue;
				    
				    char mResidue = 0;
				    if(line[mResidueColNum] != null && !line[mResidueColNum].equals("")  && !line[mResidueColNum].equals(" ") ){
				    	if(line[mResidueColNum].trim().length()>1) continue;  // skip Deletion
				    	else mResidue = line[mResidueColNum].trim().toCharArray()[0];
				    }else continue;
				    
					int offset = 0;
					if (offsetColNum != null)
						if (line[offsetColNum] != null && !line[offsetColNum].equals("") && !line[offsetColNum].equals(" ")) {
							offset = Integer.parseInt(line[offsetColNum].trim());
						}


					String property = null;
					if (propertyIdColNum != null)
						if (line[propertyIdColNum] != null && !line[propertyIdColNum].equals("") && !line[propertyIdColNum].equals(" ")) {
							property = line[propertyIdColNum].trim().replaceAll("GO:", "GO_");
						}


					String direction = null;
					if (directionColNum != null)
						if (line[directionColNum] != null && !line[directionColNum].equals("") && !line[directionColNum].equals(" ")) {
							direction = line[directionColNum].trim();
						}

					String text = null;
					if (textColNum != null)
						text = line[textColNum].trim();
				    
					String stabilityDirection = null;
					if(line[stabilityDirectionColNum] != null && !line[stabilityDirectionColNum].equals("")  && !line[stabilityDirectionColNum].equals(" ")){
						stabilityDirection = line[stabilityDirectionColNum].trim();
					}else continue;
				    
				    
				    if (!documentId.equals("") && !documentId.equals("Not Available") && !documentId.equals("N/A")){
				    	
				    	log.trace("documentId: " + documentId);
					    log.trace("proteinId: " + proteinIds); 					    
					    log.trace("GPM: " + wtResidue+position+mResidue); 
					    log.trace("property: " + property); 
					    log.trace("direction: " + direction); 
					    log.trace("text: " + text); 
						log.trace("stabilityDirection " + stabilityDirection);
					    
					    
					    DocumentInformation docInfo = null;
						if (docInfos.containsKey(documentId)) {							
							docInfo = docInfos.get(documentId);							
						}else{
							docInfo = new DocumentInformation(documentId);
							docInfos.put(documentId, docInfo);
						}
				    
						
						
						
						
						String uniqueProteinMutationPerDocument = documentId+proteinIds+wtResidue+position+mResidue;
						GroundedPointMutation gpm = null;
						if(!uniqueGPMs.contains(uniqueProteinMutationPerDocument)){
							gpm = new GroundedPointMutation();
							gpm.setWildtypeResidue(wtResidue);
							gpm.setMutantResidue(mResidue);
							gpm.setMentionedPosition(position);
							gpm.setOffset(offset);
							//gpmEval.setSwissProtId(proteinId);
							for(String proteinId : proteinIds.split("_")){
								gpm.addSwissProtId(proteinId); // changed for multiple UniProtId
							}
							

							
							docInfo.addGroundedPointMutation(gpm);
							uniqueGPMs.add(uniqueProteinMutationPerDocument);
							countGPM++;
						}else{
							log.trace("Mutation is not unique in "+file.getName()+" !!!");
						}  
						
						
						 if(property!=null && direction!=null){
							 String uniqueMIR = documentId+proteinIds+wtResidue+position+mResidue+property;				   
							    
							    if(!uniqueMies.contains(uniqueMIR)){
							    	//GroundedPointMutation 
							    	gpm = new GroundedPointMutation();
									gpm.setWildtypeResidue(wtResidue);
									gpm.setMutantResidue(mResidue);
									gpm.setMentionedPosition(position);
									gpm.setOffset(offset);
									//gpmEval.setSwissProtId(proteinId);
									for(String proteinId : proteinIds.split("_")){
										gpm.addSwissProtId(proteinId); // changed for multiple UniProtId
									}
									
							    	// Normalize DIRECTION.
								    if(direction.equals("[+]"))direction = ImpactDirection.POSITIVE;
								    if(direction.equals("[-]"))direction = ImpactDirection.NEGATIVE;
								    if(direction.equals("[O]"))direction = ImpactDirection.NEUTRAL;
									
									
								    if(direction != null && property != null && !direction.equals("") && !property.equals("")){
										
										Protein protein = new Protein(proteinIds);
										//gpmEval.setWildtypeResidue(wtResidue);
										//gpmEval.setMutantResidue(mResidue);
										//gpmEval.setMentionedPosition(position);
										///gpm.setSwissProtId(proteinId);
										//for(String proteinId : proteinIds.split("_")){
										//	gpm.addSwissProtId(proteinId); // changed for multiple UniProtId
										//}
										MutationImpact mi = new MutationImpact();
										mi.setDirection(direction);//log.debug(property);

										
										if(property.contains("GO_")){
											mi.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+property));
										}else{
											mi.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+property));
											//mi.setAffectedProteinProperty(new URI(mutationOntologyURI+"#"+property));
										}

										StabilityDirection sd = new StabilityDirection();
//										sd.setDirection(direction);
										
										GroundedMutation gmNew = new GroundedMutation(protein,  gpm, mi, sd); //log.debug(mi.toPrettyString());
										docInfo.addGrounedMutation(gmNew);		
								    }	
								    
								    uniqueMies.add(uniqueMIR);
							    }else{
									log.trace("Mutation Impact is not unique in "+file.getName()+" !!!");			
							    }// else	
						 }// if
						
						
						
						
					   				    
					    
					}// if (!documentId.equals

				} // if (line == null ||
			} // for (String[] line : lines)		   
		}
	    catch (Exception ex)
		{
		    log.fatal("Cannot load the ...  mapping from the resource csv : " + ex);
		    throw new RuntimeException("Cannot load the ... mapping from the resource csv : " + ex,ex);
		};
		log.info(file+" loaded.");		
	}
	
	
	public void loadRdf(File file){
		
	}
	
	
	public void toCsv(File file){
		
	}
	
	public void toRdf(File file){
		
	}
	
	/**
	 * To Console.
	 */
	public String resultsAsString(){
		StringBuilder sb = new StringBuilder();
		for (DocumentInformation docInfo : docInfos.values()) {
			System.out.println("\n==========================================================\n" + "DOCUMENT: " + docInfo.getName() + "		"
					+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
					+ "\n==========================================================\n");
			/**
			 * ========================================================
			 * mutationGazzetter SwissProtId: P32245 Mutant sequence:MVNSTHRGMHTSLHLWNRSSYRLHSNASESLGKGYSDGGCYEQLFVSPEVFVTLGVISLLENILVIVAIAKNKNLHSPMYFFICSLAVADMLVSVSNGSETIVITLLNSTDTDAQSFTVNIDNVIDSVICSSLLASICSLLSIAVDRYFTIFYALQYHNIMTVKRVGIIISCIWAACTVSGILFIIYSDSSAVIICLITMFFTMLALMASLYVHMFLMARLHIKRIAVLPGTGAIRQGANMKGAITLTILIGVFVVCWAPFFLHLIFYISCPQNPYCVCFMSH
			 * FNLYLILIMCNSIIDPLIYALRSQKLRKTFKEIICCYPLGGLCDLSSRY NEGATIVE
			 * impact on http://purl.org/obo/owl/GO#GO_0004977 Point
			 * mutation: E308K
			 */
			sb.append(docInfo.resultsAsPrettyString());

			/**
			 * ------------------------------------------- GATE ProteinName:
			 * 72 GATE ProperProteinName: 69 GATE OrganismName: 21
			 */
			// System.out.println(docInfo.getDocumentInfoStatistics().gateOutputInfoAsPrettyString());

			/**
	        	 * 
	        	 */
			// System.out.println(docInfo.getDocumentInfoStatistics().cooccurrencesAsPrettyString());			
		}
		return sb.toString();
	}
	
	
	public void saveMgResults(String outputFileName) throws IOException {
		/**
		 * MG. To CSV.
		 */
		CSVWriter cvsWriter = new CSVWriter(new FileWriter(outputFileName), '\t');
		cvsWriter.writeNext(new String[] { "document_id", "uniprot_id", "wt_residue", "position", "m_residue","coordinate_offset","wt_sequence", "stability_direction", "stability_value"});


		//cvsWriter.writeNext(new String[] { "Files:", results.keySet().toString()});
		
		/**
		 * Write Files which were processed.
		 */
		String[] filesLine = new String[docInfos.keySet().size()+1];
		filesLine[0] = "Files:";
		String[] array =docInfos.keySet().toArray(new String[docInfos.keySet().size()]);
		for(int i = 0; i< array.length; i++){
			filesLine[i+1]=array[i];
		}
		cvsWriter.writeNext(filesLine);
			
		
		
		for (Entry<String, DocumentInformation> e : docInfos.entrySet()) {
			DocumentInformation docInfo = e.getValue();
			cvsWriter.writeNext(new String[]{" "});
			 log.trace(docInfo.resultsAsPrettyString());
			 log.trace(docInfo.resultsAsTable());
	    	if(docInfo.getAllGroundedPointMutations()!=null && docInfo.getAllGroundedPointMutations().size()>0){
	    		for(GroundedPointMutation gpm : docInfo.getAllGroundedPointMutations()){
					int offset = gpm.getMentionedPosition()-gpm.getCorrectPosition();
					System.err.print("write: "+docInfo.getName());
					String[] csvLine = {
							docInfo.getName(),
							gpm.getSwissProtIdAsString(), // changed for multiple UniProt, e.g. "P11344_Q98967"
							Character.toString(gpm.getWildtypeResidue()),
							Integer.toString(gpm.getMentionedPosition()),
							Character.toString(gpm.getMutantResidue()),
							Integer.toString(offset),
							gpm.getWtSequence(),
							"Stability Direction",
							"Stability Value",
							}; 
					cvsWriter.writeNext(csvLine);
					

				}	
	    	}else{
	    		log.info("No results of Mutation Grounding.");
	    	}
					
		}
		cvsWriter.close();
	}
	
	
	public void saveMieResults(String outputFileName) throws IOException {
		/**
		 * MIR. To CSV.
		 */
		CSVWriter cvsWriter = new CSVWriter(new FileWriter(outputFileName), '\t');
		cvsWriter.writeNext(new String[] { "document_id", "uniprot_id", "wt_residue", "position", "m_residue","coordinate_offset","property_id","direction","text", "stability_direction", "stability_value"});

		
		
		

		/**
		 * Write Files which were processed.
		 */
		String[] filesLine = new String[docInfos.keySet().size()+1];
		filesLine[0] = "Files:";
		String[] array =docInfos.keySet().toArray(new String[docInfos.keySet().size()]);
		for(int i = 0; i< array.length; i++){
			filesLine[i+1]=array[i];
		}
		cvsWriter.writeNext(filesLine);
		
		
		for (Entry<String, DocumentInformation> e : docInfos.entrySet()) {
			DocumentInformation docInfo = e.getValue();
			cvsWriter.writeNext(new String[]{" "});
			//System.err.println(docInfo);
	    	if(docInfo.getAllGroundedMutations()!=null && docInfo.getAllGroundedMutations().size()>0){
	    		for(GroundedMutation gm : docInfo.getAllGroundedMutations()){
					GroundedPointMutation gpm = gm.getCompoundMutation().next();
					int offset = gpm.getMentionedPosition()-gpm.getCorrectPosition();
					String[] csvLine = {
							docInfo.getName(),
							gm.getProteinAppliedTo().getSwissProtId(),
							Character.toString(gm.getCompoundMutation().next().getWildtypeResidue()),
							Integer.toString(gm.getCompoundMutation().next().getMentionedPosition()),
							Character.toString(gm.getCompoundMutation().next().getMutantResidue()),
							Integer.toString(offset),
							gm.getMutationImpacts().next().getAffectedProteinProperty().toString().split("#")[1].replaceFirst("GO_", "GO:"),
							gm.getMutationImpacts().next().getDirectionAsString(),
							"",
							gm.getStabilityDirection().getStabilityDirection(),
							"Stability Value",
							}; 
					cvsWriter.writeNext(csvLine);
				}		
	    	}else{
	    		log.info("No results of Mutation Impact Extraction.");
	    	}

				
		}
		cvsWriter.close();
	}
	
	

}
