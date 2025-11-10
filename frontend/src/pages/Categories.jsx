import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Button, Form, Alert, Spinner, Table, Modal } from 'react-bootstrap';
import { Layout } from '../components/Layout';
import api from '../services/api';

export const Categories = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  
  const [formData, setFormData] = useState({
    name: '',
    icon: '📂',
    type: 'EXPENSE',
  });

  // Fetch categories
  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await api.get('/categories');
      setCategories(response.data);
      setError('');
    } catch (err) {
      setError('Failed to load categories');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleOpenModal = (category = null) => {
    if (category) {
      setEditingId(category.id);
      setFormData({
        name: category.name,
        icon: category.icon,
        type: category.type,
      });
    } else {
      setEditingId(null);
      setFormData({
        name: '',
        icon: '📂',
        type: 'EXPENSE',
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingId(null);
  };

  const handleSaveCategory = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.name || !formData.type) {
      setError('Please fill all fields');
      return;
    }

    try {
      if (editingId) {
        // Update category
        await api.put(`/categories/${editingId}`, {
          name: formData.name,
          icon: formData.icon,
          type: formData.type,
        });
        setSuccess('Category updated successfully!');
      } else {
        // Create new category
        await api.post('/categories', {
          name: formData.name,
          icon: formData.icon,
          type: formData.type,
        });
        setSuccess('Category created successfully!');
      }

      handleCloseModal();
      fetchCategories();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save category');
    }
  };

  const handleDeleteCategory = async (id) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await api.delete(`/categories/${id}`);
        setSuccess('Category deleted successfully!');
        fetchCategories();
      } catch (err) {
        setError('Failed to delete category');
      }
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

  const expenseCategories = categories.filter(c => c.type === 'EXPENSE');
  const incomeCategories = categories.filter(c => c.type === 'INCOME');

  return (
    <Layout>
      <Row className="mb-4">
        <Col>
          <h2>📂 Categories Management</h2>
        </Col>
        <Col className="text-end">
          <Button variant="primary" onClick={() => handleOpenModal()}>
            + Add Category
          </Button>
        </Col>
      </Row>

      {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}
      {success && <Alert variant="success" onClose={() => setSuccess('')} dismissible>{success}</Alert>}

      {/* Add/Edit Category Modal */}
      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>{editingId ? 'Edit Category' : 'Add Category'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSaveCategory}>
            <Form.Group className="mb-3">
              <Form.Label>Category Name</Form.Label>
              <Form.Control
                type="text"
                name="name"
                placeholder="e.g., Food"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Icon</Form.Label>
              <Form.Control
                type="text"
                name="icon"
                placeholder="e.g., 🍔"
                value={formData.icon}
                onChange={handleInputChange}
                maxLength="2"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Type</Form.Label>
              <Form.Select
                name="type"
                value={formData.type}
                onChange={handleInputChange}
                required
              >
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </Form.Select>
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100">
              {editingId ? 'Update Category' : 'Add Category'}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Expense Categories */}
      <Card className="border-0 shadow-sm mb-4">
        <Card.Header className="bg-light">
          <h5 className="mb-0">💸 Expense Categories ({expenseCategories.length})</h5>
        </Card.Header>
        <Card.Body>
          {expenseCategories.length > 0 ? (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead>
                  <tr>
                    <th>Icon</th>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {expenseCategories.map((cat) => (
                    <tr key={cat.id}>
                      <td>{cat.icon}</td>
                      <td>{cat.name}</td>
                      <td>
                        <span className="badge bg-danger">EXPENSE</span>
                      </td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          onClick={() => handleOpenModal(cat)}
                          className="me-2"
                        >
                          Edit
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDeleteCategory(cat.id)}
                        >
                          Delete
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          ) : (
            <p className="text-muted">No expense categories yet</p>
          )}
        </Card.Body>
      </Card>

      {/* Income Categories */}
      <Card className="border-0 shadow-sm">
        <Card.Header className="bg-light">
          <h5 className="mb-0">💵 Income Categories ({incomeCategories.length})</h5>
        </Card.Header>
        <Card.Body>
          {incomeCategories.length > 0 ? (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead>
                  <tr>
                    <th>Icon</th>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {incomeCategories.map((cat) => (
                    <tr key={cat.id}>
                      <td>{cat.icon}</td>
                      <td>{cat.name}</td>
                      <td>
                        <span className="badge bg-success">INCOME</span>
                      </td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          onClick={() => handleOpenModal(cat)}
                          className="me-2"
                        >
                          Edit
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDeleteCategory(cat.id)}
                        >
                          Delete
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          ) : (
            <p className="text-muted">No income categories yet</p>
          )}
        </Card.Body>
      </Card>
    </Layout>
  );
};