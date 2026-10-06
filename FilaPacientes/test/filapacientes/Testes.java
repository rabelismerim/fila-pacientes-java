package filapacientes;

import filapacientes.ui.PainelFila;
import java.awt.Component;
import java.awt.Container;
import javax.swing.*;

/** Testes sem dependências externas: execute pelo script com -Test. */
public final class Testes {
    private static int verificacoes;
    public static void main(String[] args) throws Exception {
        Fila fila = new Fila(3);
        verificar(fila.estaVazia() && fila.proximo() == null, "Fila inicial");
        falha(IllegalStateException.class, () -> fila.remover());
        falha(IllegalArgumentException.class, () -> new Fila(0));
        falha(NullPointerException.class, () -> fila.adicionar(null));
        for (int ciclo = 0; ciclo < 100; ciclo++) {
            Paciente a = new Paciente("Primeiro", 0, "Não informado");
            Paciente b = new Paciente("Segundo", 130, "Masculino");
            Paciente c = new Paciente("Terceiro", 25, "Feminino");
            fila.adicionar(a); fila.adicionar(b); fila.adicionar(c);
            verificar(fila.estaCheia(), "Capacidade");
            falha(IllegalStateException.class, () -> fila.adicionar(a));
            verificar(fila.remover() == a, "FIFO");
            fila.adicionar(a);
            verificar(fila.listar().get(0) == b && fila.listar().get(2) == a, "Reutilização circular");
            verificar(fila.remover() == b && fila.remover() == c && fila.remover() == a, "Ordem após circular");
            verificar(fila.estaVazia(), "Esvaziamento");
        }
        falha(IllegalArgumentException.class, () -> new Paciente("  ", 20, "Feminino"));
        falha(IllegalArgumentException.class, () -> new Paciente(null, 20, "Feminino"));
        falha(IllegalArgumentException.class, () -> new Paciente("Nome", -1, "Feminino"));
        falha(IllegalArgumentException.class, () -> new Paciente("Nome", 131, "Feminino"));
        falha(IllegalArgumentException.class, () -> new Paciente("Nome", 20, "Inválido"));
        verificar(new Paciente(" Ana ", 20, "Feminino").getNome().equals("Ana"), "Normalização do nome");
        fila.adicionar(new Paciente("Ana", 20, "Feminino"));
        falha(UnsupportedOperationException.class, () -> fila.listar().clear());
        verificar(fila.getQuantidade() == 1, "Lista protegida");
        SwingUtilities.invokeAndWait(() -> testarInterface());
        System.out.println(verificacoes + " verificações passaram (fila, validação e interface).");
    }
    private static void testarInterface() {
        Menu.configurarTema();
        Fila fila = new Fila(1);
        PainelFila painel = new PainelFila(fila);
        JTextField nome = (JTextField) encontrar(painel, "Nome completo");
        JTextField idade = (JTextField) encontrar(painel, "Idade em anos");
        JButton adicionar = (JButton) encontrar(painel, "Adicionar à fila");
        JButton atender = (JButton) encontrar(painel, "Atender próximo");
        verificar(!atender.isEnabled(), "Atendimento desabilitado na fila vazia");
        nome.setText("Ana"); idade.setText("abc"); adicionar.doClick();
        verificar(fila.estaVazia() && nome.getText().equals("Ana"), "Erro preserva formulário");
        idade.setText("25"); adicionar.doClick();
        verificar(fila.getQuantidade() == 1 && nome.getText().isEmpty(), "Cadastro pela interface");
        verificar(!adicionar.isEnabled() && atender.isEnabled(), "Estado da fila cheia");
        JTable tabela = (JTable) encontrarTipo(painel, JTable.class);
        verificar(tabela.getRowCount() == 1 && !tabela.isCellEditable(0, 0), "Tabela sincronizada e protegida");
        atender.doClick();
        verificar(fila.estaVazia() && tabela.getRowCount() == 0 && adicionar.isEnabled(), "Atendimento libera vaga");
    }
    private static Component encontrar(Container c, String nome) {
        for (Component f : c.getComponents()) {
            if (f instanceof JButton && nome.equals(((JButton) f).getText())) return f;
            if (f instanceof JTextField && nome.equals(f.getAccessibleContext().getAccessibleName())) return f;
            if (f instanceof Container) { Component resultado = encontrar((Container) f, nome); if (resultado != null) return resultado; }
        }
        return null;
    }
    private static Component encontrarTipo(Container c, Class<?> tipo) {
        for (Component f : c.getComponents()) {
            if (tipo.isInstance(f)) return f;
            if (f instanceof Container) { Component resultado = encontrarTipo((Container) f, tipo); if (resultado != null) return resultado; }
        }
        return null;
    }
    private static void verificar(boolean condicao, String descricao) {
        if (!condicao) throw new AssertionError(descricao);
        verificacoes++;
    }
    private static void falha(Class<? extends Throwable> tipo, Runnable acao) {
        try { acao.run(); } catch (Throwable ex) {
            verificar(tipo.isInstance(ex), "Exceção esperada: " + tipo.getSimpleName()); return;
        }
        throw new AssertionError("Exceção ausente: " + tipo.getSimpleName());
    }
}
