package org.miriam.repositorio.implementacionHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.miriam.modelo.entidad.CompraEntidad;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.EstadoCuenta;
import org.miriam.modelo.form.CompraForm;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.repositorio.interfaces.ICompraRepo;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.transaction.ISesionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CompraRepoHibernate implements ICompraRepo {

    private final ISesionManager sesionManager;

    public CompraRepoHibernate(ISesionManager sesionManager){
        this.sesionManager = sesionManager;
    }

    @Override
    public Optional<CompraEntidad> crear(CompraForm form) {
        var session = sesionManager.getSession();
        var compra = new CompraEntidad(-1L,form.getIdUsuario(), form.getIdJuego(), form.getFechaCompra(), form.getMetodoPago(), form.getPrecioSinDescuento(), form.getDescuentoAplicado(),form.getEstadoCompra());
        session.persist(compra);
        return Optional.of(compra);
    }

    @Override
    public Optional<CompraEntidad> obtenerPorId(Long id) {
        var session = sesionManager.getSession();
        var compra = session.find(CompraEntidad.class, id);
        return Optional.ofNullable(compra);
    }

    @Override
    public List<CompraEntidad> obtenerTodos() {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<CompraEntidad> cq = cb.createQuery(CompraEntidad.class);
        Root<CompraEntidad> root = cq.from(CompraEntidad.class);

        cq.select(root).orderBy(cb.asc(root.get("id")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Optional<CompraEntidad> actualizar(Long id, CompraForm form) {
        var session = sesionManager.getSession();
        var compraOpt = this.obtenerPorId(id);

        if(compraOpt.isEmpty()){
            return Optional.empty();
        }else {
            session.merge (new CompraEntidad(id, form.getIdUsuario(), form.getIdJuego(), form.getFechaCompra(), form.getMetodoPago(), form.getPrecioSinDescuento(), form.getDescuentoAplicado(),form.getEstadoCompra()));

            return this.obtenerPorId(id);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        var session = sesionManager.getSession();
        var compraOpt = this.obtenerPorId(id);

        if(compraOpt.isEmpty()){
            return false;
        }else {
            session.remove(compraOpt.get());
            return true;
        }
    }
}
