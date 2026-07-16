package entities;

import java.util.Map;

public class Homomorfismo {

    public AutomatoFinito aplicar(AutomatoFinito origem, Map<String, String> mapeamento) {
        if (mapeamento == null) {
            throw new IllegalArgumentException("Mapeamento de homomorfismo nulo.");
        }

        AutomatoFinito resultado = new AutomatoFinito();

        for (Estado e : origem.getEstados()) {
            resultado.getEstados().add(new Estado(e.getId(), e.getNome(), e.isInicial(), e.isFinal_()));
        }

        int proximoId = maxId(resultado) + 1;
        int gerados = 0;

        for (Transicao t : origem.getTransicoes()) {
            String simbolo = t.getSimbolo();

            if (simbolo == null || simbolo.isEmpty()) {
                resultado.getTransicoes().add(new Transicao(t.getDe(), t.getPara(), ""));
                continue;
            }

            if (!mapeamento.containsKey(simbolo)) {
                throw new IllegalArgumentException(
                        "Falta definir h(" + simbolo + ") no homomorfismo.");
            }

            String imagem = normalizarImagem(mapeamento.get(simbolo));
            if (imagem.isEmpty()) {
                resultado.getTransicoes().add(new Transicao(t.getDe(), t.getPara(), ""));
                continue;
            }

            int atual = t.getDe();
            for (int i = 0; i < imagem.length(); i++) {
                String ch = String.valueOf(imagem.charAt(i));
                boolean ultimo = i == imagem.length() - 1;
                int proximo = ultimo ? t.getPara() : proximoId++;

                if (!ultimo) {
                    resultado.getEstados().add(new Estado(proximo, "h" + gerados++, false, false));
                }

                resultado.getTransicoes().add(new Transicao(atual, proximo, ch));
                atual = proximo;
            }
        }

        for (Transicao t : resultado.getTransicoes()) {
            if (t.getSimbolo() != null && !t.getSimbolo().isEmpty()) {
                resultado.getAlfabeto().add(t.getSimbolo());
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

    public static String normalizarImagem(String valor) {
        if (valor == null) return "";
        String trimmed = valor.trim();
        if (trimmed.isEmpty()
                || trimmed.equalsIgnoreCase("e")
                || trimmed.equalsIgnoreCase("eps")
                || trimmed.equalsIgnoreCase("epsilon")
                || trimmed.equalsIgnoreCase("lambda")
                || trimmed.equals("ε")
                || trimmed.equals("λ")) {
            return "";
        }
        return trimmed;
    }
}
