package ca.unbsj.cbakerlab.mutation_impact;

import gate.AnnotationSet;
import gate.Factory;
import gate.Gate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;




public class Main2 {
	private static final Logger log = Logger.getLogger(Main2.class);

	static long corpusQueryTime = 0;

	public static void run(String args){
		try {
			long time = System.currentTimeMillis();

			Parameters param = new Parameters();
			param.addParameter("inputFileOrDirName", null, new ArrayList(Arrays.asList("inputFileOrDirName")), new ArrayList(Arrays.asList("i")), "string");
			param.addParameter("tmpFileOrDirName", null, new ArrayList(Arrays.asList("tmpFileOrDirName")), new ArrayList(Arrays.asList("t")), "string");
			param.addParameter("outputDirName", null, new ArrayList(Arrays.asList("outputDirName")), new ArrayList(Arrays.asList("o")), "string");

			param.addParameter("evalFileName", null, new ArrayList(Arrays.asList("evalFileName")), new ArrayList(Arrays.asList("v")), "string");

			//param.addParameter("initDocumentResetter", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initTokenizer", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initSentenceSplitter", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initPosTagger", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initNpChunker", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initProteinExtractor", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initOrganismExtractor", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initMg", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));
			//param.addParameter("initMie", null, new ArrayList(Arrays.asList("")), new ArrayList(Arrays.asList("")));

			param.addParameter("runMgPipeline", null, new ArrayList(Arrays.asList("mgpipe")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("runMiePipeline", null, new ArrayList(Arrays.asList("miepipe")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("runMgEvaluator", null, new ArrayList(Arrays.asList("mgeval")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("runMieEvaluator", null, new ArrayList(Arrays.asList("mieeval")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("convertMgResults2Tab", null, new ArrayList(Arrays.asList("mg2tab")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("convertMieResults2Tab", null, new ArrayList(Arrays.asList("mie2tab")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("convertMgResults2Rdf", null, new ArrayList(Arrays.asList("mg2rdf")), new ArrayList(Arrays.asList("")), "boolean");
			param.addParameter("convertMieResults2Rdf", null, new ArrayList(Arrays.asList("mie2rdf")), new ArrayList(Arrays.asList("")), "boolean");

			param.addParameter("debug", null, new ArrayList(Arrays.asList("debug")), new ArrayList(Arrays.asList("d")), "boolean");
			param.addParameter("useCaching", null, new ArrayList(Arrays.asList("cache")), new ArrayList(Arrays.asList("c")), "boolean");
			param.addParameter("saveStatistics", null, new ArrayList(Arrays.asList("saveStatistics")), new ArrayList(Arrays.asList("s")), "boolean");

			param.argsToValues(args);






			// ---------------------------------------------------------------------------------------------------------


			// ** set up logging.
			if (((Boolean)param.getParameterValue("debug")) == true) {
				PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));
				java.util.logging.Logger logg =  java.util.logging.Logger.getLogger("JAPELogger");
				logg.setLevel(Level.OFF);
				logg.log(Level.INFO,"LOG INIT");
			}else{
				PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4jOff.properties"));
				java.util.logging.Logger logg =  java.util.logging.Logger.getLogger("JAPELogger");
				logg.setLevel(Level.OFF);
				logg.log(Level.INFO,"LOG INIT");
			}

			// Create output directory if it does not exist.
			File outputDir = null;
			if(param.getParameterValue("outputDirName") != null){
				outputDir = Utils.createFileOrDirectoryIfNotExist((String)param.getParameterValue("outputDirName"));
			}

			// Create temp output directory if it does not exist.
			File tmpDir = null;
			if(param.getParameterValue("tmpFileOrDirName") != null){
				tmpDir = Utils.createFileOrDirectoryIfNotExist((String)param.getParameterValue("tmpFileOrDirName"));
			}



			Properties pro = new Properties();
			try {
				pro.load(new FileInputStream(new File(log.getClass().getClassLoader().getResource("project.properties").toURI())));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			//pro.load(new FileInputStream(new File(ClassLoader.getSystemClassLoader().getResource("project.properties").toURI())));

			/*
			 * *************************************************************************************
			 * PIPELINES
			 * *************************************************************************************
			 */

			if((Boolean)param.getParameterValue("runMgPipeline") || (Boolean)param.getParameterValue("runMiePipeline")){

				// Pipeline is by default consists of tokenizer, sentence splitter, np chunker, protein and organism extractors, mutation finder, and mutation grounder.

				/*
				if((Boolean)param.getParameterValue("runMgPipeline")){
					param.getParameter("initTokenizer").setValue(true);
					param.getParameter("initSentenceSplitter").setValue(true);
					param.getParameter("initPosTagger").setValue(true);
					param.getParameter("initNpChunker").setValue(true);
					param.getParameter("initProteinExtractor").setValue(true);
					param.getParameter("initOrganismExtractor").setValue(true);
					param.getParameter("initMutationExtractor").setValue(true);
					param.getParameter("initMg").setValue(true);
				}
				
				if((Boolean)param.getParameterValue("runMiePipeline")){
					param.getParameter("initTokenizer").setValue(true);
					param.getParameter("initSentenceSplitter").setValue(true);
					param.getParameter("initPosTagger").setValue(true);
					param.getParameter("initNpChunker").setValue(true);
					param.getParameter("initProteinExtractor").setValue(true);
					param.getParameter("initOrganismExtractor").setValue(true);
					param.getParameter("initMutationExtractor").setValue(true);
					param.getParameter("initMg").setValue(true);
					param.getParameter("initMie").setValue(true);
				}*/

				//
				// Prepare statistics collector.
				//
				AnalysisStatistics stat = null;
				if((Boolean)param.getParameterValue("runMgPipeline") || (Boolean)param.getParameterValue("runMiePipeline")){
					if((Boolean)param.getParameterValue("saveStatistics")){
						stat = new AnalysisStatistics();
						stat.loadMap(new File((String)param.getParameterValue("evalFileName")));
						log.debug(stat.getMap());
					}
					if((Boolean)param.getParameterValue("useCaching")){
						MutationGrounder.cacheFileName = tmpDir+"/cacheQuerying";
						MutationGrounder.cacheForGroundingFileName = tmpDir+"/cacheAlignment";
					}
				}



				String gateHome = pro.getProperty("GATE_HOME");

				//
				// Init pipeline.
				//
				Pipeline pipeline = new Pipeline(gateHome);
				if((Boolean)param.getParameterValue("runMgPipeline")){
					pipeline.init(true,true,true,true,true,true,true,true,true,false, true, true, true);
				}else if((Boolean)param.getParameterValue("runMiePipeline")){
					pipeline.init(true,true,true,true,true,true,true,true,true,true, true, true, true);
				}
				
				/*pipeline.init(
						(Boolean)param.getParameterValue("initDocumentResetter"), 
						(Boolean)param.getParameterValue("initTokenizer"), 
						(Boolean)param.getParameterValue("initSentenceSplitter"), 
						(Boolean)param.getParameterValue("initPosTagger"), 
						(Boolean)param.getParameterValue("initNpChunker"), 
						(Boolean)param.getParameterValue("initProteinExtractor"),  
						(Boolean)param.getParameterValue("initOrganismExtractor"), 
						(Boolean)param.getParameterValue("initMutationExtractor"), 
						(Boolean)param.getParameterValue("initMg"), 
						(Boolean)param.getParameterValue("initMie")
						);				
				*/

				//
				// Prepare and Process corpus.
				//
				Map<String,String> fileIndex = Utils.initFileIndex((String)param.getParameterValue("inputFileOrDirName"));

				// Monitor which file is currently processed.
				int numberOfFilesToProcess = fileIndex.size();
				if(processOnlyDocuments.size()>1){
					numberOfFilesToProcess = processOnlyDocuments.size()-1;
				}
				int numberOfFileProcessed = 1;

				for(String fileName : fileIndex.keySet()){

					if(processOnlyDocuments.size()>1 && !processOnlyDocuments.contains(fileName)){
						continue;
					}

					log.info("\n==========================================================\n"+
							"DOCUMENT: "+fileName+"		"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+
							"\t"+numberOfFileProcessed+" of "+numberOfFilesToProcess+
							"\n==========================================================\n");

					gate.Document doc = Factory.newDocument(new URL(fileIndex.get(fileName)));
					doc.setName(fileName);

					if((Boolean)param.getParameterValue("saveStatistics")){
						doc.getFeatures().put("statistics", stat);
					}

					pipeline.execute(doc);


					if(outputDir != null){
						Utils.saveGateXml(doc, new File(outputDir+"/"+doc.getName()+".xml"), false);
					}		else{
						Utils.saveGateXml(doc, new File("/home/artjomk/"+doc.getName()+".xml"), false);
					}

					// for testing: collect mutation-impacts
					AnnotationSet impactsMf = doc.getAnnotations().get("Mutation-Impact");
					log.debug("$$$$$$$$$$$$$$$$$$$$$$$$$ "+fileName+" mutimps: "+impactsMf.size());

					Factory.deleteResource(doc);

					log.info("Processing Document Time " + (System.currentTimeMillis() - time) / 1000 + " seconds = "+(System.currentTimeMillis() - time) / 1000 / 60 +" minutes");
					log.info("Query Document Time " + corpusQueryTime / 1000 + " seconds");
				}

				if(stat != null){
					log.info(stat.asString());
				}

				log.info("Processing Corpus Time " + (System.currentTimeMillis() - time) / 1000 + " seconds");
			}


			/*
			 * *************************************************************************************
			 * MG RESULTS TO TAB
			 * *************************************************************************************
			 */
			if((Boolean)param.getParameterValue("convertMgResults2Tab") || (Boolean)param.getParameterValue("convertMieResults2Tab")){

				String gateHome = pro.getProperty("GATE_HOME");
				if (!Gate.isInitialised()) {
					log.info("Initializing GATE...");

					// Set GATE HOME directory.
					Gate.setGateHome(new File(gateHome));
					// Set GATE HOME directory.
					Gate.setPluginsHome(new File(gateHome, "plugins"));System.out.println(Gate.getPluginsHome());
					// Initialise GATE.
					try{
						Gate.init();
						log.info("Initializing GATE... Done.");
					}
					catch(Exception e){
						log.fatal("GATE could not be initialized. Check GATE_HOME: " + Gate.getGateHome());
						throw new gate.util.GateRuntimeException("GATE could not be initialized. Cheack GATE_HOME: " + Gate.getGateHome());

					}
				}



				Map<String,String> fileIndex = Utils.initFileIndex((String)param.getParameterValue("inputFileOrDirName"));
				Corpus corpus = new Corpus((String)param.getParameterValue("inputFileOrDirName"));

				for(String fileName : fileIndex.keySet()){

					if(processOnlyDocuments.size()>1 && !processOnlyDocuments.contains(fileName)){
						continue;
					}

					log.info("\n==========================================================\n"+
							"DOCUMENT: "+fileName+"		"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+
							"\n==========================================================\n");

					gate.Document doc = Factory.newDocument(new URL(fileIndex.get(fileName)));
					DocumentInformation docInfo = new DocumentInformation(doc);
					corpus.getDocInfos().put(docInfo.getName().split("\\.")[0], docInfo);
					Factory.deleteResource(doc);

					log.info("Processing Document Time " + (System.currentTimeMillis() - time) / 1000 + " seconds = "+(System.currentTimeMillis() - time) / 1000 / 60 +" minutes");
					log.info("Query Document Time " + corpusQueryTime / 1000 + " seconds");
				}

				corpus.saveMgResults((String)param.getParameterValue("outputDirName") + "/results-mg.csv");
				corpus.saveMieResults((String)param.getParameterValue("outputDirName") + "/results-mie.csv");

				log.info("Processing Corpus Time " + (System.currentTimeMillis() - time) / 1000 + " seconds");
			}



			/*
			 * *************************************************************************************
			 * MG RESULTS TO RDF
			 * *************************************************************************************
			 */
			if((Boolean)param.getParameterValue("convertMgResults2Rdf")){
				Utils.initGate();

				BufferedWriter mergedOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir+"/"+"results-mg.rdf")));
				Model modelMerged = ModelFactory.createDefaultModel();
				modelMerged.setNsPrefix("mieo", Vocab.mieoNS);
				modelMerged.setNsPrefix("sio", Vocab.sioNS);
				modelMerged.setNsPrefix("lsrn", Vocab.lsrnNS);
				modelMerged.setNsPrefix("str", Vocab.strNS);
				modelMerged.setNsPrefix("foaf", Vocab.foafNS);
				modelMerged.setNsPrefix("ao", Vocab.aoNS);
				modelMerged.setNsPrefix("aos", Vocab.aosNS);
				modelMerged.setNsPrefix("aot", Vocab.aotNS);
				modelMerged.setNsPrefix("aof", Vocab.aofNS);
				modelMerged.setNsPrefix("pav", Vocab.pavNS);
				modelMerged.setNsPrefix("go", Vocab.goNS);


				Map<String, String> fileIndex = Utils.initFileIndex((String)param.getParameterValue("inputFileOrDirName"));
				for (String fileName : fileIndex.keySet()) {
					if (processOnlyDocuments.size() > 1 && !processOnlyDocuments.contains(fileName)) {
						continue;
					}

					log.info("\n==========================================================\n" + "DOCUMENT: " + fileName + "		"
							+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
							+ "\n==========================================================\n");

					gate.Document doc = Factory.newDocument(new URL(fileIndex.get(fileName)));
					doc.setName(fileName);

					Model model = Gate2MieoAoRdfConverter.mutationGroundigResults2Rdf(doc,false);
			        
					/*BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/"+doc.getName()+".rdf")));
					model.write(out, "RDF/XML"); // RDF/XML-ABBREV, N-TRIPLE
					//model.write(out, "N-TRIPLE"); // RDF/XML-ABBREV, N-TRIPLE
					out.close();
					*/
					modelMerged.add(model);

					log.info("Processing Document Time " + (System.currentTimeMillis() - time) / 1000 + " seconds = "+ (System.currentTimeMillis() - time) / 1000 / 60 + " minutes");
					log.info("Query Document Time " + corpusQueryTime / 1000 + " seconds");
					Factory.deleteResource(doc);
				}
				modelMerged.write(mergedOut, "RDF/XML"); // RDF/XML-ABBREV
				mergedOut.close();
			}


			/*
			 * *************************************************************************************
			 * MIE RESULTS TO RDF
			 * *************************************************************************************
			 */

			if((Boolean)param.getParameterValue("convertMieResults2Rdf")){
				Utils.initGate();

				BufferedWriter mergedOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir+"/"+"results-mie.rdf")));
				Model modelMerged = ModelFactory.createDefaultModel();
				modelMerged.setNsPrefix("mieo", Vocab.mieoNS);
				modelMerged.setNsPrefix("sio", Vocab.sioNS);
				modelMerged.setNsPrefix("lsrn", Vocab.lsrnNS);
				modelMerged.setNsPrefix("str", Vocab.strNS);
				modelMerged.setNsPrefix("foaf", Vocab.foafNS);
				modelMerged.setNsPrefix("ao", Vocab.aoNS);
				modelMerged.setNsPrefix("aos", Vocab.aosNS);
				modelMerged.setNsPrefix("aot", Vocab.aotNS);
				modelMerged.setNsPrefix("aof", Vocab.aofNS);
				modelMerged.setNsPrefix("pav", Vocab.pavNS);
				modelMerged.setNsPrefix("go", Vocab.goNS);


				Map<String, String> fileIndex = Utils.initFileIndex((String)param.getParameterValue("inputFileOrDirNa"));
				for (String fileName : fileIndex.keySet()) {
					if (processOnlyDocuments.size() > 1 && !processOnlyDocuments.contains(fileName)) {
						continue;
					}

					log.info("\n==========================================================\n" + "DOCUMENT: " + fileName + "		"
							+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
							+ "\n==========================================================\n");

					gate.Document doc = Factory.newDocument(new URL(fileIndex.get(fileName)));
					doc.setName(fileName);

					Model model = Gate2MieoAoRdfConverter.mutationImpactExtractionResults2Rdf(doc, false);
			        
					/*BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/"+doc.getName()+".rdf")));
					model.write(out, "RDF/XML"); // RDF/XML-ABBREV, N-TRIPLE
					//model.write(out, "N-TRIPLE"); // RDF/XML-ABBREV, N-TRIPLE
					out.close();
					*/
					modelMerged.add(model);

					log.info("Processing Document Time " + (System.currentTimeMillis() - time) / 1000 + " seconds = "+ (System.currentTimeMillis() - time) / 1000 / 60 + " minutes");
					log.info("Query Document Time " + corpusQueryTime / 1000 + " seconds");
					Factory.deleteResource(doc);
				}
				modelMerged.write(mergedOut, "RDF/XML"); // RDF/XML-ABBREV
				mergedOut.close();
			}


			/*
			 * *************************************************************************************
			 * EVALUATOR
			 * *************************************************************************************
			 */

			if((Boolean)param.getParameterValue("runMgEvaluator")){
				File evalFile = new File((String)param.getParameterValue("evalFileName"));

				File resultMGFile = null;
				try{
					if(outputDir != null){
						resultMGFile = new File(outputDir+"/results-mg.csv");
					}else{
						resultMGFile = new File((String)param.getParameterValue("mgResultFileName"));
					}
				}catch(Exception e){
					log.warn("No mutation grounding results file specified.");
				}


				if(resultMGFile != null){
					CorpusEvalMgStatistics ces = null;
					if(outputDir != null){
						ces = EvaluatorMg.evaluate(new File(outputDir + "/evaluation-compare-table.csv"), evalFile, resultMGFile);
						ces.toCsv(outputDir+"/evaluation-results-mg.csv");
					}
					else {
						ces = EvaluatorMg.evaluate(new File("evaluation-compare-table.csv"), evalFile, resultMGFile);
						ces.toCsv("evaluation-results-mg.csv");
					}

					log.info(ces.asString());
				} else{
					log.error("Can not load result mg file from path: "+resultMGFile);
				}

			}



			// #################################


			if((Boolean)param.getParameterValue("runMieEvaluator")){
				File evalFile = new File((String)param.getParameterValue("evalFileName"));

				File resultMGFile = null;
				try{
					if(outputDir != null){
						resultMGFile = new File(outputDir+"/results-mg.csv");
					}else{
						resultMGFile = new File((String)param.getParameterValue("mgResultFileName"));
					}
				}catch(Exception e){
					log.warn("No mutation grounding results file specified.");
				}


				if(resultMGFile != null){
					CorpusEvalMgStatistics ces = null;
					if(outputDir != null){
						ces = EvaluatorMg.evaluate(new File(outputDir + "/evaluation-compare-table.csv"), evalFile, resultMGFile);
						ces.toCsv(outputDir+"/evaluation-results-mg.csv");
					}
					else {
						ces = EvaluatorMg.evaluate(new File("evaluation-compare-table.csv"), evalFile, resultMGFile);
						ces.toCsv("evaluation-results-mg.csv");
					}

					log.info(ces.asString());
				} else{
					log.error("Can not load result mg file from path: "+resultMGFile);
				}




				File resultMieFile = null;
				try{
					if(outputDir != null){
						resultMieFile = new File(outputDir+"/results-mie.csv");
					}else{
						resultMieFile = new File((String)param.getParameterValue("mieResultFileName"));
					}
				}catch(Exception e){
					log.warn("No mutation impact extraction results file specified.");
				}


				if(resultMieFile != null){
					CorpusEvalMieStatistics ces2 = null;
					if(outputDir != null){
						ces2 = EvaluatorMie.evaluate(evalFile, resultMieFile, 0);
						ces2.toCsv(outputDir+"/evaluation-results-mie.csv");
					}else{
						//ces2 = EvaluatorMie.evaluate(evalFile, resultMieFile, 0);
						//ces2.toCsv("evaluation-results-mie.csv");	
					}
					log.info(ces2.asString());

				}


			}



		} catch (Exception e) {
			e.printStackTrace();
	}

	}


	static Set<String> processOnlyDocuments = new HashSet<String>(Arrays.asList(
			"x"
	));

	static Set<String> doNotProcessOnlyDocuments = new HashSet<String>(Arrays.asList(
			"x"));




	public static void main(String[] args) throws Exception {

		boolean hasArguments = false;
		if (args.length != 0) {
			if (args.length == 1 && args[0].equals("${args}")) hasArguments = false;
			else hasArguments = true;
		}

		if (hasArguments) {
			String arguments = Arrays.toString(args).replace(", ", " ");
			arguments = arguments.substring(1, arguments.length() - 1);
			log.info("ARGUMENTS: " + arguments);
			run(arguments);
		} else {
			System.out.println("NO ARGUMENTS. CHECKARGUMENTS.");

		}
		System.out.println("\nAll done.");

	}
}
