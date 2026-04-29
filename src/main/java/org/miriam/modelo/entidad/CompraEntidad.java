package org.miriam.modelo.entidad;

import jakarta.persistence.*;
import org.miriam.modelo.enums.EstadoCompra;
import org.miriam.modelo.enums.MetodoPago;

import java.time.LocalDate;
@Table(name = "compra")
@Entity

public class CompraEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id")
    private Long usuarioId;
    @Column(name = "juegi_id")
    private Long juegoId;
    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;
    @Column(name = "precio_sin_descuento")
    private Double precioSinDescuento;
    @Column(name = "descuento_aplicado")
    private Integer descuentoAplicado;
    @Column(name = "estado_compra")
    private EstadoCompra estadoCompra;

    public CompraEntidad(Long id, Long usuarioId, Long juegoId, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, EstadoCompra estadoCompra) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.juegoId = juegoId;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.descuentoAplicado = 0;
        this.estadoCompra = estadoCompra;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public Double getPrecioSinDescuento() {
        return precioSinDescuento;
    }

    public Integer getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public EstadoCompra getEstadoCompra() {
        return estadoCompra;
    }
}
