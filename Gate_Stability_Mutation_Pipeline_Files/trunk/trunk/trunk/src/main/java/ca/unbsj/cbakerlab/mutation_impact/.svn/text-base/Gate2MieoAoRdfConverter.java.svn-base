package ca.unbsj.cbakerlab.mutation_impact;

import gate.Annotation;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.util.InvalidOffsetException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class Gate2MieoAoRdfConverter {
	private static final Logger log = Logger.getLogger(Gate2MieoAoRdfConverter.class);
	static String creatorUriString = "http://example.org/mie-service";
	static int counter = 0;

		
	public static Model mutationGroundigResults2Rdf(gate.Document doc, boolean useUriSchema) throws InvalidOffsetException{
		 
		//
		// Create ontology model.
		//	
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("mieo", Vocab.mieoNS);
		model.setNsPrefix("sio", Vocab.sioNS);
		model.setNsPrefix("lsrn", Vocab.lsrnNS);
		model.setNsPrefix("str", Vocab.strNS);
		model.setNsPrefix("foaf", Vocab.foafNS);
		model.setNsPrefix("ao", Vocab.aoNS);
		model.setNsPrefix("aos", Vocab.aosNS);
		model.setNsPrefix("aot", Vocab.aotNS);
		model.setNsPrefix("aof", Vocab.aofNS);
		model.setNsPrefix("pav", Vocab.pavNS);
		model.setNsPrefix("go", Vocab.goNS);


		//System.out.println(doc.getAnnotations());
    	/////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// Results of mutation grounding.
    	//
        AnnotationSet annotationSet = doc.getAnnotations().get("GroundedPointMutation");
        
        if(annotationSet.size() == 0){
        	log.warn("No PointMutations");
        }else{
            log.debug("mutations.size(): "+annotationSet.size());
        }
        
        /*
        TO DO just annotate all mutations
        AnnotationSet mutationTempSet = doc.getAnnotations().get("MutationTemp");
        for (Annotation mutation : mutationTempSet) {
        	String mutationString = (String)mutation.getFeatures().get("wNm");
        	if(mutationString==null)continue;
        	String[] parsed = Utils.parseWNMFormat(mutationString);
        	if(!mutationStrings.contains(mutationString)){
        		GroundedPointMutation gm = new GroundedPointMutation();
        		wtResidue = parsed[0].charAt(0);
        		position = Integer.parseInt(parsed[1]);
                mResidue = parsed[2].charAt(0); 
        	}
        }*/
        
        
		
		Set<String> mutationStrings = new HashSet<String>();
	        for (Annotation mutation : annotationSet) {
	        	
	    		//
	    		// New data.
	    		//
	    		String proteinIds = null;
	    		char wtResidue = 0;
	    		int position = -9999;
	    		int cPosition = -9999;
	    		char mResidue = 0;
	    		//int offset = -9999;
	    	//	int mutationId = -9999;
	    		//String mutationType = null;

	        	
	        	//log.debug(mutation);
	        	
	        	
	        	FeatureMap features = mutation.getFeatures();
	        	//log.debug(features);
	        	
	        	String mutString = features.get("hasWildtypeResidue").toString().charAt(0)
	        		+features.get("hasMentionedPosition").toString()
	        		+features.get("hasMutantResidue").toString().charAt(0); 
	        	 
	        	// Check uniqueness.
	        	if(mutationStrings.contains(mutString)){
	        		continue;
	        	}
	        	log.debug(mutation.getId()+ " "+mutation.getType());

	        	//log.debug(mutString);
	        	mutationStrings.add(mutString);
	        	
	            //GroundedPointMutation gm = new GroundedPointMutation();
	        	cPosition = features.get("hasCorrectPosition").toString().charAt(0);
	        	position = Integer.parseInt(features.get("hasMentionedPosition").toString());
	        	mResidue = features.get("hasMutantResidue").toString().charAt(0);
	        	wtResidue = features.get("hasWildtypeResidue").toString().charAt(0);                        
	        	proteinIds = (String)features.get("isGroundedTo");
	        	
	        	
	        	//
	    		// Create author resource.
	    		//
	    		Resource creator = model.createResource(creatorUriString);
	    		
	    		//
	    		// Create Document resource.
	    		//
	    		Resource document = MieoModelResourceFactory.createSioArticleResource(doc.getName(),model, useUriSchema);
	    		
	    		Resource foafDocumentClass = model.createResource(Vocab.foafDocument.toString());
	    		document.addProperty(Vocab.type, foafDocumentClass);					

	    		//
	    		// Create PubMed Record resource.
	    	    //
	    		Resource pubmed_record = MieoModelResourceFactory.createPubMedRecordResource(doc.getName(),model);					
	    		document.addProperty(Vocab.isSubjectOf, pubmed_record);					
	    	
	    		
	    		//
	    		// Create Singular Point Mutation resource.
	    		//
	    		Resource singular_mutation = MieoModelResourceFactory.createSingularPointMutationResource(wtResidue,mResidue,position,model, useUriSchema);
	    		document.addProperty(Vocab.refersTo,singular_mutation);
	    		
	    		Resource singular_mutation_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(singular_mutation), model);
	    		
	    		Long s = mutation.getStartNode().getOffset();
				Long e = mutation.getEndNode().getOffset();
				String mutation_string = doc.getContent().getContent(s, e).toString();	
	    		Resource mutationTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
						document, 										
						Arrays.asList(singular_mutation_ann), 
						mutation_string, 
						s,
						e,
						model);	
	    		
	    		//
	    		// Create singular_mutation1_denotation resource.
	    		//
	    		Resource singular_mutation1_denotation = MieoModelResourceFactory.createSingularPointMutationDenotationResource(wtResidue, mResidue, position, model);
	    		singular_mutation1_denotation.addProperty(Vocab.type, Vocab.SingularMutationDenotation_OneLetterFormat);
	    		singular_mutation1_denotation.addProperty(Vocab.hasValue, wtResidue + Integer.toString(position) + mResidue);
	    		singular_mutation.addProperty(Vocab.isDenotedBy, singular_mutation1_denotation);
	    		
	    		
	    		
	    		
	    		//
	    		// Create comb_mut resource.
	    		//		
	    		Resource combined_mutation = MieoModelResourceFactory.createCombinedMutationResource(Arrays.asList(singular_mutation), model, useUriSchema);
	    		combined_mutation.addProperty(Vocab.type, Vocab.CombinedAminoAcidSequenceChange);
	    		combined_mutation.addProperty(Vocab.hasMember, singular_mutation);
	    		document.addProperty(Vocab.refersTo, combined_mutation);
	    		//
	    		// Create combined_mutation annotation resource.
	    		//
	    		Resource combined_mutation_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(combined_mutation), model);
	    		
	    		if(proteinIds != null){
	                String[] swissProtIdSplit = proteinIds.split("__");
	    			
	    			
	    			String[] proteinids = null;
	    			if (proteinIds.contains("_"))
	    				proteinids = proteinIds.split("_");
	    			else if (proteinIds.contains(","))
	    				proteinids = proteinIds.split(", ");
	    			else
	    				proteinids = proteinIds.split("_");

	    			 for(String swissProtId : swissProtIdSplit){
	    	            String[] swissProtIdSplit2 = swissProtId.split("_");
	    	            String uniprotid = swissProtIdSplit2[0];
	    	            
	    				System.err.println("uniprotid: "+swissProtIdSplit2[0]);
	    				System.err.println("offset: "+swissProtIdSplit2[1]);

	    				//
	    				// Create UniProt Record resource.
	    				//
	    				Resource uniprot_record = MieoModelResourceFactory.createUniProtRecordResource(uniprotid, model);
	    				
	    				//
	    				// Create ProteinVariant resource.
	    				//
	    				Resource protein = MieoModelResourceFactory.createProteinVariantResource(uniprotid, model,useUriSchema);
	    				protein.addProperty(Vocab.isSubjectOf, uniprot_record);
	    				document.addProperty(Vocab.refersTo, protein);
	    				
	    				
	    				
	    				Resource protein_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(protein), model);

	    				
	    				//
	    				// Create Mutation Application resource.
	    				//
	    				Resource mutation_application = MieoModelResourceFactory.createMutationApplicationResource(combined_mutation, protein, model, useUriSchema);						
	    				document.addProperty(Vocab.refersTo, mutation_application);
	    				
	    				Resource mutation_application_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(mutation_application), model);

	    				
	    				
	    				//
	    				// Create protein property change.
	    				/*/
	    				if(property != null && direction != null){
	    					//System.out.println("@1: "+property+direction);
	    					Resource protein_property_class = model.createResource(Vocab.goNS + property);
	    					protein_property_class.addProperty(Vocab.subClassOf, Vocab.ProteinProperty);
	    					Resource protein_property = MieoModelResourceFactory.createProteinPropertyResource(protein_property_class, protein, model, useUriSchema);							
	    				    document.addProperty(Vocab.refersTo,protein_property);

	    					Resource protein_property_change_class = MieoModelResourceFactory.proteinPropertyChangeClassResource(direction, model);
	    					Resource property_change = MieoModelResourceFactory.createProteinPropertyChangeResource(protein_property, protein_property_change_class, model, useUriSchema);					
	    					document.addProperty(Vocab.refersTo,property_change);							  
	    				    
	    					mutation_application.addProperty(Vocab.mutationApplicationCausesChange,property_change);
	    				    
	    				    
	    					Resource protein_property_change_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(property_change), model);
	    					Resource protein_property_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(protein_property), model);
	    					
	    					
	    					Resource proteinPropertyTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
	    							document, 										
	    							Arrays.asList(protein_property_ann), 
	    							mutation_string, 
	    							s,
	    							e,
	    							model);	
	    					
	    				    Resource statement_of_property_change = MieoModelPropertyReificator.reifyMutationApplicationCausesChange(
	    				    		mutation_application,
	    				    		property_change,
	    				    		model);					    
	    				    document.addProperty(Vocab.refersTo,statement_of_property_change);
	    					Resource statement_of_property_change_ann = 
	    						AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(statement_of_property_change), model);

	    				  
	    					//Resource protein_property = property_change.getRequiredProperty(Vocab.propertyChangeAppliesTo).getResource();						    
	    				    
	    				    /*
	    				    Resource protein_property_change_application = MieoModelPropertyReificator.reifyPropertyChangeAppliesTo(
	    				    		property_change,
	    				    		protein_property,
	    				    		model);					    
	    				    document.addProperty(Vocab.refersTo,protein_property_change_application);
	    				    *

	    				    
	    				    if(text != null && !text.equals("") && !text.equals(" ")){								
	    						/*Resource mutationImpactProvenence = MieoModelResourceFactory.createStringResource(
	    								text, 
	    								document, 
	    								new ArrayList<Resource>(Arrays.asList(property_change,protein_property)), 
	    								model);
	    						*
	    						//Resource impact_text_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(property_change,protein_property), model);

	    				    	Resource textSelector = AOModelResourceFactory.createTextSelectorResource(
	    								document, 										
	    								protein_property_change_ann, 
	    								text, 
	    								model);		
	    					}
	    				}	*/
	    				
	    				
	    				
	    				
	    			}
	    		}

	        	
	        	
	        }

        	return model;     
	}

	
	public static Model mutationImpactExtractionResults2Rdf(gate.Document doc, boolean useUriSchema) throws InvalidOffsetException{
		//
		// Create ontology model.
		//	
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("mieo", Vocab.mieoNS);
		model.setNsPrefix("sio", Vocab.sioNS);
		model.setNsPrefix("lsrn", Vocab.lsrnNS);
		model.setNsPrefix("str", Vocab.strNS);
		model.setNsPrefix("foaf", Vocab.foafNS);
		model.setNsPrefix("ao", Vocab.aoNS);
		model.setNsPrefix("aos", Vocab.aosNS);
		model.setNsPrefix("aot", Vocab.aotNS);
		model.setNsPrefix("aof", Vocab.aofNS);
		model.setNsPrefix("pav", Vocab.pavNS);		
		model.setNsPrefix("go", Vocab.goNS);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// Results of impact extraction.
    	//
        AnnotationSet impactsMf = doc.getAnnotations().get("Mutation-Impact");
        AnnotationSet impactOkv = doc.getAnnotations().get("Mutation-ImpactOKV");        
        
        mutationImpactExtractionResults2RdfSub(impactsMf, doc, model, useUriSchema);
        mutationImpactExtractionResults2RdfSub(impactOkv, doc, model, useUriSchema);
        
		return model;
	}
	
	
	private static void mutationImpactExtractionResults2RdfSub(AnnotationSet annotationSet, gate.Document doc, Model model, boolean useUriSchema) throws InvalidOffsetException{
        	
		for (Annotation mutation_impact : annotationSet) {
    		log.debug(mutation_impact.getId()+ " "+mutation_impact.getType());

        	FeatureMap features = mutation_impact.getFeatures();

    		//
    		// Related Gate Annotations (Mutation-Impact is root)
    		//
        	
    		Annotation gate_mutation_gann = doc.getAnnotations().get(Integer.parseInt(features.get("groundedPointMutationMentionId").toString()));

        	Annotation property_gann = doc.getAnnotations().get(Integer.parseInt(features.get("propertyMentionId").toString()));
    		//property_string = doc.getContent().getContent(property_annotation.getStartNode().getOffset(), property_annotation.getEndNode().getOffset()).toString();
       	
        	
        	Annotation direction_gann = null; 
    		if(features.get("directionMentionId")!=null){
        		direction_gann = doc.getAnnotations().get(Integer.parseInt(features.get("directionMentionId").toString()));
        		//direction_string = doc.getContent().getContent(direction_annotation.getStartNode().getOffset(), direction_annotation.getEndNode().getOffset()).toString();
    		}
    		Annotation impact_gann = null;
    		if(features.get("impactMentionId")!=null){
    			impact_gann = doc.getAnnotations().get(Integer.parseInt(features.get("impactMentionId").toString()));
        		//impact_string = doc.getContent().getContent(impact_annotation.getStartNode().getOffset(), impact_annotation.getEndNode().getOffset()).toString();
    		}
        	
    		
    		String property = ((String) features.get("affectProperty"));//.replace(":", "_");    		
    		String direction = ((String) features.get("direction"));
    		
    		
    		log.debug("property: "+property);
    		log.debug("direction: "+direction);

    		
    		// FILTER on properties
    		if(property.startsWith("P111")){
    			continue;
    		}
    		
    		// FILTER on direction
    		if(!direction.equalsIgnoreCase("Negative") &&
    				!direction.equalsIgnoreCase("Positive") &&
    				!direction.equalsIgnoreCase("Neutral")){
    			continue;
    		}
    		
    		
    		/*
    		char wtResidue = 0;
    		int position = -9999;
    		char mResidue = 0;
    		
    		String mutation_string = null;

    		String proteinIds = null;
    		int offset = -9999;
    		int cPosition = -9999;   		

    		String property = null;
    		String property_string = null;
    		String direction = null;
    		String direction_string = null;

    		String impact_string = null;
    		String mutation_impact_string = null;

    	
        	
        	mutation_impact_string = doc.getContent().getContent(impact.getStartNode().getOffset(), impact.getEndNode().getOffset()).toString();
        	
        	
    		
    	
    		
        	
        	String mutString = gate_mutation_ann.getFeatures().get("hasWildtypeResidue").toString().charAt(0)
        		+gate_mutation_ann.getFeatures().get("hasMentionedPosition").toString()
        		+gate_mutation_ann.getFeatures().get("hasMutantResidue").toString().charAt(0); 
        	 
        	// Check uniqueness.
        	//if(mutationStrings.contains(mutString)){
        		//continue;
        	//}
        	//log.debug(mutString);
        	//mutationStrings.add(mutString);
        	
            //GroundedPointMutation gm = new GroundedPointMutation();
        	cPosition = gate_mutation_ann.getFeatures().get("hasCorrectPosition").toString().charAt(0);
        	position = Integer.parseInt(gate_mutation_ann.getFeatures().get("hasMentionedPosition").toString());
        	mResidue = gate_mutation_ann.getFeatures().get("hasMutantResidue").toString().charAt(0);
        	wtResidue = gate_mutation_ann.getFeatures().get("hasWildtypeResidue").toString().charAt(0);                        
        	proteinIds = (String)gate_mutation_ann.getFeatures().get("isGroundedTo");
        	
        	    */
        	

    
        	
        	//
    		// Create author resource.
    		//
    		Resource creator = model.createResource(creatorUriString);
    		
    		//
    		// Create Document resource.
    		//
    		Resource document = MieoModelResourceFactory.createSioArticleResource(doc.getName(),model, useUriSchema);
    		
    		Resource foafDocumentClass = model.createResource(Vocab.foafDocument.toString());
    		document.addProperty(Vocab.type, foafDocumentClass);					

    		//
    		// Create PubMed Record resource.
    	    //
    		Resource pubmed_record = MieoModelResourceFactory.createPubMedRecordResource(doc.getName(),model);					
    		document.addProperty(Vocab.isSubjectOf, pubmed_record);					
    	
    		
    		
    		
    		
    		

    		
    		//
    		// Create Singular Point Mutation resource.
    		//
        	char cPosition = gate_mutation_gann.getFeatures().get("hasCorrectPosition").toString().charAt(0);
        	int position = Integer.parseInt(gate_mutation_gann.getFeatures().get("hasMentionedPosition").toString());
        	char mResidue = gate_mutation_gann.getFeatures().get("hasMutantResidue").toString().charAt(0);
        	char wtResidue = gate_mutation_gann.getFeatures().get("hasWildtypeResidue").toString().charAt(0);                        
        	String proteinIds = (String)gate_mutation_gann.getFeatures().get("isGroundedTo");
        	
        	       	
        	
        	
    		Resource singular_mutation = MieoModelResourceFactory.createSingularPointMutationResource(wtResidue,mResidue,position,model, useUriSchema);
    		document.addProperty(Vocab.refersTo,singular_mutation);    		
    		Resource singular_mutation_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(singular_mutation), model);    		
    		Long s = gate_mutation_gann.getStartNode().getOffset();
			Long e = gate_mutation_gann.getEndNode().getOffset();
			String mutation_string = doc.getContent().getContent(s, e).toString().toLowerCase();	
    		Resource mutationTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
					document, 										
					Arrays.asList(singular_mutation_ann), 
					mutation_string, 
					s,
					e-s,
					model);	
    		// add triples related to String Ontology.
    		mutationTextSelector.addProperty(Vocab.type, Vocab.stringStr);
    		
    
    		
    		
    		
    		//
    		// Create comb_mut resource.
    		//		
    		Resource combined_mutation = MieoModelResourceFactory.createCombinedMutationResource(Arrays.asList(singular_mutation), model, useUriSchema);
    		combined_mutation.addProperty(Vocab.type, Vocab.CombinedAminoAcidSequenceChange);
    		combined_mutation.addProperty(Vocab.hasMember, singular_mutation);
    		document.addProperty(Vocab.refersTo, combined_mutation);
    		//
    		// Create combined_mutation annotation resource.
    		//
    		Resource combined_mutation_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(combined_mutation), model);
    		
    		
    		
    		
    		if(proteinIds != null){
                String[] swissProtIdSplit = proteinIds.split("__");
    			
    			
    			String[] proteinids = null;
    			if (proteinIds.contains("_"))
    				proteinids = proteinIds.split("_");
    			else if (proteinIds.contains(","))
    				proteinids = proteinIds.split(", ");
    			else
    				proteinids = proteinIds.split("_");

    			 for(String swissProtId : swissProtIdSplit){
    	            String[] swissProtIdSplit2 = swissProtId.split("_");
    	            String uniprotid = swissProtIdSplit2[0];
    	            
    				//System.err.println("uniprotid: "+swissProtIdSplit2[0]);
    				//System.err.println("offset: "+swissProtIdSplit2[1]);

    				//
    				// Create UniProt Record resource.
    				//
    				Resource uniprot_record = MieoModelResourceFactory.createUniProtRecordResource(uniprotid, model);
    				
    				//
    				// Create ProteinVariant resource.
    				//
    				Resource protein = MieoModelResourceFactory.createProteinVariantResource(uniprotid, model,useUriSchema);
    				protein.addProperty(Vocab.isSubjectOf, uniprot_record);
    				document.addProperty(Vocab.refersTo, protein);
    				
    				
    				
    				Resource protein_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(protein), model);

    				
    				//
    				// Create Mutation Application resource.
    				//
    				Resource mutation_application = MieoModelResourceFactory.createMutationApplicationResource(combined_mutation, protein, model, useUriSchema);						
    				document.addProperty(Vocab.refersTo, mutation_application);
    				
    				Resource mutation_application_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(mutation_application), model);

    				
    				//
    				// Create protein property change.
    				//
    				if(property != null && direction != null){
    					System.out.println("@1: "+property+direction);
    					Resource protein_property_class = model.createResource(Vocab.goNS + property);
    					protein_property_class.addProperty(Vocab.subClassOf, Vocab.ProteinProperty);
    					Resource protein_property = MieoModelResourceFactory.createProteinPropertyResource(protein_property_class, protein, model, useUriSchema);							
    				    document.addProperty(Vocab.refersTo,protein_property);

    					Resource protein_property_change_class_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(protein_property_class), model);
    					Resource proteinPropertyChangeClassTextSelector = null;
    					if(direction_gann!=null){
    						//Long s5 = direction_gann.getStartNode().getOffset();
        					//Long e5 = direction_gann.getEndNode().getOffset();
        					//String direction_string = doc.getContent().getContent(direction_gann.getStartNode().getOffset(), direction_gann.getEndNode().getOffset()).toString();	
        					
    						// direction string
    						proteinPropertyChangeClassTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
        							document, 										
        							Arrays.asList(protein_property_change_class_ann), 
        							doc.getContent().getContent(direction_gann.getStartNode().getOffset(), direction_gann.getEndNode().getOffset()).toString(), 
        							direction_gann.getStartNode().getOffset(),
        							direction_gann.getEndNode().getOffset()-direction_gann.getStartNode().getOffset(),
        							model);
    			    		// add triples related to String Ontology.
    						proteinPropertyChangeClassTextSelector.addProperty(Vocab.type, Vocab.stringStr);
        				}
    					
    				    
    					Resource protein_property_change_class = MieoModelResourceFactory.proteinPropertyChangeClassResource(direction, model);
    					Resource property_change = MieoModelResourceFactory.createProteinPropertyChangeResource(protein_property, protein_property_change_class, model, useUriSchema);					
    					document.addProperty(Vocab.refersTo,property_change);							  
    				    
    					mutation_application.addProperty(Vocab.mutationApplicationCausesChange,property_change);
    				    
    				    
    					Resource protein_property_change_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(property_change), model);
    					Resource protein_property_ann = AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(protein_property), model);
    					
    				    Long s4 = impact_gann.getStartNode().getOffset();
    					Long e4 = impact_gann.getEndNode().getOffset();
    					String impact_string = doc.getContent().getContent(s4, e4).toString();	
    					Resource proteinPropertyChangeTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
    							document, 										
    							Arrays.asList(protein_property_change_ann), 
    							impact_string, 
    							s4,
    							e4-s4,
    							model);	
    					proteinPropertyChangeTextSelector.addProperty(Vocab.type, Vocab.stringStr);
    					
    					Long s2 = property_gann.getStartNode().getOffset();
    					Long e2 = property_gann.getEndNode().getOffset();
    					String property_string = doc.getContent().getContent(s2, e2).toString();//System.err.println("property_string: "+property_string);	
    					Resource proteinPropertyTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
    							document, 										
    							Arrays.asList(protein_property_ann), 
    							property_string, 
    							s2,
    							e2-s2,
    							model);	
    					proteinPropertyTextSelector.addProperty(Vocab.type, Vocab.stringStr);

    					
    					// add String Ontology related triples
    					if(impact_gann.getStartNode().getOffset() <= property_gann.getStartNode().getOffset()
    							&& impact_gann.getEndNode().getOffset() >= property_gann.getEndNode().getOffset()){
    						proteinPropertyChangeTextSelector.addProperty(Vocab.subStringStr, proteinPropertyTextSelector);
        					proteinPropertyTextSelector.addProperty(Vocab.superStringStr, proteinPropertyChangeTextSelector);
    					} else{
    						log.warn(proteinPropertyChangeTextSelector+" does not overlap "+proteinPropertyTextSelector+"!!!");
    					}
    					

    					if(proteinPropertyChangeClassTextSelector != null){
    						if(impact_gann.getStartNode().getOffset() <= direction_gann.getStartNode().getOffset()
        							&& impact_gann.getEndNode().getOffset() >= direction_gann.getEndNode().getOffset()){
            					proteinPropertyChangeTextSelector.addProperty(Vocab.subStringStr, proteinPropertyChangeClassTextSelector);
            					proteinPropertyChangeClassTextSelector.addProperty(Vocab.superStringStr, proteinPropertyChangeTextSelector);
    						}else{
        						log.warn(proteinPropertyChangeTextSelector+" does not overlap "+proteinPropertyChangeClassTextSelector+"!!!");
        					}
    					}

    					
    				    Resource statement_of_property_change = MieoModelPropertyReificator.reifyMutationApplicationCausesChange(
    				    		mutation_application,
    				    		property_change,
    				    		model);					    
    				    document.addProperty(Vocab.refersTo,statement_of_property_change);
    					Resource statement_of_property_change_ann = 
    						AOModelResourceFactory.createAnnotationResource(document, Arrays.asList(statement_of_property_change), model);
    					
    					Long s3 = mutation_impact.getStartNode().getOffset();
    					Long e3 = mutation_impact.getEndNode().getOffset();
    					String mutatin_impact_string = doc.getContent().getContent(s3, e3).toString();	
    					Resource statement_of_property_change_annTextSelector = AOModelResourceFactory.createOffsetRangeTextSelectorResource(
    							document, 										
    							Arrays.asList(statement_of_property_change_ann), 
    							mutatin_impact_string, 
    							s3,
    							e3-s3,
    							model);	
    					statement_of_property_change_annTextSelector.addProperty(Vocab.type, Vocab.stringStr);

    				}	
    				
    			}
    		}       	
        	
        }
		
	}
	
	
	private static String normalizeDirectionId(String directionId) {
		if(directionId.equals("[+]") 
				||  directionId.equalsIgnoreCase("positive")) 
			return "+";
		else if (directionId.equals("[-]") 
				||  directionId.equalsIgnoreCase("negative")) 
			return "-";
		else if (directionId.equalsIgnoreCase("[o]") 
				||  directionId.equalsIgnoreCase("neutral")) 
			return "O";
		return null;
	}

}
