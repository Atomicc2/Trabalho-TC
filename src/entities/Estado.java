package entities;

public class Estado {
    private int id;
    private String nome;
    private boolean eInicial;
    private boolean eFinal;

    public Estado(int id, String nome, boolean eInicial, boolean eFinal) {
        this.id = id;
        this.nome = nome;
        this.eInicial = eInicial;
        this.eFinal = eFinal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean iseInicial() {
        return eInicial;
    }

    public void seteInicial(boolean eInicial) {
        this.eInicial = eInicial;
    }

    public boolean iseFinal() {
        return eFinal;
    }

    public void seteFinal(boolean eFinal) {
        this.eFinal = eFinal;
    }
}
