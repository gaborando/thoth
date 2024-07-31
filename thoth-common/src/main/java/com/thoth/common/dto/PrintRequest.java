package com.thoth.common.dto;

public class PrintRequest {
    private String printService;
    private String svg;

    private Integer copies;

    public String getPrintService() {
        return printService;
    }

    public void setPrintService(String printService) {
        this.printService = printService;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }
}
