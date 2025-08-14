import axios, { AxiosError, type AxiosInstance, type AxiosRequestConfig } from 'axios';
import { tokenStore } from '../state/tokenStore';
import { authApi } from './authApi';

const AUTH_BASE_URL = import.meta.env.VITE_AUTH_URL ?? 'http://localhost:9000';
const API_BASE_URL  = import.meta.env.VITE_API_URL  ?? 'http://localhost:8081';

export const authClient: AxiosInstance = axios.create({
	baseURL: AUTH_BASE_URL,
	headers: { 'Content-Type': 'application/json' },
});

export const apiClient: AxiosInstance = axios.create({
	baseURL: API_BASE_URL,
	headers: { 'Content-Type': 'application/json' },
	// withCredentials: true, // habilite se for usar cookie HttpOnly
});

// ----- Request: injeta Bearer no Backend -----
apiClient.interceptors.request.use((config) => {
	const token = tokenStore.getAccess();
	if (token) {
		config.headers = config.headers ?? {};
		config.headers['Authorization'] = `Bearer ${token}`;
	}
	return config;
});

// ----- Response: 401 -> tenta refresh e refaz -----
let refreshing = false;
let queue: Array<(token: string | null) => void> = [];

function pushQueue(cb: (token: string | null) => void) {
	queue.push(cb);
}
function flushQueue(token: string | null) {
	queue.forEach(cb => cb(token));
	queue = [];
}

apiClient.interceptors.response.use(
	(res) => res,
	async (error: AxiosError) => {
		const original = error.config as AxiosRequestConfig & { _retry?: boolean };
		const status = (error.response?.status ?? 0);
		
		// Só tenta refresh para 401 no Backend e se ainda não tentou
		if (status === 401 && !original._retry) {
			original._retry = true;
			
			if (!refreshing) {
				refreshing = true;
				try {
					const newAccess = await doRefresh();
					refreshing = false;
					flushQueue(newAccess);
				} catch (err) {
					console.log(err);
					refreshing = false;
					flushQueue(null);
				}
			}
			
			// aguarda refresh concluir
			const newToken = await new Promise<string | null>((resolve) => pushQueue(resolve));
			
			if (newToken) {
				original.headers = original.headers ?? {};
				original.headers['Authorization'] = `Bearer ${newToken}`;
				return apiClient.request(original);
			} else {
				// refresh falhou: limpa sessão e propaga erro
				tokenStore.clearAll();
				// opcional: redirecionar
				// window.location.assign('/login');
			}
		}
		
		return Promise.reject(error);
	}
);

async function doRefresh(): Promise<string | null> {
	const refresh = tokenStore.getRefresh();
	if (!refresh) return null;
	
	try {
		const { accessToken, refreshToken } = await authApi.refresh(refresh);
		tokenStore.setAccess(accessToken);
		if (refreshToken) tokenStore.updateRotatedRefresh(refreshToken);
		return accessToken;
	} catch {
		return null;
	}
}
