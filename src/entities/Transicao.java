package entities;

import java.util.List;

public class Transicao {
    private int de;
    private int para;
    private String simbolo;

    public Transicao(int de, int para, String simbolo) {
        this.de = de;
        this.para = para;
        this.simbolo = simbolo;
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

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
}
