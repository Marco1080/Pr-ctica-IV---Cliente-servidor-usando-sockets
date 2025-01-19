package model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column
    private String username;

    public Usuario() {
    }

    @OneToMany(mappedBy = "usuarioEmisor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "usuarioReceptor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Mensaje> mensajesRecibidos;

    public Usuario(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Mensaje> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(Set<Mensaje> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public Set<Mensaje> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(Set<Mensaje> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }
}

