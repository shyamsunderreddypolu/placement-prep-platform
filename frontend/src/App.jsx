import React, { useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';

const PlaceholderDashboard = () => {
  const { user, logout } = useContext(AuthContext);

  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h1>Dashboard (Coming Soon)</h1>
      <p style={{ color: '#94a3b8', margin: '1rem 0' }}>Welcome, {user?.email}!</p>
      <button className="btn-primary" style={{ width: 'auto', margin: '0 auto' }} onClick={logout}>
        Sign Out
      </button>
    </div>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <PlaceholderDashboard />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
