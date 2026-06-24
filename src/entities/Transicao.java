package entities;

import java.util.List;

public class Transicao {
    private int de;
    private int para;
    private String aoLer;

    public Transicao(int de, int para, String aoLer) {
        this.de = de;
        this.para = para;
        this.aoLer = aoLer;
    }

    public int getDe() {
        return de;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public int getPara() {
        return para;
    }

    public void setPara(int para) {
        this.para = para;
    }

    public String getAoLer() {
        return aoLer;
    }

    public void setAoLer(String aoLer) {
        this.aoLer = aoLer;
    }
}
