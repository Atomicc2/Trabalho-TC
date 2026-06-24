package entities;

import java.io.File;

public class OperacoesAutomato {

    public static AutomatoFinito complemento(File arquivo) {
        AutomatoFinito automato = new AutomatoFinito(arquivo);

        

        for (Estado estado : automato.getEstados()) {
            estado.setFinal_(!estado.isFinal_());
        }

        return automato;
    }
}
