package filapacientes;

/** Dados validados e imutáveis de um paciente. */
public final class Paciente {
    private final String nome;
    private final String sexo;
    private final int idade;
    public Paciente(String nome, int idade, String sexo) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do paciente.");
        if (nome.trim().length() > 100) throw new IllegalArgumentException("O nome deve ter até 100 caracteres.");
        if (idade < 0 || idade > 130) throw new IllegalArgumentException("A idade deve estar entre 0 e 130 anos.");
        if (!"Feminino".equals(sexo) && !"Masculino".equals(sexo) && !"Não informado".equals(sexo))
            throw new IllegalArgumentException("Selecione uma opção válida para o sexo.");
        this.nome = nome.trim();
        this.idade = idade;
        this.sexo = sexo;
    }
    public String getNome() { return nome; }
    public int getIdade() { return idade; }
    public String getSexo() { return sexo; }
    @Override public String toString() { return nome + " • " + idade + " anos • " + sexo; }
}
