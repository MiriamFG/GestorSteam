package org.miriam.controlador;

import org.miriam.excepciones.FormularioInvalidoException;
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
import org.miriam.modelo.form.ErrorDto;
import org.miriam.modelo.form.ErrorTipo;
import org.miriam.repositorio.interfaces.IBibliotecaRepo;
import org.miriam.repositorio.interfaces.ICompraRepo;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.repositorio.interfaces.IUsuarioRepo;
import org.miriam.transaction.ITransactionManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class CompraControlador {
    private final ICompraRepo compraRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;

    public ITransactionManager tm;


    final int DIAS_PASADOS = 14;
    final double VALOR_CIEN = 100.00;

    public CompraControlador(ICompraRepo compraRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo, ITransactionManager tm) {
        this.compraRepo = compraRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.tm = tm;

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

        ArrayList<ErrorDto> errores = new ArrayList<>();

        if (metodo == null) {
            errores.add(new ErrorDto("metodoPago", ErrorTipo.REQUERIDO));
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }

        CompraEntidad nuevaCompra = tm.inTransaction(() -> {
            UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario).orElse(null);
            if (usuario == null) {
                errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
            }

            JuegoEntidad juego = juegoRepo.obtenerPorId(idJuego).orElse(null);
            if (juego == null) {
                errores.add(new ErrorDto("juego", ErrorTipo.NO_ENCONTRADO));
            }

            if (!errores.isEmpty()) {
                throw new FormularioInvalidoException(errores);
            }

            if (juego.getDescuentoActual() < 0 || juego.getDescuentoActual() > 100) {
                errores.add(new ErrorDto("descuentoAplicado", ErrorTipo.VALOR_INVALIDO));
            }

            if (juego.getPrecioBase() < 0) {
                errores.add(new ErrorDto("precioSinDescuento", ErrorTipo.VALOR_DEMASIADO_BAJO));
            }

            if (juego.getEstadoJuego() == EstadoJuego.NO_DISPONIBLE) {
                errores.add(new ErrorDto("juego", ErrorTipo.NO_ACTIVO));
            }

            if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
                errores.add(new ErrorDto("usuario", ErrorTipo.NO_ACTIVO));
            }

            boolean yaLoTiene = compraRepo.obtenerTodos().stream()
                    .anyMatch(c -> Objects.equals(c.getUsuarioId(), idUsuario)
                            && Objects.equals(c.getJuegoId(), idJuego)
                            && c.getEstadoCompra() == EstadoCompra.COMPLETADA);

            if (yaLoTiene) {
                errores.add(new ErrorDto("juego", ErrorTipo.EXISTENTE));
            }

            if (!errores.isEmpty()) {
                throw new FormularioInvalidoException(errores);
            }

            double precioConDescuento = juego.getPrecioBase() * (1 - (juego.getDescuentoActual() / 100.0));

            CompraForm nuevoForm = new CompraForm(
                    idUsuario,
                    idJuego,
                    LocalDate.now(),
                    metodo,
                    juego.getPrecioBase(),
                    precioConDescuento,
                    EstadoCompra.PENDIENTE
            );

            return compraRepo.crear(nuevoForm)
                    .orElseThrow(() -> {
                        ArrayList<ErrorDto> errorCreacion = new ArrayList<>();
                        errorCreacion.add(new ErrorDto("UsuarioFormulario", ErrorTipo.ERROR_CREACION));
                        return new FormularioInvalidoException(errorCreacion);
                    });
        });

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

        CompraEntidad compraActualizada = tm.inTransaction(() -> {
            ArrayList<ErrorDto> errores = new ArrayList<>();

            CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                    .orElseThrow(() -> {
                        errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });

            if (compra.getEstadoCompra() != EstadoCompra.PENDIENTE) {
                errores.add(new ErrorDto("estado", ErrorTipo.ESTADO_INVALIDO));
                throw new FormularioInvalidoException(errores);
            }

            if (compra.getMetodoPago() == MetodoPago.CARTERA_STEAM) {
                UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                        .orElseThrow(() -> {
                            errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                            return new FormularioInvalidoException(errores);
                        });

                double precioFinal = compra.getPrecioSinDescuento() * (1 - (compra.getDescuentoAplicado() / VALOR_CIEN));

                double importeADevolver = 0;
                boolean actualizado = usuarioRepo.actualizarSoloSaldo(usuario.getId(), usuario.getSaldoCartera() - precioFinal);
                if (!actualizado) {
                    errores.add(new ErrorDto("usuario saldo", ErrorTipo.NO_ACTUALIZADO));
                    throw new FormularioInvalidoException(errores);
                }
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

            return compraRepo.actualizar(compra.getId(), formActualizado)
                    .orElseThrow(() -> {
                        ArrayList<ErrorDto> err = new ArrayList<>();
                        err.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(err);
                    });

        });

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
        return tm.inTransaction(() -> {
            ArrayList<ErrorDto> errores = new ArrayList<>();

            CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                    .orElseThrow(() -> {
                        errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });
            ;
            if (!compra.getUsuarioId().equals(idUsuario)) {
                errores.add(new ErrorDto("usuario", ErrorTipo.PROHIBIDO));
                throw new FormularioInvalidoException(errores);
            }

            JuegoEntidad juego = juegoRepo.obtenerPorId(compra.getJuegoId())
                    .orElseThrow(() -> {
                        errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });

            UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                    .orElseThrow(() -> {
                        errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });


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
        });
    }

    /**
     * Procesa la solicitud de reembolso de una compra previa.
     * <p>
     * El sistema verifica que la compra exista, se encuentre en estado COMPLETADA
     * y no haya superado el límite de días permitido (14 días). Si el usuario utilizó
     * la cartera de la plataforma como método de pago, se le reintegra el importe neto exacto.
     * En todos los casos válidos, el juego asociado es retirado de la biblioteca del usuario
     * y la transacción cambia su estado a REEMBOLSADA.
     * </p>
     *
     * @param idCompra Identificador único de la transacción que se pretende reembolsar.
     * @return Un {@link CompraDTO} que refleja el nuevo estado de la transacción.
     * @throws FormularioInvalidoException Si la compra no existe, si el estado de la transacción
     *                                     no es almacenable para reembolso, si el plazo legal ha
     *                                     expirado o si ocurre un fallo de consistencia con el usuario.
     */
    public CompraDTO solicitarReembolso(Long idCompra) throws FormularioInvalidoException {
        CompraEntidad actualizada = tm.inTransaction(() -> {

            CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                    .orElseThrow(() -> {
                        ArrayList<ErrorDto> err = new ArrayList<>();
                        err.add(new ErrorDto("compraId", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(err);
                    });

            if (compra.getEstadoCompra() != EstadoCompra.COMPLETADA) {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("estadoCompra", ErrorTipo.ESTADO_INVALIDO));
                throw new FormularioInvalidoException(err);
            }

            long diasPasados = ChronoUnit.DAYS.between(compra.getFechaCompra(), LocalDate.now());
            if (diasPasados > DIAS_PASADOS) {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("fechaCompra", ErrorTipo.VALOR_INVALIDO));
                throw new FormularioInvalidoException(err);
            }

            if (compra.getMetodoPago() == MetodoPago.CARTERA_STEAM) {
                UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                        .orElseThrow(() -> {
                            ArrayList<ErrorDto> err = new ArrayList<>();
                            err.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                            return new FormularioInvalidoException(err);
                        });

                double importeADevolver =
                        compra.getPrecioSinDescuento() * (1 - (compra.getDescuentoAplicado() / VALOR_CIEN));

                usuario.ingresarSaldo(importeADevolver);
                usuarioRepo.actualizar(usuario);
            }

            bibliotecaRepo.obtenerTodos().stream()
                    .filter(b -> Objects.equals(b.getUsuarioId(), compra.getUsuarioId())
                            && Objects.equals(b.getJuegoId(), compra.getJuegoId()))
                    .findFirst()
                    .ifPresent(b -> bibliotecaRepo.eliminar(b.getId()));

            var formReembolso = new CompraForm(
                    compra.getUsuarioId(),
                    compra.getJuegoId(),
                    compra.getFechaCompra(),
                    compra.getMetodoPago(),
                    compra.getPrecioSinDescuento(),
                    compra.getDescuentoAplicado(),
                    EstadoCompra.REEMBOLSADA);


            return compraRepo.actualizar(idCompra, formReembolso)
                    .orElseThrow(() -> {
                        ArrayList<ErrorDto> err = new ArrayList<>();
                        err.add(new ErrorDto("compra", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(err);
                    });
        });

        return CompraMapper.paraDTO(actualizada);
    }
}
