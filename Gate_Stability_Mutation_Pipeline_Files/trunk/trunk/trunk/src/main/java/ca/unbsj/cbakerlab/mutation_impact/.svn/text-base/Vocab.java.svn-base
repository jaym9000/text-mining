package ca.unbsj.cbakerlab.mutation_impact;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Vocab
	{
	    private static Model m_model = ModelFactory.createDefaultModel();

	    
	    //
	    // Namespaces.
	    //
	    public static final String mieoNS = "http://cbakerlab.unbsj.ca:8080/ontologies/mutation-impact-extraction-ontology.owl#";	
	    public static final String sioNS = "http://semanticscience.org/resource/";    
		public static final String lsrnNS = "http://purl.oclc.org/SADI/LSRN/";		
		public static final String goNS = "http://purl.org/obo/owl/GO#"; //http://purl.org/obo/owl/GO#GO_0000005			
	    public static final String foafNS = "http://xmlns.com/foaf/0.1/";	    
	    public static final String strNS = "http://nlp2rdf.lod2.eu/schema/string/";	    
	    public static final String aoNS = "http://purl.org/ao/";	    
	    //public static final String aocoreNS = "http://purl.org/ao/core/";	    
	    public static final String aotNS = "http://purl.org/ao/types/";	    
	    public static final String aosNS = "http://purl.org/ao/selectors/";	   
	    public static final String pavNS = "http://purl.org/pav/";	    
	    public static final String aofNS = "http://purl.org/ao/foaf/";	
	    
	    // http://code.google.com/p/semanticscience/source/browse/trunk/ontology/sio-core.owl?r=602
	    
	    //
	    // Mutation Impact Extraction Model Resources.
	    //
		public static final Resource Alanine = m_model.createResource(mieoNS + "Alanine");
		public static final Resource Arginine = m_model.createResource(mieoNS + "Arginine");
		public static final Resource Asparagine = m_model.createResource(mieoNS + "Asparagine");
		public static final Resource Aspartic_acid = m_model.createResource(mieoNS + "Aspartic_acid");
		public static final Resource Cysteine = m_model.createResource(mieoNS + "Cysteine");
		public static final Resource Glutamic_acid = m_model.createResource(mieoNS + "Glutamic_acid");
		public static final Resource Glutamine = m_model.createResource(mieoNS + "Glutamine");
		public static final Resource Glycine = m_model.createResource(mieoNS + "Glycine");
		public static final Resource Histidine = m_model.createResource(mieoNS + "Histidine");
		public static final Resource Isoleucine = m_model.createResource(mieoNS + "Isoleucine");
		public static final Resource Leucine = m_model.createResource(mieoNS + "Leucine");
		public static final Resource Lysine = m_model.createResource(mieoNS + "Lysine");
		public static final Resource Methionine = m_model.createResource(mieoNS + "Methionine");
		public static final Resource Phenylalanine = m_model.createResource(mieoNS + "Phenylalanine");
		public static final Resource Proline = m_model.createResource(mieoNS + "Proline");
		public static final Resource Serine = m_model.createResource(mieoNS + "Serine");
		public static final Resource Threonine = m_model.createResource(mieoNS + "Threonine");
		public static final Resource Tryptophan = m_model.createResource(mieoNS + "Tryptophan");
		public static final Resource Tyrosine = m_model.createResource(mieoNS + "Tyrosine");
		public static final Resource Valine = m_model.createResource(mieoNS + "Valine");
		public static final Resource Unknown = m_model.createResource(mieoNS + "Unknown");
		public static final Resource Selenocysteine = m_model.createResource(mieoNS + "Selenocysteine");
		public static final Resource Pyrrolysine = m_model.createResource(mieoNS + "Pyrrolysine");

	    public static final Resource AminoAcidSequenceChange = 
			m_model.createResource(mieoNS + "AminoAcidSequenceChange");
	    
	    public static final Resource AminoAcidSubstitution = 
			m_model.createResource(mieoNS + "AminoAcidSubstitution");

	    public static final Resource ProteinVariant = 
			m_model.createResource(mieoNS + "ProteinVariant");
	    
	    public static final Resource ProteinMutationApplication = 
			m_model.createResource(mieoNS + "ProteinMutationApplication");
	    
	    public static final Resource CombinedAminoAcidSequenceChange = 
			m_model.createResource(mieoNS + "CombinedAminoAcidSequenceChange");
	    
	    public static final Resource CombinedAminoAcidSequenceChange_Identifier = 
			m_model.createResource(mieoNS + "CombinedAminoAcidSequenceChange_Identifier");
	    
	    public static final Resource SingularMutationDenotation_OneLetterFormat = 
			m_model.createResource(mieoNS + "SingularMutationDenotation_OneLetterFormat");
	    
	    
	    public static final Resource PositiveProteinPropertyChange = 
			m_model.createResource(mieoNS + "PositiveProteinPropertyChange");
	    
	    public static final Resource NegativeProteinPropertyChange = 
			m_model.createResource(mieoNS + "NegativeProteinPropertyChange");
	    
	    public static final Resource NeutralProteinPropertyChange = 
			m_model.createResource(mieoNS + "NeutralProteinPropertyChange");

	    
	    public static final Resource ProteinProperty = m_model.createResource(mieoNS + "ProteinProperty");

	    public static final Resource ProteinPropertyChange = m_model.createResource(mieoNS + "ProteinPropertyChange");	    
		    
		public static final Resource StatementOfMutationEffect = m_model.createResource(mieoNS + "StatementOfMutationEffect");
		
		public static final Resource ProteinPropertyChangeApplication = m_model.createResource(mieoNS + "ProteinPropertyChangeApplication");
	
		public static final Resource StringSimilarity = m_model.createResource(mieoNS + "StringSimilarity");

		//public static final Resource SimilarityScore = m_model.createResource(mieoNS + "SimilarityScore");

	    //
	    // MIE Properties.
	    //
	    public static final Property mutationHasWildtypeResidue = 
			m_model.createProperty(mieoNS + "mutationHasWildtypeResidue");

	    public static final Property mutationHasMutantResidue = 
			m_model.createProperty(mieoNS + "mutationHasMutantResidue");
	    
	    public static final Property mutationHasPosition = 
			m_model.createProperty(mieoNS + "mutationHasPosition");				    
	    
	   public static final Property isApplicationOfMutation = 
		   m_model.createProperty(mieoNS + "isApplicationOfMutation");

	   public static final Property isApplicationOfMutationToProtein = 
		   m_model.createProperty(mieoNS + "isApplicationOfMutationToProtein");
	    
	   public static final Property mutationApplicationCausesChange = 
		   m_model.createProperty(mieoNS + "mutationApplicationCausesChange");
	   
	   public static final Property propertyChangeAppliesTo = 
		   m_model.createProperty(mieoNS + "propertyChangeAppliesTo");
	   
	   public static final Property proteinHasSequence = 
		   m_model.createProperty(mieoNS + "proteinHasSequence");
	   
	   public static final Property PubMedURI = 
		   m_model.createProperty(mieoNS + "PubMedURI");	   
	   
	   public static final Property isApplicationOfProteinPropertyChange = 
		   m_model.createProperty(mieoNS + "isApplicationOfProteinPropertyChange");

	   public static final Property isApplicationOfProteinPropertyChangeToProteinProperty = 
		   m_model.createProperty(mieoNS + "isApplicationOfProteinPropertyChangeToProteinProperty");
	   
		public static final Property refersToMieo = 
			m_model.createProperty(mieoNS + "refersTo");
	
		public static final Property referedByMieo = 
			m_model.createProperty(mieoNS + "referedBy");
		
		public static final Property occurInMieo = 
			m_model.createProperty(mieoNS + "refersTo");	
			
		public static final Property mieoEntailsString = 
			m_model.createProperty(mieoNS + "entailsString");
		
		public static final Property similarStringMieo = 
			m_model.createProperty(mieoNS + "similar");	
		
		public static final Property arg1 = 
			m_model.createProperty(mieoNS + "arg1");	
		
		public static final Property arg2 = 
			m_model.createProperty(mieoNS + "arg2");	
	   
	   //
	   // SIO Resources.
	   //		
	    public static final Resource SIO_000056 = 
			m_model.createResource(sioNS + "SIO_000056");
			public static final Resource positionSio = SIO_000056;

		public static final Resource SIO_000794 = 
			m_model.createResource(sioNS + "SIO_000794");	    
		    public static final Resource countSio = SIO_000794;
		    
	    public static final Resource SIO_010015 = m_model.createResource(sioNS + "SIO_010015");	    
	    	public static final Resource proteinSequenceSio = SIO_010015;

		public static final Resource SIO_000154 = 
			m_model.createResource(sioNS + "SIO_000154");
			public static final Resource articleSio = SIO_000154;
	    

		//
	    // SIO Properties.
	    //	   
	    public static final Property SIO_000060 = 
			m_model.createProperty(sioNS + "SIO_000060");
	    public static final Property isDenotedBy = SIO_000060;	    
	    	    	    
	    public static final Property SIO_000300 = 
			m_model.createProperty(sioNS + "SIO_000300");
		    public static final Property hasValue = SIO_000300;	    
		    
		public static final Property SIO_000059 = 
		   m_model.createProperty(sioNS + "SIO_000059");
			public static final Property hasMember = SIO_000059;
		    
		public static final Property SIO_000008 = 
			m_model.createProperty(sioNS + "SIO_000008");
			public static final Property hasAttribute = SIO_000008;
					
		public static final Property SIO_000011 = 
			m_model.createProperty(sioNS + "SIO_000011");
			public static final Property isAttributeOf = SIO_000011;		
		
		public static final Property SIO_000224 = 
			m_model.createProperty(sioNS + "SIO_000224");
			public static final Property isPropertyOf = SIO_000224;
	   
	    public static final Property SIO_000629 = 
			m_model.createProperty(sioNS + "SIO_000629");
			public static final Property isSubjectOf = SIO_000629;	   
	   
		public static final Property SIO_000628 = 
			m_model.createProperty(sioNS + "SIO_000628");
			public static final Property refersTo = SIO_000628;		   
	   
		public static final Property SIO_000671 = 
			m_model.createProperty(sioNS + "SIO_000671");
			public static final Property hasIdentifier = SIO_000671;
	   
		public static final Property SIO_000673 = 
			m_model.createProperty(sioNS + "SIO_000673");
			public static final Property hasUniqueIdentifier = SIO_000673;
				
		public static final Property SIO_isDescribedBy = 
			m_model.createProperty(sioNS + "SIO_isDescribedBy");
			public static final Property isDescribedBy = SIO_isDescribedBy;
			
		
		
		//
		// LSRN resources.
		//	   
	    public static final Resource UniProt_Record = 
			m_model.createResource(lsrnNS + "UniProt_Record");		    
		    
		public static final Resource UniProt_Identifier = 
			m_model.createResource(lsrnNS + "UniProt_Identifier");
	   
	    public static final Resource Ec_Record = 
			m_model.createResource(lsrnNS + "EC_Record");		    
		    
		public static final Resource Ec_Identifier = 
			m_model.createResource(lsrnNS + "EC_Identifier");		
		
	    public static final Resource Go_Record = 
			m_model.createResource(lsrnNS + "GO_Record");		    
		    
		public static final Resource Go_Identifier = 
			m_model.createResource(lsrnNS + "GO_Identifier");
		
		public static final Resource PMID_Record = 
			m_model.createResource(lsrnNS + "PMID_Record");
			    
			    
		public static final Resource PMID_Identifier = 
			m_model.createResource(lsrnNS + "PMID_Identifier");

			    
		//
		// String Ontology.
		//
		public static final Resource stringStr = m_model.createResource(strNS + "String");
		public static final Property beginIndexStr = m_model.createProperty(strNS + "beginIndex");	
		public static final Property endIndexStr = m_model.createProperty(strNS + "endIndex");	
		public static final Property anchorOfStr = m_model.createProperty(strNS + "anchorOf");
		public static final Property subStringStr = m_model.createProperty(strNS + "subString");	
		public static final Property superStringStr = m_model.createProperty(strNS + "superString");	




		   
		//
		// Annotation Ontology Model Resources.
		//
		public static final Resource AnnotationAo = 
			m_model.createResource(aoNS+"Annotation");  
		
		public static final Resource AnnotationSetAo = 
			m_model.createResource(aoNS+"AnnotationSet");  
		
		public static final Resource TextSelectorAos = 
			m_model.createResource(aosNS+"TextSelector"); 
		
		public static final Resource OffsetRangeTextSelectorAos = 
			m_model.createResource(aosNS+"OffsetRangeTextSelector"); 
		
		public static final Resource SourceDocumentPav = 
			m_model.createResource(pavNS+"SourceDocument");  
		
		
		public static final Property hasTopicAo = 
			m_model.createProperty(aoNS + "hasTopic");	
		
		public static final Property contextAo = 
			m_model.createProperty(aoNS + "context");	
		
		public static final Property onSourceDocumentAo = 
			m_model.createProperty(aoNS + "onSourceDocument");	
		
		public static final Property itemAo = 
			m_model.createProperty(aoNS + "item");	
		
		public static final Property exactAos = 
			m_model.createProperty(aosNS + "exact");	
		
		public static final Property offsetAos = 
			m_model.createProperty(aosNS + "offset");	
		
		public static final Property rangeAos = 
			m_model.createProperty(aosNS + "range");	
		
		public static final Property endAos = 
			m_model.createProperty(aosNS + "end");	
		
		
		public static final Property importedFromPav = 
			m_model.createProperty(pavNS + "importedFrom");
		
		public static final Property importedByPav = 
			m_model.createProperty(pavNS + "importedBy");	
		
		
		public static final Property createdByPav = 
			m_model.createProperty(pavNS + "createdBy");
		
		public static final Property createdOnPav = 
			m_model.createProperty(pavNS + "createdOn");
		
		public static final Property sourceAccessedOnPav = 
			m_model.createProperty(pavNS + "sourceAccessedOn");
		
		
		public static final Property annotatesDocumentAof = 
			m_model.createProperty(aofNS + "annotatesDocument");
		
		// Experiment.
		public static final Property documentAnnotatedByAof = 
			m_model.createProperty(aofNS + "documentAnnotatedBy");
		
		
		public static final Property onDocumentAof = 
			m_model.createProperty(aofNS + "onDocument");
		
		//
		// MISC
		//
		public static final Resource foafDocument = m_model.createResource(foafNS+"Document");

	    // rdf:value
	    public static final Property value = 
		m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
	    
	    
	    // rss:link
	    public static final Property link = 
		m_model.createProperty("http://purl.org/rss/1.0/link");
 
	    // foaf:topic
	    public static final Property topic = 
		m_model.createProperty("http://xmlns.com/foaf/0.1/topic");

	    
	    // rdf:type
	    public static final Property type = 
		m_model.
		createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	    // rdf:type-inverse
	    public static final Property type_inverse = 
		m_model.
		createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type-inverse");
	    
	    // rdfs:subClassOf
	    public static final Property subClassOf = 
		m_model.
		createProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");

	    // rdfs:label
	    public static final Property label = 
		m_model.
		createProperty("http://www.w3.org/2000/01/rdf-schema#label");

	    public static final Resource Class = 
		m_model.createResource("http://www.w3.org/2002/07/owl#Class");


	    // rdfs:subClassOf
	    public static final Property sameAs = 
		m_model.
		createProperty("http://www.w3.org/2002/07/owl#sameAs");



	} // class Vocab