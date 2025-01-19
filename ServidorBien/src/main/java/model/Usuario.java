package model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column
    private String username;

    @Column(nullable = false)
    private byte[] password;

    @Column(nullable = false)
    private String role;

    public Usuario() {
    }

    @OneToMany(mappedBy = "usuarioEmisor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "usuarioReceptor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Mensaje> mensajesRecibidos;

    public Usuario(String username, byte[] password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

