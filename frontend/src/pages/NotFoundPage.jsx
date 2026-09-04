import React from 'react';
import { Container, Row, Col, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { FaHome } from 'react-icons/fa';

const NotFoundPage = () => {
  return (
    <Container fluid className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
      <Row>
        <Col md={6} className="mx-auto text-center">
          <div className="mb-4">
            <h1 style={{ fontSize: '120px', fontWeight: 'bold', color: '#3b82f6' }}>404</h1>
          </div>
          <h2 className="fw-bold mb-3">Page Not Found</h2>
          <p className="text-muted mb-4">
            Sorry, the page you're looking for doesn't exist or has been moved.
          </p>
          <Link to="/dashboard">
            <Button variant="primary" size="lg">
              <FaHome className="me-2" />
              Go to Dashboard
            </Button>
          </Link>
        </Col>
      </Row>
    </Container>
  );
};

export default NotFoundPage;
