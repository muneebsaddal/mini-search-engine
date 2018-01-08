import java.io.FileReader;
import java.io.BufferedReader;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Search {

	public void go(String word) {

		BufferedReader input = null;
		try {
			input = new BufferedReader(
					new FileReader("C:\\Users\\DELL\\Desktop\\indexed\\" + word + ".txt"));	//replace it with where text files are stored
			String line = null;
			String[] inputSplit = null;
			TreeMap<String, String> hmap = new TreeMap<String, String>();
			while ((line = input.readLine()) != null) {
				inputSplit = line.split("\\s");
				hmap.put(inputSplit[3], inputSplit[0]);
			}
			NavigableMap<String, String> nmap = hmap.descendingMap();
			for (NavigableMap.Entry<String, String> entry : nmap.entrySet()) {
				System.out.println(" PageID : " + entry.getValue() + "   ---   " + "Rank : " + entry.getKey());
			}
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {
		// try {
		// File inputFile = new File("page.xml");
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		// SAXParser saxParser = factory.newSAXParser();
		// myHandler myHandler = new myHandler();
		// saxParser.parse(inputFile, myHandler);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter word you want to search");
		String words = scan.nextLine();
		words = words.toLowerCase();
		String word[] = words.split("\\s");
		for (String word1 : word) {
			System.out.println(word1);
			new Search().go(word1);
		}
	}
}

class myHandler extends DefaultHandler {

	boolean bid = false;
	boolean btitle = false;
	boolean btext = false;
	boolean check;
	String title, text, Title, id;
	long rank;
	StringBuilder AddText = new StringBuilder();
	StringBuilder AddTitle = new StringBuilder();
	StringBuilder AddId = new StringBuilder();
	Integer countTitle;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("page")) {
			check = true;
		} else if (qName.equalsIgnoreCase("title")) {
			btitle = true;
		} else if (qName.equalsIgnoreCase("id") && check) {
			bid = true;
			check = false;
		} else if (qName.equalsIgnoreCase("text")) {
			btext = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("text")) {
			btext = false;
			text = AddText.toString();
		}
		if (qName.equalsIgnoreCase("title")) {
			btitle = false;
		}
		if (qName.equalsIgnoreCase("id")) {
			bid = false;
		}

		// PAGE ATTRIBUTE STARTS
		if (qName.equalsIgnoreCase("page")) {
			/**********************************************************************************************************************/
			// Working with TITLE
			/**********************************************************************************************************************/
			Hashtable<String, Integer> htitle = new Hashtable<String, Integer>();
			Title = Title.replaceAll("\\W+", " ");
			Title = Title.toLowerCase();
			String[] resultTitle = Title.split("\\s");
			// Counting number of each words
			for (String word : resultTitle) {
				if (htitle.containsKey(word)) {
					htitle.put(word, htitle.get(word) + 1);
				} else {
					htitle.put(word, 1);
				}
			}
			/****************************************************************************************************************************/
			// Working with TEXT
			/****************************************************************************************************************************/
			Hashtable<String, Integer> htext = new Hashtable<String, Integer>();
			text = text.replaceAll("\\W+", " ");
			text = text.toLowerCase();
			String[] regex = { "\\bthe\\b", "\\bit\\b", "\\band\\b", "\\bof\\b", "\\bin\\b", "\\bon\\b", "\\bas\\b",
					"\\bit\\b", "\\bits\\b", "\\bto\\b", "\\bby\\b", "\\bis\\b", "\\bfor\\b", "\\bhave\\b", "\\bare\\b",
					"\\bin\\b", "\\bor\\b", "\\bs\\b", "\\bhe\\b", "\\bdid\\b", "\\bthis\\b", "\\ba\\b", "\\b   \\b",
					"\\b  \\b", "\\b    \\b", "\\bing\\b" };
			for (int i = 0; i < regex.length; i++)
				text = text.replaceAll(regex[i], " ");
			String[] result = text.split("\\s");
			// Forward Indexing
			for (String word : result) {
				if (htext.containsKey(word)) {
					htext.put(word, htext.get(word) + 1);
				} else {
					htext.put(word, 1);
				}
			}
			/***********************************************************************************************************************************/
			// REVERSE INDEXING FOR TITLE
			/**********************************************************************************************************************************/
			for (String titleToken : htitle.keySet()) {
				Hashtable<String, Integer> tokenTitleDetail = new Hashtable<String, Integer>();
				if (!htitle.containsKey(titleToken)) {
					countTitle = 1;
				} else {
					countTitle = htitle.get(titleToken);
					tokenTitleDetail.put(titleToken, countTitle);
				}
			}
			/***********************************************************************************************************************************/
			// REVERSE INDEXING FOR TEXT
			/**********************************************************************************************************************************/
			for (String token : htext.keySet()) {

				Hashtable<String, Integer> tokenDetail = new Hashtable<String, Integer>();
				Integer countWord;
				if (!htext.containsKey(token)) {
					countWord = 1;
				} else {
					countWord = htext.get(token);
					tokenDetail.put(id, countWord);
					rank = (countWord * 25) + (countTitle * 975);
					String pageRank = Long.toString(rank);
					String content1 = tokenDetail.toString();
					String content2 = countTitle.toString();
					String content = content1.concat(content2);
					String blank = " ";
					content = content.concat(blank);
					content = content.concat(pageRank);
					content = content.replaceAll("\\W+", " ");
					content = content.trim();
					/***********************************************************************************************************************************/
					// CREATING TXT FILES FOR EACH WORD
					/**********************************************************************************************************************************/
					try {
						File file = new File("C:\\Users\\DELL\\Desktop\\indexed\\" + token + ".txt");	//replace it with folder where you want to store files
						if (file.createNewFile()) {
							// System.out.println("File is created!");
							try (BufferedWriter bwe = new BufferedWriter(new FileWriter(file, true))) {
								PrintWriter writer = new PrintWriter(bwe);
								writer.println(content);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							// System.out.println("File already exists.");
							try (BufferedWriter bwe = new BufferedWriter(new FileWriter(file, true))) {
								PrintWriter writer = new PrintWriter(bwe);
								writer.println(content);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					/**********************************************************************************************************************************/
				}
			}

			System.out.println(id);
			AddText = new StringBuilder();
		}
	}

	@Override
	public void characters(char[] buffer, int start, int length) throws SAXException {
		if (btitle) {
			Title = new String(buffer, start, length);
		} else if (bid) {
			id = new String(buffer, start, length);
		} else if (btext) {
			String line = new String(buffer, start, length);
			AddText.append(line);
		}
	}
}