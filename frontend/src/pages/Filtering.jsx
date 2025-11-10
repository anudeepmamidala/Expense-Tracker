import React, { useState } from 'react';
import { Row, Col, Card, Button, Form, Alert, Spinner, Table } from 'react-bootstrap';
import { Layout } from '../components/Layout';
import api from '../services/api';

export const Filtering = () => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [hasSearched, setHasSearched] = useState(false);

  const [filters, setFilters] = useState({
    type: 'expense',
    startDate: '',
    endDate: '',
    keyword: '',
    sortField: 'date',
    sortOrder: 'desc',
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters({ ...filters, [name]: value });
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    setHasSearched(true);

    try {
      const payload = {
        type: filters.type,
        startDate: filters.startDate || null,
        endDate: filters.endDate || null,
        keyword: filters.keyword || null,
        sortField: filters.sortField || 'date',
        sortOrder: filters.sortOrder || 'desc',
      };

      const response = await api.post('/filter', payload);
      setResults(response.data);
    } catch (err) {
      setError(err.response?.data || 'Failed to filter transactions');
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFilters({
      type: 'expense',
      startDate: '',
      endDate: '',
      keyword: '',
      sortField: 'date',
      sortOrder: 'desc',
    });
    setResults([]);
    setHasSearched(false);
    setError('');
  };

  return (
    <Layout>
      <h2 className="mb-4">🔍 Advanced Filtering</h2>

      {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}

      {/* Filter Form */}
      <Card className="border-0 shadow-sm mb-4">
        <Card.Header className="bg-light">
          <h5 className="mb-0">Filter Options</h5>
        </Card.Header>
        <Card.Body>
          <Form onSubmit={handleSearch}>
            <Row className="mb-3 g-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Transaction Type</Form.Label>
                  <Form.Select
                    name="type"
                    value={filters.type}
                    onChange={handleInputChange}
                  >
                    <option value="expense">Expense</option>
                    <option value="income">Income</option>
                  </Form.Select>
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group>
                  <Form.Label>Keyword (Name)</Form.Label>
                  <Form.Control
                    type="text"
                    name="keyword"
                    placeholder="Search by name"
                    value={filters.keyword}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row className="mb-3 g-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Start Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="startDate"
                    value={filters.startDate}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group>
                  <Form.Label>End Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="endDate"
                    value={filters.endDate}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row className="mb-3 g-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Sort By</Form.Label>
                  <Form.Select
                    name="sortField"
                    value={filters.sortField}
                    onChange={handleInputChange}
                  >
                    <option value="date">Date</option>
                    <option value="amount">Amount</option>
                    <option value="name">Name</option>
                  </Form.Select>
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group>
                  <Form.Label>Sort Order</Form.Label>
                  <Form.Select
                    name="sortOrder"
                    value={filters.sortOrder}
                    onChange={handleInputChange}
                  >
                    <option value="desc">Descending</option>
                    <option value="asc">Ascending</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>

            <Row className="g-2">
              <Col xs="auto">
                <Button variant="primary" type="submit" disabled={loading}>
                  {loading ? 'Searching...' : 'Search'}
                </Button>
              </Col>
              <Col xs="auto">
                <Button variant="secondary" onClick={handleReset}>
                  Reset
                </Button>
              </Col>
            </Row>
          </Form>
        </Card.Body>
      </Card>

      {/* Results */}
      {hasSearched && (
        <Card className="border-0 shadow-sm">
          <Card.Header className="bg-light">
            <h5 className="mb-0">
              Results ({results.length} {filters.type === 'expense' ? 'Expense' : 'Income'})
            </h5>
          </Card.Header>
          <Card.Body>
            {loading ? (
              <div className="d-flex justify-content-center">
                <Spinner animation="border" />
              </div>
            ) : results.length > 0 ? (
              <div className="table-responsive">
                <Table hover className="mb-0">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Category</th>
                      <th>Amount</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {results.map((transaction) => (
                      <tr key={transaction.id}>
                        <td>{transaction.icon} {transaction.name}</td>
                        <td>{transaction.categoryName}</td>
                        <td className={filters.type === 'income' ? 'text-success fw-bold' : 'text-danger fw-bold'}>
                          ₹{transaction.amount.toFixed(2)}
                        </td>
                        <td>{new Date(transaction.date).toLocaleDateString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
            ) : (
              <p className="text-muted">No transactions found matching your filters</p>
            )}
          </Card.Body>
        </Card>
      )}
    </Layout>
  );
};
