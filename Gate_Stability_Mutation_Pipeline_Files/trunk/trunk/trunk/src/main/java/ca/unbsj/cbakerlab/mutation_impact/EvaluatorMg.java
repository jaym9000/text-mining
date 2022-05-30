package ca.unbsj.cbakerlab.mutation_impact;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import ca.unbsj.cbakerlab.mutation.GroundedPointMutation;

public class EvaluatorMg {
	
	private static final Logger log = Logger.getLogger(EvaluatorMg.class);


	/**
	 * until 2012-01-22
	 * @param evalFile
	 * @param hypoFile
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public static CorpusEvalMgStatistics evaluate(File outFile, File evalFile, File hypoFile) throws IOException, URISyntaxException{

		
		CorpusEvalMgStatistics ces = new CorpusEvalMgStatistics();
		
		ces.evalMgStatisticsForDocs = new TreeMap<String,DocEvalMgStatistics>();
		
		Corpus evalCorpus = new Corpus("evalCorpus");
		Corpus hypoCorpus = new Corpus("hypoCorpus");

		evalCorpus.loadCsv(evalFile);
		hypoCorpus.loadCsv(hypoFile);	
		
		
		// temporary
		//String outputDirName = hypoFile.getAbsolutePath().split("/results-mg.csv")[0];
		///saveEvalTable(evalMap,outputDirName);
			
		
		
		// Temporary. For prepairing data for Finnland.
		CSVWriter cvsWriter = new CSVWriter(new FileWriter(outFile), '\t');
		cvsWriter.writeNext(new String[] { "document_id", "ProteinID", "wt_residue", "position", "m_residue", "GroundedTo", "Correct", "Offset"});
		
		
		/**
		 * Evaluate.
		 */
		for(Entry entry : hypoCorpus.getDocInfos().entrySet()){
			/**
			 * Process next document.
			 */
			String docName = (String) entry.getKey();
			DocumentInformation docInfoHypo = (DocumentInformation) entry.getValue();
			DocumentInformation docInfoEval = (DocumentInformation) evalCorpus.getDocInfos().get(docName);			
			
			
			// Initialise DocumentInfoStatistics
			DocEvalMgStatistics docStat;
			if(ces.evalMgStatisticsForDocs.containsKey(docName)){
				docStat = ces.evalMgStatisticsForDocs.get(docName);
			}else{
				docStat = new DocEvalMgStatistics();
				ces.evalMgStatisticsForDocs.put(docName, docStat);
			}
					
			/**
			 * Evaluate MG
			 */
			log.debug(docName);
			//System.err.println("docHypo number: "+docInfoHypo.getAllGroundedPointMutations().size());
			//System.err.println("docEval number: "+docInfoEval.getAllGroundedPointMutations().size());
			
			if(docInfoEval == null || docInfoEval.getAllGroundedPointMutations() == null){
				continue;
			}
			
			boolean hasAtLeastOneMutationFound = false;
			for (GroundedPointMutation gpmEval : docInfoEval.getAllGroundedPointMutations()) {
				
				
				// Temporary. For Finnland.
				String groundedTo =null;
				String correct = "-";
				int offset = -1; 
				
				docStat.numberOfMutations_Eval++;	
				//log.debug(gpmEval);
				boolean grounded = false;
				for (GroundedPointMutation gpmHypo : docInfoHypo.getAllGroundedPointMutations()) {					
					//log.warn("CHeck: "+gpmHypo.getNormalizedFormMentioned()+" - "+gpmHypo.getNormalizedForm());
					//log.warn("CHeck2: "+gpmHypo.getMentionedPosition()+" "+gpmHypo.getCorrectPosition()+" "+gpmHypo.getOffset());
					
					String normalizedForm = gpmHypo.getWildtypeResidue()+""+(gpmHypo.getMentionedPosition()-gpmHypo.getOffset())+gpmHypo.getMutantResidue();
					//log.warn("normalizedForm: "+normalizedForm);
					if (gpmHypo.getNormalizedFormMentioned().equalsIgnoreCase(gpmEval.getNormalizedFormMentioned())
							//|| normalizedForm.equalsIgnoreCase(gpmEval.getNormalizedFormMentioned())
							){
						hasAtLeastOneMutationFound = true;
						//System.err.println(gpmHypo.getSwissProtIdAsString());
						if(//&& gpmHypo.getSwissProtId() != null
								//&& !gpmHypo.getSwissProtId().equals("")
								//&& !gpmHypo.getSwissProtId().equals(" ")								
								gpmHypo.getSwissProtIdAsString() != null){
							
							// Check if grounded.(if SwissProtId is there.)
							docStat.numberOfMutations_Grounded++;						
							grounded = true;
							
							groundedTo = gpmHypo.getSwissProtIdAsString();
							
							//log.warn("CHeck: "+gpmEval.getNormalizedFormMentioned()+" - "+gpmEval.getSwissProtId()+" vs. "+gpmHypo.getSwissProtId());
							//log.warn("CHeck: "+gpmEval.getNormalizedFormMentioned()+" - "+gpmEval.getSwissProtId()+" vs. "+gpmHypo.getSwissProtId());
							//log.warn("CHeck2: "+gpmHypo.getMentionedPosition()+" "+gpmHypo.getCorrectPosition());

							// Check if grounded correct.
							//if (gpmHypo.getSwissProtId().equalsIgnoreCase(gpmEval.getSwissProtId())) {	
							if (gpmHypo.getSwissProtId().containsKey(gpmEval.getSwissProtId().keySet().iterator().next())) {	// changed for multiple UniProtIds												

								docStat.numberOfMutations_GroundedCorrect++;	
								docStat.mutationGroundedCorrect.add(gpmEval.getNormalizedFormMentioned());
								log.warn(docName+ " Ok: "+gpmEval.getNormalizedFormMentioned()+" - "+gpmEval.getSwissProtId()+" vs. "+gpmHypo.getSwissProtId());
								
								

								// Temporary. For Finnland.								
								correct = "+";
								
								offset = gpmHypo.getCorrectPosition() - gpmHypo.getMentionedPosition();
								
							}else{
								docStat.mutationGroundedWrong.add(gpmEval.getNormalizedFormMentioned());
								
								// Temporary. For Finnland.
								correct = "-";
								
								log.warn(docName+" Wrong: "+gpmEval.getNormalizedFormMentioned()+" - "+gpmEval.getSwissProtId()+" vs. "+gpmHypo.getSwissProtId());
							}
							break;
						}
						log.warn("In set but not grounded: "+gpmEval.getNormalizedFormMentioned()+" - "+gpmEval.getSwissProtId()+" vs. "+gpmHypo.getSwissProtId());
					}
				}
				if(!grounded){
					docStat.mutationNotGrounded.add(gpmEval.getNormalizedFormMentioned());					
					log.warn("Not grounded: "+gpmEval.getNormalizedFormMentioned());
					
					// Temporary. For Finnland.
					correct = "0";
				}
				
				
				// Temporary. For Finnland.
				String[] csvLine = {
						docName,
						gpmEval.getSwissProtId().keySet().iterator().next(), 
						Character.toString(gpmEval.getWildtypeResidue()),
						Integer.toString(gpmEval.getMentionedPosition()),
						Character.toString(gpmEval.getMutantResidue()),
						groundedTo,
						correct,						
						}; 
				cvsWriter.writeNext(csvLine);
			}
			
			
			if(!hasAtLeastOneMutationFound){
				log.warn("No mutation found in text for "+docName);	
			}
		}
		cvsWriter.close();
		
		/**
		 * Calculate.
		 */
		for(Entry e : ces.evalMgStatisticsForDocs.entrySet()){
			DocEvalMgStatistics stat = (DocEvalMgStatistics) e.getValue();
			stat.mutationGroundingPrecision = (float)stat.numberOfMutations_GroundedCorrect/(float)stat.numberOfMutations_Grounded;
			stat.mutationGroundingRecall = (float)stat.numberOfMutations_GroundedCorrect/(float)stat.numberOfMutations_Eval;
		}
		
		
		/*
		 * Summary.
		 */
		for(Entry e : ces.evalMgStatisticsForDocs.entrySet()){
			DocEvalMgStatistics stat = (DocEvalMgStatistics) e.getValue();
			ces.numberOfAllMutations_Eval = ces.numberOfAllMutations_Eval + stat.numberOfMutations_Eval;
			ces.numberOfAllMutations_Grounded = ces.numberOfAllMutations_Grounded + stat.numberOfMutations_Grounded;
			ces.numberOfAllMutations_GroundedCorrect = ces.numberOfAllMutations_GroundedCorrect + stat.numberOfMutations_GroundedCorrect;
			
			if(Float.isNaN(stat.mutationGroundingPrecision))stat.mutationGroundingPrecision=0;
			if(Float.isNaN(stat.mutationGroundingRecall))stat.mutationGroundingRecall=0;
			
			ces.mutationGroundingAllPrecisionMacro = ces.mutationGroundingAllPrecisionMacro + stat.mutationGroundingPrecision;
			ces.mutationGroundingAllRecallMacro = ces.mutationGroundingAllRecallMacro + stat.mutationGroundingRecall;
		}
		ces.mutationGroundingAllPrecisionMacro = (float)ces.mutationGroundingAllPrecisionMacro/(float)ces.evalMgStatisticsForDocs.entrySet().size();
		ces.mutationGroundingAllRecallMacro    = (float)ces.mutationGroundingAllRecallMacro/(float)ces.evalMgStatisticsForDocs.entrySet().size();

		
		
		// Micro.
		ces.mutationGroundingAllPrecisionMicro = (float)ces.numberOfAllMutations_GroundedCorrect/(float)ces.numberOfAllMutations_Grounded;
		ces.mutationGroundingAllRecallMicro    = (float)ces.numberOfAllMutations_GroundedCorrect/(float)ces.numberOfAllMutations_Eval;
			
		
		
		
		/*
		for(Entry e : ces.evalMgStatisticsForDocs.entrySet()){
			DocEvalMgStatistics stat = (DocEvalMgStatistics) e.getValue();
			
			if(Float.isNaN(stat.mutationGroundingPrecision))stat.mutationGroundingPrecision=0;
			//if(Float.isNaN(stat.mutationGroundingPrecision))stat.mutationGroundingPrecision=0;		

			
			ces.mutationGroundingAllPrecision = ces.mutationGroundingAllPrecision + stat.mutationGroundingPrecision;
			ces.mutationGroundingAllRecall = ces.mutationGroundingAllRecall + stat.mutationGroundingRecall;
			
			ces.numberOfAllMutations_Eval = ces.numberOfAllMutations_Eval + stat.numberOfMutations_Eval;
			ces.numberOfAllMutations_Grounded = ces.numberOfAllMutations_Grounded + stat.numberOfMutations_Grounded;
			ces.numberOfAllMutations_GroundedCorrect = ces.numberOfAllMutations_GroundedCorrect + stat.numberOfMutations_GroundedCorrect;
		}
		ces.mutationGroundingAllPrecision = (float)ces.mutationGroundingAllPrecision/(float)ces.evalMgStatisticsForDocs.entrySet().size();
		ces.mutationGroundingAllRecall    = (float)ces.mutationGroundingAllRecall/(float)ces.evalMgStatisticsForDocs.entrySet().size();
		*/
		return ces;
	}
	

	/*
	private static void saveEvalTable(Map<String,DocumentInformation> docInfos, String outputDirName) throws IOException {
		
		 //MG. To CSV.
		 
		CSVWriter cvsWriter = new CSVWriter(new FileWriter(outputDirName + "/evaluation-mg.csv"), '\t');
		cvsWriter.writeNext(new String[] { "document_id", "ProteinID", "wt_residue", "position", "m_residue","offset"});


		//cvsWriter.writeNext(new String[] { "Files:", results.keySet().toString()});
		
		/
		// Write Files which were processed.
		 
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
			for(GroundedPointMutation gpm : docInfo.getAllGroundedPointMutations()){	
				int offset = gpm.getCorrectPosition()-gpm.getMentionedPosition();
				String[] csvLine = {
						docInfo.getName(),
						//gpm.getSwissProtId(), 
						gpm.getSwissProtIdAsString(), // changed for multiple UniProtIds
						Character.toString(gpm.getWildtypeResidue()),
						Integer.toString(gpm.getMentionedPosition()),
						Character.toString(gpm.getMutantResidue()),
						Integer.toString(offset),
						}; 
				cvsWriter.writeNext(csvLine);
			}			
		}
		cvsWriter.close();
	}
*/
}

