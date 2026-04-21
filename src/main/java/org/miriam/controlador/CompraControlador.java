package org.miriam.controlador;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.mapper.BibliotecaMapper;
import org.miriam.mapper.CompraMapper;
import org.miriam.mapper.JuegoMapper;
import org.miriam.mapper.UsuarioMapper;
import org.miriam.modelo.dto.CompraDTO;
import org.miriam.modelo.dto.JuegoDTO;
import org.miriam.modelo.dto.UsuarioDTO;
import org.miriam.modelo.entidad.CompraEntidad;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.*;
import org.miriam.modelo.form.CompraForm;
import org.miriam.modelo.form.ErrorDTO;
import org.miriam.modelo.form.ErrorTipo;
import org.miriam.repositorio.implementacion.*;
import org.miriam.repositorio.interfaces.IBibliotecaRepo;
import org.miriam.repositorio.interfaces.ICompraRepo;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CompraControlador {
    private final ICompraRepo compraRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;

    final int DIAS_PASADOS = 14;
    final double VALOR_CIEN = 100.00;
    public CompraControlador(ICompraRepo compraRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo) {
        this.compraRepo = compraRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
    }


    /**
     * Inicia el proceso de compra de un juego por parte del usuario.
     * <p>
     * Validaciones:
     * verifica existencia y estado activo de la cuenta del usuario,
     * comprueba que usuario no posea una copua completada del juego,
     * calcula precio final aplicando descuentos vigentes,
     * valida si existe saldo suficiente en caso de que el pago se haga mediante la cartera del sistema.
     *
     * @param idUsuario Identificador del comprador.
     * @param idJuego   Identificador del juego a adquirir.
     * @param metodo    el MetodoPago elegido para la transacción.
     * @return El ID de la transacción de compra recién creada en estado PENDIENTE.
     * @throws FormularioInvalidoException Si la cuenta no está activa, el juego ya es propiedad
     *                                     del usuario o el saldo es insuficiente.
     * @throws IllegalArgumentException    Si el usuario o el juego no existen.
     */
    public CompraDTO realizarCompra(Long idUsuario, Long idJuego, MetodoPago metodo) throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (metodo == null) {
            errores.add(new ErrorDTO("metodoPago", ErrorTipo.REQUERIDO));
        }

        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario)
                .orElseThrow(() -> {
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        JuegoEntidad juego = juegoRepo.obtenerPorId(idJuego)
                .orElseThrow(() -> {
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        if (juego.getDescuentoActual() < 0) {
            errores.add(new ErrorDTO("descuento", ErrorTipo.FORMATO_INVALIDO));
        }

        if (juego.getPrecioBase() < 0) {
            errores.add(new ErrorDTO("precio", ErrorTipo.FORMATO_INVALIDO));
        }

        if (juego.getEstadoJuego() == EstadoJuego.NO_DISPONIBLE) {
            errores.add(new ErrorDTO("juego", ErrorTipo.NO_ACTIVO));
        }

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ACTIVO));
        }

        boolean yaLoTiene = compraRepo.obtenerTodos().stream()
                .anyMatch(c -> java.util.Objects.equals(c.getUsuarioId(), idUsuario)
                        && java.util.Objects.equals(c.getJuegoId(), idJuego)
                        && c.getEstadoCompra() == EstadoCompra.COMPLETADA);

        if (yaLoTiene) {
            errores.add(new ErrorDTO("juego", ErrorTipo.EXISTENTE));
        }

        CompraForm form = new CompraForm(idUsuario, idJuego, LocalDate.now(), metodo, juego.getPrecioBase(), juego.getDescuentoActual(), EstadoCompra.PENDIENTE);
        CompraEntidad nuevaCompra = compraRepo.crear(form)
                .orElseThrow(() -> {
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        if (!errores.isEmpty()) throw new FormularioInvalidoException(errores);

        return CompraMapper.paraDTO(nuevaCompra);

    }




    /**
     * Finaliza proceso de compra, gestina el cobro y le da el juego al usuario.
     * <p>
     * Acciones
     * verifica que la compra está en estado PENDIENTE para evitar cobros duplicados,
     * si el pago es mediante la cartera de sistema, calcula el precio final con descuentos y deduce el importe del saldo del usuario,
     * actualiza el estasdo de la compra a COMPELTADA,
     * registra el juego en la biblioteca del usuario.
     *
     * @param idCompra Identificador único de la transacción pendiente de procesar.
     * @return Un CompraDTO que contiene los datos actualizados de la transacción.
     * @throws FormularioInvalidoException Si ocurre un error en la validación de los datos
     *                                     al actualizar el registro de compra.
     * @throws IllegalArgumentException    Si la compra o el usuario asociado no existen.
     * @throws IllegalStateException       Si la compra ya ha sido procesada o cancelada anteriormente.
     */
    public CompraDTO procesarPago(Long idCompra) throws FormularioInvalidoException {

        CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        if (compra.getEstadoCompra() != EstadoCompra.PENDIENTE) {
            throw new IllegalStateException("La compra ya ha sido procesada o cancelada");
        }

        if (compra.getMetodoPago() == MetodoPago.CARTERA_STEAM) {
            UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                    .orElseThrow(() -> {
                        ArrayList<ErrorDTO> errores = new ArrayList<>();
                        errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });

            double precioConDescuento = compra.getPrecioSinDescuento() * (1 - (compra.getDescuentoAplicado() / 100.0));

            usuarioRepo.actualizarSoloSaldo(usuario.getId(), usuario.getSaldoCartera() - precioConDescuento);
        }

        CompraForm formActualizado = new CompraForm(
                compra.getUsuarioId(),
                compra.getJuegoId(),
                compra.getFechaCompra(),
                compra.getMetodoPago(),
                compra.getPrecioSinDescuento(),
                compra.getDescuentoAplicado(),
                EstadoCompra.COMPLETADA
        );

        CompraEntidad compraActualizada = compraRepo.actualizar(compra.getId(), formActualizado)
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

       // bibliotecaControlador.aniadirJuegosBiblioteca(compra.getUsuarioId(), compra.getJuegoId());

        return CompraMapper.paraDTO(compraActualizada);

    }


    /**
     * Recuoera la informacion detallada de la compra de un usuario.
     * <p>
     * Verifica que la comprae existe,
     * valida que el idUsuario coincida con el propietario de la compra.
     *
     * @param idCompra  Identificador único de la compra a consultar.
     * @param idUsuario Identificador del usuario que solicita la información.
     * @return un CompraDTO con la informacion del usuario y del juego.
     * @throws IllegalArgumentException Si la compra no existe, si el juego/usuario asociado
     *                                  se han eliminado, o si el usuario no tiene permisos para ver esta compra.
     *
     */
    public CompraDTO consultarDetallesCompra(Long idCompra, Long idUsuario) throws FormularioInvalidoException {
        CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });;

        if (!compra.getUsuarioId().equals(idUsuario)) {
            ArrayList<ErrorDTO> errores = new ArrayList<>();
            errores.add(new ErrorDTO("usuario", ErrorTipo.PROHIBIDO));
            throw new FormularioInvalidoException(errores);
        }

        JuegoEntidad juego = juegoRepo.obtenerPorId(compra.getJuegoId())
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });;

        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });;

        UsuarioDTO usuarioDTO = UsuarioMapper.paraDTO(usuario);
        JuegoDTO juegoDTO = JuegoMapper.paraDTO(juego);

        return new CompraDTO(
                compra.getId(),
                usuarioDTO,
                juegoDTO,
                compra.getFechaCompra(),
                compra.getMetodoPago(),
                compra.getPrecioSinDescuento(),
                compra.getDescuentoAplicado(),
                compra.getEstadoCompra()
        );
    }

    /**
     * Procesa la devulucion de una compra.
     * <p>
     * Validaciones:
     * verifica que no hayan pasado más de 14 días desde la fecha de compra,
     * si el pago fue mediante cartera, calcula el importe real pagado (aplicando descuento original) y lo reintegra al saldo del usuario,
     * el acceso al juego se elimina de la biblioteca del usuario,
     * actualiza el registro de la comrpa al estado REEMBOLSADO.
     *
     * @param idCompra Identificador de la transacción a reembolsar.
     * @param motivo   Razón proporcionada por el usuario.
     * @return un CompraDTO que refleja el nuevo estado de la transacción.
     * @throws IllegalArgumentException Si la compra o el usuario asociado no existen.
     * @throws IllegalStateException    Si el plazo de reembolso ha expirado o la compra
     *                                  ya estaba reembolsada/cancelada.
     */
    public CompraDTO solicitarReembolso(Long idCompra, String motivo) throws FormularioInvalidoException {


        CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        long diasPasados = ChronoUnit.DAYS.between(compra.getFechaCompra(), LocalDate.now());
        if (diasPasados > DIAS_PASADOS) {
            throw new IllegalStateException("Plazo de reembolso expirado (máximo 14 días)");
        }

        if (compra.getMetodoPago() == MetodoPago.CARTERA_STEAM) {
            UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                    .orElseThrow(() -> {
                        ArrayList<ErrorDTO> errores = new ArrayList<>();
                        errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });;

            double aDevolver = compra.getPrecioSinDescuento() * (1 - (compra.getDescuentoAplicado() / VALOR_CIEN));
            usuarioRepo.actualizarSoloSaldo(usuario.getId(), usuario.getSaldoCartera() + aDevolver);
        }

        bibliotecaRepo.eliminarJuegoUsuario(compra.getUsuarioId(), compra.getJuegoId());

        var formReembolso = new CompraForm(
                compra.getUsuarioId(),
                compra.getJuegoId(),
                compra.getFechaCompra(),
                compra.getMetodoPago(),
                compra.getPrecioSinDescuento(),
                compra.getDescuentoAplicado(),
                EstadoCompra.REEMBOLSADA);

        CompraEntidad actualizada = compraRepo.actualizar(idCompra, formReembolso)
                .orElseThrow(() -> {
                    ArrayList<ErrorDTO> errores = new ArrayList<>();
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        return CompraMapper.paraDTO(actualizada);
    }

}
