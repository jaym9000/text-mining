/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.unbsj.cbakerlab.mutation_impact;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ca.unbsj.cbakerlab.mutation.GroundedMutation;
import ca.unbsj.cbakerlab.mutation.GroundedPointMutation;
import ca.unbsj.cbakerlab.mutation.ImpactDirection;
import ca.unbsj.cbakerlab.mutation.MutationImpact;
import ca.unbsj.cbakerlab.mutation.Protein;
import ca.unbsj.cbakerlab.mutation.ProteinMutant;
import ca.unbsj.cbakerlab.mutation.StabilityDirection;
import ca.unbsj.cbakerlab.mutation.StabDirection;

//edit on line 321 ******************************


/**
 *
 * @author UNBSJ
 */
public class DocumentInformation {
	private static final Logger log = Logger.getLogger(DocumentInformation.class);
	
	
    private Set<GroundedMutation> groundedMutations;
    
    private Set<MutationImpact> mutationImpacts;
    // private Set<StabilityDirection> stabilityDirections; //a
    private Set<GroundedPointMutation> groundedPointMutations;
    
    //private DocumentInfoStatistics statistics;
    private String name;
    
    private Map<GroundedMutation,List<String>> contextOfPositive = new HashMap<GroundedMutation, List<String>>();

    private String mutationOntologyURI;

    {	    Properties pro = new Properties();
    try {
		pro.load(new FileInputStream(new File(this.getClass().getClassLoader().getResource("project.properties").toURI())));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    mutationOntologyURI = pro.getProperty("mutationOntologyURI");}
    
    
    Set<String> mutationsMentioned;
    
    
	/**
	 * Comparator by discourseId.
	 */
	static DocumentInformationComparatorByName entityCompByDiscourseId = new DocumentInformationComparatorByName();
    
    public DocumentInformation(String name){
    	this.name = name;  
    	groundedPointMutations = new HashSet<GroundedPointMutation>();
    }
    
    public DocumentInformation(gate.Document doc) throws InvalidOffsetException, IOException, URISyntaxException{
    	this.name = doc.getName();  
    	//groundedPointMutations = new HashSet<GroundedPointMutation>();
    	//gateDocument2documentInformation(doc);

    	//postprocess(doc);

    	gate2docInfo(doc);

    }
    
    
    private void gate2docInfo(gate.Document doc) throws URISyntaxException {
		//DocumentInformation documentInformation = new DocumentInformation(doc.getName());
		
		// Return result as a Iterable<GroundedMutation>.
    	
    	//
    	// Results of mutation grounding.
    	//
        Set<GroundedPointMutation> gmResult = new HashSet<GroundedPointMutation>();
        AnnotationSet mutations = doc.getAnnotations().get("GroundedPointMutation");
        
        if(mutations.size() == 0){
        	log.warn("No PointMutations");
        }else{
            log.debug("mutations.size(): "+mutations.size());
        }
        
        Set<String> mutationStrings = new HashSet<String>();
        for (Annotation mutation : mutations) {
        	FeatureMap features = mutation.getFeatures();
        	//log.debug(features);
        	
        	String mutString = features.get("hasWildtypeResidue").toString().charAt(0)
        		+features.get("hasMentionedPosition").toString()
        		+features.get("hasMutantResidue").toString().charAt(0); 
        	 
        	// Check uniqueness.
        	if(mutationStrings.contains(mutString)){
        		continue;
        	}
        	//log.debug(mutString);
        	mutationStrings.add(mutString);
        	
            GroundedPointMutation gm = new GroundedPointMutation();
            gm.setCorrectPosition(Integer.parseInt(features.get("hasCorrectPosition").toString()));
            gm.setMentionedPosition(Integer.parseInt(features.get("hasMentionedPosition").toString()));
            gm.setMutantResidue(features.get("hasMutantResidue").toString().charAt(0));
            gm.setWildtypeResidue(features.get("hasWildtypeResidue").toString().charAt(0));
            gm.setSequence(features.get("zmSequence").toString());
            gm.setWtSequence(features.get("wtSequence").toString());

           // System.err.println(features.get("isGroundedTo"));
           
            //List<String[]> isgrounded = (List<String[]>)features.get("isGroundedTo");
            //for(String s : isgrounded){
            //    System.err.println(s[0]+s[1]);

            //}
            
            gm.setSwissProtId((String)features.get("isGroundedTo"));
            //log.info(doc.getContent().toString().substring(mutation.getStartNode().getOffset().intValue(), mutation.getEndNode().getOffset().intValue()).trim());
            gmResult.add(gm);
            //System.err.println("-----------> "+gm.getCorrectPosition());
        }

        AnnotationSet mutationTempSet = doc.getAnnotations().get("MutationTemp");
        for (Annotation mutation : mutationTempSet) {
        	String mutationString = (String)mutation.getFeatures().get("wNm");
        	if(mutationString==null)continue;
        	String[] parsed = parseWNMFormat(mutationString);
        	if(!mutationStrings.contains(mutationString)){
        		GroundedPointMutation gm = new GroundedPointMutation();
        		gm.setWildtypeResidue(parsed[0].charAt(0));
                gm.setMentionedPosition(Integer.parseInt(parsed[1]));
                gm.setMutantResidue(parsed[2].charAt(0)); 
                gmResult.add(gm);
        	}
        }

        AnnotationSet stabilityTempSet = doc.getAnnotations().get("StabilityDirection"); //a
        for (Annotation stability : stabilityTempSet) {         //a
        	String stabilityString = (String)stability.getFeatures().get("StabilityDirectionRule1");
        	if(stabilityString==null)continue;
        	String[] parsed = parseWNMFormat(stabilityString);
        	if(!stabilityString.contains(stabilityString)){
        		GroundedPointMutation gm = new GroundedPointMutation();
        		gm.setWildtypeResidue(parsed[0].charAt(0));
                gm.setMentionedPosition(Integer.parseInt(parsed[1]));
                gm.setMutantResidue(parsed[2].charAt(0));
                gmResult.add(gm);

                FeatureMap stabilityDirectionAnnFeatures = stability.getFeatures();

                int stabilityDirectionAnnId = (Integer) stabilityDirectionAnnFeatures.get("StabilityDirectionRule1");

                Annotation stabilityDirectionAnn = doc.getAnnotations().get(stabilityDirectionAnnId);
                stabilityDirectionAnnFeatures = stabilityDirectionAnn.getFeatures();

                String sDirection = (String)stabilityDirectionAnnFeatures.get("StabilityDirectionRule1"); //a

                StabilityDirection sDirection2 = new StabilityDirection(sDirection); //a

                GroundedMutation groundedMutation = new GroundedMutation();
                groundedMutation.setStabilityDirection(sDirection2); //a

        	}
        }
        
        /*/ For debugging.
        for(GroundedPointMutation gpm : gmResult) {    // System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");          
            if(!gpm.getNormalizedForm().equalsIgnoreCase(gpm.getNormalizedFormMentioned())){
            	GlobalStatistics.correctedposition++;
            	//log.debug("GPMc: "+gpm.getNormalizedForm());
            	//log.debug("GPMm: "+gpm.getNormalizedFormMentioned());
            }else GlobalStatistics.notCorrectedposition++;
        }*/
        this.setAllGroundedPointMutations(gmResult);
        log.debug("gmResult.size(): "+gmResult.size()); 
        
        
        
    	//
    	// Results of mutation impact extraction for MOLECULAR FUNCTIONS.
    	//
        Set<GroundedMutation> groundedMutations = new HashSet<GroundedMutation>();
        
        AnnotationSet mutationImpactAnnotationSet = doc.getAnnotations().get("Mutation-Impact");
        // AnnotationSet stabilityDirectionAnnotationSet = doc.getAnnotations().get("StabilityDirection"); //a
        log.debug("mutationImpactMentionAnnotationSet: "+mutationImpactAnnotationSet.size());
        for(Annotation mutationImpactAnn : mutationImpactAnnotationSet) {
            //create a grounded mutation object


            FeatureMap mutationImpactAnnFeatures = mutationImpactAnn.getFeatures();

            System.err.println("mutationImpactAnnFeatures: "+mutationImpactAnnFeatures);
            int groundedPointMutationMentionAnnId = (Integer)
            mutationImpactAnnFeatures
            .get("groundedPointMutationMentionId");
            log.debug("groundedPointMutationMentionAnnId: "+groundedPointMutationMentionAnnId);

            Annotation groundedPointMutationMentionAnn = doc.getAnnotations().get(groundedPointMutationMentionAnnId);
            FeatureMap groundedPointMutationMentionAnnFeatures = groundedPointMutationMentionAnn.getFeatures();

            // int StabilityDirAnnId = (Integer) 
            // Annotation stabilityDirectionMentionAnn = doc.getAnnotations().get(StabilityDirAnnId);
            // FeatureMap stabilityDirectionMentionAnnFeatures = stabilityDirectionMentionAnn.getFeatures();
            
            String swissProtId = (String)groundedPointMutationMentionAnnFeatures.get("isGroundedTo");
            String wtSequence = (String)groundedPointMutationMentionAnnFeatures.get("wtSequence");
            String mResidue = (String)groundedPointMutationMentionAnnFeatures.get("hasMutantResidue");

            String sDirection1 = (String)groundedPointMutationMentionAnnFeatures.get("StabilityDirectionRule1"); //a
            // String sDirection2 = (String)stabilityDirectionMentionAnnFeatures.get("StabilityDir"); //a
            
            //create the wildtype protein
            // Artjom: temporary
            String[] swissProtIdSplit = swissProtId.split("__");
            for(String s : swissProtIdSplit){
            	String[] swissProtIdSplit2 = swissProtIdSplit[0].split("_");
                Protein protein = new Protein(swissProtIdSplit2[0],wtSequence);
                
                StabilityDirection sDirection = new StabilityDirection(); //a
                // StabilityDirection sDirection3 = new StabilityDirection(sDirection2); //a
                
                GroundedMutation groundedMutation = new GroundedMutation();
                groundedMutation.setProteinAppliedTo(protein);
                log.trace(mResidue);

                // groundedMutation.setStabilityDirection(sDirection3); //a
                // log.trace(mResidue);    
                //create the resulting protein mutant
               ///// StringBuilder mSequenceSb = new StringBuilder(wtSequence);
                /////mSequenceSb.setCharAt(Integer.parseInt(swissProtIdSplit2[1]), mResidue.charAt(0));
                /////String mSequence = mSequenceSb.toString();
                groundedMutation.setStabilityDirection(sDirection); //a
                log.trace(mResidue); //a
                
                String mSequence = groundedPointMutationMentionAnnFeatures.get("zmSequence").toString();
                ProteinMutant proteinMutant = new ProteinMutant(mSequence, true);
                groundedMutation.setResultingProteinMutant(proteinMutant);
                
                
                GroundedPointMutation gm = new GroundedPointMutation();
                gm.setCorrectPosition(Integer.parseInt(groundedPointMutationMentionAnnFeatures.get("hasCorrectPosition").toString()));
                gm.setMentionedPosition(Integer.parseInt(groundedPointMutationMentionAnnFeatures.get("hasMentionedPosition").toString()));
                gm.setMutantResidue(groundedPointMutationMentionAnnFeatures.get("hasMutantResidue").toString().charAt(0));
                gm.setWildtypeResidue(groundedPointMutationMentionAnnFeatures.get("hasWildtypeResidue").toString().charAt(0));
                


                //create all involved point mutations
                Set<GroundedPointMutation> groundedPointMutations = new HashSet<GroundedPointMutation>();
               // Annotation pointMutationAnnotation = doc.getAnnotations().get("GroundedPointMutation", mutant.getStartNode().getOffset(),mutant.getEndNode().getOffset()).iterator().next();
               // FeatureMap pointMutationFeatures = pointMutationAnnotation.getFeatures();
                
                GroundedPointMutation groundedPointMutation = new GroundedPointMutation();
                groundedPointMutation.setCorrectPosition((Integer)groundedPointMutationMentionAnnFeatures.get("hasCorrectPosition"));
                groundedPointMutation.setMentionedPosition((Integer)groundedPointMutationMentionAnnFeatures.get("hasMentionedPosition"));
                groundedPointMutation.setMutantResidue(((String)groundedPointMutationMentionAnnFeatures.get("hasMutantResidue")).charAt(0));
                groundedPointMutation.setWildtypeResidue(((String)groundedPointMutationMentionAnnFeatures.get("hasWildtypeResidue")).charAt(0));
                groundedPointMutation.setSwissProtId((String)groundedPointMutationMentionAnnFeatures.get("isGroundedTo"));
                //groundedPointMutation.setSwissProtId((String)pointMutationFeatures.get("isGroundedTo")); // for multiple UniProtIds
                //gm.setSwissProtId((List<String[]>)features.get("isGroundedTo"));
                
                groundedPointMutations.add(groundedPointMutation);
                //allGroundedPointMutations.addAll(groundedPointMutations);
                groundedMutation.setCompoundMutations(groundedPointMutations);

                
                
                int impactMentionAnnId = (Integer)mutationImpactAnnFeatures.get("impactMentionId");
                log.debug("impactMentionAnnId: "+impactMentionAnnId);

                Annotation impactMentionAnn = doc.getAnnotations().get(impactMentionAnnId);
                FeatureMap relatedImpactMentionFeatures = impactMentionAnn.getFeatures();
                
                
                // Only if direction and property are available, add GroundedMutation.
                if(relatedImpactMentionFeatures.get("direction") !=null && relatedImpactMentionFeatures.get("propertyMentionId") != null){
                	//retrieve related impacts
                    Set<MutationImpact> relatedMutationImpacts = new HashSet<MutationImpact>();

                    //log.trace("====> "+mutant.getStartNode().getOffset().toString()+":"+mutant.getEndNode().getOffset().toString());
                    //for(String m : mutantImpactMap.keySet()){
                    //	log.trace(m+" : ["+mutantImpactMap.get(m)+"]");
                    //}
                	
                	
                   // Annotation relatedImpactMention = doc.getAnnotations().get(relatedImpactId);
                    //FeatureMap relatedImpactMentionFeatures = relatedImpactMention.getFeatures();

                    //create a new mutationImpact object
                    MutationImpact mutationImpact = new MutationImpact();

                    //set direction
                    if(((String)relatedImpactMentionFeatures.get("direction")).equals("Positive")) {
                        mutationImpact.setDirection(ImpactDirection.POSITIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures.get("direction")).equals("Negative")) {
                        mutationImpact.setDirection(ImpactDirection.NEGATIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures.get("direction")).equals("Neutral")) {
                        mutationImpact.setDirection(ImpactDirection.NEUTRAL);
                    }
                    
                    
                    int propertyMentionAnnId = (Integer) relatedImpactMentionFeatures.get("propertyMentionId");
                    log.debug("propertyMentionAnnId: "+propertyMentionAnnId);

                    Annotation propertyMentionAnn = doc.getAnnotations().get(propertyMentionAnnId);
                    FeatureMap propertyMentionAnnFeatures = propertyMentionAnn.getFeatures();
                    String propertyId = (String)propertyMentionAnnFeatures.get("isGroundedTo");
                    propertyId = propertyId.replaceAll(":", "_");
                    mutationImpact.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+propertyId));
                    log.trace(mutationImpact.getAffectedProteinProperty());
                    relatedMutationImpacts.add(mutationImpact);
                
                
                    
                  //THIS MAKES SURE THAT NO MUTANT IS EXPORTED WHEN CONDRACTIONARY
                    //TOWARDS OTHER HIGHER SCORED IMPACT!
                    groundedMutation.setMutationImpacts(relatedMutationImpacts);   
                    groundedMutations.add(groundedMutation);   
                    this.addGrounedMutation(groundedMutation);
                }

                int impactMentionAnnId_1 = (Integer)mutationImpactAnnFeatures.get("impactMentionId"); //a for everything below till the if statement ends
                log.debug("impactMentionAnnId: "+impactMentionAnnId_1);

                Annotation impactMentionAnn_1 = doc.getAnnotations().get(impactMentionAnnId);
                FeatureMap relatedImpactMentionFeatures_1 = impactMentionAnn.getFeatures();
                
                
                // Only if direction and property are available, add GroundedMutation.
                if(relatedImpactMentionFeatures_1.get("direction") !=null && relatedImpactMentionFeatures_1.get("propertyMentionId") != null){
                	//retrieve related impacts
                    // Set<StabilityDirection> relatedStabilityDirections = new HashSet<StabilityDirection>();

                    //log.trace("====> "+mutant.getStartNode().getOffset().toString()+":"+mutant.getEndNode().getOffset().toString());
                    //for(String m : mutantImpactMap.keySet()){
                    //	log.trace(m+" : ["+mutantImpactMap.get(m)+"]");
                    //}
                	
                	
                   // Annotation relatedImpactMention = doc.getAnnotations().get(relatedImpactId);
                    //FeatureMap relatedImpactMentionFeatures = relatedImpactMention.getFeatures();

                    //create a new mutationImpact object
                    StabilityDirection stabilityDirection = new StabilityDirection();

                    //set direction
                    if(((String)relatedImpactMentionFeatures_1.get("direction")).equals("Positive")) {
                        stabilityDirection.setStabilityDirection(ImpactDirection.POSITIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures_1.get("direction")).equals("Negative")) {
                        stabilityDirection.setStabilityDirection(ImpactDirection.NEGATIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures_1.get("direction")).equals("Neutral")) {
                        stabilityDirection.setStabilityDirection(ImpactDirection.NEUTRAL);
                    }
                    
                    
                    int propertyMentionAnnId = (Integer) relatedImpactMentionFeatures_1.get("propertyMentionId");
                    log.debug("propertyMentionAnnId: "+propertyMentionAnnId);

                    Annotation propertyMentionAnn = doc.getAnnotations().get(propertyMentionAnnId);
                    FeatureMap propertyMentionAnnFeatures = propertyMentionAnn.getFeatures();
                    String propertyId = (String)propertyMentionAnnFeatures.get("isGroundedTo");
                    propertyId = propertyId.replaceAll(":", "_");
                    stabilityDirection.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+propertyId));
                    log.trace(stabilityDirection.getAffectedProteinProperty());
                    // relatedStabilityDirections.add(stabilityDirection);
                
                
                    
                  //THIS MAKES SURE THAT NO MUTANT IS EXPORTED WHEN CONDRACTIONARY
                    //TOWARDS OTHER HIGHER SCORED IMPACT!
                    groundedMutation.setStabilityDirection(stabilityDirection);  
                    groundedMutations.add(groundedMutation);   
                    this.addGrounedMutation(groundedMutation);
                }

                
                
                         
                
            }           
           
        }    
        
        
        
        //
    	// Results of mutation impact extraction for MOLECULAR FUNCTIONS.
    	//
        Set<GroundedMutation> groundedMutationsOkv = new HashSet<GroundedMutation>();
        
        AnnotationSet mutationOkvImpactAnnotationSet = doc.getAnnotations().get("Mutation-ImpactOKV");
        log.debug("mutationOkvImpactAnnotationSet: "+mutationOkvImpactAnnotationSet.size());
        for(Annotation mutationImpactAnn : mutationOkvImpactAnnotationSet) {
            //create a grounded mutation object


            FeatureMap mutationImpactAnnFeatures = mutationImpactAnn.getFeatures();

            System.err.println("mutationImpactAnnFeatures: "+mutationImpactAnnFeatures);
            int groundedPointMutationMentionAnnId = (Integer)
            mutationImpactAnnFeatures
            .get("groundedPointMutationMentionId");
            log.debug("groundedPointMutationMentionAnnId: "+groundedPointMutationMentionAnnId);

            Annotation groundedPointMutationMentionAnn = doc.getAnnotations().get(groundedPointMutationMentionAnnId);
            FeatureMap groundedPointMutationMentionAnnFeatures = groundedPointMutationMentionAnn.getFeatures();

            
            
            String swissProtId = (String)groundedPointMutationMentionAnnFeatures.get("isGroundedTo");
            String wtSequence = (String)groundedPointMutationMentionAnnFeatures.get("wtSequence");
            String mResidue = (String)groundedPointMutationMentionAnnFeatures.get("hasMutantResidue");

            String sDirection1 = (String)groundedPointMutationMentionAnnFeatures.get("StabilityDirectionRule1"); //a
            
            //create the wildtype protein
            // Artjom: temporary
            String[] swissProtIdSplit = swissProtId.split("__");
            for(String s : swissProtIdSplit){
            	String[] swissProtIdSplit2 = swissProtIdSplit[0].split("_");
                Protein protein = new Protein(swissProtIdSplit2[0],wtSequence);

                StabilityDirection sDirection = new StabilityDirection(); //a

                GroundedMutation groundedMutation = new GroundedMutation();
                groundedMutation.setProteinAppliedTo(protein);
                groundedMutation.setStabilityDirection(sDirection); //a
                
                //create the resulting protein mutant
               // StringBuilder mSequenceSb = new StringBuilder(wtSequence);
                //mSequenceSb.setCharAt(Integer.parseInt(swissProtIdSplit2[1]), mResidue.charAt(0));
                //String mSequence = mSequenceSb.toString();
                String mSequence = groundedPointMutationMentionAnnFeatures.get("zmSequence").toString();
                ProteinMutant proteinMutant = new ProteinMutant(mSequence, true);
                groundedMutation.setResultingProteinMutant(proteinMutant);
                
                
                GroundedPointMutation gm = new GroundedPointMutation();
                gm.setCorrectPosition(Integer.parseInt(groundedPointMutationMentionAnnFeatures.get("hasCorrectPosition").toString()));
                gm.setMentionedPosition(Integer.parseInt(groundedPointMutationMentionAnnFeatures.get("hasMentionedPosition").toString()));
                gm.setMutantResidue(groundedPointMutationMentionAnnFeatures.get("hasMutantResidue").toString().charAt(0));
                gm.setWildtypeResidue(groundedPointMutationMentionAnnFeatures.get("hasWildtypeResidue").toString().charAt(0));
                


                //create all involved point mutations
                Set<GroundedPointMutation> groundedPointMutations = new HashSet<GroundedPointMutation>();
               // Annotation pointMutationAnnotation = doc.getAnnotations().get("GroundedPointMutation", mutant.getStartNode().getOffset(),mutant.getEndNode().getOffset()).iterator().next();
               // FeatureMap pointMutationFeatures = pointMutationAnnotation.getFeatures();
                
                GroundedPointMutation groundedPointMutation = new GroundedPointMutation();
                groundedPointMutation.setCorrectPosition((Integer)groundedPointMutationMentionAnnFeatures.get("hasCorrectPosition"));
                groundedPointMutation.setMentionedPosition((Integer)groundedPointMutationMentionAnnFeatures.get("hasMentionedPosition"));
                groundedPointMutation.setMutantResidue(((String)groundedPointMutationMentionAnnFeatures.get("hasMutantResidue")).charAt(0));
                groundedPointMutation.setWildtypeResidue(((String)groundedPointMutationMentionAnnFeatures.get("hasWildtypeResidue")).charAt(0));
                groundedPointMutation.setSwissProtId((String)groundedPointMutationMentionAnnFeatures.get("isGroundedTo"));
                //groundedPointMutation.setSwissProtId((String)pointMutationFeatures.get("isGroundedTo")); // for multiple UniProtIds
                //gm.setSwissProtId((List<String[]>)features.get("isGroundedTo"));
                
                groundedPointMutations.add(groundedPointMutation);
                //allGroundedPointMutations.addAll(groundedPointMutations);
                groundedMutation.setCompoundMutations(groundedPointMutations);

                
                
                int impactMentionAnnId = (Integer)mutationImpactAnnFeatures.get("impactMentionId");
                log.debug("impactMentionAnnId: "+impactMentionAnnId);

                Annotation impactMentionAnn = doc.getAnnotations().get(impactMentionAnnId);
                FeatureMap relatedImpactMentionFeatures = impactMentionAnn.getFeatures();

                //Annotation stabilityDirectionAnn = doc.getAnnotations().get();
                
                
                // Only if direction and property are available, add GroundedMutation.
                if(relatedImpactMentionFeatures.get("direction") !=null && relatedImpactMentionFeatures.get("propertyMentionId") != null){
                	//retrieve related impacts
                    Set<MutationImpact> relatedMutationImpacts = new HashSet<MutationImpact>();

                    //log.trace("====> "+mutant.getStartNode().getOffset().toString()+":"+mutant.getEndNode().getOffset().toString());
                    //for(String m : mutantImpactMap.keySet()){
                    //	log.trace(m+" : ["+mutantImpactMap.get(m)+"]");
                    //}
                	
                	
                   // Annotation relatedImpactMention = doc.getAnnotations().get(relatedImpactId);
                    //FeatureMap relatedImpactMentionFeatures = relatedImpactMention.getFeatures();

                    //create a new mutationImpact object
                    MutationImpact mutationImpact = new MutationImpact();

                    
                    
                    //set stability direction


                    
                    
                    //set direction
                    if(((String)relatedImpactMentionFeatures.get("direction")).equals("Positive")) {
                        mutationImpact.setDirection(ImpactDirection.POSITIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures.get("direction")).equals("Negative")) {
                        mutationImpact.setDirection(ImpactDirection.NEGATIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures.get("direction")).equals("Neutral")) {
                        mutationImpact.setDirection(ImpactDirection.NEUTRAL);
                    }
                    
                    
                    int propertyMentionAnnId = (Integer) relatedImpactMentionFeatures.get("propertyMentionId");
                    log.debug("propertyMentionAnnId: "+propertyMentionAnnId);

                    Annotation propertyMentionAnn = doc.getAnnotations().get(propertyMentionAnnId);
                    FeatureMap propertyMentionAnnFeatures = propertyMentionAnn.getFeatures();
                    String propertyId = null;// = (String)propertyMentionAnnFeatures.get("isGroundedTo");
                    
                    if(((String)propertyMentionAnn.getFeatures().get("kind")).equals("Km")) {
                    	propertyId = "MichaelisConstant";
    				} else if (((String)propertyMentionAnn.getFeatures().get("kind")).equals("kcat")) {
    					propertyId = "CatalyticRateConstant";
    				} else if (((String)propertyMentionAnn.getFeatures().get("kind")).equals("kcatOverKm")) {
    					propertyId = "EnzymeEfficiencyConstant";
    				} 
                    
                    
                    mutationImpact.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+propertyId));
                    log.trace(mutationImpact.getAffectedProteinProperty());
                    relatedMutationImpacts.add(mutationImpact);
                    
                
                    
                  //THIS MAKES SURE THAT NO MUTANT IS EXPORTED WHEN CONDRACTIONARY
                    //TOWARDS OTHER HIGHER SCORED IMPACT!
                    groundedMutation.setMutationImpacts(relatedMutationImpacts);   
                    groundedMutations.add(groundedMutation);   
                    this.addGrounedMutation(groundedMutation);
                }
                
                         
                
            }           
           
        }    
        
        //this.setAllGroundedMutations(groundedMutations);
	}
    
    
    
  
    private void postprocess(Document doc) throws InvalidOffsetException, IOException, URISyntaxException {
		//DocumentInformation documentInformation = new DocumentInformation(doc.getName());
		
		// Return result as a Iterable<GroundedMutation>.
        Set<GroundedPointMutation> gmResult = new HashSet<GroundedPointMutation>();
        AnnotationSet mutations = doc.getAnnotations().get("GroundedPointMutation");
        
        if(mutations.size() == 0){
        	log.info("No PointMutations");
        }else{
        	log.info("mutations.size(): "+mutations.size());
        }
        
        System.err.println("=================================================\n" +
        		"GROUNDED POINT MUTATIONS" +
        		"\n=================================================");
        Set<String> mutationStrings = new HashSet<String>();
        for (Annotation mutation : mutations) {
        	FeatureMap features = mutation.getFeatures();
        	
        	String mutString = features.get("hasWildtypeResidue").toString().charAt(0)
        		+features.get("hasMentionedPosition").toString()
        		+features.get("hasMutantResidue").toString().charAt(0); 
        	 
        	// Check uniqueness.
        	if(mutationStrings.contains(mutString)){
        		continue;
        	}
        	log.trace(mutString);
        	mutationStrings.add(mutString);
        	
            GroundedPointMutation gm = new GroundedPointMutation();
            gm.setCorrectPosition(Integer.parseInt(features.get("hasCorrectPosition").toString()));
            gm.setMentionedPosition(Integer.parseInt(features.get("hasMentionedPosition").toString()));
            gm.setMutantResidue(features.get("hasMutantResidue").toString().charAt(0));
            gm.setWildtypeResidue(features.get("hasWildtypeResidue").toString().charAt(0));
            //gm.setSwissProtId(features.get("isGroundedTo").toString());
            gm.setSwissProtId((String)features.get("isGroundedTo"));
            //log.info(doc.getContent().toString().substring(mutation.getStartNode().getOffset().intValue(), mutation.getEndNode().getOffset().intValue()).trim());
            gmResult.add(gm);
        }

        System.err.println("gmResult.size: "+gmResult.size());
        System.err.println(gmResult);
        
        AnnotationSet mutationTempSet = doc.getAnnotations().get("Mutation");
        for (Annotation mutation : mutationTempSet) {
        	String mutationString = (String)mutation.getFeatures().get("WNMFormat");
        	if(mutationString==null)continue;
        	String[] parsed = Utils.parseWNMFormat(mutationString);
        	if(!mutationStrings.contains(mutationString)){
        		GroundedPointMutation gm = new GroundedPointMutation();
        		gm.setWildtypeResidue(parsed[0].charAt(0));
                gm.setMentionedPosition(Integer.parseInt(parsed[1]));
                gm.setMutantResidue(parsed[2].charAt(0)); 
                gmResult.add(gm);
        	}
        }
        
        System.err.println("gmResult.size: "+gmResult.size());
        System.err.println(gmResult);
        
        
        /*/ For debugging.
        for(GroundedPointMutation gpm : gmResult) {    // System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");          
            if(!gpm.getNormalizedForm().equalsIgnoreCase(gpm.getNormalizedFormMentioned())){
            	GlobalStatistics.correctedposition++;
            	System.out.println("GPMc: "+gpm.getNormalizedForm());
                System.out.println("GPMm: "+gpm.getNormalizedFormMentioned());
            }else GlobalStatistics.notCorrectedposition++;
        }*/

        
        //this.setAllGroundedPointMutations(gmResult);
        //log.debug("gmResult.size(): "+gmResult.size());  
        

        // Initialize output objects.        
        Set<MutationImpact> allMutationImpacts = new HashSet<MutationImpact>();
     
        
        //initiate a map between impacts and mutants.
        Map<String,List<Integer>> mutantImpactMap = new HashMap<String,List<Integer>>();

        /*
         *  MOLECULAR FUNCTION
         */
        
        System.err.println("=====================================================================\n" +
        		"MOLECULAR FUNCTION IMPACTS" +
        		"\n==================================================================");
        //retrieve all mutation impacts
        AnnotationSet mutationImpactMentionAnnotationSet = doc.getAnnotations().get("Mutation-Impact");
        log.debug("mutationImpactMentionAnnotationSet: "+mutationImpactMentionAnnotationSet.size());
        
        log.debug("Size of allMutationImpacts before MF: "+allMutationImpacts.size());
        for(Annotation mutationImpactMentionAnnotation : mutationImpactMentionAnnotationSet) {
        	log.debug("mutantString comes: "+mutationImpactMentionAnnotation.getFeatures().get("mutantString"));
            FeatureMap mutationImpactFeatures = mutationImpactMentionAnnotation.getFeatures();
            
            ///if(!impactsToRemove.contains(mutationImpactMentionAnnotation.getId())) {
            
            	log.debug("mutantString checked: "+mutationImpactMentionAnnotation.getFeatures().get("mutantString"));
                Long[] mutantOffsets = (Long[])mutationImpactFeatures.get("mutant");
                
                String mutantString = mutantOffsets[0].toString()+":"+mutantOffsets[1].toString();
                //add to map
                if(mutantImpactMap.get(mutantOffsets)==null) {
                    List<Integer> mutantList = new ArrayList<Integer>();
                    mutantList.add(mutationImpactMentionAnnotation.getId());
                    log.debug("10 "+mutantString);
                    if(mutantImpactMap.containsKey(mutantString)){//System.err.println("\nERRRRRRRRROOOOOOOORRRRRRRRRRR\n");
                    	mutantImpactMap.get(mutantString).add(mutationImpactMentionAnnotation.getId());
                    }else{
                    	mutantImpactMap.put(mutantString, mutantList);
                    }
                    
                    log.debug("mutantImpactMap: "+mutantImpactMap);
                    
                    log.trace("11");
                } else {//log.debug("ERRRRRRRRRRRRRRRROOOOOOOOOOOOOOOORRRRRRRRRRRR");
                    List<Integer> mutantList = mutantImpactMap.get(mutantOffsets);
                    mutantList.add(mutationImpactMentionAnnotation.getId());
                    if(mutantImpactMap.containsKey(mutantString)){
                    	mutantImpactMap.get(mutantString).add(mutationImpactMentionAnnotation.getId());
                    }else{
                    	mutantImpactMap.put(mutantString, mutantList);
                    }
                    log.trace("22");
                }
                
                /*/add to map
                if(mutantImpactMap.get(mutantOffsets)==null) {
                    List mutantList = new ArrayList<Integer>();
                    mutantList.add(mutationImpactAnnotation.getId());
                    mutantImpactMap.put(mutantOffsets[0].toString()+":"+mutantOffsets[1].toString(), mutantList);
                } else {
                    List mutantList = mutantImpactMap.get(mutantOffsets);
                    mutantList.add(mutationImpactAnnotation.getId());
                    mutantImpactMap.put(mutantOffsets[0].toString()+":"+mutantOffsets[1].toString(), mutantList);
                }*/
                
                //create a new mutationImpact object
                MutationImpact mutationImpact = new MutationImpact();

                //set direction
                if(((String)mutationImpactFeatures.get("direction")).equals("Positive")) {
                    mutationImpact.setDirection(ImpactDirection.POSITIVE);
                }
                else if(((String)mutationImpactFeatures.get("direction")).equals("Negative")) {
                    mutationImpact.setDirection(ImpactDirection.NEGATIVE);
                }
                else if(((String)mutationImpactFeatures.get("direction")).equals("Neutral")) {
                    mutationImpact.setDirection(ImpactDirection.NEUTRAL);
                }
                mutationImpact.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+(String)mutationImpactFeatures.get("affectProperty")));
                allMutationImpacts.add(mutationImpact);
                               
        }
        log.trace("Size of allMutationImpacts after MF: "+allMutationImpacts.size());


        /*
         *  KINETIC VARIABLES
         */
        System.err.println("=====================================================================\n" +
        		"KINETIC VARIABLE IMPACTS" +
        		"\n==================================================================");
         //retrieve all mutation impacts
        AnnotationSet mutationImpactOKVAnnotationSet = doc.getAnnotations().get("Mutation-ImpactOKV");
        log.debug("mutationImpactOKVAnnotationSet: "+mutationImpactOKVAnnotationSet.size());
        
        log.trace("Size of allMutationImpacts before KV: "+allMutationImpacts.size());
        for(Annotation mutationImpactAnnotation : mutationImpactOKVAnnotationSet) {
        	log.trace("mutantString comes: "+mutationImpactAnnotation.getFeatures().get("mutantString"));
            FeatureMap mutationImpactFeatures = mutationImpactAnnotation.getFeatures();
            	log.trace("mutantString checked: "+mutationImpactAnnotation.getFeatures().get("mutantString"));
                Long[] mutantOffsets = (Long[])mutationImpactFeatures.get("mutant");
                
                String mutantString = mutantOffsets[0].toString()+":"+mutantOffsets[1].toString();
                          
                
                //add to map
                if(mutantImpactMap.get(mutantOffsets)==null) {
                    List mutantList = new ArrayList<Integer>();
                    mutantList.add(mutationImpactAnnotation.getId());
                    log.trace("10 "+mutantString);
                    if(mutantImpactMap.containsKey(mutantString)){
                    	mutantImpactMap.get(mutantString).add(mutationImpactAnnotation.getId());
                    }else{
                    	mutantImpactMap.put(mutantString, mutantList);
                    }
                    
                } else {
                    List mutantList = mutantImpactMap.get(mutantOffsets);
                    mutantList.add(mutationImpactAnnotation.getId());
                    if(mutantImpactMap.containsKey(mutantString)){
                    	mutantImpactMap.get(mutantString).add(mutationImpactAnnotation.getId());
                    }else{
                    	mutantImpactMap.put(mutantString, mutantList);
                    }
                }

                //create a new mutationImpact object
                MutationImpact mutationImpact = new MutationImpact();


                
                //set direction
                if(((String)mutationImpactFeatures.get("direction")).equals("Positive")) {
                    mutationImpact.setDirection(ImpactDirection.POSITIVE);
                }
                else if(((String)mutationImpactFeatures.get("direction")).equals("Negative")) {
                    mutationImpact.setDirection(ImpactDirection.NEGATIVE);
                }
                else if(((String)mutationImpactFeatures.get("direction")).equals("Neutral")) {
                    mutationImpact.setDirection(ImpactDirection.NEUTRAL);
                }
                mutationImpact.setAffectedProteinProperty(new URI(mutationOntologyURI+"#"+(String)mutationImpactFeatures.get("affectProperty")));
                allMutationImpacts.add(mutationImpact);
        }
        log.trace("Size of allMutationImpacts after KV: "+allMutationImpacts.size());
        
        // debug
        log.info("final mutantImpactMap: "+mutantImpactMap);        
        //for(String key : mutantImpactMap.keySet()) 	log.info(key+": "+mutantImpactMap.get(key));
        log.info("final mutationImpacts: "+allMutationImpacts);
       // for(MutationImpact mi : allMutationImpacts)log.info(mi.toString());

        /*
         *  MUTANTS
         *
         */
        System.err.println("=====================================================================\n" +
        		"MUTANTS" +
        		"\n==================================================================");
        Set<GroundedMutation> groundedMutations = new HashSet<GroundedMutation>();
        //Set<GroundedPointMutation> allGroundedPointMutations = new HashSet<GroundedPointMutation>();

        System.err.println(Utils.mapAsPrettyString(mutantImpactMap));
        
        //iterate through mutants
        AnnotationSet proteinMutants = doc.getAnnotations().get("ProteinMutant");
        for(Annotation protein_mutant : proteinMutants) {
        	
        	log.debug("mutantId: "+protein_mutant.getId());
        	

            
            List<Integer> relatedImpactIds = mutantImpactMap.get(protein_mutant.getStartNode().getOffset().toString()+":"+protein_mutant.getEndNode().getOffset().toString());
            
            
            //if not removed as a result of contradiction
            if(relatedImpactIds!=null) {
                for(Integer relatedImpactId : relatedImpactIds) {
                	log.debug("relatedImpactId: "+relatedImpactId);
                	
                	
                	
                    //create a grounded mutation object
                    GroundedMutation groundedMutation = new GroundedMutation();

                    FeatureMap mutantFeatures = protein_mutant.getFeatures();
                    String swissProtId = (String)mutantFeatures.get("mutatesProteinWithSwissProtId");
                    String wtSequence = (String)mutantFeatures.get("mutatesSequence");
                    String mSequence = (String)mutantFeatures.get("hasSequence");

                    String sDirection1 = (String)mutantFeatures.get("StabilityDirectionRule1"); //a
                    String sDirection2 = (String)mutantFeatures.get("StabilityDir"); //a

                    //create the wildtype protein
                    // Artjom: temporary
                    String[] swissProtIdSplit = swissProtId.split("__");
                    String[] swissProtIdSplit2 = swissProtIdSplit[0].split("_");
                    Protein protein = new Protein(swissProtIdSplit2[0],wtSequence);
                    StabilityDirection sDirection = new StabilityDirection(); //a
                    StabilityDirection sDirection3 = new StabilityDirection(sDirection2); //a
                    groundedMutation.setProteinAppliedTo(protein);
                    groundedMutation.setStabilityDirection(sDirection); //a
                    groundedMutation.setStabilityDirection(sDirection3); //a


                    //create the resulting protein mutant
                    ProteinMutant proteinMutant = new ProteinMutant(mSequence, true);
                    groundedMutation.setResultingProteinMutant(proteinMutant);

                    //create all involved point mutations
                    Set<GroundedPointMutation> groundedPointMutations = new HashSet<GroundedPointMutation>();
                    Annotation pointMutationAnn = doc.getAnnotations().get("GroundedPointMutation", protein_mutant.getStartNode().getOffset(),protein_mutant.getEndNode().getOffset()).iterator().next();
                    FeatureMap pointMutationFeatures = pointMutationAnn.getFeatures();
                    
                    GroundedPointMutation groundedPointMutation = new GroundedPointMutation();
                    groundedPointMutation.setCorrectPosition((Integer)pointMutationFeatures.get("hasCorrectPosition"));
                    groundedPointMutation.setMentionedPosition((Integer)pointMutationFeatures.get("hasMentionedPosition"));
                    groundedPointMutation.setMutantResidue(((String)pointMutationFeatures.get("hasMutantResidue")).charAt(0));
                    groundedPointMutation.setWildtypeResidue(((String)pointMutationFeatures.get("hasWildtypeResidue")).charAt(0));
                    //groundedPointMutation.setSwissProtId((String)pointMutationFeatures.get("isGroundedTo"));
                    groundedPointMutation.setSwissProtId((String)pointMutationFeatures.get("isGroundedTo")); // for multiple UniProtIds
                    //gm.setSwissProtId((List<String[]>)features.get("isGroundedTo"));
                    
                    groundedPointMutations.add(groundedPointMutation);
                    //allGroundedPointMutations.addAll(groundedPointMutations);
                    groundedMutation.setCompoundMutations(groundedPointMutations);

                    //retrieve related impacts
                    Set<MutationImpact> relatedMutationImpacts = new HashSet<MutationImpact>();

                    log.debug("====> "+protein_mutant.getStartNode().getOffset().toString()+":"+protein_mutant.getEndNode().getOffset().toString());
                    for(String m : mutantImpactMap.keySet()){
                    	log.debug(m+" : ["+mutantImpactMap.get(m)+"]");
                    }
                	
                	
                    Annotation relatedImpactMention = doc.getAnnotations().get(relatedImpactId);
                    FeatureMap relatedImpactMentionFeatures = relatedImpactMention.getFeatures();

                    //create a new mutationImpact object
                    MutationImpact mutationImpact = new MutationImpact();

                    //set direction
                    if(((String)relatedImpactMentionFeatures.get("direction")).equals("Positive")) {
                        mutationImpact.setDirection(ImpactDirection.POSITIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures.get("direction")).equals("Negative")) {
                        mutationImpact.setDirection(ImpactDirection.NEGATIVE);
                    }
                    else if(((String)relatedImpactMentionFeatures.get("direction")).equals("Neutral")) {
                        mutationImpact.setDirection(ImpactDirection.NEUTRAL);
                    }
                    mutationImpact.setAffectedProteinProperty(new URI("http://purl.org/obo/owl/GO#"+(String)relatedImpactMentionFeatures.get("affectProperty")));
                    log.debug("O1");
                    log.debug(mutationImpact.getAffectedProteinProperty());
                    relatedMutationImpacts.add(mutationImpact);
                
                
                    
                  //THIS MAKES SURE THAT NO MUTANT IS EXPORTED WHEN CONDRACTIONARY
                    //TOWARDS OTHER HIGHER SCORED IMPACT!
                    groundedMutation.setMutationImpacts(relatedMutationImpacts);                 
                    
                    
                    
                    groundedMutations.add(groundedMutation);
                    
                    
                    
                    if(this.getContextOfPositive().containsKey(groundedMutation)){
                    	this.getContextOfPositive().get(groundedMutation).add(doc.getContent()
                        		.getContent(
                        				relatedImpactMention
                        				.getStartNode()
                        				.getOffset(), relatedImpactMention.getEndNode().getOffset()).toString());
                    }else{
                    	List<String> list = new ArrayList<String>();
                    	list.add(doc.getContent()
                        		.getContent(
                        				relatedImpactMention
                        				.getStartNode()
                        				.getOffset(), relatedImpactMention.getEndNode().getOffset()).toString());
                    	this.getContextOfPositive().put(groundedMutation,list);
                    }
                    
                
                }//for
                //log.debug("Size of relatedMutationImpacts: "+relatedMutationImpacts.size());
                
            }//if
        }
        
        
        
         /*
          *  OUTPUT
          */      
       

        //add to document information object
        this.setAllGroundedMutations(groundedMutations);

        //ERROR FOUND BELOW! should be gmResult instead!
        //documentInformation.setAllGroundedPointMutations(allGroundedPointMutations);
        this.setAllGroundedPointMutations(gmResult);
        log.debug("gmResult.size(): "+gmResult.size());

        this.setAllImpacts(allMutationImpacts);

        

        log.trace("finished.");

	}
    
    
    
    
        
    
    
    
    
    
	public Map<GroundedMutation,List<String>> getContextOfPositive() {
		return this.contextOfPositive;
	}
    
    public void addGrounedMutation(GroundedMutation gm){
    	if(groundedMutations == null){
    		groundedMutations = new HashSet<GroundedMutation>();
    	}
    	groundedMutations.add(gm);      	
    }
    
    public void addGroundedPointMutation(GroundedPointMutation gpm){
    	if(groundedPointMutations == null){
    		groundedPointMutations = new HashSet<GroundedPointMutation>();
    	}
    	groundedPointMutations.add(gpm);      	
    }
    
    public Set<GroundedMutation> getAllGroundedMutations(){
    	return groundedMutations;
    }
    
    public Iterator<MutationImpact> getAllImpactsIterator() {
        return mutationImpacts.iterator();
    }

    // public Iterator<StabilityDirection> getAllDirectionsIterator() {
    //     return stabilityDirections.iterator();
    // }

    public Iterator<GroundedMutation> getAllGroundedMutationsIterator() {
        return groundedMutations.iterator();
    }

    public Iterator<GroundedPointMutation> getAllGroundedPointMutationsIterator() {
        return groundedPointMutations.iterator();
    }

    public void setAllGroundedMutations(Set<GroundedMutation> groundedMutations) {
        this.groundedMutations = groundedMutations;
    }

    public void setAllGroundedPointMutations(Set<GroundedPointMutation> groundedPointMutations) {
        this.groundedPointMutations = groundedPointMutations;
    }

    public void setAllImpacts(Set<MutationImpact> mutationImpacts) {
        this.mutationImpacts = mutationImpacts;
    }

    // public void setAllDirections(Set<StabilityDirection> stabilityDirections) {
    //     this.stabilityDirections = stabilityDirections;
    // }
    
    public String [][] resultsAsTable(){
    	int numberOfRows = this.groundedPointMutations.size();
    	List<GroundedPointMutation> list = new ArrayList<GroundedPointMutation>(this.groundedPointMutations);
    	String[][] table = new String[numberOfRows][2];
    	for(int i = 0; i<this.groundedPointMutations.size(); i++){
    		GroundedPointMutation gpm = list.get(i);
    		table[i][0] = gpm.getNormalizedFormMentioned();
    		table[i][1] = gpm.getSwissProtId().keySet().toString();
    	}
    	return table;
    }
    public String resultsAsPrettyString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("\n\n-----------------------------------------------------\n" +
    			"Results: \n" +
    			"----------------------------------------------------- \n");  
    	if(groundedPointMutations!=null && groundedPointMutations.size()>0){
        	for(GroundedPointMutation gpm : this.groundedPointMutations){
        		sb.append(gpm.getNormalizedFormMentioned() + ": " + gpm.getSwissProtId()+"\n");
        	}        	
    	}else{
    		log.info("No results of Mutation Grounding.");
    	}

    	
    	if(groundedMutations!=null && groundedMutations.size()>0){
        	Map<String,GroundedMutation> sortedGroundedMutations = new TreeMap<String,GroundedMutation>();
    		for(GroundedMutation gm : groundedMutations){
        		String mutation = gm.getCompoundMutation().next().getNormalizedFormMentioned();
        		MutationImpact impact = gm.getMutationImpacts().next();
//                StabilityDirection stability = gm.getStabilityDirection().next();
        		String property = impact.getAffectedProteinProperty().toString();
        		String direction = impact.getDirection();
//                String sDirection = stability.getStabilityDirection();
        		if(direction==null)direction = "";
//                if(sDirection==null)sDirection = "";
        		sortedGroundedMutations.put(mutation+property+direction/*+sDirection*/, gm);
        	}   
        	for(String key : sortedGroundedMutations.keySet()){
        		sb.append(sortedGroundedMutations.get(key).toPrettyString());
        		sb.append(this.getContextOfPositive().get(sortedGroundedMutations.get(key))+"\n");
        	}
    	}else{
    		log.info("No results of Mutation Impact Extraction.");
    	}
    	
    	
    	/*for(GroundedMutation gm : groundedMutations){
    		String mutation = gm.getGrouindedPointMutations().next().getNormalizedFormMentioned();
    		if(sortedGroundedMutations.containsKey(mutation)){
    			sortedGroundedMutations.get(mutation).add(gm);
    		}else{
    			List<GroundedMutation> list = new ArrayList();
    			list.add(gm);
        		sortedGroundedMutations.put(mutation, list);
    		}
    	}   
    	for(String key : sortedGroundedMutations.keySet()){
    		for(GroundedMutation gm : sortedGroundedMutations.get(key)){
        		sb.append(gm.toPrettyString());
    		}
    	}*/
    	return sb.toString();
    }

	public Set<GroundedPointMutation> getAllGroundedPointMutations() {
		return groundedPointMutations;
	}

/*
	public void initDocumentInfoStatistics() {
		if(this.statistics!=null)return;
		this.statistics = new DocumentInfoStatistics();
		statistics.setDocumentInformation(this);
	}*/

	public String getName() {		
		return this.name;
	}
/*
	public DocumentInfoStatistics getDocumentInfoStatistics() {
		return this.statistics;
	}*/
	
	private static String[] parseWNMFormat(String mutationString){
		String wtResidue = mutationString.substring(0,1);
		String mResidue = mutationString.substring(mutationString.length()-1,mutationString.length());
		String position = mutationString.substring(1,mutationString.length()-1);
		String[] split = {wtResidue,position,mResidue};	
		return split;
	}

	

}

/**
 * Comapare entities by discourseId
 */
class DocumentInformationComparatorByName implements Comparator<DocumentInformation> {
	@Override
	public int compare(DocumentInformation o1, DocumentInformation o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
