import {
	Alert,
	Box, Button, Checkbox, Container, Divider, FormControl,
	FormControlLabel, Link, Paper, TextField, Typography
} from '@mui/material'
import { InacIcon } from '../../components/'
import React, { useState } from "react";
import type { LoginForm } from "../../data";
import { useAuth } from '../../auth/AuthProvider';
import { useLocation, useNavigate } from 'react-router-dom';


function LoginPage() {
	const blankFormData: LoginForm = {
		username: '',
		password: '',
		rememberMe: false
	}
	const [formData, setFormData] = useState<LoginForm>(blankFormData);
	const [submitting, setSubmitting] = useState(false);
	const [error, setError] = useState<string | null>(null);
	
	const { login } = useAuth();
	
	const navigate = useNavigate();
	const location = useLocation();
	
	const from = (location.state as any)?.from?.pathname ?? '/';
	
	async function handleSubmit(e?: React.FormEvent | React.MouseEvent) {
		e?.preventDefault();
		setError(null);
		
		// validação bem básica
		const username = formData.username.trim();
		const password = formData.password;
		if (!username || !password) {
			setError('Informe usuário e senha.');
			return;
		}
		
		try {
			setSubmitting(true);
			// chama o provider de auth (que já guarda tokens e carrega /api/me)
			await login(username, password, formData.rememberMe);
			// navega pra rota de origem (ou /)
			navigate(from, { replace: true });
		} catch (err: any) {
			// mensagens amigáveis (ajuste conforme seu backend)
			const msg =
				err?.response?.status === 401
					? 'Credenciais inválidas.'
					: err?.response?.data?.message ?? 'Falha ao entrar. Tente novamente.';
			setError(msg);
		} finally {
			setSubmitting(false);
		}
	}
	
	return (
		<div className="full_page">
			<Container maxWidth="sm">
				<Paper
					sx={{
						marginY: 10,
						padding: 5,
						position: 'relative',
						backgroundColor: 'rgba(15, 18, 20, 0.5)',
						backdropFilter: 'blur(6px)',
						WebkitBackdropFilter: 'blur(6px)',
						borderRadius: 3,
						border: '1px solid rgba(255, 255, 255, 0.18)',
					}}
				>
					<InacIcon centered />
					<Typography variant="h3" align="center" gutterBottom fontWeight="bold" sx={{mt: 6}}>Login</Typography>
					
					{error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
					
					
					<Box
						sx={{
							display: 'flex',
							flexDirection: 'column',
							width: '100%',
							gap: 2,
						}}
					>
						<FormControl fullWidth sx={{ my: 2, gap: 2 }}>
							<TextField
								label="Username"
								variant="outlined"
								value={formData.username}
								autoComplete="username"
								disabled={submitting}
								onChange={e =>
									setFormData(prev => ({ ...prev, username: e.target.value }))
								}
							/>
							
							<TextField
								label="Password"
								variant="outlined"
								type="password"
								value={formData.password}
								autoComplete="current-password"
								disabled={submitting}
								onChange={e =>
									setFormData(prev => ({ ...prev, password: e.target.value }))
								}
							/>
						</FormControl>
						
						<FormControlLabel
							control={
								<Checkbox
									value={formData.rememberMe}
									color="primary"
									disabled={submitting}
									onChange={e =>
										setFormData(prev => ({ ...prev, rememberMe: e.target.checked }))
									}
								/>
							}
							label="Remember me"
						/>
						
						<Button
							type="submit"
							fullWidth
							variant="contained"
							sx={{ py: 1.3 }}
							disabled={submitting}
							onClick={handleSubmit}
						>
							{submitting ? 'Entrando…' : 'Login'}
						</Button>
						<Link
							href="/forgotpassword"
							variant="body2"
							sx={{ alignSelf: 'center' }}
						>
							Forgot your password?
						</Link>
						
						<Divider/>
						
						<Typography sx={{ textAlign: 'center' }}>
							Don&apos;t have an account?{' '}
							<Link
								href="/register"
								variant="body2"
								sx={{ alignSelf: 'center' }}
							>
								Sign up
							</Link>
						</Typography>
					</Box>
				</Paper>
			</Container>
		</div>
	)
}

export default LoginPage;