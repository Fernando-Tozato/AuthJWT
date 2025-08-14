package inac.fernando.aulas.projetos.authlogin.authserver.controllers

import inac.fernando.aulas.projetos.authlogin.authserver.dto.LoginRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.RefreshRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.RegisterRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.TokenResponse
import inac.fernando.aulas.projetos.authlogin.authserver.dto.UserResponse
import inac.fernando.aulas.projetos.authlogin.authserver.service.AccessTokenService
import inac.fernando.aulas.projetos.authlogin.authserver.service.RefreshTokenService
import inac.fernando.aulas.projetos.authlogin.authserver.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant


@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val accessTokens: AccessTokenService,
    private val refreshTokens: RefreshTokenService
) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody req: RegisterRequest): ResponseEntity<UserResponse> {
        val res = userService.register(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): TokenResponse {
        val auth = UsernamePasswordAuthenticationToken(req.username.trim(), req.password)
        try {
            authenticationManager.authenticate(auth)
        } catch (ex: BadCredentialsException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        val (access, exp) = accessTokens.issueForUsername(req.username.trim())
        val refresh = refreshTokens.issueForUsername(req.username.trim())

        val expiresIn = Duration.between(Instant.now(), exp).seconds
        return TokenResponse(accessToken = access, expiresIn = expiresIn, refreshToken = refresh)
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody req: RefreshRequest): TokenResponse {
        val (username, newRefresh) = refreshTokens.rotate(req.refreshToken)
        val (access, exp) = accessTokens.issueForUsername(username)
        return TokenResponse(
            accessToken = access,
            expiresIn = Duration.between(Instant.now(), exp).seconds,
            refreshToken = newRefresh
        )
    }
}