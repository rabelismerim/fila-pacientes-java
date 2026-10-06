package filapacientes;

import filapacientes.ui.PainelFila;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;

/** Ponto de entrada da aplicação desktop. */
public final class Menu {
    private Menu() { }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            configurarTema();
            JFrame janela = new JFrame("Fila de pacientes");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(new PainelFila(new Fila()));
            janela.pack();
            janela.setMinimumSize(janela.getSize());
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
    public static void configurarTema() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ex) { System.err.println("Tema padrão mantido: " + ex.getMessage()); }
        UIManager.put("Label.font", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        UIManager.put("Button.font", new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
    }
}
