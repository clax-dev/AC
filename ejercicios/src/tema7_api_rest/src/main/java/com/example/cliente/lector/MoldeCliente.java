package com.example.cliente.lector;

public enum MoldeCliente {
    // constantes con su valor inicial entre paréntesis (son las columnas del fichero y y posición)
    TIPO_REGISTRO(0),
    CODIGO(1),
    FECHA_ALTA(2),
    ACTIVO(3),
    NOMBRE(4),
    APELLIDOS(5),
    DNI(6),
    RAZON_SOCIAL(7),
    CIF(8),
    EMAIL(9),
    TELEFONO(10),
    SECTOR(11);

    // variable para identificar en que posición de la fila está la columna seleccionada
    private final int posicion;

    // constructor
    MoldeCliente(int posicion) {
        this.posicion = posicion;
    }

    // getter; para leer los valores
    public int getPosicion() {
        return posicion;
    }


}
