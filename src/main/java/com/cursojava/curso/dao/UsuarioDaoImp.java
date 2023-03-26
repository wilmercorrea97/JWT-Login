package com.cursojava.curso.dao;

import com.cursojava.curso.models.Usuario;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository // Hace referencia a la conexión a la base de datos

@Transactional// Le da la funcionalidad a la clase de armar las consultas a la base de datos


// Se crea esta clase para implementar la interfaz UsuarioDao
public class UsuarioDaoImp implements UsuarioDao {

    @PersistenceContext //
    private EntityManager entityManager; // EntityManager nos ayudara a realizar la conexión a la base de datos

    @Override
    public List<Usuario> getUsuarios() {
        String query = "FROM Usuario";
        return entityManager.createQuery(query).getResultList(); // Ejecutamos la consulta y obtenemos el resultado en una lista

    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = entityManager.find(Usuario.class, id); // Buscamos el usuario en la clase por su campo ID
        entityManager.remove(usuario); // Eliminamos el usuario
    }

    @Override
    public void registrar(Usuario usuario) {
        entityManager.merge(usuario);
    }

    @Override
    public Usuario obtenerUsuarioPorCredenciales( Usuario usuario ){
        String query = "FROM Usuario WHERE email = :email";
        List<Usuario> lista = entityManager.createQuery(query)
                .setParameter("email", usuario.getEmail())
                .getResultList();

        if (lista.isEmpty()){
            return null;
        }


        String passwordHashed = lista.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (argon2.verify(passwordHashed, usuario.getPassword())){
            return lista.get(0);
        }
        return null;
    }
}
