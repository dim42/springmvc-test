package test.springmvc.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = Field.FIELD_ELEMENT)
public class Field {
    public static final String FIELD_ELEMENT = "field";

    private String name;
    private String mask;
    private String title;
    private String value;

    public String getName() {
        return name;
    }

    @XmlAttribute(required = true)
    public void setName(String name) {
        this.name = name;
    }

    public String getMask() {
        return mask;
    }

    @XmlAttribute
    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getTitle() {
        return title;
    }

    @XmlAttribute
    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Field [name=" + name + ", mask=" + mask + ", title=" + title + ", value=" + value + "]";
    }
}
