package edu.upenn.cis650.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.exception.ProcessException;
import edu.upenn.cis650.extractor.InfoExtractor;
import edu.upenn.cis650.extractor.InfoExtractorManager;
import edu.upenn.cis650.parsers.ParserManager;
import edu.upenn.cis650.parsers.SourceParser;
import edu.upenn.cis650.score.FeedbackInstance;
import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.sources.SourceEdge;
import edu.upenn.cis650.sources.URLSource;
import edu.upenn.cis650.structure.Association;
import edu.upenn.cis650.structure.Node;
import edu.upenn.cis650.suggestion.Suggestion;
import edu.upenn.cis650.suggestion.Suggestions;
import gate.Gate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class MainController {

	
	private int nrows;
	private int ncols;
	
	private Suggestions[] columnSuggestions;
	
	private Suggestions[] sentSuggestions;
	
	private FeedbackInstance[] feedbackState;
	
	private Source s; //This is the home/original source of the page
	
	
	public MainController() {
		nrows = 5;
		ncols = 4;
		columnSuggestions = new Suggestions[ncols];
		feedbackState = new FeedbackInstance[ncols];
		for(int i = 0; i < ncols; i++)
			feedbackState[i] = new FeedbackInstance();
		sentSuggestions = new Suggestions[ncols];
		s = null;
		
		//Also refresh state
		
		
	}
	
	@RequestMapping(value="/updateRates", method=RequestMethod.GET)
	public @ResponseBody
	boolean updateWeights(@RequestParam double delta, double eta) {
		
		for(int i = 0; i < ncols; i++) {
			feedbackState[i].setIncrementStep(delta);
			feedbackState[i].setLearningRate(eta);
		}
		
		
		return false;
	}
	
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView getMainPage() {
		ModelAndView model = new ModelAndView("helloWorld");
		model.addObject("nrows", nrows);
		model.addObject("ncols", ncols);
		model.addObject("gateStatus",Gate.isInitialised());
		//Also refresh state
		columnSuggestions = new Suggestions[ncols];
		feedbackState = new FeedbackInstance[ncols];
		for(int i = 0; i < ncols; i++)
			feedbackState[i] = new FeedbackInstance();
		sentSuggestions = new Suggestions[ncols];

		
		return model;
	}
	
	
	
	@RequestMapping(value="/setSource", method=RequestMethod.GET)
	public @ResponseBody
	String setSource(@RequestParam String url) {
		try {
			s = new URLSource(url);
			s.setDistance(0);
			return "Source: "+ s.getSourceAddresss() + " set";
		} catch (InitializationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "There was an error setting the source! Please try again.";
	}
	
	
	@RequestMapping(value = "/getSugs", method = RequestMethod.GET)
	public @ResponseBody
	Suggestions getReccomendation(@RequestParam String text, int column) throws ProcessException {
		
		//System.out.println("Request for "+text+" is received, column number:"+column);
		
		if(this.s == null) {
			throw new ProcessException("Source has not been set");
		}
		
		Suggestions presentSuggestions = recommend(text, this.s);
		
		if(columnSuggestions[column] == null) {
			//System.out.println("Earlier colum suggestions don't exist");
			columnSuggestions[column] = new Suggestions();
		}
		
		columnSuggestions[column].addAll(presentSuggestions);
		//System.out.println("Column suggestions now has "+columnSuggestions[column].toList().size()+" number of suggestions");
		
		if(feedbackState[column] == null)
			feedbackState[column] = new FeedbackInstance();
		
		//System.out.println("Feedback instance:"+feedbackState[column]);
		
		columnSuggestions[column].sort(feedbackState[column].getFeatureWeight());
		
		sentSuggestions[column] = filterTop(columnSuggestions[column],20);
		String prov = "---------------------------<br/>" + 
					 "Feature Vector:"+feedbackState[column].getFeatureVector()+
					  "<br/>Weights:"+feedbackState[column].getFeatureWeight() +
					  "<br/>Learning Rate: "+feedbackState[column].getLearningRate() +
					  "<br/>Delta: "+feedbackState[column].getIncrementStep();
		sentSuggestions[column].addProvenanceInfo(prov);
		return sentSuggestions[column];
				
	}
	
	@RequestMapping(value="/getMVSugs", method=RequestMethod.GET)
	public @ResponseBody
	Suggestions getMVRecommendation(@RequestParam String marker, String value, String text, int column) throws ProcessException {
		if(this.s == null) {
			throw new ProcessException("Source has not been set");
		}
		
		
		
		if(columnSuggestions[column] == null) {
			//System.out.println("Earlier colum suggestions don't exist");
			columnSuggestions[column] = new Suggestions();
		}
		
		if(value != null && value.length() > 0) {
			Suggestions presentSingleSuggestions = recommend(value, this.s);
			Suggestions currentSuggestions = new Suggestions();
			if(text == null || text.length() == 0)  {
				//these are the final set of suggestions
				currentSuggestions = presentSingleSuggestions;
			}
			else {
				currentSuggestions.addAll(presentSingleSuggestions);
				currentSuggestions.addAll(recommend(marker, value, text, this.s));
			}
				
			columnSuggestions[column].addAll(currentSuggestions);
		}
		if(feedbackState[column] == null)
			feedbackState[column] = new FeedbackInstance();
		
		//System.out.println("Feedback instance:"+feedbackState[column]);
		
		columnSuggestions[column].sort(feedbackState[column].getFeatureWeight());
		
		
		
		sentSuggestions[column] = filterTop(columnSuggestions[column],20);
		
		String prov = "---------------------------<br/>" + 
				 "Feature Vector:"+feedbackState[column].getFeatureVector()+
				  "<br/>Weights:"+feedbackState[column].getFeatureWeight() +
				  "<br/>Learning Rate: "+feedbackState[column].getLearningRate() +
				  "<br/>Delta: "+feedbackState[column].getIncrementStep();
		sentSuggestions[column].addProvenanceInfo(prov);
		
		return sentSuggestions[column];
		
		
	}
	
	
	@RequestMapping(value = "/feedback")
	public @ResponseBody
	boolean setFeedback(@RequestParam String suggText, int column) {
		//System.out.println("Feedback request received, column:"+column+"|Selected:"+suggText);
		
		//search for the suggestion from the given Suggestion text
		Suggestion selected = getSuggestionForText(suggText,column);
		
		if(selected == null)
			return false;
		
		if(feedbackState[column] == null) {
			//Something went wrong, no feedback instance has been created
			//for this column
			return false;
		}
		//System.out.println();
		feedbackState[column].addSelected(selected.getFeature());
		return true;
		
	}
	
	private Suggestion getSuggestionForText(String suggText, int column) {
		for(Suggestion x: sentSuggestions[column]) {
			if(x.getSuggestionText().equals(suggText))
				return x;
		}
		return null;
	}


	private Suggestions recommend(String text, Source src)  {
		
		Suggestions suggestions = new Suggestions();
		
		//1. Retrieve the Source Parser
		SourceParser sp = ParserManager.getSourceParser(src);
		
		//2. Find the given list of nodes
		List<Node> matchingNodes = sp.findContainingText(text);
		
		//3. If the list of nodes is null, explore a new search space
		if(matchingNodes.size() == 0) { 
			//System.out.println("No matching nodes found, text="+text+"|Source:"+src.getSourceAddresss());
		}
		else {
			InfoExtractor ie = InfoExtractorManager.getInfoExtractor(src);
			for(Node node: matchingNodes) {
				try {
					suggestions.addAll(ie.recommend(node));
				} catch (ProcessException e) {
					
					e.printStackTrace();
				}	
			}
		}
		//System.out.println("In nested method, number of suggestions:"+suggestions.toList().size());
		return suggestions;
	}
	
	private Suggestions recommend(String markerText, String valueText, String text, Source src) {
		
		Suggestions suggestions = new Suggestions();
		
		//1. Retrieve the Source Parser
		SourceParser sp = ParserManager.getSourceParser(src);
		
		//2. Find the given list of nodes, for marker
		List<Node> matchingMarkerNodes = sp.findContainingText(markerText);
		
		//3. Find the given list of nodes, for value
		List<Node> matchingValueNodes = sp.findContainingText(valueText);
		
		//4. Find the given list of nodes, for new marker
		List<Node> matchingNewMarkerNodes = sp.findContainingText(text);
		
		if(matchingValueNodes.size() == 0) {
			matchingValueNodes = new ArrayList<Node>();
			for(Node marker: matchingMarkerNodes) {
				SourceEdge edgeSource = InfoExtractorManager.searchSourceSpace(marker, valueText);
				//System.out.println("Edge Source received...."+edgeSource.getSource());
				//Now search within this source
				//System.out.println("Searching for value = "+valueText);
				matchingValueNodes.addAll(ParserManager.getSourceParser(edgeSource.getSource()).findContainingText(valueText));
			}
		}
		Suggestions  tempSuggestions;
		if(matchingValueNodes.size() == 0) {
			//System.out.println("Matching values is not null");
			return null; //There is still not possible value node
		}
		for(Node marker: matchingMarkerNodes) {
			for(Node value: matchingValueNodes) {
				for(Node matchNew: matchingNewMarkerNodes) {
					tempSuggestions = null;
					try {
						//System.out.println("Marker:"+marker.getNodeText()+"---s:"+marker.getSource().getSourceAddresss()+"|Value:"+value.getNodeText()+"--S:"+value.getSource().getSourceAddresss()+"|NEW:"+matchNew.getNodeText()+"--S:"+matchNew.getSource().getSourceAddresss());
						tempSuggestions = InfoExtractorManager.recommend(new Association(marker, value), matchNew);
					} 
					catch (ProcessException e) {
						// TODO Auto-generated catch block
						//System.err.println("Exception was thrown"+e.toString());
					}
					if(tempSuggestions != null) 
						suggestions.addAll(tempSuggestions);
				}
			}
		}
		
		return suggestions;
		
		
	}
	

	private Suggestions filterTop(Suggestions suggestions, int k) {
		
		Map<String, Suggestion> suggestionMap = new HashMap<String, Suggestion>();
		Suggestions topSuggestions = new Suggestions();
		int uniqueCount = 0;
		
		for(Suggestion suggest: suggestions) {
			
			if(suggestionMap.get(suggest.getSuggestionText())== null) {
				
				//1. Add to the map
				suggestionMap.put(suggest.getSuggestionText(), suggest);
				
				//2. Increment the count
				uniqueCount ++;
				
				//3. Add to top suggestion list
				topSuggestions.add(suggest);
				
				//4. Check for unique count
				if(uniqueCount == k )
					break;
			}
			
		}
		
		return topSuggestions;
	}
	

}
