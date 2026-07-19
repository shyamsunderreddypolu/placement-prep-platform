import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || '');

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      const email = localStorage.getItem('userEmail') || '';
      setUser({ email });
    } else {
      localStorage.removeItem('token');
      localStorage.removeItem('userEmail');
      setUser(null);
    }
  }, [token]);

  const login = (jwtToken, email) => {
    localStorage.setItem('token', jwtToken);
    localStorage.setItem('userEmail', email);
    setToken(jwtToken);
    setUser({ email });
  };

  const logout = () => {
    setToken('');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};
