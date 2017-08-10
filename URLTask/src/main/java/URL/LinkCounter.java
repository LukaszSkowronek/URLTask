package URL;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkCounter {
	
	private final String hostStartsWith = "www";

	public static void main(String[] args) throws URISyntaxException, NullPointerException {
		LinkCounter urlCounter = new LinkCounter();
		urlCounter.proccessAndPrint(args[0]);
	}
	
	private void proccessAndPrint(String url) {
		printMap(sortByValue(getFrequencyMap(getListOfDomains(getDocument(url)))));
	}
	
	private Document getDocument (String urlAddress) {
		Document document = null;
		try {
			document = Jsoup.connect(urlAddress).get();
		} catch (UnknownHostException e) {
			System.out.println("the IP address of a host could not be determined.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("Please enter valid URL");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (document == null) {
			throw new NullPointerException("Wrong URL address");
		}
		return document;
	}

	private List<String> getListOfDomains(Document document) {
		List<String> listOfDomains = new ArrayList<String>();
		
		Elements links = document.select("a[href]");

		for (Element link : links) {
			try {
				String domain = getDomain(link.attr("abs:href"));
				if (domain != null) {
					listOfDomains.add(domain);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return listOfDomains;
	}

	private String getDomain(String url) throws MalformedURLException {
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}
		URL netUrl = new URL(url);
		String host = netUrl.getHost();
		if (host.equals(""))
			return null;
		if (host.startsWith(hostStartsWith)) {
			host = host.substring(hostStartsWith.length() + 1);
		}
		return host;
	}

	private Map<String, Integer> getFrequencyMap(List<String> listOfDomains) {
		Set<String> uniqueDomains = new HashSet<String>(listOfDomains);
		Map<String, Integer> domains = new HashMap<String, Integer>();
		
		for (String uniqueDomain : uniqueDomains) {
			int domainFrequency = Collections.frequency(listOfDomains, uniqueDomain);
			domains.put(uniqueDomain, domainFrequency);
		}
		return domains;
	}

	private Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {
		List<Map.Entry<String, Integer>> listOfMap = new LinkedList<Map.Entry<String, Integer>>(unsortedMap.entrySet());
		Collections.sort(listOfMap, (o1, o2) -> -o1.getValue().compareTo(o2.getValue()));
		Map<String, Integer> sortedMapOfDomains = new LinkedHashMap<String, Integer>();
		
		for (Map.Entry<String, Integer> entry : listOfMap) {
			sortedMapOfDomains.put(entry.getKey(), entry.getValue());
		}
		return sortedMapOfDomains;
	}

	private void printMap(Map<String, Integer> map) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
