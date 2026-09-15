import api from './api';

export const getPlacementReadiness = async () => {
  const response = await api.get('/readiness');
  return response.data;
};
