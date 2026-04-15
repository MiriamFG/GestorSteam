package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.UsuarioMapper;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.entidad.UsuarioEntidad;
import org.example.modelo.enums.EstadoCuenta;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.modelo.form.UsuarioForm;
import org.example.repositorio.implementacion.PaisesRepoInMemory;
import org.example.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class UsuarioControlador {

    private final IUsuarioRepo usuarioRepo;
    private PaisesRepoInMemory paisRepo = new PaisesRepoInMemory();


    public UsuarioControlador(IUsuarioRepo usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }


    /**
     * Registrar un nuevo usuario en el sistema tras realizar validaciones de seguridad y negocio.
     * <p>
     * Ejecucion
     * ejecuta la validacion interna de UsuarioFrom (formatos, campos obligatorios),
     * verifica que el pais existe en el repositoriod e paises permitidos,
     * comprueba que la fechad e nacimiento no sea futura,
     * valida que ni el nombre ni el email del usuario estén ya registrados en el sistema.
     *
     * @param form objeto de UsuarioForm que contiene los datos del nuevo usuario.
     * @return UsuarioDTO con la información del usuario recién creado y el ID asignado.
     * @throws FormularioInvalidoException si ocurre cualquiera de estos errores:
     *                                     ErrorTipo.NO_ENCONTRADO: El país no es válido,
     *                                     ErrorTipo.FECHA_FUTURA: La fecha de nacimiento es posterior a hoy,
     *                                     ErrorTipo.EXISTENTE: El nombre de usuario ya está en uso,
     *                                     ErrorTipo.REGISTRADO: El email ya está vinculado a otra cuenta,
     * @throws IllegalArgumentException    Si el repositorio no crea al usuario correctamente.
     *
     */
    public UsuarioDTO registrarUsuario(UsuarioForm form) throws FormularioInvalidoException {

        form.validarFormulario();

        var errores = new ArrayList<ErrorDTO>();

        boolean paisValido = paisRepo.obtenerTodos().stream().anyMatch(p -> p.equalsIgnoreCase(form.getPais()));
        if (!paisValido) {
            errores.add(new ErrorDTO("pais", ErrorTipo.NO_ENCONTRADO));
        }

        if (form.getFechaNac().isAfter(LocalDate.now())) {
            errores.add(new ErrorDTO("fechaNac", ErrorTipo.FECHA_FUTURA));
        }

        for (UsuarioEntidad usuario : usuarioRepo.obtenerTodos()) {

            if (usuario.getNombreUsuario().equalsIgnoreCase(form.getNombreUsuario())) {
                errores.add(new ErrorDTO("usuario", ErrorTipo.EXISTENTE));
            }

            if (usuario.getEmail().equalsIgnoreCase(form.getEmail())) {
                errores.add(new ErrorDTO("email", ErrorTipo.REGISTRADO));
            }

            if (!errores.isEmpty()) {
                throw new FormularioInvalidoException(errores);
            }
        }

        UsuarioEntidad nuevoUsuario = usuarioRepo.crear(form)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no creado"));

        return UsuarioMapper.paraDTO(nuevoUsuario);
    }

    /**
     * Busca y recupera la informacion compelta de un usuario a partir de su nombre de usuario.
     *
     * @param nombreUsuario El nombre único (username) del usuario que se desea buscar.
     * @return el UsuarioEntidad correspondiente al nombre indicado.
     * @throws IllegalArgumentException si no existe ningun usuario con ese nombre en el repositorio.
     */
    public UsuarioEntidad consultarPerfilPorNombre(String nombreUsuario) {
        return usuarioRepo.obtenerPorNombre(nombreUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    /**
     * Incrementa el saldo de la cartera del usuario tras validar limites y el estado de la cuenta.
     * <p>
     * Reglas de negocio:
     * la cuenta del usuario debe estar en estado ACTIVA,
     * la cantiuidad debe ser positiva y estar entre 5.00€ y 500.00€,
     * no se permiten más de 2 decimales,
     *
     * @param idUsuario Identificador único del usuario que recibe la recarga.
     * @param cantidad  cantidad  Importe a añadir a la cuenta (tipo Double).
     * @return El nuevo saldo total.
     * @throws IllegalArgumentException Si el usuario no existe,
     *                                  el estado de la cuenta no es activo o la precisión de decimales es incorrecta.
     */

    final int VALOR_ZERO = 0;
    final int DOS_DECIMALES = 2;
    final double VALOR_CINCO = 5.00;
    final double VALOR_CIEN = 100.00;
    final double VALOR_QUINIENTOS = 500.00;

    public Double aniadirSaldo(Long idUsuario, Double cantidad) {
        var errores = new ArrayList<ErrorDTO>();



        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            errores.add(new ErrorDTO("cuenta", ErrorTipo.NO_ACTIVO));
        }

        if (cantidad <= VALOR_ZERO) {
            errores.add(new ErrorDTO("saldo", ErrorTipo.VALOR_DEMASIADO_BAJO));
        }

        if (cantidad < VALOR_CINCO || cantidad > VALOR_QUINIENTOS) {
            errores.add(new ErrorDTO("saldo", ErrorTipo.LONGITUD_INVALIDA, VALOR_CINCO, VALOR_QUINIENTOS));
        }

        if (Math.round(cantidad * VALOR_CIEN) / VALOR_CIEN != cantidad) {
            errores.add(new ErrorDTO("saldo", ErrorTipo.MAX_DECIMALES, DOS_DECIMALES));
        }

        Double nuevoSaldo = usuario.getSaldoCartera() + cantidad;

        Optional<Double> saldoOp = Optional.of(nuevoSaldo);

        UsuarioForm formFicticio = new UsuarioForm(
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getContrasena(),
                usuario.getNombreReal(),
                usuario.getPais(),
                usuario.getFechaNac(),
                usuario.getAvatar()
        );

        usuarioRepo.actualizar(idUsuario, formFicticio);

        return nuevoSaldo;
    }

    /**
     * Obtiene el saldo actual de la cartera del usuario.
     * Se obtiene la infomación del usuario y se transforma el valor numerico en una cadena de texto con fomato de moneda europea(2 decimales y simbolo €).
     *
     * @param idUsuario Identificador único del usuario cuyo saldo se desea consultar.
     * @return Una String que representa el saldo formateado.
     * @throws IllegalArgumentException Si el ID proporcionado no corresponde a ningún usuario existente.
     */
    public String consultarSaldo(Long idUsuario) {
        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return String.format("%.2f €", usuario.getSaldoCartera());
    }
}
