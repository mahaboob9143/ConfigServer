import React, { useRef } from 'react';
import { Card, Button } from 'react-bootstrap';
import { toPng } from 'html-to-image';
import { saveAs } from 'file-saver';
import { QRCodeCanvas } from 'qrcode.react';

const TicketDownloadCard = ({ ticket }) => {
  const cardRef = useRef();

  const handleDownload = () => {
    if (!cardRef.current) return;
    toPng(cardRef.current, { cacheBust: true })
      .then((dataUrl) => saveAs(dataUrl, `Ticket_${ticket.ticketID}.png`))
      .catch((err) => console.error('Error generating image', err));
  };

  const qrData = `
Name: ${ticket.userName}
Event: ${ticket.eventName}
Ticket ID: ${ticket.ticketID}
Booking Date: ${new Date(ticket.bookingDate).toLocaleString()}
  `.trim();

  return (
    <div
      ref={cardRef}
      style={{
        maxWidth: '500px',
        margin: 'auto',
        background: '#fff',
        border: '2px solid #333',
        borderRadius: '15px',
        padding: '30px',
        fontFamily: '"Segoe UI", Tahoma, Geneva, Verdana, sans-serif',
        boxShadow: '0 4px 10px rgba(0,0,0,0.2)',
        position: 'relative',
      }}
    >
      <h3 style={{ textAlign: 'center', marginBottom: '20px', color: '#343a40' }}>
        🎟️ Entry Pass
      </h3>

      <div style={{ marginBottom: '10px' }}>
        <strong>👤 Name:</strong> <span style={{ float: 'right' }}>{ticket.userName}</span>
      </div>

      <div style={{ marginBottom: '10px' }}>
        <strong>📌 Event:</strong> <span style={{ float: 'right' }}>{ticket.eventName}</span>
      </div>

      <div style={{ marginBottom: '10px' }}>
        <strong>🆔 Ticket ID:</strong> <span style={{ float: 'right' }}>{ticket.ticketID}</span>
      </div>

      <div style={{ marginBottom: '20px' }}>
        <strong>📅 Booked on:</strong> <span style={{ float: 'right' }}>{new Date(ticket.bookingDate).toLocaleString()}</span>
      </div>

      <div style={{ textAlign: 'center', paddingTop: '20px' }}>
        <QRCodeCanvas value={qrData} size={150} level="H" />
        <p style={{ fontSize: '12px', color: '#666', marginTop: '10px' }}>
          Scan for ticket verification
        </p>
      </div>

      <div style={{ textAlign: 'center', marginTop: '30px' }}>
        <Button variant="success" onClick={handleDownload}>
          Download Pass 🎫
        </Button>
      </div>
    </div>
  );
};

export default TicketDownloadCard;
