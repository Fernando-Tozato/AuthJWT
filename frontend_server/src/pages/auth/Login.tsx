import { Box, Button, Checkbox, Container, Divider, FormControl, FormControlLabel, Link, Paper, TextField, Typography } from '@mui/material'
import { InacIcon } from '../../components/'
import { useState } from "react";
import type { LoginForm } from "../../data";
import { useAuth } from "../../auth/AuthContext.tsx";



function LoginPage() {
	const blankFormData: LoginForm = {
		username: '',
		password: '',
		rememberMe: false
	}
	const [formData, setFormData] = useState<LoginForm>(blankFormData);
	
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
								onChange={e =>
									setFormData(prev => ({ ...prev, username: e.target.value }))
								}
							/>
							
							<TextField
								label="Password"
								variant="outlined"
								value={formData.password}
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
							sx={{py: 1.3}}
							onClick={handleSubmit}
						>
							Login
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