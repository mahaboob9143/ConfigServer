import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { Container, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { BsStarFill } from 'react-icons/bs';

const FeedbackPage = () => {
  const { eventId } = useParams();
  const token = useSelector((state) => state.auth.token);

  const [rating, setRating] = useState(0);
  const [message, setMessage] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');

    if (!rating || !message.trim()) {
      setErrorMsg('⚠️ Please provide both a rating and feedback message.');
      return;
    }

    setSubmitting(true);

    try {
      const payload = {
        eventId: parseInt(eventId),
        rating,
        message
      };

      await axios.post('http://localhost:9090/feedback-api/submit', payload, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      setSuccessMsg('✅ Feedback submitted successfully!');
      setMessage('');
      setRating(0);
    } catch (err) {
      if (err.response?.status === 409) {
        setErrorMsg('⚠️ You have already submitted feedback for this event.');
      } else {
        setErrorMsg('❌ Failed to submit feedback. Please try again later.');
        console.error(err);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Container className="mt-5" style={{ maxWidth: '600px' }}>
      <h3 className="text-center mb-4">📝 Give Feedback</h3>

      {successMsg && <Alert variant="success">{successMsg}</Alert>}
      {errorMsg && <Alert variant="danger">{errorMsg}</Alert>}

      <Form onSubmit={handleSubmit}>
        {/* Rating */}
        <Form.Group className="mb-4">
          <Form.Label>Rating:</Form.Label>
          <div>
            {[1, 2, 3, 4, 5].map((val) => (
              <BsStarFill
                key={val}
                size={30}
                color={val <= rating ? 'gold' : 'lightgray'}
                style={{ cursor: 'pointer' }}
                onClick={() => setRating(val)}
              />
            ))}
          </div>
        </Form.Group>

        {/* Message */}
        <Form.Group className="mb-3">
          <Form.Label>Feedback Message</Form.Label>
          <Form.Control
            as="textarea"
            rows={4}
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Write about your experience..."
            required
          />
        </Form.Group>

        {/* Submit */}
        <Button
          type="submit"
          variant="success"
          className="w-100"
          disabled={submitting}
        >
          {submitting ? (
            <>
              <Spinner animation="border" size="sm" /> Submitting...
            </>
          ) : (
            'Submit Feedback'
          )}
        </Button>
      </Form>
    </Container>
  );
};

export default FeedbackPage;
