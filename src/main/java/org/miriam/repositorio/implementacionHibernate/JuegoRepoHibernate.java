package org.miriam.repositorio.implementacionHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.miriam.HibernateUtil;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.enums.EstadoJuego;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.transaction.ISesionManager;

import java.util.List;
import java.util.Optional;

public class JuegoRepoHibernate implements IJuegoRepo {

    private final ISesionManager sesionManager;

    public JuegoRepoHibernate(ISesionManager sesionManager){
        this.sesionManager = sesionManager;
    }
    @Override
    public Optional<JuegoEntidad> obtenerPorTitulo(String titulo) {
      var session = sesionManager.getSession();
      CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<JuegoEntidad> cq = cb.createQuery(JuegoEntidad.class);
        Root<JuegoEntidad> root = cq.from(JuegoEntidad.class);

        cq.select(root).where(cb.equal(root.get("titulo"), titulo));

        return session.createQuery(cq).getResultStream().findFirst();

    }

    @Override
    public Optional<JuegoEntidad> crear(JuegoForm form) {
        var session = sesionManager.getSession();
        var juego = new JuegoEntidad(-1L, form.getTitulo(), form.getDescipcion(),
                form.getDesarrollador(), form.getFechaLanz(),
                form.getPrecioBase(), form.getDescuentoActual(),
                form.getCategoria(), form.getClasificacionEdad(),form.getIdiomasDisponibles(), form.getEstadoJuego());
        session.persist(juego);
        return Optional.of(juego);

    }

    @Override
    public Optional<JuegoEntidad> obtenerPorId(Long id) {
        var session = sesionManager.getSession();
        var juego = session.find(JuegoEntidad.class, id);
        return Optional.ofNullable(juego);
    }

    @Override
    public List<JuegoEntidad> obtenerTodos() {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<JuegoEntidad> cq = cb.createQuery(JuegoEntidad.class);
        Root<JuegoEntidad> root = cq.from(JuegoEntidad.class);

        cq.select(root).orderBy(cb.asc(root.get("nombre")));

        return session.createQuery(cq).getResultList();

    }

    @Override
    public Optional<JuegoEntidad> actualizar(Long id, JuegoForm form) {
        var session = sesionManager.getSession();
        var juegoOpt = this.obtenerPorId(id);

        if(juegoOpt.isEmpty()){
            return Optional.empty();
        }else {
            session.merge (new JuegoEntidad(id, form.getTitulo(), form.getDescipcion(), form.getDesarrollador(),
                    form.getFechaLanz(), form.getPrecioBase(), form.getDescuentoActual(),
                    form.getCategoria(),form.getClasificacionEdad(),  form.getIdiomasDisponibles(), form.getEstadoJuego()));

            return this.obtenerPorId(id);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        var session = sesionManager.getSession();
        var juegoOpt = this.obtenerPorId(id);

        if(juegoOpt.isEmpty()){
            return false;
        }else {
            session.remove(juegoOpt.get());
            return true;
        }

    }
}
