import entities.AutomatoFinito;
import entities.Estado;
import entities.Homomorfismo;
import entities.OperacoesAutomato;
import entities.Transicao;
import gui.JanelaInicial;
import gui.SeletorArquivos;
import gui.enums.Operacao;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.GridLayout;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Program {

    private AutomatoFinito resultadoFinal;
    private boolean naoMostrarJflap = false;
    private final SeletorArquivos seletorArquivos = new SeletorArquivos();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Program().iniciar());
    }

    private void iniciar() {
        configurarLookAndFeel();

        while (true) {
            Operacao operacao = escolherOperacao();
            if (operacao == null) {
                encerrar("Nenhuma operação foi selecionada. Encerrando.");
                return;
            }

            List<File> arquivosEntrada = selecionarArquivosEntrada(operacao);
            if (arquivosEntrada == null) continue;

            if (processarOperacao(operacao, arquivosEntrada)) {

                File destino = seletorArquivos.selecionarDestino(null);
                if (destino == null) continue;

                salvarResultado(destino);

                JOptionPane.showMessageDialog(null,
                        "Arquivo salvo em:\n" + destino.getAbsolutePath(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                perguntarAbrirJflap(destino);
            }
        }
    }

    private Operacao escolherOperacao() {
        JanelaInicial janela = new JanelaInicial();
        return janela.exibir();
    }

    private List<File> selecionarArquivosEntrada(Operacao operacao) {
        if (operacao == Operacao.DIFERENCA_SIMETRICA || operacao == Operacao.INTERSECCAO
                || operacao == Operacao.UNIAO || operacao == Operacao.DIFERENCA
                || operacao == Operacao.CONCATENACAO) {
            return seletorArquivos.selecionarDoisArquivos(null);
        } else {
            File arquivo = seletorArquivos.selecionarUmArquivo(null);
            if (arquivo == null) return null;

            List<File> lista = new ArrayList<>();
            lista.add(arquivo);
            return lista;
        }
    }

    private boolean processarOperacao(Operacao operacao, List<File> arquivosEntrada) {
        try {

            if (operacao == Operacao.COMPLEMENTO) {
                resultadoFinal = OperacoesAutomato.complemento(arquivosEntrada.getFirst());
            }

            else if (operacao == Operacao.ESTRELA) {
                resultadoFinal = OperacoesAutomato.estrela(arquivosEntrada.getFirst());
            }

            else if (operacao == Operacao.DIFERENCA_SIMETRICA) {
                resultadoFinal = OperacoesAutomato.diferencaSimetrica(
                        arquivosEntrada.get(0),
                        arquivosEntrada.get(1)
                );
            }

            else if (operacao == Operacao.INTERSECCAO) {
                resultadoFinal = OperacoesAutomato.interseccao(
                        arquivosEntrada.get(0),
                        arquivosEntrada.get(1)
                );
            }

            else if (operacao == Operacao.REVERSO) {
                resultadoFinal = OperacoesAutomato.reverso(arquivosEntrada.getFirst());
            }

            else if (operacao == Operacao.UNIAO) {
                resultadoFinal = OperacoesAutomato.uniao(
                        arquivosEntrada.get(0),
                        arquivosEntrada.get(1)
                );
            }

            else if (operacao == Operacao.DIFERENCA) {
                resultadoFinal = OperacoesAutomato.diferenca(
                        arquivosEntrada.get(0),
                        arquivosEntrada.get(1)
                );
            }

            else if (operacao == Operacao.MINIMIZACAO) {
                resultadoFinal = OperacoesAutomato.minimizar(arquivosEntrada.getFirst());
            }

            else if (operacao == Operacao.CONVERSAO_AFN_AFD) {
                resultadoFinal = OperacoesAutomato.converterAFNParaAFD(arquivosEntrada.getFirst());
            }

            else if (operacao == Operacao.CONCATENACAO) {
                resultadoFinal = OperacoesAutomato.concatenar(
                        arquivosEntrada.get(0),
                        arquivosEntrada.get(1)
                );
            }

            else if (operacao == Operacao.HOMOMORFISMO) {
                AutomatoFinito automato = new AutomatoFinito(arquivosEntrada.getFirst());
                Map<String, String> mapeamento = mostrarDialogoHomomorfismo(automato.getAlfabeto());
                if (mapeamento == null) return false;
                resultadoFinal = OperacoesAutomato.homomorfismo(automato, mapeamento);
            }

            return true;

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Map<String, String> mostrarDialogoHomomorfismo(Set<String> alfabeto) {
        if (alfabeto.isEmpty()) return new LinkedHashMap<>();

        JPanel painel = new JPanel(new GridLayout(alfabeto.size(), 2, 8, 8));
        Map<String, JTextField> campos = new LinkedHashMap<>();

        for (String simbolo : alfabeto) {
            painel.add(new JLabel("h(" + simbolo + "):"));
            JTextField campo = new JTextField(12);
            painel.add(campo);
            campos.put(simbolo, campo);
        }

        int resultado = JOptionPane.showConfirmDialog(null, painel,
                "Defina o Homomorfismo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) return null;

        Map<String, String> mapeamento = new LinkedHashMap<>();
        for (Map.Entry<String, JTextField> entry : campos.entrySet()) {
            mapeamento.put(entry.getKey(), Homomorfismo.normalizarImagem(entry.getValue().getText()));
        }
        return mapeamento;
    }

    private void salvarResultado(File arquivoDestino) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
            Document doc = builder.newDocument();

            Element estrutura = doc.createElement("structure");
            doc.appendChild(estrutura);

            Element tipo = doc.createElement("type");
            tipo.setTextContent("fa");
            estrutura.appendChild(tipo);

            Element automato = doc.createElement("automaton");

            for (Estado e : resultadoFinal.getEstados()) {
                Element estado = doc.createElement("state");

                String id = String.valueOf(e.getId());
                estado.setAttribute("id", id);

                String nome = e.getNome();
                estado.setAttribute("name", nome);

                Element x = doc.createElement("x");
                x.setTextContent(String.valueOf(100 * (e.getId() + 1)));
                estado.appendChild(x);

                Element y = doc.createElement("y");
                y.setTextContent(String.valueOf(200));
                estado.appendChild(y);

                if (e.isInicial()) {
                    Element inicial = doc.createElement("initial");
                    estado.appendChild(inicial);
                }
                if (e.isFinal_()) {
                    Element final_ = doc.createElement("final");
                    estado.appendChild(final_);
                }

                automato.appendChild(estado);
            }

            for (Transicao t : resultadoFinal.getTransicoes()) {
                Element transicao = doc.createElement("transition");

                Element de = doc.createElement("from");
                de.setTextContent(String.valueOf(t.getDe()));
                transicao.appendChild(de);

                Element para = doc.createElement("to");
                para.setTextContent(String.valueOf(t.getPara()));
                transicao.appendChild(para);

                Element simbolo = doc.createElement("read");
                simbolo.setTextContent(t.getSimbolo());
                transicao.appendChild(simbolo);

                automato.appendChild(transicao);
            }

            estrutura.appendChild(automato);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(arquivoDestino));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo .jff:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }


        System.out.println("[Aplicacao] salvarResultado chamado.");
        System.out.println("  Destino: " + arquivoDestino.getAbsolutePath());
    }

    private void perguntarAbrirJflap(File arquivo) {
        if (naoMostrarJflap) return;

        JCheckBox checkbox = new JCheckBox("Não mostrar novamente");
        Object[] mensagem = {"Deseja visualizar o autômato no JFLAP?", checkbox};

        int resposta = JOptionPane.showConfirmDialog(null, mensagem, "Abrir no JFLAP", JOptionPane.YES_NO_OPTION);

        if (checkbox.isSelected()) {
            naoMostrarJflap = true;
        }
        if (resposta == JOptionPane.YES_OPTION) {
            abrirNoJflap(arquivo);
        }
    }

    private void abrirNoJflap(File arquivo) {
        try {
            InputStream is = getClass().getResourceAsStream("JFLAP.jar");
            File temp = File.createTempFile("JFLAP", ".jar");
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Runtime.getRuntime().exec(new String[]{"java", "-jar", temp.getAbsolutePath(), arquivo.getAbsolutePath()});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao abrir o JFLAP:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("[Aplicacao] Não foi possível aplicar o L&F do sistema: " + e.getMessage());
        }
    }

    private void encerrar(String mensagem) {
        JOptionPane.showMessageDialog(
                null,
                mensagem,
                "Encerrando",
                JOptionPane.INFORMATION_MESSAGE
        );
        System.exit(0);
    }
}
