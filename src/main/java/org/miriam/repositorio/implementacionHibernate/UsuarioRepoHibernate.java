package org.miriam.repositorio.implementacionHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.EstadoCuenta;
import org.miriam.modelo.form.JuegoForm;
import org.miriam.modelo.form.UsuarioForm;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.repositorio.interfaces.IUsuarioRepo;
import org.miriam.transaction.ISesionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UsuarioRepoHibernate implements IUsuarioRepo {

    private final ISesionManager sesionManager;

    public UsuarioRepoHibernate(ISesionManager sesionManager){
        this.sesionManager = sesionManager;
    }


    @Override
    public Optional<UsuarioEntidad> obtenerPorNombre(String nombreUsuario) {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UsuarioEntidad> cq = cb.createQuery(UsuarioEntidad.class);
        Root<UsuarioEntidad> root = cq.from(UsuarioEntidad.class);

        cq.select(root).where(cb.equal(root.get("nombreUsuario"), nombreUsuario));

        return session.createQuery(cq).getResultStream().findFirst();
    }

    @Override
    public void actualizarSoloSaldo(Long id, Double nuevoSaldo) {
        var session = sesionManager.getSession();

        String hql = "UPDATE UsuarioEntidad u SET u.saldoCartera = :nuevoSaldo WHERE u.id = :id";

        session.createMutationQuery(hql)
                .setParameter("nuevoSaldo", nuevoSaldo)
                .setParameter("id", id)
                .executeUpdate();

    }

    @Override
    public Optional<UsuarioEntidad> crear(UsuarioForm form) {
        var session = sesionManager.getSession();
        var user = new UsuarioEntidad(-1L, form.getNombreUsuario(), form.getEmail(), form.getContrasena(),
                form.getNombreReal(), form.getPais(), form.getFechaNac(), LocalDateTime.now(), form.getAvatar(), 0.0, EstadoCuenta.ACTIVA);
        session.persist(user);
        return Optional.of(user);
    }

    @Override
    public Optional<UsuarioEntidad> obtenerPorId(Long id) {
        var session = sesionManager.getSession();
        var user = session.find(UsuarioEntidad.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UsuarioEntidad> obtenerPorEmail(String email) {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UsuarioEntidad> cq = cb.createQuery(UsuarioEntidad.class);
        Root<UsuarioEntidad> root = cq.from(UsuarioEntidad.class);

        cq.select(root).where(cb.equal(root.get("email"), email));

        var resultados = session.createQuery(cq).getResultList();

        if (resultados.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultados.get(0));
        }
    }

    @Override
    public List<UsuarioEntidad> obtenerTodos() {
        var session = sesionManager.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<UsuarioEntidad> cq = cb.createQuery(UsuarioEntidad.class);
        Root<UsuarioEntidad> root = cq.from(UsuarioEntidad.class);

        cq.select(root).orderBy(cb.asc(root.get("nombreUsuario")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Optional<UsuarioEntidad> actualizar(Long id, UsuarioForm form) {
        var session = sesionManager.getSession();
        var userOpt = this.obtenerPorId(id);

        if(userOpt.isEmpty()){
            return Optional.empty();
        }else {
            UsuarioEntidad usuarioExistente = userOpt.get();

            var user = new UsuarioEntidad(
                    id,
                    form.getNombreUsuario(),
                    form.getEmail(),
                    form.getContrasena(),
                    form.getNombreReal(),
                    form.getPais(),
                    form.getFechaNac(),
                    usuarioExistente.getFechaReg(),
                    form.getAvatar(),
                    usuarioExistente.getSaldoCartera(),
                    usuarioExistente.getEstadoCuenta()

            );

            session.merge (user);

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
