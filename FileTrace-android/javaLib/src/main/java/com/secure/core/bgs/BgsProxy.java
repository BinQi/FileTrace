package com.secure.core.bgs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by Jerry on 2020-03-17 11:50
 */
public class BgsProxy {
//    public static void beforeStartActivity(Callable callable) throws Exception {
//        try {
//            BgsHelper.beforeStartActivity(callable);
//            return;
//        } catch (NoClassDefFoundError e) {
//            e.printStackTrace();
//        }
//        callable.call();
//    }

    static final String TAG_PROPERTY = "property";
    static final String TAG_STR = "vt:lpwstr";
    static final String ATTR_FMTID = "fmtid";
    static final String ATTR_PID = "pid";
    static final String ATTR_NAME = "name";

    static final String RELS_CUSTOM = "<Relationship Id=\"rId4\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/custom-properties\" Target=\"docProps/custom.xml\"/>";
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {


        Path configPath = Paths.get(System.getProperty("user.home"), "Downloads", "作息时间表1", "docProps");
        File configFile = new File(configPath.toString(), "custom.xml");
        print(configFile.getAbsolutePath());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();

        Document doc = null;
        doc = builder.parse(configFile);
        doc.getDocumentElement().normalize();

        // 读值
        NodeList nodes = doc.getElementsByTagName(TAG_PROPERTY);

        Element pp = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node mynode = nodes.item(i);
            System.out.println("Property = " + mynode.getNodeName());
            if (null == pp) {
                pp = (Element) mynode.getParentNode();
            }

            if (mynode.getNodeType() == Node.ELEMENT_NODE) {
                Element myelement = (Element) mynode;

                print("vt:lpwstr = " + myelement.getElementsByTagName("vt:lpwstr").item(0).getTextContent());

                print("fmtid = " + myelement.getAttribute(ATTR_FMTID));
                print("pid = " + myelement.getAttribute(ATTR_PID));
                print("name = " + myelement.getAttribute(ATTR_NAME));

            }
        }

        pp.appendChild(createCtgFTagNode(doc));
        // write DOM back to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer xtransform;

        DOMSource mydom = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(configFile);

        xtransform = transformerFactory.newTransformer();
        xtransform.transform(mydom, streamResult);
    }

    private static Node createCtgFTagNode(Document doc) {
        Element e = doc.createElement(TAG_PROPERTY);
        e.setAttribute(ATTR_FMTID, "fmtid123");
        e.setAttribute(ATTR_PID, "pid1235678888");
        e.setAttribute(ATTR_NAME, "ctgFTag");

        Element e1 = doc.createElement(TAG_STR);
        e1.appendChild(doc.createTextNode("DFSFSSGDSGSDF"));

        e.appendChild(e1);
        return e;
    }

    private static void print(String msg) {
        System.out.println(msg);
    }
}
