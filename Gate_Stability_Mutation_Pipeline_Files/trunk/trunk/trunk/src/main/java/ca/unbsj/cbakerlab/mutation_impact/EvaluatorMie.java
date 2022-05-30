package ca.unbsj.cbakerlab.mutation_impact;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ca.unbsj.cbakerlab.mutation.GroundedMutation;
import ca.unbsj.cbakerlab.mutation.GroundedPointMutation;
import ca.unbsj.cbakerlab.mutation.ImpactDirection;
import ca.unbsj.cbakerlab.mutation.MutationImpact;


public class EvaluatorMie {
	
	private static final Logger log = Logger.getLogger(EvaluatorMie.class);

	
	
	/**
	 * 
	 * @param results
	 * @param evalMap
	 * @param granularity 
	 * 	0 - +/-/0
	 *  1 - 0/(+/-)
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public static CorpusEvalMieStatistics evaluate(File evalFile, File hypoFile, int granularity) throws IOException, URISyntaxException {
		
		CorpusEvalMieStatistics ces = new CorpusEvalMieStatistics();		
		ces.evalMieStatisticsForDocs = new TreeMap<String,DocEvalMieStatistics>();
		
		Corpus evalCorpus = new Corpus("evalCorpus");
		Corpus hypoCorpus = new Corpus("hypoCorpus");

		evalCorpus.loadCsv(evalFile);
		hypoCorpus.loadCsv(hypoFile);	
		
		for(Entry entry : hypoCorpus.getDocInfos().entrySet()){
			/**
			 * Process next document.
			 */
			String docName = (String) entry.getKey();
			DocumentInformation docInfoHypo = (DocumentInformation) entry.getValue();			
			DocumentInformation docInfoEval = (DocumentInformation) evalCorpus.getDocInfos().get(docName);
			
			
			// Initialise DocumentInfoStatistics
			DocEvalMieStatistics docStat;
			if(ces.evalMieStatisticsForDocs.containsKey(docName)){
				docStat = ces.evalMieStatisticsForDocs.get(docName);
			}else{
				docStat = new DocEvalMieStatistics();
				ces.evalMieStatisticsForDocs.put(docName, docStat);
			}			

			
			/**
			 *  Evaluate MIE
			 */
			log.debug("docName: "+docName);
			log.debug("docInfoEval.getAllGroundedMutations().size: "+docInfoEval.getAllGroundedMutations().size());
			for(GroundedMutation gmEval : docInfoEval.getAllGroundedMutations()){
				GroundedPointMutation gpmEval = gmEval.getCompoundMutation().next();
				MutationImpact miEval = gmEval.getMutationImpacts().next();
						
				docStat.numberOfImpacts_Eval++;
				
				if(docInfoHypo.getAllGroundedMutations() == null || docInfoHypo.getAllGroundedMutations().isEmpty())continue;
				int mark = 0;				
				for(GroundedMutation gmHypo : docInfoHypo.getAllGroundedMutations()){	
					GroundedPointMutation gpmHypo = gmHypo.getCompoundMutation().next();					
					MutationImpact miHypo = gmHypo.getMutationImpacts().next();
					log.trace(gpmEval.getNormalizedFormMentioned());
					log.trace(gpmHypo.getNormalizedFormMentioned());
					log.trace(miEval.getAffectedProteinProperty());
					log.trace(miHypo.getAffectedProteinProperty());
					log.trace(miEval.getDirection()+ " "+miHypo.getDirection());
					// for dhla-corpus
					if(granularity == 0){
						// If GPMs and AffectedProperties are the same.
						
						// TEMPORARY!!!!
						//if(gpmHypo.getNormalizedFormMentioned().equalsIgnoreCase(gpmEval.getNormalizedFormMentioned())		
								
						if(
						   (
							(gpmHypo.getNormalizedFormMentioned().startsWith("GO") && gpmEval.getNormalizedFormMentioned().startsWith("GO"))
							|| (gpmHypo.getNormalizedFormMentioned().equalsIgnoreCase(gpmEval.getNormalizedFormMentioned())))
							//|| (gpmHypo.getNormalizedFormMentioned().startsWith("P11") && gpmEval.getNormalizedFormMentioned().startsWith("GO"))

								&& miHypo.getAffectedProteinProperty().equals(miEval.getAffectedProteinProperty())
										){										
							if(mark == 0){
								mark = 1;
								docStat.numberOfImpacts_Found++;
								log.debug("####Found: "+gpmHypo.getNormalizedFormMentioned()+" "+miHypo.toPrettyString());								
								//log.debug("gpmHypoMentioned: "+gpmHypo.getNormalizedFormMentioned());
								//log.debug("miHypo: "+miHypo.toPrettyString());
							}
							
							//log.debug(miEval.getDirection()+ " "+miHypo.getDirection());
							
							if(miHypo.getDirection() == null)continue;						
														
							// Check if correct.
							if(miHypo.getDirection().equals(miEval.getDirection())){
								docStat.numberOfImpacts_Correct++;
								log.debug("#CORRECT: "+gpmHypo.getNormalizedFormMentioned()+" "+miHypo.toPrettyString());
								break;
							}							
													
							
						}
						// for yana (mc4r)-corpus
					}else if(granularity == 1){
						// If GPMs and AffectedProperties are the same.
						if(gpmHypo.getNormalizedFormMentioned().equalsIgnoreCase(gpmEval.getNormalizedFormMentioned())							
								//&& miHypo.getAffectedProteinProperty().equals(miEval.getAffectedProteinProperty())
										){													
							if(mark == 0){
								mark = 1;
								docStat.numberOfImpacts_Found++;
								log.trace("gpmHypoMentioned: "+gpmHypo.getNormalizedFormMentioned());
								log.trace("miHypo: "+miHypo.toPrettyString());
								//log.debug("AffectedProteinProperty: "+miHypo.getAffectedProteinProperty());
							}	

							log.trace(miEval.getDirection() + " " + miHypo.getDirection());

							if (miHypo.getDirection() == null)
								continue;

							// Check if correct: Yana Corpus
							if (miHypo.getDirection().equals(miEval.getDirection())) {
								docStat.numberOfImpacts_Correct++;
								////if(miHypo.getDirection().equals(ImpactDirection.POSITIVE))CorpusStatistics.positiveImpacts++;
								/////if(miHypo.getDirection().equals(ImpactDirection.NEGATIVE))CorpusStatistics.negativeImpacts++;
								/////if(miHypo.getDirection().equals(ImpactDirection.NEUTRAL))CorpusStatistics.neutralImpacts++;
								break;
							} else if ((miHypo.getDirection().equals(ImpactDirection.POSITIVE) || miHypo.getDirection().equals(ImpactDirection.NEGATIVE))
									&& miEval.getDirection() == null) {
								docStat.numberOfImpacts_Correct++;
								// for debugging (for yana)
								////if(miHypo.getDirection().equals(ImpactDirection.POSITIVE))CorpusStatistics.positiveImpacts++;
								/////if(miHypo.getDirection().equals(ImpactDirection.NEGATIVE))CorpusStatistics.negativeImpacts++;
								////if(miHypo.getDirection().equals(ImpactDirection.NEUTRAL))CorpusStatistics.neutralImpacts++;
								break;
							}					
							
						}
					}				

				}
				mark=0;
			}
			log.debug("");
	
		}

		/**
		 * Calculate.
		 */
		for(Entry e : ces.evalMieStatisticsForDocs.entrySet()){
			DocEvalMieStatistics stat = (DocEvalMieStatistics) e.getValue();
			stat.impactsPrecision = (float)stat.numberOfImpacts_Correct/(float)stat.numberOfImpacts_Found;
			stat.impactsRecall = (float)stat.numberOfImpacts_Correct/(float)stat.numberOfImpacts_Eval;
			stat.foundImpactRate = (float)stat.numberOfImpacts_Found/(float)stat.numberOfImpacts_Eval;
		}
		
		
		/**
		 * Summary.
		 */
		for(Entry e : ces.evalMieStatisticsForDocs.entrySet()){
			DocEvalMieStatistics stat = (DocEvalMieStatistics) e.getValue();
			ces.numberOfAllImpacts_Eval = ces.numberOfAllImpacts_Eval + stat.numberOfImpacts_Eval;
			ces.numberOfAllImpacts_Found = ces.numberOfAllImpacts_Found + stat.numberOfImpacts_Found;
			ces.numberOfAllImpacts_Correct = ces.numberOfAllImpacts_Correct + stat.numberOfImpacts_Correct;
		}
		ces.impactsAllPrecision = (float)ces.numberOfAllImpacts_Correct/(float)ces.numberOfAllImpacts_Found;
		ces.impactsAllRecall    = (float)ces.numberOfAllImpacts_Correct/(float)ces.numberOfAllImpacts_Eval;
		ces.foundAllImpactRate = (float)ces.numberOfAllImpacts_Found/(float)ces.numberOfAllImpacts_Eval;
		return ces;
	}



	

}

