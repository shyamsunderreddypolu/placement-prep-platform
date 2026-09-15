import React, { createContext, useContext, useState, useEffect } from 'react';
import { logoutUser } from '../services/authService';

export const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || '');

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      const email = localStorage.getItem('userEmail') || '';
      const name = localStorage.getItem('userName') || '';
      const role = localStorage.getItem('userRole') || 'ROLE_USER';
      setUser({ email, name, role });
    } else {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userEmail');
      localStorage.removeItem('userName');
      localStorage.removeItem('userRole');
      setUser(null);
    }
  }, [token]);

  const login = (jwtToken, email, refreshToken, name, role) => {
    localStorage.setItem('token', jwtToken);
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
    if (email) localStorage.setItem('userEmail', email);
    if (name) localStorage.setItem('userName', name);
    if (role) localStorage.setItem('userRole', role);

    setToken(jwtToken);
    setUser({ email, name, role: role || 'ROLE_USER' });
  };

  const logout = async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    await logoutUser(refreshToken);
    setToken('');
    setUser(null);
  };

  const isAdmin = user?.role === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        logout,
        isAuthenticated: !!token,
        isAdmin,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};