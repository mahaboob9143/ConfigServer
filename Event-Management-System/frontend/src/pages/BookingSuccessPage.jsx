import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { BsCheckCircleFill } from 'react-icons/bs';

const BookingSuccessPage = () => {
  const { ticketId } = useParams();

  return (
    <div className="text-center mt-5">
      <BsCheckCircleFill size={80} color="green" />
      <h2 className="mt-3">🎉 Booking Confirmed!</h2>
      <p>Your ticket (ID: <strong>{ticketId}</strong>) has been successfully booked.</p>
      <Link to="/dashboard/user" className="btn btn-primary mt-3">
        Go to Dashboard
      </Link>
    </div>
  );
};

export default BookingSuccessPage;
