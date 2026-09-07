import React, { useState } from 'react';
import { Container, Form, Button, Card, Alert } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { FaPhone, FaEnvelope, FaLock } from 'react-icons/fa';
import authService from '../services/authService';
import './AuthPages.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    if (!email && !phoneNumber) {
      setError('Please enter email or phone number');
      return;
    }

    if (!password) {
      setError('Please enter password');
      return;
    }

    setLoading(true);
    try {
      const loginData = {
        email: email || undefined,
        phoneNumber: phoneNumber || undefined,
        password,
      };

      const response = await authService.login(loginData);
      const { accessToken, ...user } = response.data;
      
      authService.setAuthToken(accessToken);
      authService.setUser(user);
      
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container fluid className="auth-container d-flex align-items-center justify-content-center">
      <Card className="auth-card shadow-lg">
        <Card.Body className="p-5">
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary">Contact Manager</h2>
            <p className="text-muted">Sign in to your account</p>
          </div>

          {error && <Alert variant="danger">{error}</Alert>}

          <Form onSubmit={handleLogin}>
            <Form.Group className="mb-3">
              <Form.Label className="fw-600">Email</Form.Label>
              <Form.Control
                type="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="form-control-custom"
              />
            </Form.Group>

            <div className="text-center my-2 text-muted">OR</div>

            <Form.Group className="mb-3">
              <Form.Label className="fw-600">Phone Number</Form.Label>
              <Form.Control
                type="tel"
                placeholder="Enter your phone number"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                className="form-control-custom"
              />
            </Form.Group>

            <Form.Group className="mb-4">
              <Form.Label className="fw-600">Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="form-control-custom"
              />
            </Form.Group>

            <Button
              variant="primary"
              className="w-100 py-2 fw-bold"
              onClick={handleLogin}
              disabled={loading}
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </Button>
          </Form>

          <div className="text-center mt-4">
            <p className="text-muted">
              Don't have an account?{' '}
              <Link to="/register" className="text-primary fw-bold">
                Sign up
              </Link>
            </p>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default LoginPage;
