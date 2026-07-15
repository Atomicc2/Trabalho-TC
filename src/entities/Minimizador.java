package entities;

import java.util.*;

public class Minimizador {

    private HashMap<String, ParEstados> tabela;

    private static class ParEstados {
        boolean equivalente;
        Set<ParEstados> dependentes;

        ParEstados() {
            this.equivalente = true;
            this.dependentes = new HashSet<>();
        }
    }

    private String chave(int id1, int id2) {
        return Math.min(id1, id2) + "_" + Math.max(id1, id2);
    }

    private ParEstados getPar(int id1, int id2) {
        return tabela.get(chave(id1, id2));
    }

    public AutomatoFinito minimizar(AutomatoFinito automato) {

        completarAlfabeto(automato);
        completarAutomato(automato);

        removerEstadosInuteis(automato);

        if (automato.getEstados().size() <= 1) {
            System.out.println("Automato com um estado apos limpeza, nada a minimizar.");
            return automato;
        }

        gerarTabela(automato.getEstados());
        marcarTrivialmenteNaoEquivalente(automato.getEstados());
        marcarNaoEquivalente(automato);

        HashMap<Integer, Estado> mapeamento = new HashMap<>();
        List<Estado> estadosNovos = unificarEstados(automato.getEstados(), mapeamento);

        List<Transicao> transicoesNovas = gerarTransicoesNovas(
                automato.getTransicoes(), mapeamento);

        renomearEstados(estadosNovos);

        AutomatoFinito automatoMin = new AutomatoFinito(estadosNovos, transicoesNovas);
        automatoMin.getAlfabeto().addAll(automato.getAlfabeto());

        boolean temInicial = false;
        for (Estado e : automatoMin.getEstados()) {
            if (e.isInicial()) { temInicial = true; break; }
        }
        if (!temInicial && !automatoMin.getEstados().isEmpty()) {
            automatoMin.getEstados().get(0).setInicial(true);
        }

        return automatoMin;
    }

    private void completarAlfabeto(AutomatoFinito automato) {
        for (Transicao t : automato.getTransicoes()) {
            if (t.getSimbolo() != null && !t.getSimbolo().isEmpty()
                    && !automato.getAlfabeto().contains(t.getSimbolo())) {
                automato.getAlfabeto().add(t.getSimbolo());
            }
        }
    }

    private void completarAutomato(AutomatoFinito automato) {
        int maxId = -1;
        for (Estado e : automato.getEstados()) {
            if (e.getId() > maxId) maxId = e.getId();
        }

        Estado estadoErro = new Estado(maxId + 1, "q_erro", false, false);
        List<Transicao> novas = new ArrayList<>();
        boolean precisaErro = false;

        for (Estado e : automato.getEstados()) {
            for (String s : automato.getAlfabeto()) {
                boolean temTrans = false;
                for (Transicao t : automato.getTransicoes()) {
                    if (t.getDe() == e.getId() && t.getSimbolo().equals(s)) {
                        temTrans = true;
                        break;
                    }
                }
                if (!temTrans) {
                    novas.add(new Transicao(e.getId(), estadoErro.getId(), s));
                    precisaErro = true;
                }
            }
        }

        if (precisaErro) {
            automato.getEstados().add(estadoErro);
            for (String s : automato.getAlfabeto()) {
                novas.add(new Transicao(estadoErro.getId(), estadoErro.getId(), s));
            }
        }
        automato.getTransicoes().addAll(novas);
    }

    private void removerEstadosInuteis(AutomatoFinito automato) {
        Estado ini = null;
        for (Estado e : automato.getEstados()) {
            if (e.isInicial()) { ini = e; break; }
        }
        List<Estado> fins = new ArrayList<>();
        for (Estado e : automato.getEstados()) {
            if (e.isFinal_()) fins.add(e);
        }
        if (ini == null || fins.isEmpty()) return;

        List<Integer> acessiveis = new ArrayList<>();
        buscarAcessiveis(ini.getId(), acessiveis, automato);

        List<Integer> coAcessiveis = new ArrayList<>();
        for (Estado f : fins) {
            buscarCoAcessiveis(f.getId(), coAcessiveis, automato);
        }

        List<Estado> validos = new ArrayList<>();
        for (Estado e : automato.getEstados()) {
            if (acessiveis.contains(e.getId()) && coAcessiveis.contains(e.getId())) {
                validos.add(e);
            }
        }

        Set<Integer> idsValidos = new HashSet<>();
        for (Estado e : validos) idsValidos.add(e.getId());

        List<Transicao> transicoesValidas = new ArrayList<>();
        for (Transicao t : automato.getTransicoes()) {
            if (idsValidos.contains(t.getDe()) && idsValidos.contains(t.getPara())) {
                transicoesValidas.add(t);
            }
        }

        automato.getEstados().clear();
        automato.getEstados().addAll(validos);
        automato.getTransicoes().clear();
        automato.getTransicoes().addAll(transicoesValidas);
    }

    public void buscarAcessiveis(int id, List<Integer> lista, AutomatoFinito automato) {
        if (lista.contains(id)) return;
        lista.add(id);
        for (Transicao t : automato.getTransicoes()) {
            if (t.getDe() == id) {
                buscarAcessiveis(t.getPara(), lista, automato);
            }
        }
    }

    public void buscarCoAcessiveis(int id, List<Integer> lista, AutomatoFinito automato) {
        if (lista.contains(id)) return;
        lista.add(id);
        for (Transicao t : automato.getTransicoes()) {
            if (t.getPara() == id) {
                buscarCoAcessiveis(t.getDe(), lista, automato);
            }
        }
    }

    private void gerarTabela(List<Estado> estados) {
        tabela = new HashMap<>();
        for (int i = 0; i < estados.size(); i++) {
            for (int j = i + 1; j < estados.size(); j++) {
                Estado a = estados.get(i);
                Estado b = estados.get(j);
                tabela.put(chave(a.getId(), b.getId()), new ParEstados());
            }
        }
    }

    private void marcarTrivialmenteNaoEquivalente(List<Estado> estados) {
        for (int i = 0; i < estados.size(); i++) {
            for (int j = i + 1; j < estados.size(); j++) {
                Estado a = estados.get(i);
                Estado b = estados.get(j);
                if (a.isFinal_() != b.isFinal_()) {
                    getPar(a.getId(), b.getId()).equivalente = false;
                }
            }
        }
    }

    private void marcarNaoEquivalente(AutomatoFinito automato) {
        List<Estado> estados = automato.getEstados();

        for (int i = 0; i < estados.size(); i++) {
            for (int j = i + 1; j < estados.size(); j++) {
                Estado a = estados.get(i);
                Estado b = estados.get(j);
                ParEstados par = getPar(a.getId(), b.getId());

                if (!par.equivalente) continue;

                for (String s : automato.getAlfabeto()) {
                    int da = mover(a.getId(), s, automato.getTransicoes(), automato.getEstados());
                    int db = mover(b.getId(), s, automato.getTransicoes(), automato.getEstados());

                    if (da == -1 || db == -1 || da == db) continue;

                    ParEstados parDestino = getPar(da, db);
                    if (parDestino == null) continue;

                    if (!parDestino.equivalente) {
                        marcarENaoPropagar(par);
                    } else {
                        parDestino.dependentes.add(par);
                    }
                }
            }
        }
    }

    private void marcarENaoPropagar(ParEstados par) {
        if (!par.equivalente) return;
        par.equivalente = false;
        for (ParEstados dep : par.dependentes) {
            marcarENaoPropagar(dep);
        }
        par.dependentes.clear();
    }

    private int mover(int id, String s, List<Transicao> transicoes,
            List<Estado> estados) {
        for (Transicao t : transicoes) {
            if (t.getDe() == id && t.getSimbolo().equals(s)) {
                return t.getPara();
            }
        }
        return -1;
    }

    private List<Estado> unificarEstados(List<Estado> estados,
            HashMap<Integer, Estado> mapeamento) {

        List<Estado> novos = new ArrayList<>();
        boolean[] visitado = new boolean[estados.size()];
        int novoId = 0;

        for (int i = 0; i < estados.size(); i++) {
            if (visitado[i]) continue;

            Estado e1 = estados.get(i);
            List<Estado> grupo = new ArrayList<>();
            grupo.add(e1);
            visitado[i] = true;

            for (int j = i + 1; j < estados.size(); j++) {
                if (visitado[j]) continue;
                Estado e2 = estados.get(j);
                ParEstados par = getPar(e1.getId(), e2.getId());
                if (par != null && par.equivalente) {
                    grupo.add(e2);
                    visitado[j] = true;
                }
            }

            boolean ehInicial = false, ehFinal = false;
            StringBuilder nome = new StringBuilder();
            for (int k = 0; k < grupo.size(); k++) {
                Estado g = grupo.get(k);
                if (k > 0) nome.append("_");
                nome.append(g.getNome());
                if (g.isInicial()) ehInicial = true;
                if (g.isFinal_()) ehFinal = true;
            }

            Estado novo = new Estado(novoId, nome.toString(), ehInicial, ehFinal);
            novos.add(novo);

            for (Estado g : grupo) {
                mapeamento.put(g.getId(), novo);
            }
            novoId++;
        }

        return novos;
    }

    private List<Transicao> gerarTransicoesNovas(List<Transicao> originais,
            HashMap<Integer, Estado> mapeamento) {

        List<Transicao> novas = new ArrayList<>();

        for (Transicao t : originais) {
            Estado novaOrigem  = mapeamento.get(t.getDe());
            Estado novaDestino = mapeamento.get(t.getPara());

            if (novaOrigem == null || novaDestino == null) continue;

            boolean jaExiste = false;
            for (Transicao n : novas) {
                if (n.getDe() == novaOrigem.getId()
                        && n.getPara() == novaDestino.getId()
                        && n.getSimbolo().equals(t.getSimbolo())) {
                    jaExiste = true;
                    break;
                }
            }
            if (!jaExiste) {
                novas.add(new Transicao(novaOrigem.getId(), novaDestino.getId(), t.getSimbolo()));
            }
        }
        return novas;
    }

    private void renomearEstados(List<Estado> estados) {
        for (int i = 0; i < estados.size(); i++) {
            if (estados.get(i).isInicial() && i != 0) {
                Estado tmp = estados.get(0);
                estados.set(0, estados.get(i));
                estados.set(i, tmp);
                break;
            }
        }
        for (int i = 0; i < estados.size(); i++) {
            estados.get(i).setId(i);
            estados.get(i).setNome("q" + i);
        }
    }
}
