package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService(
    private val userRepo: UserRepository,
    private val userRoleRepo: UserRoleRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepo.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User '$username' not found") }

        val roleNames = userRoleRepo.findRoleNamesByUserId(user.id)
        val authorities = roleNames.map { SimpleGrantedAuthority(it) }

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.passwordHash,
            user.enabled && !user.locked,
            true, true, !user.locked,
            authorities
        )
    }
}