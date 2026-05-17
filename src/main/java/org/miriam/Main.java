package org.miriam;

import org.miriam.controlador.*;
import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.modelo.dto.*;
import org.miriam.modelo.enums.ClasificacionEdad;
import org.miriam.modelo.enums.EstadoJuego;
import org.miriam.modelo.enums.EstadoResena;
import org.miriam.modelo.enums.MetodoPago;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.modelo.form.ResenaForm;
import org.miriam.modelo.form.UsuarioForm;
import org.miriam.repositorio.implementacion.PaisesRepoInMemory;
import org.miriam.repositorio.implementacionHibernate.*;
import org.miriam.transaction.HibernateTransactionManager;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("==============================================");
            System.out.println("      INICIANDO SISTEMA TIPO STEAM");
            System.out.println("==============================================");

            // TRANSACTION MANAGER
            var tm = new HibernateTransactionManager();

            // REPOSITORIOS HIBERNATE
            var usuarioRepo = new UsuarioRepoHibernate(tm);
            var juegoRepo = new JuegoRepoHibernate(tm);
            var compraRepo = new CompraRepoHibernate(tm);
            var bibliotecaRepo = new BibliotecaRepoHibernate(tm);
            var resenaRepo = new ResenaRepoHibernate(tm);
            var paisRepo = new PaisesRepoInMemory();

            // CONTROLADORES
            var usuarioControlador = new UsuarioControlador(usuarioRepo, paisRepo, tm);
            var juegoControlador = new JuegoControlador(juegoRepo, tm);
            var compraControlador = new CompraControlador(compraRepo, usuarioRepo, juegoRepo, bibliotecaRepo, tm);
            var bibliotecaControlador = new BibliotecaControlador(usuarioRepo, juegoRepo, bibliotecaRepo, tm);
            var resenaControlador = new ResenaControlador(resenaRepo, compraRepo, usuarioRepo, juegoRepo, bibliotecaRepo, tm);

            // CREAR USUARIO
            UsuarioForm usuarioForm = new UsuarioForm(
                    "TesttUser",
                    "darkuser20@gmail.com",
                    "Prueba123",
                    "Miriam",
                    "España",
                    LocalDate.of(2000, 1, 1),
                    "avatar.png"
            );

            UsuarioDTO usuario = usuarioControlador.registrarUsuario(usuarioForm);
            System.out.println("USUARIO CREADO -> ID: " + usuario.getId() + " | NOMBRE: " + usuario.getNombreUsuario());

            // CONSULTAR PERFIL POR ID
            UsuarioDTO perfilPorId = usuarioControlador.consultarPerfil(usuario.getId());
            System.out.println("\nCONSULTAR PERFIL POR ID -> " + perfilPorId.getNombreUsuario());

            // CONSULTAR PERFIL POR NOMBRE
            UsuarioDTO perfilPorNombre = usuarioControlador.consultarPerfil(usuario.getNombreUsuario());
            System.out.println("\nCONSULTAR PERFIL POR NOMBRE -> " + perfilPorNombre.getEmail());

            // AÑADIR SALDO
            UsuarioDTO usuarioSaldo = usuarioControlador.aniadirSaldo(usuario.getId(), 150.00);
            System.out.println("\nSALDO AÑADIDO -> ACTUAL: " + usuarioSaldo.getSaldoCartera() + " €");

            // CONSULTAR SALDO
            Double saldoAntesCompra = usuarioControlador.consultarSaldo(usuario.getId());
            System.out.printf("\nCONSULTAR SALDO -> %.2f €%n", saldoAntesCompra);

            // CREAR JUEGO 1
            JuegoForm juegoForm = new JuegoForm(
                    "Elden Ring 6",
                    "Juego RPG de mundo abierto",
                    "FromSoftware",
                    LocalDate.of(2022, 2, 25),
                    59.99,
                    10,
                    "RPG",
                    ClasificacionEdad.PEGI_18,
                    List.of("Español", "Inglés"),
                    EstadoJuego.DISPONIBLE
            );

            JuegoDTO juego = juegoControlador.aniadirJuego(juegoForm);
            System.out.println("\nJUEGO CREADO -> ID: " + juego.getId() + " | TITULO: " + juego.getTitulo());

            // CREAR JUEGO 2 ("Minecraft Java")
            JuegoForm juegoForm2 = new JuegoForm(
                    "Minecraft Java",
                    "Sandbox infinito",
                    "Mojang",
                    LocalDate.of(2011, 11, 18),
                    29.99,
                    0,
                    "Sandbox",
                    ClasificacionEdad.PEGI_7,
                    List.of("Español", "Inglés"),
                    EstadoJuego.DISPONIBLE
            );

            JuegoDTO juego2 = juegoControlador.aniadirJuego(juegoForm2);
            System.out.println("\nSEGUNDO JUEGO CREADO -> " + juego2.getTitulo());

            // CONSULTAR JUEGO
            JuegoDTO juegoConsultado = juegoControlador.consultarJuego(juego.getId());
            System.out.println("\nCONSULTAR JUEGO -> " + juegoConsultado.getTitulo());

            // BUSCAR JUEGO
            List<JuegoDTO> juegosRPG = juegoControlador.buscarJuego("elden", "RPG", 20.0, 100.0, ClasificacionEdad.PEGI_18, EstadoJuego.DISPONIBLE);
            System.out.println("\nBUSQUEDA DE JUEGOS RPG:");
            juegosRPG.forEach(j -> System.out.println("- " + j.getTitulo()));

            // CONSULTAR CATALOGO
            List<JuegoDTO> catalogo = juegoControlador.consultarCatalogo("precio");
            System.out.println("\nCATALOGO ORDENADO POR PRECIO:");
            catalogo.forEach(j -> System.out.println("  " + j.getTitulo() + " -> " + j.getPrecioBase() + " €"));

            // APLICAR DESCUENTO
            JuegoDTO juegoDescuento = juegoControlador.aplicarDescuento(juego.getId(), 25);
            System.out.println("\nDESCUENTO APLICADO -> " + juegoDescuento.getTitulo() + " DESCUENTO: " + juegoDescuento.getDescuentoActual() + "%");

            // CAMBIAR ESTADO DEL JUEGO 2
            JuegoDTO juegoActualizadoEstado = juegoControlador.cambiarEstado(juego2.getId(), EstadoJuego.NO_DISPONIBLE);
            System.out.println("\nESTADO CAMBIADO -> " + juegoActualizadoEstado.getTitulo() + " -> " + juegoActualizadoEstado.getEstadoJuego());

            // REALIZAR COMPRA
            CompraDTO compra = compraControlador.realizarCompra(usuario.getId(), juegoDescuento.getId(), MetodoPago.CARTERA_STEAM);
            System.out.println("\nCOMPRA REALIZADA -> ID: " + compra.getId() + " | ESTADO: " + compra.getEstadoCompra());

            // CONSULTAR DETALLES COMPRA
            CompraDTO detallesCompra = compraControlador.consultarDetallesCompra(compra.getId(), usuario.getId());
            System.out.println("\nDETALLES DE COMPRA -> USUARIO: " + detallesCompra.getUsuarioDTO().getNombreUsuario() + " | JUEGO: " + detallesCompra.getJuegoDTO().getTitulo());

            // PROCESAR PAGO
            CompraDTO compraProcesada = compraControlador.procesarPago(compra.getId());
            System.out.println("\nPAGO PROCESADO -> ESTADO COMPRA: " + compraProcesada.getEstadoCompra());

            // AÑADIR A BIBLIOTECA
            BibliotecaDTO bibliotecaNueva = bibliotecaControlador.aniadirJuegosBiblioteca(usuario.getId(), juegoDescuento.getId());
            System.out.println("\nJUEGO AÑADIDO A BIBLIOTECA -> " + bibliotecaNueva.getJuegoDTO().getTitulo());

            // ACTUALIZAR HORAS
            BibliotecaDTO tiempoActualizado = bibliotecaControlador.actualizarTiempoJuego(usuario.getId(), juegoDescuento.getId(), 25);
            System.out.println("\nHORAS ACTUALIZADAS -> TOTAL: " + tiempoActualizado.getNumHorasTotal() + " horas");

            // CONSULTAR ULTIMA SESION
            SesionInfoDTO sesion = bibliotecaControlador.consultarUltimaSesion(usuario.getId(), juegoDescuento.getId());
            System.out.println("\nULTIMA SESION -> FECHA: " + sesion.getFecha() + " | DIAS PASADOS: " + sesion.getDiasTranscurridos());

            // ====================================================================
            // FLUJO DE RESEÑAS
            ResenaForm resenaForm = new ResenaForm(
                    usuario.getId(),
                    juegoDescuento.getId(),
                    true,
                    "Obra maestra absoluta. Juegazo extremadamente bueno",
                    20d,
                    EstadoResena.PUBLICADA
            );
            ResenaDTO resena = resenaControlador.crearResena(resenaForm);
            System.out.println("\n[PASO 1] Reseña creada con ID: " + resena.getId());
            System.out.println("Antes de ocultar (Reseñas públicas del juego): " + resenaControlador.listarResenasJuego(juegoDescuento.getId()).size());

            resenaControlador.ocultarResena(resena.getId(), usuario.getId());
            System.out.println("\n--> ACCIÓN: Se ha ocultado la reseña.");

            System.out.println("Después de ocultar (Reseñas del juego - Esperado 0): " + resenaControlador.listarResenasJuego(juegoDescuento.getId()).size());
            System.out.println("Después de ocultar (Historial del usuario - Esperado 1): " + resenaControlador.listarResenasPorUsuario(usuario.getId()).size());

            ResenaDTO resenaEliminada = resenaControlador.eliminarResena(resena.getId(), usuario.getId());
            System.out.println("\n--> ACCIÓN: Se ha eliminado la reseña.");
            System.out.println("El DTO devuelto confirma el estado final: " + resenaEliminada.getEstadoResena());

            System.out.println("Después de eliminar (Historial del usuario - Esperado 0): " + resenaControlador.listarResenasPorUsuario(usuario.getId()).size());


            // LISTAR RESEÑAS JUEGO
            List<ResenaDTO> resenasJuego = resenaControlador.listarResenasJuego(juegoDescuento.getId());
            System.out.println("\nRESEÑAS DEL JUEGO:");
            resenasJuego.forEach(r -> System.out.println("  " + r.getUsuarioDTO().getNombreUsuario() + " -> " + r.getTextoResena()));

            // LISTAR RESEÑAS USUARIO
            List<ResenaDTO> resenasUsuario = resenaControlador.listarResenasPorUsuario(usuario.getId());
            System.out.println("\nRESEÑAS DEL USUARIO:");
            resenasUsuario.forEach(r -> System.out.println("  " + r.getJuegoDTO().getTitulo() + " -> Estado: " + r.getEstadoResena()));

            // ESTADISTICAS
            EstadisticasBiblioDTO estadisticasFinales = bibliotecaControlador.consultarEstadisticas(usuario.getId());
            System.out.println("\nESTADISTICAS FINALES:");
            System.out.println("  TOTAL JUEGOS: " + estadisticasFinales.getTotalJuegos());
            System.out.println("  HORAS TOTALES: " + estadisticasFinales.getHorasTotales());
            System.out.println("  JUEGO MAS JUGADO: " + estadisticasFinales.getJuegoMasJugado());

            // VER BIBLIOTECA ANTES DEL REEMBOLSO
            List<BibliotecaDTO> bibliotecaFinal = bibliotecaControlador.verBibliotecaPersonal(usuario.getId(), "alfabetico");
            System.out.println("\nBIBLIOTECA ANTES DE REEMBOLSO:");
            if (bibliotecaFinal.isEmpty()) {
                System.out.println("  SIN JUEGOS EN BIBLIOTECA");
            } else {
                bibliotecaFinal.forEach(b -> System.out.println("  - " + b.getJuegoDTO().getTitulo() + " | " + b.getEstadoInstalacion()));
            }

            // SOLICITAR REEMBOLSO
            CompraDTO compraReembolsada = compraControlador.solicitarReembolso(compra.getId());
            System.out.println("\nREEMBOLSO REALIZADO -> NUEVO ESTADO COMPRA: " + compraReembolsada.getEstadoCompra());

            // SALDO FINAL
            Double saldoFinal = usuarioControlador.consultarSaldo(usuario.getId());
            System.out.printf("\nSALDO FINAL -> %.2f €%n", saldoFinal);

            // RESUMEN FINAL
            System.out.println("\n==============================================");
            System.out.println("           RESUMEN FINAL DEL SISTEMA");
            System.out.println("==============================================");
            System.out.printf("%-25s | %-30s%n", "USUARIO", usuario.getNombreUsuario());
            System.out.printf("%-25s | %-30s%n", "EMAIL", usuario.getEmail());
            System.out.printf("%-25s | %-30s%n", "PAIS", usuario.getPais());
            System.out.printf("%-25s | %-30s%n", "JUEGO PRINCIPAL", juegoDescuento.getTitulo());
            System.out.printf("%-25s | %-30.2f%n", "PRECIO BASE", juegoDescuento.getPrecioBase());
            System.out.printf("%-25s | %-30d%n", "DESCUENTO APLICADO", juegoDescuento.getDescuentoActual());
            System.out.printf("%-25s | %-30s%n", "ESTADO TRANSACCION", compraReembolsada.getEstadoCompra());
            System.out.printf("%-25s | %-30.2f%n", "SALDO EN CARTERA", saldoFinal);
            System.out.println("==============================================");
            System.out.println("PROGRAMA FINALIZADO CORRECTAMENTE");
            System.out.println("==============================================");

        } catch (FormularioInvalidoException e) {
            System.out.println("\nERROR DE VALIDACIÓN ENCONTRADO:");
            e.getErrores().forEach(error -> {
                System.out.println("  [CAMPO]: " + error.getC() + " | [ERROR TIPO]: " + error.getM());
            });
        } catch (Exception e) {
            System.out.println("\nERROR GENERAL NO CONTROLADO:");
            e.printStackTrace();
        }
    }
}

