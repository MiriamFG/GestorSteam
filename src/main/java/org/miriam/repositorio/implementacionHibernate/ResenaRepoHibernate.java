package org.miriam.repositorio.implementacionHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.ResenaEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.EstadoCuenta;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.modelo.form.ResenaForm;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.repositorio.interfaces.IResenaRepo;
import org.miriam.transaction.ISesionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ResenaRepoHibernate implements IResenaRepo {

    private final ISesionManager sesionManager;

    public ResenaRepoHibernate(ISesionManager sesionManager){
        this.sesionManager = sesionManager;
    }

    @Override
    public Optional<ResenaEntidad> crear(ResenaForm form) {
        var session = sesionManager.getSession();
        var resena = new ResenaEntidad(-1L, form.getIdUsuario(), form.getIdJuego(), form.getRecomendado(), form.getTextoResena(), form.getHorasJuegoResena(), LocalDate.now(), LocalDate.now(), form.getEstado());
        session.persist(resena);
        return Optional.of(resena);
    }

    @Override
    public Optional<ResenaEntidad> obtenerPorId(Long id) {
        var session = sesionManager.getSession();
        var resena = session.find(ResenaEntidad.class, id);
        return Optional.ofNullable(resena);
    }

    @Override
    public List<ResenaEntidad> obtenerTodos() {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<ResenaEntidad> cq = cb.createQuery(ResenaEntidad.class);
        Root<ResenaEntidad> root = cq.from(ResenaEntidad.class);

        cq.select(root).orderBy(cb.asc(root.get("id")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Optional<ResenaEntidad> actualizar(Long id, ResenaForm form) {
        var session = sesionManager.getSession();
        var resenaOpt = this.obtenerPorId(id);

        if(resenaOpt.isEmpty()){
            return Optional.empty();
        }else {
            var resenaExistente = resenaOpt.get();
            var resenaActualizada = new ResenaEntidad(
                    id,
                    form.getIdUsuario(),
                    form.getIdJuego(),
                    form.getRecomendado(),
                    form.getTextoResena(),
                    form.getHorasJuegoResena(),
                    resenaExistente.getFechaPubli(),
                    LocalDate.now(),
                    form.getEstado()
            );
            session.merge(resenaActualizada);

            return this.obtenerPorId(id);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        var session = sesionManager.getSession();
        var resenaOpt = this.obtenerPorId(id);

        if(resenaOpt.isEmpty()){
            return false;
        }else {
            session.remove(resenaOpt.get());
            return true;
        }
    }
}
