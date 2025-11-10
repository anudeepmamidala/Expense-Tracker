import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Button, Form, Alert, Spinner, Table, Modal } from 'react-bootstrap';
import { Layout } from '../components/Layout';
import api from '../services/api';

export const Income = () => {
  const [incomes, setIncomes] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showModal, setShowModal] = useState(false);
  
  const [formData, setFormData] = useState({
    name: '',
    amount: '',
    categoryId: '',
    date: new Date().toISOString().split('T')[0],
    icon: '💵',
  });

  // Fetch incomes and categories
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [incomesRes, categoriesRes] = await Promise.all([
        api.get('/incomes'),
        api.get('/categories/INCOME'),
      ]);
      setIncomes(incomesRes.data);
      setCategories(categoriesRes.data);
      setError('');
    } catch (err) {
      setError('Failed to load data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleAddIncome = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.name || !formData.amount || !formData.categoryId) {
      setError('Please fill all fields');
      return;
    }

    try {
      await api.post('/incomes', {
        name: formData.name,
        amount: parseFloat(formData.amount),
        categoryId: parseInt(formData.categoryId),
        date: formData.date,
        icon: formData.icon,
      });

      setSuccess('Income added successfully!');
      setFormData({
        name: '',
        amount: '',
        categoryId: '',
        date: new Date().toISOString().split('T')[0],
        icon: '💵',
      });
      setShowModal(false);
      fetchData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add income');
    }
  };

  const handleDeleteIncome = async (id) => {
    if (window.confirm('Are you sure you want to delete this income?')) {
      try {
        await api.delete(`/incomes/${id}`);
        setSuccess('Income deleted successfully!');
        fetchData();
      } catch (err) {
        setError('Failed to delete income');
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

  return (
    <Layout>
      <Row className="mb-4">
        <Col>
          <h2>💵 Income Management</h2>
        </Col>
        <Col className="text-end">
          <Button variant="success" onClick={() => setShowModal(true)}>
            + Add Income
          </Button>
        </Col>
      </Row>

      {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}
      {success && <Alert variant="success" onClose={() => setSuccess('')} dismissible>{success}</Alert>}

      {/* Add Income Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Add Income</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleAddIncome}>
            <Form.Group className="mb-3">
              <Form.Label>Income Name</Form.Label>
              <Form.Control
                type="text"
                name="name"
                placeholder="e.g., Salary"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Category</Form.Label>
              <Form.Select
                name="categoryId"
                value={formData.categoryId}
                onChange={handleInputChange}
                required
              >
                <option value="">Select Category</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.icon} {cat.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Amount</Form.Label>
              <Form.Control
                type="number"
                name="amount"
                placeholder="0.00"
                step="0.01"
                value={formData.amount}
                onChange={handleInputChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Date</Form.Label>
              <Form.Control
                type="date"
                name="date"
                value={formData.date}
                onChange={handleInputChange}
                required
              />
            </Form.Group>

            <Button variant="success" type="submit" className="w-100">
              Add Income
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Incomes List */}
      <Card className="border-0 shadow-sm">
        <Card.Header className="bg-light">
          <h5 className="mb-0">📋 Current Month Income</h5>
        </Card.Header>
        <Card.Body>
          {incomes.length > 0 ? (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Amount</th>
                    <th>Date</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {incomes.map((income) => (
                    <tr key={income.id}>
                      <td>{income.icon} {income.name}</td>
                      <td>{income.categoryName}</td>
                      <td className="text-success fw-bold">₹{income.amount.toFixed(2)}</td>
                      <td>{new Date(income.date).toLocaleDateString()}</td>
                      <td>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleDeleteIncome(income.id)}
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
            <p className="text-muted">No income records yet</p>
          )}
        </Card.Body>
      </Card>
    </Layout>
  );
};
