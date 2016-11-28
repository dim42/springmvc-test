package test.springmvc.xml;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class JdomXml {

    private static final Logger logger = Logger.getLogger(JdomXml.class.getName());

    public static String getFormattedXml(String xmlPathName) throws IOException {
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        SAXBuilder saxBuilder = new SAXBuilder();
        Document jdomDocument;
        try {
            jdomDocument = saxBuilder.build(new File(xmlPathName));
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
        return xmlOutputter.outputString(jdomDocument);
    }

    public static void jdom(String filePath) {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(new InputStreamReader(new FileInputStream(filePath)));
            document.getRootElement().addContent("<end/>");
            logger.info("document.getContent(): " + document.getContent());
            IteratorIterable<Content> descendants = document.getDescendants();
            while (descendants.hasNext()) {
                logger.info("descendants.next(): " + descendants.next());
            }
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
