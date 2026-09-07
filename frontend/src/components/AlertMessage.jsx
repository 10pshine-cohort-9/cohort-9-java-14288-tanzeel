import React, { useEffect, useState } from 'react';
import { Alert } from 'react-bootstrap';

const AlertMessage = ({ message, type = 'success', onClose }) => {
  const [show, setShow] = useState(!!message);

  useEffect(() => {
    setShow(!!message);
    if (message) {
      const timer = setTimeout(() => {
        setShow(false);
        if (onClose) onClose();
      }, 4000);
      return () => clearTimeout(timer);
    }
  }, [message, onClose]);

  return (
    <>
      {show && (
        <Alert
          variant={type === 'error' ? 'danger' : type}
          onClose={() => {
            setShow(false);
            if (onClose) onClose();
          }}
          dismissible
        >
          {message}
        </Alert>
      )}
    </>
  );
};

export default AlertMessage;
