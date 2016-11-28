package test.springmvc.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "accounting-item-list")
public class AccountingItemList {

    private final Map<String, AccountingItem> map = new HashMap<>();
    private String lastChanged;
    private List<AccountingItem> accountingItems = new ArrayList<>();

    @XmlAttribute(name = "last-changed")
    public String getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }

    @XmlElements(value = {@XmlElement(name = "accounting-item")})
    public List<AccountingItem> getAccountingItem() {
        return accountingItems;
    }

    public void setAccountingItem(List<AccountingItem> items) {
        this.accountingItems = items;
        accountingItemsToMap();
    }

    public Map<String, AccountingItem> toMap() {
        if (map.isEmpty()) {
            accountingItemsToMap();
        }
        return map;
    }

    private void accountingItemsToMap() {
        accountingItems.forEach(accountingItem -> map.put(accountingItem.getId(), accountingItem));
    }

    @Override
    public String toString() {
        return "AccountingItemList [lastChanged=" + lastChanged + ", accountingItems=" + accountingItems + "]";
    }
}
