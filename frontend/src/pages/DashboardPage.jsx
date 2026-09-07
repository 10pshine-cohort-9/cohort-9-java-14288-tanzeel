import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Button, Table, Pagination, Form, Modal } from 'react-bootstrap';
import { FaPlus, FaEdit, FaTrash, FaEye } from 'react-icons/fa';
import NavbarComponent from '../components/Navbar';
import LoadingSpinner from '../components/LoadingSpinner';
import AlertMessage from '../components/AlertMessage';
import ContactForm from '../components/ContactForm';
import ContactDetails from '../components/ContactDetails';
import DeleteConfirmModal from '../components/DeleteConfirmModal';
import contactService from '../services/contactService';
import './DashboardPage.css';

const DashboardPage = () => {
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [search, setSearch] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [showContactForm, setShowContactForm] = useState(false);
  const [showContactDetails, setShowContactDetails] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [selectedContact, setSelectedContact] = useState(null);
  const [editingContact, setEditingContact] = useState(null);

  useEffect(() => {
    fetchContacts();
  }, [currentPage, search]);

  const fetchContacts = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await contactService.getContacts(currentPage, pageSize, search);
      setContacts(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
    } catch (err) {
      setError('Failed to load contacts');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setCurrentPage(0);
  };

  const handleAddContact = () => {
    setEditingContact(null);
    setShowContactForm(true);
  };

  const handleEditContact = (contact) => {
    setEditingContact(contact);
    setShowContactForm(true);
  };

  const handleViewContact = (contact) => {
    setSelectedContact(contact);
    setShowContactDetails(true);
  };

  const handleDeleteContact = (contact) => {
    setSelectedContact(contact);
    setShowDeleteConfirm(true);
  };

  const handleConfirmDelete = async () => {
    try {
      await contactService.deleteContact(selectedContact.id);
      setShowDeleteConfirm(false);
      setSuccess('Contact deleted successfully');
      fetchContacts();
    } catch (err) {
      setError('Failed to delete contact');
      console.error(err);
    }
  };

  const handleSaveContact = async () => {
    setShowContactForm(false);
    setSuccess(editingContact ? 'Contact updated successfully' : 'Contact created successfully');
    fetchContacts();
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div>
      <NavbarComponent />
      <Container fluid className="dashboard-container py-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h1 className="fw-bold text-dark">Contacts</h1>
          <Button
            variant="primary"
            className="btn-custom"
            onClick={handleAddContact}
          >
            <FaPlus className="me-2" />
            Add Contact
          </Button>
        </div>

        <AlertMessage message={error} type="error" onClose={() => setError('')} />
        <AlertMessage message={success} type="success" onClose={() => setSuccess('')} />

        <Row className="mb-4">
          <Col md={4}>
            <Form.Control
              type="text"
              placeholder="Search contacts..."
              value={search}
              onChange={handleSearchChange}
              className="form-control-custom"
            />
          </Col>
          <Col md={8} className="text-end text-muted small">
            Total Contacts: {totalElements}
          </Col>
        </Row>

        {loading ? (
          <LoadingSpinner />
        ) : contacts.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">📋</div>
            <h4>No contacts yet</h4>
            <p>Start by adding your first contact</p>
            <Button variant="primary" onClick={handleAddContact}>
              <FaPlus className="me-2" />
              Add Contact
            </Button>
          </div>
        ) : (
          <>
            <div className="table-responsive card-custom">
              <Table className="mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Name</th>
                    <th>Title</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Company</th>
                    <th className="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {contacts.map((contact) => (
                    <tr key={contact.id}>
                      <td className="fw-500">{contact.fullName}</td>
                      <td>{contact.title}</td>
                      <td>{contact.primaryEmail || '-'}</td>
                      <td>{contact.primaryPhone || '-'}</td>
                      <td>{contact.company || '-'}</td>
                      <td className="text-end">
                        <Button
                          variant="outline-info"
                          size="sm"
                          className="me-2"
                          title="View"
                          onClick={() => handleViewContact(contact)}
                        >
                          <FaEye />
                        </Button>
                        <Button
                          variant="outline-warning"
                          size="sm"
                          className="me-2"
                          title="Edit"
                          onClick={() => handleEditContact(contact)}
                        >
                          <FaEdit />
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          title="Delete"
                          onClick={() => handleDeleteContact(contact)}
                        >
                          <FaTrash />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>

            {totalPages > 1 && (
              <div className="mt-4 d-flex justify-content-center">
                <Pagination>
                  <Pagination.First
                    onClick={() => handlePageChange(0)}
                    disabled={currentPage === 0}
                  />
                  <Pagination.Prev
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                  />
                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    const pageNum = currentPage > 2 ? currentPage - 2 + i : i;
                    if (pageNum >= totalPages) return null;
                    return (
                      <Pagination.Item
                        key={pageNum}
                        active={pageNum === currentPage}
                        onClick={() => handlePageChange(pageNum)}
                      >
                        {pageNum + 1}
                      </Pagination.Item>
                    );
                  })}
                  <Pagination.Next
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                  />
                  <Pagination.Last
                    onClick={() => handlePageChange(totalPages - 1)}
                    disabled={currentPage === totalPages - 1}
                  />
                </Pagination>
              </div>
            )}
          </>
        )}

        {showContactForm && (
          <ContactForm
            show={showContactForm}
            contact={editingContact}
            onClose={() => setShowContactForm(false)}
            onSave={handleSaveContact}
          />
        )}

        {showContactDetails && (
          <ContactDetails
            show={showContactDetails}
            contact={selectedContact}
            onClose={() => setShowContactDetails(false)}
            onEdit={() => {
              setShowContactDetails(false);
              handleEditContact(selectedContact);
            }}
          />
        )}

        {showDeleteConfirm && (
          <DeleteConfirmModal
            show={showDeleteConfirm}
            contact={selectedContact}
            onConfirm={handleConfirmDelete}
            onCancel={() => setShowDeleteConfirm(false)}
          />
        )}
      </Container>
    </div>
  );
};

export default DashboardPage;
