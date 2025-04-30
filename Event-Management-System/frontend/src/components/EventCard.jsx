import React from 'react';
import { Card, Button, Badge } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { FaCalendarAlt, FaMapMarkerAlt } from 'react-icons/fa';

const EventCard = ({ event }) => {
  const navigate = useNavigate();
  const imageSrc = `/images/${event.category?.toLowerCase() || 'default'}.jpg`;

  return (
    <Card
      className="mb-4 shadow-sm"
      style={{
        borderRadius: '12px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        border: '1px solid #ddd',
        overflow: 'hidden',
        height: '100%',
        transition: 'transform 0.2s ease-in-out',
      }}
    >
      {/* Image with fallback */}
      <div style={{ position: 'relative' }}>
        <Card.Img
          variant="top"
          src={imageSrc}
          alt={event.name}
          onError={(e) => {
            e.target.onerror = null;
            e.target.src = '/images/default.jpg'; // fallback image
          }}
          style={{ height: '170px', width: '100%', objectFit: 'cover' }}
        />
        <Badge
          bg="primary"
          style={{
            position: 'absolute',
            top: '10px',
            right: '10px',
            borderRadius: '15px',
            padding: '4px 10px',
            fontSize: '0.75rem',
            textTransform: 'capitalize',
          }}
        >
          {event.category || 'Uncategorized'}
        </Badge>
      </div>

      {/* Content */}
      <Card.Body style={{ padding: '15px 20px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', height: '100%' }}>
        <div>
          <Card.Title style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '10px' }}>
            {event.name}
          </Card.Title>

          <div style={{ fontSize: '0.9rem', color: '#555', textAlign: 'left' }}>
            <div className="d-flex align-items-center mb-2">
              <FaCalendarAlt className="me-2 text-muted" />
              <span>{new Date(event.date).toLocaleDateString()}</span>
            </div>
            <div className="d-flex align-items-center">
              <FaMapMarkerAlt className="me-2 text-muted" />
              <span>{event.location}</span>
            </div>
          </div>
        </div>

        <Button
          variant="primary"
          className="w-100 mt-3"
          style={{ borderRadius: '20px' }}
          onClick={() => navigate(`/event/${event.eventId}`)}
        >
          View Details
        </Button>
      </Card.Body>
    </Card>
  );
};

export default EventCard;
