import { authClient } from './http';
import { type AuthTypes } from '../types';

export const authApi = {
	async login(payload: AuthTypes.LoginRequest): Promise<AuthTypes.TokenResponse> {
		const { data } = await authClient.post<AuthTypes.TokenResponse>('/auth/login', payload);
		return data;
	},
	
	async refresh(refreshToken: string): Promise<AuthTypes.TokenResponse> {
		const { data } = await authClient.post<AuthTypes.TokenResponse>('/auth/refresh', { refreshToken });
		return data;
	},
	
	async register(payload: AuthTypes.RegisterRequest) {
		const { data } = await authClient.post('/auth/register', payload);
		return data; // UserResponse
	},
};
