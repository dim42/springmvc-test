package test.springmvc.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.when;
import static test.springmvc.web.ItemController.ACCOUNTING_ITEM_ID_PARAM;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    private static final String RESOURCES_PATH = "src/test/resources/";
    private static final String FILE_NAME = "accounting_items_controller.xml";
    private static final String FILE_PATH = RESOURCES_PATH + FILE_NAME;
    private static final String CSRF_KEY = "CSRF_key";
    private static final String CSRF_KEY_VALUE = "CSRF_key1";

    private ItemController controller;
    private File file;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest request;

    @Before
    public void before() throws IOException {
        createTestFile();
        controller = new ItemController();
        controller.setFilePath(FILE_PATH);
        when(request.getParameterMap()).thenReturn(getParameterMap());
        when(request.getSession().getAttribute(CSRF_KEY)).thenReturn(CSRF_KEY_VALUE);
    }

    private void createTestFile() throws IOException {
        file = new File(FILE_PATH);
        if (file.exists()) {
            throw new RuntimeException(file.getAbsolutePath() + " exists");
        }
        file.createNewFile();
        try (PrintWriter writer = new PrintWriter(file)) {
            String stub = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><accounting-item-list></accounting-item-list>";
            writer.println(stub);
        }
    }

    @Test
    public void testProcessAddNew() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            int poolSize = 200;
            final CountDownLatch latch = new CountDownLatch(poolSize);
            for (int i = 0; i < poolSize; i++) {
                pool.execute(() -> {
                    controller.processAddNew(request);
                    latch.countDown();
                });
            }
            latch.await();
        } finally {
            pool.shutdown();
        }
    }

    @After
    public void remove() {
        file.delete();
    }

    private Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<>();
        map.put(CSRF_KEY, new String[]{CSRF_KEY_VALUE});
        map.put(ACCOUNTING_ITEM_ID_PARAM, new String[]{UUID.randomUUID().toString()});
        map.put("Item name", new String[]{"Item name1"});
        map.put("Account", new String[]{"1122334455"});
        return map;
    }
}
