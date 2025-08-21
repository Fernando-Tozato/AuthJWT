package inac.fernando.aulas.projetos.authlogin.authserver.domain.specification

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.Role
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

object UserSpecs {
    fun notDeleted(): Specification<User> = Specification { root, _, cb ->
        cb.isNull(root.get<Instant>("deletedAt"))
    }

    fun usernameContains(q: String?): Specification<User>? =
        if (q.isNullOrBlank()) null else Specification { root, _, cb ->
            cb.like(cb.lower(root.get("username")), "%" + q.lowercase() + "%")
        }

    fun emailContains(q: String?): Specification<User>? =
        if (q.isNullOrBlank()) null else Specification { root, _, cb ->
            cb.like(cb.lower(root.get("email")), "%" + q.lowercase() + "%")
        }

    fun hasRole(roleName: String?): Specification<User>? =
        if (roleName.isNullOrBlank()) null else Specification { root, _, cb ->
            val roles = root.join<Any, Any>("roles")
            cb.equal(roles.get<String>("name"), roleName)
        }

    fun hasEnabled(v: Boolean?): Specification<User>? =
        v?.let { Specification<User> { root, _, cb -> cb.equal(root.get<Boolean>("enabled"), it) } }

    fun hasLocked(v: Boolean?): Specification<User>? =
        v?.let { Specification<User> { root, _, cb -> cb.equal(root.get<Boolean>("locked"), it) } }
}