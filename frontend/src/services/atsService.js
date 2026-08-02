import api from './api';

export const analyzeResume = async (resumeId, jobDescription) => {
  const response = await api.post('/ats/analyze', {
    resumeId,
    jobDescription,
  });
  return response.data;
};
