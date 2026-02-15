package org.example.modelo.form;

public class ErrorDTO {
    private String c;
    private ErrorTipo m;
    private Object[] params;

    public ErrorDTO(String c, ErrorTipo m, Object...params) {
        this.c = c;
        this.m = m;
        this.params = params;
    }

    public String getC() {
        return c;
    }

    public ErrorTipo getM() {
        return m;
    }

    public Object[] getParams() {
        return params;
    }
}
