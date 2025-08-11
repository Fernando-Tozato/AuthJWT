// src/auth/AuthContext.tsx
import {
	createContext,
	useContext,
	useState,
	useEffect,
	type ReactNode
} from 'react'

interface AuthContextType {
	accessToken: string | null
	isAuthenticated: boolean
	rememberMe: boolean
	setRememberMe: (value: boolean) => void
	login: (token: string, remember: boolean) => void
	logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
	const [accessToken, setAccessToken] = useState<string | null>(null)
	const [rememberMe, setRememberMe] = useState<boolean>(false)
	
	// Ao montar, tenta recuperar de localStorage → sessionStorage
	useEffect(() => {
		const token =
			localStorage.getItem('access_token') ||
			sessionStorage.getItem('access_token')
		if (token) {
			setAccessToken(token)
		}
	}, [])
	
	const login = (token: string, remember: boolean) => {
		// guarda no storage correto
		if (remember) {
			localStorage.setItem('access_token', token)
		} else {
			sessionStorage.setItem('access_token', token)
		}
		setAccessToken(token)
		setRememberMe(remember)
	}
	
	const logout = () => {
		// limpa ambos
		localStorage.removeItem('access_token')
		sessionStorage.removeItem('access_token')
		setAccessToken(null)
	}
	
	const value: AuthContextType = {
		accessToken,
		isAuthenticated: accessToken !== null,
		rememberMe,
		setRememberMe,
		login,
		logout
	}
	
	return (
		<AuthContext.Provider value={value}>
			{children}
		</AuthContext.Provider>
	)
}

// Hook de conveniência
export function useAuth(): AuthContextType {
	const ctx = useContext(AuthContext)
	if (!ctx) throw new Error('useAuth must be used within AuthProvider')
	return ctx
}
