package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.CompraMapper;
import org.example.mapper.JuegoMapper;
import org.example.mapper.UsuarioMapper;
import org.example.modelo.dto.CompraDTO;
import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.entidad.CompraEntidad;
import org.example.modelo.entidad.JuegoEntidad;
import org.example.modelo.entidad.UsuarioEntidad;
import org.example.modelo.enums.EstadoCompra;
import org.example.modelo.enums.EstadoCuenta;
import org.example.modelo.enums.MetodoPago;
import org.example.modelo.form.CompraForm;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.repositorio.interfaces.IBibliotecaRepo;
import org.example.repositorio.interfaces.ICompraRepo;
import org.example.repositorio.interfaces.IJuegoRepo;
import org.example.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CompraControlador {
    private final ICompraRepo compraRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;
    private final BibliotecaControlador bibliotecaControlador;

    public CompraControlador(ICompraRepo compraRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo, BibliotecaControlador bibliotecaControlador) {
        this.compraRepo = compraRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.bibliotecaControlador = bibliotecaControlador;
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
    public Long realizarCompra(Long idUsuario, Long idJuego, MetodoPago metodo) throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        JuegoEntidad juego = juegoRepo.obtenerPorId(idJuego)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ACTIVO));
        }

        for (CompraEntidad c : compraRepo.obtenerTodos()) {
            if (c.getUsuarioId().equals(idUsuario) && c.getJuegoId().equals(idJuego) && c.getEstadoCompra() == EstadoCompra.COMPLETADA) {
                errores.add(new ErrorDTO("juego", ErrorTipo.EXISTENTE));
            }
        }

        double descuentoEuros = (juego.getPrecioBase() * juego.getDescuentoActual());
        double precioFinal = juego.getPrecioBase() - descuentoEuros;

        if (metodo == MetodoPago.CARTERA_STEAM && usuario.getSaldoCartera() < precioFinal) {
            errores.add(new ErrorDTO("saldo", ErrorTipo.SALDO_INSUFICIENTE));
        }

        if (!errores.isEmpty()) throw new FormularioInvalidoException(errores);

        CompraForm form = new CompraForm(idUsuario, idJuego, LocalDate.now(), metodo, juego.getPrecioBase(), juego.getDescuentoActual(), EstadoCompra.PENDIENTE);
        CompraEntidad nuevaCompra = compraRepo.crear(form)
                .orElseThrow(() -> new RuntimeException("Error al registar compra"));

        return nuevaCompra.getId();

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
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));

        if (compra.getEstadoCompra() != EstadoCompra.PENDIENTE) {
            throw new IllegalStateException("La compra ya ha sido procesada o cancelada");
        }

        if (compra.getMetodoPago() == MetodoPago.CARTERA_STEAM) {
            UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Uusario no encontrado"));
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
                .orElseThrow(() -> new RuntimeException("Error al actualizar el estado de la compra"));

        bibliotecaControlador.aniadirJuegosBiblioteca(compra.getUsuarioId(), compra.getJuegoId());

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
    public CompraDTO consultarDetallesCompra(Long idCompra, Long idUsuario) {
        CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));

        if (!compra.getUsuarioId().equals(idUsuario)) {
            throw new IllegalArgumentException("No tienes permiso para ver esta compra");
        }

        JuegoEntidad juego = juegoRepo.obtenerPorId(compra.getJuegoId())
                .orElseThrow(() -> new IllegalArgumentException("El juego ya no existe en el catálogo"));

        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario ya no existe"));

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
    public CompraDTO solicitarReembolso(Long idCompra, String motivo) {
        final int DIAS_PASADOS = 14;
        final double VALOR_CIEN = 100.00;

        CompraEntidad compra = compraRepo.obtenerPorId(idCompra)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));

        long diasPasados = ChronoUnit.DAYS.between(compra.getFechaCompra(), LocalDate.now());
        if (diasPasados > DIAS_PASADOS) {
            throw new IllegalStateException("Plazo de reembolso expirado (máximo 14 días)");
        }

        if (compra.getMetodoPago() == MetodoPago.CARTERA_STEAM) {
            UsuarioEntidad usuario = usuarioRepo.obtenerPorId(compra.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

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
                .orElseThrow(() -> new RuntimeException("Error al actualizar registro"));

        return CompraMapper.paraDTO(actualizada);
    }

}
