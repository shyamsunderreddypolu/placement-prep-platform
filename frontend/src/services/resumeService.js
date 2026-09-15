import api from './api';

export const uploadResume = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await api.post('/resumes/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const getUserResumes = async (page, size) => {
  const params = {};
  if (page !== undefined && page !== null) params.page = page;
  if (size !== undefined && size !== null) params.size = size;

  const response = await api.get('/resumes', { params });
  return response.data.content || response.data;
};

export const downloadResume = async (resumeId, fileName) => {
  const response = await api.get(`/resumes/${resumeId}/download`, {
    responseType: 'blob',
  });
  const blob = new Blob([response.data], {
    type: response.headers['content-type'] || 'application/octet-stream',
  });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', fileName || `resume-${resumeId}.pdf`);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};