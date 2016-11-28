package test.springmvc.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Some item to view on the page.
 */
@XmlRootElement
public class AccountingItem {
    private String name;
    private String id;
    private Integer com;
    private List<Field> fields = new ArrayList<>();

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCom() {
        return com;
    }

    @XmlAttribute
    public void setCom(Integer com) {
        this.com = com;
    }

    public void setField(List<Field> fields) {
        this.fields = fields;
    }

    @XmlElements(value = { @XmlElement })
    public List<Field> getField() {
        return fields;
    }

    @Override
    public String toString() {
        return "AccountingItem [name=" + name + ", id=" + id + ", com=" + com + ", fields=" + fields + "]";
    }
}
