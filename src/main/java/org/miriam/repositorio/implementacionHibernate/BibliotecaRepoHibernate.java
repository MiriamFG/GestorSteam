package org.miriam.repositorio.implementacionHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.miriam.modelo.entidad.BibliotecaEntidad;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.EstadoCuenta;
import org.miriam.modelo.form.BibliotecaForm;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.repositorio.interfaces.IBibliotecaRepo;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.transaction.ISesionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BibliotecaRepoHibernate implements IBibliotecaRepo {

    private final ISesionManager sesionManager;

    public BibliotecaRepoHibernate(ISesionManager sesionManager){
        this.sesionManager = sesionManager;
    }

    @Override
    public void eliminarJuegoUsuario(Long usuarioId, Long juegoId) {

    }

    @Override
    public Optional<BibliotecaEntidad> crear(BibliotecaForm form) {
        var session = sesionManager.getSession();
        var biblio = new BibliotecaEntidad(-1L, form.getIdUsuario(), form.getIdJuego(), form.getFechaAdquisicion(), form.getNumHorasTotal(), form.getUltimaFechaJuego(), form.getEstadoInstalacion());
        session.persist(biblio);
        return Optional.of(biblio);
    }

    @Override
    public Optional<BibliotecaEntidad> obtenerPorId(Long id) {
        var session = sesionManager.getSession();
        var biblio = session.find(BibliotecaEntidad.class, id);
        return Optional.ofNullable(biblio);
    }

    @Override
    public List<BibliotecaEntidad> obtenerTodos() {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<BibliotecaEntidad> cq = cb.createQuery(BibliotecaEntidad.class);
        Root<BibliotecaEntidad> root = cq.from(BibliotecaEntidad.class);

        cq.select(root).orderBy(cb.asc(root.get("nombreUsuario")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Optional<BibliotecaEntidad> actualizar(Long id, BibliotecaForm form) {
        var session = sesionManager.getSession();
        var biblioOpt = this.obtenerPorId(id);

        if(biblioOpt.isEmpty()){
            return Optional.empty();
        }else {
            session.merge (new BibliotecaEntidad(id, form.getIdUsuario(), form.getIdJuego(), form.getFechaAdquisicion(), form.getNumHorasTotal(), form.getUltimaFechaJuego(), form.getEstadoInstalacion()));

            return this.obtenerPorId(id);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        var session = sesionManager.getSession();
        var userOpt = this.obtenerPorId(id);

        if(userOpt.isEmpty()){
            return false;
        }else {
            session.remove(userOpt.get());
            return true;
        }
    }
}
