import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Alert, Spinner, Button, Form } from 'react-bootstrap';
import { Layout } from '../components/Layout';
import { useAuth } from '../hooks/useAuth';
import { useTheme } from '../hooks/useTheme';
import api from '../services/api';

export const Profile = () => {
  const { user, logout } = useAuth();
  const { isDark, toggleTheme } = useTheme();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await api.get('/dashboard');
      setProfile(response.data.profile);
      setError('');
    } catch (err) {
      setError('Failed to load profile');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    if (window.confirm('Are you sure you want to logout?')) {
      logout();
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="d-flex justify-content-center align-items-center" style={{ height: '50vh' }}>
          <Spinner animation="border" />
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <h2 className="mb-4">👤 Profile Settings</h2>

      {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}
      {success && <Alert variant="success" onClose={() => setSuccess('')} dismissible>{success}</Alert>}

      <Row>
        <Col lg={8}>
          {/* User Information */}
          <Card className="border-0 shadow-sm mb-4">
            <Card.Header className="bg-light">
              <h5 className="mb-0">📋 Account Information</h5>
            </Card.Header>
            <Card.Body>
              <Form>
                <Form.Group className="mb-3">
                  <Form.Label>Full Name</Form.Label>
                  <Form.Control
                    type="text"
                    value={profile?.fullname || 'N/A'}
                    disabled
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    type="email"
                    value={profile?.email || 'N/A'}
                    disabled
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Account Created</Form.Label>
                  <Form.Control
                    type="text"
                    value={profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString() : 'N/A'}
                    disabled
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Last Updated</Form.Label>
                  <Form.Control
                    type="text"
                    value={profile?.updatedAt ? new Date(profile.updatedAt).toLocaleDateString() : 'N/A'}
                    disabled
                  />
                </Form.Group>
              </Form>
            </Card.Body>
          </Card>

          {/* Account Actions */}
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">⚙️ Account Actions</h5>
            </Card.Header>
            <Card.Body>
              <Button
                variant="danger"
                className="w-100"
                onClick={handleLogout}
              >
                🚪 Logout
              </Button>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          {/* Settings Card */}
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">🎨 Settings</h5>
            </Card.Header>
            <Card.Body>
              <div className="mb-3">
                <h6 className="mb-2">Theme</h6>
                <Button
                  variant={isDark ? 'dark' : 'light'}
                  className="w-100"
                  onClick={toggleTheme}
                >
                  {isDark ? '☀️ Switch to Light Mode' : '🌙 Switch to Dark Mode'}
                </Button>
              </div>

              <div className="p-3 bg-light rounded">
                <h6 className="mb-2">Current Theme</h6>
                <p className="mb-0">
                  {isDark ? '🌙 Dark Mode' : '☀️ Light Mode'}
                </p>
              </div>
            </Card.Body>
          </Card>

          {/* Quick Stats */}
          <Card className="border-0 shadow-sm mt-3">
            <Card.Header className="bg-light">
              <h5 className="mb-0">📊 Quick Stats</h5>
            </Card.Header>
            <Card.Body>
              <div className="mb-3">
                <h6 className="text-muted mb-2">Account Status</h6>
                <span className="badge bg-success">Active</span>
              </div>

              <div>
                <h6 className="text-muted mb-2">Account ID</h6>
                <small className="text-muted">{profile?.id || 'N/A'}</small>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Layout>
  );
};