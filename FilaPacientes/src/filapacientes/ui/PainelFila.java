package filapacientes.ui;

import filapacientes.Fila;
import filapacientes.Paciente;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Interface de cadastro e atendimento; a ordem da tabela acompanha a fila. */
public final class PainelFila extends JPanel {
    private static final Color FUNDO = new Color(243, 246, 250);
    private static final Color TEXTO = new Color(28, 44, 65);
    private static final Color AZUL = new Color(28, 99, 204);
    private final Fila fila;
    private final JTextField nome = new JTextField();
    private final JTextField idade = new JTextField();
    private final JComboBox<String> sexo = new JComboBox<>(new String[]{"Não informado", "Feminino", "Masculino"});
    private final JLabel aguardando = new JLabel();
    private final JLabel vagas = new JLabel();
    private final JLabel proximo = new JLabel();
    private final JLabel mensagem = new JLabel("Tudo pronto. Cadastre o primeiro paciente.");
    private final JButton adicionar = new JButton("Adicionar à fila");
    private final JButton atender = new JButton("Atender próximo");
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Posição", "Paciente", "Idade", "Sexo"}, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };

    public PainelFila(Fila fila) {
        this.fila = fila;
        setLayout(new BorderLayout(0, 22));
        setBackground(FUNDO);
        setBorder(new EmptyBorder(28, 32, 24, 32));
        setPreferredSize(new Dimension(1000, 720));

        JPanel topo = painel(new BorderLayout(0, 18), FUNDO);
        JPanel titulo = painel(new GridLayout(3, 1, 0, 5), FUNDO);
        titulo.add(texto("RECEPÇÃO  /  CONTROLE DE ATENDIMENTO", 11, AZUL, true));
        titulo.add(texto("Fila de pacientes", 30, TEXTO, true));
        titulo.add(texto("Organize a chegada. Acompanhe a espera. Atenda em ordem.", 14, new Color(92, 108, 128), false));
        topo.add(titulo, BorderLayout.NORTH);
        JPanel indicadores = painel(new GridLayout(1, 3, 16, 0), FUNDO);
        indicadores.add(indicador("AGUARDANDO", aguardando));
        indicadores.add(indicador("VAGAS DISPONÍVEIS", vagas));
        indicadores.add(indicador("PRÓXIMO PACIENTE", proximo));
        topo.add(indicadores, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JPanel corpo = painel(new BorderLayout(24, 0), FUNDO);
        JPanel formulario = painel(new GridBagLayout(), Color.WHITE);
        formulario.setBorder(new EmptyBorder(22, 22, 22, 22));
        formulario.setPreferredSize(new Dimension(285, 320));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 12, 0);
        formulario.add(texto("Novo paciente", 19, TEXTO, true), c);
        campo(formulario, c, "Nome completo", nome);
        campo(formulario, c, "Idade (anos)", idade);
        campo(formulario, c, "Sexo", sexo);
        estiloBotao(adicionar, AZUL, Color.WHITE);
        c.gridy++; c.insets = new Insets(10, 0, 0, 0); formulario.add(adicionar, c);
        c.gridy++; c.weighty = 1; formulario.add(painel(new BorderLayout(), Color.WHITE), c);
        corpo.add(formulario, BorderLayout.WEST);

        JPanel lista = painel(new BorderLayout(0, 14), Color.WHITE);
        lista.setBorder(new EmptyBorder(22, 22, 22, 22));
        JPanel cabecalho = painel(new BorderLayout(), Color.WHITE);
        cabecalho.add(texto("Ordem de atendimento", 19, TEXTO, true), BorderLayout.WEST);
        cabecalho.add(texto("FIFO · até " + fila.getCapacidade() + " pacientes", 12, new Color(92, 108, 128), false), BorderLayout.EAST);
        lista.add(cabecalho, BorderLayout.NORTH);
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(38); tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setForeground(TEXTO); tabela.setShowGrid(false); tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(new Color(222, 234, 251));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(FUNDO); tabela.getTableHeader().setForeground(TEXTO);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(65);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                super.getTableCellRendererComponent(t, v, s, f, r, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!s) setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                return this;
            }
        });
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setColumnHeaderView(tabela.getTableHeader());
        rolagem.setBorder(BorderFactory.createLineBorder(new Color(231, 236, 242)));
        rolagem.getViewport().setBackground(Color.WHITE);
        lista.add(rolagem, BorderLayout.CENTER);
        estiloBotao(atender, new Color(225, 236, 252), AZUL);
        lista.add(atender, BorderLayout.SOUTH);
        corpo.add(lista, BorderLayout.CENTER);
        add(corpo, BorderLayout.CENTER);
        mensagem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mensagem.setForeground(new Color(92, 108, 128));
        add(mensagem, BorderLayout.SOUTH);
        nome.getAccessibleContext().setAccessibleName("Nome completo");
        idade.getAccessibleContext().setAccessibleName("Idade em anos");
        sexo.getAccessibleContext().setAccessibleName("Sexo");
        adicionar.addActionListener(e -> cadastrar());
        nome.addActionListener(e -> idade.requestFocusInWindow());
        idade.addActionListener(e -> cadastrar());
        atender.addActionListener(e -> {
            if (fila.estaVazia()) return;
            Paciente paciente = fila.remover();
            atualizar();
            informar("Atendimento iniciado: " + paciente.getNome() + ".", false);
        });
        atualizar();
        if (!fila.estaVazia()) informar("Pacientes aguardando. Atenda o próximo por ordem de chegada.", false);
    }

    private void cadastrar() {
        try {
            if (nome.getText().trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do paciente.");
            int anos;
            try { anos = Integer.parseInt(idade.getText().trim()); }
            catch (NumberFormatException ex) { throw new IllegalArgumentException("Informe uma idade inteira entre 0 e 130 anos."); }
            Paciente paciente = new Paciente(nome.getText(), anos, (String) sexo.getSelectedItem());
            fila.adicionar(paciente);
            nome.setText(""); idade.setText(""); sexo.setSelectedIndex(0);
            atualizar(); informar(paciente.getNome() + " adicionado(a) à fila.", false);
            nome.requestFocusInWindow();
        } catch (IllegalArgumentException | IllegalStateException ex) { informar(ex.getMessage(), true); }
    }

    public void atualizar() {
        modelo.setRowCount(0);
        int posicao = 1;
        for (Paciente p : fila.listar()) modelo.addRow(new Object[]{posicao++, p.getNome(), p.getIdade(), p.getSexo()});
        aguardando.setText(fila.getQuantidade() + " / " + fila.getCapacidade());
        vagas.setText(String.valueOf(fila.getCapacidade() - fila.getQuantidade()));
        proximo.setText(fila.estaVazia() ? "Fila vazia" : fila.proximo().getNome());
        proximo.setToolTipText(fila.estaVazia() ? "Cadastre um paciente para começar" : fila.proximo().toString());
        atender.setEnabled(!fila.estaVazia()); adicionar.setEnabled(!fila.estaCheia());
    }

    private void informar(String texto, boolean erro) {
        mensagem.setText(texto);
        mensagem.setForeground(erro ? new Color(177, 47, 47) : new Color(29, 115, 79));
    }
    private static JPanel painel(LayoutManager layout, Color cor) {
        JPanel painel = new JPanel(layout); painel.setBackground(cor); return painel;
    }
    private static JLabel texto(String valor, int tamanho, Color cor, boolean negrito) {
        JLabel label = new JLabel(valor); label.setFont(new Font("SansSerif", negrito ? Font.BOLD : Font.PLAIN, tamanho));
        label.setForeground(cor); return label;
    }
    private static JPanel indicador(String titulo, JLabel valor) {
        JPanel painel = painel(new BorderLayout(0, 8), Color.WHITE);
        painel.setBorder(new EmptyBorder(16, 18, 16, 18));
        painel.add(texto(titulo, 11, new Color(92, 108, 128), true), BorderLayout.NORTH);
        valor.setFont(new Font("SansSerif", Font.BOLD, 21)); valor.setForeground(TEXTO);
        painel.add(valor, BorderLayout.CENTER); return painel;
    }
    private static void campo(JPanel painel, GridBagConstraints c, String titulo, JComponent campo) {
        c.gridy++; c.insets = new Insets(6, 0, 6, 0); painel.add(texto(titulo, 12, TEXTO, true), c);
        c.gridy++; campo.setPreferredSize(new Dimension(220, 36));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14)); painel.add(campo, c);
    }
    private static void estiloBotao(JButton botao, Color fundo, Color texto) {
        botao.setBackground(fundo); botao.setForeground(texto); botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(12, 16, 12, 16)); botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
