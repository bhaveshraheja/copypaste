package edu.upenn.cis650.sources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis650.exception.InitializationErrorException;

/**
 * This class models the URL sources, where the address is an entire URL
 * @author bhaveshraheja
 *
 */
public class URLSource extends Source {
	
	private URL u;
	
	public URLSource() {
		
	}
	
	public URLSource(String address) throws InitializationErrorException {
		super(address);
		//Check if the address is a valid URL
		try {
			u = new URL(address);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new InitializationErrorException("URL address not valid");
		}
		
	}

	/**
	 * Since the URL source uses a URL as source address, retrieve relative names, etc
	 */
	@Override
	public List<String> getAlternativeAddresses() {
		List<String> alternateAddresses = new ArrayList<String>();
		
		alternateAddresses.add(this.getSourceAddresss());
		
		try {
			URL u = new URL(this.getSourceAddresss());
			alternateAddresses.add(u.getFile());
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alternateAddresses;
	}

}
