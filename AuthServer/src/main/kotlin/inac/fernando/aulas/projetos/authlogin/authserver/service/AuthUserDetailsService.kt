package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.exception.NotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserDetailsService(
    private val userRepo: UserRepository,
    private val userRoleRepo: UserRoleRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepo.findByUsername(username).orElseThrow { NotFoundException("user not found") }

        val roleNames = userRoleRepo.findRoleNamesByUserId(user.id)
        val authorities = roleNames.map { SimpleGrantedAuthority(it) }

        return User(
            user.username,
            user.passwordHash,
            user.enabled && !user.locked,
            true, true, !user.locked,
            authorities
        )
    }
}