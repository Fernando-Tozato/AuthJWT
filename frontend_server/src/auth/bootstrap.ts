import { tokenStore } from '../state/tokenStore';
import { authApi } from '../api/authApi';
import { userApi } from '../api/userApi';

export type BootResult =
	| { status: 'anon' }
	| { status: 'auth'; user: { username: string; email: string; roles: string[] } };

export async function authBootstrap(): Promise<BootResult> {
	tokenStore.init(); // detecta modo (session/local) existente
	
	const refresh = tokenStore.getRefresh();
	if (!refresh) return { status: 'anon' };
	
	try {
		// tenta reemitir access no boot
		const { accessToken, refreshToken } = await authApi.refresh(refresh);
		tokenStore.setAccess(accessToken);
		if (refreshToken) tokenStore.updateRotatedRefresh(refreshToken);
		
		// opcional: carrega /api/me para ter dados na UI logo
		const me = await userApi.me();
		return { status: 'auth', user: { username: me.username, email: me.email, roles: me.roles } };
	} catch {
		tokenStore.clearAll(); // refresh inválido → sessão cai
		return { status: 'anon' };
	}
}
