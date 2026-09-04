import React from 'react';
import { Modal, Button } from 'react-bootstrap';
import { FaExclamationTriangle } from 'react-icons/fa';

const DeleteConfirmModal = ({ show, contact, onConfirm, onCancel }) => {
  if (!contact) return null;

  return (
    <Modal show={show} onHide={onCancel} centered>
      <Modal.Header closeButton>
        <Modal.Title>Confirm Delete</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div className="text-center mb-3">
          <FaExclamationTriangle size={48} className="text-warning" />
        </div>
        <p className="text-center mb-2">
          Are you sure you want to delete <strong>{contact.fullName}</strong>?
        </p>
        <p className="text-center text-muted small">
          This action cannot be undone.
        </p>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
        <Button variant="danger" onClick={onConfirm}>
          Delete Contact
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default DeleteConfirmModal;
