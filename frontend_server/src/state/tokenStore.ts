// src/state/tokenStore.ts
type RefreshMode = 'local' | 'session' | null;

const LOCAL_KEY = 'app:refresh_token';
const SESSION_KEY = 'app:refresh_token';

let accessTokenMemory: string | null = null;
let refreshMode: RefreshMode = null;

function detectMode(): RefreshMode {
	try {
		if (sessionStorage.getItem(SESSION_KEY)) return 'session';
		if (localStorage.getItem(LOCAL_KEY))   return 'local';
	} catch {}
	return null;
}

type Listener = () => void;
const listeners = new Set<Listener>();
const notify = () => listeners.forEach(l => l());

export const tokenStore = {
	init() {
		refreshMode = detectMode();
	},
	
	// ACCESS em memória (menos exposição)
	getAccess() { return accessTokenMemory; },
	setAccess(token: string | null) { accessTokenMemory = token; notify(); },
	
	// REFRESH: leitura tenta sessão > local (isso também detecta o modo)
	getRefresh(): string | null {
		try {
			const s = sessionStorage.getItem(SESSION_KEY);
			if (s) { refreshMode = 'session'; return s; }
			const l = localStorage.getItem(LOCAL_KEY);
			if (l) { refreshMode = 'local'; return l; }
		} catch {}
		return null;
	},
	
	// set inicial (login): escolhe onde guardar
	setRefresh(token: string | null, rememberMe: boolean) {
		try {
			// limpa o outro modo para não haver duplicata
			localStorage.removeItem(LOCAL_KEY);
			sessionStorage.removeItem(SESSION_KEY);
			
			if (token) {
				if (rememberMe) { localStorage.setItem(LOCAL_KEY, token);  refreshMode = 'local'; }
				else            { sessionStorage.setItem(SESSION_KEY, token); refreshMode = 'session'; }
			} else {
				refreshMode = null;
			}
			notify();
		} catch {}
	},
	
	// rotação: persiste no MESMO modo atual
	updateRotatedRefresh(token: string) {
		try {
			if (refreshMode === 'session') sessionStorage.setItem(SESSION_KEY, token);
			else                           localStorage.setItem(LOCAL_KEY, token), (refreshMode = 'local');
			notify();
		} catch {}
	},
	
	clearAll() {
		this.setAccess(null);
		this.setRefresh(null, true);
	},
	
	subscribe(fn: Listener) { listeners.add(fn); return () => listeners.delete(fn); },
};
