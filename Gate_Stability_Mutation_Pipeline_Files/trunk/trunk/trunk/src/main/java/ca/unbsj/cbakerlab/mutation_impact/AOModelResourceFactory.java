package ca.unbsj.cbakerlab.mutation_impact;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class AOModelResourceFactory {
	private static final Logger log = Logger.getLogger(AOModelResourceFactory.class);

	/**
	 * 
	 * @param document
	 * @param topics
	 * @param model
	 * @return
	 */
	public static Resource createAnnotationResource(
			Resource document,
			List<Resource> topics,
			Model model){
		Resource annotation = null;
		//if(topic.getURI() != null){
		//	UriBuilder uriBuilder = UriBuilder.fromUri(Vocab.AnnotationAo.toString());
		//	uriBuilder.queryParam("hasTopic", topic.getURI());
		//	URI uri = uriBuilder.build();
			
		//	annotation = model.createResource(uri.toString());
		//}else{
			annotation = Utils.createTimestampedInstance(Vocab.AnnotationAo, model);
		//}	
		
		annotation.addProperty(Vocab.type,Vocab.AnnotationAo);
		annotation.addProperty(Vocab.annotatesDocumentAof,document);
		
		// Experiment.
		document.addProperty(Vocab.documentAnnotatedByAof,annotation);

		
		for(Resource topic : topics){
			annotation.addProperty(Vocab.hasTopicAo,topic);		
		}		
		return annotation;
	}	
	

	
	/**
	 * 
	 * @param document
	 * @param annotations
	 * @param text
	 * @param offset
	 * @param range
	 * @param model
	 * @return
	 */
	public static Resource createOffsetRangeTextSelectorResource(
			Resource document,
			//Resource sourceDocument, 
			List<Resource> annotations, 
			String text, 
			long offset, 
			long range,
			Model model){
		long end = offset + range;
		
		// Variant 1.
		//UriBuilder uriBuilder = UriBuilder.fromUri(document.getNameSpace()+"#offset_"+offset+"_"+end);
		
		
		// Variant 2.
		UriBuilder uriBuilder = UriBuilder.fromUri(document.getNameSpace());
		uriBuilder.queryParam("start", offset);
		uriBuilder.queryParam("end", end);

		
		URI uri = uriBuilder.build();	
		Resource selector = model.createResource(uri.toString());
		
		selector.addProperty(Vocab.type,Vocab.OffsetRangeTextSelectorAos);
		selector.addProperty(Vocab.type,Vocab.TextSelectorAos);
		selector.addProperty(Vocab.onDocumentAof, document);
		//selector.addProperty(Vocab.onSourceDocumentAo, sourceDocument);
		for(Resource annotation : annotations){
			// Wrong.
			//selector.addProperty(Vocab.contextAo,annotation);
			// Right.
			annotation.addProperty(Vocab.contextAo,selector);

		}	
		selector.addLiteral(Vocab.exactAos,text);
		selector.addLiteral(Vocab.offsetAos,model.createTypedLiteral(offset));
		selector.addLiteral(Vocab.rangeAos,model.createTypedLiteral(range));
		selector.addLiteral(Vocab.endAos,model.createTypedLiteral(end));
		
		return selector;
	}
	
	
	/**
	 * Very flexible! Null values are possible.
	 * @param document
	 * @param createdBy
	 * @param createdOn
	 * @param importedFrom
	 * @param importedBy
	 * @param model
	 * @return
	 */
	public static Resource createSourceDocumentResource(
			Resource document,
			Resource createdBy,
			String createdOn,
			Resource importedFrom, 
			Resource importedBy, 
			Model model){
		UriBuilder uriBuilder = UriBuilder.fromUri(Vocab.SourceDocumentPav.toString());
		uriBuilder.queryParam("source", document.getURI());
		URI uri = uriBuilder.build();
		
		Resource sourceDocument = model.createResource(uri.toString());		
		sourceDocument.addProperty(Vocab.type,Vocab.SourceDocumentPav);
		
		if(createdBy != null)sourceDocument.addProperty(Vocab.createdByPav,createdBy);
		if(createdOn != null)sourceDocument.addLiteral(Vocab.createdOnPav,createdOn);

		if(importedFrom != null)sourceDocument.addProperty(Vocab.importedFromPav,importedFrom);
		if(importedBy != null)sourceDocument.addProperty(Vocab.importedByPav,importedBy);
		
		return sourceDocument;
	}
	
	
	public static Resource createTextSelectorResource(
			Resource document,
			Resource annotation, 
			String text, 
			Model model){

		//UriBuilder uriBuilder = UriBuilder.fromUri(document.getNameSpace()+"#offset_"+offset+"_"+end);
		//uriBuilder.queryParam("ann", annotation.getURI());
		//URI uri = uriBuilder.build();
		
		Resource selector = Utils.createTimestampedInstance(Vocab.TextSelectorAos, model);
		
		selector.addProperty(Vocab.type,Vocab.TextSelectorAos);
		selector.addProperty(Vocab.onDocumentAof, document);
		annotation.addProperty(Vocab.contextAo,selector);
		selector.addLiteral(Vocab.exactAos,text);
		
		return selector;
	}
	
	public static Resource createTextSelectorResource(
			Resource document,
			Resource annotation, 
			String text, 
			Model model,
			boolean useUriSchema){
		String uriString = null;

		if(useUriSchema){
			UriBuilder uriBuilder = UriBuilder.fromPath(document.getNameSpace()+text.substring(0, 20)+"-"+text.substring(text.length()-20, text.length()));			
			URI uri = uriBuilder.build();
			uriString = uri.toString();
		} else {			
			uriString = Utils.createTimestampedUri(Vocab.TextSelectorAos);
		}		
		
		Resource selector = model.createResource(uriString);

		selector.addProperty(Vocab.type,Vocab.TextSelectorAos);
		selector.addProperty(Vocab.onDocumentAof, document);
		annotation.addProperty(Vocab.contextAo,selector);
		selector.addLiteral(Vocab.exactAos,text);
		
		return selector;
	}
	
	//=================================================
	/*
	public static Resource createOffsetRangeTextSelectorResource(
			Resource document,
			Resource sourceDocument, 
			Resource annotation, 
			String text, 
			int offset, 
			int range, 
			Model model){
		int end = offset + range;
		UriBuilder uriBuilder = UriBuilder.fromUri(document.getNameSpace()+"#offset_"+offset+"_"+end);
		uriBuilder.queryParam("ann", annotation.getURI());
		URI uri = uriBuilder.build();
		
		Resource selector = model.createResource(uri.toString());
		
		selector.addProperty(Vocab.type,Vocab.OffsetRangeTextSelectorAos);
		selector.addProperty(Vocab.onDocumentAof, document);
		selector.addProperty(Vocab.onSourceDocumentAo, sourceDocument);
		selector.addProperty(Vocab.contextAo,annotation);
		selector.addLiteral(Vocab.exactAos,text);
		selector.addLiteral(Vocab.offsetAos,offset);
		selector.addLiteral(Vocab.rangeAos,range);
		selector.addLiteral(Vocab.endAos,end);
		
		return selector;
	}*/
}
