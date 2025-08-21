package inac.fernando.aulas.projetos.authlogin.authserver.controllers

import inac.fernando.aulas.projetos.authlogin.authserver.dto.*
import inac.fernando.aulas.projetos.authlogin.authserver.service.AccessTokenService
import inac.fernando.aulas.projetos.authlogin.authserver.service.RefreshTokenService
import inac.fernando.aulas.projetos.authlogin.authserver.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<TokenResponse> {
        val auth = UsernamePasswordAuthenticationToken(req.username.trim(), req.password)

        authenticationManager.authenticate(auth)

        val (access, exp) = accessTokens.issueForUsername(req.username.trim())
        val refresh = refreshTokens.issueForUsername(req.username.trim())

        val expiresIn = Duration.between(Instant.now(), exp).seconds

        val res = TokenResponse(
            accessToken = access,
            expiresIn = expiresIn,
            refreshToken = refresh
        )
        return ResponseEntity.ok(res)
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody req: RefreshRequest): ResponseEntity<TokenResponse> {
        val (username, newRefresh) = refreshTokens.rotate(req.refreshToken)
        val (access, exp) = accessTokens.issueForUsername(username)

        val expiresIn = Duration.between(Instant.now(), exp).seconds

        val res = TokenResponse(
            accessToken = access,
            expiresIn = expiresIn,
            refreshToken = newRefresh
        )
        return ResponseEntity.ok(res)
    }
}