import api from './api';

export const getProblems = async (topic = '', difficulty = '') => {
  const params = {};
  if (topic) params.topic = topic;
  if (difficulty) params.difficulty = difficulty;

  const response = await api.get('/problems', { params });
  return response.data;
};

export const addProblem = async (problemData) => {
  const response = await api.post('/problems', problemData);
  return response.data;
};
