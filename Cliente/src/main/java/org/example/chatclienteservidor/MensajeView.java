package org.example.chatclienteservidor;

public class MensajeView {
    private String usuario;
    private String mensaje;
    private String fechaHora;

    public MensajeView(String usuario, String mensaje, String fechaHora) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}

