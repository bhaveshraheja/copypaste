package edu.upenn.cis650.sources;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.upenn.cis650.extractor.InfoExtractorManager;

public class SourceSpaceExplorer  {
	
	private int threadPoolSize;
	
	private PriorityQueue<SourceEdge> pq;
	
	String searchText;
	
	private int jobSize;
	
	public SourceSpaceExplorer() {
		threadPoolSize = 6;
		jobSize = 15;
	}
	
	public SourceSpaceExplorer(int threadPoolSize) {
		jobSize = 15;
		this.threadPoolSize = threadPoolSize;
	}
	
	public SourceSpaceExplorer(List<SourceEdge> sourceEdges) {
		threadPoolSize = 6;
		jobSize = 15;
		pq = new PriorityQueue<SourceEdge>(sourceEdges);
	}
	
	public SourceSpaceExplorer(List<SourceEdge> sourceEdges, String searchText) {
		threadPoolSize = 6;
		jobSize = 15;
		pq = new PriorityQueue<SourceEdge>(sourceEdges);
		this.searchText = searchText;
		//System.out.println("PQ: Number of source edges:"+sourceEdges.size());
		//System.out.println(pq);
	}
	
	public void setJobSize(int size) {
		this.jobSize = size;
	}
	
	public void addElement(SourceEdge sourceEdge) {
		pq.add(sourceEdge);
	}
	
	public boolean hasElement() {
		return !this.pq.isEmpty();
	}
	
	public SourceEdge getElement() {
		return this.pq.poll();
	}
	
	public List<SearchSource> getJobBlock() {
		
		//Get a block of the jobs from the queue
		List<SearchSource> jobList = new ArrayList<SearchSource>();
		SourceEdge se;
		boolean validresult = true;
		int jobcount = 0;
		while(validresult && jobcount < jobSize) {
			se = pq.poll();
			jobcount ++;
			//System.out.println("Creating JOB block, job count:"+jobcount+"Source edge is:"+se);
			if(se == null)
				validresult = false;
			else
				jobList.add(new SearchSource(se));
		}
		
		return jobList;
		
	}
	
	
	/**
	 * Finds the source containing the required text
	 * @param text, the text to be searched
	 * @return the source that contains this text
	 */
	public SourceEdge getSourceContaining() {
		ExecutorService executorService;
		//System.out.println("Job size:"+jobSize+"|ThreadPoolSize:"+threadPoolSize);
		if(jobSize < threadPoolSize)
			executorService = Executors.newFixedThreadPool(jobSize);
		else
			executorService = Executors.newFixedThreadPool(threadPoolSize);
			
		CompletionService<SourceEdge> completionService = new ExecutorCompletionService<SourceEdge>(executorService);
		
		List<Future<SourceEdge>> futures = new ArrayList<Future<SourceEdge>>(jobSize);
		
		SourceEdge s = null;
		try {
			List<SearchSource> sources = getJobBlock();
			//System.out.println("Job-Block:"+sources);
			for(SearchSource ss: sources) {
				//System.out.println("Job submitted for "+ss.sourceEdge.getSource());
				futures.add(completionService.submit(ss));
			}
			for(int i = 0; i < sources.size(); i++) {
				try {
					//System.out.println("Waiting for result");
					SourceEdge se = completionService.take().get();
					if(se != null) {
						//System.out.println("Result that is not null"+se.getSource().getSourceAddresss());
						s = se;
						break;
					}
				}
				catch(Exception ignore) { }
				
				
			}
		}
		finally {
			for(Future<SourceEdge> f: futures)
				f.cancel(true);
			executorService.shutdownNow();
		}
		
		if(s != null) {
			return s;
		}
		
		return null;
	}
	
	
	
	/**
	 * A callable class that checks whether the source contains
	 * the required search text or not
	 * @author bhaveshraheja
	 *
	 */
	class SearchSource implements Callable<SourceEdge> {
		
		private SourceEdge sourceEdge;
		
		public SearchSource(SourceEdge se) {
			this.sourceEdge = se;
		}
		
		
		public SourceEdge call() throws Exception {
			//System.out.println("Source called:"+sourceEdge.getSource().getSourceAddresss());
			String content = InfoExtractorManager.getInfoExtractor(sourceEdge.getSource()).getSourceContent();
			if(content.contains(searchText))
				return sourceEdge;
			return null;
		}
		
	}
	

}
