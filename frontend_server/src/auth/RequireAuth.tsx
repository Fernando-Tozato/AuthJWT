import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthProvider';
import type { JSX } from "react";

export function RequireAuth({ children }: { children: JSX.Element }) {
	const auth = useAuth();
	if (auth.phase === 'loading') return <div>Carregando…</div>;
	if (auth.phase === 'anon') return <Navigate to="/login" replace />;
	return children;
}