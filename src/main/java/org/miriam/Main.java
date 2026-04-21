package org.miriam;

import org.miriam.controlador.CompraControlador;
import org.miriam.controlador.JuegoControlador;
import org.miriam.controlador.UsuarioControlador;
import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.modelo.entidad.CompraEntidad;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.ClasificacionEdad;
import org.miriam.modelo.enums.EstadoCuenta;
import org.miriam.modelo.enums.EstadoJuego;
import org.miriam.modelo.enums.MetodoPago;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.modelo.form.UsuarioForm;
import org.miriam.repositorio.implementacion.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FormularioInvalidoException {

        List<CompraEntidad> listaCompra = new ArrayList<>();

        var usuarioRepo = new UsuarioRepoInMemory();
        var juegoRepo = new JuegoRepoInMemory();
        var pais = new PaisesRepoInMemory();
        var biblio = new BibliotecaRepoInMemory();
        var compra = new CompraRepoInMemory();

        UsuarioForm usuarioForm = new UsuarioForm(
                "DarkUser",                  // válido: 3-20 chars, sin empezar por número
                "pepe@gmail.com",           // válido formato email
                "Prueba123",                // válido: >=8, mayúscula, minúscula y número
                "Pepe",                     // válido: 2-50 chars
                "España",                   // válido: no vacío
                LocalDate.of(2000, 1, 1),   // válido: mayor de 13 años
                "avatar.png"                // válido: < 100 chars
        );

        JuegoForm juegoForm = new JuegoForm(
                "Elden Ring",                          // válido: 1-100 chars
                "Juego de rol de acción en mundo abierto con alta dificultad", // <2000 chars
                "FromSoftware",                        // válido: 2-100 chars
                LocalDate.of(2022, 2, 25),             // fecha válida
                59.99,                                 // válido: dentro de rango y 2 decimales
                10,                                    // válido: 0-100
                "RPG",                                 // categoría (sin restricciones en validate)
                ClasificacionEdad.PEGI_16,             // no null
                List.of("Español", "Inglés"),          // no vacío y strings cortos
                EstadoJuego.DISPONIBLE                 // no null
        );

        UsuarioControlador usuarioControlador = new UsuarioControlador(usuarioRepo, pais);
        JuegoControlador juegoControlador = new JuegoControlador(juegoRepo);
        CompraControlador compraControlador = new CompraControlador
                (compra, usuarioRepo, juegoRepo, biblio);

        usuarioControlador.registrarUsuario(usuarioForm);
        juegoControlador.aniadirJuego(juegoForm);

        compraControlador.realizarCompra(1L, 1L, MetodoPago.CARTERA_STEAM);


        listaCompra.addAll(compra.obtenerTodos());

        if (listaCompra.isEmpty()){
            System.out.println("No funciona");
        }else {
            System.out.println("Funciona");
        }

    }

}