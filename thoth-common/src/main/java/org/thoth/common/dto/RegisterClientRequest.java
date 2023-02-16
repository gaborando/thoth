package org.thoth.common.dto;

import java.io.Serializable;
import java.util.List;

public class RegisterClientRequest implements Serializable {
    private String identifier;
    private String name;
    private List<String> printServices;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPrintServices() {
        return printServices;
    }

    public void setPrintServices(List<String> printServices) {
        this.printServices = printServices;
    }
}
