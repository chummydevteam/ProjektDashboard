package projekt.dashboard.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Nicholas Chum (nicholaschum)
 */

public class ReadProfile {

    public static String main(String argv[]) {

        try {
            String tagRequest = argv[0];
            File fXmlFile = new File(argv[1]);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("profile");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String key = "";
                    if (argv.length == 3) {
                        if (argv[2].equals("alpha")) {
                            key = eElement.getElementsByTagName(tagRequest).item(0).
                                    getTextContent();
                        }
                        if (argv[2].equals("boolean")) {
                            key = eElement.getElementsByTagName(tagRequest).item(0).
                                    getTextContent();
                            if (key.equals("true")) {
                                return "true";
                            } else {
                                if (key.equals("false")) {
                                    return "false";
                                } else {
                                    return null;
                                }
                            }
                        }
                        if (argv[2].equals("id")) {
                            key = eElement.getAttribute("id");
                        }
                        if (argv[2].equals("unformatted")) {
                            key = eElement.getElementsByTagName(tagRequest).item(0).
                                    getTextContent();
                        }
                    } else {
                        // Check if string is a non-alpha hex with 7 characters including "#"
                        if (key.length() == 7) {
                            key = eElement.getElementsByTagName(tagRequest).item(0).
                                    getTextContent();
                        } else {
                            key = "#" + eElement.getElementsByTagName(tagRequest).item(0).
                                    getTextContent().substring(3);
                        }
                    }
                    return key;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}