import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ThemeToggle } from './ThemeToggle';
import './Sidebar.css';

export const Sidebar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(true);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };

  if (!isAuthenticated) {
    return null;
  }

  return (
    <aside className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
      <div className="sidebar-header">
        <Link to="/dashboard" className="sidebar-brand">
          💰 ExpenseTracker
        </Link>
        <button 
          className="sidebar-toggle-btn d-lg-none"
          onClick={toggleSidebar}
          aria-label="Toggle sidebar"
        >
          ☰
        </button>
      </div>

      <nav className="sidebar-nav">
        <ul>
          <li>
            <Link to="/dashboard" className="sidebar-link">
              <span className="icon">📊</span>
              <span className="label">Dashboard</span>
            </Link>
          </li>
          <li>
            <Link to="/income" className="sidebar-link">
              <span className="icon">💵</span>
              <span className="label">Income</span>
            </Link>
          </li>
          <li>
            <Link to="/expense" className="sidebar-link">
              <span className="icon">💸</span>
              <span className="label">Expense</span>
            </Link>
          </li>
          <li>
            <Link to="/categories" className="sidebar-link">
              <span className="icon">📁</span>
              <span className="label">Categories</span>
            </Link>
          </li>
          <li>
            <Link to="/filtering" className="sidebar-link">
              <span className="icon">🔍</span>
              <span className="label">Filtering</span>
            </Link>
          </li>
          <li>
            <Link to="/charts" className="sidebar-link">
              <span className="icon">📈</span>
              <span className="label">Charts</span>
            </Link>
          </li>
          <li>
            <Link to="/profile" className="sidebar-link">
              <span className="icon">👤</span>
              <span className="label">Profile</span>
            </Link>
          </li>
        </ul>
      </nav>

      <div className="sidebar-footer">
        <div className="theme-toggle-wrapper">
          <ThemeToggle />
        </div>
        <button 
          className="btn btn-danger w-100"
          onClick={handleLogout}
        >
          Logout
        </button>
      </div>
    </aside>
  );
};