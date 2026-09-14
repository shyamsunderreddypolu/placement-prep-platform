import api from './api';

export const logSubmission = async (submissionData) => {
  const response = await api.post('/submissions', submissionData);
  return response.data;
};

export const getSubmissionHistory = async (page, size) => {
  const params = {};
  if (page !== undefined && page !== null) params.page = page;
  if (size !== undefined && size !== null) params.size = size;

  const response = await api.get('/submissions', { params });
  return response.data.content || response.data;
};