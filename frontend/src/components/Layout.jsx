import React from 'react';
import { NavBar } from './Navbar';
import { Container } from 'react-bootstrap';

export const Layout = ({ children }) => {
  return (
    <div>
      <NavBar />
      <Container className="py-4">
        {children}
      </Container>
    </div>
  );
};