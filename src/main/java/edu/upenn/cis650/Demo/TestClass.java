package edu.upenn.cis650.Demo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.exception.ProcessException;
import edu.upenn.cis650.extractor.GateInfoExtractor;
import edu.upenn.cis650.extractor.InfoExtractor;
import edu.upenn.cis650.extractor.InfoExtractorManager;
import edu.upenn.cis650.parsers.JsoupParser;
import edu.upenn.cis650.parsers.SourceParser;
import edu.upenn.cis650.score.Feature;
import edu.upenn.cis650.score.FeatureWeightFactory;
import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.sources.URLSource;
import edu.upenn.cis650.structure.Association;
import edu.upenn.cis650.structure.Node;
import edu.upenn.cis650.structure.SourceAssociation;
import edu.upenn.cis650.suggestion.Suggestion;
import edu.upenn.cis650.suggestion.Suggestions;
import gate.AnnotationSet;
import gate.Gate;
import gate.Annotation;
import gate.util.InvalidOffsetException;

public class TestClass {

	public static void main(String args[]) {
		
		try {
			
			Source s = new URLSource("http://marvel.com/movies/all");
			//Source s = new URLSource("http://www.amazon.com/s/ref=nb_sb_noss_1?url=search-alias%3Dstripbooks&field-keywords=computer+science");
			
			
			URL x = new URL(s.getSourceAddresss());
			
		
			GateInfoExtractor ie = new GateInfoExtractor();
			ie.initialize();
			System.out.println("Gate initialized"+ Gate.isInitialised());
			ie.setSource(s);
			
//			SourceParser sp = new JsoupParser(s);
//			for(Suggestion suggestion: ie.recommend(sp.findContainingText("Iron Man 3").get(1))) {
//				System.out.println("**********Suggestion************");
//				System.out.println(suggestion);
//			}
			
			//Test LCA method
			SourceParser sp = new JsoupParser(s);
			Source s1 = new URLSource("http://marvel.com/movies/all");
			SourceParser sp1 = new JsoupParser(s1);
			
			//Node extra = sp1.findContainingText("Coming soon from director Edgar Wright, starring Paul Rudd! More details soon!").get(0);
			
			Node one = sp.findContainingText("Ant-Man").get(1);
			Node two = sp.findContainingText("Captain America: The First Avenger").get(0);
			Node oneval = sp.findContainingText("Jul 17, 2015").get(0);
			Node three = sp.findContainingText("Iron Man: Rise of Technovore").get(0);
			
			/*
			 * ONE NODE, ONE SOURCE
			 */
			Suggestions sug1 = new Suggestions();
			System.out.println("Alajdl;asd");
			for(Node temp: sp.findContainingText("Ant-Man")) {
				sug1.addAll(InfoExtractorManager.recommend(temp));
			}
			sug1.sort(FeatureWeightFactory.getSingleNodeWeights());
			System.out.println(sug1);
			
			Suggestions oneSet = InfoExtractorManager.recommend(one);
			oneSet.sort(FeatureWeightFactory.getSingleNodeWeights());
			for(Suggestion sg:oneSet) {
				System.out.println(sg);
			}
			
			/*
			 * TWO NODE, ONE SOURCE
			 */
			/*
			for(Suggestion sg:InfoExtractorManager.recommend(new Association(one, oneval), two)) {
				System.out.println(sg);
			}
			*/
			/*
			Feature t = new Feature();
			for(Field f: t.getClass().getFields())
				System.out.println(f.getType());
			*/
			
			/**
			 * Two Node, get Different Source
			 */
			//System.out.println(ie.getAssociatiedSource(new SourceAssociation(one,s1), three).getSourceAddresss());
			
			//System.out.println(InfoExtractorManager.searchSourceSpace(two, "Chris Evans, Samuel L. Jackson, Sebastian Stan, Hayley Atwell, Hugo Weaving, Stanley Tucci, Tommy Lee Jones, Dominic Cooper, Toby Jones, Bruno Ricci, Neal McDonough, Derek Luke, Richard Armitage, JJ Field, Michael Brandon"));
			
			
			//System.out.println(InfoExtractorManager.recommend(new Association(one, extra), three));
			
			//System.out.println(ie.recommend(new Association(one, two), three));
			
			
			int tx = 01;
			int ty = 1;
			System.out.println((tx & ty) + "|" + (tx >> ty) );
		
		} catch (InitializationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
}
