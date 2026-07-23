import api from './api';

export const getDifficultyStats = async () => {
  const response = await api.get('/dashboard/difficulty');
  return response.data;
};

export const getTopicStats = async () => {
  const response = await api.get('/dashboard/topic');
  return response.data;
};

export const getStreakStats = async () => {
  const response = await api.get('/dashboard/streak');
  return response.data;
};
