import React, { useState } from 'react';
import { Navbar, Nav, Container, Offcanvas } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ThemeToggle } from './ThemeToggle';

export const NavBar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [show, setShow] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleClose = () => setShow(false);

  if (!isAuthenticated) {
    return null; // Don't show navbar on auth pages
  }

  return (
    <Navbar bg="light" expand="lg" sticky="top" className="border-bottom">
      <Container>
        <Navbar.Brand as={Link} to="/dashboard" className="fw-bold">
          💰 ExpenseTracker
        </Navbar.Brand>
        
        <Navbar.Toggle aria-controls="offcanvasNavbar" onClick={() => setShow(true)} />
        
        <Navbar.Offcanvas
          id="offcanvasNavbar"
          aria-labelledby="offcanvasNavbarLabel"
          placement="end"
          show={show}
          onHide={handleClose}
        >
          <Offcanvas.Header closeButton>
            <Offcanvas.Title id="offcanvasNavbarLabel">
              Menu
            </Offcanvas.Title>
          </Offcanvas.Header>
          
          <Offcanvas.Body>
            <Nav className="justify-content-end flex-grow-1 pe-3">
              <Nav.Link as={Link} to="/dashboard" onClick={handleClose}>
                📊 Dashboard
              </Nav.Link>
              <Nav.Link as={Link} to="/income" onClick={handleClose}>
                💵 Income
              </Nav.Link>
              <Nav.Link as={Link} to="/expense" onClick={handleClose}>
                💸 Expense
              </Nav.Link>
              <Nav.Link as={Link} to="/categories" onClick={handleClose}>
                📂 Categories
              </Nav.Link>
              <Nav.Link as={Link} to="/filtering" onClick={handleClose}>
                🔍 Filtering
              </Nav.Link>
              <Nav.Link as={Link} to="/charts" onClick={handleClose}>
                📈 Charts
              </Nav.Link>
              <Nav.Link as={Link} to="/profile" onClick={handleClose}>
                👤 Profile
              </Nav.Link>
              
              <div className="d-flex gap-2 mt-3 mt-lg-0 ms-lg-3">
                <ThemeToggle />
                <button 
                  className="btn btn-danger"
                  onClick={handleLogout}
                >
                  Logout
                </button>
              </div>
            </Nav>
          </Offcanvas.Body>
        </Navbar.Offcanvas>
      </Container>
    </Navbar>
  );
};