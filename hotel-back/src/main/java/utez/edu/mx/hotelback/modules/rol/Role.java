package utez.edu.mx.hotelback.modules.rol;

public enum Role {
    RECEPCION("ROLE_RECEPCION"),
    CAMARERA("ROLE_CAMARERA");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}