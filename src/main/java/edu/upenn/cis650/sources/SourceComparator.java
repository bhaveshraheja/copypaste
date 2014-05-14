package edu.upenn.cis650.sources;

import java.util.Comparator;

public class SourceComparator implements Comparator<Source>{

	public SourceComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Source arg0, Source arg1) {
		// TODO Auto-generated method stub
		return (arg0.getDistance() < arg1.getDistance()) ? 1: (arg0.getDistance() > arg1.getDistance())? -1: 0;
	}

}
