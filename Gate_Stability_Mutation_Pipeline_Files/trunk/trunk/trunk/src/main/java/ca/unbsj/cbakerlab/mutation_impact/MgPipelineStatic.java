package ca.unbsj.cbakerlab.mutation_impact;

import gate.util.GateException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;



public class MgPipelineStatic {
	private static final Logger log = Logger.getLogger(MgPipelineStatic.class);
	
	private static Pipeline pipeline;
	private static String gateHome;

	public MgPipelineStatic(String gateHomeHere) {
		if (pipeline == null) {
			gateHome = gateHomeHere;
			try {
				pipeline = new Pipeline(gateHome);
			} catch (GateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				pipeline.init(true, true, true, true, true, true, true, true, true, false, true, true, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (GateException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info("Initialised a Pipeline");
		}
		log.info("pipeline is already initialised.");
		
	}
	
	//static 
	//{	}
	
	public Pipeline getPipeline(){
		return pipeline;
	}
	
	public void execute(gate.Document doc){
		try {
			 synchronized (pipeline) 
				{			
				 	pipeline.execute(doc);				
				}
		} catch (GateException e) {
			e.printStackTrace();
		}
	}
	
	
}
