package ca.unbsj.cbakerlab.mutation_impact;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;


public class CorpusEvalMgStatistics{
	int numberOfAllMutations_Eval = 0;
	int numberOfAllMutations_Grounded = 0;
	int numberOfAllMutations_GroundedCorrect = 0;
	
    float mutationGroundingAllPrecisionMicro = 0;
    float mutationGroundingAllRecallMicro = 0;	

    float mutationGroundingAllPrecisionMacro = 0;
    float mutationGroundingAllRecallMacro = 0;	
    
	//int numberOfAllImpacts_Eval = 0;
	//int numberOfAllImpacts_Found = 0;
	//int numberOfAllImpacts_Correct = 0;
	
   // float impactsAllPrecision = 0;
    //float impactsAllRecall = 0;
    //float foundAllImpactRate = 0;
    
    Map<String,DocEvalMgStatistics> evalMgStatisticsForDocs;
    
    public void toCsv(String outputFileName) throws IOException {
		/**
		 * MG. To CSV.
		 */
		CSVWriter cvsWriter = new CSVWriter(new FileWriter(outputFileName), '\t');
		cvsWriter.writeNext(new String[] { "document_id", "Precision", "Recall"});

		for (Entry<String, DocEvalMgStatistics> e : evalMgStatisticsForDocs.entrySet()) {
			DocEvalMgStatistics docInfo = e.getValue();
			cvsWriter.writeNext(new String[]{e.getKey(),Float.toString(docInfo.mutationGroundingPrecision), Float.toString(docInfo.mutationGroundingRecall)});
		}

		cvsWriter.writeNext(new String[]{"Number of documents: "+evalMgStatisticsForDocs.size()});
		cvsWriter.writeNext(new String[]{"numberOfAllMutations_Eval: "+numberOfAllMutations_Eval});
		cvsWriter.writeNext(new String[]{"numberOfAllMutations_Grounded: "+numberOfAllMutations_Grounded});
		cvsWriter.writeNext(new String[]{"numberOfAllMutations_GroundedCorrect: "+numberOfAllMutations_GroundedCorrect});
		cvsWriter.writeNext(new String[]{"MicroPrecision: "+Float.toString(mutationGroundingAllPrecisionMicro)});
		cvsWriter.writeNext(new String[]{"MicroRecall: "+Float.toString(mutationGroundingAllRecallMicro)});	
		cvsWriter.writeNext(new String[]{"MacroPrecision: "+Float.toString(mutationGroundingAllPrecisionMacro)});
		cvsWriter.writeNext(new String[]{"MacroRecall: "+Float.toString(mutationGroundingAllRecallMacro)});	
		cvsWriter.writeNext(new String[]{"GroundingRate: "+Float.toString(numberOfAllMutations_Grounded/numberOfAllMutations_Eval)});				

		cvsWriter.close();
	}
    
    
    public String asString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (Entry<String, DocEvalMgStatistics> e : evalMgStatisticsForDocs.entrySet()) {
			DocEvalMgStatistics docInfo = e.getValue();
			sb.append(e.getKey() +"	"+ Float.toString(docInfo.mutationGroundingPrecision)+"	"+ Float.toString(docInfo.mutationGroundingRecall)+"\n");
		}

		sb.append("Number of documents: "+evalMgStatisticsForDocs.size()+"\n");
		sb.append("numberOfAllMutations_Eval: "+numberOfAllMutations_Eval+"\n");
		sb.append("numberOfAllMutations_Grounded: "+numberOfAllMutations_Grounded+"\n");
		sb.append("numberOfAllMutations_GroundedCorrect: "+numberOfAllMutations_GroundedCorrect+"\n---------------\n");
		sb.append("MicroPrecision: "+Float.toString(mutationGroundingAllPrecisionMicro)+"\n");
		sb.append("MicroRecall: "+Float.toString(mutationGroundingAllRecallMicro)+"\n---------------\n");	
		sb.append("MacroPrecision: "+Float.toString(mutationGroundingAllPrecisionMacro)+"\n");
		sb.append("MacroRecall: "+Float.toString(mutationGroundingAllRecallMacro)+"\n---------------\n");	
		sb.append("GroundingRateMacro: "+Float.toString((float)numberOfAllMutations_Grounded/(float)numberOfAllMutations_Eval)+"\n");	
		return sb.toString();
	}
    
    
}


class DocEvalMgStatistics{
	
	Set<String> mutationEval;
	Set<String> mutationResult;
	Set<String> mutationProteinEval;
	Set<String> mutationProteinResult;
	
	Set<String> mutationGroundedCorrect;
	Set<String> mutationGroundedWrong;
	Set<String> mutationNotGrounded;
	
	int numberOfMutations_Eval = 0;
	int numberOfMutations_Grounded = 0;
	int numberOfMutations_GroundedCorrect = 0;
	
    float mutationGroundingPrecision = 0;
    float mutationGroundingRecall = 0;
    float mutationGroundingRate = 0;

	
	
	DocEvalMgStatistics(){
		mutationEval = new HashSet<String>();
		mutationResult = new HashSet<String>();
		mutationProteinEval = new HashSet<String>();
		mutationProteinResult = new HashSet<String>();
		
		mutationGroundedCorrect = new HashSet<String>();
		mutationGroundedWrong = new HashSet<String>();
		mutationNotGrounded = new HashSet<String>();
	}
	
}

