import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { Container, Row, Col, Card, Button, Form } from 'react-bootstrap';
import { FaTicketAlt, FaCalendarAlt, FaInfoCircle, FaDownload } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import TicketDownloadCardModal from './TicketDownloadCardModal'; // ⬅️ Import modal

const TicketPage = () => {
  const token = useSelector((state) => state.auth.token);
  const [showActive, setShowActive] = useState(true);
  const [tickets, setTickets] = useState([]);
  const [eventNames, setEventNames] = useState({});
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const navigate = useNavigate();

  const fetchEventName = async (eventId, isCancelled = false) => {
    if (eventNames[eventId]) return;
    try {
      const url = isCancelled
        ? `http://localhost:9090/events-api/internal/${eventId}`
        : `http://localhost:9090/events-api/${eventId}`;
      const res = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setEventNames(prev => ({ ...prev, [eventId]: res.data.name }));
    } catch (err) {
      console.error(`Error fetching event name for eventId ${eventId}:`, err);
      setEventNames(prev => ({ ...prev, [eventId]: 'Unknown Event' }));
    }
  };

  const fetchTickets = async () => {
    try {
      const url = showActive
        ? 'http://localhost:9090/tickets-api/user'
        : 'http://localhost:9090/tickets-api/user/cancelled';

      const response = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setTickets(response.data);

      const eventIds = response.data.map(ticket => ticket.eventId);
      eventIds.forEach(eventId => fetchEventName(eventId, !showActive));
    } catch (err) {
      console.error('Error fetching tickets:', err);
    }
  };

  useEffect(() => {
    fetchTickets();
  }, [showActive]);

  const handleCancel = async (ticketId) => {
    try {
      await axios.put(`http://localhost:9090/tickets-api/cancel/${ticketId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchTickets();
    } catch (err) {
      console.error('Error cancelling ticket', err);
    }
  };

  const handleFeedback = (eventId) => {
    navigate(`/feedback/${eventId}`);
  };

  const handleDownloadClick = (ticket) => {
    setSelectedTicket(ticket);
    setShowModal(true);
  };

  return (
    <Container className="mt-5">
      {/* Toggle View */}
      <Row className="align-items-center justify-content-between mb-4">
        <Col xs={12} md="auto">
          <h3 className="fw-bold mb-0">🎟️ My Tickets</h3>
        </Col>
        <Col xs={12} md="auto" className="mt-3 mt-md-0 d-flex align-items-center gap-2">
          <Form.Check
            type="switch"
            id="ticket-view-toggle"
            checked={!showActive}
            onChange={() => {
              setEventNames({});
              setShowActive(!showActive);
            }}
            style={{ transform: 'scale(1.5)', cursor: 'pointer' }}
            label=""
          />
          <span style={{ fontWeight: '600', fontSize: '1rem', color: !showActive ? '#dc3545' : 'blueviolet' }}>
            {showActive ? 'Showing Active' : 'Showing Cancelled'}
          </span>
        </Col>
      </Row>

      {/* Tickets List */}
      <Row className="justify-content-center">
        {tickets.length === 0 ? (
          <p className="text-center text-muted mt-3">No tickets found.</p>
        ) : (
          tickets.map((ticket) => (
            <Col xs={12} sm={6} md={4} lg={3} key={ticket.ticketID} className="mb-4">
              <Card className="border shadow-sm h-100" style={{ borderRadius: '12px' }}>
                <Card.Body>
                  <div className="d-flex align-items-center mb-2">
                    <FaTicketAlt className="me-2 text-primary" />
                    <Card.Title className="mb-0">
                      Ticket ID: <strong>{ticket.ticketID}</strong>
                    </Card.Title>
                  </div>

                  <Card.Subtitle className="text-muted mb-3" style={{ fontSize: '0.95rem' }}>
                    Event: <strong>{eventNames[ticket.eventId] || 'Loading...'}</strong>
                  </Card.Subtitle>

                  <div className="mb-2 text-muted d-flex align-items-center">
                    <FaInfoCircle className="me-2" />
                    <span>Status: {ticket.status}</span>
                  </div>

                  <div className="mb-4 text-muted d-flex align-items-center">
                    <FaCalendarAlt className="me-2" />
                    <span>Booked on: {new Date(ticket.bookingDate).toLocaleString()}</span>
                  </div>

                  {showActive && (
                    <div className="d-flex justify-content-between align-items-center">
                      <Button variant="danger" size="sm" onClick={() => handleCancel(ticket.ticketID)}>
                        Cancel
                      </Button>
                      <Button variant="warning" size="sm" onClick={() => handleFeedback(ticket.eventId)}>
                        Feedback
                      </Button>
                      <Button
                        variant="success"
                        size="sm"
                        onClick={() => handleDownloadClick({
                          ...ticket,
                          eventName: eventNames[ticket.eventId]
                        })}
                      >
                        <FaDownload /> {/* Download icon */}
                      </Button>
                    </div>
                  )}
                </Card.Body>
              </Card>
            </Col>
          ))
        )}
      </Row>

      {/* Modal for QR + Ticket Preview */}
      {selectedTicket && (
        <TicketDownloadCardModal
          show={showModal}
          onHide={() => setShowModal(false)}
          ticket={selectedTicket}
          eventName={selectedTicket.eventName}
        />
      )}
    </Container>
  );
};

export default TicketPage;
