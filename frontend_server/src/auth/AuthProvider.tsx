import React, { createContext, useContext, useEffect, useState } from 'react';
import { authBootstrap } from './bootstrap';
import { authApi } from '../api/authApi';
import { tokenStore } from '../state/tokenStore';

type User = { username: string; email: string; roles: string[] };
type State =
	| { phase: 'loading' }
	| { phase: 'anon' }
	| { phase: 'auth'; user: User };

type Ctx = State & {
	login: (u: string, p: string, remember: boolean) => Promise<void>;
	logout: () => void;
};

const AuthCtx = createContext<Ctx>({ phase: 'loading', login: async () => {}, logout: () => {} });

export function AuthProvider({ children }: { children: React.ReactNode }) {
	const [state, setState] = useState<State>({ phase: 'loading' });
	
	useEffect(() => {
		let mounted = true;
		authBootstrap().then((res) => {
			if (!mounted) return;
			if (res.status === 'auth') setState({ phase: 'auth', user: res.user });
			else setState({ phase: 'anon' });
		});
		
		// cross-tab sync (login/logout em outra aba)
		const onStorage = (e: StorageEvent) => {
			if (e.key === 'app:refresh_token') {
				// se virou null → força anon; se passou a existir → próximo 401 cuida; opcionalmente, rebootstrap aqui.
				const has = !!tokenStore.getRefresh();
				if (!has) {
					tokenStore.setAccess(null);
					setState({ phase: 'anon' });
				}
			}
		};
		window.addEventListener('storage', onStorage);
		return () => { mounted = false; window.removeEventListener('storage', onStorage); };
	}, []);
	
	const login = async (username: string, password: string, remember: boolean) => {
		const res = await authApi.login({ username, password });
		tokenStore.setAccess(res.accessToken);
		tokenStore.setRefresh(res.refreshToken ?? null, remember);
		// carregue o perfil (ou decode o access se preferir)
		// supondo que seu /api/me está protegido:
		// caso queira evitar a chamada extra, você pode decodificar o access (sem validar) e montar {username,email,roles}
		setState({ phase: 'loading' });
		try {
			const me = await (await import('../api/userApi')).userApi.me();
			setState({ phase: 'auth', user: { username: me.username, email: me.email, roles: me.roles } });
		} catch {
			// se falhar, ainda assim estamos autenticados; caia para anon se preferir
			setState({ phase: 'auth', user: { username, email: '', roles: [] } });
		}
	};
	
	const logout = () => {
		tokenStore.clearAll();
		setState({ phase: 'anon' });
	};
	
	return <AuthCtx.Provider value={{ ...state, login, logout }}>{children}</AuthCtx.Provider>;
}

export const useAuth = () => useContext(AuthCtx);
