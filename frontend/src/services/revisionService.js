import api from './api';

export const getDueRevisions = async () => {
  const response = await api.get('/revisions/due');
  return response.data;
};

export const submitReview = async (submissionId, feedback) => {
  const response = await api.post(`/revisions/${submissionId}/review`, { feedback });
  return response.data;
};
