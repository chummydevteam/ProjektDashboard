package projekt.dashboard.layers.util;

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

public class ReadXMLFile {

    public static String[] main(String argv[]) {

        try {
            File fXmlFile = new File(argv[0]);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("header");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String header_pack_name = eElement.getElementsByTagName("name").item(0).
                            getTextContent();
                    String header_pack_author = eElement.getElementsByTagName("author").item(0).
                            getTextContent();
                    String header_pack_development_team =
                            eElement.getElementsByTagName("team").item(0).
                                    getTextContent();
                    String header_pack_version = eElement.getAttribute("id");

                    String[] finalArray = {header_pack_name, header_pack_author,
                            header_pack_development_team, header_pack_version};

                    return finalArray;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}