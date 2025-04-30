import React, { useEffect, useState } from 'react';
import { Container, Form, Button, Alert, Spinner, InputGroup } from 'react-bootstrap';
import axios from 'axios';
import { useSelector } from 'react-redux';
import { BsEye, BsEyeSlash } from 'react-icons/bs';

const UserProfilePage = () => {
  const token = useSelector((state) => state.auth.token);
  const email = token ? JSON.parse(atob(token.split('.')[1])).sub : null;

  const [userData, setUserData] = useState({
    name: '',
    email: '',
    contactNumber: '',
    password: '',
  });

  const [userId, setUserId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const fetchUser = async () => {
    try {
      const res = await axios.get(`http://localhost:9090/user-api/email/${email}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const id = res.data;
      setUserId(id);

      const userDetails = await axios.get(`http://localhost:9090/user-api/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setUserData({
        name: userDetails.data.name,
        email: userDetails.data.email,
        contactNumber: userDetails.data.contactNumber,
        password: '',
      });
    } catch (err) {
      setErrorMsg('Failed to load user data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (email) fetchUser();
  }, [email]);

  const handleChange = (e) => {
    setUserData({ ...userData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');

    try {
      await axios.put(`http://localhost:9090/user-api/${userId}`, userData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSuccessMsg('Profile updated successfully!');
    } catch (err) {
      if (err.response?.status === 409) {
        setErrorMsg(err.response.data);
      } else if (err.response?.data?.errors) {
        const combinedErrors = Object.values(err.response.data.errors).join(' ');
        setErrorMsg(combinedErrors);
      } else {
        setErrorMsg('Failed to update profile');
      }
    }
  };

  return (
    <Container className="mt-5" style={{ maxWidth: '600px' }}>
      <h3 className="text-center mb-4">👤 My Profile</h3>

      {errorMsg && <Alert variant="danger">{errorMsg}</Alert>}
      {successMsg && <Alert variant="success">{successMsg}</Alert>}

      {loading ? (
        <div className="text-center">
          <Spinner animation="border" />
          <p>Loading profile...</p>
        </div>
      ) : (
        <Form onSubmit={handleSubmit} noValidate>
          <Form.Group className="mb-3">
            <Form.Label>Name</Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={userData.name}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={50}
              isInvalid={userData.name.length < 2}
            />
            <Form.Control.Feedback type="invalid">
              Name must be at least 2 characters.
            </Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Email</Form.Label>
            <Form.Control
              type="email"
              name="email"
              value={userData.email}
            //   onChange={handleChange}
            //   required
              readOnly
              plaintext
              style={{backgroundColor:'#f5f5f5'}}
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Contact Number</Form.Label>
            <Form.Control
              type="text"
              name="contactNumber"
              value={userData.contactNumber}
              onChange={handleChange}
              required
              pattern="^[6-9]\d{9}$"
              isInvalid={!/^[6-9]\d{9}$/.test(userData.contactNumber)}
            />
            <Form.Control.Feedback type="invalid">
              Must be a 10-digit number starting with 6-9.
            </Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mb-4">
            <Form.Label>Change Password (optional)</Form.Label>
            <InputGroup>
              <Form.Control
                type={showPassword ? 'text' : 'password'}
                name="password"
                value={userData.password}
                onChange={handleChange}
                placeholder="Leave blank to keep current password"
              />
              <Button
                variant="outline-secondary"
                onClick={() => setShowPassword((prev) => !prev)}
              >
                {showPassword ? <BsEyeSlash /> : <BsEye />}
              </Button>
            </InputGroup>
          </Form.Group>

          <div className="text-center">
            <Button variant="primary" type="submit">
              Update Profile
            </Button>
          </div>
        </Form>
      )}
    </Container>
  );
};

export default UserProfilePage;
