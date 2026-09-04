import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Modal, Form, Alert } from 'react-bootstrap';
import { FaUser, FaEnvelope, FaPhone, FaCalendar, FaKey } from 'react-icons/fa';
import NavbarComponent from '../components/Navbar';
import LoadingSpinner from '../components/LoadingSpinner';
import userService from '../services/userService';
import './ProfilePage.css';

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [passwordError, setPasswordError] = useState('');
  const [passwordLoading, setPasswordLoading] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const response = await userService.getProfile();
      setUser(response.data);
    } catch (err) {
      setError('Failed to load profile');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setPasswordError('');

    if (!passwordData.currentPassword) {
      setPasswordError('Current password is required');
      return;
    }

    if (!passwordData.newPassword || passwordData.newPassword.length < 8) {
      setPasswordError('New password must be at least 8 characters');
      return;
    }

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setPasswordError('Passwords do not match');
      return;
    }

    setPasswordLoading(true);
    try {
      await userService.changePassword(passwordData);
      setSuccess('Password changed successfully');
      setShowPasswordModal(false);
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
    } catch (err) {
      setPasswordError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setPasswordLoading(false);
    }
  };

  if (loading) {
    return (
      <div>
        <NavbarComponent />
        <Container className="py-4">
          <LoadingSpinner />
        </Container>
      </div>
    );
  }

  return (
    <div>
      <NavbarComponent />
      <Container className="profile-container py-4">
        <Row>
          <Col lg={8} className="mx-auto">
            <h1 className="fw-bold mb-4">My Profile</h1>

            {error && <Alert variant="danger">{error}</Alert>}
            {success && <Alert variant="success">{success}</Alert>}

            {user && (
              <>
                <Card className="profile-card shadow-sm mb-4">
                  <Card.Body className="p-4">
                    <div className="text-center mb-4 pb-4 border-bottom">
                      <div className="profile-avatar">
                        <FaUser size={64} className="text-primary" />
                      </div>
                      <h2 className="fw-bold mt-3 mb-1">{user.fullName}</h2>
                      <p className="text-muted">Account ID: {user.id}</p>
                    </div>

                    <Row className="mb-4">
                      <Col md={6}>
                        <div className="profile-item">
                          <label className="text-muted small">First Name</label>
                          <p className="fw-600">{user.firstName}</p>
                        </div>
                      </Col>
                      <Col md={6}>
                        <div className="profile-item">
                          <label className="text-muted small">Last Name</label>
                          <p className="fw-600">{user.lastName}</p>
                        </div>
                      </Col>
                    </Row>

                    <Row className="mb-4">
                      <Col md={6}>
                        <div className="profile-item">
                          <label className="text-muted small">
                            <FaEnvelope className="me-2" />
                            Email
                          </label>
                          <p className="fw-600">{user.email}</p>
                        </div>
                      </Col>
                      <Col md={6}>
                        <div className="profile-item">
                          <label className="text-muted small">
                            <FaPhone className="me-2" />
                            Phone Number
                          </label>
                          <p className="fw-600">{user.phoneNumber || 'Not provided'}</p>
                        </div>
                      </Col>
                    </Row>

                    <Row className="border-top pt-4">
                      <Col md={6}>
                        <div className="profile-item">
                          <label className="text-muted small">
                            <FaCalendar className="me-2" />
                            Created At
                          </label>
                          <p className="fw-600">
                            {new Date(user.createdAt).toLocaleDateString()} at{' '}
                            {new Date(user.createdAt).toLocaleTimeString()}
                          </p>
                        </div>
                      </Col>
                      <Col md={6}>
                        <div className="profile-item">
                          <label className="text-muted small">
                            <FaCalendar className="me-2" />
                            Last Updated
                          </label>
                          <p className="fw-600">
                            {new Date(user.updatedAt).toLocaleDateString()} at{' '}
                            {new Date(user.updatedAt).toLocaleTimeString()}
                          </p>
                        </div>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>

                <div className="text-center">
                  <Button
                    variant="primary"
                    size="lg"
                    onClick={() => setShowPasswordModal(true)}
                    className="fw-600"
                  >
                    <FaKey className="me-2" />
                    Change Password
                  </Button>
                </div>
              </>
            )}
          </Col>
        </Row>

        {/* Change Password Modal */}
        <Modal show={showPasswordModal} onHide={() => setShowPasswordModal(false)} centered>
          <Modal.Header closeButton>
            <Modal.Title>Change Password</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {passwordError && <Alert variant="danger">{passwordError}</Alert>}

            <Form>
              <Form.Group className="mb-3">
                <Form.Label className="fw-600">Current Password</Form.Label>
                <Form.Control
                  type="password"
                  name="currentPassword"
                  value={passwordData.currentPassword}
                  onChange={handlePasswordChange}
                  placeholder="Enter your current password"
                  className="form-control-custom"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-600">New Password</Form.Label>
                <Form.Control
                  type="password"
                  name="newPassword"
                  value={passwordData.newPassword}
                  onChange={handlePasswordChange}
                  placeholder="Enter new password"
                  className="form-control-custom"
                />
                <small className="text-muted d-block mt-1">
                  At least 8 characters with uppercase, lowercase, number and special character
                </small>
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Label className="fw-600">Confirm Password</Form.Label>
                <Form.Control
                  type="password"
                  name="confirmPassword"
                  value={passwordData.confirmPassword}
                  onChange={handlePasswordChange}
                  placeholder="Confirm new password"
                  className="form-control-custom"
                />
              </Form.Group>
            </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowPasswordModal(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              onClick={handleChangePassword}
              disabled={passwordLoading}
            >
              {passwordLoading ? 'Changing...' : 'Change Password'}
            </Button>
          </Modal.Footer>
        </Modal>
      </Container>
    </div>
  );
};

export default ProfilePage;
