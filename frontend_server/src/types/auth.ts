export type LoginRequest = {
	username: string;
	password: string;
}

export type TokenResponse = {
	accessToken: string;
	tokenType: 'Bearer';
	expiresIn: number;
	refreshToken?: string;
}

export type RegisterRequest = {
	username: string;
	email: string;
	password: string;
}