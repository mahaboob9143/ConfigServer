import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container, Card, Row, Col, Spinner, Alert } from 'react-bootstrap';
import { Bar } from 'react-chartjs-2';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend);

const EventAnalyticsPage = () => {
  const { eventId } = useParams();
  const token = useSelector((state) => state.auth.token);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const res = await axios.get(`http://localhost:9090/feedback-api/event/${eventId}/analytics`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setAnalytics(res.data);
      } catch (err) {
        console.error('Error fetching analytics:', err);
        setErrorMsg('Failed to load analytics');
      } finally {
        setLoading(false);
      }
    };

    if (eventId) fetchAnalytics();
  }, [token, eventId]);

  const chartData = {
    labels: ['Total Tickets', 'Avg Rating', 'Feedback Count'],
    datasets: [
      {
        label: 'Event Stats',
        data: analytics ? [analytics.totalFeedback * 1.5, analytics.averageRating, analytics.totalFeedback] : [],
        backgroundColor: ['#007bff', '#ffc107', '#28a745'],
      },
    ],
  };

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4">📊 Event Analytics</h3>

      {loading ? (
        <div className="text-center">
          <Spinner animation="border" />
          <p>Loading analytics...</p>
        </div>
      ) : errorMsg ? (
        <Alert variant="danger" className="text-center">
          {errorMsg}
        </Alert>
      ) : (
        <>
          <Row className="mb-4">
            <Col md={4}>
              <Card bg="info" text="white" className="text-center">
                <Card.Body>
                  <Card.Title>Tickets</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>{analytics.totalFeedback}</Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card bg="warning" text="dark" className="text-center">
                <Card.Body>
                  <Card.Title>Average Rating</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>{analytics.averageRating.toFixed(1)}</Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card bg="success" text="white" className="text-center">
                <Card.Body>
                  <Card.Title>Total Feedback(Approx)</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>
                    {(analytics.totalFeedback * 1.5).toFixed(0)}
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <Bar data={chartData} />
        </>
      )}
    </Container>
  );
};

export default EventAnalyticsPage;
