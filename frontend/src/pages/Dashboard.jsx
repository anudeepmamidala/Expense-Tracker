import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Alert, Spinner, Table } from 'react-bootstrap';
import { Layout } from '../components/Layout';
import { useAuth } from '../hooks/useAuth';
import api from '../services/api';

export const Dashboard = () => {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const response = await api.get('/dashboard');
        setDashboard(response.data);
        setError('');
      } catch (err) {
        setError('Failed to load dashboard data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <Layout>
        <div className="d-flex justify-content-center align-items-center" style={{ height: '50vh' }}>
          <Spinner animation="border" />
        </div>
      </Layout>
    );
  }

  const totalIncome = dashboard?.latestIncomes?.reduce((sum, income) => sum + income.amount, 0) || 0;
  const totalExpense = dashboard?.latestExpenses?.reduce((sum, expense) => sum + expense.amount, 0) || 0;
  const balance = totalIncome - totalExpense;

  return (
    <Layout>
      <h2 className="mb-4">Welcome, {user?.fullname}! 👋</h2>

      {error && <Alert variant="danger">{error}</Alert>}

      {/* Summary Cards */}
      <Row className="mb-4 g-3">
        <Col md={4} sm={6} xs={12}>
          <Card className="text-center border-0 shadow-sm">
            <Card.Body>
              <h6 className="text-muted mb-2">Total Income</h6>
              <h3 className="text-success">₹{totalIncome.toFixed(2)}</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4} sm={6} xs={12}>
          <Card className="text-center border-0 shadow-sm">
            <Card.Body>
              <h6 className="text-muted mb-2">Total Expense</h6>
              <h3 className="text-danger">₹{totalExpense.toFixed(2)}</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4} sm={6} xs={12}>
          <Card className="text-center border-0 shadow-sm">
            <Card.Body>
              <h6 className="text-muted mb-2">Balance</h6>
              <h3 className={balance >= 0 ? 'text-success' : 'text-danger'}>
                ₹{balance.toFixed(2)}
              </h3>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Recent Transactions */}
      <Card className="border-0 shadow-sm">
        <Card.Header className="bg-light">
          <h5 className="mb-0">📝 Recent Transactions</h5>
        </Card.Header>
        <Card.Body>
          {dashboard?.recentTransactions && dashboard.recentTransactions.length > 0 ? (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead>
                  <tr>
                    <th>Type</th>
                    <th>Name</th>
                    <th>Amount</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {dashboard.recentTransactions.slice(0, 10).map((transaction) => (
                    <tr key={transaction.id}>
                      <td>
                        <span className={`badge ${transaction.type === 'INCOME' ? 'bg-success' : 'bg-danger'}`}>
                          {transaction.type}
                        </span>
                      </td>
                      <td>{transaction.icon} {transaction.name}</td>
                      <td className={transaction.type === 'INCOME' ? 'text-success' : 'text-danger'}>
                        ₹{transaction.amount.toFixed(2)}
                      </td>
                      <td>{new Date(transaction.createdAt).toLocaleDateString()}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          ) : (
            <p className="text-muted">No transactions yet</p>
          )}
        </Card.Body>
      </Card>
    </Layout>
  );
};
