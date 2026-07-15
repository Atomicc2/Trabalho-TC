package gui.enums;

public enum Operacao {

    UNIAO("União"),
    DIFERENCA("Diferença"),
    INTERSECCAO("Intersecção"),
    REVERSO("Reverso"),
    CONCATENACAO("Concatenação"),
    HOMOMORFISMO("Homomorfismo"),
    COMPLEMENTO("Complemento"),
    ESTRELA("Estrela"),
    DIFERENCA_SIMETRICA("Diferença Simétrica"),
    CONVERSAO_AFN_AFD("Conversão AFN → AFD"),
    MINIMIZACAO("Minimização");

    private final String descricao;

    Operacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
