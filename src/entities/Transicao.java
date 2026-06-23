package entities;

import java.util.ArrayList;
import java.util.List;

public class Transicao {
    private int de;
    private int para;
    private List<Integer> aoLer = new ArrayList<>();

    public Transicao(int de, int para, List<Integer> aoLer) {
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

    public List<Integer> getAoLer() {
        return aoLer;
    }
}
