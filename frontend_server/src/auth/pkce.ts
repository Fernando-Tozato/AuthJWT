// src/auth/pkce.ts

/**
 * Gera um code verifier aleatório (string URL-safe, 43–128 chars).
 */
export function generateCodeVerifier(length = 128): string {
	const array = new Uint8Array(length)
	crypto.getRandomValues(array)
	// converte cada byte para URL-safe Base64 sem padding
	return btoa(String.fromCharCode(...array))
		.replace(/\+/g, '-')
		.replace(/\//g, '_')
		.replace(/=+$/, '')
}

/**
 * Faz SHA-256 e retorna ArrayBuffer.
 */
async function sha256(buffer: Uint8Array): Promise<ArrayBuffer> {
	return crypto.subtle.digest('SHA-256', buffer)
}

/**
 * Codifica ArrayBuffer em Base64-URL sem padding.
 */
function base64UrlEncode(buffer: ArrayBuffer): string {
	const bytes = new Uint8Array(buffer)
	let str = ''
	for (let i = 0; i < bytes.byteLength; i++) {
		str += String.fromCharCode(bytes[i])
	}
	return btoa(str)
		.replace(/\+/g, '-')
		.replace(/\//g, '_')
		.replace(/=+$/, '')
}

/**
 * Gera o code challenge a partir do verifier.
 */
export async function generateCodeChallenge(verifier: string): Promise<string> {
	const data = new TextEncoder().encode(verifier)
	const digest = await sha256(data)
	return base64UrlEncode(digest)
}

/**
 * Monta a URL de autorização (Authorization Code + PKCE).
 * Retorna também o codeVerifier, que você deve armazenar
 * (localStorage, sessão, Redux etc) para usar no callback.
 */
export async function buildAuthUrl(options: {
	authEndpoint: string     // ex: "http://localhost:9000/oauth2/authorize"
	clientId: string
	redirectUri: string
	scope: string            // ex: "openid profile backend.read"
	responseType?: string    // padrão "code"
}): Promise<{ url: string; codeVerifier: string }> {
	const { authEndpoint, clientId, redirectUri, scope } = options
	const responseType = options.responseType ?? 'code'
	
	const codeVerifier = generateCodeVerifier()
	const codeChallenge = await generateCodeChallenge(codeVerifier)
	
	const url = new URL(authEndpoint)
	url.searchParams.set('response_type', responseType)
	url.searchParams.set('client_id', clientId)
	url.searchParams.set('redirect_uri', redirectUri)
	url.searchParams.set('scope', scope)
	url.searchParams.set('code_challenge', codeChallenge)
	url.searchParams.set('code_challenge_method', 'S256')
	
	return { url: url.toString(), codeVerifier }
}
