import React, { useContext } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { LayoutDashboard, Code2, LogOut, CheckCircle2 } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <Code2 size={28} className="brand-icon" />
        <span className="brand-title">Placement Prep</span>
      </div>

      <div className="nav-links">
        <Link to="/dashboard" className={`nav-item ${isActive('/dashboard') ? 'active' : ''}`}>
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </Link>
        <Link to="/problems" className={`nav-item ${isActive('/problems') ? 'active' : ''}`}>
          <CheckCircle2 size={18} />
          <span>Problems</span>
        </Link>
      </div>

      <div className="nav-user">
        <span className="user-email">{user?.email}</span>
        <button onClick={logout} className="btn-logout" title="Sign Out">
          <LogOut size={18} />
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
