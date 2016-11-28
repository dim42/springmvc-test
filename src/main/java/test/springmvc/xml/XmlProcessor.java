package test.springmvc.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import test.springmvc.jaxb.AccountingItemList;
import test.springmvc.jaxb.Field;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Helper for it uses JAXB for parsing and marshalling and org.w3c.dom xml update.
 */
public class XmlProcessor {

    private static final Logger logger = Logger.getLogger(XmlProcessor.class.getName());
    private static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String SCHEMA_LANG = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private static final String SCHEME_FILE_NAME = "accounting_items.xsd";

    public static final String ACCOUNT_FIELD_NAME = "account";
    public static final String AMOUNT_FIELD_NAME = "amount";
    private static final String COMMENT_FIELD_NAME = "comment";

    private static final String LAST_CHANGED_ATTR = "last-changed";
    private static final String NAME_ATTR = "name";
    private static final String VALUE_ATTR = "value";
    private static final String MASK_ATTR = "mask";

    public static synchronized AccountingItemList parse(String xmlPathName) {
        File file = new File(xmlPathName);
        try {
            JAXBContext context = JAXBContext.newInstance(AccountingItemList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (AccountingItemList) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void toFile(AccountingItemList accountingItemList, String fileName) {
        try {
            JAXBContext context = JAXBContext.newInstance(AccountingItemList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            marshaller.marshal(accountingItemList, file);
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void updateItem(String pathName, String itemId, String account, String amount, String comment) {
        try {
            DocumentBuilderFactory factory = newDocumentBuilderFactory();
            Document document = factory.newDocumentBuilder().parse(pathName);
            Element documentElement = document.getDocumentElement();
            documentElement.setAttribute(LAST_CHANGED_ATTR, new Date().toString());
            NodeList fields = getItemFields(document, itemId);
            updateFields(fields, account, amount, comment);
            transformToFile(document, pathName);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
                | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilderFactory newDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            /* Schema is used to enable to use document.getElementById(). */
        String schemaPathName = XmlProcessor.class.getClassLoader().getResource(SCHEME_FILE_NAME).getFile();
        File schema = new File(schemaPathName);
        try {
            // Setting the required schema details
            factory.setAttribute(SCHEMA_LANG, XML_SCHEMA);
            factory.setAttribute(SCHEMA_SOURCE, schema);
        } catch (IllegalArgumentException x) {
            logger.severe("DOM Parser does not support validation.");
        }
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        return factory;
    }

    private static NodeList getItemFields(Document document, String itemId) {
        Element accountingItem = document.getElementById(itemId);
        return accountingItem.getElementsByTagName(Field.FIELD_ELEMENT);
    }

    private static void updateFields(NodeList fields, String account, String amount, String comment) {
        for (int i = 0; i < fields.getLength(); i++) {
            Element field = (Element) fields.item(i);
            String attrValue;
            switch (field.getAttribute(NAME_ATTR)) {
                case ACCOUNT_FIELD_NAME:
                    attrValue = account;
                    break;
                case AMOUNT_FIELD_NAME:
                    attrValue = amount;
                    break;
                case COMMENT_FIELD_NAME:
                    attrValue = comment;
                    break;
                default:
                    continue;
            }
            validateAndSet(field, attrValue);
        }
    }

    private static void validateAndSet(Element field, String attrValue) {
        validate(field, attrValue);
        setFieldValue(field, attrValue);
    }

    private static void validate(Element field, String attrValue) {
        String pattern = field.getAttribute(MASK_ATTR);
        if (attrValue != null && !attrValue.isEmpty() && !pattern.isEmpty() && !attrValue.matches(pattern)) {
            throw new RuntimeException(String.format("Wrong %s: %s, pattern: %s", field.getAttribute(NAME_ATTR),
                    attrValue, pattern));
        }
    }

    private static void setFieldValue(Element field, String attrValue) {
        if (attrValue == null || attrValue.isEmpty()) {
            field.removeAttribute(VALUE_ATTR);
        } else {
            field.setAttribute(VALUE_ATTR, attrValue);
        }
    }

    private static void transformToFile(Document document, String pathName) throws TransformerException, IOException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        document.setXmlStandalone(true);
        transformer.transform(new DOMSource(document), new StreamResult(new File(pathName)));
        logger.info("getFormattedXml:\n" + JdomXml.getFormattedXml(pathName));
    }
}
