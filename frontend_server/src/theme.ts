import { createTheme } from '@mui/material/styles';

// 1.1 – (Opcional) estender a paleta para incluir nossos status
declare module '@mui/material/styles' {
	interface Palette {
		status: {
			cancelled: string;
			todo: string;
			inProgress: string;
			completed: string;
		};
	}
	interface PaletteOptions {
		status?: {
			cancelled?: string;
			todo?: string;
			inProgress?: string;
			completed?: string;
		};
	}
}

const theme = createTheme({
	typography: {
		fontFamily: '"Comfortaa", sans-serif', // raiz
		// você pode customizar h1, h2…body1, button aqui também
	},
	palette: {
		mode: 'dark',
		// cores principais da interface
		primary: { main: '#0288d1' },      // azul para botões primários, campos, etc
		secondary: { main: '#ed6c02' },    // laranja como cor de destaque
		background: {
			default: '#06082d',
		},
		// tokens de semântica que já vimos
		error:   { main: '#d32f2f' },
		warning: { main: '#ed6c02' },
		info:    { main: '#0288d1' },
		success: { main: '#2e7d32' },
		
		// nosso namespace customizado
		status: {
			todo:       '#0288d1',
			inProgress: '#ed6c02',
			completed:  '#2e7d32',
			cancelled:  '#d32f2f',
		}
	},
	components: {
		MuiButton: {
			defaultProps: {
				variant: 'contained',
				color: 'primary',
			},
			styleOverrides: {
				root: {
					borderRadius: 8,
				}
			}
		},
		MuiPaper: {
			styleOverrides: {
				root: {
					borderRadius: 12,
				}
			}
		},
		MuiCssBaseline: {
			styleOverrides: {
				body: {
					fontFamily: '"Comfortaa", sans-serif',
				}
			}
		}
	}
});

export default theme;
