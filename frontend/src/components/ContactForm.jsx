import React, { useState, useEffect } from 'react';
import { Modal, Form, Button, Row, Col } from 'react-bootstrap';
import { FaPlus, FaTrash } from 'react-icons/fa';
import contactService from '../services/contactService';
import './ContactForm.css';

const ContactForm = ({ show, contact, onClose, onSave }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    title: '',
    company: '',
    notes: '',
    emails: [],
    phones: [],
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (contact) {
      setFormData(contact);
    } else {
      setFormData({
        firstName: '',
        lastName: '',
        title: '',
        company: '',
        notes: '',
        emails: [],
        phones: [],
      });
    }
  }, [contact, show]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleEmailChange = (index, field, value) => {
    const newEmails = [...formData.emails];
    newEmails[index][field] = value;
    setFormData((prev) => ({
      ...prev,
      emails: newEmails,
    }));
  };

  const handlePhoneChange = (index, field, value) => {
    const newPhones = [...formData.phones];
    newPhones[index][field] = value;
    setFormData((prev) => ({
      ...prev,
      phones: newPhones,
    }));
  };

  const addEmail = () => {
    setFormData((prev) => ({
      ...prev,
      emails: [...prev.emails, { email: '', label: 'Work' }],
    }));
  };

  const removeEmail = (index) => {
    setFormData((prev) => ({
      ...prev,
      emails: prev.emails.filter((_, i) => i !== index),
    }));
  };

  const addPhone = () => {
    setFormData((prev) => ({
      ...prev,
      phones: [...prev.phones, { phoneNumber: '', label: 'Mobile' }],
    }));
  };

  const removePhone = (index) => {
    setFormData((prev) => ({
      ...prev,
      phones: prev.phones.filter((_, i) => i !== index),
    }));
  };

  const validateForm = () => {
    if (!formData.firstName || !formData.lastName) {
      setError('First name and last name are required');
      return false;
    }
    if (!formData.title) {
      setError('Title is required');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      if (contact && contact.id) {
        await contactService.updateContact(contact.id, formData);
      } else {
        await contactService.createContact(formData);
      }
      onSave();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save contact');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={show} onHide={onClose} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>{contact ? 'Edit Contact' : 'Add New Contact'}</Modal.Title>
      </Modal.Header>
      <Modal.Body className="contact-form-body">
        {error && <div className="alert alert-danger">{error}</div>}

        <Form>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="fw-600">First Name *</Form.Label>
                <Form.Control
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  placeholder="John"
                  className="form-control-custom"
                />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="fw-600">Last Name *</Form.Label>
                <Form.Control
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  placeholder="Doe"
                  className="form-control-custom"
                />
              </Form.Group>
            </Col>
          </Row>

          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="fw-600">Title *</Form.Label>
                <Form.Control
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  placeholder="Manager"
                  className="form-control-custom"
                />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="fw-600">Company</Form.Label>
                <Form.Control
                  type="text"
                  name="company"
                  value={formData.company}
                  onChange={handleInputChange}
                  placeholder="Company Name"
                  className="form-control-custom"
                />
              </Form.Group>
            </Col>
          </Row>

          <Form.Group className="mb-4">
            <Form.Label className="fw-600">Notes</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="notes"
              value={formData.notes}
              onChange={handleInputChange}
              placeholder="Add any notes here..."
              className="form-control-custom"
            />
          </Form.Group>

          <div className="section-divider">
            <h5 className="fw-600">Email Addresses</h5>
            {formData.emails.map((email, index) => (
              <Row key={index} className="mb-3">
                <Col md={7}>
                  <Form.Control
                    type="email"
                    placeholder="Email address"
                    value={email.email}
                    onChange={(e) => handleEmailChange(index, 'email', e.target.value)}
                    className="form-control-custom"
                  />
                </Col>
                <Col md={4}>
                  <Form.Select
                    value={email.label}
                    onChange={(e) => handleEmailChange(index, 'label', e.target.value)}
                    className="form-control-custom"
                  >
                    <option>Work</option>
                    <option>Personal</option>
                    <option>Other</option>
                  </Form.Select>
                </Col>
                <Col md={1}>
                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => removeEmail(index)}
                    className="w-100"
                  >
                    <FaTrash />
                  </Button>
                </Col>
              </Row>
            ))}
            <Button
              variant="outline-primary"
              size="sm"
              onClick={addEmail}
              className="mb-3"
            >
              <FaPlus className="me-2" />
              Add Email
            </Button>
          </div>

          <div className="section-divider">
            <h5 className="fw-600">Phone Numbers</h5>
            {formData.phones.map((phone, index) => (
              <Row key={index} className="mb-3">
                <Col md={7}>
                  <Form.Control
                    type="tel"
                    placeholder="Phone number"
                    value={phone.phoneNumber}
                    onChange={(e) => handlePhoneChange(index, 'phoneNumber', e.target.value)}
                    className="form-control-custom"
                  />
                </Col>
                <Col md={4}>
                  <Form.Select
                    value={phone.label}
                    onChange={(e) => handlePhoneChange(index, 'label', e.target.value)}
                    className="form-control-custom"
                  >
                    <option>Work</option>
                    <option>Home</option>
                    <option>Personal</option>
                    <option>Mobile</option>
                    <option>Other</option>
                  </Form.Select>
                </Col>
                <Col md={1}>
                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => removePhone(index)}
                    className="w-100"
                  >
                    <FaTrash />
                  </Button>
                </Col>
              </Row>
            ))}
            <Button
              variant="outline-primary"
              size="sm"
              onClick={addPhone}
            >
              <FaPlus className="me-2" />
              Add Phone
            </Button>
          </div>
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Cancel
        </Button>
        <Button
          variant="primary"
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading ? 'Saving...' : 'Save Contact'}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ContactForm;
