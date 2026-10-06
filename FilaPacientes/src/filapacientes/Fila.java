package filapacientes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Fila circular limitada, independente da interface. */
public final class Fila {
    public static final int CAPACIDADE_PADRAO = 20;
    private final Paciente[] pacientes;
    private int inicio;
    private int quantidade;

    public Fila() { this(CAPACIDADE_PADRAO); }
    public Fila(int capacidade) {
        if (capacidade <= 0) throw new IllegalArgumentException("A capacidade deve ser positiva.");
        pacientes = new Paciente[capacidade];
    }
    public void adicionar(Paciente paciente) {
        Objects.requireNonNull(paciente, "Paciente obrigatório.");
        if (estaCheia()) throw new IllegalStateException("A fila está cheia. Atenda um paciente para liberar uma vaga.");
        pacientes[(inicio + quantidade) % pacientes.length] = paciente;
        quantidade++;
    }
    public Paciente remover() {
        if (estaVazia()) throw new IllegalStateException("Não há pacientes aguardando atendimento.");
        Paciente paciente = pacientes[inicio];
        pacientes[inicio] = null;
        inicio = (inicio + 1) % pacientes.length;
        quantidade--;
        return paciente;
    }
    public Paciente proximo() { return estaVazia() ? null : pacientes[inicio]; }
    public boolean estaVazia() { return quantidade == 0; }
    public boolean estaCheia() { return quantidade == pacientes.length; }
    public int getQuantidade() { return quantidade; }
    public int getCapacidade() { return pacientes.length; }
    public List<Paciente> listar() {
        List<Paciente> resultado = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) resultado.add(pacientes[(inicio + i) % pacientes.length]);
        return Collections.unmodifiableList(resultado);
    }
}
