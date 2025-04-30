import React, { useState } from 'react';
import { Container, Form, Button, Alert, Spinner } from 'react-bootstrap';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ForgotPasswordPage = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [otp, setOtp] = useState('');
  const [otpVerified, setOtpVerified] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState({ type: '', message: '' });

  const clearAlert = () => setTimeout(() => setAlert({ type: '', message: '' }), 3000);

  const handleSendOtp = async () => {
    if (!email) {
      setAlert({ type: 'danger', message: 'Please enter a valid email address.' });
      clearAlert();
      return;
    }

    setLoading(true);
    try {
      const res = await axios.post(`http://localhost:9090/user-api/send-otp?email=${email}`);
      setAlert({ type: 'success', message: res.data });
      setOtpSent(true);
    } catch (err) {
      setAlert({ type: 'danger', message: err.response?.data || 'Failed to send OTP.' });
    } finally {
      setLoading(false);
      clearAlert();
    }
  };

  const handleVerifyOtp = async () => {
    if (!otp) {
      setAlert({ type: 'danger', message: 'Please enter the OTP.' });
      clearAlert();
      return;
    }

    setLoading(true);
    try {
      const res = await axios.post(`http://localhost:9090/user-api/verify-otp?email=${email}&otp=${otp}`);
      setAlert({ type: 'success', message: res.data });
      setOtpVerified(true);
    } catch (err) {
      setAlert({ type: 'danger', message: err.response?.data || 'Invalid OTP.' });
    } finally {
      setLoading(false);
      clearAlert();
    }
  };

  const handleResetPassword = async () => {
    if (!newPassword || newPassword.length < 6) {
      setAlert({ type: 'danger', message: 'Password must be at least 6 characters long.' });
      clearAlert();
      return;
    }

    setLoading(true);
    try {
      const res = await axios.post(
        `http://localhost:9090/user-api/reset-password?email=${email}&newPassword=${newPassword}`
      );
      setAlert({ type: 'success', message: res.data });

      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setAlert({ type: 'danger', message: err.response?.data || 'Password reset failed.' });
    } finally {
      setLoading(false);
      clearAlert();
    }
  };

  return (
    <div
      style={{
        backgroundImage: `url("/images/login-bg.jpg")`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '450px',
          padding: '30px',
          borderRadius: '20px',
          background: 'rgba(255, 255, 255, 0.1)',
          backdropFilter: 'blur(16px)',
          WebkitBackdropFilter: 'blur(16px)',
          border: '1px solid rgba(255, 255, 255, 0.3)',
          color: '#fff',
          boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
        }}
      >
        <h3 className="text-center mb-4" style={{ fontWeight: '600' }}>
          🔑 Forgot Password
        </h3>

        {alert.message && <Alert variant={alert.type}>{alert.message}</Alert>}

        <Form.Group className="mb-3">
          <Form.Label style={{ color: '#fff' }}>Email address</Form.Label>
          <Form.Control
            type="email"
            placeholder="Enter your registered email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={otpSent}
            style={{
              background: 'rgba(255, 255, 255, 0.2)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              color: '#fff',
              borderRadius: '10px',
              padding: '12px',
            }}
          />
        </Form.Group>

        {!otpSent ? (
          <Button onClick={handleSendOtp} disabled={loading} className="w-100 mb-3" variant="light">
            {loading ? <Spinner size="sm" animation="border" /> : 'Send OTP'}
          </Button>
        ) : (
          <>
            <Form.Group className="mb-3">
              <Form.Label style={{ color: '#fff' }}>Enter OTP</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter the OTP sent to your email"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                disabled={otpVerified}
                style={{
                  background: 'rgba(255, 255, 255, 0.2)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  color: '#fff',
                  borderRadius: '10px',
                  padding: '12px',
                }}
              />
            </Form.Group>

            {!otpVerified && (
              <Button onClick={handleVerifyOtp} disabled={loading} className="w-100 mb-3" variant="light">
                {loading ? <Spinner size="sm" animation="border" /> : 'Verify OTP'}
              </Button>
            )}
          </>
        )}

        {otpVerified && (
          <>
            <Form.Group className="mb-3">
              <Form.Label style={{ color: '#fff' }}>New Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Enter new password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                style={{
                  background: 'rgba(255, 255, 255, 0.2)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  color: '#fff',
                  borderRadius: '10px',
                  padding: '12px',
                }}
              />
            </Form.Group>

            <Button onClick={handleResetPassword} disabled={loading} className="w-100" variant="light">
              {loading ? <Spinner size="sm" animation="border" /> : 'Reset Password'}
            </Button>
          </>
        )}
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
