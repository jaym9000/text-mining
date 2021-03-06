/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.unbsj.cbakerlab.mutation_impact;

import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 *
 * @author UNBSJ
 */
public class Pipeline implements Serializable {
    private static final Logger log = Logger.getLogger(Pipeline.class);

    private SerialAnalyserController annieController;
    private ProcessingResource mutationGrounderJapeTransducer;

    public Pipeline(String gateHome) throws GateException{
        if (!Gate.isInitialised()) {
            log.info("Initializing GATE...");

            // Set GATE HOME directory.
            Gate.setGateHome(new File(gateHome));
            // Set GATE HOME directory.
            Gate.setPluginsHome(new File(gateHome, "plugins"));System.out.println(Gate.getPluginsHome());
            // Initialise GATE.

            //Gate.setSiteConfigFile(new File(Gate.getGateHome()+"/gate.xml"));
			/*File configFile = new File(
					log
					.getClass()
					.getClassLoader()
					.getResource(
							Gate.getGateHome()+"/gate.xml").getPath());			
			Gate.setSiteConfigFile(configFile);
			*/

            try{
                Gate.init();
                log.info("Initializing GATE... Done.");
            }
            catch(Exception e){
                e.printStackTrace();
                log.fatal("GATE could not be initialized. Check GATE_HOME: " + Gate.getGateHome());
                throw new gate.util.GateRuntimeException("GATE could not be initialized. Check GATE_HOME: " + Gate.getGateHome());

            }

        }
    }

    public void init(boolean runDocumentResetter,
                     boolean runTokenizer,
                     boolean runSentenceSplitter,
                     boolean runPosTagger,
                     boolean runNpChunker,
                     boolean runProteinExtractor,
                     boolean runOrganismExtractor,
                     boolean runMutationExtractor,
                     boolean runMG,
                     boolean runMie,
                     boolean runSd,
                     boolean runSdExtractor,
                     boolean runSv
    ) throws GateException, URISyntaxException, FileNotFoundException, IOException {

        //Utils.initGate();

        // Load ANNIE plugin.
        File pluginsHome = Gate.getPluginsHome();
        Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "ANNIE").toURI().toURL());

        // Create a serial analyser controller to run ANNIE with.
        annieController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController",
                Factory.newFeatureMap(), Factory.newFeatureMap(), "ANNIE_" + Gate.genSym());




        if(runDocumentResetter){
            // Add tokenizer.
            FeatureMap prParams = Factory.newFeatureMap();
            ProcessingResource pr = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR", prParams);
            annieController.add(pr);
            log.info("document resetter added.");
        }

        if(runTokenizer){
            // Add tokenizer.
            FeatureMap tokParams = Factory.newFeatureMap();
            ProcessingResource tokPr = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser", tokParams);
            annieController.add(tokPr);
            log.info("tokenizer added");
        }

        if(runSentenceSplitter){
            // Add sentence splitter.
            FeatureMap senParams = Factory.newFeatureMap();
            ProcessingResource senPr = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter", senParams);
            annieController.add(senPr);
            log.info("sentence splitter added");
        }

        if(runPosTagger){
            // Add a part of speech tagger.
            ProcessingResource posPr = (ProcessingResource) Factory.createResource("gate.creole.POSTagger", Factory.newFeatureMap());
            annieController.add(posPr);
            log.info("part of speech tagger added");
        }

        if(runNpChunker){
            // Add MuNPEx.
            FeatureMap munpexParams = Factory.newFeatureMap();
            //munpexParams.put("grammarURL", ClassLoader.getSystemClassLoader().getResource("gate/japerules/MuNPEx-1.1/en-np_main.jape"));
            munpexParams.put("grammarURL", this.getClass().getClassLoader().getResource("gate/japerules/MuNPEx-1.1/en-np_main.jape"));
//System.out.println("@1"+this.getClass().getClassLoader().getResource("gate/japerules/MuNPEx-1.1/en-np_main.jape"));
            ProcessingResource munpexPr = (ProcessingResource) Factory.createResource("gate.creole.ANNIETransducer", munpexParams);
            annieController.add(munpexPr);
            log.info("MuNPEx added");
        }

        if(runMutationExtractor){
            // Add MF
            Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "Tagger_MutationFinder").toURI().toURL());
            ///Gate.getCreoleRegister().registerDirectories(new URL(log.getClass().getResource("/gate/plugins/Tagger_MutationFinder/").toURI().toString()));

            FeatureMap mfParams = Factory.newFeatureMap();
            ProcessingResource mfParamsPr = (ProcessingResource) Factory.createResource("gate.creole.mutationfinder.MutationFinderPR", mfParams);
            annieController.add(mfParamsPr);
            log.info("MF added");
        }


        if(runOrganismExtractor){
            // Add Gazetteer: organisms.
            log.info("organism gazetteer adding...");

            //  File oDir = new File(log.getClass().getResource("gate/gazetteers/organisms/").toURI());

            //  File oDir = new File(this.getClass().getClassLoader().getResource("/gate/gazetteers/organisms/").toURI());
            FeatureMap oParams = Factory.newFeatureMap();
            oParams.put("caseSensitive", false);
            oParams.put("encoding", "UTF-8");
            oParams.put("gazetteerFeatureSeparator", null);
            //oParams.put("listsURL", new URL(oDir.toURI().toURL().toString() + "lists.def"));


            oParams.put("listsURL", new URL(log.getClass().getResource("/gate/gazetteers/organisms/lists.def").toURI().toString()));
            ProcessingResource oPr = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", oParams);
            annieController.add(oPr);
            log.info("organism gazetteer added");
        }


        if(runProteinExtractor){
            // Add Gazetteer: one word protein names.
            log.info("one word protein names adding...");
            //File owDir = new File(this.getClass().getClassLoader().getResource("/gate/gazetteers/oneword/").toURI().toString());
            // System.out.println("@2: "+this.getClass().getClassLoader().getResource("gate/gazetteers/oneword/").toURI().toString());
            // System.out.println("@3: "+new File(this.getClass().getClassLoader().getResource("gate/gazetteers/oneword/").toURI().toURL().toString()));

            FeatureMap owParams = Factory.newFeatureMap();
            owParams.put("caseSensitive", true);
            owParams.put("encoding", "UTF-8");
            owParams.put("gazetteerFeatureSeparator", null);
            // System.out.println("@4: "+owDir.toURI().toURL().toString() + "/lists.def");
            //System.out.println("@5: "+new URL(owDir.toURI().toURL().toString() + "/lists.def"));
            // System.out.println("@6: "+this.getClass().getClassLoader().getResource("gate/gazetteers/oneword/").toURI().toString()+ "lists.def");
            //owParams.put("listsURL",owDir.toURI().toURL().toString() + "/lists.def");


            ///!!!!owParams.put("listsURL",this.getClass().getClassLoader().getResource("gate/gazetteers/oneword/").toURI().toString()+ "lists.def");

            owParams.put("listsURL",new URL(log.getClass().getResource("/gate/gazetteers/oneword/lists.def").toURI().toString()));


            ProcessingResource owPr = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", owParams);
            annieController.add(owPr);
            log.info("one word protein names added");

            //URL LKB = this.getClass().getClassLoader().getResource("gate/plugins/Gazetteer_LKB");
            //Gate.getCreoleRegister().registerDirectories(LKB);
            Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "Gazetteer_LKB").toURI().toURL());


            /*/ Add LKB Gazetteer: more than one word protein names.
            log.info("LKB more than one word protein names adding...");
            File mtowDictDir = new File(log.getClass().getResource("gate/gazetteers/dictionary_from_remote_repository").toURI());
            FeatureMap mtowDictParams = Factory.newFeatureMap();
            mtowDictParams.put("forceCaseSensitive", false);
            mtowDictParams.put("dictionaryPath", mtowDictDir.toURI().toURL());
            ProcessingResource mtowDictPr = (ProcessingResource) Factory.createResource("com.ontotext.kim.gate.KimGazetteer", mtowDictParams);
            annieController.add(mtowDictPr);
            log.info("LKB more than one word protein names added");
            */

            // OLD Add Gazetteer: more than one word protein names.
            //File mtowDir = new File(this.getClass().getClassLoader().getResource("gate/gazetteers/morethanoneword/").toURI());
            FeatureMap mtowParams = Factory.newFeatureMap();
            mtowParams.put("caseSensitive", false);
            mtowParams.put("encoding", "UTF-8");
            mtowParams.put("gazetteerFeatureSeparator", null);
            // mtowParams.put("listsURL", new URL(mtowDir.toURI().toURL().toString() + "lists.def"));

            mtowParams.put("listsURL",new URL(log.getClass().getResource("/gate/gazetteers/morethanoneword/lists.def").toURI().toString()));

            ProcessingResource mtowPr = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", mtowParams);
            annieController.add(mtowPr);
            log.info("more than one word protein names added");



            /*/ Add Abner
            Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "Tagger_Abner").toURI().toURL());
            FeatureMap abnerParams = Factory.newFeatureMap();
           // abnerParams.put("forceCaseSensitive", false);
            ProcessingResource abnerParamsPr = (ProcessingResource) Factory.createResource("gate.abner.AbnerTagger", abnerParams);
            annieController.add(abnerParamsPr);
            log.info("Abner added");
            */
        }





        if(runMG){
            // Add mutation grounder.
            FeatureMap ltParams = Factory.newFeatureMap();
            ltParams.put("grammarURL", this.getClass().getClassLoader().getResource("gate/japerules/MGMain.jape"));
            mutationGrounderJapeTransducer = (ProcessingResource) Factory.createResource("gate.creole.ANNIETransducer",ltParams);
            annieController.add(mutationGrounderJapeTransducer);
            log.info("mutation grounding transducer added");
        }

        if(runMie){
            // Add impact transducer part # 1.
            FeatureMap iT1Params = Factory.newFeatureMap();
            iT1Params.put("grammarURL", this.getClass().getClassLoader().getResource("gate/japerules/MIMain_part1.jape"));
            ProcessingResource iT1Pr = (ProcessingResource) Factory.createResource("gate.creole.ANNIETransducer", iT1Params);
            annieController.add(iT1Pr);
            log.info("impact transducer part#1 added");

            // Add impact transducer part # 2.
            FeatureMap iT2Params = Factory.newFeatureMap();
            iT2Params.put("grammarURL", this.getClass().getClassLoader().getResource("gate/japerules/MIMain_part2.jape"));
            ProcessingResource iT2Pr = (ProcessingResource) Factory.createResource("gate.creole.ANNIETransducer", iT2Params);
            annieController.add(iT2Pr);
            log.info("impact transducer part#2 added");

        }

        if(runSv){
            // Add stability value.
            FeatureMap iT3Params = Factory.newFeatureMap();
            iT3Params.put("grammarURL", this.getClass().getClassLoader().getResource("gate/japerules/stabilityValues.jape"));
            ProcessingResource iT3Pr = (ProcessingResource) Factory.createResource("gate.creole.ANNIETransducer", iT3Params);
            annieController.add(iT3Pr);
            log.info("stability value added");
        }

        if(runSd){
            // add stability direction.
            FeatureMap iT4Params = Factory.newFeatureMap();
            iT4Params.put("grammarURL", this.getClass().getClassLoader().getResource("gate/japerules/stabilityDirection.jape"));
            ProcessingResource iT4Pr = (ProcessingResource) Factory.createResource("gate.creole.ANNIETransducer", iT4Params);
            annieController.add(iT4Pr);
            log.info("stability direction added");
        }

        // runSdExtractor
        if(runSdExtractor){
            // Add Gazetteer: Stability Direction.
            log.info("Stability Direction gazetteer adding...");

            //  File oDir = new File(log.getClass().getResource("gate/gazetteers/organisms/").toURI());

            //  File oDir = new File(this.getClass().getClassLoader().getResource("/gate/gazetteers/organisms/").toURI());
            FeatureMap ohParams = Factory.newFeatureMap();
            ohParams.put("caseSensitive", false);
            ohParams.put("encoding", "UTF-8");
            ohParams.put("gazetteerFeatureSeparator", null);
            //oParams.put("listsURL", new URL(oDir.toURI().toURL().toString() + "lists.def"));


            ohParams.put("listsURL", new URL(log.getClass().getResource("/gate/gazetteers/stability/lists.def").toURI().toString()));
            ProcessingResource ohPr = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", ohParams);
            annieController.add(ohPr);
            log.info("Stability Direction gazetteer added");
        }
    }

    public void addFeature(String feature, Object value) throws ResourceInstantiationException {
        mutationGrounderJapeTransducer.getFeatures().put(feature, value);
    }

    public void addStatisticsAsFeature(AnalysisStatistics value) throws ResourceInstantiationException {
        mutationGrounderJapeTransducer.getFeatures().put("statisticsObject", value);
    }

    /**
     * Tell MutationGrounderApp's controller about the corpus you want to run on
     */
    public void setCorpus(Corpus corpus) {
        annieController.setCorpus(corpus);
    }

    /** Run MutationGrounderApp */
    public void execute(gate.Document doc) throws GateException {
        log.info("Running Pipeline...");
        gate.Corpus gateCorpus = Factory.newCorpus("corpus"+doc.getName());
        gateCorpus.add(doc);
        this.setCorpus(gateCorpus);
        annieController.execute();


        Factory.deleteResource(gateCorpus);

        log.info("...Pipeline complete");
    }

}
