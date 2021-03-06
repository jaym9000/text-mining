package ca.unbsj.cbakerlab.mutation_impact;

import gate.Factory;

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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;




public class Main {
	private static final Logger log = Logger.getLogger(Main.class);

	static long corpusQueryTime = 0;


	Map<String,Object> parameters = new HashMap<String,Object>();

	public Main(){

	}
	/**
	 * Executed from main. Supports JSAP parser to read arguments from command line. Details of how to use arguments in Main please
	 * read in readme
	 *
	 * @param args arguments from command line
	 * @throws Exception
	 */
	public static void run(String args) {

		try {
			long time = System.currentTimeMillis();

			// Get command line arguments.
			boolean debug = false;

			String inputFileOrDirName = null;
			String tmpFileOrDirName = null;
			String xmlOutputDirName = null;
			String tabularOutputDirName = null;
			String rdfOutputDirName = null;

			String evalFileName = null;
			String mgResultFileName = null;
			String mieResultFileName = null;

			boolean initDocumentResetter = false;
			boolean initTokenizer = false;
			boolean initSentenceSplitter = false;
			boolean initPosTagger = false;
			boolean initNpChunker = false;
			boolean initProteinExtractor = false;
			boolean initOrganismExtractor = false;
			boolean initMutationExtractor = false;
			boolean initMg = false;
			boolean initMie = false;
			boolean initSd = false;
			boolean initSv = false;
			boolean initSdExtractor = false;

			boolean runMgPipeline = false;
			boolean runMiePipeline = false;
			boolean runEvaluator = false;

			boolean useCaching = false;
			boolean saveStatistics = false;
			boolean saveResults = false;
			boolean convertMgResults2Rdf = false;
			boolean convertMieResults2Rdf = false;
			boolean convertGate2Tab = false;


			//
			// Define options.
			//
			{
				JSAP jsap = new JSAP();
				{
					FlaggedOption s = new FlaggedOption("input").setStringParser(JSAP.STRING_PARSER).setLongFlag("input").setShortFlag('i');
					s.setHelp("The name input file or directory.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("tmpdir").setStringParser(JSAP.STRING_PARSER).setLongFlag("tmpdir");
					s.setHelp("The name tmp file or directory.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("tabular").setStringParser(JSAP.STRING_PARSER).setLongFlag("tabular").setShortFlag('t');
					s.setHelp("The name output file or directory.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("xmldir").setStringParser(JSAP.STRING_PARSER).setLongFlag("xmldir").setShortFlag('x');
					s.setHelp("The name xml file or directory.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("rdfdir").setStringParser(JSAP.STRING_PARSER).setLongFlag("rdfdir").setShortFlag('r');
					s.setHelp("The name rdf file or directory.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("mutationGazzetterPath").setStringParser(JSAP.STRING_PARSER).setLongFlag("gazzetter-mutations").setShortFlag('g');
					s.setHelp("The name of the mutation gazetteer file.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("evalFile").setStringParser(JSAP.STRING_PARSER).setLongFlag("evalfile").setShortFlag('v');
					s.setHelp("The name of the evaluation file.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("mgResultFile").setStringParser(JSAP.STRING_PARSER).setLongFlag("vmg");
					s.setHelp("The name of the mg result file.");
					jsap.registerParameter(s);
				}
				{
					FlaggedOption s = new FlaggedOption("mieResultFile").setStringParser(JSAP.STRING_PARSER).setLongFlag("vmie");
					s.setHelp("The name of the mie result file.");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("useCaching").setLongFlag("useCaching").setLongFlag("cache");
					s.setHelp("Enable useCaching");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("debugging").setLongFlag("debug").setShortFlag('d');
					s.setHelp("Enable debugging messages");
					jsap.registerParameter(s);
				}

				//
				// Execute program.
				//
				{
					Switch s = new Switch("runDocumentResetter").setLongFlag("runDocumentResetter").setLongFlag("reset");
					s.setHelp("runDocumentResetter");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runTokenizer").setLongFlag("runTokenizer").setLongFlag("tok");
					s.setHelp("runTokenizer");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runSentenceSplitter").setLongFlag("runSentenceSplitter").setLongFlag("sent");
					s.setHelp("runSentenceSplitter");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runPosTagger").setLongFlag("runPosTagger").setLongFlag("pos");
					s.setHelp("runPosTagger");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runNpChunker").setLongFlag("runNpChunker").setLongFlag("npch");
					s.setHelp("runNpChunker");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runProteinExtractor").setLongFlag("runProteinExtractor").setLongFlag("prot");
					s.setHelp("runProteinExtractor");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runMutationExtractor").setLongFlag("runMutationExtractor").setLongFlag("mut");
					s.setHelp("runMutationExtractor");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runOrganismExtractor").setLongFlag("runOrganismExtractor").setLongFlag("org");
					s.setHelp("runOrganismExtractor");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runMG").setLongFlag("runMG").setLongFlag("mg");
					s.setHelp("runMG");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runMie").setLongFlag("runMie").setLongFlag("mie");
					s.setHelp("runMie");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runStabilityDirection").setLongFlag("runStabilityDirection").setLongFlag("sd");
					s.setHelp("runStabilityDirection");
					jsap.registerParameter(s);
				} 
				{
					Switch s = new Switch("runStabilityDirectionExtractor").setLongFlag("runStabilityDirectionExtractor").setLongFlag("sde");
					s.setHelp("runStabilityDirectionExtractor");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runStabilityValues").setLongFlag("runStabilityValues").setLongFlag("sv");
					s.setHelp("runStabilityValues");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runMgPipeline").setLongFlag("runMgPipeline").setLongFlag("mgpipe");
					s.setHelp("runMgPipeline");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runMiePipeline").setLongFlag("runMiePipeline").setLongFlag("miepipe");
					s.setHelp("runMiePipeline");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("convertGate2Tab").setLongFlag("gate2tab");
					s.setHelp("convertGate2Tab");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("convertMgResults2Rdf").setLongFlag("mg2rdf");
					s.setHelp("convertMgResults2Rdf");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("convertMieResults2Rdf").setLongFlag("mie2rdf");
					s.setHelp("convertMieResults2Rdf");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("runEvaluator").setLongFlag("runEvaluator").setShortFlag('e');
					s.setHelp("runEvaluator");
					jsap.registerParameter(s);
				}

				//
				// Optional.
				//
				{
					Switch s = new Switch("saveStatisticsFlag").setLongFlag("statistics").setShortFlag('s');
					s.setHelp("Save statistics in statistics.csv");
					jsap.registerParameter(s);
				}
				{
					Switch s = new Switch("saveResultsFlag").setLongFlag("results").setShortFlag('q');
					s.setHelp("Save results in result.csv");
					jsap.registerParameter(s);
				}


				// Parse the command-line arguments.
				JSAPResult config = jsap.parse(args);

				// Display the helpful help in case trouble happens.
				if (!config.success()) {
					System.err.println();
					System.err.println(" " + jsap.getUsage());
					System.err.println();
					System.err.println(jsap.getHelp());
					System.exit(1);
				}


				// Assign to variables.

				//
				// Directories.
				//
				inputFileOrDirName = config.getString("input");
				if (inputFileOrDirName != null) log.info("corpus path: " + inputFileOrDirName);

				tmpFileOrDirName = config.getString("tmpdir");
				if (tmpFileOrDirName != null) log.info("tmp output path: " + tmpFileOrDirName);

				xmlOutputDirName = config.getString("xmldir");
				if (xmlOutputDirName != null) log.info("xml output path: " + xmlOutputDirName);

				tabularOutputDirName = config.getString("tabular");
				if (tabularOutputDirName != null) log.info("tab output path: " + tabularOutputDirName);

				rdfOutputDirName = config.getString("rdfdir");
				if (rdfOutputDirName != null) log.info("rdf output path: " + rdfOutputDirName);


				//
				// Modules.
				//
				initDocumentResetter = config.getBoolean("runDocumentResetter");
				if (initDocumentResetter) log.info("runDocumentResetter: " + initDocumentResetter);

				initTokenizer = config.getBoolean("runTokenizer");
				if (initTokenizer) log.info("runTokenizer: " + initTokenizer);

				initSentenceSplitter = config.getBoolean("runSentenceSplitter");
				if (initSentenceSplitter) log.info("runSentenceSplitter: " + initSentenceSplitter);

				initPosTagger = config.getBoolean("runPosTagger");
				if (initPosTagger) log.info("runPosTagger: " + initPosTagger);

				initNpChunker = config.getBoolean("runNpChunker");
				if (initNpChunker) log.info("runNpChunker: " + initNpChunker);

				initProteinExtractor = config.getBoolean("runProteinExtractor");
				if (initProteinExtractor) log.info("runProteinExtractor: " + initProteinExtractor);

				initOrganismExtractor = config.getBoolean("runOrganismExtractor");
				if (initOrganismExtractor) log.info("runOrganismExtractor: " + initOrganismExtractor);

				initMutationExtractor = config.getBoolean("runMutationExtractor");
				if (initMutationExtractor) log.info("runMutationExtractor: " + initMutationExtractor);

				initMg = config.getBoolean("runMG");
				if (initMg) log.info("runMG: " + initMg);

				initMie = config.getBoolean("runMie");
				if (initMie) log.info("runMie: " + initMie);

				initSd = config.getBoolean("runStabilityDirection");
				if (initSd) log.info("runStabilityDirection: " + initSd);

				initSdExtractor = config.getBoolean("runStabilityDirectionExtractor");
				if (initSdExtractor) log.info("runStabilityDirectionExtractor" + initSdExtractor);

				initSv = config.getBoolean("runStabilityValues");
				if (initSv) log.info("runStabilityValues: " + initSv);

				runMgPipeline = config.getBoolean("runMgPipeline");
				if (runMgPipeline) log.info("runMgPipeline: " + runMgPipeline);

				runMiePipeline = config.getBoolean("runMiePipeline");
				if (runMiePipeline) log.info("runMiePipeline: " + runMiePipeline);

				convertMgResults2Rdf = config.getBoolean("convertMgResults2Rdf");
				if (convertMgResults2Rdf) log.info("convertMgResults2Rdf: " + convertMgResults2Rdf);

				convertMieResults2Rdf = config.getBoolean("convertMieResults2Rdf");
				if (convertMieResults2Rdf) log.info("convertMieResults2Rdf: " + convertMieResults2Rdf);

				convertGate2Tab = config.getBoolean("convertGate2Tab");
				if (convertGate2Tab) log.info("convertGate2Tab: " + convertGate2Tab);


				runEvaluator = config.getBoolean("runEvaluator");
				if (runEvaluator) log.info("runEvaluator: " + runEvaluator);

				//
				// Resources.
				//
				evalFileName = config.getString("evalFile");
				if (evalFileName != null) log.info("eval File path: " + evalFileName);

				mgResultFileName = config.getString("mgResultFile");
				if (mgResultFileName != null) log.info("mg result File path: " + mgResultFileName);

				mieResultFileName = config.getString("mieResultFile");
				if (mieResultFileName != null) log.info("mie result File path: " + mieResultFileName);

				//
				// Optional.
				//
				useCaching = config.getBoolean("useCaching");
				if(useCaching)log.info("useCaching: " + useCaching);

				saveStatistics = config.getBoolean("saveStatisticsFlag");
				if(saveStatistics)log.info("save statistics: " + saveStatistics);

				//saveResults = config.getBoolean("saveResultsFlag");
				//if(saveResults) log.info("save results: " + saveResults);

				debug = config.getBoolean("debugging");
				log.info("debug: " + debug);
			}

			// ** set up logging.
			if (debug == true) {
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


			// Create temp output directory if it does not exist.
			File tmpOutputDir = null;
			if(tmpFileOrDirName != null){
				tmpOutputDir = Utils.createFileOrDirectoryIfNotExist(tmpFileOrDirName);
			}

			// Create tab output directory if it does not exist.
			File tabularOutputDir = null;
			if(tabularOutputDirName != null){
				tabularOutputDir = Utils.createFileOrDirectoryIfNotExist(tabularOutputDirName);
			}

			// Create xml output directory if it does not exist.
			File xmlOutputDir = null;
			if(xmlOutputDirName != null){
				xmlOutputDir = Utils.createFileOrDirectoryIfNotExist(xmlOutputDirName);
			}

			// Create rdf output directory if it does not exist.
			File rdfOutputDir = null;
			if(rdfOutputDirName != null){
				rdfOutputDir = Utils.createFileOrDirectoryIfNotExist(rdfOutputDirName);
			}




			// Pipeline is by default consists of tokenizer, sentence splitter, np chunker, protein and organism extractors, mutation finder, and mutation grounder.
			if(runMgPipeline){
				initTokenizer = true;
				initSentenceSplitter = true;
				initPosTagger = true;
				initNpChunker = true;
				initProteinExtractor = true;
				initOrganismExtractor = true;
				initMutationExtractor = true;
				initMg = true;
				initSd = true;
				initSdExtractor = true;
				initSv = true;
			}

			if(runMiePipeline){
				initTokenizer = true;
				initSentenceSplitter = true;
				initPosTagger = true;
				initNpChunker = true;
				initProteinExtractor = true;
				initOrganismExtractor = true;
				initMutationExtractor = true;
				initMg = true;
				initMie = true;
				initSd = true;
				initSdExtractor = true;
				initSv = true;
			}


			//
			// RUN PIPELINE.
			//
			if(runMgPipeline || runMiePipeline
					|| initTokenizer
					|| initSentenceSplitter
					|| initPosTagger
					|| initNpChunker
					|| initProteinExtractor
					|| initOrganismExtractor
					|| initMutationExtractor
					|| initMg
					|| initMie
					|| initSd
					|| initSdExtractor
					|| initSv){


				//
				// Prepare statistics collector.
				//
				AnalysisStatistics stat = null;
				if(initMg){
					if(saveStatistics){
						stat = new AnalysisStatistics();
						stat.loadMap(new File(evalFileName));
						log.debug(stat.getMap());
					}
					if(useCaching){
						MutationGrounder.cacheFileName = tmpOutputDir+"/cacheQuerying";
						MutationGrounder.cacheForGroundingFileName = tmpOutputDir+"/cacheAlignment";
					}
				}

				Properties pro = new Properties();
				try {
					pro.load(new FileInputStream(new File(log.getClass().getClassLoader().getResource("project.properties").toURI())));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//pro.load(new FileInputStream(new File(ClassLoader.getSystemClassLoader().getResource("project.properties").toURI())));

				String gateHome = pro.getProperty("GATE_HOME");
				//
				// Init pipeline.
				//
				Pipeline pipeline = new Pipeline(gateHome);
				pipeline.init(
						initDocumentResetter,
						initTokenizer,
						initSentenceSplitter,
						initPosTagger,
						initNpChunker,
						initProteinExtractor,
						initOrganismExtractor,
						initMutationExtractor,
						initMg,
						initMie,
						initSd,
						initSdExtractor,
						initSv
				);

				Model mgModelMerged = null;
				Model mieModelMerged = null;
				if(rdfOutputDir != null){
					mgModelMerged = ModelFactory.createDefaultModel();
					mgModelMerged.setNsPrefix("mieo", Vocab.mieoNS);
					mgModelMerged.setNsPrefix("sio", Vocab.sioNS);
					mgModelMerged.setNsPrefix("lsrn", Vocab.lsrnNS);
					mgModelMerged.setNsPrefix("str", Vocab.strNS);
					mgModelMerged.setNsPrefix("foaf", Vocab.foafNS);
					mgModelMerged.setNsPrefix("ao", Vocab.aoNS);
					mgModelMerged.setNsPrefix("aos", Vocab.aosNS);
					mgModelMerged.setNsPrefix("aot", Vocab.aotNS);
					mgModelMerged.setNsPrefix("aof", Vocab.aofNS);
					mgModelMerged.setNsPrefix("pav", Vocab.pavNS);
					mgModelMerged.setNsPrefix("go", Vocab.goNS);

					mieModelMerged = ModelFactory.createDefaultModel();
					mieModelMerged.setNsPrefix("mieo", Vocab.mieoNS);
					mieModelMerged.setNsPrefix("sio", Vocab.sioNS);
					mieModelMerged.setNsPrefix("lsrn", Vocab.lsrnNS);
					mieModelMerged.setNsPrefix("str", Vocab.strNS);
					mieModelMerged.setNsPrefix("foaf", Vocab.foafNS);
					mieModelMerged.setNsPrefix("ao", Vocab.aoNS);
					mieModelMerged.setNsPrefix("aos", Vocab.aosNS);
					mieModelMerged.setNsPrefix("aot", Vocab.aotNS);
					mieModelMerged.setNsPrefix("aof", Vocab.aofNS);
					mieModelMerged.setNsPrefix("pav", Vocab.pavNS);
					mieModelMerged.setNsPrefix("go", Vocab.goNS);

				}



				//
				// Prepare and Process corpus.
				//
				Map<String,String> fileIndex = Utils.initFileIndex(inputFileOrDirName);
				Corpus corpus = new Corpus(inputFileOrDirName);

				for(String fileName : fileIndex.keySet()){


					if(processOnlyDocuments.size()>1 && !processOnlyDocuments.contains(fileName)){
						continue;
					}

					log.info("\n==========================================================\n"+
							"DOCUMENT: "+fileName+"		"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+
							"\n==========================================================\n");


					gate.Document doc = Factory.newDocument(new URL(fileIndex.get(fileName)));
					doc.setName(fileName);

					if(saveStatistics){
						doc.getFeatures().put("statistics", stat);
					}

					pipeline.execute(doc);

					if(xmlOutputDir != null){
						Utils.saveGateXml(doc, new File(xmlOutputDir+"/"+doc.getName()+".xml"), false);
					}

					if(mgModelMerged != null){
						Model mgModel = Gate2MieoAoRdfConverter.mutationGroundigResults2Rdf(doc,false);
						mgModelMerged.add(mgModel);
						// Write ontology to file.						
						//BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/"+doc.getName()+".rdf")));
						//model.write(out, "RDF/XML"); // RDF/XML-ABBREV
						//out.close();
					}

					if(mieModelMerged != null){
						Model mieModel = Gate2MieoAoRdfConverter.mutationImpactExtractionResults2Rdf(doc,false);
						mieModelMerged.add(mieModel);
						// Write ontology to file.						
						//BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/"+doc.getName()+".rdf")));
						//model.write(out, "RDF/XML"); // RDF/XML-ABBREV
						//out.close();
					}


					DocumentInformation docInfo = new DocumentInformation(doc);
					corpus.getDocInfos().put(docInfo.getName().split("\\.")[0], docInfo);

					Factory.deleteResource(doc);

					log.info("Processing Document Time " + (System.currentTimeMillis() - time) / 1000 + " seconds = "+(System.currentTimeMillis() - time) / 1000 / 60 +" minutes");
					log.info("Query Document Time " + corpusQueryTime / 1000 + " seconds");
				}

				if(mgModelMerged != null){
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/results-mg.rdf")));
					mgModelMerged.write(out, "RDF/XML"); // RDF/XML-ABBREV
					out.close();
				}

				if(mieModelMerged != null){
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/results-mie.rdf")));
					mgModelMerged.write(out, "RDF/XML"); // RDF/XML-ABBREV
					out.close();
				}

				if(tabularOutputDir != null){
					corpus.saveMgResults(tabularOutputDirName + "/results-mg.csv");
					corpus.saveMieResults(tabularOutputDirName + "/results-mie.csv");
				}


				if(stat != null){
					log.info(stat.asString());
				}

				log.info("Processing Corpus Time " + (System.currentTimeMillis() - time) / 1000 + " seconds");
			}


			if(convertGate2Tab){
				Utils.initGate();

			}


			if(convertMgResults2Rdf){
				Utils.initGate();

				BufferedWriter mergedOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/"+"results-mg.rdf")));
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


				Map<String, String> fileIndex = Utils.initFileIndex(inputFileOrDirName);
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


			if(convertMieResults2Rdf){
				Utils.initGate();

				BufferedWriter mergedOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rdfOutputDir+"/"+"results-mie.rdf")));
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


				Map<String, String> fileIndex = Utils.initFileIndex(inputFileOrDirName);
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



			if(runEvaluator){
				File evalFile = new File(evalFileName);

				File resultMGFile = null;
				try{
					if(tabularOutputDir != null){
						resultMGFile = new File(tabularOutputDir+"/results-mg.csv");
					}else{
						resultMGFile = new File(mgResultFileName);
					}
				}catch(Exception e){
					log.warn("No mutation grounding results file specified.");
				}


				if(resultMGFile != null){
					CorpusEvalMgStatistics ces = null;
					if(tabularOutputDir != null){
						ces = EvaluatorMg.evaluate(new File(tabularOutputDir + "/evaluation-compare-table.csv"), evalFile, resultMGFile);
						ces.toCsv(tabularOutputDir+"/evaluation-results-mg.csv");
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
					if(tabularOutputDir != null){
						resultMieFile = new File(tabularOutputDir+"/results-mie.csv");
					}else{
						resultMieFile = new File(mieResultFileName);
					}
				}catch(Exception e){
					log.warn("No mutation impact extraction results file specified.");
				}


				if(resultMieFile != null){
					CorpusEvalMieStatistics ces2 = null;
					if(tabularOutputDir != null){
						ces2 = EvaluatorMie.evaluate(evalFile, resultMieFile, 0);
						ces2.toCsv(tabularOutputDir+"/evaluation-results-mie.csv");
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
