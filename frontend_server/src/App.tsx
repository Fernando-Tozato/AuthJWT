import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { HomePage, CallbackPage, AuthPages } from "./pages";

function App() {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<HomePage />} />
				
				<Route path="/login" element={<AuthPages.LoginPage />} />
				<Route path="/register" element={<AuthPages.RegisterPage />} />
				<Route path="/forgotpassword" element={<AuthPages.ForgotPasswordPage />} />
				
				{/* rota de callback do OAuth2 */}
				<Route path="/callback" element={<CallbackPage />} />
				
				{/* redireciona qualquer outro para home (ou 404) */}
				<Route path="*" element={<Navigate to="/" replace />} />
			</Routes>
		</BrowserRouter>
	)
}

export default App
