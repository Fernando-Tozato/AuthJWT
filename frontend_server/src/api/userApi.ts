import { apiClient } from './http';

export const userApi = {
	async me() {
		const { data } = await apiClient.get('/api/me');
		return data as {
			sub: string;
			username: string;
			email: string;
			roles: string[];
		};
	},
	
	async adminHello() {
		const { data } = await apiClient.get('/api/admin/hello');
		return data as { msg: string };
	},
};
