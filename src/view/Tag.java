package view;

import java.io.Serializable;

/**
 * Represents a tag on a photo, consisting of a tag name and a tag value.
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The tag name (type). */
    private String name;
    /** The tag value. */
    private String value;

    /**
     * Constructs a Tag with the given name and value.
     * @param name the tag name (type)
     * @param value the tag value
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the tag name.
     * @return the name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the tag value.
     * @return the value of the tag
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        return this.name.equalsIgnoreCase(other.name) && this.value.equalsIgnoreCase(other.value);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode() * 31 + value.toLowerCase().hashCode();
    }
}
