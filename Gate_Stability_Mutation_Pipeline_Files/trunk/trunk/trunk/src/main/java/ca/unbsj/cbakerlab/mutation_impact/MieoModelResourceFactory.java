package ca.unbsj.cbakerlab.mutation_impact;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class MieoModelResourceFactory {
	private static final Logger log = Logger.getLogger(MieoModelResourceFactory.class);
	
	private static final String UNIPROT_URL = "http://www.uniprot.org/uniprot/";

	
	
	/**
	 * Vocabulary of amino acids resources.
	 */
	private static Map<String,Resource> aminoAcidVocab = new HashMap<String,Resource>(){{
			put("A", Vocab.Alanine);
			put("R", Vocab.Arginine);
			put("N", Vocab.Asparagine);
			put("D", Vocab.Aspartic_acid);
			put("C", Vocab.Cysteine);
			put("E", Vocab.Glutamic_acid);
			put("Q", Vocab.Glutamine);
			put("G", Vocab.Glycine);
			put("H", Vocab.Histidine);
			put("I", Vocab.Isoleucine);
			put("L", Vocab.Leucine);
			put("K", Vocab.Lysine);
			put("M", Vocab.Methionine);
			put("F", Vocab.Phenylalanine);
			put("P", Vocab.Proline);
			put("S", Vocab.Serine);
			put("T", Vocab.Threonine);
			put("W", Vocab.Tryptophan);
			put("Y", Vocab.Tyrosine);
			put("V", Vocab.Valine);
			put("X", Vocab.Unknown);
			put("U", Vocab.Selenocysteine);
			put("O", Vocab.Pyrrolysine);
			}};
	/**
	 * Vocabulary of amino acids resources.
	 */
	private static Map<Resource,String> aminoAcidInvertVocab = new HashMap<Resource,String>() {
		{
			put(Vocab.Alanine,"A");
			put(Vocab.Arginine,"R");
			put(Vocab.Asparagine,"N");
			put(Vocab.Aspartic_acid,"D");
			put(Vocab.Cysteine,"C");
			put(Vocab.Glutamic_acid,"E");
			put(Vocab.Glutamine,"Q");
			put(Vocab.Glycine,"G");
			put(Vocab.Histidine,"H");
			put(Vocab.Isoleucine,"I");
			put(Vocab.Leucine,"L");
			put(Vocab.Lysine,"K");
			put(Vocab.Methionine,"M");
			put(Vocab.Phenylalanine,"F");
			put(Vocab.Proline,"P");
			put(Vocab.Serine,"S");
			put(Vocab.Threonine,"T");
			put(Vocab.Tryptophan,"W");
			put(Vocab.Tyrosine,"Y");
			put(Vocab.Valine,"V");
			put(Vocab.Unknown,"X");
			put(Vocab.Selenocysteine,"U");
			put(Vocab.Pyrrolysine,"O");
		}
	};

	
	
	public static Resource createMutationApplicationResource(Resource mutation, Resource protein, Model model, boolean useUriSchema) {
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilderMutApplication = UriBuilder.fromUri(Vocab.ProteinMutationApplication.toString());
			uriBuilderMutApplication.queryParam("protein", protein.getURI());
			uriBuilderMutApplication.queryParam("mutation", mutation.getURI());
			URI uriMutApplication = uriBuilderMutApplication.build();
			uriString = uriMutApplication.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.ProteinMutationApplication);
		}
		Resource mutation_application = model.createResource(uriString);
		mutation_application.addProperty(Vocab.type, Vocab.ProteinMutationApplication);
		mutation_application.addProperty(Vocab.isApplicationOfMutation, mutation);
		mutation_application.addProperty(Vocab.isApplicationOfMutationToProtein, protein);
		return mutation_application;
	}
	
	
	public static Resource createProteinVariantResource(String id, Model model, boolean useUriSchema) {
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilderProtein = UriBuilder.fromUri(Vocab.ProteinVariant.toString());
			uriBuilderProtein.queryParam("id", id);
			URI uriProtein = uriBuilderProtein.build();
			uriString = uriProtein.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.ProteinVariant);
		}		
		Resource protein = model.createResource(uriString);
		protein.addProperty(Vocab.type, Vocab.ProteinVariant);
		return protein;
	}	
	
	
	public static Resource createSioArticleResource(String documentId, Model model, boolean useUriSchema){
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilderDocument = UriBuilder.fromUri(Vocab.articleSio.toString());
			uriBuilderDocument.queryParam("article_id", documentId);
			URI uriDocument = uriBuilderDocument.build();
			uriString = uriDocument.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.articleSio);
		}			
		Resource article = model.createResource(uriString);
		article.addProperty(Vocab.type, Vocab.articleSio);
		return article;
	}
	
	
	public static Resource createCombinedMutationResource(List<Resource> memberMutations, Model model, boolean useUriSchema) {
		String uriString = null;
		// New uri for combined_mutation: is build from uris of member mutations.
		if(useUriSchema){
			uriString = buildCombinedMutationURI(memberMutations).toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.CombinedAminoAcidSequenceChange);
		}
		Resource combined_mutation = model.createResource(uriString);
		combined_mutation.addProperty(Vocab.type, Vocab.CombinedAminoAcidSequenceChange);


		// Create Identifier resource.
		Resource comb_mutation_identifier = createCombinedMutationIdentifierResource(memberMutations, model);
		combined_mutation.addProperty(Vocab.hasUniqueIdentifier, comb_mutation_identifier);

		/////System.out.println("comb_mutation_identifier: "+comb_mutation_identifier);
		//System.out.println("comb_mutation_identifier: "+comb_mutation_identifier);

		// Attach members to combined_mutation.
		for (Resource member : memberMutations) {
			combined_mutation.addProperty(Vocab.hasMember, member);
		}


		// Create Count resource.
		UriBuilder uriBuilder = UriBuilder.fromUri(Vocab.countSio.toString());
		uriBuilder.queryParam("value", "" + memberMutations.size());
		URI uri2 = uriBuilder.build();
		Resource countAttr = model.createResource(uri2.toString());
		countAttr.addLiteral(Vocab.hasValue, model.createTypedLiteral(memberMutations.size()));
		countAttr.addProperty(Vocab.type, Vocab.countSio);
		combined_mutation.addProperty(Vocab.hasAttribute, countAttr);

		return combined_mutation;
	}
	
	
	static Resource createCombinedMutationIdentifierResource(List<Resource> memberMutations, Model model) {
		List<String> mutations = new ArrayList<String>();
		for(Resource member : memberMutations){
			Resource p = member.getPropertyResourceValue(Vocab.mutationHasPosition);			
			mutations.add(
					aminoAcidInvertVocab.get(member.getPropertyResourceValue(Vocab.mutationHasWildtypeResidue))
					+ Integer.toString(p.getProperty(Vocab.hasValue).getObject().asLiteral().getInt())
					+ aminoAcidInvertVocab.get(member.getPropertyResourceValue(Vocab.mutationHasMutantResidue)));			
		}
		Collections.sort(mutations);
		String id = StringUtils.collectionToDelimitedString(mutations, "_");
		
		UriBuilder uriBuilderCombMutationIdentifier = UriBuilder.fromUri(Vocab.CombinedAminoAcidSequenceChange_Identifier.toString());
		uriBuilderCombMutationIdentifier.queryParam("id", id);
		URI uriCombMutationIdentifier = uriBuilderCombMutationIdentifier.build();
		Resource comb_mutation_identifier = model.createResource(uriCombMutationIdentifier.toString());		
		comb_mutation_identifier.addProperty(Vocab.type, Vocab.CombinedAminoAcidSequenceChange_Identifier);		
		comb_mutation_identifier.addProperty(Vocab.hasValue,id);
			
		return comb_mutation_identifier;
	}

	
	
	
	
	private static URI buildCombinedMutationURI(List<Resource> elements) {

		TreeSet<String> sortedSingularMutationURIs = new TreeSet<String>();
		for (Resource r : elements) {
			sortedSingularMutationURIs.add(r.getURI().toString());
		}
		
		UriBuilder uriBuilder = UriBuilder.fromUri(Vocab.CombinedAminoAcidSequenceChange.toString());
		for (String singMutURI : sortedSingularMutationURIs) {
			uriBuilder.queryParam("member", singMutURI);
		}
		URI uri = uriBuilder.build();
		return uri;

	}

	
	public static Resource createSingularPointMutationResource(char wtResidue, char mResidue, int position, Model model, boolean useUriSchema){
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilderSPM = UriBuilder.fromUri(Vocab.AminoAcidSubstitution.toString());
			uriBuilderSPM.queryParam("annotation", Character.toString(wtResidue) + Integer.toString(position) + Character.toString(mResidue));
			URI uriSPM = uriBuilderSPM.build();
			uriString = uriSPM.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.AminoAcidSubstitution);
		}

		Resource singular_mutation = model.createResource(uriString);		
		singular_mutation.addProperty(Vocab.type, Vocab.AminoAcidSubstitution);

		Resource wResidue = aminoAcidVocab.get(Character.toString(wtResidue));
		Resource mutResidue = aminoAcidVocab.get(Character.toString(mResidue));		
		
		String uriString2 = null;
		if(useUriSchema){
			UriBuilder uriBuilderPosition = UriBuilder.fromUri(Vocab.positionSio.toString());
			uriBuilderPosition.queryParam("value","" + Integer.toString(position));
		    URI uriPosition = uriBuilderPosition.build();
		    uriString2 = uriPosition.toString();
		}else{
			uriString2 = Utils.createTimestampedUri(Vocab.positionSio);
		}		
	    Resource positionResource = model.createResource(uriString2);
		positionResource.addProperty(Vocab.type, Vocab.positionSio);

		if(wResidue != null) singular_mutation.addProperty(Vocab.mutationHasWildtypeResidue, wResidue);
		if(mutResidue != null) singular_mutation.addProperty(Vocab.mutationHasMutantResidue, mutResidue);
		if(positionResource != null) {
			singular_mutation.addProperty(Vocab.mutationHasPosition, positionResource);
			//positionResource.addProperty(Vocab.hasValue, Integer.toString(position), XSDDatatype.XSDinteger);// sio:'has			
			positionResource.addLiteral(Vocab.hasValue, model.createTypedLiteral(position));
		}

		return singular_mutation;
	}
	
	
	
	public static Resource createSingularPointMutationDenotationResource(char wtResidue, char mResidue, int position, Model model){
		UriBuilder uriBuilderSPMDenotation = UriBuilder.fromUri(Vocab.SingularMutationDenotation_OneLetterFormat.toString());
		uriBuilderSPMDenotation.queryParam("annotation", Character.toString(wtResidue) + Integer.toString(position) + Character.toString(mResidue));
		URI uriSPMDenotation = uriBuilderSPMDenotation.build();
		Resource singular_mutation_denotation = model.createResource(uriSPMDenotation.toString());
		singular_mutation_denotation.addProperty(Vocab.type, Vocab.SingularMutationDenotation_OneLetterFormat);
		singular_mutation_denotation.addProperty(Vocab.hasValue, wtResidue + Integer.toString(position) + mResidue);
		return singular_mutation_denotation;
	}

		
	//
	//
	// PROTEIN PROPERTY CHANGE.
	//
	//
	public static Resource createProteinPropertyChangeResource(Resource propertyResource, Resource propChangeClass, Model model, boolean useUriSchema) {		
		//Resource propertyResource = createProteinPropertyResource(propertyId, protein, model);
		//Resource propChangeClass = proteinPropertyChangeClassResource(directionId, model);
		
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilder = UriBuilder.fromUri(propChangeClass.toString());
			uriBuilder.queryParam("property", propertyResource.toString());
			URI uri = uriBuilder.build();
			uriString = uri.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.ProteinPropertyChange);
		}		
		
		Resource result = model.createResource(uriString);
		result.addProperty(Vocab.type, propChangeClass);
		//result.addProperty(Vocab.type, Vocab.ProteinPropertyChange);
		result.addProperty(Vocab.propertyChangeAppliesTo, propertyResource);
		return result;
	}
		
	public static Resource createProteinPropertyChangeResource(Model model) {		
		Resource propChangeSuperClass = model.createResource(Vocab.ProteinPropertyChange.toString());
		Resource propChangeClass = Utils.createTimestampedInstance(Vocab.Class, model);
		propChangeClass.addProperty(Vocab.subClassOf, propChangeSuperClass);		
		Resource result = Utils.createTimestampedInstance(Vocab.ProteinPropertyChange, model);
		result.addProperty(Vocab.type, propChangeClass);
		result.addProperty(Vocab.type, Vocab.ProteinPropertyChange);
		return result;
	}

	/*
	 * 	public static Resource createProteinPropertyChangeResource(String propertyId, String directionId, Resource protein, Model model) throws URISyntaxException {
		
		Resource propertyResource = createProteinPropertyResource(propertyId, protein, model);

		Resource propChangeClass = proteinPropertyChangeClassResource(directionId, model);

		UriBuilder uriBuilder = UriBuilder.fromUri(propChangeClass.toString());
		uriBuilder.queryParam("property", propertyResource.toString());
		URI uri = uriBuilder.build();

		Resource result = model.createResource(uri.toString());
		result.addProperty(Vocab.type, propChangeClass);
		result.addProperty(Vocab.propertyChangeAppliesTo, propertyResource);

		return result;
	}
	 */
	public static Resource proteinPropertyChangeClassResource(String directionId, Model model) {

		Resource result;
		if (directionId != null && (directionId.equals("[+]") || directionId.equals("+") || directionId.equalsIgnoreCase("positive"))) {
			result = model.createResource(Vocab.PositiveProteinPropertyChange.toString());
			result.addProperty(Vocab.subClassOf, Vocab.ProteinPropertyChange);
		} else if (directionId != null && (directionId.equals("[-]") || directionId.equals("-") || directionId.equalsIgnoreCase("negative"))) {
			result = model.createResource(Vocab.NegativeProteinPropertyChange.toString());
			result.addProperty(Vocab.subClassOf, Vocab.ProteinPropertyChange);
		} else if (directionId != null && (directionId.equals("[O]") || directionId.equals("O") || directionId.equalsIgnoreCase("neutral"))) {
			result = model.createResource(Vocab.NeutralProteinPropertyChange.toString());
			result.addProperty(Vocab.subClassOf, Vocab.ProteinPropertyChange);
		} else {
			String uri = Utils.createTimestampedUri(Vocab.ProteinPropertyChange);
			result = model.createResource(uri);
			result.addProperty(Vocab.subClassOf, Vocab.ProteinPropertyChange);

			//log.fatal("Internal Mutation Impact pipeline error: an impact that is neither positive, nor negative, nor neutral.");
			//throw new IllegalArgumentException("An impact that is neither positive, nor negative, nor neutral.");
		}		
		result.addProperty(Vocab.label, result.getLocalName());		
		
		return result;

	}

	
		
	
	//
	//
	// PROTEIN PROPERTY.
	//
	//
	public static Resource createProteinPropertyResource(Resource proteinPropertyClass, Resource protein, Model model, boolean useUriSchema) {
		//Resource propertyClassResource = model.createResource(Vocab.goNS + proteinPropertyId);
		
		//proteinPropertyClass.addProperty(Vocab.subClassOf, Vocab.ProteinProperty); // optional.
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilder = UriBuilder.fromUri(proteinPropertyClass.getURI());
			uriBuilder.queryParam("protein", protein.getURI());
			URI uri = uriBuilder.build();
			uriString = uri.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.ProteinProperty);
		}	
		
		Resource result = model.createResource(uriString);
		result.addProperty(Vocab.type, proteinPropertyClass);
		//result.addProperty(Vocab.type, Vocab.ProteinProperty);
		result.addProperty(Vocab.isPropertyOf, protein);
		return result;
	}

	public static Resource createProteinPropertyResourceByProteinPropertyClass1(Resource proteinPropertyClass, Model model) {
		proteinPropertyClass.addProperty(Vocab.subClassOf, Vocab.ProteinProperty);		// optional.
		Resource result = Utils.createTimestampedInstance(proteinPropertyClass, model);
		result.addProperty(Vocab.type, Vocab.ProteinProperty);
		return result;
	}
	
	public static Resource createProteinPropertyResourceByProtein(Resource protein, Model model, boolean useUriSchema) {
		Resource propertyClassResource = Utils.createTimestampedInstance(Vocab.Class, model);
		propertyClassResource.addProperty(Vocab.subClassOf, Vocab.ProteinProperty);
		
		String uriString = null;
		if(useUriSchema){
			UriBuilder uriBuilder = UriBuilder.fromUri(propertyClassResource.toString());
			uriBuilder.queryParam("protein", protein.toString());
			URI uri = uriBuilder.build();
			uriString = uri.toString();
		}else{
			uriString = Utils.createTimestampedUri(Vocab.ProteinProperty);
		}		
		Resource result = model.createResource(uriString);
		result.addProperty(Vocab.type, propertyClassResource);
		//result.addProperty(Vocab.type, Vocab.ProteinProperty);
		result.addProperty(Vocab.isPropertyOf, protein);
		return result;
	}
	
	public static Resource createProteinPropertyResource1(Model model) {
		Resource propertySuperClass = model.createResource(Vocab.ProteinProperty.toString());
		Resource propertyClass = Utils.createTimestampedInstance(Vocab.Class, model);
		propertyClass.addProperty(Vocab.subClassOf, propertySuperClass);		
		Resource result = Utils.createTimestampedInstance(propertyClass, model);
		result.addProperty(Vocab.type, Vocab.ProteinProperty);
		return result;
	}
	
	public static Resource createProteinPropertyResource(Model model) {
		//Resource propertySuperClass = model.createResource(Vocab.ProteinProperty.toString());
		//Resource propertyClass = Utils.createTimestampedInstance(Vocab.Class, model);
		//propertyClass.addProperty(Vocab.subClassOf, propertySuperClass);		
		Resource result = model.createResource(Utils.createTimestampedUri(Vocab.ProteinProperty));
		result.addProperty(Vocab.type, Vocab.ProteinProperty);
		return result;
	}
	
	/*
	 * 	public static Resource createProteinPropertyResource(String proteinPropertyId, Model model) {
		Resource propertyClassResource = model.createResource(Vocab.goNS + proteinPropertyId);
		propertyClassResource.addProperty(Vocab.label, proteinPropertyId);
		propertyClassResource.addProperty(Vocab.subClassOf, Vocab.ProteinProperty);		
		Resource result = Utils.createTimestampedInstance(propertyClassResource, model);
		return result;
	}
	 */
			
	/*
	 * 
	public static Resource createProteinPropertyResource(String proteinPropertyId, Resource protein, Model model) {
		Resource propertyClassResource = model.createResource(Vocab.goNS + proteinPropertyId);
		propertyClassResource.addProperty(Vocab.label, proteinPropertyId);
		propertyClassResource.addProperty(Vocab.subClassOf, Vocab.ProteinProperty);
		UriBuilder uriBuilder = UriBuilder.fromUri(propertyClassResource.toString());
		uriBuilder.queryParam("protein", protein.toString());
		URI uri = uriBuilder.build();
		Resource result = model.createResource(uri.toString());
		result.addProperty(Vocab.type, propertyClassResource);
		result.addProperty(Vocab.isPropertyOf, protein);
		return result;
	}
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	public static Resource createStringResource(String text, Resource document, List<Resource> referents, Model model){
		Resource string = Utils.createTimestampedInstance(Vocab.stringStr, model);
		//string.addProperty(Vocab.type,Vocab.strString);
		string.addProperty(Vocab.anchorOfStr,text);
		string.addProperty(Vocab.occurInMieo,document);
		for(Resource referent : referents){
			string.addProperty(Vocab.refersToMieo,referent);	
			referent.addProperty(Vocab.referedByMieo,string);
		}
		return string;
	}

	//
	//
	// Records and Identifiers.
	//
	//
	/*
	public static Resource createRecordResource(Resource klass, String id, Model model){		 
		UriBuilder uriBuilderGoRecord = UriBuilder.fromUri(Vocab.Go_Record.toString());		
		
		Resource record = model.createResource(klass);
		record.addProperty(Vocab.type, klass);	
			
		Resource go_identifier = createIdentifierResource(id, model);
		record.addProperty(Vocab.hasAttribute, go_identifier);
		
		return record;
	}
	
	private static Resource createIdentifierResource(String uniprotId, Model model){
		UriBuilder uriBuilderUniProtIdentifier = UriBuilder.fromUri(Vocab.Go_Identifier.toString());
		uriBuilderUniProtIdentifier.queryParam("go_id", uniprotId);
		URI uriUniProtIdentifier = uriBuilderUniProtIdentifier.build();
		Resource uniprot_identifier = model.createResource(uriUniProtIdentifier.toString());
		uniprot_identifier.addProperty(Vocab.type, Vocab.Go_Identifier);

		uniprot_identifier.addProperty(Vocab.hasValue, uniprotId);
		return uniprot_identifier;
	}
	*/
	
	public static Resource createUniProtRecordResource(String uniprotId, Model model){		 
		UriBuilder uriBuilderUniProtRecord = UriBuilder.fromUri(Vocab.UniProt_Record.toString());
		uriBuilderUniProtRecord.queryParam("uniprot_id", uniprotId);
		URI uriUniProtRecord = uriBuilderUniProtRecord.build();
		Resource uniprot_record = model.createResource(uriUniProtRecord.toString());
		uniprot_record.addProperty(Vocab.type, Vocab.UniProt_Record);	
			
		Resource uniprot_identifier = createUniProtIdentifierResource(uniprotId, model);
		uniprot_record.addProperty(Vocab.hasAttribute, uniprot_identifier);
		
		return uniprot_record;
	}
	
	private static Resource createUniProtIdentifierResource(String uniprotId, Model model){
		UriBuilder uriBuilderUniProtIdentifier = UriBuilder.fromUri(Vocab.UniProt_Identifier.toString());
		uriBuilderUniProtIdentifier.queryParam("uniprot_id", uniprotId);
		URI uriUniProtIdentifier = uriBuilderUniProtIdentifier.build();
		Resource uniprot_identifier = model.createResource(uriUniProtIdentifier.toString());
		uniprot_identifier.addProperty(Vocab.type, Vocab.UniProt_Identifier);

		uniprot_identifier.addProperty(Vocab.hasValue, uniprotId);
		return uniprot_identifier;
	}
	
	
	
	
	public static Resource createEcRecordResource(String ecNumber, Model model){
		 
		UriBuilder uriBuilderEcRecord = UriBuilder.fromUri(Vocab.Ec_Record.toString());
		uriBuilderEcRecord.queryParam("ec_number", ecNumber);
		URI uriEcRecord = uriBuilderEcRecord.build();
		Resource ec_record = model.createResource(uriEcRecord.toString());
		ec_record.addProperty(Vocab.type, Vocab.Ec_Record);	
			
		Resource ec_identifier = createEcIdentifierResource(ecNumber, model);
		ec_record.addProperty(Vocab.hasAttribute, ec_identifier);
		
		return ec_record;
	}
	
	private static Resource createEcIdentifierResource(String ecNumber, Model model){
		UriBuilder uriBuilderEcIdentifier = UriBuilder.fromUri(Vocab.Ec_Identifier.toString());
		uriBuilderEcIdentifier.queryParam("ec_number", ecNumber);
		URI uriEcIdentifier = uriBuilderEcIdentifier.build();
		Resource ec_identifier = model.createResource(uriEcIdentifier.toString());
		ec_identifier.addProperty(Vocab.type, Vocab.Ec_Identifier);

		ec_identifier.addProperty(Vocab.hasValue, ecNumber);
		return ec_identifier;
	}
	
	
	public static Resource createPubMedIdentifierResource(String documentId, Model model){
	    UriBuilder uriBuilderPubMedIdentifier = UriBuilder.fromUri(Vocab.PMID_Identifier.toString());						    
	    uriBuilderPubMedIdentifier.queryParam("pubmed_id",documentId);
		URI uriPubMedIdentifier = uriBuilderPubMedIdentifier.build();
		Resource pubmed_identifier = model.createResource(uriPubMedIdentifier.toString());
		pubmed_identifier.addProperty(Vocab.type,Vocab.PMID_Identifier);
		pubmed_identifier.addProperty(Vocab.hasValue,documentId);		 
		return pubmed_identifier;
	}
	
	public static Resource createPubMedRecordResource(String documentId, Model model){
	    UriBuilder uriBuilderPubMedRecord = UriBuilder.fromUri(Vocab.PMID_Record.toString());						    
	    uriBuilderPubMedRecord.queryParam("pubmed_id",documentId);
		URI uriPubMedRecord = uriBuilderPubMedRecord.build();
		Resource pubmed_record = model.createResource(uriPubMedRecord.toString());
		pubmed_record.addProperty(Vocab.type,Vocab.PMID_Record);
	    //
	    // Create PubMed Identifier resource.
	    //
		Resource pubmed_identifier = MieoModelResourceFactory.createPubMedIdentifierResource(documentId, model);
		pubmed_record.addProperty(Vocab.hasAttribute, pubmed_identifier);
		return pubmed_record;
	}
	
	
	public static Resource createGoRecordResource(String goId, Model model){		 
		UriBuilder uriBuilderGoRecord = UriBuilder.fromUri(Vocab.Go_Record.toString());
		uriBuilderGoRecord.queryParam("go_id", goId);
		URI uriGoRecord = uriBuilderGoRecord.build();
		Resource go_record = model.createResource(uriGoRecord.toString());
		go_record.addProperty(Vocab.type, Vocab.Go_Record);	
			
		Resource go_identifier = createGoIdentifierResource(goId, model);
		go_record.addProperty(Vocab.hasAttribute, go_identifier);
		
		return go_record;
	}
	
	private static Resource createGoIdentifierResource(String goId, Model model){
		UriBuilder uriBuilderUniProtIdentifier = UriBuilder.fromUri(Vocab.Go_Identifier.toString());
		uriBuilderUniProtIdentifier.queryParam("go_id", goId);
		URI uriUniProtIdentifier = uriBuilderUniProtIdentifier.build();
		Resource uniprot_identifier = model.createResource(uriUniProtIdentifier.toString());
		uniprot_identifier.addProperty(Vocab.type, Vocab.Go_Identifier);

		uniprot_identifier.addProperty(Vocab.hasValue, goId);
		return uniprot_identifier;
	}


	

}
