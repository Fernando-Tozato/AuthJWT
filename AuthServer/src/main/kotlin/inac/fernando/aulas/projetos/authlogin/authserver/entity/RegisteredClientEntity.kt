package inac.fernando.aulas.projetos.authlogin.authserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "oauth2_registered_client", schema = "auth")
class RegisteredClientEntity(
    @Id
    @Column(length = 100)
    var id: String? = null,

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    var clientId: String,

    @Column(name = "client_secret", length = 200)
    var clientSecret: String? = null,

    @Column(name = "client_id_issued_at")
    var clientIdIssuedAt: Instant? = null,

    @Column(name = "client_secret_expires_at")
    var clientSecretExpiresAt: Instant? = null,

    @Column(name = "client_name", length = 200)
    var clientName: String? = null,

    @Column(name = "client_authentication_methods", length = 100)
    var clientAuthenticationMethods: String,

    @Column(name = "authorization_grant_types", length = 100)
    var authorizationGrantTypes: String,

    @Column(name = "redirect_uris", length = 2000)
    var redirectUris: String,

    @Column(name = "scopes", length = 2000)
    var scopes: String,

    @Column(name = "client_settings", length = 5000)
    var clientSettings: String,

    @Column(name = "token_settings", length = 5000)
    var tokenSettings: String
)