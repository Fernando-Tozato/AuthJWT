import { createRoot } from 'react-dom/client';
import { ThemeProvider } from '@mui/material/styles';
import App from './App';
import theme from './theme';
import { CssBaseline } from "@mui/material";
import './assets/Main.css';
import { AuthProvider } from "./auth/AuthProvider.tsx";
import { tokenStore } from "./state/tokenStore.ts";

tokenStore.init();

const root = createRoot(document.getElementById('root')!);
root.render(
    <ThemeProvider theme={theme}>
        <CssBaseline/>
        <AuthProvider>
            <App />
        </AuthProvider>
    </ThemeProvider>
);
