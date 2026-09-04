import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Navbar, Nav, Container, Dropdown } from 'react-bootstrap';
import { FaSignOutAlt, FaUser } from 'react-icons/fa';
import authService from '../services/authService';
import './Navbar.css';

const NavbarComponent = () => {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <Navbar bg="white" expand="lg" className="navbar-custom shadow-sm">
      <Container fluid>
        <Navbar.Brand href="/dashboard" className="navbar-brand-custom d-flex align-items-center">
          <span className="company-mark">10P</span>
          <span className="brand-copy">
            <strong>10Pearls</strong>
            <small>Contact Manager</small>
          </span>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <Dropdown>
              <Dropdown.Toggle variant="link" className="text-decoration-none">
                <FaUser className="me-2" />
                {user?.firstName} {user?.lastName}
              </Dropdown.Toggle>
              <Dropdown.Menu align="end">
                <Dropdown.Item onClick={() => navigate('/profile')}>
                  <FaUser className="me-2" />
                  My Profile
                </Dropdown.Item>
                <Dropdown.Divider />
                <Dropdown.Item onClick={handleLogout} className="text-danger">
                  <FaSignOutAlt className="me-2" />
                  Logout
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavbarComponent;
