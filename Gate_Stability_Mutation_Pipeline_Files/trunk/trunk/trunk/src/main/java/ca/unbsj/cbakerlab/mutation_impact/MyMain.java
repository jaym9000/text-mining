package ca.unbsj.cbakerlab.mutation_impact;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class MyMain {
	private static final Logger log = Logger.getLogger(MyMain.class);


	
	public static void main(String[] args) throws Exception {
		
    	boolean hasArguments = false;
		if (args.length != 0) {
			if (args.length == 1 && args[0].equals("${args}")) hasArguments = false;
			else hasArguments = true;		
		}
		//Main main = new Main();
		if (hasArguments) {
			String arguments = Arrays.toString(args).replace(", ", " ");
			arguments = arguments.substring(1, arguments.length() - 1);
			log.info("ARGUMENTS: " + arguments);
			Main.run(arguments);
		} else {
			System.out.println("NO ARGUMENTS. DEFAULTS ARE LOADED.");

			Main2.processOnlyDocuments = new HashSet<String>(Arrays.asList(
					//"9784233",
					//"7705355",
					//"1068633",
					//"10579523", 
					//"10579523",
					//"7705355",
					//"7828730",
					//"127408",

					//"1635434", // 4545 4545
					//"2405954", 
					//"2435086", 
					//"2598388", // 0.0 0.0
					//"1068633",
					//"10100638",
					//"9051734",
					//"7705355",
					//"12089046",
					//"1199364",
					//"1316309",
					//"2598388",
					//"12676719",
					//"2548357",
					//"15206895", //omm
					//"17914867", //omm
					//"10544015", //omm
					//"19290871", //omm
					//"11265460", //omm
					//"gkg234",
			 		"x"
					));
			
			Main.doNotProcessOnlyDocuments = new HashSet<String>(Arrays.asList(
					"1199364",
					"new", 
					"has"));
			
			//
			// Set corpus path.
			//
			String fileOrDirName = "/home/artjomk/workspace2/Corpora/mie-corpus/TXT";

			
			//String fileOrDirName = "/home/artjomk/testmg";
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/test-corpus";
			
			//String fileOrDirName = "/home/artjomk/workspace2/Corpora/dhla-corpus-dev";
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/dhla-corpus-dev/OUTPUT/XML";
			
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/dhla-corpus-dev-2";
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/dhla-corpus-dev-2/MG/XML";

			
		   //  String fileOrDirName = "/home/artjomk/workspace/Corpora/cosmic-corpus/PIK3CA-mined-no-ref";
		   // String mutationListName = "pik3ca_corpus_mutations.def"; 
			  
		    // String fileOrDirName = "/home/artjomk/workspace/Corpora/cosmic-corpus/MEN1";
		    // String mutationListName = "men1_corpus_mutations.def"; 
			
		  // String fileOrDirName = "/home/artjomk/workspace/Corpora/cosmic-corpus/FGFR3-mined-no-ref";
		  // String mutationListName = "fgfr3_corpus_mutations.def"; 
		     

			
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/enzyminer-corpus-dev/TXT-mined"; 
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/enzyminer-corpus-dev/TXT-mined/OUTPUT/XML";
		    // String mutationListName = "enzyminer_corpus_mutations.def"; 
		        			
			// String fileOrDirName = "/home/artjomk/workspace/Corpora/enzyminer-corpus-dev/TXT-mined_orig"; 	        
		    // String mutationListName = "enzyminer_corpus_mutations_orig.def";
			
			// String fileOrDirName = "/home/artjomk/workspace/Corpora/enzyminer-corpus-dev/TXT-mined_orig_3"; 
			// String fileOrDirName = "/home/artjomk/workspace/Corpora/enzyminer-corpus-dev/TXT-mined_orig_3/MG/XML"; 	        

		     	    
		   
		    //String fileOrDirName = "/home/artjomk/workspace/Corpora/kinmutbase-corpus/TXT-mined"; 	        
		   //String mutationListName = "kinmutbase_corpus_mutations.def"; 
			
		    //String fileOrDirName = "/home/artjomk/workspace/Corpora/kinmutbase-corpus/TXT-mined_final"; 	        
		   //String mutationListName = "kinmutbase_corpus_mutations_final.def"; 
			
	       // String fileOrDirName = "/home/artjomk/workspace/Corpora/kinmutbase-corpus/TXT-mined_dev"; 	        
	       // String mutationListName = "kinmutbase_corpus_mutations_dev.def"; 
	        	       
	        
		     //String fileOrDirName = "/home/artjomk/workspace/Corpora/kinmutbase-corpus/TXT-mined_devel"; 	        
		     //String mutationListName = "kinmutbase_corpus_mutations_devel.def"; 
		     
	        //String fileOrDirName = "/home/artjomk/workspace/Corpora/kinmutbase-corpus/TXT-mined_devel_2"; 	        
		    //String mutationListName = "kinmutbase_corpus_mutations_devel.def"; 

			//String fileOrDirName = "/home/artjomk/workspace/Corpora/omm-corpus/TXT"; 
			//String fileOrDirName = "/home/artjomk/workspace/Corpora/omm-corpus/TXT/OUTPUT/XML";
			
	       //
	       // This block generates temp and xml output directories as defaults. Used by Artjom.
	       /*/	       
	       String origDirName = null;	       
	       String xmlDirName = null;
	       if(fileOrDirName.endsWith("/MG/XML")){
	    	   origDirName = fileOrDirName.replace("/MG/XML", "");	    	   
	    	   xmlDirName = origDirName+"/MG/XML2";
	       }else{
	    	   origDirName = fileOrDirName;
	    	   xmlDirName = origDirName+"/MG/XML";
	       }
	       String tempDirName = origDirName+"/MG";
		   String evalFileName = origDirName+"/evaluation.csv";
	       */
	       //
	       // This block generates temp and xml output directories as defaults. Used by Artjom.
	       //	       
	       String tmpDirName = null;
	       String xmlDirName = null;
	       String rdfDirName = null;	
	       String tabularDirName = null;

	       if(fileOrDirName.endsWith("/OUTPUT/XML")){
	    	   tmpDirName = fileOrDirName.replace("/OUTPUT/XML", "");	    	   
	    	   //origDirName = fileOrDirName;
	    	   xmlDirName = tmpDirName+"/OUTPUT/XML2";
	    	   rdfDirName = tmpDirName+"/OUTPUT/RDF";
		       tabularDirName = tmpDirName+"/OUTPUT/TAB";
	       }else{
	    	   tmpDirName = fileOrDirName;
	    	   xmlDirName = tmpDirName+"/OUTPUT/XML";
	    	   rdfDirName = tmpDirName+"/OUTPUT/RDF";
		       tabularDirName = tmpDirName+"/OUTPUT/TAB";
	       }
			File outputOutputDir = new File(tmpDirName + "/OUTPUT");
			if (!outputOutputDir.exists()) {
				boolean success = (new File(tmpDirName + "/OUTPUT")).mkdir();
				if (success) {
					System.out.println("Output output directory: " + outputOutputDir + " created");
				} else {
					log.warn("Output output directory is not set.");
				}
			}

			String evalFileName = tmpDirName + "/evaluation.csv";
		   
			//Main2.run("--mgpipe -d --cache"+" -i "+fileOrDirName+" -t "+tmpDirName+" -o "+xmlDirName);
			Main2.run("--miepipe -d --cache"+" -i "+fileOrDirName+" -t "+tmpDirName+" -o "+xmlDirName);
			
		//	Main2.run("--mie2tab -d"+" -i "+xmlDirName+" -o "+tabularDirName);

			
			//Main2.run("--mgeval -d "+" -i "+tabularDirName+ " -o "+tabularDirName +" -v "+evalFileName );
			//Main2.run("--mgeval -d "+" -i "+fileOrDirName+" -t "+tmpDirName+" -o "+xmlDirName +"-v "+evalFileName );

	        //
	        // Run with command line arguments.
	        //
   	      	// Main.run("--tok --sent --pos --npch --mut --org --prot --mg -d "+  " -i "+fileOrDirName+  " -o "+tempDirName+  " -x "+xmlDirName);
   	      	// Main.run("--tok --sent --pos --npch -d "+  " -i "+fileOrDirName+  " -o "+tempDirName+  " -x "+xmlDirName);
			
			//Main.run("--mgpipe -d -e"+" -i "+fileOrDirName+" --cache --tmpdir "+tmpDirName+" -t "+tabularDirName+" -x "+xmlDirName);
			//Main.run("--mgpipe -d"+" -i "+fileOrDirName+" --cache --tmpdir "+tmpDirName+" -x "+xmlDirName);
			//Main.run("--miepipe -d -e"+  " -i "+fileOrDirName+  " -t "+tabularDirName+  " -x "+xmlDirName+" --cache --tmpdir "+tmpDirName+" -v "+evalFileName );
		   
			// Main.run("--mg -d -e"+" -i "+fileOrDirName+" --cache --tmpdir "+tmpDirName+" -x "+xmlDirName+" -v "+evalFileName );
			 //Main.run("--mie -d "+  " -i "+fileOrDirName+  " -t "+tabularDirName+  " -x "+xmlDirName);
			
			//Main.run("--gate2tab -d"+" -i "+fileOrDirName+" -t "+tabularDirName);
		  	       
			//Main.run("--mie2rdf -d "+  " -i "+fileOrDirName+  " -r "+rdfDirName);
			//Main.run("-d -e -v benchmark/query/example-dev/evaluation-dev.csv --vmg benchmark/query/example-dev/results-mg-dev.csv --vmie benchmark/query/example-dev/results-mg-dev.csv");
			//Main.run("-d -e -v /home/artjomk/workspace/mutationImpactPipeline2/final_experiments/new/kinmut2/all/results-mg.csv --vmg  /home/artjomk/workspace/Corpora/kinmutbase-corpus-2/TXT-poppler-raw-utf8/evaluation_neww.csv");
			//Main.run("-d -e -v /home/artjomk/workspace2/Corpora/dhla-corpus-dev/OUTPUT/TAB/results-mg.csv --vmg  /home/artjomk/workspace2/Corpora/dhla-corpus-dev/evaluation.csv");

		}
		System.out.println("\nAll done.");
	
	}	

}
