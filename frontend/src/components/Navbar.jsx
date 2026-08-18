import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Code2, LayoutDashboard, CheckSquare, LogOut, FileText, BookOpen } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="nav-logo">
          <Code2 className="logo-icon" />
          <span className="logo-text">PlacementPrep</span>
        </Link>

        <div className="nav-links">
          <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}>
            <LayoutDashboard size={18} />
            <span>Dashboard</span>
          </Link>
          <Link to="/problems" className={`nav-link ${location.pathname === '/problems' ? 'active' : ''}`}>
            <Code2 size={18} />
            <span>Problems</span>
          </Link>
          <Link to="/submissions" className={`nav-link ${location.pathname === '/submissions' ? 'active' : ''}`}>
            <CheckSquare size={18} />
            <span>Submissions</span>
          </Link>
          <Link to="/resumes" className={`nav-link ${location.pathname === '/resumes' ? 'active' : ''}`}>
            <FileText size={18} />
            <span>Resumes & ATS</span>
          </Link>
          <Link to="/flashcards" className={`nav-link ${location.pathname === '/flashcards' ? 'active' : ''}`}>
            <BookOpen size={18} />
            <span>Core CS & System Design</span>
          </Link>
        </div>

        <div className="nav-user">
          <span className="user-email">{user?.email}</span>
          <button onClick={handleLogout} className="logout-btn" title="Logout">
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;