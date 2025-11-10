import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Alert, Spinner } from 'react-bootstrap';
import { Layout } from '../components/Layout';
import api from '../services/api';

export const Charts = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const response = await api.get('/dashboard');
      setData(response.data);
      setError('');
    } catch (err) {
      setError('Failed to load chart data');
      console.error(err);
    } finally {
      setLoading(false);
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

  const totalIncome = data?.latestIncomes?.reduce((sum, income) => sum + income.amount, 0) || 0;
  const totalExpense = data?.latestExpenses?.reduce((sum, expense) => sum + expense.amount, 0) || 0;
  const balance = totalIncome - totalExpense;

  // Calculate income by category
  const incomeByCategory = {};
  data?.latestIncomes?.forEach((income) => {
    incomeByCategory[income.categoryName] = (incomeByCategory[income.categoryName] || 0) + income.amount;
  });

  // Calculate expense by category
  const expenseByCategory = {};
  data?.latestExpenses?.forEach((expense) => {
    expenseByCategory[expense.categoryName] = (expenseByCategory[expense.categoryName] || 0) + expense.amount;
  });

  return (
    <Layout>
      <h2 className="mb-4">📈 Charts & Analytics</h2>

      {error && <Alert variant="danger">{error}</Alert>}

      {/* Summary Overview */}
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

      {/* Income by Category */}
      <Row className="mb-4">
        <Col lg={6}>
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">💵 Income by Category</h5>
            </Card.Header>
            <Card.Body>
              {Object.keys(incomeByCategory).length > 0 ? (
                <div>
                  {Object.entries(incomeByCategory).map(([category, amount]) => (
                    <div key={category} className="mb-3">
                      <div className="d-flex justify-content-between mb-1">
                        <span>{category}</span>
                        <strong>₹{amount.toFixed(2)}</strong>
                      </div>
                      <div className="progress">
                        <div
                          className="progress-bar bg-success"
                          style={{ width: `${(amount / totalIncome) * 100}%` }}
                        ></div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-muted">No income data available</p>
              )}
            </Card.Body>
          </Card>
        </Col>

        {/* Expense by Category */}
        <Col lg={6}>
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">💸 Expense by Category</h5>
            </Card.Header>
            <Card.Body>
              {Object.keys(expenseByCategory).length > 0 ? (
                <div>
                  {Object.entries(expenseByCategory).map(([category, amount]) => (
                    <div key={category} className="mb-3">
                      <div className="d-flex justify-content-between mb-1">
                        <span>{category}</span>
                        <strong>₹{amount.toFixed(2)}</strong>
                      </div>
                      <div className="progress">
                        <div
                          className="progress-bar bg-danger"
                          style={{ width: `${(amount / totalExpense) * 100}%` }}
                        ></div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-muted">No expense data available</p>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Income vs Expense Comparison */}
      <Row>
        <Col>
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">💰 Income vs Expense</h5>
            </Card.Header>
            <Card.Body>
              <div className="mb-4">
                <div className="d-flex justify-content-between mb-2">
                  <span>Income</span>
                  <strong>₹{totalIncome.toFixed(2)}</strong>
                </div>
                <div className="progress" style={{ height: '30px' }}>
                  <div
                    className="progress-bar bg-success"
                    style={{ width: `${totalIncome > 0 ? 50 : 0}%` }}
                  ></div>
                </div>
              </div>

              <div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Expense</span>
                  <strong>₹{totalExpense.toFixed(2)}</strong>
                </div>
                <div className="progress" style={{ height: '30px' }}>
                  <div
                    className="progress-bar bg-danger"
                    style={{ width: `${totalExpense > 0 ? 50 : 0}%` }}
                  ></div>
                </div>
              </div>

              <div className="mt-4 p-3 bg-light rounded">
                <h6>Summary</h6>
                <p className="mb-1">
                  Income Percentage: <strong>{totalIncome > 0 ? ((totalIncome / (totalIncome + totalExpense)) * 100).toFixed(2) : 0}%</strong>
                </p>
                <p className="mb-0">
                  Expense Percentage: <strong>{totalExpense > 0 ? ((totalExpense / (totalIncome + totalExpense)) * 100).toFixed(2) : 0}%</strong>
                </p>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Layout>
  );
};
