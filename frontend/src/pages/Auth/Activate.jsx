import React, { useEffect, useState } from 'react';
import { Container, Card, Alert, Button } from 'react-bootstrap';
import { useSearchParams, Link } from 'react-router-dom';
import { authService } from '../../services/authService';

export const Activate = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const activateAccount = async () => {
      const token = searchParams.get('token');

      if (!token) {
        setStatus('error');
        setMessage('No activation token provided');
        return;
      }

      try {
        const response = await authService.activate(token);
        setStatus('success');
        setMessage(response.data || 'Account activated successfully!');
      } catch (err) {
        setStatus('error');
        setMessage(err.response?.data || 'Activation failed. Token may be invalid or expired.');
      }
    };

    activateAccount();
  }, [searchParams]);

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
      <Card style={{ width: '100%', maxWidth: '400px' }}>
        <Card.Body className="text-center">
          <h2 className="mb-4">💰 ExpenseTracker</h2>

          {status === 'loading' && (
            <>
              <div className="spinner-border text-primary mb-3" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
              <p>Activating your account...</p>
            </>
          )}

          {status === 'success' && (
            <>
              <Alert variant="success">✅ {message}</Alert>
              <p className="mb-3">Your account has been activated successfully!</p>
              <Link to="/login">
                <Button variant="primary" className="w-100">
                  Go to Login
                </Button>
              </Link>
            </>
          )}

          {status === 'error' && (
            <>
              <Alert variant="danger">❌ {message}</Alert>
              <Link to="/login">
                <Button variant="primary" className="w-100">
                  Back to Login
                </Button>
              </Link>
            </>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};
