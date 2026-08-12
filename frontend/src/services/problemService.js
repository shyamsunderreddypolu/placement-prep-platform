import api from './api';

export const getProblems = async (topic = '', difficulty = '', pattern = '') => {
  const params = {};
  if (topic) params.topic = topic;
  if (difficulty) params.difficulty = difficulty;
  if (pattern) params.pattern = pattern;

  const response = await api.get('/problems', { params });
  return response.data;
};

export const getDistinctPatterns = async () => {
  const response = await api.get('/problems/patterns');
  return response.data;
};

export const addProblem = async (problemData) => {
  const response = await api.post('/problems', problemData);
  return response.data;
};
