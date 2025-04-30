import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import axios from 'axios';

const PaymentPage = () => {
  const { eventId } = useParams();
  const navigate = useNavigate();

  const token = useSelector((state) => state.auth.token);
  const user = useSelector((state) => state.auth.user); // assumes user object has name, email, contactNumber

  useEffect(() => {
    const loadRazorpay = () => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.async = true;
      document.body.appendChild(script);
    };
    loadRazorpay();
  }, []);

  const handlePayment = async () => {
    const options = {
      key: 'rzp_test_4mTemu0SvyMbaX',
      amount: 49900,
      currency: 'INR',
      name: 'Event Manager',
      description: 'Event Ticket Purchase',
      image: 'https://your-logo-url.com/logo.png',

      handler: async function (response) {
        try {
          console.log('🧾 Razorpay success:', response.razorpay_payment_id);

          const res = await axios.post(
            `http://localhost:9090/tickets-api/book?eventId=${eventId}`,
            {},
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );

          console.log('🎟️ Ticket booked:', res.data);

          setTimeout(() => {
            navigate(`/booked/${res.data.ticketID}`);
          }, 1000);
        } catch (error) {
          console.error('❌ Error booking ticket:', error);
          alert('Booking failed, please try again.');
        }
      },

      notes: {
        userName: user?.name || 'User',
        userEmail: user?.email || 'user@example.com',
        userPhone: user?.contactNumber || '0000000000',
      },

      theme: {
        color: '#3399cc',
      },
    };

    const rzp = new window.Razorpay(options);
    rzp.open();
  };

  return (
    <div className="container text-center mt-5">
      <h2>Payment for Event</h2>
      <p>Amount: ₹499</p>
      <button className="btn btn-success" onClick={handlePayment}>
        Pay ₹499 with Razorpay
      </button>
    </div>
  );
};

export default PaymentPage;
