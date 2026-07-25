import api from './api';

export const getSubmissionHistory = async () => {
  const response = await api.get('/submissions');
  return response.data;
};

export const logSubmission = async (submissionData) => {
  const response = await api.post('/submissions', submissionData);
  return response.data;
};
