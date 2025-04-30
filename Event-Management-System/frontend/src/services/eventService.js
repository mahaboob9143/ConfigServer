import API from './axiosConfig';

export const getAllEvents = async () => {
  const response = await API.get('/events-api/view');
  return response.data;
};