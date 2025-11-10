import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Alert, Spinner } from 'react-bootstrap';
import { PieChart, Pie, Cell, Legend, Tooltip, ResponsiveContainer } from 'recharts';
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

  // Income by Category data for pie chart
  const incomeByCategory = {};
  dashboard?.latestIncomes?.forEach((income) => {
    incomeByCategory[income.categoryName] = (incomeByCategory[income.categoryName] || 0) + income.amount;
  });
  const incomePieData = Object.entries(incomeByCategory).map(([name, value]) => ({
    name,
    value: parseFloat(value.toFixed(2)),
  }));

  // Expense by Category data for pie chart
  const expenseByCategory = {};
  dashboard?.latestExpenses?.forEach((expense) => {
    expenseByCategory[expense.categoryName] = (expenseByCategory[expense.categoryName] || 0) + expense.amount;
  });
  const expensePieData = Object.entries(expenseByCategory).map(([name, value]) => ({
    name,
    value: parseFloat(value.toFixed(2)),
  }));

  // Overall Income vs Expense data
  const overallData = [
    { name: 'Income', value: parseFloat(totalIncome.toFixed(2)) },
    { name: 'Expense', value: parseFloat(totalExpense.toFixed(2)) },
  ];

  const COLORS_INCOME = ['#198754', '#20c997', '#51cf66', '#94d82d', '#ffd43b'];
  const COLORS_EXPENSE = ['#dc3545', '#fd7e14', '#ff6b6b', '#ff8787', '#ffa8a8'];
  const COLORS_OVERALL = ['#198754', '#dc3545'];

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

      {/* Pie Charts */}
      <Row className="mb-4 g-3">
        {/* Income by Category Pie Chart */}
        <Col lg={6}>
          <Card className="border-0 shadow-sm h-100">
            <Card.Header className="bg-light">
              <h5 className="mb-0">💵 Income Breakdown</h5>
            </Card.Header>
            <Card.Body>
              {incomePieData.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={incomePieData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, value }) => `${name}: ₹${value}`}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {incomePieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS_INCOME[index % COLORS_INCOME.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => `₹${value}`} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <p className="text-muted text-center">No income data available</p>
              )}
            </Card.Body>
          </Card>
        </Col>

        {/* Expense by Category Pie Chart */}
        <Col lg={6}>
          <Card className="border-0 shadow-sm h-100">
            <Card.Header className="bg-light">
              <h5 className="mb-0">💸 Expense Breakdown</h5>
            </Card.Header>
            <Card.Body>
              {expensePieData.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={expensePieData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, value }) => `${name}: ₹${value}`}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {expensePieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS_EXPENSE[index % COLORS_EXPENSE.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => `₹${value}`} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <p className="text-muted text-center">No expense data available</p>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Overall Income vs Expense */}
      <Row>
        <Col lg={6} className="mx-auto">
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">💰 Income vs Expense</h5>
            </Card.Header>
            <Card.Body>
              {overallData.some(item => item.value > 0) ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={overallData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, value }) => `${name}: ₹${value}`}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {overallData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS_OVERALL[index % COLORS_OVERALL.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => `₹${value}`} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <p className="text-muted text-center">No transaction data available</p>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Layout>
  );
};
