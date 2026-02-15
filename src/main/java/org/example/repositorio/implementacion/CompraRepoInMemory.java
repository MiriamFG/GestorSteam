package org.example.repositorio.implementacion;

import org.example.modelo.entidad.CompraEntidad;
import org.example.modelo.form.CompraForm;
import org.example.repositorio.interfaces.ICompraRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompraRepoInMemory implements ICompraRepo {
    private static List<CompraEntidad> compras = new ArrayList<>();
    private static Long idCount = 1L;

    @Override
    public Optional<CompraEntidad> crear(CompraForm form) {
        var compra = new CompraEntidad(idCount++, form.getUsuarioDTO(), form.getJuegoDTO(), LocalDate.now(), form.getMetodoPago(), form.getPrecioSinDescuento(), form.getEstadoCompra());
        return Optional.of(compra);
    }

    @Override
    public Optional<CompraEntidad> obtenerPorId(Long id) {
        return compras.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<CompraEntidad> obtenerTodos() {
        return new ArrayList<>(compras);
    }

    @Override
    public Optional<CompraEntidad> actualizar(Long id, CompraForm form) {
        var compraOpc = obtenerPorId(id);
        if(compraOpc.isEmpty()){
            throw new IllegalArgumentException("Compra no encontrada");
        }
        var compraActualizada = new CompraEntidad(id, form.getUsuarioDTO(), form.getJuegoDTO(), compraOpc.get().getFechaCompra(), form.getMetodoPago(), form.getPrecioSinDescuento(), form.getEstadoCompra());
        compras.removeIf(c -> c.getId().equals(id));
        compras.add(compraActualizada);
        return Optional.of(compraActualizada);
    }

    @Override
    public boolean eliminar(Long id) {
        return compras.removeIf(c -> c.getId().equals(id));
    }
}
