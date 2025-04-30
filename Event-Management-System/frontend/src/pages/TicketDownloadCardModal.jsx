import React, { useEffect, useState, useRef } from 'react';
import { Modal, Button, Spinner } from 'react-bootstrap';
import { QRCodeCanvas } from 'qrcode.react';
import { toPng } from 'html-to-image';
import { saveAs } from 'file-saver';
import axios from 'axios';
import { useSelector } from 'react-redux';

const TicketDownloadCardModal = ({ show, onHide, ticket }) => {
  const token = useSelector((state) => state.auth.token);
  const cardRef = useRef();
  const [ticketDetails, setTicketDetails] = useState(null);

  // Fetch fresh ticket details
  useEffect(() => {
    if (show && ticket) {
      axios
        .get(`http://localhost:9090/tickets-api/${ticket.ticketID}/details`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => setTicketDetails(res.data))
        .catch((err) => console.error('Error fetching ticket details:', err));
    }
  }, [show, ticket, token]);

  // Download handler
  const handleDownload = () => {
    if (!cardRef.current) return;
    toPng(cardRef.current)
      .then((dataUrl) => saveAs(dataUrl, `Ticket_${ticketDetails.ticketID}.png`))
      .catch((err) => console.error('Error downloading ticket:', err));
  };

  // Show loading spinner while fetching
  if (!ticketDetails) {
    return (
      <Modal show={show} onHide={onHide} centered>
        <Modal.Body className="text-center py-5">
          <Spinner animation="border" />
          <p className="mt-3">Loading ticket details...</p>
        </Modal.Body>
      </Modal>
    );
  }

  // Construct QR Text
  const qrText = `
Name: ${ticketDetails.userName}
Event: ${ticketDetails.eventName}
Ticket ID: ${ticketDetails.ticketId}
Booking Date: ${new Date(ticketDetails.bookingDate).toLocaleString()}
  `.trim();

  // Render full modal
  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>🎟️ Ticket Preview</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div
          ref={cardRef}
          className="p-4 text-center shadow"
          style={{
            border: '2px solid #333',
            borderRadius: '14px',
            background: '#fefefe',
            fontFamily: 'Arial, sans-serif',
          }}
        >
          <h5 className="mb-3 fw-bold">🎟️Event Entry Pass</h5>
          <p><strong>👤 Name:</strong> {ticketDetails.userName}</p>
          <p><strong>📌 Event:</strong> {ticketDetails.eventName}</p>
          <p><strong>🆔 Ticket ID:</strong> {ticketDetails.ticketId}</p>
          <p><strong>📅 Booking Date:</strong> {new Date(ticketDetails.bookingDate).toLocaleString()}</p>
          <div className="mt-3">
            <QRCodeCanvas value={qrText} size={140} level="H" />
            <div style={{ fontSize: '12px', color: '#777', marginTop: '8px' }}>
              Scan to verify
            </div>
          </div>
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>Close</Button>
        <Button variant="success" onClick={handleDownload}>Download</Button>
      </Modal.Footer>
    </Modal>
  );
};

export default TicketDownloadCardModal;
