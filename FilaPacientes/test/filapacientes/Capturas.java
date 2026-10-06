package filapacientes;

import filapacientes.ui.PainelFila;
import java.awt.Container;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/** Renderiza os componentes Swing reais, sem precisar de um monitor. */
public final class Capturas {
    public static void main(String[] args) throws Exception {
        File diretorio = new File(args[0]);
        if (!diretorio.isDirectory() && !diretorio.mkdirs()) throw new IllegalStateException("Não foi possível criar a pasta.");
        SwingUtilities.invokeAndWait(() -> {
            Menu.configurarTema();
            Fila fila = new Fila();
            salvar(fila, new File(diretorio, "fila-vazia.png"));
            fila.adicionar(new Paciente("Ana Oliveira", 32, "Feminino"));
            fila.adicionar(new Paciente("Carlos Santos", 45, "Masculino"));
            fila.adicionar(new Paciente("Beatriz Lima", 27, "Feminino"));
            fila.adicionar(new Paciente("Lucas Pereira", 19, "Não informado"));
            salvar(fila, new File(diretorio, "fila-pacientes.png"));
        });
    }
    private static void layout(Container c) {
        c.doLayout();
        for (Component filho : c.getComponents()) if (filho instanceof Container) layout((Container) filho);
    }
    private static void salvar(Fila fila, File arquivo) {
        PainelFila painel = new PainelFila(fila);
        painel.setSize(painel.getPreferredSize());
        layout(painel);
        BufferedImage imagem = new BufferedImage(painel.getWidth(), painel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        painel.printAll(g); g.dispose();
        try { ImageIO.write(imagem, "png", arquivo); }
        catch (java.io.IOException ex) { throw new IllegalStateException(ex); }
    }
}
