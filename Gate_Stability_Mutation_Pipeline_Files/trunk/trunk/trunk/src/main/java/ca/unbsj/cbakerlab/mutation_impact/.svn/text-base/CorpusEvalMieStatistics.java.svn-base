package ca.unbsj.cbakerlab.mutation_impact;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVWriter;

public class CorpusEvalMieStatistics{
    
	int numberOfAllImpacts_Eval = 0;
	int numberOfAllImpacts_Found = 0;
	int numberOfAllImpacts_Correct = 0;
	
    float impactsAllPrecision = 0;
    float impactsAllRecall = 0;
    float foundAllImpactRate = 0;
    
    Map<String,DocEvalMieStatistics> evalMieStatisticsForDocs;
    
    public void toCsv(String outputFileName) throws IOException {
		/**
		 * MG. To CSV.
		 */
		CSVWriter cvsWriter = new CSVWriter(new FileWriter(outputFileName), '\t');
		cvsWriter.writeNext(new String[] { "document_id", "Precision", "Recall", "FRate"});

		for (Entry<String, DocEvalMieStatistics> e : evalMieStatisticsForDocs.entrySet()) {
			DocEvalMieStatistics docInfoStat = e.getValue();
			cvsWriter.writeNext(new String[]{e.getKey(),Float.toString(docInfoStat.impactsPrecision), Float.toString(docInfoStat.impactsRecall), Float.toString(docInfoStat.foundImpactRate)});
		}

		cvsWriter.writeNext(new String[]{"Number of documents: "+evalMieStatisticsForDocs.size()});
		cvsWriter.writeNext(new String[]{"numberOfAllImpacts_Eval: "+numberOfAllImpacts_Eval});
		cvsWriter.writeNext(new String[]{"numberOfAllImpacts_Found: "+numberOfAllImpacts_Found});
		cvsWriter.writeNext(new String[]{"numberOfAllImpacts_Correct: "+numberOfAllImpacts_Correct});
		cvsWriter.writeNext(new String[]{"Precision: "+Float.toString(impactsAllPrecision)});
		cvsWriter.writeNext(new String[]{"Recall: "+Float.toString(impactsAllRecall)});				
		cvsWriter.writeNext(new String[]{"FindingRate: "+Float.toString(foundAllImpactRate)});
		
		cvsWriter.close();
	}
    
    public String asString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (Entry<String, DocEvalMieStatistics> e : evalMieStatisticsForDocs.entrySet()) {
			DocEvalMieStatistics docInfo = e.getValue();
			sb.append(e.getKey() +"	"+ Float.toString(docInfo.impactsPrecision)+"	"+ Float.toString(docInfo.impactsRecall)+"\n");
		}

		sb.append("Number of documents: "+evalMieStatisticsForDocs.size()+"\n");
		sb.append("numberOfAllImpacts_Eval: "+numberOfAllImpacts_Eval+"\n");
		sb.append("numberOfAllImpacts_Found: "+numberOfAllImpacts_Found+"\n");
		sb.append("numberOfAllImpacts_Correct: "+numberOfAllImpacts_Correct+"\n");
		sb.append("Precision: "+Float.toString(impactsAllPrecision)+"\n");
		sb.append("Recall: "+Float.toString(impactsAllRecall)+"\n");	
		return sb.toString();
	}
    
}


class DocEvalMieStatistics{
	int numberOfImpacts_Eval = 0;
	int numberOfImpacts_Found = 0;
	int numberOfImpacts_Correct = 0;
	
	float impactsPrecision = 0;
    float impactsRecall = 0;
    float foundImpactRate = 0;
}
