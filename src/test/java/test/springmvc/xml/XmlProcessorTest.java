package test.springmvc.xml;

import org.junit.Assert;
import org.junit.Test;
import test.springmvc.jaxb.AccountingItemList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class XmlProcessorTest {

    private static final String RESOURCES_PATH = "src/test/resources/";
    private static final String IN_FILE_NAME = "accounting_items_in.xml";
    private static final String OUT_FILE_NAME = "accounting_items_out.xml";
    private static final String EXPECTED_FILE_NAME = "accounting_items_in_exp.xml";

    @Test
    public void testParseAndToFile() {
        AccountingItemList accountingItemList = XmlProcessor.parse(RESOURCES_PATH + IN_FILE_NAME);

        XmlProcessor.toFile(accountingItemList, RESOURCES_PATH + OUT_FILE_NAME);

        String input = getFileLines(RESOURCES_PATH + IN_FILE_NAME, true);
        String output = getFileLines(RESOURCES_PATH + OUT_FILE_NAME, true);
        Assert.assertEquals("in:\n" + input + "out:\n" + output, input, output);

        JdomXml.jdom(RESOURCES_PATH + IN_FILE_NAME);
    }

    @Test
    public void testUpdate() {
        XmlProcessor.updateItem(RESOURCES_PATH + IN_FILE_NAME, "1", "1122334455", "7465.43", "comment1");

        String expected = getFileLines(RESOURCES_PATH + EXPECTED_FILE_NAME);
        String updated = getFileLines(RESOURCES_PATH + IN_FILE_NAME);
        expected = expected.replaceFirst("last-changed=\"[\\w: ]+\"", "last-changed=\"%DATE%\"");
        updated = updated.replaceFirst("last-changed=\"[\\w: ]+\"", "last-changed=\"%DATE%\"");
        Assert.assertEquals("in:\n" + expected + "out:\n" + updated, expected, updated);
    }

    private String getFileLines(String fileName) {
        return getFileLines(fileName, false);
    }

    private String getFileLines(String fileName, boolean trim) {
        Path path = FileSystems.getDefault().getPath(fileName);
        try {
            Stream<String> lines = Files.lines(path);
            return lines.reduce("", (s1, s2) -> (trim ? s1.trim() + s2.trim() : s1 + s2) + System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile(String fileName, boolean trim) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(trim ? str : str.trim()).append(System.lineSeparator());
            }
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
