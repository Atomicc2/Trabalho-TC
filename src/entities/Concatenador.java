package entities;

import java.util.HashMap;
import java.util.Map;

public class Concatenador {

    public AutomatoFinito concatenar(AutomatoFinito a, AutomatoFinito b) {
        Estado inicialA = null, inicialB = null;
        for (Estado e : a.getEstados()) {
            if (e.isInicial()) { inicialA = e; break; }
        }
        for (Estado e : b.getEstados()) {
            if (e.isInicial()) { inicialB = e; break; }
        }
        if (inicialA == null || inicialB == null) {
            throw new IllegalArgumentException("Autômato sem estado inicial.");
        }

        AutomatoFinito resultado = new AutomatoFinito();

        Map<Integer, Integer> mapaA = copiarEstados(a, resultado, "A1_", 0, true, false);

        int offset = maxId(resultado) + 1;
        Map<Integer, Integer> mapaB = copiarEstados(b, resultado, "A2_", offset, false, true);

        copiarTransicoes(a, resultado, mapaA);
        copiarTransicoes(b, resultado, mapaB);

        int idInicialB = mapaB.get(inicialB.getId());
        for (Estado e : a.getEstados()) {
            if (e.isFinal_()) {
                resultado.getTransicoes().add(new Transicao(mapaA.get(e.getId()), idInicialB, ""));
            }
        }

        return resultado;
    }

    private int maxId(AutomatoFinito a) {
        int max = -1;
        for (Estado e : a.getEstados()) {
            if (e.getId() > max) max = e.getId();
        }
        return max;
    }

    private Map<Integer, Integer> copiarEstados(
            AutomatoFinito origem, AutomatoFinito destino,
            String prefixoNome, int offset,
            boolean manterInicial, boolean manterFinal) {
        Map<Integer, Integer> ids = new HashMap<>();
        for (Estado e : origem.getEstados()) {
            int novoId = e.getId() + offset;
            ids.put(e.getId(), novoId);
            Estado copia = new Estado(novoId, prefixoNome + e.getNome(),
                    manterInicial && e.isInicial(), manterFinal && e.isFinal_());
            destino.getEstados().add(copia);
        }
        return ids;
    }

    private void copiarTransicoes(AutomatoFinito origem, AutomatoFinito destino, Map<Integer, Integer> ids) {
        for (Transicao t : origem.getTransicoes()) {
            destino.getTransicoes().add(new Transicao(
                    ids.get(t.getDe()), ids.get(t.getPara()), t.getSimbolo()));
        }
    }
}
