import React from 'react';
import { Modal, Button, Row, Col, Badge } from 'react-bootstrap';
import { FaEdit, FaPhone, FaEnvelope, FaCalendar, FaBuilding } from 'react-icons/fa';
import './ContactDetails.css';

const ContactDetails = ({ show, contact, onClose, onEdit }) => {
  if (!contact) return null;

  return (
    <Modal show={show} onHide={onClose} size="lg" centered>
      <Modal.Header closeButton className="contact-details-header">
        <Modal.Title>Contact Details</Modal.Title>
      </Modal.Header>
      <Modal.Body className="contact-details-body">
        <div className="contact-header mb-4 pb-4 border-bottom">
          <h3 className="fw-bold mb-1">{contact.fullName}</h3>
          <p className="text-muted mb-2">{contact.title}</p>
          {contact.company && (
            <p className="text-muted mb-0">
              <FaBuilding className="me-2" />
              {contact.company}
            </p>
          )}
        </div>

        <div className="contact-section">
          <h5 className="fw-bold mb-3">Email Addresses</h5>
          {contact.emails && contact.emails.length > 0 ? (
            <div className="contact-list">
              {contact.emails.map((email, index) => (
                <div key={index} className="contact-item mb-2">
                  <Row className="align-items-center">
                    <Col md={8}>
                      <FaEnvelope className="me-2 text-primary" />
                      <a href={`mailto:${email.email}`} className="text-break">
                        {email.email}
                      </a>
                    </Col>
                    <Col md={4} className="text-end">
                      <Badge bg="light" text="dark">
                        {email.label}
                      </Badge>
                    </Col>
                  </Row>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-muted">No email addresses</p>
          )}
        </div>

        <div className="contact-section mt-4 pt-4 border-top">
          <h5 className="fw-bold mb-3">Phone Numbers</h5>
          {contact.phones && contact.phones.length > 0 ? (
            <div className="contact-list">
              {contact.phones.map((phone, index) => (
                <div key={index} className="contact-item mb-2">
                  <Row className="align-items-center">
                    <Col md={8}>
                      <FaPhone className="me-2 text-primary" />
                      <a href={`tel:${phone.phoneNumber}`}>{phone.phoneNumber}</a>
                    </Col>
                    <Col md={4} className="text-end">
                      <Badge bg="light" text="dark">
                        {phone.label}
                      </Badge>
                    </Col>
                  </Row>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-muted">No phone numbers</p>
          )}
        </div>

        {contact.notes && (
          <div className="contact-section mt-4 pt-4 border-top">
            <h5 className="fw-bold mb-3">Notes</h5>
            <p className="text-muted" style={{ whiteSpace: 'pre-wrap' }}>
              {contact.notes}
            </p>
          </div>
        )}

        <div className="contact-section mt-4 pt-4 border-top">
          <small className="text-muted d-block">
            <FaCalendar className="me-2" />
            Created: {new Date(contact.createdAt).toLocaleDateString()}
          </small>
          <small className="text-muted d-block">
            <FaCalendar className="me-2" />
            Updated: {new Date(contact.updatedAt).toLocaleDateString()}
          </small>
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Close
        </Button>
        <Button variant="primary" onClick={onEdit}>
          <FaEdit className="me-2" />
          Edit Contact
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ContactDetails;
