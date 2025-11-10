import React from 'react';
import { Sidebar } from './Sidebar';
import { Container } from 'react-bootstrap';

export const Layout = ({ children }) => {
  return (
    <div style={{ display: 'flex' }}>
      <Sidebar />
      <div className="main-content" style={{ flex: 1, marginLeft: '260px', transition: 'margin-left 0.3s ease' }}>
        <Container className="py-4">
          {children}
        </Container>
      </div>
    </div>
  );
};
