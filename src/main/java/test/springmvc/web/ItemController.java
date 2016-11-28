package test.springmvc.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import test.springmvc.jaxb.AccountingItem;
import test.springmvc.jaxb.AccountingItemList;
import test.springmvc.jaxb.Field;
import test.springmvc.xml.XmlProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import static test.springmvc.xml.XmlProcessor.ACCOUNT_FIELD_NAME;
import static test.springmvc.xml.XmlProcessor.AMOUNT_FIELD_NAME;

/**
 * Controller for viewing some items list, reading item details, editing them and adding new item.
 */
@Controller
public class ItemController {

    private static final Logger logger = Logger.getLogger(ItemController.class.getName());
    private static final String PROJECT_PATH_VAR = "PROJECT_PATH";
    private static final String RESOURCES_PATH = "src/main/resources/";
    private static final String PROPERTIES_FILE_NAME = "prop.properties";
    private static final String FILE_PATH;
    private static final String FILE_NAME = "accounting_items.xml";
    private static final String INDEX_VIEW = "index";
    private static final String DETAILS_VIEW = "details";
    private static final String NEW_ITEM = "new_item";
    private static final String CSRF_KEY = "CSRF_key";
    public static final String ACCOUNTING_ITEM_ID_PARAM = "accounting_item_id";
    private static final String ACCOUNTING_ITEM_ID_VIEW_ATTR = "accountingItemId";

    static {
        String resourcesPath = getResourcesPath();
        logger.info("resourcesPath:" + resourcesPath);
        FILE_PATH = resourcesPath + FILE_NAME;
    }

    private String filePath = FILE_PATH;

    private static String getResourcesPath() {
        String resourcesPath = System.getProperty(PROJECT_PATH_VAR);
        if (resourcesPath != null) {
            resourcesPath = appendFileSeparatorIfNeeded(resourcesPath);
            return resourcesPath + RESOURCES_PATH;
        }
        try {
            InputStream inputStream = ItemController.class.getResourceAsStream(File.separator + PROPERTIES_FILE_NAME);
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                resourcesPath = properties.getProperty(PROJECT_PATH_VAR);
                if (resourcesPath != null) {
                    resourcesPath = appendFileSeparatorIfNeeded(resourcesPath);
                    return resourcesPath + RESOURCES_PATH;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resourcesPath = ItemController.class.getResource(File.separator).getFile();
        return resourcesPath;
    }

    private static String appendFileSeparatorIfNeeded(String resourcesPath) {
        if (!resourcesPath.endsWith("/") && !resourcesPath.endsWith("\\")) {
            resourcesPath += File.separator;
        }
        return resourcesPath;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(INDEX_VIEW);
        mav.addObject("accountingItems", XmlProcessor.parse(FILE_PATH).getAccountingItem());
        mav.addObject(CSRF_KEY, getOrCreateCsrfKeyForSession(request));
        return mav;
    }

    private String getOrCreateCsrfKeyForSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String csrfKey = (String) session.getAttribute(CSRF_KEY);
        if (csrfKey == null || csrfKey.isEmpty()) {
            csrfKey = UUID.randomUUID().toString();
            session.setAttribute(CSRF_KEY, csrfKey);
        }
        return csrfKey;
    }

    @RequestMapping(value = "processDetails", method = RequestMethod.GET)
    public ModelAndView process(@RequestParam(value = ACCOUNTING_ITEM_ID_PARAM, required = true) String itemId) {
        AccountingItemList accountingItemList = XmlProcessor.parse(FILE_PATH);
        AccountingItem accountingItem = accountingItemList.toMap().get(itemId);

        ModelAndView mav = new ModelAndView(DETAILS_VIEW);
        mav.addObject("com", accountingItem.getCom());
        mav.addObject(ACCOUNTING_ITEM_ID_VIEW_ATTR, accountingItem.getId());
        mav.addObject("fields", accountingItem.getField());
        return mav;
    }

    @RequestMapping(value = "showAddNew", method = RequestMethod.GET)
    public ModelAndView addNew() {
        ModelAndView mav = new ModelAndView(NEW_ITEM);
        mav.addObject(ACCOUNTING_ITEM_ID_VIEW_ATTR, UUID.randomUUID().toString());
        mav.addObject("fields", newItemFields());
        return mav;
    }

    private Collection<Field> newItemFields() {
        Collection<Field> fields = new ArrayList<>();
        Field field = new Field();
        field.setName("Item name");
        field.setTitle("Accounting name");
        fields.add(field);
        field = new Field();
        field.setName("Account");
        field.setTitle("account");
        fields.add(field);
        return fields;
    }

    @RequestMapping(value = "submitDetails", method = RequestMethod.POST)
    public ModelAndView submitDetails(@RequestParam(value = ACCOUNTING_ITEM_ID_PARAM) String itemId,
            @RequestParam(value = ACCOUNT_FIELD_NAME, required = false) String account,
            @RequestParam(value = AMOUNT_FIELD_NAME, required = false) String amount,
            @RequestParam(required = false) String comment, HttpServletRequest request) {
        checkCsrfKey(request);
        XmlProcessor.updateItem(FILE_PATH, itemId, account, amount, comment);
        return null;
    }

    private void checkCsrfKey(HttpServletRequest request) {
        String csrfKeyParam = request.getParameterMap().get(CSRF_KEY)[0];
        String csrfKey = (String) request.getSession().getAttribute(CSRF_KEY);
        if (csrfKey == null || csrfKey.isEmpty() || !csrfKey.equals(csrfKeyParam)) {
            throw new RuntimeException("CSRF_KEY problem, go to index");
        }
    }

    @RequestMapping(value = "processAddNew", method = RequestMethod.POST)
    public ModelAndView processAddNew(HttpServletRequest request) {
        checkCsrfKey(request);
        AccountingItem accountingItem = newAccountingItem(request);
        synchronized (this) {
            AccountingItemList accountingItems = XmlProcessor.parse(filePath);
            accountingItems.getAccountingItem().add(accountingItem);
            XmlProcessor.toFile(accountingItems, filePath);
        }
        return null;
    }

    private AccountingItem newAccountingItem(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        AccountingItem accountingItem = new AccountingItem();
        accountingItem.setId(params.get(ACCOUNTING_ITEM_ID_PARAM)[0]);
        accountingItem.setName(params.get("Item name")[0]);
        List<Field> fields = new ArrayList<>();
        Field field = new Field();
        field.setName(AMOUNT_FIELD_NAME);
        fields.add(field);
        field = new Field();
        field.setName(ACCOUNT_FIELD_NAME);
        field.setValue(params.get("Account")[0]);
        fields.add(field);
        accountingItem.setField(fields);
        return accountingItem;
    }

    public void setFilePath(String filePath) {
        logger.info("filePath: " + filePath);
        this.filePath = filePath;
    }
}
