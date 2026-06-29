package entities;

import org.w3c.dom.Element;

import java.io.File;

public class OperacoesAutomato {

    public static AutomatoFinito complemento(File arquivo) {
        AutomatoFinito automato = new AutomatoFinito(arquivo);

        if (!automato.isAFD()) {
            throw new IllegalArgumentException("O autômato fornecido não é um AFD.");
        }

        for (Estado estado : automato.getEstados()) {
            estado.setFinal_(!estado.isFinal_());
        }

        if (!automato.isCompleto()) {
            automato.completarAutomato();
        }

        return automato;
    }

    public static AutomatoFinito estrela(File arquivo){
        AutomatoFinito automato = new AutomatoFinito(arquivo);

        if (!automato.isAFD()) {
            throw new IllegalArgumentException("O autômato fornecido não é um AFD.");
        }

        if (!automato.isCompleto()) {
            automato.completarAutomato();
        }

        int idAntigoInic = -1;

        for (Estado estado : automato.getEstados()) {
            if(estado.isInicial()){
                idAntigoInic = estado.getId();
                estado.setInicial(false);
                break;
            }
        }

        int idNovo = 0;

        for (Estado estado : automato.getEstados()) {
            idNovo++;
        }

        Estado novoInicial = new Estado(idNovo, "novoInicial", true, true);
        automato.getTransicoes().add(new Transicao(idNovo, idAntigoInic, ""));
        automato.getEstados().add(novoInicial);

        for (Estado estado : automato.getEstados()) {

            if(estado.isFinal_()) {
                automato.getTransicoes().add(new Transicao(estado.getId(), idAntigoInic, ""));
            }
        }

        return automato;
    }

}
